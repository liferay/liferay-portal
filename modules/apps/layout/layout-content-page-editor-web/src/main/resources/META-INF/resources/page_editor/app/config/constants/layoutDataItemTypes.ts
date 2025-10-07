/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const LAYOUT_DATA_ITEM_TYPES = {
	collection: 'collection',
	collectionItem: 'collection-item',
	column: 'column',
	container: 'container',
	dropZone: 'drop-zone',
	form: 'form',
	formRelationship: 'form-relationship',
	formStep: 'form-step',
	formStepContainer: 'form-step-container',
	fragment: 'fragment',
	fragmentDropZone: 'fragment-drop-zone',
	root: 'root',
	row: 'row',
} as const;

export type LayoutDataItemType =
	(typeof LAYOUT_DATA_ITEM_TYPES)[keyof typeof LAYOUT_DATA_ITEM_TYPES];
