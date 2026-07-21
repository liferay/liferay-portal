/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Prompt} from '../types';
import confirmAndDeletePromptAction from './actions/confirmAndDeletePromptAction';
import duplicatePromptAction from './actions/duplicatePromptAction';

interface ItemsAction {
	data?: {id?: string};
}

interface PromptsFDSPropsTransformerProps {
	[key: string]: unknown;
}

export default function PromptsFDSPropsTransformer(
	props: PromptsFDSPropsTransformerProps
) {
	return {
		...props,
		onActionDropdownItemClick({
			action,
			itemData,
			loadData,
		}: {
			action: ItemsAction;
			itemData: Prompt;
			loadData: () => void;
		}) {
			if (action?.data?.id === 'duplicate') {
				duplicatePromptAction({itemData, loadData});
			}
			else if (action?.data?.id === 'delete') {
				confirmAndDeletePromptAction({itemData, loadData});
			}
		},
	};
}
