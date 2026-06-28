import math
import re
import unicodedata


def check_type(student, repair=False):
    reason = ""

    # 1. Remove spaces and symbols from phone to test it
    phone = student.get("phone", "")
    test_phone = re.sub(r'[-+ \t()]', '', str(phone))

    if not test_phone.isdigit():
        if repair:
            student["phone"] = re.sub(r'[^\d+]', '', str(phone))
            if not re.sub(r'[-+ \t()]', '', student["phone"]).isdigit():
                return False, student, "CHECK_TYPE: Phone still invalid after repair"
        else:
            return False, student, "CHECK_TYPE: Phone contains invalid characters"

    # 2. Check if the email has exactly one @
    email = student.get("email", "")
    if str(email).count('@') != 1:
        if repair:
            student["email"] = re.sub(r'[!*# ]', '', str(email))
            if student["email"].count('@') != 1:
                return False, student, "CHECK_TYPE: Email still invalid after repair"
        else:
            return False, student, "CHECK_TYPE: Email does not have exactly one @"

    # 3. Check if the name has any numbers in it
    name = student.get("name", "")
    if re.search(r'\d', str(name)):
        if repair:
            student["name"] = re.sub(r'\d', '', str(name))
            if re.search(r'\d', student["name"]):
                return False, student, "CHECK_TYPE: Name still contains numbers after repair"
        else:
            return False, student, "CHECK_TYPE: Name contains numbers"

    return True, student, reason


def check_required(student, required_fields):
    # 1. Make a list of bad words that mean missing data
    invalid_placeholders = {"none", "nan", "no data", ""}

    # 2. Check each required field from the config file
    for field in required_fields:
        value = student.get(field)
        string_val = str(value).strip().lower()

        if value is None or string_val in invalid_placeholders:
            return False, student, f"CHECK_REQUIRED: Missing or invalid field '{field}'"

    return True, student, ""


def normalize(student, repair=False):
    reason = ""

    # 1. Make email lowercase and remove spaces
    email = str(student.get("email", ""))
    student["email"] = email.lower().replace(" ", "").replace("\t", "")

    # 2. Fix the name to start with big letters
    name = str(student.get("name", ""))
    words = name.split()

    if len(words) < 2 or name.lower() in ["none", "nan", "no data"]:
        if repair:
            student["name"] = "NO DATA"
        else:
            return False, student, "NORMALIZE: Missing first or last name"
    else:
        clean_words = []
        for word in words:
            if '-' in word:
                clean_words.append('-'.join([part.capitalize() for part in word.split('-')]))
            else:
                clean_words.append(word.capitalize())
        student["name"] = ' '.join(clean_words)

    # 3. Fix weird phone fonts and add country code brackets
    phone = str(student.get("phone", ""))
    phone = unicodedata.normalize('NFKC', phone)

    translated_phone = "".join([str(int(char)) if char.isdigit() else char for char in phone])
    phone = translated_phone

    if phone.startswith('+'):
        space_idx = phone.find(' ')
        if space_idx != -1:
            country_code = phone[:space_idx]
            rest_of_phone = phone[space_idx:]
            student["phone"] = f"({country_code}){rest_of_phone}"
    else:
        student["phone"] = phone

    # 4. Remove plus and minus signs from grades
    grades = str(student.get("grades", ""))
    grades = unicodedata.normalize('NFKC', grades)
    grades = grades.replace('+', '').replace('-', '')

    # 5. Keep only 1 number after the dot
    grades = re.sub(r'(\d\.\d)\d+', r'\1', grades)
    student["grades"] = grades

    return True, student, reason


def grade_mapper(student, repair=False):
    reason = ""
    grades_raw = str(student.get("grades", ""))

    # 1. Split grades by comma
    grade_strings = grades_raw.split(',')

    mapped_grades = []
    nan_count = 0

    for g in grade_strings:
        # 2. Remove letters and keep only numbers and dots
        clean_g = re.sub(r'[^\d.]', '', g)

        # 3. Fix multiple dots left over from cleaning
        if '..' in clean_g:
            clean_g = clean_g.replace('..', '.')

        if clean_g == "" or clean_g == ".":
            mapped_grades.append(float('nan'))
            nan_count += 1
        else:
            try:
                mapped_grades.append(float(clean_g))
            except ValueError:
                mapped_grades.append(float('nan'))
                nan_count += 1

    if nan_count == 0:
        student["grades"] = mapped_grades
        return True, student, reason

    # 4. If only one grade is missing, find the average of the rest
    elif nan_count == 1 and repair:
        valid_numbers = [grade for grade in mapped_grades if not math.isnan(grade)]
        if len(valid_numbers) > 0:
            average = round(sum(valid_numbers) / len(valid_numbers), 1)
            for i in range(len(mapped_grades)):
                if math.isnan(mapped_grades[i]):
                    mapped_grades[i] = average
                    break
            student["grades"] = mapped_grades
            return True, student, reason
        else:
            return False, student, "GRADE_MAPPER: Only NaN present, cannot calculate average"
    else:
        return False, student, "GRADE_MAPPER: Invalid grades present and could not be repaired"


def deduplicate(student, seen_ids, seen_names, repair=False):
    reason = ""
    student_id = student.get("id")
    student_name = student.get("name")

    # 1. Check if we already saw this ID
    if student_id in seen_ids:
        return False, student, "DEDUPLICATE: Duplicate ID found"

    # 2. Check if we already saw this Name
    if student_name in seen_names:
        return False, student, "DEDUPLICATE: Duplicate Name found"

    # 3. Save the new ID and Name to our memory sets
    if student_id is not None:
        seen_ids.add(student_id)

    if student_name is not None and student_name != "NO DATA":
        seen_names.add(student_name)

    return True, student, reason


def checksum(student, repair=False):
    reason = ""
    student_id = str(student.get("id", ""))

    # 1. Check if ID has a minus sign
    if '-' not in student_id:
        return False, student, "CHECKSUM: Invalid ID format"

    prefix, number_part = student_id.split('-', 1)

    if not number_part.isdigit() or len(number_part) < 2:
        return False, student, "CHECKSUM: ID number part is invalid"

    # 2. Separate numbers from the control digit
    core_str = number_part[:-1]
    control_digit = int(number_part[-1])

    # 3. Calculate the math sum
    def calculate_weighted_sum(core):
        total = 0
        for i, digit_str in enumerate(core):
            total += int(digit_str) * (i + 1)
        return total % 10

    if calculate_weighted_sum(core_str) == control_digit:
        return True, student, reason

    # 4. If math fails, try to reverse the string and check again
    if repair:
        reversed_core = core_str[::-1]
        if calculate_weighted_sum(reversed_core) == control_digit:
            student["id"] = f"{prefix}-{reversed_core}{control_digit}"
            return True, student, reason
        else:
            return False, student, "CHECKSUM: Failed checksum even after repair"

    return False, student, "CHECKSUM: Failed checksum"