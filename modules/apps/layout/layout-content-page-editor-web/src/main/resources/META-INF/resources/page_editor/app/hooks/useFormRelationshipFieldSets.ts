/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FormLayoutDataItem} from '../../types/layout_data/FormLayoutDataItem';
import {FormRelationshipLayoutDataItem} from '../../types/layout_data/FormRelationshipLayoutDataItem';
import {LayoutData, LayoutDataItem} from '../../types/layout_data/LayoutData';
import {ObjectFieldSet, useObjectFields} from '../contexts/ObjectDataContext';
import {useSelector} from '../contexts/StoreContext';

function getParent(
	item: LayoutDataItem,
	layoutData: LayoutData
): FormLayoutDataItem | FormRelationshipLayoutDataItem {
	const parent = layoutData.items[item.parentId];

	if (parent.type === 'form' || parent.type === 'form-relationship') {
		return parent;
	}

	return getParent(parent, layoutData);
}

export default function useFormRelationshipFieldSets(
	item: FormRelationshipLayoutDataItem
) {
	const layoutData = useSelector((state) => state.layoutData);

	const parent = getParent(item, layoutData);

	const fields = useObjectFields(
		parent.type === 'form'
			? {
					classNameId: parent.config.classNameId,
					classTypeId: parent.config.classTypeId,
				}
			: {name: parent.config.contentType as string}
	);

	// Look in first fieldSet that is not Basic Information if parent is a form

	if (parent.type === 'form') {
		const mainFieldSet = fields.find(
			(item) =>
				'fields' in item &&
				item.name &&
				item.name !== 'basic-information'
		) as ObjectFieldSet;

		if (!mainFieldSet) {
			return [];
		}

		return mainFieldSet.fields.filter(
			(fieldSet) => 'relationship' in fieldSet && fieldSet.relationship
		) as ObjectFieldSet[];
	}

	// Take relationship fieldSets directly if parent is a form relationship

	return fields.filter(
		(fieldSet) => 'relationship' in fieldSet && fieldSet.relationship
	) as ObjectFieldSet[];
}
