const Button: React.FC<{ children: React.ReactNode, className: string, onClick: any }> = ({ children, className, onClick }) => {
    return (
        <button className={`text-white bg-gray-900 hover:bg-gray-800 focus:outline-none focus:ring-4 focus:ring-gray-300 font-medium rounded-md text-sm px-4 py-2 me-2 mb-2 ${className}`} onClick={onClick}>
            {children}
        </button>
    );
}

export default Button;