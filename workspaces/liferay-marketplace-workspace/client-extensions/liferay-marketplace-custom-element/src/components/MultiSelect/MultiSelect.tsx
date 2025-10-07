/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayMultiSelect from '@clayui/multi-select';

import {getIconSpriteMap} from '../../liferay/constants';
import {FieldBase} from '../FieldBase';

type MultiSelectProps<T> = {
	className?: string;
	helpMessage?: string;
	hideFeedback?: boolean;
	inputName: string;
	label?: string;
	localized?: boolean;
	multiselectKey: string;
	onChange?: (values: T) => void;
	onItemsChange: (values: T) => void;
	placeholder?: string;
	required?: boolean;
	selectedItems: T[];
	sourceItems: T[];
	tooltip?: string;
	value?: string;
};

const MultiSelect: React.FC<MultiSelectProps<any>> = ({
	className,
	helpMessage,
	hideFeedback,
	inputName,
	label,
	localized,
	multiselectKey,
	onChange,
	onItemsChange,
	placeholder,
	required,
	selectedItems,
	sourceItems,
	tooltip,
	value,
}) => {
	return (
		<FieldBase
			className={className}
			helpMessage={helpMessage}
			hideFeedback={hideFeedback}
			label={label}
			localized={localized}
			required={required}
			tooltip={tooltip}
		>
			<ClayMultiSelect
				inputName={inputName}
				items={selectedItems}
				key={multiselectKey}
				onChange={onChange}
				onItemsChange={onItemsChange}
				placeholder={placeholder}
				sourceItems={sourceItems}
				spritemap={getIconSpriteMap()}
				value={value}
			/>
		</FieldBase>
	);
};

export default MultiSelect;
