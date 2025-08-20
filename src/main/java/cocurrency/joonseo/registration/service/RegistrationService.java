package cocurrency.joonseo.registration.service;

import cocurrency.joonseo.course.entity.Course;
import cocurrency.joonseo.course.repository.CourseRepository;
import cocurrency.joonseo.registration.entity.Registration;
import cocurrency.joonseo.registration.repository.RegistrationRepository;
import cocurrency.joonseo.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final CourseRepository courseRepository;
    private final RegistrationRepository registrationRepository;

    @Transactional
    public void registerCourse(Long studentId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("course not found"));

        course.decreaseCapacity();

        Registration registration = Registration.create(studentId, courseId);

        registrationRepository.save(registration);
    }
}
