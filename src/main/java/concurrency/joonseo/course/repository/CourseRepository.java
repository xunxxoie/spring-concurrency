package concurrency.joonseo.course.repository;

import concurrency.joonseo.course.entity.Course;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT c
        FROM Course c
        WHERE c.id = :courseId
    """)
    Optional<Course> findByIdWithPessimisticWriteLock(@Param("courseId") Long courseId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("""
        SELECT c
        FROM Course c
        WHERE c.id = :courseId
    """)
    Optional<Course> findByIdWithPessimisticReadLock(@Param("courseId") Long courseId);

    Optional<Course> findById(Long courseId);
}
