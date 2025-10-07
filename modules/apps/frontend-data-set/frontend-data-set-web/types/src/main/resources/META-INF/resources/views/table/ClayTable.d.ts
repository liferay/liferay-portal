/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/// <reference types="react" />

import {IItemsActions} from '../..';
declare type Field = {
	contentRenderer: string;
	contentRendererClientExtension: boolean;
	fieldName: any;
	label: string;
	localizeLabel: boolean;
	mapData: Function;
	sortable: boolean;
};
declare type Props = {
	fields: Array<Field>;
	inlineAddingSettings?: {
		apiURL?: string;
		defaultBodyContent?: Record<string, any>;
	};
	itemInlineChanges?: Array<any>;
	items: Array<any>;
	itemsActions: Array<IItemsActions>;
	nestedItemsReferenceKey?: string;
	selectItems: Function;
	selectable?: boolean;
	selectedItemsKey: string;
	selectedItemsValue: any;
	selectionType?: 'single' | 'multiple';
};
export declare function ClayTable({
	fields,
	inlineAddingSettings,
	itemInlineChanges,
	items,
	itemsActions,
	nestedItemsReferenceKey,
	selectItems,
	selectable,
	selectedItemsKey,
	selectedItemsValue,
	selectionType,
}: Props): JSX.Element;
export {};
