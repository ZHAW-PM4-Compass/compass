import { onClose } from "@spotlightjs/spotlight";
import { useEffect } from "react";
import Button from "./button";
import Title2 from "./title2";

import { Dismiss24Regular } from '@fluentui/react-icons';
import Select from "./select";

export default function Modal({ children, close, title, saveButton }: Readonly<{
  children?: React.ReactNode;
  close: () => void;
  title: string;
  saveButton: React.ReactNode;
}>) {
  useEffect(() => {
    const handleEsc = (event: any) => {
      if (event.keyCode === 27) close();
    };

    window.addEventListener('keydown', handleEsc);
  }, []);

  const participants = [
    {
      id: "1",
      label: "Noah Baumgartner"
    },
    {
      id: "2",
      label: "Max Mustermann"
    },
    {
      id: "3",
      label: "Erika Mustermann"
    }
  ];

  return (
    <div className="absolute top-0 right-0 bottom-0 left-0 bg-slate-900/40 backdrop-blur-sm z-30">
      <div className="md:container md:mx-auto px-5 md:px-28 lg:px-60 w-full">
        <div className="bg-white p-6 lg:p-8 rounded-xl mt-24">
          <div className="flex flex-row justify-between">
            <Title2 className="leading-9">{title}</Title2>
            <button className="px-2 py-1.5 hover:bg-slate-100 duration-200 rounded-md focus:outline-2 focus:outline-black" onClick={close}>
                <Dismiss24Regular className="color-black w-5 h-5 -mt-1" />
            </button>
          </div>
          <div className="mt-5 min-h-36 max-h-[50vh] -m-4 p-4 overflow-y-scroll">
            <Select 
              placeholder="Teilnehmer wählen" 
              className="mb-4 block"
              data={participants} />
            {children}
          </div>
          <div className="flex justify-end pt-8">
            {saveButton}
          </div>
        </div>
      </div>
    </div>
  );
}