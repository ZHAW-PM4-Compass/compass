export default function Input({ className, type, placeholder, name, disabled }: Readonly<{
  className?: string;
  type?: string;
  placeholder?: string;
  name?: string;
  disabled?: boolean;
}>) {
  return (
    <input 
      type={type}
      placeholder={placeholder} 
      className={`${className} px-3 py-2 bg-slate-200 text-sm rounded-md focus:outline-2 focus:outline-black duration-200 placeholder:text-slate-400`} 
      name={name}
      disabled={disabled}/>
  );
}