/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Rule} from '../Rule';
import {CollectionItemLayoutDataItem} from './CollectionItemLayoutDataItem';
import {CollectionLayoutDataItem} from './CollectionLayoutDataItem';
import {ColumnLayoutDataItem} from './ColumnLayoutDataItem';
import {ContainerLayoutDataItem} from './ContainerLayoutDataItem';
import {DropZoneLayoutDataItem} from './DropZoneLayoutDataItem';
import {FormLayoutDataItem} from './FormLayoutDataItem';
import {FormRelationshipLayoutDataItem} from './FormRelationshipLayoutDataItem';
import {FormStepContainerDataItem} from './FormStepContainerLayoutDataItem';
import {FormStepLayoutDataItem} from './FormStepLayoutDataItem';
import {FragmentDropZoneLayoutDataItem} from './FragmentDropZoneLayoutDataItem';
import {FragmentLayoutDataItem} from './FragmentLayoutDataItem';
import {RootLayoutDataItem} from './RootLayoutDataItem';
import {RowLayoutDataItem} from './RowLayoutDataItem';

export interface DeletedLayoutDataItem {
	childrenItemIds: string[];
	itemId: string;
	portletIds: string[];
	position: number;
}

export type LayoutDataItem =
	| CollectionItemLayoutDataItem
	| CollectionLayoutDataItem
	| ColumnLayoutDataItem
	| ContainerLayoutDataItem
	| DropZoneLayoutDataItem
	| FormLayoutDataItem
	| FormRelationshipLayoutDataItem
	| FormStepLayoutDataItem
	| FormStepContainerDataItem
	| FragmentDropZoneLayoutDataItem
	| FragmentLayoutDataItem
	| RootLayoutDataItem
	| RowLayoutDataItem;

export interface LayoutData {
	deletedItems: DeletedLayoutDataItem[];
	items: Record<string, LayoutDataItem>;
	pageRules: Rule[];
	rootItems: {dropZone: string; main: string};
	version: string;
}
