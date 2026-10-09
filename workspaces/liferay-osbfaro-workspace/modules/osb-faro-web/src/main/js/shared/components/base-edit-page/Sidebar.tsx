import getCN from 'classnames';
import React from 'react';

interface ISidebarProps {
	children?: React.ReactNode;
	className?: string;
	position?: 'left' | 'right';
}

/**
 * Must be placed inside BaseEditPage.Body, which reserves the sidebar's width
 * so it pushes the main content instead of overlapping it.
 */
const Sidebar: React.FC<ISidebarProps> = ({
	children,
	className,
	position = 'left',
}) => (
	<aside
		className={getCN(
			'base-edit-page-sidebar bg-white',
			`base-edit-page-sidebar-${position}`,
			{
				'border-left': position === 'right',
				'border-right': position === 'left',
			},
			className
		)}
	>
		{children}
	</aside>
);

export default Sidebar;
