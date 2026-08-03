/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Profile} from '../types';
import confirmAndDeleteProfileAction from './actions/confirmAndDeleteProfileAction';

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
