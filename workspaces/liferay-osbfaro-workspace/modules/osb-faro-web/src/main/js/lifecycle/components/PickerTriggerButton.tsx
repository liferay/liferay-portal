import ClayButton from '@clayui/button';
import getCN from 'classnames';
import React from 'react';
import {Icon} from '@clayui/core';

interface IPickerTriggerButtonProps
	extends React.ButtonHTMLAttributes<HTMLButtonElement> {
	buttonClassName?: string;
	label: string;
	size?: 'sm' | 'xs';
}

const PickerTriggerButton = React.forwardRef<
	HTMLButtonElement,
	IPickerTriggerButtonProps
>(({buttonClassName, className, label, size = 'sm', ...rest}, ref) => (
	<ClayButton
		{...rest}
		className={getCN('rounded-lg', buttonClassName, {
			show: className?.split(' ').includes('show'),
		})}
		displayType="secondary"
		ref={ref}
		size={size}
	>
		{label}

		<Icon className="inline-item inline-item-after" symbol="caret-double" />
	</ClayButton>
));

export default PickerTriggerButton;
