import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import getCN from 'classnames';
import React from 'react';

interface ILifecycleFieldRowProps {
	children: React.ReactNode;
	errorMessage?: string;
	icon: string;
	id: string;
	label: string;
	narrow?: boolean;
	required?: boolean;
}

const LifecycleFieldRow: React.FC<ILifecycleFieldRowProps> = ({
	children,
	errorMessage,
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
			<ClayForm.Group
				className={getCN('mb-0', {'has-error': !!errorMessage})}
			>
				{children}

				{errorMessage && (
					<p
						className="font-weight-semi-bold mb-0 mt-1 text-danger"
						id={`${id}Error`}
						role="alert"
					>
						<ClayIcon className="mr-1" symbol="exclamation-full" />

						{errorMessage}
					</p>
				)}
			</ClayForm.Group>
		</ClayLayout.Col>
	</ClayLayout.Row>
);

export default LifecycleFieldRow;
