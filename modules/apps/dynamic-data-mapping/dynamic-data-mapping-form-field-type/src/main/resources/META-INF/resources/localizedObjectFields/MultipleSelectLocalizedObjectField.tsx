/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {stringUtils} from '@liferay/object-js-components-web';
import {useFormState} from 'data-engine-js-components-web';
import React, {useCallback, useEffect, useMemo, useState} from 'react';

import {MultipleSelectBase} from '../Select/MultipleSelectBase';
import {MultipleSelectBaseProps} from '../Select/select.d';
import {LocalizedValue} from '../types';
import LocalesDropdown, {
	AvailableLocale,
} from '../util/localizable/LocalesDropdown';

import type {Locale} from '../types';

type valueTypes = string[] | LocalizedValue<string[]>;

export interface MultipleSelectLocalizedObjectFieldProps
	extends MultipleSelectBaseProps<string[] | LocalizedValue<string[]>> {
	availableLocales: AvailableLocale[];
}

function getDefaultValue(locale: Locale, value: valueTypes) {
	return Array.isArray(value) ? {[locale]: value} : value;
}

export default function MultipleSelectLocalizedObjectField({
	availableLocales,
	errorMessage,
	fieldName,
	id,
	label,
	name,
	onChange,
	options,
	readOnly,
	required,
	tip,
	value,
}: MultipleSelectLocalizedObjectFieldProps) {
	const {
		defaultLanguageId,
		editingLanguageId,
	}: {
		defaultLanguageId: Liferay.Language.Locale;
		editingLanguageId: Liferay.Language.Locale;
	} = useFormState();

	const [localizedValues, setLocalizedValues] = useState(
		getDefaultValue(editingLanguageId, value)
	);

	const handleChange = (_: object, uniqueItems: string[]) => {
		const newLocalizedValues = {
			...localizedValues,
			[editingLanguageId]: uniqueItems,
		};

		onChange({}, newLocalizedValues);

		setLocalizedValues(newLocalizedValues);
	};

	const localizedOptions = useMemo(() => {
		return options.map((option) => ({
			...option,
			label: stringUtils.getLocalizableLabel({
				labels: option.labelMap,
				preferredLanguageId: editingLanguageId,
			}),
		}));
	}, [options, editingLanguageId]);

	const handleAsyncOptions = useCallback(() => {
		return new Promise((resolve) => {
			resolve(localizedOptions);
		});
	}, [localizedOptions]);

	useEffect(() => {
		if (!Object.hasOwn(localizedValues, editingLanguageId)) {
			setLocalizedValues((previous) => {
				return {
					...previous,
					...(localizedValues[defaultLanguageId] && {
						[editingLanguageId]: localizedValues[defaultLanguageId],
					}),
				};
			});
		}
	}, [defaultLanguageId, editingLanguageId, localizedValues]);

	return (
		<ClayInput.Group>
			<MultipleSelectBase
				defaultLanguageId={defaultLanguageId}
				errorMessage={errorMessage}
				fieldName={fieldName}
				id={id}
				label={label}
				name={name}
				onChange={handleChange}
				onLoadMore={handleAsyncOptions}
				options={localizedOptions}
				readOnly={readOnly}
				required={required}
				tip={tip}
				value={localizedValues[editingLanguageId] ?? ['']}
			/>

			<ClayInput.GroupItem shrink>
				<LocalesDropdown
					availableLocales={availableLocales}
					fieldName={fieldName}
					value={localizedValues}
				/>
			</ClayInput.GroupItem>
		</ClayInput.Group>
	);
}
