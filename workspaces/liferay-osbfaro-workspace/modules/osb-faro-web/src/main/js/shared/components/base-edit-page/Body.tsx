import getCN from 'classnames';
import React from 'react';

interface IBodyProps {
	children?: React.ReactNode;
	className?: string;
}

const Body: React.FC<IBodyProps> = ({children, className}) => (
	<div className={getCN('base-edit-page-body', className)}>{children}</div>
);

export default Body;
