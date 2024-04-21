import { useEffect, useState } from "react";

export default function Select({ className, placeholder, data }: Readonly<{
  className?: string;
  placeholder?: string;
  data: Array<{ id: string, label: string}>;
}>) {
  const [showDropdown, setShowDropdown] = useState(false);
  const [search, setSearch] = useState("");
  const [selected, setSelected] = useState({ id: "", label: "" });
  const [focused, setFocused] = useState(false);
  const [placeholderText, setPlaceholderText] = useState(placeholder);

  const onChange = (event: any) => {
    setSearch(event.target.value);
  }

  const onFocus = () => {
    setFocused(true);
  }

  const onBlur = () => {
    setTimeout(() => {
      //setFocused(false);
    }, 10);
  }

  const isInSearch = (label: string) => {
    return !search || label.toLowerCase().includes(search.toLowerCase());
  }

  const selectItem = (item: { id: string, label: string}) => {
    setFocused(false);
    setSelected(item);
  }

  useEffect(() => {
    if (focused) {
      setShowDropdown(true);
      setPlaceholderText("");
    } else {
      setSearch("");
      setShowDropdown(false);
      setPlaceholderText(selected?.label || placeholder);
    }
  }, [focused]);

  return (
    <>
      <input 
        type="text"
        placeholder={placeholderText} 
        className={`${className} px-3 py-2 bg-slate-200 text-sm rounded-md focus:outline-2 focus:outline-black duration-200 placeholder:text-slate-900 focus:placeholder:text-slate-400`}
        value={search}
        onChange={onChange}
        onFocus={onFocus}
        onBlur={onBlur} />  
      {showDropdown && (
        <div id="dropdown" className="dropdown absolute w-auto bg-white rounded-md max-h-24 overflow-y-scroll drop-shadow border-[1px] border-slate-100">
          {data ? data.filter(item => !search || isInSearch(item.label)).map((item, index) => {
            return (
              <div 
                key={index} 
                onClick={() => selectItem(item)}
                className="hover:bg-slate-100 cursor-pointer py-2 pl-3 pr-8 text-sm">{item.label}</div>
            )
          }) : (
            <div className="py-2 pl-3 pr-8 text-sm text-slate-500">...</div>
          )}
        </div>
      )}
    </>
  );
}