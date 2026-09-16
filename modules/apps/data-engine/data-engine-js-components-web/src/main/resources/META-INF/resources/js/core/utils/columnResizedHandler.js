/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import * as FormSupport from '../../utils/FormSupport.es';
import {updateField} from '../../utils/settingsContext';

export function getColumn(pages, nestedIndexes = []) {
	let column;
	let context = pages;

	nestedIndexes.forEach((indexes) => {
		const {columnIndex, pageIndex, rowIndex} = indexes;

		let index = pageIndex;

		if (pages.length - 1 < pageIndex) {
			index = 0;
		}

		column = FormSupport.getColumn(
			context,
			column ? 0 : index,
			rowIndex,
			columnIndex
		);

		if (column && context[0].nestedFields) {
			context = context[0].nestedFields.filter((nestedField) =>
				column.fields.includes(nestedField.fieldName)
			);
		}
		else {
			context = column && column.fields;
		}
	});

	return column;
}

const getContext = (context, nestedIndexes = []) => {
	if (nestedIndexes.length) {
		nestedIndexes.forEach((indexes, i) => {
			const {columnIndex, pageIndex, rowIndex} = indexes;

			let fields =
				context[i > 0 ? 0 : pageIndex].rows[rowIndex].columns[
					columnIndex
				].fields;

			if (context[0].nestedFields) {
				fields = fields.map((field) =>
					context[0].nestedFields.find(
						(nestedField) => nestedField.fieldName === field
					)
				);
			}

			context = fields;
		});
	}

	return context;
};

const getColumnPosition = (context, indexes) => {
	const {columnIndex, pageIndex, rowIndex} = indexes;

	return FormSupport.getColumnPosition(
		context,
		pageIndex,
		rowIndex,
		columnIndex
	);
};

export function handleResizeRight(props, state, indexes, columnTarget) {
	const {pages} = state;

	const {columnIndex, pageIndex, rowIndex} = indexes[indexes.length - 1];

	const currentContext = getContext(pages, indexes.slice(0, -1));

	const currentColumn = getColumn(currentContext, [
		indexes[indexes.length - 1],
	]);

	const nextColumn = getColumn(currentContext, [
		{
			columnIndex: columnIndex + 1,
			pageIndex,
			rowIndex,
		},
	]);

	const currentColumnPosition = getColumnPosition(currentContext, {
		columnIndex,
		pageIndex: indexes.length > 1 ? 0 : pageIndex,
		rowIndex,
	});

	let newContext = currentContext;
	let newCurrentColumn;
	let newNextColumn;

	if (
		!nextColumn &&
		currentColumn.size > currentColumnPosition - columnTarget &&
		columnTarget < currentColumnPosition
	) {
		const newSize = currentColumnPosition - columnTarget;

		newCurrentColumn = {
			...currentColumn,
			size: currentColumn.size - newSize,
		};

		newContext = FormSupport.addColumn(
			newContext,
			columnIndex + 1,
			indexes.length > 1 ? 0 : pageIndex,
			rowIndex,
			{
				fields: [],
				size: newSize,
			}
		);
	}
	else if (nextColumn) {
		const newSize = Math.abs(currentColumnPosition - columnTarget);

		if (
			columnTarget < currentColumnPosition &&
			currentColumn.size > newSize
		) {
			newCurrentColumn = {
				...currentColumn,
				size: Math.max(currentColumn.size - newSize, 1),
			};

			newNextColumn = {
				...nextColumn,
				size: Math.min(nextColumn.size + newSize, 12),
			};
		}
		else if (columnTarget > currentColumnPosition) {
			if (nextColumn.size === 1 && !nextColumn.fields.length) {
				newCurrentColumn = {
					...currentColumn,
					size: currentColumn.size + newSize,
				};

				newContext = FormSupport.removeColumn(
					newContext,
					indexes.length > 1 ? 0 : pageIndex,
					rowIndex,
					columnIndex + 1
				);
			}
			else if (nextColumn.size > newSize) {
				newCurrentColumn = {
					...currentColumn,
					size: currentColumn.size + newSize,
				};

				newNextColumn = {
					...nextColumn,
					size: nextColumn.size - newSize,
				};
			}
		}
	}

	if (newCurrentColumn) {
		newContext = FormSupport.updateColumn(
			newContext,
			indexes.length > 1 ? 0 : pageIndex,
			rowIndex,
			columnIndex,
			newCurrentColumn
		);
	}

	if (newNextColumn) {
		newContext = FormSupport.updateColumn(
			newContext,
			indexes.length > 1 ? 0 : pageIndex,
			rowIndex,
			columnIndex + 1,
			newNextColumn
		);
	}

	if (indexes.length > 1) {
		newContext = [
			updateField(props, newContext[0], 'rows', newContext[0].rows),
		];

		currentContext[0].settingsContext = newContext[0].settingsContext;
	}

	currentContext[indexes.length > 1 ? 0 : pageIndex].rows =
		newContext[indexes.length > 1 ? 0 : pageIndex].rows;

	return pages;
}

