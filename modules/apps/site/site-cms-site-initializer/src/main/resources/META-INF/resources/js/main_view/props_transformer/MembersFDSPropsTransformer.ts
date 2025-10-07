/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';

import manageMembersAction from './actions/manageMembersAction';
import UserGroupRenderer from './cell_renderers/UserGroupRenderer';
import UserRenderer from './cell_renderers/UserRenderer';
import addOnClickToCreationMenuItems from './utils/addOnClickToCreationMenuItems';

const ACTIONS = {
	addMembers: manageMembersAction,
};

export default function MembersFDSPropsTransformer({
	creationMenu,
	...otherProps
}: {
	creationMenu: any;
	otherProps: any;
}) {
	return {
		...otherProps,
		creationMenu: {
			...creationMenu,
			primaryItems: addOnClickToCreationMenuItems(
				creationMenu.primaryItems,
				ACTIONS
			),
		},
		customRenderers: {
			tableCell: [
				{
					component: UserGroupRenderer,
					name: 'userGroupTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: UserRenderer,
					name: 'userTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
	};
}
