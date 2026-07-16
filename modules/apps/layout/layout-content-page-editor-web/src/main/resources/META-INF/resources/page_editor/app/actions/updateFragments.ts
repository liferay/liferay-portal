/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LayoutDataItemType} from '../config/constants/layoutDataItemTypes';
import {FragmentComposition} from './addFragmentComposition';
import {UPDATE_FRAGMENTS} from './types';

import type {FragmentEntryType} from '../config/constants/fragmentEntryTypes';

export interface FragmentEntry {
	fragmentEntryKey: string;
	groupId?: string;
	highlighted: boolean;
	icon: string;
	imagePreviewURL: string;
	itemType: LayoutDataItemType;
	name: string;
	type: FragmentEntryType;
}

export type ScopeType = 'design-library' | 'global' | 'site';

export interface FragmentSetScope {
	id: string;
	label: string;
	type: ScopeType;
}

export interface FragmentSet {
	deprecated: boolean;
	fragmentCollectionId: string;
	fragmentEntries: Array<FragmentEntry | FragmentComposition>;
	name: string;
	scope?: FragmentSetScope;
}

export default function updateFragments({
	fragments,
}: {
	fragments: FragmentSet[];
}) {
	return {
		fragments,
		type: UPDATE_FRAGMENTS,
	} as const;
}
