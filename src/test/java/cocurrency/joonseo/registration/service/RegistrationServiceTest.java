package cocurrency.joonseo.registration.service;

import cocurrency.joonseo.course.entity.Course;
import cocurrency.joonseo.course.repository.CourseRepository;
import cocurrency.joonseo.registration.repository.RegistrationRepository;
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
class RegistrationServiceTest {

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
    @DisplayName("멀티 스레드 환경에서 동시에 100개의 수강신청 요청이 들어오면 데이터 정합성이 깨진다.")
    void concurrencyTest() throws InterruptedException {
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
                    registrationService.registerCourseWithoutSynchronize(studentId, courseId);
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("course not found"));

        long registrationSize = registrationRepository.count();

        //then
        assertThat(course.getCapacity()).isNotZero();
        assertThat(registrationSize).isEqualTo(maxThread);
    }

    @Test
    @DisplayName("멀티 스레드 환경에서 메소드 호출 래밸에 synchronized을 적용하면, 데이터 정합성이 깨지지 않는다.")
    void concurrencyTestWithSynchronizedInvocation() throws InterruptedException {
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
                    synchronized (this){
                        registrationService.registerCourseWithoutSynchronize(studentId, courseId);
                    }
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("course not found"));

        long registrationSize = registrationRepository.count();

        //then
        assertThat(course.getCapacity()).isZero();
        assertThat(registrationSize).isEqualTo(maxThread);
    }

    @Test
    @DisplayName("멀티 스레드 환경에서 메소드 래밸에 synchronized을 적용하면, 데이터 정합성이 깨진다.")
    void concurrencyTestWithSynchronizedMethod() throws InterruptedException {
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
                    registrationService.registerCourseWithSynchronize(studentId, courseId);
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("course not found"));

        long registrationSize = registrationRepository.count();

        //then
        assertThat(course.getCapacity()).isNotZero();
        assertThat(registrationSize).isEqualTo(maxThread);
    }

    @Test
    @DisplayName("싱글스레드 처리 테스트")
    void singleThreadTest() throws InterruptedException {
        //given
        long courseId = 1L;

        int maxThread = 1;
        ExecutorService executorService = Executors.newFixedThreadPool(maxThread);
        CountDownLatch countDownLatch = new CountDownLatch(100);

        //when
        for(int i = 0; i < 100; i++){
            final long studentId = i + 1L;
            executorService.execute(() -> {
                try{
                    registrationService.registerCourseWithSynchronize(studentId, courseId);
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("course not found"));

        long registrationSize = registrationRepository.count();

        //then
        assertThat(course.getCapacity()).isZero();
        assertThat(registrationSize).isEqualTo(100);
    }
}