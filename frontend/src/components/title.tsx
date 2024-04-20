import { Poppins } from 'next/font/google'

const poppins = Poppins({
  subsets: ['latin'],
  weight: "600",
})

export default function Title({ children }: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <h1 className={"text-2xl font-bold " + poppins.className}>
      {children}
    </h1>
  );
}