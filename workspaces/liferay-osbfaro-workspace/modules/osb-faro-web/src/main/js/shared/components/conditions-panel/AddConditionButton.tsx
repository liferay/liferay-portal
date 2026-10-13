import React from 'react';
import {ClayButtonWithIcon} from '@clayui/button';

interface IAddConditionButtonProps
	extends React.ButtonHTMLAttributes<HTMLButtonElement> {
	label: string;
}

const AddConditionButton = React.forwardRef<
	HTMLButtonElement,
	IAddConditionButtonProps
>(({label, ...otherProps}, ref) => (
	<ClayButtonWithIcon
		{...otherProps}
		aria-label={label}
		data-html2canvas-ignore
		displayType="secondary"
		monospaced
		ref={ref}
		size="sm"
		symbol="plus"
		title={label}
	/>
));

export default AddConditionButton;
