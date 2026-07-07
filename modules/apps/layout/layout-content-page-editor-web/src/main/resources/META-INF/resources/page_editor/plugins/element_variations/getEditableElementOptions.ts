/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LAYOUT_STRUCTURE_ITEM_CLASS_NAME_PREFIX} from '../../app/config/constants/layoutStructureItemClassNamePrefix';

export interface EditableElementOption {
	label: string;
	value: string;
}

export default function getEditableElementOptions(
	document: Document,
	itemNames: Record<string, string>
): EditableElementOption[] {
	const editableElementOptions: EditableElementOption[] = [];
	const values = new Set<string>();

	Object.entries(itemNames).forEach(([layoutStructureItemId, name]) => {
		document
			.querySelectorAll(
				`.${LAYOUT_STRUCTURE_ITEM_CLASS_NAME_PREFIX}${layoutStructureItemId} [data-lfr-editable-id]`
			)
			.forEach((editableElement) => {
				const editableId = editableElement.getAttribute(
					'data-lfr-editable-id'
				);

				if (!editableId) {
					return;
				}

				const value = `.${LAYOUT_STRUCTURE_ITEM_CLASS_NAME_PREFIX}${layoutStructureItemId} [data-lfr-editable-id="${editableId}"]`;

				if (values.has(value)) {
					return;
				}

				values.add(value);

				editableElementOptions.push({
					label: `${name} (${editableId})`,
					value,
				});
			});
	});

	return editableElementOptions;
}
