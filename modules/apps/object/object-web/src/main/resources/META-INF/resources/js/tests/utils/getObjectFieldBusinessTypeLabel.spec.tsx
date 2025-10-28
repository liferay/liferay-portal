/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {getObjectFieldBusinessTypeLabel} from '../../utils/getObjectFieldBusinessTypeLabel';

describe('Check the return for all object field business types)', () => {
	it('returns aggregation key', () => {
		expect(getObjectFieldBusinessTypeLabel('Aggregation')).toStrictEqual(
			'aggregation'
		);
	});

	it('returns attachment language key', () => {
		expect(getObjectFieldBusinessTypeLabel('Attachment')).toStrictEqual(
			'attachment'
		);
	});

	it('returns auto increment language key', () => {
		expect(getObjectFieldBusinessTypeLabel('AutoIncrement')).toStrictEqual(
			'auto-increment'
		);
	});

	it('returns boolean language key', () => {
		expect(getObjectFieldBusinessTypeLabel('Boolean')).toStrictEqual(
			'boolean'
		);
	});

	it('returns date language key', () => {
		expect(getObjectFieldBusinessTypeLabel('Date')).toStrictEqual('date');
	});

	it('returns date time language key', () => {
		expect(getObjectFieldBusinessTypeLabel('DateTime')).toStrictEqual(
			'date-time'
		);
	});

	it('returns decimal language key', () => {
		expect(getObjectFieldBusinessTypeLabel('Decimal')).toStrictEqual(
			'decimal'
		);
	});

	it('returns decimal language key', () => {
		expect(getObjectFieldBusinessTypeLabel('Decimal')).toStrictEqual(
			'decimal'
		);
	});

	it('returns encrypted language key', () => {
		expect(getObjectFieldBusinessTypeLabel('Encrypted')).toStrictEqual(
			'encrypted'
		);
	});

	it('returns formula language key', () => {
		expect(getObjectFieldBusinessTypeLabel('Formula')).toStrictEqual(
			'formula'
		);
	});

	it('returns integer language key', () => {
		expect(getObjectFieldBusinessTypeLabel('Integer')).toStrictEqual(
			'integer'
		);
	});

	it('returns long text language key', () => {
		expect(getObjectFieldBusinessTypeLabel('LongText')).toStrictEqual(
			'long-text'
		);
	});

	it('returns multiselect picklist language key', () => {
		expect(
			getObjectFieldBusinessTypeLabel('MultiselectPicklist')
		).toStrictEqual('multiselect-picklist');
	});

	it('returns Picklist language key', () => {
		expect(getObjectFieldBusinessTypeLabel('Picklist')).toStrictEqual(
			'picklist'
		);
	});

	it('returns precision decimal language key', () => {
		expect(
			getObjectFieldBusinessTypeLabel('PrecisionDecimal')
		).toStrictEqual('precision-decimal');
	});

	it('returns relationship language key', () => {
		expect(getObjectFieldBusinessTypeLabel('Relationship')).toStrictEqual(
			'relationship'
		);
	});

	it('returns rich text language key', () => {
		expect(getObjectFieldBusinessTypeLabel('RichText')).toStrictEqual(
			'rich-text'
		);
	});

	it('returns text language key', () => {
		expect(getObjectFieldBusinessTypeLabel('Text')).toStrictEqual('text');
	});
});
