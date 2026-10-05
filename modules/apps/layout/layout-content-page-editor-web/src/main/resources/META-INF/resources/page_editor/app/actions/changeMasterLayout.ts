/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CHANGE_MASTER_LAYOUT} from './types';

import type {LayoutData} from '../../types/layout_data/LayoutData';
import type {FragmentEntryLinkMap} from './addFragmentEntryLinks';

type MasterLayoutOptions =
	| {masterLayoutData: null; masterLayoutPageTemplateEntryERC: ''}
	| {masterLayoutData: LayoutData; masterLayoutPageTemplateEntryERC: string};

export default function changeMasterLayout({
	fragmentEntryLinks = {},
	masterLayoutData = null,
	masterLayoutPageTemplateEntryERC = '',
	masterLayoutPageTemplateEntryScopeERC = '',
}: MasterLayoutOptions & {
	fragmentEntryLinks: FragmentEntryLinkMap;
	masterLayoutPageTemplateEntryScopeERC?: string;
}) {
	return {
		fragmentEntryLinks,
		masterLayoutData,
		masterLayoutPageTemplateEntryERC,
		masterLayoutPageTemplateEntryScopeERC,
		type: CHANGE_MASTER_LAYOUT,
	} as const;
}
