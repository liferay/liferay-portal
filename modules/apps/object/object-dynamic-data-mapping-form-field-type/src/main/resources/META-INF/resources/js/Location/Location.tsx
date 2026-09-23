/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {
	LocationInput,
	LocationValue,
	MAP_PROVIDER,
	MapProviderKey,
	stringifyLocationValue,
} from '@liferay/object-js-components-web';
import {useFormState} from 'data-engine-js-components-web';
import {LocalesDropdown} from 'dynamic-data-mapping-form-field-type';
import {ReactFieldBase as FieldBase} from 'dynamic-data-mapping-form-field-type/api';
import React, {useEffect, useState} from 'react';

import type {
	FieldChangeEventHandler,
	LocalizedValue,
} from 'dynamic-data-mapping-form-field-type';

interface BaseLocationProps {
	fieldName: string;
	googleMapsAPIKey?: string;
	id?: string;
	mapProviderKey?: MapProviderKey;
	name: string;
	onBlur?: (event: React.FocusEvent<HTMLInputElement>) => void;
	onFocus?: (event: React.FocusEvent<HTMLInputElement>) => void;
	readOnly?: boolean;
	[key: string]: unknown;
}

interface LocalizableLocationProps extends BaseLocationProps {
	localizedObjectField: true;
	onChange?: FieldChangeEventHandler<LocalizedValue<string>>;
	value?: LocalizedValue<string>;
}

interface NonLocalizableLocationProps extends BaseLocationProps {
	localizedObjectField?: false;
	onChange?: FieldChangeEventHandler<string>;
	value?: string;
}

type LocationProps = LocalizableLocationProps | NonLocalizableLocationProps;

const LocalizableLocation = ({
	fieldName,
	googleMapsAPIKey,
	id,
	mapProviderKey = MAP_PROVIDER.openStreetMap,
	name,
	onBlur,
	onChange,
	onFocus,
	readOnly,
	value = {} as LocalizedValue<string>,
	...otherProps
}: LocalizableLocationProps) => {
	const {availableLocales, editingLanguageId} = useFormState();

	const disabled = readOnly || (otherProps.disabled as boolean);

	const handleLocationChange = (locationValue: LocationValue | null) => {
		onChange?.({
			target: {
				value: {
					...value,
					[editingLanguageId]: stringifyLocationValue(locationValue),
				},
			},
		});
	};

	return (
		<FieldBase
			{...otherProps}
			id={id ?? name}
			name={name}
			readOnly={disabled}
		>
			<LocationInput
				disabled={disabled}
				googleMapsAPIKey={googleMapsAPIKey}
				id={id ?? name}
				mapProviderKey={mapProviderKey}
				onBlur={onBlur}
				onFocus={onFocus}
				onLocationChange={handleLocationChange}
				serializedValue={value[editingLanguageId]}
			>
				<ClayInput.GroupItem shrink>
					<LocalesDropdown
						availableLocales={availableLocales}
						fieldName={fieldName}
						value={value}
					/>
				</ClayInput.GroupItem>
			</LocationInput>
		</FieldBase>
	);
};

const NonLocalizableLocation = ({
	googleMapsAPIKey,
	id,
	mapProviderKey = MAP_PROVIDER.openStreetMap,
	name,
	onBlur,
	onChange,
	onFocus,
	readOnly,
	value = '',
	...otherProps
}: NonLocalizableLocationProps) => {
	const [serializedValue, setSerializedValue] = useState(value);

	useEffect(() => {
		setSerializedValue(value);
	}, [value]);

	const disabled = readOnly || (otherProps.disabled as boolean);

	const handleLocationChange = (locationValue: LocationValue | null) => {
		const newValue = stringifyLocationValue(locationValue);

		setSerializedValue(newValue);

		onChange?.({target: {value: newValue}});
	};

	return (
		<FieldBase
			{...otherProps}
			id={id ?? name}
			name={name}
			readOnly={disabled}
		>
			<LocationInput
				disabled={disabled}
				googleMapsAPIKey={googleMapsAPIKey}
				id={id ?? name}
				mapProviderKey={mapProviderKey}
				onBlur={onBlur}
				onFocus={onFocus}
				onLocationChange={handleLocationChange}
				serializedValue={serializedValue}
			/>

			<input name={name} type="hidden" value={serializedValue} />
		</FieldBase>
	);
};

const Location = (props: LocationProps) =>
	props.localizedObjectField ? (
		<LocalizableLocation {...props} />
	) : (
		<NonLocalizableLocation {...props} />
	);

export default Location;
