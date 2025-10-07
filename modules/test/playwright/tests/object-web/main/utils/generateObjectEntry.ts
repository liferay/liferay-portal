/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectField} from '@liferay/object-admin-rest-client-js';

import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {
	getObjectEntryAPIDateFormat,
	getObjectEntryUIDateFormat,
} from './dateFormat';

function getRandomDate(format: 'API' | 'UI'): string {
	const currentDate = new Date();

	const randomDate = new Date(currentDate.getTime() * Math.random());

	if (format === 'API') {
		return getObjectEntryAPIDateFormat(randomDate);
	}
	else {
		return getObjectEntryUIDateFormat(randomDate);
	}
}

function generateObjectEntryValue({
	listTypeEntriesName,
	objectEntryFormat,
	objectFieldBusinessType,
	role,
}: {
	listTypeEntriesName?: string[];
	objectEntryFormat: 'API' | 'UI';
	objectFieldBusinessType: Partial<ObjectField['businessType']>;
	role?: string;
}) {
	const listTypeEntriesRandomLength1 = listTypeEntriesName
		? Math.floor(Math.random() * listTypeEntriesName.length)
		: '';
	const listTypeEntriesRandomLength2 = listTypeEntriesName
		? Math.floor(Math.random() * listTypeEntriesName.length)
		: '';

	switch (objectFieldBusinessType) {
		case 'Assignee':
			return role;
		case 'Boolean':
			return Math.random() < 0.5;
		case 'Date':
			return getRandomDate(objectEntryFormat);
		case 'Decimal':
			return parseFloat(Math.random().toFixed(10)).toString();
		case 'Encrypted':
			return getRandomString();
		case 'Integer':
			return Math.floor(Math.random() * 100).toString();
		case 'LongInteger':
			return getRandomInt().toString();
		case 'LongText':
			return getRandomString();
		case 'MultiselectPicklist':
			return [
				listTypeEntriesName[listTypeEntriesRandomLength1],
				listTypeEntriesName[listTypeEntriesRandomLength2],
			];
		case 'Picklist':
			return {
				key: listTypeEntriesName[listTypeEntriesRandomLength1],
			};
		case 'PrecisionDecimal':
			return parseFloat(Math.random().toFixed(15)).toString();
		case 'RichText':
			return getRandomString().substring(0, 35);
		case 'Text':
			return getRandomString();
		default:
			return '';
	}
}

type UnsupportedBusinessTypes =
	| 'Aggregation'
	| 'Attachment'
	| 'AutoIncrement'
	| 'Formula'
	| 'Relationship';

export type SupportedBusinessType = Exclude<
	ObjectField['businessType'],
	UnsupportedBusinessTypes
>;

export type SupportedObjectField = Partial<ObjectField> & {
	businessType: SupportedBusinessType;
};

export async function generateObjectEntryValues({
	listTypeEntries,
	objectEntryFormat,
	objectFields,
	role,
}: {
	listTypeEntries?: string[];
	objectEntryFormat?: 'API' | 'UI';
	objectFields: ObjectField[];
	role?: string;
}) {
	const objectEntry: {
		[objectFieldName: string]: boolean | string | string[] | {key: string};
	} = {};

	for (const objectField of objectFields) {
		objectEntry[objectField.name] = generateObjectEntryValue({
			listTypeEntriesName: listTypeEntries,
			objectEntryFormat,
			objectFieldBusinessType: objectField.businessType,
			role,
		});
	}

	return {objectEntry};
}
