package cocurrency.joonseo.registration.service;

import cocurrency.joonseo.course.entity.Course;
import cocurrency.joonseo.course.repository.CourseRepository;
import cocurrency.joonseo.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegistrationServiceTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        courseRepository.save(Course.create("운영체제", 100L));
    }

    @Test
    @DisplayName("멀티 스레드 환경에서 동시에 100개의 수강신청 요청이 들어오면 데이터 정합성이 깨진다.")
    void concurrencyTest() throws InterruptedException {
        //given
        Long courseId = 1L;
        Long userId = 1L;

        int maxThread = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(maxThread);
        CountDownLatch countDownLatch = new CountDownLatch(maxThread);

        //when
        for(int i = 0; i < maxThread; i++){
            executorService.execute(() -> {
                try{
                    registrationService.registerCourse(courseId, userId);
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("course not found"));

        //then
        assertThat(course.getCapacity()).isZero();
    }
}