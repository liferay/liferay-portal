import getCN from 'classnames';
import React from 'react';

interface IBodyProps extends React.HTMLAttributes<HTMLElement> {
	disabled?: boolean;
	fluid?: boolean;
	pageContainer?: boolean;
}

const Body: React.FC<IBodyProps> = ({
	children,
	className,
	disabled,
	fluid = false,
	pageContainer = true,
}) => {
	if (fluid) {
		return (
			<div
				className={getCN('fluid-body-root pt-3 px-5', className, {
					disabled,
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
				},
				className
			)}
		>
			<span className="children-wrapper">{children}</span>
		</div>
	);
};

export default Body;
