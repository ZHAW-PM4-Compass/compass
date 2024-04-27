const Header = ({ columns }: Readonly<{
  columns: Array<{ header: string, title: string }>
}>) => {
  return (
    <thead className="bg-slate-200 w-full">
      <tr>
        {columns && columns.map((column, index) => {
          return (
            <th key={index} className="py-2 px-6 text-left text-md">{column.header}</th>
          )
        })}
        <th></th>
      </tr>
    </thead>
  )  
}

const Item = ({ item, columns, actions }: Readonly<{
  item: any,
  columns: Array<{ header: string, title: string }>
  actions?: Array<{ icon: any, label?: string, onClick: (id: number) => void }>
}>) => {
  return (
    <tr className="bg-white border-t-4 border-slate-100">
      {columns && columns.map((column, index) => {
        return (
          <td key={index} className="py-4 px-6 text-left text-md">{item[column.title]}</td>
        )
      })}
      <td className="text-right text-md pr-2 min-w-48">
        {actions && actions.map((action, index) => {
          return (
            <button key={index} onClick={() => action.onClick(index)} className={`rounded-md hover:bg-slate-100 text-sm mr-2 px-2 py-1.5 focus:outline-2 focus:outline-black duration-200 ${!action.label && "w-9"}`}>
              {action.icon && <action.icon className="w-5 h-5 mr-2" />}
              {action.label}
            </button>
          )
        })}
      </td>
    </tr>
  )  
}

export default function Table({ className, data, columns, actions }: Readonly<{
  className?: string,
  data: Array<any>,
  columns: Array<{ header: string, title: string }>
  actions?: Array<{ icon: any, label?: string, onClick: (id: number) => void }>
}>) {
  return (
    <div className="overflow-scroll">
      <table className={`table-auto w-full rounded-lg overflow-hidden ${className}`}>
        <Header columns={columns}></Header>
        <tbody>
          {data && data.map((item,index) => {
            return (<Item key={index} item={item} columns={columns} actions={actions}></Item>)
          })}
        </tbody>
      </table>
    </div>
  );
}