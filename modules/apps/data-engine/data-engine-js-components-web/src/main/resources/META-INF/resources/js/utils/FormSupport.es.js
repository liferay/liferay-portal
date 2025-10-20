/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {PagesVisitor} from './visitors.es';

export function implAddColumn(size, fields = []) {
	return {
		fields,
		size,
	};
}

export function implAddRow(size, fields) {
	return {
		columns: [implAddColumn(size, fields)],
	};
}

export function addRow(
	pages,
	indexToAddRow,
	pageIndex,
	newRow = implAddRow(12, [])
) {
	const visitor = new PagesVisitor(pages);

	return visitor.mapPages((page, currentPageIndex) => {
		let newPage = page;

		if (pageIndex === currentPageIndex) {
			newPage = {
				...page,
				rows: [
					...page.rows.slice(0, indexToAddRow),
					newRow,
					...page.rows.slice(indexToAddRow),
				],
			};
		}

		return newPage;
	});
}

export function addColumn(
	pages,
	indexToAddColumn,
	pageIndex,
	rowIndex,
	newColumn = implAddColumn(11, [])
) {
	const visitor = new PagesVisitor(pages);

	return visitor.mapRows((row, currentRowIndex, currentPageIndex) => {
		let newRow = row;

		if (currentRowIndex === rowIndex && currentPageIndex === pageIndex) {
			newRow = {
				...row,
				columns: [
					...row.columns.slice(0, indexToAddColumn),
					newColumn,
					...row.columns.slice(indexToAddColumn),
				],
			};
		}

		return newRow;
	});
}

export function isEmptyColumn(pages, pageIndex, rowIndex, columnIndex) {
	return !pages[pageIndex].rows[rowIndex].columns[columnIndex].fields.length;
}

export function addFieldToColumn(
	pages,
	pageIndex,
	rowIndex,
	columnIndex,
	field
) {
	const numberOfRows = pages[pageIndex].rows.length;

	if (rowIndex >= numberOfRows) {
		const newRow = implAddRow(12, [field]);

		return addRow(pages, numberOfRows, pageIndex, newRow);
	}

	if (!isEmptyColumn(pages, pageIndex, rowIndex, columnIndex)) {
		pages = addRow(pages, rowIndex, pageIndex);
	}

	const visitor = new PagesVisitor(pages);

	return visitor.mapColumns(
		(column, currentColumnIndex, currentRowIndex, currentPageIndex) => {
			if (
				currentColumnIndex === columnIndex &&
				currentRowIndex === rowIndex &&
				currentPageIndex === pageIndex
			) {
				return {
					...column,
					fields: [...column.fields, field],
				};
			}

			return column;
		}
	);
}

/**
 * Cleans a single column by recursively cleaning its fields,
 * removing any fields that have no content.
 */
function cleanColumn(column) {
	const fields = Array.isArray(column.fields) ? column.fields : [];

	column.fields = fields.map(cleanField).filter(hasFieldContent);

	return column;
}

/**
 * Recursively cleans a field by processing its nested fields
 * and rows, removing empty ones. Returns the cleaned
 * field only if it still has content.
 */
function cleanField(field) {
	if (!field) {
		return field;
	}

	if (Array.isArray(field.nestedFields)) {
		field.nestedFields = field.nestedFields
			.map(cleanField)
			.filter(hasFieldContent);
	}

	if (Array.isArray(field.rows)) {
		field.rows = field.rows
			.map(cleanRow)
			.filter((row) => !!row.columns.length);
	}

	return field;
}

/**
 * Cleans a single page by removing rows that have no columns
 */
function cleanPage(page) {
	const rows = Array.isArray(page.rows) ? page.rows : [];

	page.rows = rows.map(cleanRow).filter((row) => !!row.columns.length);
}

/**
 * Cleans a single row by removing columns that no longer contain
 * any valid fields.
 */
function cleanRow(row) {
	const columns = Array.isArray(row.columns) ? row.columns : [];

	row.columns = columns
		.map(cleanColumn)
		.filter((column) => !!column.fields.length);

	return row;
}

