package cocurrency.joonseo.course.repository;

import cocurrency.joonseo.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
