package com.example.redisdatastructure.setzset;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CourseEnrollmentServiceTest {

    @Autowired
    private CourseEnrollmentService courseEnrollmentService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @AfterEach
    void tearDown() {
        // 각 테스트 후 Redis 데이터 초기화
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("학생들이 강의에 등록하면 실시간 인기 랭킹이 업데이트된다.")
    void enrollAndGetTopCourses_test() {
        // given: 여러 강의와 학생들이 존재
        String courseRedis = "course:redis";
        String courseJava = "course:java";
        String courseJpa = "course:jpa";

        // when: 학생들이 각 강의에 수강 신청
        // Redis 강의: 3명 신청
        courseEnrollmentService.enrollStudent(courseRedis, "studentA");
        courseEnrollmentService.enrollStudent(courseRedis, "studentB");
        courseEnrollmentService.enrollStudent(courseRedis, "studentC");

        // Java 강의: 2명 신청
        courseEnrollmentService.enrollStudent(courseJava, "studentA");
        courseEnrollmentService.enrollStudent(courseJava, "studentD");
        // 중복 신청 (무시되어야 함)
        courseEnrollmentService.enrollStudent(courseJava, "studentA");

        // JPA 강의: 1명 신청
        courseEnrollmentService.enrollStudent(courseJpa, "studentE");


        // then: 인기 강의 TOP 2를 조회하면 수강생 수가 많은 순서대로 반환된다.
        List<String> top2Courses = courseEnrollmentService.getTopPopularCourses(2);

        // 결과 검증
        assertThat(top2Courses).hasSize(2);
        // 1위: course:redis (3명)
        // 2위: course:java (2명)
        assertThat(top2Courses).containsExactly(courseRedis, courseJava);

        // 추가 검증: Java 강의의 수강생 목록 조회 시 중복이 없는지 확인
        Set<String> javaStudents = courseEnrollmentService.getStudentsInCourse(courseJava);
        assertThat(javaStudents).hasSize(2).containsExactlyInAnyOrder("studentA", "studentD");
    }
}