/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {useFormState} from 'data-engine-js-components-web';
import React from 'react';

import CheckboxBase, {ICheckboxBaseProps} from '../Checkbox/CheckboxBase';
import LocalesDropdown, {
	AvailableLocale,
} from '../util/localizable/LocalesDropdown';

import type {FieldChangeEventHandler, LocalizedValue} from '../types';

export default function CheckboxLocalizedObjectField(props: IProps) {
	const {editingLanguageId} = useFormState();
	const {availableLocales, fieldName, onChange, value} = props;
	const checked = !!value[editingLanguageId];

	const handleCheckboxToggle: FieldChangeEventHandler<
		LocalizedValue<boolean>
	> = (event) => {
		const eventValue = event.target.value;

		const newValue = {
			...value,
			[editingLanguageId]: eventValue,
		};

		onChange({target: {value: newValue}});
	};

	return (
		<ClayInput.Group>
			<ClayInput.GroupItem>
				<CheckboxBase
					{...props}
					checked={checked}
					onChange={handleCheckboxToggle}
					supportLocalization
				/>
			</ClayInput.GroupItem>

			<ClayInput.GroupItem shrink>
				<LocalesDropdown
					availableLocales={availableLocales}
					fieldName={fieldName}
					value={value}
				/>
			</ClayInput.GroupItem>
		</ClayInput.Group>
	);
}

export interface IProps extends ICheckboxBaseProps {
	availableLocales: AvailableLocale[];
	editOnlyInDefaultLanguage: boolean;
	fieldName: string;
	onChange: FieldChangeEventHandler<LocalizedValue<boolean>>;
	systemSettingsURL: string;
	value: LocalizedValue<boolean>;
}
