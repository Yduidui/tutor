package com.example.springboot.mapper;


import com.example.springboot.entity.Course;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseMapper {
    List<Course> selectAll(Course course);

//    @Select("select * from course where id = #{id}")  注解方式
    Course selectById(Integer id);

    void insert(Course course);

    void updateById(Course course);

    void deleteById(Integer id);

    @Select("select * from course where title = #{title}")
    Course selectByTitle(String title);

    @Select("select count(*) from course where time like'%${date}%'")
    Integer selectCountByDate(String date);

    List<Course> selectByTeacherId(Integer teacherId);

    List<Course> selectByStudentId(Integer studentId);

    // 添加学生到课程
    @Update("UPDATE course SET student_ids = CONCAT(IFNULL(student_ids, ''), #{studentId}, ',') WHERE id = #{courseId}")
    void addStudentToCourse(@Param("courseId") Integer courseId, @Param("studentId") Integer studentId);

    // 获取学生已报名的课程
    @Select("SELECT c.*, s.name as subjectName, t.name as teacherName " +
            "FROM course c " +
            "LEFT JOIN subject s ON c.subject_id = s.id " +
            "LEFT JOIN teacher t ON c.teacher_id = t.id " +
            "WHERE FIND_IN_SET(#{studentId}, c.student_ids) " +
            "ORDER BY c.id DESC")
    List<Course> selectStudentCourses(@Param("studentId") Integer studentId);

    // 获取教师发布的课程
    @Select("SELECT c.*, s.name as subjectName, t.name as teacherName " +
            "FROM course c " +
            "LEFT JOIN subject s ON c.subject_id = s.id " +
            "LEFT JOIN teacher t ON c.teacher_id = t.id " +
            "WHERE c.teacher_id = #{teacherId} " +
            "ORDER BY c.id DESC")
    List<Course> selectTeacherCourses(@Param("teacherId") Integer teacherId);

    void updateStudentIdByCourseId(
            @Param("courseId") Integer courseId,
            @Param("studentId") Integer studentId
    );
    @Select("SELECT * FROM course WHERE id = #{courseId}")
    Course getCourseTitleById(Integer courseId);

    List<Course> selectByCourseIds(@Param("courseIds") List<Integer> courseIds);

    int countByTeacherId(Integer teacherId);

    int getAllCourseCount();

    List<Course> selectTopCourses();
}
