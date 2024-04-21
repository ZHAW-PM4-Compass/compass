export default function Input({ className, type, placeholder }: Readonly<{
  className?: string;
  type?: string;
  placeholder?: string;
}>) {
  return (
    <input 
      type={type} 
      placeholder={placeholder} 
      className={`${className} px-3 py-2 bg-slate-200 text-sm rounded-md focus:outline-2 focus:outline-black duration-200`} />
  );
}