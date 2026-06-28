package student_processing;

// Represents a single student with a name, group ID, and test score
public class Student {

    private final String name;
    private final String group;
    private final int score;

    public Student(String name, String group, int score) {
        this.name = name;
        this.group = group;
        this.score = score;
    }

    public String getGroup() {
        return group;
    }

    // Output format: Mary(12c)-78
    @Override
    public String toString() {
        return name + "(" + group + ")-" + score;
    }
}
