/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const defaultLanguageId = Liferay.ThemeDisplay.getDefaultLanguageId();

export const DEFAULT_VALUE_SUPPORTED_BUSINESS_TYPES = [
	'Boolean',
	'Date',
	'DateTime',
	'Decimal',
	'EmailAddress',
	'Integer',
	'Location',
	'LongInteger',
	'LongText',
	'PhoneNumber',
	'Picklist',
	'PrecisionDecimal',
	'RichText',
	'Text',
];

export const HEADERS = new Headers({
	'Accept': 'application/json',
	'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
	'Content-Type': 'application/json',
});

export const NAME_OUTPUT_OBJECT_FIELD_EXTERNAL_REFERENCE_CODE =
	'outputObjectFieldExternalReferenceCode';
