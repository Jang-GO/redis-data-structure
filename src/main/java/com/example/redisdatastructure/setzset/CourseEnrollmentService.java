package com.example.redisdatastructure.setzset;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CourseEnrollmentService {
    private final RedisTemplate<String, String> redisTemplate;
    private final SetOperations<String, String> setOps;
    private final ZSetOperations<String, String> zSetOps;
    private static final String popularityKey = "course:popularity";

    public CourseEnrollmentService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.setOps = redisTemplate.opsForSet();
        this.zSetOps = redisTemplate.opsForZSet();
    }

    public void enrollStudent(String courseId, String studentId){
        String courseStudentKey = getCourseStudentKey(courseId);

        // 1. [Set] 해당 강의의 수강생 집합에 학생 ID를 추가
        // SADD 명령어. 이미 학생이 존재하면 0, 새로 추가되면 1을 반환
        Long addedCount = setOps.add(courseStudentKey, studentId);

        // 2. 새로운 학생이 추가되었을 경우에만 랭킹을 업데이트
        if (addedCount != null && addedCount > 0) {
            // 3. [Set] 현재 강의의 총수강생 수를 조회 (SCARD 명령어)
            Long totalStudents = setOps.size(courseStudentKey);

            // 4. [Sorted Set] 'courses:popularity' 랭킹에 점수(총수강생 수)를 업데이트 (ZADD 명령어)
            if (totalStudents != null) {
                zSetOps.add(popularityKey, courseId, totalStudents.doubleValue());
            }
        }

    }

    /**
     * 인기 강의 TOP N 목록을 조회하는 메서드
     * @param topN 조회할 랭킹 수
     * @return 인기 강의 ID 목록 (수강생 수가 많은 순)
     */
    public List<String> getTopPopularCourses(long topN) {

        // [Sorted Set] 랭킹이 높은(점수가 큰) 순서대로 0위부터 topN-1위까지 조회 (ZREVRANGE 명령어)
        Set<String> topCourses = zSetOps.reverseRange(popularityKey, 0, topN - 1);

        return new ArrayList<>(topCourses);
    }

    /**
     * 특정 강의의 전체 수강생 목록을 조회하는 메서드
     */
    public Set<String> getStudentsInCourse(String courseId) {
        String courseStudentsKey = getCourseStudentKey(courseId);
        // [Set] 해당 강의의 모든 수강생 ID를 조회 (SMEMBERS 명령어)
        return setOps.members(courseStudentsKey);
    }

    private String getCourseStudentKey(String courseId) {
        return "course:" + courseId + ":students";
    }
}
