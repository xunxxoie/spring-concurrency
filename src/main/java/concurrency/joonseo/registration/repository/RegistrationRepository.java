package concurrency.joonseo.registration.repository;

import concurrency.joonseo.registration.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    long countByCourseId(Long courseId);
}
