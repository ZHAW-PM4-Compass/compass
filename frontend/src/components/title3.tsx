export default function Title3({ children, className }: Readonly<{
  children: React.ReactNode;
  className?: string;
}>) {
  return (
    <h2 className={`text-lg font-bold block ${className}`}>
      {children}
    </h2>
  );
}