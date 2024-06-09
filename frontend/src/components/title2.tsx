export default function Title2({ children, className }: Readonly<{
  children: React.ReactNode;
  className?: string;
}>) {
  return (
    <h2 className={`text-xl font-bold block ${className}`}>
      {children}
    </h2>
  );
}