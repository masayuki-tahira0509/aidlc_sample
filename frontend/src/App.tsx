import { useCallback, useEffect, useState } from 'react';
import type { Announcement, AnnouncementInput, ListStatus } from './types';
import { announcementApi } from './api';
import { AnnouncementList } from './components/AnnouncementList';
import { AnnouncementFormModal } from './components/AnnouncementFormModal';
import { DeleteConfirmModal } from './components/DeleteConfirmModal';
import { Toast } from './components/Toast';

type ModalState =
  | { kind: 'none' }
  | { kind: 'create' }
  | { kind: 'edit'; target: Announcement }
  | { kind: 'delete'; target: Announcement };

export default function App() {
  const [announcements, setAnnouncements] = useState<Announcement[]>([]);
  const [status, setStatus] = useState<ListStatus>('loading');
  const [modal, setModal] = useState<ModalState>({ kind: 'none' });
  const [toast, setToast] = useState<string | null>(null);

  // 一覧読み込み。空ならステータスを empty に切り替える。
  const load = useCallback(async () => {
    setStatus('loading');
    try {
      const data = await announcementApi.list();
      setAnnouncements(data);
      setStatus(data.length === 0 ? 'empty' : 'ready');
    } catch {
      setStatus('error');
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  const closeModal = useCallback(() => setModal({ kind: 'none' }), []);

  const handleCreate = async (input: AnnouncementInput) => {
    await announcementApi.create(input);
    closeModal();
    setToast('お知らせを保存しました');
    await load();
  };

  const handleUpdate = (id: number) => async (input: AnnouncementInput) => {
    await announcementApi.update(id, input);
    closeModal();
    setToast('お知らせを更新しました');
    await load();
  };

  const handleDelete = (id: number) => async () => {
    await announcementApi.remove(id);
    closeModal();
    setToast('お知らせを削除しました');
    await load();
  };

  const findById = (id: number) => announcements.find((a) => a.id === id) ?? null;

  return (
    <main className="app">
      <AnnouncementList
        announcements={announcements}
        status={status}
        onCreate={() => setModal({ kind: 'create' })}
        onEdit={(id) => {
          const target = findById(id);
          if (target) setModal({ kind: 'edit', target });
        }}
        onDelete={(id) => {
          const target = findById(id);
          if (target) setModal({ kind: 'delete', target });
        }}
        onRetry={load}
      />

      {modal.kind === 'create' && (
        <AnnouncementFormModal mode="create" onSubmit={handleCreate} onCancel={closeModal} />
      )}

      {modal.kind === 'edit' && (
        <AnnouncementFormModal
          mode="edit"
          initialValue={modal.target}
          onSubmit={handleUpdate(modal.target.id)}
          onCancel={closeModal}
        />
      )}

      {modal.kind === 'delete' && (
        <DeleteConfirmModal
          title={modal.target.title}
          onConfirm={handleDelete(modal.target.id)}
          onCancel={closeModal}
        />
      )}

      {toast && <Toast message={toast} onDismiss={() => setToast(null)} />}
    </main>
  );
}