export function handleResizeLeft(props, state, indexes, columnTarget) {
	const {pages} = state;

	const {columnIndex, pageIndex, rowIndex} = indexes[indexes.length - 1];

	const currentContext = getContext(pages, indexes.slice(0, -1));

	const currentColumn = getColumn(currentContext, [
		indexes[indexes.length - 1],
	]);

	const previousColumn = getColumn(currentContext, [
		{
			columnIndex: columnIndex - 1,
			pageIndex,
			rowIndex,
		},
	]);

	const previousColumnPosition = getColumnPosition(currentContext, {
		columnIndex: columnIndex - 1,
		pageIndex: indexes.length > 1 ? 0 : pageIndex,
		rowIndex,
	});

	let newContext = currentContext;

	if (
		columnIndex === 0 &&
		columnTarget > 0 &&
		columnTarget < currentColumn.size
	) {
		newContext = FormSupport.addColumn(
			newContext,
			columnIndex,
			indexes.length > 1 ? 0 : pageIndex,
			rowIndex,
			{
				fields: [],
				size: columnTarget,
			}
		);

		newContext = FormSupport.updateColumn(
			newContext,
			indexes.length > 1 ? 0 : pageIndex,
			rowIndex,
			columnIndex + 1,
			{
				...currentColumn,
				size: currentColumn.size - columnTarget,
			}
		);
	}
	else if (
		previousColumn &&
		previousColumn.size === 1 &&
		!previousColumn.fields.length &&
		columnTarget <= previousColumnPosition
	) {
		newContext = FormSupport.removeColumn(
			newContext,
			indexes.length > 1 ? 0 : pageIndex,
			rowIndex,
			columnIndex - 1
		);

		newContext = FormSupport.updateColumn(
			newContext,
			indexes.length > 1 ? 0 : pageIndex,
			rowIndex,
			columnIndex - 1,
			{
				...currentColumn,
				size: currentColumn.size + 1,
			}
		);
	}
	else if (previousColumn) {
		const newSize = Math.abs(previousColumnPosition - columnTarget);

		if (
			previousColumnPosition >= columnTarget &&
			previousColumn.size > newSize
		) {
			currentColumn.size += newSize;
			previousColumn.size -= newSize;
		}
		else if (
			previousColumnPosition < columnTarget &&
			currentColumn.size > newSize
		) {
			currentColumn.size -= newSize;
			previousColumn.size += newSize;
		}

		newContext = FormSupport.updateColumn(
			newContext,
			indexes.length > 1 ? 0 : pageIndex,
			rowIndex,
			columnIndex,
			currentColumn
		);

		newContext = FormSupport.updateColumn(
			newContext,
			indexes.length > 1 ? 0 : pageIndex,
			rowIndex,
			columnIndex - 1,
			previousColumn
		);
	}

	if (indexes.length > 1) {
		newContext = [
			updateField(props, newContext[0], 'rows', newContext[0].rows),
		];

		currentContext[0].settingsContext = newContext[0].settingsContext;
	}

	currentContext[indexes.length > 1 ? 0 : pageIndex].rows =
		newContext[indexes.length > 1 ? 0 : pageIndex].rows;

	return pages;
}

export default function columnResizedHandler({
	column,
	direction,
	loc,
	props,
	state,
}) {
	const {pages} = state;

	let newPages = [...pages];

	const currentColumn = getColumn(pages, loc);

	if (currentColumn) {
		if (direction === 'left') {
			newPages = handleResizeLeft(props, state, loc, column);
		}
		else {
			newPages = handleResizeRight(props, state, loc, column + 1);
		}
	}

	return {
		pages: newPages,
	};
}
