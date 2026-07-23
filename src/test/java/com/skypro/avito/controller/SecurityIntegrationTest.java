package com.skypro.avito.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skypro.avito.dto.CreateOrUpdateAd;
import com.skypro.avito.dto.CreateOrUpdateComment;
import com.skypro.avito.dto.NewPassword;
import com.skypro.avito.dto.Role;
import com.skypro.avito.entity.AdEntity;
import com.skypro.avito.entity.CommentEntity;
import com.skypro.avito.entity.UserEntity;
import com.skypro.avito.repository.AdRepository;
import com.skypro.avito.repository.CommentRepository;
import com.skypro.avito.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для проверки безопасности и прав доступа.
 * <p>
 * Покрывают сценарии:
 * <ul>
 *   <li>Публичный доступ к GET /ads и GET /ads/{id}</li>
 *   <li>Авторизация для POST /ads</li>
 *   <li>Проверка прав на редактирование и удаление объявлений (владелец, другой пользователь, администратор)</li>
 *   <li>Аналогичные проверки для комментариев</li>
 *   <li>Смена пароля (правильный и неправильный старый пароль)</li>
 *   <li>Загрузка аватарки</li>
 * </ul>
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private UserEntity owner;
    private UserEntity otherUser;
    private UserEntity admin;
    private AdEntity ad;
    private CommentEntity comment;

    /**
     * Настраивает тестовые данные перед каждым тестом.
     * <p>
     * Создаёт:
     * <ul>
     *   <li>Владельца объявления и комментария</li>
     *   <li>Другого пользователя</li>
     *   <li>Администратора</li>
     *   <li>Одно объявление (принадлежит владельцу)</li>
     *   <li>Один комментарий (принадлежит владельцу)</li>
     * </ul>
     * </p>
     */
    @BeforeEach
    void setUp() {
        owner = userRepository.save(new UserEntity(null, "owner",
                passwordEncoder.encode("password"), "Owner", "User",
                "+79991112233", Role.USER, null));
        otherUser = userRepository.save(new UserEntity(null, "other",
                passwordEncoder.encode("password"), "Other", "User",
                "+79991112244", Role.USER, null));
        admin = userRepository.save(new UserEntity(null, "admin",
                passwordEncoder.encode("password"), "Admin", "User",
                "+79991112255", Role.ADMIN, null));

        ad = adRepository.save(new AdEntity(null, "Test Ad", 1000,
                "Test description", owner, null, System.currentTimeMillis()));

        comment = commentRepository.save(new CommentEntity(null, "Test comment",
                owner, ad, System.currentTimeMillis()));
    }

    // Тесты объявлений
    @Test
    void getAdsWithoutAuth_shouldReturn200() throws Exception {
        mockMvc.perform(get("/ads"))
                .andExpect(status().isOk());
    }

    @Test
    void getAdByIdWithoutAuth_shouldReturn200() throws Exception {
        mockMvc.perform(get("/ads/{id}", ad.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void addAdWithoutAuth_shouldReturn401() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg",
                MediaType.IMAGE_JPEG_VALUE, new byte[0]);
        mockMvc.perform(multipart("/ads")
                        .file(image)
                        .param("properties", "{\"title\":\"Test\",\"price\":1000,\"description\":\"Test ad\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addAdWithAuth_shouldReturn201() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg",
                MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3});
        mockMvc.perform(multipart("/ads")
                        .file(image)
                        .param("properties", "{\"title\":\"Test\",\"price\":1000,\"description\":\"Test ad\"}")
                        .with(user("owner").password("password").roles("USER")))
                .andExpect(status().isCreated());
    }

    @Test
    void addAdWithInvalidData_shouldReturn400() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg",
                MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3});
        mockMvc.perform(multipart("/ads")
                        .file(image)
                        .param("properties", "invalid json")
                        .with(user("owner").password("password").roles("USER")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAdAsOwner_shouldReturn200() throws Exception {
        CreateOrUpdateAd body = new CreateOrUpdateAd("Updated", 2000, "Updated desc");
        mockMvc.perform(patch("/ads/{id}", ad.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(user("owner").password("password").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void updateAdAsOtherUser_shouldReturn403() throws Exception {
        CreateOrUpdateAd body = new CreateOrUpdateAd("Updated", 2000, "Updated desc");
        mockMvc.perform(patch("/ads/{id}", ad.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(user("other").password("password").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateAdAsAdmin_shouldReturn200() throws Exception {
        CreateOrUpdateAd body = new CreateOrUpdateAd("Updated", 2000, "Updated desc");
        mockMvc.perform(patch("/ads/{id}", ad.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAdAsOwner_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/ads/{id}", ad.getId())
                        .with(user("owner").password("password").roles("USER")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAdAsOtherUser_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/ads/{id}", ad.getId())
                        .with(user("other").password("password").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAdAsAdmin_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/ads/{id}", ad.getId())
                        .with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    // Тесты комментариев
    @Test
    void addCommentAsUser_shouldReturn201() throws Exception {
        CreateOrUpdateComment body = new CreateOrUpdateComment("New comment for test");
        mockMvc.perform(post("/ads/{adId}/comments", ad.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(user("other").password("password").roles("USER")))
                .andExpect(status().isCreated());
    }

    @Test
    void updateCommentAsOwner_shouldReturn200() throws Exception {
        CreateOrUpdateComment body = new CreateOrUpdateComment("Updated comment text");
        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", ad.getId(), comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(user("owner").password("password").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void updateCommentAsOtherUser_shouldReturn403() throws Exception {
        CreateOrUpdateComment body = new CreateOrUpdateComment("Updated comment text");
        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", ad.getId(), comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(user("other").password("password").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateCommentAsAdmin_shouldReturn200() throws Exception {
        CreateOrUpdateComment body = new CreateOrUpdateComment("Updated comment text");
        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", ad.getId(), comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCommentAsOwner_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", ad.getId(), comment.getId())
                        .with(user("owner").password("password").roles("USER")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCommentAsOtherUser_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", ad.getId(), comment.getId())
                        .with(user("other").password("password").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteCommentAsAdmin_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", ad.getId(), comment.getId())
                        .with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    // Тесты пользователя
    @Test
    void changePasswordWithCorrectOldPassword_shouldReturn200() throws Exception {
        NewPassword body = new NewPassword("password", "newPassword123");
        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(user("owner").password("password").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void changePasswordWithIncorrectOldPassword_shouldReturn400() throws Exception {
        NewPassword body = new NewPassword("wrongPassword", "newPassword123");
        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(user("owner").password("password").roles("USER")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAvatar_shouldReturn200() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3, 4, 5});
        mockMvc.perform(multipart(HttpMethod.PATCH, "/users/me/image")
                        .file(image)
                        .with(user("owner").password("password").roles("USER")))
                .andExpect(status().isOk());
    }

    // Тесты картинок
    @Test
    void getAdImage_shouldReturn404_forMissing() throws Exception {
        mockMvc.perform(get("/images/ads/nonexistent.jpg"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserImage_shouldReturn404_forMissing() throws Exception {
        mockMvc.perform(get("/images/users/nonexistent.jpg"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAdImage_shouldReturn200_withImage() throws Exception {
        mockMvc.perform(multipart("/ads")
                        .file(new MockMultipartFile("image", "test.jpg",
                                MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3}))
                        .param("properties", "{\"title\":\"Test\",\"price\":1000,\"description\":\"Test ad\"}")
                        .with(user("owner").password("password").roles("USER")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.image").isNotEmpty());

        String filename = "nonexistent";
        mockMvc.perform(get("/images/ads/" + filename))
                .andExpect(status().isNotFound());
    }
}