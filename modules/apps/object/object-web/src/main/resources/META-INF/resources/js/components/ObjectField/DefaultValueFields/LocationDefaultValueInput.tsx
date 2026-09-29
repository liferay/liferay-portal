/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	LocationInput,
	LocationValue,
	MapProviderKey,
	stringifyLocationValue,
} from '@liferay/object-js-components-web';
import {FieldBase} from 'frontend-js-components-web';
import React from 'react';

import {getUpdatedDefaultValueFieldSettings} from '../../../utils/defaultValues';
import {InputAsValueFieldComponentProps} from '../Tabs/Advanced/DefaultValueContainer';

const LocationDefaultValueInput: React.FC<InputAsValueFieldComponentProps> = ({
	defaultValue,
	error,
	googleMapsAPIKey,
	id,
	label,
	mapProviderKey,
	onSubmit,
	required,
	setValues,
	values,
}: InputAsValueFieldComponentProps) => {
	const serializedValue =
		typeof defaultValue === 'string' ? defaultValue : '';

	const handleLocationChange = (locationValue: LocationValue | null) => {
		const newSettings = getUpdatedDefaultValueFieldSettings(
			values,
			stringifyLocationValue(locationValue),
			'inputAsValue'
		);

		setValues({objectFieldSettings: newSettings});

		onSubmit?.({
			...values,
			objectFieldSettings: newSettings,
		});
	};

	return (
		<FieldBase
			errorMessage={error}
			id={id}
			label={label}
			required={required}
		>
			<LocationInput
				googleMapsAPIKey={googleMapsAPIKey}
				id={id ?? 'locationDefaultValue'}
				mapProviderKey={mapProviderKey as MapProviderKey}
				onLocationChange={handleLocationChange}
				serializedValue={serializedValue}
			/>
		</FieldBase>
	);
};

export default LocationDefaultValueInput;
