/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {
	MS_PER_DAY,
	OBJECT_ENTRY_FOLDER_CLASS_NAME,
} from '../../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
import * as expirationStatus from '../../../../../src/main/resources/META-INF/resources/js/common/utils/expirationStatus';
import {
	getFileMimeTypeObjectDefinitionStickerValue,
	transformItemCardView,
} from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/utils/transformViewsItemProps';

const NOW = new Date('2026-04-21T10:00:00Z');

const callTransform = (item: any, props: any) =>
	transformItemCardView(item, undefined, undefined, {}, {}, props);

describe('transformItemCardView labels', () => {
	beforeAll(() => {
		jest.useFakeTimers();
		jest.setSystemTime(NOW);
	});

	afterAll(() => {
		jest.useRealTimers();
	});

	it('returns an empty label for folder items', () => {
		const result = callTransform(
			{embedded: {}, entryClassName: OBJECT_ENTRY_FOLDER_CLASS_NAME},
			{actions: [], labels: [{displayType: 'success', value: 'approved'}]}
		);

		expect(result.labels).toEqual([{displayType: 'empty', value: ''}]);
	});

	it('returns the original labels unchanged when nothing expiration-related applies', () => {
		const labels = [{displayType: 'success', value: 'approved'}];

		const result = callTransform(
			{
				embedded: {
					expirationDate: new Date(
						NOW.getTime() + 30 * MS_PER_DAY
					).toISOString(),
					status: {label: 'approved'},
				},
				entryClassName: 'some.Class',
			},
			{actions: [], labels}
		);

		expect(result.labels).toEqual(labels);
	});

	it('appends an Expiring Soon label with tooltip attributes when approved item is within the threshold', () => {
		const labels = [{displayType: 'success', value: 'approved'}];

		const result = callTransform(
			{
				embedded: {
					expirationDate: new Date(
						NOW.getTime() + 3 * MS_PER_DAY
					).toISOString(),
					status: {label: 'approved'},
				},
				entryClassName: 'some.Class',
			},
			{actions: [], labels}
		);

		expect(result.labels).toHaveLength(2);
		expect(result.labels[0]).toEqual(labels[0]);

		const expiringSoon = result.labels[1];

		expect(expiringSoon.value).toBe('expiring-soon');
		expect(expiringSoon.displayType).toBe('warning');
		expect(expiringSoon.className).toBe('lfr-portal-tooltip');
		expect(expiringSoon.tabIndex).toBe(0);
		expect(expiringSoon.title).toBeTruthy();
		expect(expiringSoon['aria-label']).toBe('expiring-soon-expires-on-x');
	});

	it('does not append an Expiring Soon label when the formatted dates are not available', () => {
		const formatSpy = jest
			.spyOn(expirationStatus, 'formatExpirationDate')
			.mockReturnValueOnce(null);
		const formatLongSpy = jest
			.spyOn(expirationStatus, 'formatExpirationDateLong')
			.mockReturnValueOnce(null);

		const labels = [{displayType: 'success', value: 'approved'}];

		const result = callTransform(
			{
				embedded: {
					expirationDate: new Date(
						NOW.getTime() + 3 * MS_PER_DAY
					).toISOString(),
					status: {label: 'approved'},
				},
				entryClassName: 'some.Class',
			},
			{actions: [], labels}
		);

		expect(result.labels).toEqual(labels);

		formatSpy.mockRestore();
		formatLongSpy.mockRestore();
	});

	it('decorates the Expired label with tooltip attributes when item is expired and has expirationDate', () => {
		const past = new Date(NOW.getTime() - MS_PER_DAY).toISOString();
		const labels = [{displayType: 'danger', value: 'expired'}];

		const result = callTransform(
			{
				embedded: {
					expirationDate: past,
					status: {label: 'expired'},
				},
				entryClassName: 'some.Class',
			},
			{actions: [], labels}
		);

		expect(result.labels).toHaveLength(1);

		const expired = result.labels[0];

		expect(expired.value).toBe('expired');
		expect(expired.displayType).toBe('danger');
		expect(expired.className).toBe('lfr-portal-tooltip');
		expect(expired.tabIndex).toBe(0);
		expect(expired.title).toBeTruthy();
		expect(expired['aria-label']).toBe('expired-on-x');
	});

	it('leaves the Expired label untouched when no expirationDate is provided', () => {
		const labels = [{displayType: 'danger', value: 'expired'}];

		const result = callTransform(
			{
				embedded: {status: {label: 'expired'}},
				entryClassName: 'some.Class',
			},
			{actions: [], labels}
		);

		expect(result.labels).toEqual(labels);
	});

	it('leaves labels unchanged when the expired item has no label whose value matches "expired"', () => {
		const past = new Date(NOW.getTime() - MS_PER_DAY).toISOString();
		const labels = [{displayType: 'success', value: 'approved'}];

		const result = callTransform(
			{
				embedded: {
					expirationDate: past,
					status: {label: 'expired'},
				},
				entryClassName: 'some.Class',
			},
			{actions: [], labels}
		);

		expect(result.labels).toEqual(labels);
	});
});

describe('getFileMimeTypeObjectDefinitionStickerValue', () => {
	const OBJECT_DEFINITION_VALUES = {
		L_CMS_BASIC_WEB_CONTENT: 'web-content',
		L_CMS_STRUCTURED_CONTENT: 'structured-content',
		default: 'document-default',
	};

	const callSticker = (item: any) =>
		getFileMimeTypeObjectDefinitionStickerValue(
			undefined,
			OBJECT_DEFINITION_VALUES,
			item
		);

	it('reads the object definition from the embedded system properties', () => {
		expect(
			callSticker({
				embedded: {
					systemProperties: {
						objectDefinitionBrief: {
							externalReferenceCode: 'L_CMS_BASIC_WEB_CONTENT',
						},
					},
				},
			})
		).toBe('web-content');
	});

	it('reads the object definition from the item when it is not embedded', () => {
		expect(
			callSticker({
				objectDefinitionExternalReferenceCode:
					'L_CMS_BASIC_WEB_CONTENT',
			})
		).toBe('web-content');
	});

	it('prefers the embedded object definition over the one on the item', () => {
		expect(
			callSticker({
				embedded: {
					systemProperties: {
						objectDefinitionBrief: {
							externalReferenceCode: 'L_CMS_BASIC_WEB_CONTENT',
						},
					},
				},
				objectDefinitionExternalReferenceCode:
					'L_CMS_STRUCTURED_CONTENT',
			})
		).toBe('web-content');
	});

	it('returns an empty sticker when the item carries no object definition', () => {
		expect(callSticker({})).toBe('');
	});
});
