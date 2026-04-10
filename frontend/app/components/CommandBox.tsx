export default function CommandBox() {
  return (
    <section className="w-full min-w-0 rounded-[2rem] border border-white/10 bg-[linear-gradient(180deg,rgba(15,23,42,0.98),rgba(2,6,23,0.98))] p-6 text-slate-50 shadow-[0_30px_80px_rgba(2,6,23,0.45)] lg:max-h-[calc(100vh-3rem)]">
      <div className="flex h-screen flex-col lg:max-h-[calc(100vh-6rem)]">
        <div className="mb-6">
          <p className="text-sm font-medium uppercase tracking-[0.3em] text-cyan-300">
            Console
          </p>
          <h2 className="mt-2 text-2xl font-bold text-white">Command Input</h2>
          <p className="mt-2 text-sm text-slate-300">
            Enter filters or simulation commands without leaving the dashboard.
          </p>
        </div>
        <textarea
          className="min-h-64 max-h-screen w-full flex-1 resize-none rounded-2xl border border-white/10 bg-slate-950/80 p-4 text-sm text-slate-100 placeholder:text-slate-500 focus:border-cyan-400/40 focus:outline-none lg:min-h-0"
          placeholder="Enter your command here..."
        />
        <button className="mt-4 w-full rounded-2xl bg-cyan-400 px-4 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-300">
          Send Command
        </button>
      </div>
    </section>
  );
}
