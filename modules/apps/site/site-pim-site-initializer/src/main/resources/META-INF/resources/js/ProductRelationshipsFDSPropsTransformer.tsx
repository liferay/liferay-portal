/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addOnClickToCreationMenuItems} from '@liferay/site-cms-site-initializer';

import openProductRelationshipSelectorModal from './openProductRelationshipSelectorModal';

const ACTIONS = {
	createProductRelationship: openProductRelationshipSelectorModal,
};

export default function propsTransformer({
	creationMenu,
	itemsActions,
	...props
}: {
	creationMenu?: any;
	itemsActions?: any[];
	[key: string]: any;
}) {
	return {
		...props,
		creationMenu: creationMenu && {
			...creationMenu,
			primaryItems: addOnClickToCreationMenuItems(
				creationMenu.primaryItems,
				ACTIONS
			),
		},
		itemsActions: itemsActions?.map((action) =>
			action?.data?.id === 'delete'
				? {...action, className: 'text-danger'}
				: action
		),
	};
}
