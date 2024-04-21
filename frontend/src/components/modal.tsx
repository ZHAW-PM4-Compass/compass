import Title1 from "./title1";
import Title2 from "./title2";

export default function Modal({ children, close }: Readonly<{
  children?: React.ReactNode;
  close?: () => void;
}>) {
  return (
    <div className="absolute top-0 right-0 bottom-0 left-0 bg-slate-900/40 backdrop-blur-sm z-30">
      <div className="md:container md:mx-auto px-5 md:px-24 lg:px-48 w-full">
        <div className="bg-white p-6 rounded-xl mt-24">
          <Title2>Benutzer erstellen</Title2>
        </div>
      </div>
    </div>
  );
}