export function getFieldIndexes(pages, fieldName) {
	let indexes = {};
	const visitor = new PagesVisitor(pages);

	visitor.mapFields((field, fieldIndex, columnIndex, rowIndex, pageIndex) => {
		if (
			(typeof field === 'string' && field === fieldName) ||
			(typeof field === 'object' && field.fieldName === fieldName)
		) {
			indexes = {
				columnIndex,
				fieldIndex,
				pageIndex,
				rowIndex,
			};
		}
	});

	return indexes;
}

/**
 * Determines whether a field still contains any content
 * after cleanup. If:
 * - It has nested fields or rows with content, or
 * - It is a simple field (no nested structures).
 */
function hasFieldContent(field) {
	if (!field) {
		return false;
	}

	const hasNestedFields =
		Array.isArray(field.nestedFields) && !!field.nestedFields.length;

	const hasRows = Array.isArray(field.rows) && !!field.rows.length;

	if (!hasNestedFields && !hasRows) {
		return true;
	}

	if (
		(hasNestedFields &&
			field.nestedFields.some((nestedField) =>
				hasFieldContent(nestedField)
			)) ||
		(hasRows &&
			field.rows.some((row) =>
				row.columns?.some((column) =>
					column.fields?.some((field) => hasFieldContent(field))
				)
			))
	) {
		return true;
	}

	return false;
}

export function isEmptyRow(pages, pageIndex, rowIndex) {
	return pages[pageIndex].rows[rowIndex].columns.every(
		(column, columnIndex) =>
			isEmptyColumn(pages, pageIndex, rowIndex, columnIndex)
	);
}

export function isEmptyPage(pages, pageIndex) {
	return pages[pageIndex].rows.every((row, rowIndex) =>
		isEmptyRow(pages, pageIndex, rowIndex)
	);
}

export function isEmpty(pages) {
	return pages.every((page, pageIndex) => isEmptyPage(pages, pageIndex));
}

export function setColumnFields(
	pages,
	pageIndex,
	rowIndex,
	columnIndex,
	fields = []
) {
	const numberOfRows = pages[Number(pageIndex)].rows.length;

	if (numberOfRows - 1 < rowIndex) {
		pages = addRow(pages, rowIndex, pageIndex);
		pages = addFieldToColumn(
			pages,
			pageIndex,
			rowIndex,
			columnIndex,
			fields
		);
	}
	else {
		pages[Number(pageIndex)].rows[Number(rowIndex)].columns[
			Number(columnIndex)
		].fields = fields;
	}

	return pages;
}

export function removeColumn(pages, pageIndex, rowIndex, columnIndex) {
	const visitor = new PagesVisitor(pages);

	return visitor.mapRows((row, currentRowIndex, currentPageIndex) => {
		let newRow = row;

		if (currentRowIndex === rowIndex && currentPageIndex === pageIndex) {
			newRow = {
				...row,
				columns: row.columns.filter((col, currentColumnIndex) => {
					return currentColumnIndex !== columnIndex;
				}),
			};
		}

		return newRow;
	});
}

export function removeFields(pages, pageIndex, rowIndex, columnIndex) {
	const visitor = new PagesVisitor(pages);

	return visitor.mapColumns(
		(column, currentColumnIndex, currentRowIndex, currentPageIndex) => {
			const newColumn = {...column};

			if (
				currentPageIndex === pageIndex &&
				currentRowIndex === rowIndex &&
				currentColumnIndex === columnIndex
			) {
				newColumn.fields = [];
			}

			return newColumn;
		}
	);
}

export function rowHasFields(pages, pageIndex, rowIndex) {
	let hasFields = false;
	const page = pages[Number(pageIndex)];

	if (page) {
		const row = page.rows[Number(rowIndex)];

		if (row && row.columns) {
			hasFields = row.columns.some((column) => column.fields.length);
		}
	}

	return hasFields;
}

export function removeEmptyRows(pages, pageIndex) {
	return pages[pageIndex].rows.reduce((result, next, index) => {
		if (rowHasFields(pages, pageIndex, index)) {
			result = [...result, next];
		}

		return result;
	}, []);
}

