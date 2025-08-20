package cocurrency.joonseo.registration.repository;

import cocurrency.joonseo.registration.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
}
