package student_processing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        // Read the file, parse each line into a Student, then group by group ID
        Map<String, List<Student>> groupMap = Files.lines(Path.of("src/student_processing/StudentProcessing1.txt"))
                .map(line -> line.split("\\s+"))
                .map(parts -> new Student(parts[0], parts[1], Integer.parseInt(parts[2])))
                .collect(Collectors.groupingBy(Student::getGroup));

        // Print each group and its students
        groupMap.forEach((group, students) ->
                System.out.println("Group " + group + ":  " + students));
    }
}
