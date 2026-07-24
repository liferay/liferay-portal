/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	MembersFDSPropsTransformer,
	addOnClickToCreationMenuItems,
} from '@liferay/site-cms-site-initializer';

import manageMembersAction from './actions/manageMembersAction';

const ACTIONS = {
	addMembers: manageMembersAction,
};

export default function ProjectMembersFDSPropsTransformer({
	creationMenu,
	...otherProps
}: {
	creationMenu: any;
	otherProps: any;
}) {
	return {
		...MembersFDSPropsTransformer({creationMenu, ...otherProps}),
		creationMenu: {
			...creationMenu,
			primaryItems: addOnClickToCreationMenuItems(
				creationMenu.primaryItems,
				ACTIONS
			),
		},
	};
}
