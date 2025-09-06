package com.smartEdu.SmartEduBackend.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.LetterStatus;
import com.smartEdu.SmartEduBackend.enums.LetterType;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LetterRequestServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ParentRepo parentRepo;

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private GradesRepo gradesRepo;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private LetterRequestRepo letterRequestRepo;

    @Mock
    private GridFSBucket gridFSBucket;

    @InjectMocks
    private LetterRequestService letterRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate_ValidInput_SavesLetterRequest() {
        String token = "validToken";
        String username = "user1";
        String profileId = "parent1";
        String studentId = "student1";
        String gradeId = "grade1";
        User user = User.builder().username(username).profileId(profileId).build();
        Parent parent = Parent.builder().id(profileId).studentIds(Arrays.asList(studentId)).build();
        Student student = Student.builder().id(studentId).fullNameWithInitials("John Doe").gradeId(gradeId).build();
        Grades grade = Grades.builder().id(gradeId).gradeName("5").build();
        LetterRequest request = LetterRequest.builder().letterType(LetterType.CHARACTER_CERTIFICATE).build();
        LetterRequest savedRequest = LetterRequest.builder()
                .id("letter1")
                .studentId(studentId)
                .studentName("John Doe")
                .lastGrade("5")
                .letterType(LetterType.CHARACTER_CERTIFICATE)
                .status(LetterStatus.PENDING)
                .requestedDate(LocalDate.now())
                .build();

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(parentRepo.findById(profileId)).thenReturn(Optional.of(parent));
        when(studentRepo.findById(studentId)).thenReturn(Optional.of(student));
        when(gradesRepo.findById(gradeId)).thenReturn(Optional.of(grade));
        when(letterRequestRepo.save(any(LetterRequest.class))).thenReturn(savedRequest);

        LetterRequest result = letterRequestService.create(request, token);

        assertEquals(savedRequest, result);
        assertEquals(studentId, result.getStudentId());
        assertEquals("John Doe", result.getStudentName());
        assertEquals("5", result.getLastGrade());
        assertEquals(LetterStatus.PENDING, result.getStatus());
        assertEquals(LocalDate.now(), result.getRequestedDate());
        verify(letterRequestRepo, times(1)).save(any(LetterRequest.class));
    }

    @Test
    void testGetAllAcceptedLettersAndCertificatesToParents_ValidToken_ReturnsApprovedLetters() {
        String token = "validToken";
        String username = "user1";
        String profileId = "parent1";
        String studentId = "student1";
        User user = User.builder().username(username).profileId(profileId).build();
        Parent parent = Parent.builder().id(profileId).studentIds(Arrays.asList(studentId)).build();
        Student student = Student.builder().id(studentId).build();
        LetterRequest letter = LetterRequest.builder()
                .id("letter1")
                .studentId(studentId)
                .status(LetterStatus.APPROVED)
                .build();

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(parentRepo.findById(profileId)).thenReturn(Optional.of(parent));
        when(studentRepo.findById(studentId)).thenReturn(Optional.of(student));
        when(letterRequestRepo.findByStudentIdAndStatus(studentId, LetterStatus.APPROVED)).thenReturn(Arrays.asList(letter));

        List<LetterRequest> result = letterRequestService.getAllAcceptedLettersAndCertificatesToParents(token);

        assertEquals(1, result.size());
        assertEquals("letter1", result.get(0).getId());
        assertEquals(LetterStatus.APPROVED, result.get(0).getStatus());
        verify(letterRequestRepo, times(1)).findByStudentIdAndStatus(studentId, LetterStatus.APPROVED);
    }

    @Test
    void testGetPendingLettersAndCertificatesToParents_ValidToken_ReturnsPendingLetters() {
        String token = "validToken";
        String username = "user1";
        String profileId = "parent1";
        String studentId = "student1";
        User user = User.builder().username(username).profileId(profileId).build();
        Parent parent = Parent.builder().id(profileId).studentIds(Arrays.asList(studentId)).build();
        Student student = Student.builder().id(studentId).build();
        LetterRequest letter = LetterRequest.builder()
                .id("letter1")
                .studentId(studentId)
                .status(LetterStatus.PENDING)
                .build();

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(parentRepo.findById(profileId)).thenReturn(Optional.of(parent));
        when(studentRepo.findById(studentId)).thenReturn(Optional.of(student));
        when(letterRequestRepo.findByStudentIdAndStatus(studentId, LetterStatus.PENDING)).thenReturn(Arrays.asList(letter));

        List<LetterRequest> result = letterRequestService.getPendingLettersAndCertificatesToParents(token);

        assertEquals(1, result.size());
        assertEquals("letter1", result.get(0).getId());
        assertEquals(LetterStatus.PENDING, result.get(0).getStatus());
        verify(letterRequestRepo, times(1)).findByStudentIdAndStatus(studentId, LetterStatus.PENDING);
    }

    @Test
    void testGetRejectLettersAndCertificatesToParents_ValidToken_ReturnsRejectedLetters() {
        String token = "validToken";
        String username = "user1";
        String profileId = "parent1";
        String studentId = "student1";
        User user = User.builder().username(username).profileId(profileId).build();
        Parent parent = Parent.builder().id(profileId).studentIds(Arrays.asList(studentId)).build();
        Student student = Student.builder().id(studentId).build();
        LetterRequest letter = LetterRequest.builder()
                .id("letter1")
                .studentId(studentId)
                .status(LetterStatus.REJECTED)
                .build();

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(parentRepo.findById(profileId)).thenReturn(Optional.of(parent));
        when(studentRepo.findById(studentId)).thenReturn(Optional.of(student));
        when(letterRequestRepo.findByStudentIdAndStatus(studentId, LetterStatus.REJECTED)).thenReturn(Arrays.asList(letter));

        List<LetterRequest> result = letterRequestService.getRejectLettersAndCertificatesToParents(token);

        assertEquals(1, result.size());
        assertEquals("letter1", result.get(0).getId());
        assertEquals(LetterStatus.REJECTED, result.get(0).getStatus());
        verify(letterRequestRepo, times(1)).findByStudentIdAndStatus(studentId, LetterStatus.REJECTED);
    }

    @Test
    void testApprove_ValidId_ApprovesLetterRequest() {
        String id = "letter1";
        String signatureUrl = "signature.jpg";
        String documentUrl = "document.pdf";
        String remarks = "Approved by principal";
        LetterRequest request = LetterRequest.builder()
                .id(id)
                .studentId("student1")
                .status(LetterStatus.PENDING)
                .build();
        LetterRequest approvedRequest = LetterRequest.builder()
                .id(id)
                .studentId("student1")
                .status(LetterStatus.APPROVED)
                .principalSignatureUrl(signatureUrl)
                .documentUrl(documentUrl)
                .principalRemarks(remarks)
                .issuedDate(LocalDate.now())
                .build();

        when(letterRequestRepo.findById(id)).thenReturn(Optional.of(request));
        when(letterRequestRepo.save(any(LetterRequest.class))).thenReturn(approvedRequest);

        LetterRequest result = letterRequestService.approve(id, signatureUrl, documentUrl, remarks);

        assertEquals(LetterStatus.APPROVED, result.getStatus());
        assertEquals(signatureUrl, result.getPrincipalSignatureUrl());
        assertEquals(documentUrl, result.getDocumentUrl());
        assertEquals(remarks, result.getPrincipalRemarks());
        assertEquals(LocalDate.now(), result.getIssuedDate());
        verify(letterRequestRepo, times(1)).save(any(LetterRequest.class));
    }

    @Test
    void testApprove_NonExistingId_ThrowsRuntimeException() {
        String id = "nonExisting";

        when(letterRequestRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> letterRequestService.approve(id, "signature.jpg", "document.pdf", "remarks"));
        verify(letterRequestRepo, never()).save(any(LetterRequest.class));
    }

    @Test
    void testReject_ValidId_RejectsLetterRequest() {
        String id = "letter1";
        LetterRequest request = LetterRequest.builder()
                .id(id)
                .studentId("student1")
                .status(LetterStatus.PENDING)
                .build();
        LetterRequest rejectedRequest = LetterRequest.builder()
                .id(id)
                .studentId("student1")
                .status(LetterStatus.REJECTED)
                .principalRemarks("Visit the principal at the school.")
                .build();

        when(letterRequestRepo.findById(id)).thenReturn(Optional.of(request));
        when(letterRequestRepo.save(any(LetterRequest.class))).thenReturn(rejectedRequest);

        LetterRequest result = letterRequestService.reject(id);

        assertEquals(LetterStatus.REJECTED, result.getStatus());
        assertEquals("Visit the principal at the school.", result.getPrincipalRemarks());
        verify(letterRequestRepo, times(1)).save(any(LetterRequest.class));
    }

    @Test
    void testReject_NonExistingId_ThrowsRuntimeException() {
        String id = "nonExisting";

        when(letterRequestRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> letterRequestService.reject(id));
        verify(letterRequestRepo, never()).save(any(LetterRequest.class));
    }

    @Test
    void testGetByStudentId_ValidStudentId_ReturnsLetterRequests() {
        String studentId = "student1";
        LetterRequest letter = LetterRequest.builder()
                .id("letter1")
                .studentId(studentId)
                .status(LetterStatus.PENDING)
                .build();

        when(letterRequestRepo.findByStudentId(studentId)).thenReturn(Arrays.asList(letter));

        List<LetterRequest> result = letterRequestService.getByStudentId(studentId);

        assertEquals(1, result.size());
        assertEquals("letter1", result.get(0).getId());
        assertEquals(studentId, result.get(0).getStudentId());
        verify(letterRequestRepo, times(1)).findByStudentId(studentId);
    }

    @Test
    void testGetByStatus_ValidStatus_ReturnsLetterRequests() {
        LetterStatus status = LetterStatus.APPROVED;
        LetterRequest letter = LetterRequest.builder()
                .id("letter1")
                .studentId("student1")
                .status(status)
                .build();

        when(letterRequestRepo.findByStatus(status)).thenReturn(Arrays.asList(letter));

        List<LetterRequest> result = letterRequestService.getByStatus(status);

        assertEquals(1, result.size());
        assertEquals("letter1", result.get(0).getId());
        assertEquals(status, result.get(0).getStatus());
        verify(letterRequestRepo, times(1)).findByStatus(status);
    }

    @Test
    void testGetById_ValidId_ReturnsLetterRequest() {
        String id = "letter1";
        LetterRequest letter = LetterRequest.builder()
                .id(id)
                .studentId("student1")
                .status(LetterStatus.PENDING)
                .build();

        when(letterRequestRepo.findById(id)).thenReturn(Optional.of(letter));

        Optional<LetterRequest> result = letterRequestService.getById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(letterRequestRepo, times(1)).findById(id);
    }

    @Test
    void testUploadCertificate_ValidFiles_UpdatesLetterRequest() throws IOException {
        String studentId = "student1";
        String requestId = "letter1";
        MultipartFile pdfFile = mock(MultipartFile.class);
        MultipartFile signatureFile = mock(MultipartFile.class);
        LetterRequest request = LetterRequest.builder()
                .id(requestId)
                .studentId(studentId)
                .status(LetterStatus.PENDING)
                .build();
        LetterRequest updatedRequest = LetterRequest.builder()
                .id(requestId)
                .studentId(studentId)
                .status(LetterStatus.APPROVED)
                .documentUrl("pdf123")
                .principalSignatureUrl("sig123")
                .issuedDate(LocalDate.now())
                .principalRemarks("Certificate uploaded successfully")
                .build();
        ObjectId pdfFileId = new ObjectId();
        ObjectId signatureFileId = new ObjectId();

        when(letterRequestRepo.findById(requestId)).thenReturn(Optional.of(request));
        when(pdfFile.isEmpty()).thenReturn(false);
        when(pdfFile.getOriginalFilename()).thenReturn("document.pdf");
        when(pdfFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(signatureFile.isEmpty()).thenReturn(false);
        when(signatureFile.getOriginalFilename()).thenReturn("signature.jpg");
        when(signatureFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(mongoTemplate.getDb()).thenReturn(mock(com.mongodb.client.MongoDatabase.class));
        when(gridFSBucket.uploadFromStream(eq("document.pdf"), any(), any(GridFSUploadOptions.class))).thenReturn(pdfFileId);
        when(gridFSBucket.uploadFromStream(eq("signature.jpg"), any(), any(GridFSUploadOptions.class))).thenReturn(signatureFileId);
        when(letterRequestRepo.save(any(LetterRequest.class))).thenReturn(updatedRequest);

        LetterRequest result = letterRequestService.uploadCertificate(studentId, requestId, pdfFile, signatureFile);

        assertEquals(LetterStatus.APPROVED, result.getStatus());
        assertEquals("pdf123", result.getDocumentUrl());
        assertEquals("sig123", result.getPrincipalSignatureUrl());
        assertEquals("Certificate uploaded successfully", result.getPrincipalRemarks());
        assertEquals(LocalDate.now(), result.getIssuedDate());
        verify(letterRequestRepo, times(1)).save(any(LetterRequest.class));
        verify(gridFSBucket, times(2)).uploadFromStream(anyString(), any(), any(GridFSUploadOptions.class));
    }

    @Test
    void testUploadCertificate_InvalidStudentId_ThrowsIllegalArgumentException() throws IOException {
        String studentId = "student1";
        String requestId = "letter1";
        MultipartFile pdfFile = mock(MultipartFile.class);
        LetterRequest request = LetterRequest.builder()
                .id(requestId)
                .studentId("student2")
                .status(LetterStatus.PENDING)
                .build();

        when(letterRequestRepo.findById(requestId)).thenReturn(Optional.of(request));
        when(pdfFile.isEmpty()).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> letterRequestService.uploadCertificate(studentId, requestId, pdfFile, null));
        verify(letterRequestRepo, never()).save(any(LetterRequest.class));
        verify(gridFSBucket, never()).uploadFromStream(anyString(), any(), any(GridFSUploadOptions.class));
    }

    @Test
    void testUploadCertificate_EmptyPdfFile_ThrowsIllegalArgumentException() {
        String studentId = "student1";
        String requestId = "letter1";
        MultipartFile pdfFile = mock(MultipartFile.class);

        when(pdfFile.isEmpty()).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> letterRequestService.uploadCertificate(studentId, requestId, pdfFile, null));
        verify(letterRequestRepo, never()).findById(anyString());
        verify(letterRequestRepo, never()).save(any(LetterRequest.class));
        verify(gridFSBucket, never()).uploadFromStream(anyString(), any(), any(GridFSUploadOptions.class));
    }

    @Test
    void testUploadCertificate_NonExistingRequest_ThrowsRuntimeException() throws IOException {
        String studentId = "student1";
        String requestId = "letter1";
        MultipartFile pdfFile = mock(MultipartFile.class);

        when(pdfFile.isEmpty()).thenReturn(false);
        when(letterRequestRepo.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> letterRequestService.uploadCertificate(studentId, requestId, pdfFile, null));
        verify(letterRequestRepo, never()).save(any(LetterRequest.class));
        verify(gridFSBucket, never()).uploadFromStream(anyString(), any(), any(GridFSUploadOptions.class));
    }
}
