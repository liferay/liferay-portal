/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import extractDataRecordValueKey from '../utils/extractDataRecordValueKey.es';

export default function setDataRecord(
	{
		locale,
		localizable,
		localizedValue = {
			[locale]: '',
		},
		localizedValueEdited,
		name,
		transient,
		value = '',
		visible,
	},
	dataRecordValues,
	languageId,
	preserveValue
) {
	const dataRecordValueKey = extractDataRecordValueKey(name);

	if (transient) {
		dataRecordValues[dataRecordValueKey] = '';

		return;
	}

	let _value = value;

	if (typeof value !== 'string') {
		_value = JSON.stringify(value);
	}

	if (!visible) {
		_value = localizable ? '' : _value;
	}

	if (localizable) {
		const edited =
			(!!Array.isArray(localizedValue?.[languageId]) &&
				!!localizedValue?.[languageId].length) ||
			(localizedValueEdited && localizedValueEdited[languageId]);

		const isValidValue = (value) => {
			return value === '' || !value?.length || value === false;
		};

		if (
			!edited &&
			Liferay.ThemeDisplay.getDefaultLanguageId() === languageId &&
			isValidValue(value)
		) {
			delete localizedValue[languageId];
		}

		Object.keys(localizedValue)
			.filter(
				(languageId) =>
					!localizedValueEdited?.[languageId] &&
					(localizedValue[languageId] === '' ||
						localizedValue[languageId] === '[]' ||
						localizedValue[languageId] === '{}')
			)
			.forEach((languageId) => {
				delete localizedValue[languageId];
			});

		dataRecordValues[dataRecordValueKey] = {...localizedValue};

		if (edited) {
			dataRecordValues[dataRecordValueKey] = {
				...localizedValue,
				[languageId]: _value,
			};
		}
		else if (
			preserveValue &&
			Liferay.ThemeDisplay.getDefaultLanguageId() !== languageId
		) {
			dataRecordValues[dataRecordValueKey] = {
				...localizedValue,
				[languageId]: value,
			};
		}
	}
	else {
		dataRecordValues[dataRecordValueKey] = _value;
	}
}
