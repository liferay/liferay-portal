import React from 'react';
import {Icon} from '@clayui/core';

const FilterPickerTrigger = React.forwardRef<
	HTMLButtonElement,
	React.ButtonHTMLAttributes<HTMLButtonElement>
>(({children, ...rest}, ref) => (
	<button {...rest} ref={ref}>
		<Icon className="inline-item inline-item-before" symbol="filter" />

		{children}
	</button>
));

FilterPickerTrigger.displayName = 'FilterPickerTrigger';

export default FilterPickerTrigger;
