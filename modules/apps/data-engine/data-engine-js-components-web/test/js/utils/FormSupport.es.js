/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import * as FormSupport from '../../../src/main/resources/META-INF/resources/js/utils/FormSupport.es';
import createElement from '../__mock__/createElement.es';
import mockPageWithNested from '../__mock__/mockPageWithNested.es';
import mockPages from '../__mock__/mockPages.es';

let pages = null;

describe('FormSupport', () => {
	beforeEach(() => {
		pages = JSON.parse(JSON.stringify(mockPages));
	});

	afterEach(() => {
		pages = null;
	});

	it('add a new field to column to the pages', () => {
		const columnIndex = 1;
		const field = {
			spritemap: 'icons.svg',
			type: 'text',
		};
		const pageIndex = 0;
		const rowIndex = 0;

		expect(
			FormSupport.addFieldToColumn(
				pages,
				pageIndex,
				rowIndex,
				columnIndex,
				field
			)
		).toMatchSnapshot();
	});

	it('adds a new fields to column void', () => {
		const columnIndex = 2;
		const fields = [
			{
				spritemap: 'icons.svg',
				type: 'text',
			},
		];
		const pageIndex = 0;
		const rowIndex = 1;

		expect(
			FormSupport.setColumnFields(
				pages,
				pageIndex,
				rowIndex,
				columnIndex,
				fields
			)
		).toMatchSnapshot();
	});

	it('add a new row to the pages and reorder', () => {
		const indexToAddRow = 0;
		const newRow = FormSupport.implAddRow(12, [
			{
				type: 'newRow',
			},
		]);
		const pageIndex = 0;

		expect(
			FormSupport.addRow(pages, indexToAddRow, pageIndex, newRow)
		).toMatchSnapshot();
	});

	it('extracts the location of the field through the element', () => {
		const element = createElement({
			attributes: [
				{
					key: 'data-ddm-field-column',
					value: 0,
				},
				{
					key: 'data-ddm-field-row',
					value: 2,
				},
				{
					key: 'data-ddm-field-page',
					value: 2,
				},
			],
			tagname: 'div',
		});

		expect(FormSupport.getIndexes(element)).toEqual({
			columnIndex: 0,
			pageIndex: 2,
			rowIndex: 2,
		});
	});

	it('extracts the location of the row through the element', () => {
		const element = createElement({
			attributes: [
				{
					key: 'data-ddm-field-column',
					value: 0,
				},
				{
					key: 'data-ddm-field-row',
					value: 1,
				},
				{
					key: 'data-ddm-field-page',
					value: 2,
				},
			],
			tagname: 'div',
		});

		expect(FormSupport.getIndexes(element)).toEqual({
			columnIndex: 0,
			pageIndex: 2,
			rowIndex: 1,
		});
	});

	it('gets a column from pages', () => {
		const columnIndex = 1;
		const pageIndex = 0;
		const rowIndex = 1;

		expect(
			FormSupport.getColumn(pages, pageIndex, rowIndex, columnIndex)
		).toMatchSnapshot();
	});

	it('gets a row from pages', () => {
		const pageIndex = 0;
		const rowIndex = 1;

		expect(
			FormSupport.getRow(pages, pageIndex, rowIndex)
		).toMatchSnapshot();
	});

	it('gets a specific field through the pages', () => {
		const indexColumn = 0;
		const indexPage = 0;
		const indexRow = 0;

		expect(
			FormSupport.getField(pages, indexPage, indexRow, indexColumn)
		).toMatchSnapshot();
	});

	it('removes a column from pages and reorder', () => {
		const columnIndex = 1;
		const pageIndex = 0;
		const rowIndex = 1;

		expect(
			FormSupport.removeColumn(pages, pageIndex, rowIndex, columnIndex)
		).toMatchSnapshot();
	});

	it('removes a fields to column from pages', () => {
		const columnIndex = 1;
		const pageIndex = 0;
		const rowIndex = 1;

		expect(
			FormSupport.removeFields(pages, pageIndex, rowIndex, columnIndex)
		).toMatchSnapshot();
	});

	it('removes a row from pages and reorder', () => {
		const pageIndex = 0;
		const rowIndex = 1;

		expect(
			FormSupport.removeRow(pages, pageIndex, rowIndex)
		).toMatchSnapshot();
	});

	it('removes an empty column from nested fieldsets', () => {
		pages = JSON.parse(JSON.stringify(mockPageWithNested));

		const nestedFieldset =
			pages[0].rows[0].columns[0].fields[0].nestedFields[0];

		expect(nestedFieldset.rows).toEqual([
			expect.objectContaining({
				columns: [
					expect.objectContaining({
						fields: [],
					}),
				],
			}),
			expect.objectContaining({
				columns: [
					expect.objectContaining({
						fields: ['Text67163348'],
					}),
				],
			}),
		]);

		FormSupport.removeNestedEmptyRows(pages, 0);

		expect(nestedFieldset.rows).toEqual([
			expect.objectContaining({
				columns: [
					expect.objectContaining({
						fields: ['Text67163348'],
					}),
				],
			}),
		]);
	});

	it('returns an implementation of a row for the pages', () => {
		const row = [
			{
				spritemap: 'icons.svg',
				type: 'text',
			},
		];
		const size = 12;

		expect(FormSupport.implAddRow(size, row)).toEqual({
			columns: [
				{
					fields: [
						{
							spritemap: 'icons.svg',
							type: 'text',
						},
					],
					size: 12,
				},
			],
		});
	});

	it('returns false if there are fields in a row', () => {
		const pageIndex = 0;
		const rowIndex = 0;

		expect(
			FormSupport.rowHasFields(
				FormSupport.removeFields(pages, pageIndex, rowIndex, 0),
				pageIndex,
				rowIndex
			)
		).toBeFalsy();
	});

	it('returns true if there are fields in a row', () => {
		const pageIndex = 0;
		const rowIndex = 0;

		expect(
			FormSupport.rowHasFields(pages, pageIndex, rowIndex)
		).toBeTruthy();
	});

	it('updates a field', () => {
		const properties = {
			label: 'Foo',
			type: 'radio',
		};

		expect(
			FormSupport.updateField(pages, 'radioField', properties)
		).toMatchSnapshot();
	});
});
