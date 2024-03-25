import { UserProvider } from '@auth0/nextjs-auth0/client';

export default function RootLayout({
  children, // will be a page or nested layout
}: {
  children: React.ReactNode
}) {
  return (
    <UserProvider>
      <section>
        {/* Include shared UI here e.g. a header or sidebar */}
        <nav><a className="mr-4" href={"/"}>Startseite</a><a href={"/time-management"}>Zeit Erfassung</a></nav>
        {children}
      </section>
    </UserProvider>
  )
}