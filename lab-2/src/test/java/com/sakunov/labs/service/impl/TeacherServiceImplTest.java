package com.sakunov.labs.service.impl;

import com.sakunov.labs.entity.TeacherEntity;
import com.sakunov.labs.exception.ServiceException;
import com.sakunov.labs.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit-тесты для TeacherServiceImpl")
class TeacherServiceImplTest {
    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherServiceImpl teacherService;

    private TeacherEntity testTeacher;
    private static final int TEST_ID = 1;
    private static final String TEST_NAME = "Иван Петров";
    private static final int TEST_EXPERIENCE = 10;

    @BeforeEach
    void setUp() {
        testTeacher = new TeacherEntity(TEST_ID, TEST_NAME, TEST_EXPERIENCE);
    }

    @Test
    @DisplayName("save: успешное создание учителя с валидными данными")
    void save_withValidData_returnsGeneratedId() {
        when(teacherRepository.save(any(TeacherEntity.class))).thenReturn(TEST_ID);

        int resultId = teacherService.save(TEST_NAME, TEST_EXPERIENCE);

        assertEquals(TEST_ID, resultId);
        verify(teacherRepository, times(1)).save(any(TeacherEntity.class));
    }

    @Test
    @DisplayName("save: бросает исключение при пустом имени")
    void save_withEmptyName_throwsServiceException() {
        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> teacherService.save("", TEST_EXPERIENCE)
        );
        assertTrue(exception.getMessage().contains("пустым"));
        verify(teacherRepository, never()).save(any());
    }

    @Test
    @DisplayName("save: бросает исключение при отрицательном стаже")
    void save_withNegativeExperience_throwsServiceException() {
        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> teacherService.save(TEST_NAME, -5)
        );
        assertTrue(exception.getMessage().contains("отрицательным"));
        verify(teacherRepository, never()).save(any());
    }

    @Test
    @DisplayName("save: бросает исключение при null-стаже")
    void save_withNullExperience_throwsServiceException() {
        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> teacherService.save(TEST_NAME, null)
        );
        assertTrue(exception.getMessage().contains("отрицательным"));
        verify(teacherRepository, never()).save(any());
    }


    @Test
    @DisplayName("findById: успешный поиск существующего учителя")
    void findById_withExistingId_returnsTeacher() {
        when(teacherRepository.findById(TEST_ID)).thenReturn(Optional.of(testTeacher));

        TeacherEntity result = teacherService.findById(TEST_ID);

        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals(TEST_NAME, result.getName());
        assertEquals(TEST_EXPERIENCE, result.getExperienceYears());
        verify(teacherRepository).findById(TEST_ID);
    }

    @Test
    @DisplayName("findById: бросает исключение при отсутствии учителя")
    void findById_withNonExistingId_throwsServiceException() {
        when(teacherRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> teacherService.findById(TEST_ID)
        );
        assertTrue(exception.getMessage().contains("не найден"));
        assertTrue(exception.getMessage().contains(String.valueOf(TEST_ID)));
        verify(teacherRepository).findById(TEST_ID);
    }


    @Test
    @DisplayName("findByName: успешный поиск по существующему имени")
    void findByName_withExistingName_returnsTeacher() {
        // Arrange: репозиторий возвращает список с искомым учителем
        List<TeacherEntity> teachers = Arrays.asList(
                new TeacherEntity(2, "Мария Сидорова", 5),
                testTeacher,
                new TeacherEntity(3, "Алексей Иванов", 15)
        );
        when(teacherRepository.findAll()).thenReturn(teachers);

        TeacherEntity result = teacherService.findByName(TEST_NAME);

        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        verify(teacherRepository).findAll();
    }

    @Test
    @DisplayName("findByName: бросает исключение при отсутствии учителя с таким именем")
    void findByName_withNonExistingName_throwsServiceException() {
        List<TeacherEntity> teachers = Arrays.asList(
                new TeacherEntity(2, "Мария Сидорова", 5),
                new TeacherEntity(3, "Алексей Иванов", 15)
        );
        when(teacherRepository.findAll()).thenReturn(teachers);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> teacherService.findByName("Неизвестный")
        );
        assertTrue(exception.getMessage().contains("не найден"));
        verify(teacherRepository).findAll();
    }

    @Test
    @DisplayName("findByName: бросает исключение при пустом имени для поиска")
    void findByName_withEmptyName_throwsServiceException() {
        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> teacherService.findByName("")
        );
        assertTrue(exception.getMessage().contains("пустым"));
        verify(teacherRepository, never()).findAll();
    }

    @Test
    @DisplayName("findAll: возвращает пустой список если учителей нет")
    void findAll_whenNoTeachers_returnsEmptyList() {
        when(teacherRepository.findAll()).thenReturn(List.of());

        List<TeacherEntity> result = teacherService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(teacherRepository).findAll();
    }

    @Test
    @DisplayName("findAll: возвращает список всех учителей")
    void findAll_withTeachers_returnsAllTeachers() {
        List<TeacherEntity> expectedTeachers = Arrays.asList(
                testTeacher,
                new TeacherEntity(2, "Мария Сидорова", 5)
        );
        when(teacherRepository.findAll()).thenReturn(expectedTeachers);

        List<TeacherEntity> result = teacherService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(TEST_NAME, result.get(0).getName());
        verify(teacherRepository).findAll();
    }

    @Test
    @DisplayName("update: успешное обновление существующего учителя")
    void update_withValidTeacher_updatesSuccessfully() {
        TeacherEntity updatedTeacher = new TeacherEntity(TEST_ID, "Обновлённое имя", 20);
        when(teacherRepository.findById(TEST_ID)).thenReturn(Optional.of(testTeacher));
        when(teacherRepository.update(updatedTeacher)).thenReturn(true);

        teacherService.update(updatedTeacher);

        verify(teacherRepository).findById(TEST_ID);
        verify(teacherRepository).update(updatedTeacher);
    }

    @Test
    @DisplayName("update: бросает исключение при обновлении несуществующего учителя")
    void update_withNonExistingTeacher_throwsServiceException() {
        when(teacherRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> teacherService.update(testTeacher)
        );
        assertTrue(exception.getMessage().contains("не найден"));
        verify(teacherRepository).findById(TEST_ID);
        verify(teacherRepository, never()).update(any());
    }

    @Test
    @DisplayName("update: бросает исключение при null-объекте")
    void update_withNullTeacher_throwsServiceException() {
        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> teacherService.update(null)
        );
        assertTrue(exception.getMessage().contains("null"));
        verify(teacherRepository, never()).findById(anyInt());
    }

    @Test
    @DisplayName("update: бросает исключение при null-ID")
    void update_withNullId_throwsServiceException() {
        TeacherEntity teacherWithoutId = new TeacherEntity(null, TEST_NAME, TEST_EXPERIENCE);
        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> teacherService.update(teacherWithoutId)
        );
        assertTrue(exception.getMessage().contains("ID"));
        verify(teacherRepository, never()).findById(anyInt());
    }

    @Test
    @DisplayName("update: бросает исключение если репозиторий вернул false")
    void update_whenRepositoryReturnsFalse_throwsServiceException() {
        when(teacherRepository.findById(TEST_ID)).thenReturn(Optional.of(testTeacher));
        when(teacherRepository.update(any(TeacherEntity.class))).thenReturn(false);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> teacherService.update(testTeacher)
        );
        assertTrue(exception.getMessage().contains("Ошибка при обновлении"));
        verify(teacherRepository).update(any(TeacherEntity.class));
    }

    @Test
    @DisplayName("deleteById: успешное удаление существующего учителя")
    void deleteById_withExistingId_deletesTeacher() {
        teacherService.deleteById(TEST_ID);

        verify(teacherRepository).deleteById(TEST_ID);
    }

    @Test
    @DisplayName("deleteById: удаление несуществующего учителя не бросает исключение")
    void deleteById_withNonExistingId_doesNotThrow() {
        assertDoesNotThrow(() -> teacherService.deleteById(999));
        verify(teacherRepository).deleteById(999);
    }

    @Test
    @DisplayName("save: бросает исключение при null-имени")
    void save_withNullName_throwsServiceException() {
        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> teacherService.save(null, TEST_EXPERIENCE)
        );
        assertTrue(exception.getMessage().contains("пустым"));
        verify(teacherRepository, never()).save(any());
    }

    @Test
    @DisplayName("findByName: бросает исключение при null-имени")
    void findByName_withNullName_throwsServiceException() {
        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> teacherService.findByName(null)
        );
        assertTrue(exception.getMessage().contains("пустым"));
        verify(teacherRepository, never()).findAll();
    }
}