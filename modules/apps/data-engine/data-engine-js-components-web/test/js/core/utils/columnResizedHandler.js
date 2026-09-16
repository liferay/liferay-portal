/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	handleResizeLeft,
	handleResizeRight,
} from '../../../../src/main/resources/META-INF/resources/js/core/utils/columnResizedHandler';

const props = {};

describe('core/utils/columnResizedHandler', () => {
	describe('handleResizeLeft(props, state, index, columnTarget)', () => {
		it('adds an empty column before the first field', () => {
			const indexes = [
				{
					columnIndex: 0,
					pageIndex: 0,
					rowIndex: 0,
				},
			];
			const state = {
				pages: [
					{
						rows: [
							{
								columns: [
									{
										fields: [{}],
										size: 6,
									},
								],
							},
						],
					},
				],
			};

			const result = handleResizeLeft(props, state, indexes, 2);

			expect(result[0].rows[0].columns).toHaveLength(2);
			expect(result[0].rows[0].columns[0].size).toEqual(2);
			expect(result[0].rows[0].columns[1].size).toEqual(4);
		});

		it('does not shrink the first field to nothing', () => {
			const indexes = [
				{
					columnIndex: 0,
					pageIndex: 0,
					rowIndex: 0,
				},
			];
			const state = {
				pages: [
					{
						rows: [
							{
								columns: [
									{
										fields: [{}],
										size: 6,
									},
								],
							},
						],
					},
				],
			};

			const result = handleResizeLeft(props, state, indexes, 6);

			expect(result[0].rows[0].columns).toHaveLength(1);
			expect(result[0].rows[0].columns[0].size).toEqual(6);
		});
	});

	describe('handleResizeRight(props, state, index, columnTarget)', () => {
		it('resizes first field to the left', () => {
			const indexes = [
				{
					columnIndex: 0,
					pageIndex: 0,
					rowIndex: 0,
				},
			];
			const state = {
				pages: [
					{
						rows: [
							{
								columns: [
									{
										fields: [{}],
										size: 4,
									},
									{
										fields: [{}],
										size: 8,
									},
								],
							},
						],
					},
				],
			};

			const result = handleResizeRight(props, state, indexes, 2);

			expect(result[0].rows[0].columns[0].size).toEqual(2);
			expect(result[0].rows[0].columns[1].size).toEqual(10);
		});

		it('resizes first field to the right', () => {
			const indexes = [
				{
					columnIndex: 0,
					pageIndex: 0,
					rowIndex: 0,
				},
			];
			const state = {
				pages: [
					{
						rows: [
							{
								columns: [
									{
										fields: [{}],
										size: 4,
									},
									{
										fields: [{}],
										size: 8,
									},
								],
							},
						],
					},
				],
			};

			const result = handleResizeRight(props, state, indexes, 6);

			expect(result[0].rows[0].columns[0].size).toEqual(6);
			expect(result[0].rows[0].columns[1].size).toEqual(6);
		});

		it('resizes second field to the left', () => {
			const indexes = [
				{
					columnIndex: 1,
					pageIndex: 0,
					rowIndex: 0,
				},
			];
			const state = {
				pages: [
					{
						rows: [
							{
								columns: [
									{
										fields: [{}],
										size: 4,
									},
									{
										fields: [{}],
										size: 8,
									},
								],
							},
						],
					},
				],
			};

			const result = handleResizeRight(props, state, indexes, 6);

			expect(result[0].rows[0].columns[0].size).toEqual(4);
			expect(result[0].rows[0].columns[1].size).toEqual(2);
			expect(result[0].rows[0].columns.length).toEqual(3);
		});

		it('resizes second field to the right', () => {
			const indexes = [
				{
					columnIndex: 1,
					pageIndex: 0,
					rowIndex: 0,
				},
			];
			const state = {
				pages: [
					{
						rows: [
							{
								columns: [
									{
										fields: [{}],
										size: 4,
									},
									{
										fields: [{}],
										size: 4,
									},
									{
										fields: [{}],
										size: 4,
									},
								],
							},
						],
					},
				],
			};

			const result = handleResizeRight(props, state, indexes, 11);

			expect(result[0].rows[0].columns[0].size).toEqual(4);
			expect(result[0].rows[0].columns[1].size).toEqual(7);
			expect(result[0].rows[0].columns[2].size).toEqual(1);
		});
	});
});
