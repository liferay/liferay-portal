import getCN from 'classnames';
import React from 'react';

interface IBodyProps extends React.HTMLAttributes<HTMLElement> {
	disabled?: boolean;
	fluid?: boolean;
	pageContainer?: boolean;
	sidebarOpened?: boolean;
}

const Body: React.FC<IBodyProps> = ({
	children,
	className,
	disabled,
	fluid = false,
	pageContainer = true,
	sidebarOpened = false,
}) => {
	if (fluid) {
		return (
			<div
				className={getCN('fluid-body-root pt-3 px-5', className, {
					disabled,
					'sidebar-opened': sidebarOpened,
				})}
			>
				{children}
			</div>
		);
	}

	return (
		<div
			className={getCN(
				'body-root',
				{
					disabled,
					'page-container': pageContainer,
					'sidebar-opened': sidebarOpened,
				},
				className
			)}
		>
			<span className="children-wrapper">{children}</span>
		</div>
	);
};

export default Body;
