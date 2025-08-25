package concurrency.joonseo.student.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true)
    private String studentId;

    @Column(nullable = false)
    private String password;

    @Builder
    private Student(String studentId, String password) {
        this.studentId = studentId;
        this.password = password;
    }

    public static Student create(String studentId, String password){
        return Student.builder()
                .studentId(studentId)
                .password(password)
                .build();
    }
}
