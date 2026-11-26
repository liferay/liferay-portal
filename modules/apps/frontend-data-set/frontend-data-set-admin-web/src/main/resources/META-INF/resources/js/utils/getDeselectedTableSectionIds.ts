/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IDataSetTableSection, IField, IFieldTreeItem} from './types';

function collectFieldNames(
	fieldTreeItems: Array<IFieldTreeItem>,
	fieldNames: Set<string>
) {
	fieldTreeItems.forEach((fieldTreeItem) => {
		fieldNames.add(fieldTreeItem.name);

		if (fieldTreeItem.children) {
			collectFieldNames(fieldTreeItem.children, fieldNames);
		}
	});

	return fieldNames;
}

export default function getDeselectedTableSectionIds({
	fieldTreeItems,
	selectedFields,
	tableSections,
}: {
	fieldTreeItems: Array<IFieldTreeItem>;
	selectedFields: Array<IField>;
	tableSections: Array<IDataSetTableSection>;
}): Array<string> {
	const fieldNames = collectFieldNames(fieldTreeItems, new Set<string>());

	const selectedFieldNames = new Set(
		selectedFields.map((selectedField) => selectedField.name)
	);

	return tableSections
		.filter(
			(tableSection) =>
				fieldNames.has(tableSection.fieldName) &&
				!selectedFieldNames.has(tableSection.fieldName)
		)
		.map((tableSection) => String(tableSection.id));
}
