import { UserProvider } from '@auth0/nextjs-auth0/client';
import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Compass",
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
