/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import React from 'react';

import FieldBase from '../FieldBase/ReactFieldBase.es';
import CheckboxLocalizedObjectField, {
	IProps as ICheckboxLocalizedObjectFieldProps,
} from '../localizedObjectFields/CheckboxLocalizedObjectField';
import CheckboxBase from './CheckboxBase';

const Checkbox: React.FC<IProps> = (props) => {
	const {predefinedValue, value} = props;

	const checked = !!(
		value ??
		(Array.isArray(predefinedValue)
			? predefinedValue[0] === 'true'
			: predefinedValue)
	);

	return (
		<ClayInput.Group>
			<ClayInput.GroupItem>
				<CheckboxBase {...props} checked={checked} />
			</ClayInput.GroupItem>
		</ClayInput.Group>
	);
};

export default function Main({
	label,
	localizedObjectField,
	required,
	...otherProps
}: IProps) {
	const Component =
		Liferay.FeatureFlags['LPD-32050'] && localizedObjectField
			? CheckboxLocalizedObjectField
			: Checkbox;

	return (
		<FieldBase showLabel={false} {...otherProps}>
			<Component label={label} required={required} {...otherProps} />
		</FieldBase>
	);
}

interface IProps extends ICheckboxLocalizedObjectFieldProps {
	localizedObjectField?: boolean;
	predefinedValue?: boolean | String[];
	readOnly?: boolean;
	showAsSwitcher?: boolean;
	showMaximumRepetitionsInfo?: boolean;
	supportLocalization: boolean;
	systemSettingsURL: string;
	visible?: boolean;
}

Main.displayName = 'Checkbox';
