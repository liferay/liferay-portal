/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const ALLOWED_BUSINESS_TYPES = [
	'Aggregation',
	'Attachment',
	'Date',
	'DateTime',
	'Decimal',
	'Encrypted',
	'Formula',
	'Integer',
	'LongInteger',
	'LongText',
	'MultiselectPicklist',
	'Picklist',
	'PrecisionDecimal',
	'RichText',
	'Text',
	'Workflow Status',
];

export const ALLOWED_UNMODIFIABLE_OBJECTS = Liferay.FeatureFlags['LPD-21414']
	? ['L_ACCOUNT', 'L_USER']
	: [];

export const BUSINESS_TYPES_TO_SYMBOLS = {
	'Aggregation': 'text',
	'Attachment': 'file-script',
	'Date': 'date',
	'DateTime': 'date-time',
	'Decimal': 'number',
	'Encrypted': 'text',
	'Formula': 'text,',
	'Integer': 'number',
	'LongInteger': 'number',
	'LongText': 'text',
	'MultiselectPicklist': 'list',
	'Picklist': 'list',
	'PrecisionDecimal': 'number',
	'Record': 'folder',
	'Relationship': 'text',
	'RichText': 'text',
	'Text': 'text',
	'Workflow Status': 'text',
};

export const DEFAULT_LANGUAGE_ID: string =
	Liferay.ThemeDisplay.getDefaultLanguageId();

export const HTTP_METHODS = {
	GET: 'get',
	POST: 'post',
};

export const RETRIEVE_TYPES = {
	COLLECTION: 'collection',
	SINGLE_ELEMENT: 'singleElement',
};

export const STR_BLANK = '';
