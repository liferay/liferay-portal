/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LAYOUT_DATA_ITEM_TYPES} from './layoutDataItemTypes';

export const LAYOUT_DATA_ITEM_TYPE_LABELS = {
	[LAYOUT_DATA_ITEM_TYPES.collection]:
		Liferay.Language.get('collection-display'),
	[LAYOUT_DATA_ITEM_TYPES.collectionItem]:
		Liferay.Language.get('collection-item'),
	[LAYOUT_DATA_ITEM_TYPES.column]: Liferay.Language.get('module'),
	[LAYOUT_DATA_ITEM_TYPES.container]: Liferay.Language.get('container'),
	[LAYOUT_DATA_ITEM_TYPES.dropZone]: Liferay.Language.get('drop-zone'),
	[LAYOUT_DATA_ITEM_TYPES.form]: Liferay.Language.get('form-container'),
	[LAYOUT_DATA_ITEM_TYPES.formRelationship]:
		Liferay.Language.get('form-relationship'),
	[LAYOUT_DATA_ITEM_TYPES.formStep]: Liferay.Language.get('step-x'),
	[LAYOUT_DATA_ITEM_TYPES.formStepContainer]:
		Liferay.Language.get('form-steps'),
	[LAYOUT_DATA_ITEM_TYPES.fragment]: Liferay.Language.get('fragment'),
	[LAYOUT_DATA_ITEM_TYPES.fragmentDropZone]:
		Liferay.Language.get('fragment-dropzone'),
	[LAYOUT_DATA_ITEM_TYPES.root]: Liferay.Language.get('root'),
	[LAYOUT_DATA_ITEM_TYPES.row]: Liferay.Language.get('grid'),
} as const;
