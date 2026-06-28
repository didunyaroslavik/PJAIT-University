package enums_lambdas;

import java.util.Arrays;
import java.util.Comparator;

// 1. Enumeration for Sex
enum Sex {
    F, M
}

// 2. Enumeration for Size
enum Size {
    XS, S, M, L, XL
}

// 3. Enumeration for Country with overridden toString()
enum Country {
    PL {
        @Override
        public String toString() {
            return "Polska";
        }
    },
    NL {
        @Override
        public String toString() {
            return "Nederland";
        }
    },
    DE {
        @Override
        public String toString() {
            return "Deutschland";
        }
    }
}

// Class Person
class Person {
    String name;
    Sex sex;
    Size size;
    Country country;

    public Person(String name, Sex sex, Size size, Country country) {
        this.name = name;
        this.sex = sex;
        this.size = size;
        this.country = country;
    }

    @Override
    public String toString() {
        return name + "(" + sex + ", " + size + ", " + country + ")";
    }
}

public class EnumsLambdas {

    // Generic, static function to print an array of any type
    public static <T> void printArray(String message, T[] array) {
        System.out.println("    *** " + message + " ***");
        for (T element : array) {
            System.out.println(element);
        }
    }

    public static void main(String[] args) {
        Person[] persons = {
                new Person("Max",  Sex.M, Size.XL, Country.NL),
                new Person("Jan",  Sex.M, Size.S,  Country.PL),
                new Person("Eva",  Sex.F, Size.XS, Country.NL),
                new Person("Lina", Sex.F, Size.L,  Country.DE),
                new Person("Mila", Sex.F, Size.S,  Country.DE),
                new Person("Ola",  Sex.F, Size.M,  Country.PL),
        };

        // Lambda: Sort by sex first. If sex is the same, sort by size.
        Comparator<Person> sexThenSize = (p1, p2) -> {
            int sexCompare = p1.sex.compareTo(p2.sex);
            return (sexCompare != 0) ? sexCompare : p1.size.compareTo(p2.size);
        };

        Arrays.sort(persons, sexThenSize);
        printArray("Persons by sex and then size", persons);

        // Lambda: Sort by size first. If size is the same, sort by name alphabetically.
        Arrays.sort(persons, (p1, p2) -> {
            int sizeCompare = p1.size.compareTo(p2.size);
            return (sizeCompare != 0) ? sizeCompare : p1.name.compareTo(p2.name);
        });
        printArray("Persons by size and then name", persons);

        Country[] countries = Country.values();

        // Lambda: Sort countries alphabetically based on their overridden string names.
        Arrays.sort(countries, (c1, c2) -> c1.toString().compareTo(c2.toString()));
        printArray("Countries by name", countries);
    }
}