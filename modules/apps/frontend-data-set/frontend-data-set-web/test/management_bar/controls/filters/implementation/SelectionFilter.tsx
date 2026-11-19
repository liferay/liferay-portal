/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import SelectionFilter from '../../../../../src/main/resources/META-INF/resources/management_bar/controls/filters/implementation/SelectionFilter';
import {EEntityFieldType} from '../../../../../src/main/resources/META-INF/resources/management_bar/controls/filters/utils/types';

const {Component, getOdataString} = SelectionFilter;

describe('SelectionFilter', () => {
	beforeEach(() => {
		(window as any).IntersectionObserver = class {
			disconnect() {}

			observe() {}

			unobserve() {}
		};
	});

	afterEach(() => {
		delete (window as any).IntersectionObserver;
	});

	it('sends the search term encoded once', async () => {
		fetch.mockResponse(
			JSON.stringify({
				items: [{label: 'United States', value: 'United States'}],
				totalCount: 20,
			})
		);

		render(
			<Component
				active={false}
				apiURL="/o/values"
				autocompleteEnabled
				entityFieldType={EEntityFieldType.STRING}
				id="country"
				inputPlaceholder="search"
				itemKey="value"
				itemLabel="label"
				items={[]}
				multiple
				selectedData={{exclude: false, selectedItems: []}}
				setFilter={jest.fn()}
			/>
		);

		await userEvent.type(
			screen.getByPlaceholderText('search'),
			'united states'
		);

		await waitFor(() =>
			expect(fetch).toHaveBeenLastCalledWith(
				'/o/values?page=1&pageSize=10&search=united+states',
				expect.anything()
			)
		);
	});
});

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
		it('generates an "any" filter with "in" for a single item', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'Item 1', value: '123'}],
				},
			} as any);

			expect(result).toBe("testField/any(x:x in ('123'))");
		});

		it('generates an "any" filter with "in" with quotes for a single item with a number value', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'Item 1', value: 123}],
				},
			} as any);

			expect(result).toBe("testField/any(x:x in ('123'))");
		});

		it('generates an "any" filter with "in" for multiple items', () => {
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

			expect(result).toBe("testField/any(x:x in ('123', '456'))");
		});

		it('generates an "any" filter with "not" for a single excluded item', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: true,
					selectedItems: [{label: 'Item 1', value: '123'}],
				},
			} as any);

			expect(result).toBe("not (testField/any(x:x in ('123')))");
		});

		it('generates an "any" filter with "not" for multiple excluded items', () => {
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

			expect(result).toBe("not (testField/any(x:x in ('123', '456')))");
		});
	});

	describe('with entityFieldType="collection-string"', () => {
		it('generates an "any" filter with "in" and quotes for a single item', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION_STRING,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'Item 1', value: '123'}],
				},
			} as any);

			expect(result).toBe("testField/any(x:x in ('123'))");
		});

		it('generates an "any" filter with "in" and quotes for a single item with a number value', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION_STRING,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'Item 1', value: 123}],
				},
			} as any);

			expect(result).toBe("testField/any(x:x in ('123'))");
		});

		it('generates an "any" filter with "in" for multiple items', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION_STRING,
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

			expect(result).toBe("testField/any(x:x in ('123', '456'))");
		});

		it('generates an "any" filter with "not" for a single excluded item', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION_STRING,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: true,
					selectedItems: [{label: 'Item 1', value: '123'}],
				},
			} as any);

			expect(result).toBe("not (testField/any(x:x in ('123')))");
		});

		it('generates an "any" filter with "not" for multiple excluded items', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION_STRING,
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

			expect(result).toBe("not (testField/any(x:x in ('123', '456')))");
		});
	});

	describe('with entityFieldType="collection-integer"', () => {
		it('generates an "any" filter without quotes for a single item', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION_INTEGER,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'Item 1', value: 123}],
				},
			} as any);

			expect(result).toBe('testField/any(x:x in (123))');
		});

		it('generates an "any" filter without quotes for a single item with a string value', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION_INTEGER,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'Item 1', value: '123'}],
				},
			} as any);

			expect(result).toBe('testField/any(x:x in (123))');
		});

		it('generates an "any" filter with "in" for multiple items', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION_INTEGER,
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

			expect(result).toBe('testField/any(x:x in (123, 456))');
		});

		it('generates an "any" filter with "not" for a single excluded item', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION_INTEGER,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: true,
					selectedItems: [{label: 'Item 1', value: 123}],
				},
			} as any);

			expect(result).toBe('not (testField/any(x:x in (123)))');
		});

		it('generates an "any" filter with "not" for multiple excluded items', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.COLLECTION_INTEGER,
				id: 'testField',
				multiple: true,
				selectedData: {
					exclude: true,
					selectedItems: [
						{label: 'Item 1', value: 123},
						{label: 'Item 2', value: 456},
					],
				},
			} as any);

			expect(result).toBe('not (testField/any(x:x in (123, 456)))');
		});
	});

	describe('without entityFieldType', () => {
		it('generates a "eq" filter with quotes for a single item', () => {
			const result = getOdataString({
				id: 'testField',
				multiple: false,
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'Item 1', value: '123'}],
				},
			} as any);

			expect(result).toBe("testField eq '123'");
		});

		it('generates a "eq" filter without quotes for a single item with a number value', () => {
			const result = getOdataString({
				id: 'testField',
				multiple: false,
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'Item 1', value: 123}],
				},
			} as any);

			expect(result).toBe('testField eq 123');
		});

		it('escapes single quotes in a string value', () => {
			const result = getOdataString({
				id: 'testField',
				multiple: false,
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'Item 1', value: "O'Connor"}],
				},
			} as any);

			expect(result).toBe("testField eq 'O''Connor'");
		});
	});
});
