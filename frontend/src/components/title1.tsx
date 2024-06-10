export default function Title1({ children, className }: Readonly<{
  children: React.ReactNode;
  className?: string;
}>) {
  return (
    <h1 className={`text-xl leading-9 font-bold block ${className}`}>
      {children}
    </h1>
  );
}