'use client';

export default function Home() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-between p-24 bg-gradient-to-br bg-gray-500">
      <div className="relative flex place-items-center before:absolute before:h-[300px] before:w-full sm:before:w-[480px]">
        <h1 className="text-4xl font-bold dark:text-white">Compass 🧭</h1>
      </div>
      <a href='/api/auth/login'>Login</a>

      <div className="mb-32 grid text-center lg:max-w-5xl lg:w-full lg:mb-0 lg:grid-cols-4 lg:text-left">
        <a
          href="/timetrack"
          className="group rounded-lg border border-transparent px-5 py-4 transition-colors hover:border-gray-300 hover:bg-gray-100 hover:dark:border-neutral-700 hover:dark:bg-neutral-800/30"
          rel="noopener noreferrer"
        >
          <h2 className={`mb-3 text-2xl font-semibold`}>
            Zeiterfassung{" "}
            <span className="inline-block transition-transform group-hover:translate-x-1 motion-reduce:transform-none">
              -&gt;
            </span>
          </h2>
          <p className={`m-0 max-w-[30ch] text-sm opacity-50`}>
            Tägliche Arbeitszeiten erfassen
          </p>
        </a>
      </div>
    </main>
  );
}