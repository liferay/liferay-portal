/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FragmentEntryLink} from '../actions/addFragmentEntryLinks';
import {PORTLET_DEFAULT_ACTIONS} from '../config/constants/portletDefaultActions';

export default function getPortletCustomActions(
	fragmentEntryLink: FragmentEntryLink
) {
	if (!fragmentEntryLink.actions) {
		return [];
	}

	const defaultActions: string[] = Object.values(PORTLET_DEFAULT_ACTIONS);

	const actions = Object.entries(fragmentEntryLink.actions);

	const customActions = actions
		.filter(([key]) => !defaultActions.includes(key))
		.map(([, value]) => value);

	return customActions;
}
