package com.example.announcementboard.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.announcementboard.domain.Announcement;
import com.example.announcementboard.domain.Category;
import com.example.announcementboard.service.AnnouncementInput;
import com.example.announcementboard.service.AnnouncementService;
import com.example.announcementboard.service.NotFoundException;
import com.example.announcementboard.service.ValidationException;

/**
 * API 層のスライステスト [Testing Contract]。
 *
 * <p>{@code @WebMvcTest} でサービスをモックし、各CRUDの正常系と必須未入力の
 * 異常系（400 + 統一エラー本文）を検証する。</p>
 */
@WebMvcTest(AnnouncementController.class)
@TestPropertySource(properties = "app.cors.allowed-origin=http://localhost:5173")
class AnnouncementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnnouncementService service;

    private Announcement sample() {
        Announcement a = new Announcement("タイトル", "本文", "太郎", Category.GENERAL);
        a.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        return a;
    }

    @Test
    void list_returnsAnnouncements() throws Exception {
        when(service.findAll()).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("タイトル")))
                .andExpect(jsonPath("$[0].category", is("一般")));
    }

    @Test
    void create_returns201() throws Exception {
        when(service.create(any(AnnouncementInput.class))).thenReturn(sample());

        String json = "{\"title\":\"タイトル\",\"body\":\"本文\",\"author\":\"太郎\",\"category\":\"一般\"}";
        mockMvc.perform(post("/api/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("タイトル")));
    }

    @Test
    void create_returns400OnValidationError() throws Exception {
        when(service.create(any(AnnouncementInput.class)))
                .thenThrow(new ValidationException("タイトルは必須です。"));

        String json = "{\"title\":\"\",\"body\":\"本文\",\"category\":\"一般\"}";
        mockMvc.perform(post("/api/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("タイトルは必須です。")));
    }

    @Test
    void update_returnsUpdated() throws Exception {
        when(service.update(eq(1L), any(AnnouncementInput.class))).thenReturn(sample());

        String json = "{\"title\":\"タイトル\",\"body\":\"本文\",\"author\":\"太郎\",\"category\":\"一般\"}";
        mockMvc.perform(put("/api/announcements/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("タイトル")));
    }

    @Test
    void update_returns404WhenMissing() throws Exception {
        when(service.update(eq(99L), any(AnnouncementInput.class)))
                .thenThrow(new NotFoundException("お知らせが見つかりません: id=99"));

        String json = "{\"title\":\"タイトル\",\"body\":\"本文\",\"category\":\"一般\"}";
        mockMvc.perform(put("/api/announcements/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns204() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/announcements/1"))
                .andExpect(status().isNoContent());
        verify(service).delete(1L);
    }

    @Test
    void delete_returns404WhenMissing() throws Exception {
        doThrow(new NotFoundException("お知らせが見つかりません: id=99")).when(service).delete(99L);

        mockMvc.perform(delete("/api/announcements/99"))
                .andExpect(status().isNotFound());
    }
}
