/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ProfileTool} from '../types';
import confirmAndRemoveProfileToolAction from './actions/confirmAndRemoveProfileToolAction';
import openAddToolsModal from './actions/openAddToolsModal';

interface CreationActionItem {
	href?: string;
	label: string;
	onClick?: (context: {loadData: () => void}) => void;
}

interface ItemsAction {
	data?: {id?: string};
}

interface ProfileToolsFDSPropsTransformerProps {
	additionalProps: {profileERC: string};
	creationMenu?: {primaryItems?: CreationActionItem[]};
	[key: string]: unknown;
}

export default function ProfileToolsFDSPropsTransformer({
	additionalProps: {profileERC},
	creationMenu,
	...otherProps
}: ProfileToolsFDSPropsTransformerProps) {
	return {
		...otherProps,
		creationMenu: {
			...creationMenu,
			primaryItems: (creationMenu?.primaryItems ?? []).map((item) => ({
				...item,
				href: undefined,
				onClick: ({loadData}: {loadData: () => void}) =>
					openAddToolsModal({loadData, profileERC}),
			})),
		},
		onActionDropdownItemClick({
			action,
			itemData,
			loadData,
		}: {
			action: ItemsAction;
			itemData: ProfileTool;
			loadData: () => void;
		}) {
			if (action?.data?.id === 'remove') {
				confirmAndRemoveProfileToolAction({itemData, loadData});
			}
		},
	};
}
