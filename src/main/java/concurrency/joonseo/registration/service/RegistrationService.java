package concurrency.joonseo.registration.service;

import concurrency.joonseo.course.entity.Course;
import concurrency.joonseo.course.repository.CourseRepository;
import concurrency.joonseo.registration.entity.Registration;
import concurrency.joonseo.registration.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final CourseRepository courseRepository;
    private final RegistrationRepository registrationRepository;

    @Transactional
    public void registerCourseWithoutSynchronize(Long studentId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("course not found"));

        course.decreaseCapacity();

        Registration registration = Registration.create(studentId, courseId);

        registrationRepository.save(registration);
    }

    @Transactional
    public synchronized void registerCourseWithSynchronize(Long studentId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("course not found"));

        course.decreaseCapacity();

        Registration registration = Registration.create(studentId, courseId);

        registrationRepository.save(registration);
    }

    @Transactional
    public void registerCourseWithPessimisticWriteLock(Long studentId, Long courseId) {
        Course course = courseRepository.findByIdWithPessimisticWriteLock(courseId)
                .orElseThrow(() -> new RuntimeException("course not found"));

        course.decreaseCapacity();

        Registration registration = Registration.create(studentId, courseId);

        registrationRepository.save(registration);
    }

    @Transactional
    public void registerCourseWithPessimisticReadLock(Long studentId, Long courseId) {
        Course course = courseRepository.findByIdWithPessimisticReadLock(courseId)
                .orElseThrow(() -> new RuntimeException("course not found"));

        course.decreaseCapacity();

        Registration registration = Registration.create(studentId, courseId);

        registrationRepository.save(registration);
    }
}
