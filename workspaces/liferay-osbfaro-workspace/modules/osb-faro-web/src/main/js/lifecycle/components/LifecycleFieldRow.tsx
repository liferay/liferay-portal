import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import React from 'react';

interface ILifecycleFieldRowProps {
	children: React.ReactNode;
	icon: string;
	id: string;
	label: string;
	narrow?: boolean;
	required?: boolean;
}

const LifecycleFieldRow: React.FC<ILifecycleFieldRowProps> = ({
	children,
	icon,
	id,
	label,
	narrow = false,
	required = false,
}) => (
	<ClayLayout.Row className="align-items-center mb-3">
		<ClayLayout.Col md={3} xl={2}>
			<label
				className="align-items-center d-flex font-weight-semi-bold mb-0 text-4 text-nowrap"
				htmlFor={id}
			>
				<ClayIcon className="mr-2 text-secondary" symbol={icon} />

				{label}

				{required && (
					<ClayIcon className="reference-mark" symbol="asterisk" />
				)}
			</label>
		</ClayLayout.Col>

		<ClayLayout.Col md={narrow ? 5 : 9} xl={narrow ? 4 : 10}>
			{children}
		</ClayLayout.Col>
	</ClayLayout.Row>
);

export default LifecycleFieldRow;
