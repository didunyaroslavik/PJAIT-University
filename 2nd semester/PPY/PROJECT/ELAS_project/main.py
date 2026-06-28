import re
import json
from filters import check_type, check_required, normalize, grade_mapper, deduplicate, checksum


def counting_decorator(func):
    def wrapper(*args, **kwargs):
        total_rows = 0
        successfull_rows = 0

        for status, student_data in func(*args, **kwargs):
            # 1. Count every row we read
            total_rows += 1
            if status == "SUCCESS":
                # 2. Count only the good rows
                successfull_rows += 1
                yield student_data

        print(f"\n--- Stage 1 Summary ---")
        print(f"Total rows read: {total_rows}")
        print(f"Successfully mapped: {successfull_rows}")
        print(f"-------------------------\n")

    return wrapper


def extract_data(line):
    # 1. Create empty dictionary for student
    student_data = {
        "name": None,
        "id": None,
        "address": None,
        "grades": None,
        "phone": None,
        "email": None
    }

    # 2. Find data using regex
    id_match = re.search(r'(?i)(idx-\d+|req-\d+)', line)
    if id_match:
        student_data["id"] = id_match.group(1).upper()

    name_match = re.search(r'(?i)student[-:~ \t]+([^%|;!]+)', line)
    if name_match:
        student_data["name"] = name_match.group(1).strip()

    addr_match = re.search(r'(?i)addr[-:~ \t]+([^%|;!]+)', line)
    if addr_match:
        student_data["address"] = addr_match.group(1).strip()

    grades_match = re.search(r'(?i)(?:marks|results)[-:\~ \t]+([^%|;!]+)', line)
    if grades_match:
        student_data["grades"] = grades_match.group(1).strip()

    phone_match = re.search(r'(?i)phone[-:~ \t]+([^\*|]+)', line)
    if phone_match:
        student_data["phone"] = phone_match.group(1).strip()

    email_match = re.search(r'(?i)email[-:~ \t]+([^>)]+)', line)
    if email_match:
        student_data["email"] = email_match.group(1).strip()

    return student_data


@counting_decorator
def read_log_data(filepath):
    # 1. Open the file line by line to save RAM
    with open(filepath, 'r', encoding='utf-8') as file:
        for line in file:
            student = extract_data(line.strip())

            # 2. Check if important data is missing
            missing_id = student.get("id") is None
            missing_name = student.get("name") is None
            missing_phone = student.get("phone") is None
            missing_email = student.get("email") is None
            missing_all_comms = missing_phone and missing_email

            if missing_id or missing_name or missing_all_comms:
                yield ("REJECTED", None)
            else:
                yield ("SUCCESS", student)


def load_config(config_filepath):
    with open(config_filepath, 'r') as file:
        return json.load(file)


def save_quarantine_to_json(junk_data, filename="quarantine.json"):
    with open(filename, 'w', encoding='utf-8') as file:
        json.dump(junk_data, file, indent=4)


def sort_students(students, sort_rules):
    if not sort_rules:
        return students

    # 1. Create a score for each student based on rules
    def sorting_criteria(student):
        scores = []
        grades = student.get("grades", [])

        for rule in sort_rules:
            attribute = rule.get("attr")
            direction = rule.get("dir", "asc")
            value = 0

            # 2. Check which sorting rule to use
            if attribute == "grades_avg":
                value = sum(grades) / len(grades) if grades else 0
            elif attribute == "grades_min":
                value = min(grades) if grades else 0
            elif attribute == "grades_max":
                value = max(grades) if grades else 0
            elif attribute == "name_length":
                name = student.get("name", "")
                value = len(name.replace(" ", ""))

            # 3. Make number negative if we want descending order
            if direction == "desc":
                value = -value

            scores.append(value)
        return tuple(scores)

    return sorted(students, key=sorting_criteria)


def export_to_json(students, student_number):
    filename = f"{student_number}_report.json"
    with open(filename, 'w', encoding='utf-8') as file:
        json.dump(students, file, indent=4)


def run_pipeline(student_generator, config):
    cleaned_students = []
    quarantine = {}

    seen_ids = set()
    seen_names = set()

    active_filters = []
    final_ops = []
    hit_final = False

    # 1. Separate row filters from final operations
    for step in config:
        if step.get("name") in ["SORT", "EXPORT_JSON"]:
            hit_final = True
            final_ops.append(step)
        elif not hit_final:
            active_filters.append(step)

    for student in student_generator:
        passed_all = True

        # 2. Run the row filters one by one
        for step in active_filters:
            filter_name = step.get("name")
            can_repair = step.get("repair", False)

            if filter_name == "CHECK_TYPE":
                passed, student, error_reason = check_type(student, repair=can_repair)
            elif filter_name == "CHECK_REQUIRED":
                required_fields = step.get("args", [])
                passed, student, error_reason = check_required(student, required_fields)
            elif filter_name == "NORMALIZE":
                passed, student, error_reason = normalize(student, repair=can_repair)
            elif filter_name == "GRADE_MAPPER":
                passed, student, error_reason = grade_mapper(student, repair=can_repair)
            elif filter_name == "DEDUPLICATE":
                passed, student, error_reason = deduplicate(student, seen_ids, seen_names, repair=can_repair)
            elif filter_name == "CHECKSUM":
                passed, student, error_reason = checksum(student, repair=can_repair)
            else:
                passed = True

            # 3. Save failed students to quarantine
            if not passed:
                if error_reason not in quarantine:
                    quarantine[error_reason] = []
                quarantine[error_reason].append(student)
                passed_all = False
                break

        if passed_all:
            cleaned_students.append(student)

    # 4. Run final sorting and saving at the end
    for op in final_ops:
        if op.get("name") == "SORT":
            cleaned_students = sort_students(cleaned_students, op.get("args", []))
        elif op.get("name") == "EXPORT_JSON":
            export_to_json(cleaned_students, op.get("student_id", "report"))

    return cleaned_students, quarantine


if __name__ == '__main__':
    pipeline_config = load_config('pipeline_config.json')
    student_data_generator = read_log_data('s35774.usrlog')

    clean_data, junk_data = run_pipeline(student_data_generator, pipeline_config)

    print(f"Pipeline Finished!")
    print(f"Clean Students: {len(clean_data)}")

    print("\n--- Quarantine Summary ---")
    for reason, failed_students in junk_data.items():
        print(f"Reason: {reason} | Count: {len(failed_students)}")

    save_quarantine_to_json(junk_data)
    print("\nQuarantine saved to quarantine.json")