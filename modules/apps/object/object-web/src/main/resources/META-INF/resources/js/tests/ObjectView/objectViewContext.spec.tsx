/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	TAction,
	viewReducer,
} from '../../components/ObjectView/objectViewContext';

describe('viewReducer ADD_OBJECT_VIEW_COLUMN', () => {
	it('can add new object field', () => {
		const state = {
			objectView: {
				objectViewColumns: [
					{
						objectFieldName: 'status',
					},
					{
						objectFieldName: 'externalReferenceCode',
					},
				],
				objectViewFilterColumns: [],
				objectViewSortColumns: [],
			},
		} as any;

		const action = {
			payload: {
				creationLanguageId: 'en_US',
				selectedObjectFields: [
					{
						businessType: 'Text',
						label: {
							en_US: 'Status',
						},
						name: 'status',
					},
					{
						businessType: 'Text',
						label: {
							en_US: 'External Reference Code',
						},
						name: 'externalReferenceCode',
					},
					{
						businessType: 'Text',
						label: {
							en_US: 'Author',
						},
						name: 'creator',
					},
				],
			},
			type: 'ADD_OBJECT_VIEW_COLUMN',
		} as TAction;

		const result = viewReducer(state, action);

		expect(result.objectView.objectViewColumns.length).toBe(3);

		expect(result.objectView.objectViewColumns[0].objectFieldName).toBe(
			'status'
		);

		expect(result.objectView.objectViewColumns[1].objectFieldName).toBe(
			'externalReferenceCode'
		);

		expect(result.objectView.objectViewColumns[2].objectFieldName).toBe(
			'creator'
		);
	});

	it('can remove object field', () => {
		const state = {
			objectView: {
				objectViewColumns: [
					{
						objectFieldName: 'status',
					},
					{
						objectFieldName: 'externalReferenceCode',
					},
					{
						objectFieldName: 'creator',
					},
				],
				objectViewFilterColumns: [],
				objectViewSortColumns: [],
			},
		} as any;

		const action = {
			payload: {
				creationLanguageId: 'en_US',
				selectedObjectFields: [
					{
						businessType: 'Text',
						label: {
							en_US: 'Status',
						},
						name: 'status',
					},
					{
						businessType: 'Text',
						label: {
							en_US: 'Author',
						},
						name: 'creator',
					},
				],
			},
			type: 'ADD_OBJECT_VIEW_COLUMN',
		} as TAction;

		const result = viewReducer(state, action);

		expect(result.objectView.objectViewColumns.length).toBe(2);

		expect(result.objectView.objectViewColumns[0].objectFieldName).toBe(
			'status'
		);

		expect(result.objectView.objectViewColumns[1].objectFieldName).toBe(
			'creator'
		);
	});
});
