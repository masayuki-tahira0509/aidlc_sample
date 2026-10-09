// カテゴリバッジ [FR5.1][interaction-spec: CategoryBadge]
// 色のみに依存せず、必ずテキストラベルを含む [WCAG 1.4.1]。

interface CategoryBadgeProps {
  category: string;
}

// 控えめな配色。色は補助であり、ラベル文字が常に意味を伝える。
const COLOR_MAP: Record<string, { bg: string; fg: string }> = {
  一般: { bg: '#e8eef5', fg: '#1f3a5f' },
  重要: { bg: '#fdeaea', fg: '#8a1f1f' },
  業務連絡: { bg: '#eaf5ee', fg: '#1f5f36' },
};

export function CategoryBadge({ category }: CategoryBadgeProps) {
  const colors = COLOR_MAP[category] ?? { bg: '#eceff1', fg: '#37474f' };
  return (
    <span
      className="category-badge"
      data-testid="category-badge"
      style={{ backgroundColor: colors.bg, color: colors.fg }}
    >
      {category}
    </span>
  );
}
