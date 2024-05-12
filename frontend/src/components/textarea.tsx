export default function TextArea({ className, placeholder, name, required }: Readonly<{
  className?: string;
  placeholder?: string;
  name?: string;
  required?: boolean;
}>) {
  return (
    <textarea
      placeholder={placeholder} 
      className={`${className} px-3 py-2 bg-slate-200 text-sm rounded-md focus:outline-2 focus:outline-black duration-200 placeholder:text-slate-400`}
      name={name}
      required={required} />
  );
}