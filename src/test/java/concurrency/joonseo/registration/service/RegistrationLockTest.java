package concurrency.joonseo.registration.service;

import concurrency.joonseo.course.entity.Course;
import concurrency.joonseo.course.repository.CourseRepository;
import concurrency.joonseo.registration.repository.RegistrationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RegistrationLockTest {
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        courseRepository.save(Course.create("운영체제", 100L));
    }

    @AfterEach
    void tearDown() {
        courseRepository.deleteAll();
    }


    @Test
    @DisplayName("LockType = PESSIMISTIC_WRITE")
    void pessimisticWriteLock() throws InterruptedException {
        //given
        long courseId = 1L;

        int maxThread = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(maxThread);
        CountDownLatch countDownLatch = new CountDownLatch(maxThread);

        //when
        for(int i = 0; i < maxThread; i++){
            final long studentId = i + 1L;
            executorService.execute(() -> {
                try{
                    registrationService.registerCourseWithPessimisticWriteLock(studentId, courseId);
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("course not found"));

        long registrationSize = registrationRepository.countByCourseId(courseId);

        //then
        assertThat(course.getCapacity()).isZero();
        assertThat(registrationSize).isEqualTo(maxThread);
    }

    @Test
    @DisplayName("LockType = PESSIMISTIC_READ")
    void pessimisticReadLock() throws InterruptedException {
        //given
        long courseId = 2L;

        int maxThread = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(maxThread);
        CountDownLatch countDownLatch = new CountDownLatch(maxThread);

        //when
        for(int i = 0; i < maxThread; i++){
            final long studentId = i + 1L;
            executorService.execute(() -> {
                try{
                    registrationService.registerCourseWithPessimisticReadLock(studentId, courseId);
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("course not found"));

        long registrationSize = registrationRepository.countByCourseId(courseId);

        //then
        assertThat(course.getCapacity()).isNotZero();
        assertThat(registrationSize).isNotEqualTo(maxThread);
    }
}
