package cocurrency.joonseo.course.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long capacity;

    @Builder
    private Course(String name, Long capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public static Course create(String name, Long capacity) {
        return Course.builder()
                .name(name)
                .capacity(capacity)
                .build();
    }

    public void decreaseCapacity() {
        if(capacity <= 0){
            return;
        }
        this.capacity -= 1;
    }
}
