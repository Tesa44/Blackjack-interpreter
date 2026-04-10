import type { ReactNode } from "react";

type LayoutSectionProps = {
  eyebrow?: string;
  title?: string;
  description?: string;
  titleTag?: "h1" | "h2" | "h3";
  headerAside?: ReactNode;
  children: ReactNode;
  className?: string;
  contentClassName?: string;
  headerClassName?: string;
};

export default function LayoutSection({
  eyebrow,
  title,
  description,
  titleTag = "h2",
  headerAside,
  children,
  className = "",
  contentClassName = "",
  headerClassName = "",
}: LayoutSectionProps) {
  const TitleTag = titleTag;

  return (
    <section
      className={`rounded-[2rem] border border-white/10 bg-slate-950 p-6 text-slate-50 shadow-[0_30px_80px_rgba(2,6,23,0.4)] ${className}`.trim()}
    >
      {(eyebrow || title || description || headerAside) && (
        <div
          className={`mb-6 flex flex-col gap-4 md:flex-row md:items-end md:justify-between ${headerClassName}`.trim()}
        >
          <div>
            {eyebrow && (
              <p className="text-sm font-medium uppercase tracking-[0.3em] text-cyan-300">
                {eyebrow}
              </p>
            )}
            {title && (
              <TitleTag className="mt-2 text-3xl font-bold text-white">
                {title}
              </TitleTag>
            )}
            {description && (
              <p className="mt-2 max-w-2xl text-sm text-slate-300">
                {description}
              </p>
            )}
          </div>
          {headerAside}
        </div>
      )}

      <div className={contentClassName}>{children}</div>
    </section>
  );
}