/**
 * Removes empty rows, columns, and nested field structures
 * from a deeply nested form pages array.
 * Returns the cleaned rows of the given page index.
 */
export function removeNestedEmptyRows(pages, pageIndex) {
	const page = pages[pageIndex];

	if (!page) {
		return [];
	}

	cleanPage(page);

	return page.rows;
}

export function removeRow(pages, pageIndex, rowIndex) {
	pages[Number(pageIndex)].rows.splice(Number(rowIndex), 1);

	return pages;
}

export function visitNestedFields({nestedFields}, fn) {
	if (Array.isArray(nestedFields)) {
		nestedFields.forEach((nestedField) => {
			fn(nestedField);

			visitNestedFields(nestedField, fn);
		});
	}
}

export function findField(pages, predicate) {
	const visitor = new PagesVisitor(pages);

	return visitor.findField(predicate);
}

export function findFieldByFieldName(pages, fieldName) {
	return findField(pages, (field) => field.fieldName === fieldName);
}

export function findFieldByName(pages, name) {
	return findField(pages, (field) => field.name === name);
}

export function getRow(pages, pageIndex, rowIndex) {
	const currentPage = pages[Number(pageIndex)];

	return currentPage.rows[Number(rowIndex)];
}

export function getColumn(pages, pageIndex, rowIndex, columnIndex) {
	const row = getRow(pages, pageIndex, rowIndex);

	return row.columns[Number(columnIndex)];
}

export function getColumnPosition(pages, pageIndex, rowIndex, columnIndex) {
	const currentPage = pages[pageIndex];

	let currentRow = null;

	currentRow = currentPage.rows[rowIndex];

	if (!currentPage) {
		console.error(
			`Row Index ${rowIndex} cannot be retrieved from ${currentPage}`
		);

		return;
	}

	return columnIndex !== -1 && currentRow.columns
		? currentRow.columns.reduce((result, _, index) => {
				if (index <= columnIndex) {
					const column = getColumn(pages, pageIndex, rowIndex, index);

					result += column.size;
				}

				return result;
			}, 0)
		: 0;
}

export function getField(context, pageIndex, rowIndex, columnIndex) {
	let field = getColumn(context, pageIndex, rowIndex, columnIndex).fields[0];

	if (context[pageIndex].nestedFields) {
		field = context[pageIndex].nestedFields.find(
			(nestedField) => nestedField.fieldName === field
		);
	}

	return field;
}

export function getIndexes(node) {
	const {ddmFieldColumn, ddmFieldPage, ddmFieldRow} = node.dataset;

	return {
		columnIndex: Number(ddmFieldColumn) || 0,
		pageIndex: Number(ddmFieldPage) || 0,
		rowIndex: Number(ddmFieldRow) || 0,
	};
}

export function getNestedIndexes(node) {
	let indexes = [];

	if (node.dataset.ddmFieldRow) {
		indexes = [getIndexes(node)];
	}

	if (!node.parentElement.classList.contains('ddm-form-page')) {
		indexes = [...getNestedIndexes(node.parentElement), ...indexes];
	}

	return indexes;
}

export function updateField(pages, fieldName, properties) {
	const visitor = new PagesVisitor(pages);

	return visitor.mapFields(
		(field) => {
			if (fieldName === field.fieldName) {
				return {
					...field,
					...properties,
				};
			}

			return field;
		},
		true,
		true
	);
}

export function updateColumn(
	pages,
	pageIndex,
	rowIndex,
	columnIndex,
	properties
) {
	const visitor = new PagesVisitor(pages);

	return visitor.mapColumns(
		(column, currentColumnIndex, currentRowIndex, currentPageIndex) => {
			let newColumn = column;

			if (
				currentColumnIndex === columnIndex &&
				currentRowIndex === rowIndex &&
				currentPageIndex === pageIndex
			) {
				newColumn = {
					...column,
					...properties,
				};
			}

			return newColumn;
		}
	);
}
