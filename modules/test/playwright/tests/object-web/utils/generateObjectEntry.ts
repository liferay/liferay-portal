/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectField} from '@liferay/object-admin-rest-client-js';

import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {
	getObjectEntryAPIDateFormat,
	getObjectEntryAPIDateTimeFormat,
	getObjectEntryUIDateFormat,
	getObjectEntryUIDateTimeFormat,
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

function getRandomDateTime(format: 'API' | 'UI'): string {
	const currentDate = new Date();

	const randomDate = new Date(currentDate.getTime() * Math.random());

	if (format === 'API') {
		return getObjectEntryAPIDateTimeFormat(randomDate);
	}
	else {
		return getObjectEntryUIDateTimeFormat(randomDate);
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
		: 0;

	switch (objectFieldBusinessType) {
		case 'Assignee':
			return role;
		case 'Boolean':
			return Math.random() < 0.5;
		case 'Date':
			return getRandomDate(objectEntryFormat);
		case 'DateTime':
			return getRandomDateTime(objectEntryFormat);
		case 'Decimal':
			return parseFloat(
				(0.001 + Math.random() * 0.998).toFixed(10)
			).toString();
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
				listTypeEntriesName[
					(listTypeEntriesRandomLength1 + 1) %
						listTypeEntriesName.length
				],
			];
		case 'Picklist':
			return {
				key: listTypeEntriesName[listTypeEntriesRandomLength1],
			};
		case 'PhoneNumber':
			return String(Math.floor(1000000000 + Math.random() * 9000000000));
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

const UNSUPPORTED_BUSINESS_TYPES = [
	'Aggregation',
	'AutoIncrement',
	'Formula',
	'Relationship',
] as const;

type UnsupportedBusinessTypes = (typeof UNSUPPORTED_BUSINESS_TYPES)[number];

export type SupportedBusinessType = Exclude<
	ObjectField['businessType'],
	UnsupportedBusinessTypes
>;

export function isFillableBusinessType(
	businessType: ObjectField['businessType']
): businessType is SupportedBusinessType {
	return !UNSUPPORTED_BUSINESS_TYPES.includes(
		businessType as UnsupportedBusinessTypes
	);
}

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
