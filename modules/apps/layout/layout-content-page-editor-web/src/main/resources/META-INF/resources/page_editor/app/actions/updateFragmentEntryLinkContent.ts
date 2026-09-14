/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FragmentEntryLink} from './addFragmentEntryLinks';
import {UPDATE_FRAGMENT_ENTRY_LINK_CONTENT} from './types';

export default function updateFragmentEntryLinkContent({
	collectionItemId,
	content,
	editableTypes,
	fragmentEntryLinkId,
}: {
	collectionItemId?: string;
	content: string;
	editableTypes?: FragmentEntryLink['editableTypes'];
	fragmentEntryLinkId: string;
}) {
	return {
		collectionItemId,
		content,
		editableTypes,
		fragmentEntryLinkId,
		type: UPDATE_FRAGMENT_ENTRY_LINK_CONTENT,
	} as const;
}
