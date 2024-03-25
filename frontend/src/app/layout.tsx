export default function RootLayout({
  children, // will be a page or nested layout
}: {
  children: React.ReactNode
}) {
  return (
      <section>
        {/* Include shared UI here e.g. a header or sidebar */}
        <nav><a href={"/"}>Startseite</a><a href={"/time-management"}>Zeit Erfassung</a></nav>

        {children}
      </section>
  )
}