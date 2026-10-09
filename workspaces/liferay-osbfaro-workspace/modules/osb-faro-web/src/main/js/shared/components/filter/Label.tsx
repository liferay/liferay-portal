import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import React from 'react';

interface ILabelProps extends React.HTMLAttributes<HTMLSpanElement> {
	label: string;
	onRemove: () => void;
}

export const Label: React.FC<ILabelProps> = ({label, onRemove}) => (
	<span className="label label-secondary label-dismissible label-lg">
		<span className="label-item label-item-expand">{label}</span>

		<span className="label-item label-item-after">
			<ClayButton
				aria-label={Liferay.Language.get('close')}
				className="button-root close"
				displayType="unstyled"
				onClick={onRemove}
			>
				<ClayIcon symbol="icon-root times" />
			</ClayButton>
		</span>
	</span>
);
