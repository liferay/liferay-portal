/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import SelectionFilter from '../../../../../src/main/resources/META-INF/resources/management_bar/controls/filters/implementation/SelectionFilter';
import {EEntityFieldType} from '../../../../../src/main/resources/META-INF/resources/management_bar/controls/filters/utils/types';

const {getOdataString} = SelectionFilter;

describe('SelectionFilter.getOdataString', () => {
	it('returns an empty string if no items are selected', () => {
		const result = getOdataString({
			entityFieldType: EEntityFieldType.STRING,
			id: 'testField',
			multiple: false,
			selectedData: {
				exclude: false,
				selectedItems: [],
			},
		} as any);

		expect(result).toBe('');
	});

	describe('with entityFieldType="string"', () => {
		it('generates a "eq" filter with quotes for a single item', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.STRING,
				id: 'testField',
				multiple: false,
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'Item 1', value: '123'}],
				},
			} as any);

			expect(result).toBe("testField eq '123'");
		});

		it('generates a "eq" filter with quotes for a single item with a number value', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.STRING,
				id: 'testField',
				multiple: false,
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'Item 1', value: 123}],
				},
			} as any);

			expect(result).toBe("testField eq '123'");
		});

		it('generates a "ne" filter with quotes when excluded', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.STRING,
				id: 'testField',
				multiple: false,
				selectedData: {
					exclude: true,
					selectedItems: [{label: 'Item 1', value: '123'}],
				},
			} as any);

			expect(result).toBe("testField ne '123'");
		});

		it('generates an "in" filter with quotes for multiple items', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.STRING,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: false,
					selectedItems: [
						{label: 'Item 1', value: '123'},
						{label: 'Item 2', value: '456'},
					],
				},
			} as any);

			expect(result).toBe("testField in ('123', '456')");
		});

		it('generates an "in" filter with quotes for multiple items with a number value', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.STRING,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: false,
					selectedItems: [
						{label: 'Item 1', value: 123},
						{label: 'Item 2', value: 456},
					],
				},
			} as any);

			expect(result).toBe("testField in ('123', '456')");
		});

		it('generates an "in" filter with quotes if multiple items are selected even if multiple is false', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.STRING,
				id: 'testField',
				multiple: false,
				selectedData: {
					exclude: false,
					selectedItems: [
						{label: 'Item 1', value: '123'},
						{label: 'Item 2', value: '456'},
					],
				},
			} as any);

			expect(result).toBe("testField in ('123', '456')");
		});

		it('generates an "in" filter with quotes if multiple items are selected even if multiple is not defined', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.STRING,
				id: 'testField',
				selectedData: {
					exclude: false,
					selectedItems: [
						{label: 'Item 1', value: '123'},
						{label: 'Item 2', value: '456'},
					],
				},
			} as any);

			expect(result).toBe("testField in ('123', '456')");
		});

		it('generates a "not in" filter with quotes for multiple items when excluded', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.STRING,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: true,
					selectedItems: [
						{label: 'Item 1', value: '123'},
						{label: 'Item 2', value: '456'},
					],
				},
			} as any);

			expect(result).toBe("not (testField in ('123', '456'))");
		});
	});

	describe('with entityFieldType="integer"', () => {
		it('generates an "in" filter without quotes for a single item', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.INTEGER,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'Item 1', value: 123}],
				},
			} as any);

			expect(result).toBe('testField in (123)');
		});

		it('generates an "in" filter without quotes for a single item with a string value', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.INTEGER,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'Item 1', value: '123'}],
				},
			} as any);

			expect(result).toBe('testField in (123)');
		});

		it('generates an "in" filter without quotes for multiple items', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.INTEGER,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: false,
					selectedItems: [
						{label: 'Item 1', value: 123},
						{label: 'Item 2', value: 456},
					],
				},
			} as any);

			expect(result).toBe('testField in (123, 456)');
		});

		it('generates an "in" filter without quotes for multiple items with a string value', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.INTEGER,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: false,
					selectedItems: [
						{label: 'Item 1', value: '123'},
						{label: 'Item 2', value: '456'},
					],
				},
			} as any);

			expect(result).toBe('testField in (123, 456)');
		});

		it('generates an "in" filter with quotes if multiple items are selected even if multiple is false', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.INTEGER,
				id: 'testField',
				multiple: false,
				selectedData: {
					exclude: false,
					selectedItems: [
						{label: 'Item 1', value: '123'},
						{label: 'Item 2', value: '456'},
					],
				},
			} as any);

			expect(result).toBe('testField in (123, 456)');
		});

		it('generates a "not in" filter for multiple items when excluded', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.INTEGER,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: true,
					selectedItems: [
						{label: 'Item 1', value: '123'},
						{label: 'Item 2', value: '456'},
					],
				},
			} as any);

			expect(result).toBe('not (testField in (123, 456))');
		});
	});

	describe('with entityFieldType="collection"', () => {
		it('generates an "any" filter with "eq" for a single item', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'Item 1', value: '123'}],
				},
			} as any);

			expect(result).toBe("testField/any(x:(x eq '123'))");
		});

		it('generates an "any" filter with "eq" without quotes for a single item with a number value', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'Item 1', value: 123}],
				},
			} as any);

			expect(result).toBe('testField/any(x:(x eq 123))');
		});

		it('generates an "any" filter with "or" for multiple items', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: false,
					selectedItems: [
						{label: 'Item 1', value: '123'},
						{label: 'Item 2', value: '456'},
					],
				},
			} as any);

			expect(result).toBe(
				"testField/any(x:(x eq '123') or (x eq '456'))"
			);
		});

		it('generates an "any" filter with "ne" for a single excluded item', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: true,
					selectedItems: [{label: 'Item 1', value: '123'}],
				},
			} as any);

			expect(result).toBe("testField/any(x:(x ne '123'))");
		});

		it('generates an "any" filter with "and" for multiple excluded items', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: true,
					selectedItems: [
						{label: 'Item 1', value: '123'},
						{label: 'Item 2', value: '456'},
					],
				},
			} as any);

			expect(result).toBe(
				"testField/any(x:(x ne '123') and (x ne '456'))"
			);
		});
	});
});
