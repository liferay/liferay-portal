/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';

import {Profile} from '../types';
import confirmAndDeleteProfileAction from './actions/confirmAndDeleteProfileAction';
import MCPStatusRenderer from './cell_renderers/MCPStatusRenderer';

interface ItemsAction {
	data?: {id?: string};
}

interface ProfilesFDSPropsTransformerProps {
	[key: string]: unknown;
}

export default function ProfilesFDSPropsTransformer(
	props: ProfilesFDSPropsTransformerProps
) {
	return {
		...props,
		customRenderers: {
			tableCell: [
				{
					component: MCPStatusRenderer,
					name: 'mcpStatusRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		onActionDropdownItemClick({
			action,
			itemData,
			loadData,
		}: {
			action: ItemsAction;
			itemData: Profile;
			loadData: () => void;
		}) {
			if (action?.data?.id === 'delete') {
				confirmAndDeleteProfileAction({itemData, loadData});
			}
		},
	};
}
