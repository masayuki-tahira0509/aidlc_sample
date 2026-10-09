package com.example.announcementboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.announcementboard.domain.Announcement;
import com.example.announcementboard.domain.Category;
import com.example.announcementboard.repository.AnnouncementRepository;

/**
 * サービス層の単体テスト [team-practices][Testing Contract]。
 *
 * <p>リポジトリは Mockito でモックし、検証ロジック・既定値補完・投稿日時の
 * 不変保持を単体で確認する。正常系と必須未入力の異常系を含む。</p>
 */
@ExtendWith(MockitoExtension.class)
class AnnouncementServiceTest {

    @Mock
    private AnnouncementRepository repository;

    @InjectMocks
    private AnnouncementService service;

    private AnnouncementInput validInput(String author) {
        return new AnnouncementInput("タイトル", "本文です", author, "一般");
    }

    // ---------------- 正常系（ハッピーパス） ----------------

    @Test
    void create_savesAnnouncementWithTrimmedTitleAndCategory() {
        when(repository.save(any(Announcement.class))).thenAnswer(inv -> inv.getArgument(0));

        Announcement result = service.create(new AnnouncementInput("  題名  ", "本文", "太郎", "重要"));

        assertThat(result.getTitle()).isEqualTo("題名");
        assertThat(result.getCategory()).isEqualTo(Category.IMPORTANT);
        verify(repository).save(any(Announcement.class));
    }

    @Test
    void create_fillsDefaultAuthorWhenBlank() {
        ArgumentCaptor<Announcement> captor = ArgumentCaptor.forClass(Announcement.class);
        when(repository.save(any(Announcement.class))).thenAnswer(inv -> inv.getArgument(0));

        service.create(validInput("   "));

        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getAuthor()).isEqualTo("名無し");
    }

    @Test
    void create_keepsProvidedAuthor() {
        ArgumentCaptor<Announcement> captor = ArgumentCaptor.forClass(Announcement.class);
        when(repository.save(any(Announcement.class))).thenAnswer(inv -> inv.getArgument(0));

        service.create(validInput("花子"));

        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getAuthor()).isEqualTo("花子");
    }

    @Test
    void findAll_delegatesToDescendingQuery() {
        Announcement a = new Announcement("t", "b", "太郎", Category.GENERAL);
        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(java.util.List.of(a));

        assertThat(service.findAll()).containsExactly(a);
        verify(repository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void update_changesFieldsButKeepsCreatedAt() {
        Instant original = Instant.now().minus(3, ChronoUnit.HOURS);
        Announcement existing = new Announcement("旧", "旧本文", "太郎", Category.GENERAL);
        existing.setCreatedAt(original);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Announcement.class))).thenAnswer(inv -> inv.getArgument(0));

        Announcement result = service.update(1L,
                new AnnouncementInput("新題名", "新本文", "次郎", "業務連絡"));

        assertThat(result.getTitle()).isEqualTo("新題名");
        assertThat(result.getCategory()).isEqualTo(Category.BUSINESS);
        assertThat(result.getCreatedAt()).isEqualTo(original); // 不変 [FR3.3]
    }

    @Test
    void delete_removesWhenExists() {
        when(repository.existsById(5L)).thenReturn(true);

        service.delete(5L);

        verify(repository).deleteById(5L);
    }

    // ---------------- 異常系（検証失敗） ----------------

    @Test
    void create_rejectsMissingTitle() {
        AnnouncementInput input = new AnnouncementInput("  ", "本文", "太郎", "一般");

        assertThatThrownBy(() -> service.create(input))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("タイトル");
        verify(repository, never()).save(any());
    }

    @Test
    void create_rejectsMissingBody() {
        AnnouncementInput input = new AnnouncementInput("タイトル", "   ", "太郎", "一般");

        assertThatThrownBy(() -> service.create(input))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("本文");
        verify(repository, never()).save(any());
    }

    @Test
    void create_rejectsDisallowedCategory() {
        AnnouncementInput input = new AnnouncementInput("タイトル", "本文", "太郎", "緊急");

        assertThatThrownBy(() -> service.create(input))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("カテゴリ");
        verify(repository, never()).save(any());
    }

    // ---------------- 境界値テスト（文字数上限） ----------------

    private static String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    @Test
    void create_acceptsTitleAtMaxLength() {
        when(repository.save(any(Announcement.class))).thenAnswer(inv -> inv.getArgument(0));
        String title100 = repeat("あ", 100); // ちょうど100字 = OK

        Announcement result = service.create(new AnnouncementInput(title100, "本文", "太郎", "一般"));

        assertThat(result.getTitle()).hasSize(100);
        verify(repository).save(any(Announcement.class));
    }

    @Test
    void create_rejectsTitleOverMaxLength() {
        String title101 = repeat("あ", 101); // 101字 = NG
        AnnouncementInput input = new AnnouncementInput(title101, "本文", "太郎", "一般");

        assertThatThrownBy(() -> service.create(input))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("タイトル");
        verify(repository, never()).save(any());
    }

    @Test
    void create_acceptsBodyAtMaxLength() {
        when(repository.save(any(Announcement.class))).thenAnswer(inv -> inv.getArgument(0));
        String body2000 = repeat("本", 2000); // ちょうど2000字 = OK

        Announcement result = service.create(new AnnouncementInput("タイトル", body2000, "太郎", "一般"));

        assertThat(result.getBody()).hasSize(2000);
        verify(repository).save(any(Announcement.class));
    }

    @Test
    void create_rejectsBodyOverMaxLength() {
        String body2001 = repeat("本", 2001); // 2001字 = NG
        AnnouncementInput input = new AnnouncementInput("タイトル", body2001, "太郎", "一般");

        assertThatThrownBy(() -> service.create(input))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("本文");
        verify(repository, never()).save(any());
    }

    @Test
    void create_acceptsAuthorAtMaxLength() {
        when(repository.save(any(Announcement.class))).thenAnswer(inv -> inv.getArgument(0));
        String author100 = repeat("名", 100); // ちょうど100字 = OK

        Announcement result = service.create(new AnnouncementInput("タイトル", "本文", author100, "一般"));

        assertThat(result.getAuthor()).hasSize(100);
        verify(repository).save(any(Announcement.class));
    }

    @Test
    void create_rejectsAuthorOverMaxLength() {
        String author101 = repeat("名", 101); // 101字 = NG
        AnnouncementInput input = new AnnouncementInput("タイトル", "本文", author101, "一般");

        assertThatThrownBy(() -> service.create(input))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("投稿者名");
        verify(repository, never()).save(any());
    }

    @Test
    void update_throwsNotFoundWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, validInput("太郎")))
                .isInstanceOf(NotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void delete_throwsNotFoundWhenMissing() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(NotFoundException.class);
        verify(repository, never()).deleteById(anyLong());
    }
}
