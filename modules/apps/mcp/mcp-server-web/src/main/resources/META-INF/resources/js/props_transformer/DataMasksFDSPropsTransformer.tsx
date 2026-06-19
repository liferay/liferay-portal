/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DataMask} from '../types';
import {isSystemMask} from '../utils';
import confirmAndDeleteDataMaskAction from './actions/confirmAndDeleteDataMaskAction';
import duplicateDataMaskAction from './actions/duplicateDataMaskAction';

interface ItemsAction {
	data?: {id?: string};
}

interface DataMasksFDSPropsTransformerProps {
	itemsActions: ItemsAction[];
	[key: string]: unknown;
}

export default function DataMasksFDSPropsTransformer({
	itemsActions,
	...props
}: DataMasksFDSPropsTransformerProps) {
	return {
		...props,
		itemsActions: itemsActions.map((action) => {
			if (action?.data?.id === 'view') {
				return {
					...action,
					isVisible: (item: DataMask) => isSystemMask(item),
				};
			}

			if (action?.data?.id === 'edit' || action?.data?.id === 'delete') {
				return {
					...action,
					isVisible: (item: DataMask) => !isSystemMask(item),
				};
			}

			return action;
		}),
		onActionDropdownItemClick({
			action,
			itemData,
			loadData,
		}: {
			action: ItemsAction;
			itemData: DataMask;
			loadData: () => void;
		}) {
			if (action?.data?.id === 'duplicate') {
				duplicateDataMaskAction({itemData, loadData});
			}
			else if (action?.data?.id === 'delete') {
				confirmAndDeleteDataMaskAction({itemData, loadData});
			}
		},
	};
}
