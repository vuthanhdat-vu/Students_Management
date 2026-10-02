package com.StudentManagement;

import com.StudentManagement.dto.request.CourseRequest;
import com.StudentManagement.dto.request.EnrollmentRequest;
import com.StudentManagement.dto.request.LoginRequest;
import com.StudentManagement.dto.request.RegisterRequest;
import com.StudentManagement.dto.request.ScoreUpdateRequest;
import com.StudentManagement.dto.response.CourseResponse;
import com.StudentManagement.dto.response.EnrollmentResponse;
import com.StudentManagement.dto.response.StudentResponse;
import com.StudentManagement.entity.Course;
import com.StudentManagement.entity.Student;
import com.StudentManagement.entity.User;
import com.StudentManagement.repository.CourseRepository;
import com.StudentManagement.repository.EnrollmentRepository;
import com.StudentManagement.repository.StudentRepository;
import com.StudentManagement.repository.UserRepository;
import com.StudentManagement.service.AuthService;
import com.StudentManagement.service.CourseService;
import com.StudentManagement.service.EnrollmentService;
import com.StudentManagement.service.StudentService;
import com.StudentManagement.utils.JwtUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class StudentManagementIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        enrollmentRepository.deleteAll();
        studentRepository.deleteAll();
        userRepository.findAll().forEach(u -> {
            if (!"admin".equals(u.getUsername())) {
                userRepository.delete(u);
            }
        });
        courseRepository.deleteAll();
    }

    @Test
    void test1_Register_ReturnsOnlyToken_AndSavesStudentWithSVCodeAndFullName() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("student_test1");
        registerRequest.setPassword("123456");
        registerRequest.setFullName("Nguyen Van A");
        registerRequest.setEmail("vana@example.com");
        registerRequest.setClassName("IT01");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đăng ký thành công"))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.username").doesNotExist())
                .andExpect(jsonPath("$.data.role").doesNotExist())
                .andExpect(jsonPath("$.data.email").doesNotExist())
                .andReturn();

        JsonNode rootNode = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals(1, rootNode.get("data").size(), "Data payload should contain ONLY token");
        String token = rootNode.get("data").get("token").asText();
        assertEquals("ROLE_STUDENT", jwtUtils.extractRole(token));

        // Verify Student entity saved
        Student student = studentRepository.findByUserUsername("student_test1").orElse(null);
        assertNotNull(student);
        assertEquals("SV001", student.getStudentCode());
        assertEquals("Nguyen Van A", student.getFullName());
        assertEquals("IT01", student.getClassName());
        assertNotNull(student.getUser());
        assertEquals("student_test1", student.getUser().getUsername());
    }

    @Test
    void test2_RegisterSecondStudent_GetsSequentialCode_SV002() throws Exception {
        RegisterRequest req1 = new RegisterRequest();
        req1.setUsername("student_1");
        req1.setPassword("123456");
        req1.setFullName("Nguyen Van A");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated());

        RegisterRequest req2 = new RegisterRequest();
        req2.setUsername("student_2");
        req2.setPassword("123456");
        req2.setFullName("Tran Thi B");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isCreated());

        Student student1 = studentRepository.findByUserUsername("student_1").orElseThrow();
        Student student2 = studentRepository.findByUserUsername("student_2").orElseThrow();

        assertEquals("SV001", student1.getStudentCode());
        assertEquals("SV002", student2.getStudentCode());
        assertEquals("Nguyen Van A", student1.getFullName());
        assertEquals("Tran Thi B", student2.getFullName());
    }

    @Test
    void test3_Login_ReturnsOnlyToken() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("student_login");
        req.setPassword("password123");
        req.setFullName("Le Van C");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername("student_login");
        loginReq.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.username").doesNotExist())
                .andReturn();

        JsonNode rootNode = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals(1, rootNode.get("data").size(), "Login data payload should contain ONLY token");
    }

    @Test
    void test4_StudentSelfEnrollment_AndCourseManagement() throws Exception {
        // 1. Create Course
        Course course = courseRepository.save(Course.builder()
                .courseName("Java Core")
                .numberOfSessions(12)
                .processWeight(0.3)
                .componentWeight(0.7)
                .build());

        // 2. Register Student 1
        RegisterRequest req1 = new RegisterRequest();
        req1.setUsername("student_self");
        req1.setPassword("123456");
        req1.setFullName("Student Self");
        MvcResult regRes = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated())
                .andReturn();

        String token1 = objectMapper.readTree(regRes.getResponse().getContentAsString()).get("data").get("token").asText();

        // 3. Student registers course for themselves
        EnrollmentRequest enrollReq = new EnrollmentRequest();
        enrollReq.setCourseId(course.getId());

        mockMvc.perform(post("/api/enrollments")
                        .header("Authorization", "Bearer " + token1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enrollReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.courseName").value("Java Core"))
                .andExpect(jsonPath("$.data.status").value("CHƯA CÓ ĐIỂM"));

        // 4. Check enrollment count for student
        Student student1 = studentRepository.findByUserUsername("student_self").orElseThrow();
        mockMvc.perform(get("/api/students/" + student1.getId())
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enrollmentCount").value(1));
    }

    @Test
    void test5_StudentCannotEnrollForOtherStudent_AndCannotViewOtherStudentDetails() throws Exception {
        Course course = courseRepository.save(Course.builder()
                .courseName("Python")
                .numberOfSessions(10)
                .processWeight(0.4)
                .componentWeight(0.6)
                .build());

        // Register Student 1
        RegisterRequest req1 = new RegisterRequest();
        req1.setUsername("student_a");
        req1.setPassword("123456");
        req1.setFullName("Student A");
        MvcResult res1 = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated()).andReturn();
        String token1 = objectMapper.readTree(res1.getResponse().getContentAsString()).get("data").get("token").asText();

        // Register Student 2
        RegisterRequest req2 = new RegisterRequest();
        req2.setUsername("student_b");
        req2.setPassword("123456");
        req2.setFullName("Student B");
        MvcResult res2 = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isCreated()).andReturn();

        Student student1 = studentRepository.findByUserUsername("student_a").orElseThrow();
        Student student2 = studentRepository.findByUserUsername("student_b").orElseThrow();

        // Student 1 tries to enroll Student 2
        EnrollmentRequest badEnrollReq = new EnrollmentRequest();
        badEnrollReq.setStudentId(student2.getId());
        badEnrollReq.setCourseId(course.getId());

        mockMvc.perform(post("/api/enrollments")
                        .header("Authorization", "Bearer " + token1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badEnrollReq)))
                .andExpect(status().isForbidden());

        // Student 1 views own detail -> OK
        mockMvc.perform(get("/api/students/" + student1.getId())
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName").value("Student A"));

        // Student 1 views /api/students/me -> OK
        mockMvc.perform(get("/api/students/me")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName").value("Student A"));

        // Student 1 tries to view Student 2 detail -> Forbidden
        mockMvc.perform(get("/api/students/" + student2.getId())
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isForbidden());
    }

    @Test
    void test6_ScoreManagement_AndPassFailCalculation() throws Exception {
        // 1. Login as Admin
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setUsername("admin");
        adminLogin.setPassword("123456");
        MvcResult adminRes = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk()).andReturn();
        String adminToken = objectMapper.readTree(adminRes.getResponse().getContentAsString()).get("data").get("token").asText();

        // 2. Course with 30% process, 70% component
        Course course = courseRepository.save(Course.builder()
                .courseName("Database")
                .numberOfSessions(15)
                .processWeight(0.3)
                .componentWeight(0.7)
                .build());

        // 3. Register student
        RegisterRequest req = new RegisterRequest();
        req.setUsername("student_score");
        req.setPassword("123456");
        req.setFullName("Student Score");
        MvcResult stRes = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated()).andReturn();
        String studentToken = objectMapper.readTree(stRes.getResponse().getContentAsString()).get("data").get("token").asText();

        Student student = studentRepository.findByUserUsername("student_score").orElseThrow();

        // 4. Student enrolls course
        EnrollmentRequest enrollReq = new EnrollmentRequest();
        enrollReq.setCourseId(course.getId());
        MvcResult enRes = mockMvc.perform(post("/api/enrollments")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enrollReq)))
                .andExpect(status().isCreated()).andReturn();
        Long enrollmentId = objectMapper.readTree(enRes.getResponse().getContentAsString()).get("data").get("id").asLong();

        // 5. Student attempts to update own score -> Forbidden
        ScoreUpdateRequest scoreReq = new ScoreUpdateRequest();
        scoreReq.setProcessScore(9.0);
        scoreReq.setComponentScore(8.0);
        mockMvc.perform(put("/api/enrollments/" + enrollmentId + "/score")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoreReq)))
                .andExpect(status().isForbidden());

        // 6. Admin enters passing scores (9.0 and 8.0 => 9.0*0.3 + 8.0*0.7 = 2.7 + 5.6 = 8.3 => PASS / ĐỖ)
        mockMvc.perform(put("/api/enrollments/" + enrollmentId + "/score")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoreReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.finalScore").value(8.3))
                .andExpect(jsonPath("$.data.passed").value(true))
                .andExpect(jsonPath("$.data.status").value("ĐỖ"));

        // 7. Student checks own scores
        mockMvc.perform(get("/api/enrollments/me")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].courseName").value("Database"))
                .andExpect(jsonPath("$.data[0].finalScore").value(8.3))
                .andExpect(jsonPath("$.data[0].passed").value(true))
                .andExpect(jsonPath("$.data[0].status").value("ĐỖ"));

        // 8. Admin updates failing scores (2.0 and 3.0 => 2.0*0.3 + 3.0*0.7 = 0.6 + 2.1 = 2.7 => FAIL / TRƯỢT)
        ScoreUpdateRequest failScoreReq = new ScoreUpdateRequest();
        failScoreReq.setProcessScore(2.0);
        failScoreReq.setComponentScore(3.0);
        mockMvc.perform(put("/api/enrollments/" + enrollmentId + "/score")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(failScoreReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.finalScore").value(2.7))
                .andExpect(jsonPath("$.data.passed").value(false))
                .andExpect(jsonPath("$.data.status").value("TRƯỢT"));
    }

    @Test
    void test7_AdminCanCreateTeacher_TeacherCanLoginAndEnterScores_StudentBlocked() throws Exception {
        // 1. Login as Admin
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setUsername("admin");
        adminLogin.setPassword("123456");
        MvcResult adminRes = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk()).andReturn();
        String adminToken = objectMapper.readTree(adminRes.getResponse().getContentAsString()).get("data").get("token").asText();

        // 2. Admin creates a Teacher account
        com.StudentManagement.dto.request.TeacherCreateRequest teacherReq = new com.StudentManagement.dto.request.TeacherCreateRequest();
        teacherReq.setUsername("teacher_john");
        teacherReq.setPassword("teacher123");
        teacherReq.setEmail("john.teacher@school.edu");

        mockMvc.perform(post("/api/teachers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("teacher_john"))
                .andExpect(jsonPath("$.data.role").value("ROLE_TEACH"));

        // 3. Admin gets all teachers
        mockMvc.perform(get("/api/teachers")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].username").value("teacher_john"));

        // 4. Teacher logs in and gets JWT token
        LoginRequest teacherLogin = new LoginRequest();
        teacherLogin.setUsername("teacher_john");
        teacherLogin.setPassword("teacher123");
        MvcResult teacherLoginRes = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn();
        String teacherToken = objectMapper.readTree(teacherLoginRes.getResponse().getContentAsString()).get("data").get("token").asText();
        assertEquals("ROLE_TEACH", jwtUtils.extractRole(teacherToken));

        // 5. Create student and course and enrollment
        RegisterRequest stReq = new RegisterRequest();
        stReq.setUsername("st_grade_me");
        stReq.setPassword("123456");
        stReq.setFullName("Student To Grade");
        MvcResult stRes = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stReq)))
                .andExpect(status().isCreated()).andReturn();
        String studentToken = objectMapper.readTree(stRes.getResponse().getContentAsString()).get("data").get("token").asText();

        Course course = courseRepository.save(Course.builder()
                .courseName("Algorithms")
                .numberOfSessions(10)
                .processWeight(0.5)
                .componentWeight(0.5)
                .build());

        EnrollmentRequest enReq = new EnrollmentRequest();
        enReq.setCourseId(course.getId());
        MvcResult enRes = mockMvc.perform(post("/api/enrollments")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enReq)))
                .andExpect(status().isCreated()).andReturn();
        Long enrollmentId = objectMapper.readTree(enRes.getResponse().getContentAsString()).get("data").get("id").asLong();

        // 6. Teacher enters scores (10.0 and 8.0 => 10.0*0.5 + 8.0*0.5 = 9.0 => PASS / ĐỖ)
        ScoreUpdateRequest teacherScoreReq = new ScoreUpdateRequest();
        teacherScoreReq.setProcessScore(10.0);
        teacherScoreReq.setComponentScore(8.0);

        mockMvc.perform(put("/api/enrollments/" + enrollmentId + "/score")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherScoreReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.finalScore").value(9.0))
                .andExpect(jsonPath("$.data.status").value("ĐỖ"));

        // 7. Student tries to create a teacher -> Forbidden (403)
        com.StudentManagement.dto.request.TeacherCreateRequest fakeTeacher = new com.StudentManagement.dto.request.TeacherCreateRequest();
        fakeTeacher.setUsername("fake_teacher");
        fakeTeacher.setPassword("123456");
        mockMvc.perform(post("/api/teachers")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fakeTeacher)))
                .andExpect(status().isForbidden());
    }
}
