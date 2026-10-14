import ClayLayout from '@clayui/layout';
import getCN from 'classnames';
import React from 'react';

interface IMainProps {
	children?: React.ReactNode;
	className?: string;
}

/**
 * Centers the page content in a container at most 1280px wide.
 */
const Main: React.FC<IMainProps> = ({children, className}) => (
	<ClayLayout.ContainerFluid
		className={getCN('base-edit-page-main py-4', className)}
		size="xl"
	>
		{children}
	</ClayLayout.ContainerFluid>
);

export default Main;
