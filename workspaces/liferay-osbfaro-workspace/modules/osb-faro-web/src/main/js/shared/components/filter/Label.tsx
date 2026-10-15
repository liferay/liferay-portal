import ClayLabel from '@clayui/label';
import React from 'react';

interface ILabelProps extends React.HTMLAttributes<HTMLSpanElement> {
	label: string;
	onRemove: () => void;
}

export const Label: React.FC<ILabelProps> = ({label, onRemove}) => (
	<ClayLabel
		closeButtonProps={{
			'aria-label': Liferay.Language.get('close'),
			onClick: onRemove,
		}}
		displayType="secondary"
		inverse
		size="lg"
	>
		{label}
	</ClayLabel>
);
