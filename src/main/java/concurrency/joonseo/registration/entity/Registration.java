package concurrency.joonseo.registration.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long courseId;

    @Builder
    private Registration(Long studentId, Long courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public static Registration create(Long studentId, Long courseId) {
        return Registration.builder()
                .studentId(studentId)
                .courseId(courseId)
                .build();
    }
}
