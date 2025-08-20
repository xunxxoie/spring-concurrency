package cocurrency.joonseo.student.repository;

import cocurrency.joonseo.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
