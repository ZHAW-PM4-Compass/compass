import { UserProvider } from '@auth0/nextjs-auth0/client';
import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Compass - Home",
  description: "",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        <UserProvider>{children}</UserProvider>
      </body>
    </html>
  );
}
