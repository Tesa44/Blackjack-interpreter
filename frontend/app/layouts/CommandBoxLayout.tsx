import type { ReactNode } from "react";

type CommandBoxLayoutProps = {
  children: ReactNode;
};

export default function CommandBoxLayout({
  children,
}: CommandBoxLayoutProps) {
  return (
    <aside className="hidden min-w-0 lg:block lg:shrink-0">
      <div className="fixed right-8 top-6 w-[calc(30%-0.75rem)]">
        {children}
      </div>
    </aside>
  );
}
