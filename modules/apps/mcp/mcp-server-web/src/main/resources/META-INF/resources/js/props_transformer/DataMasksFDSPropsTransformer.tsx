/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {navigate} from 'frontend-js-web';

import {ActionContext, DataMask} from '../types';
import confirmAndDeleteDataMaskAction from './actions/confirmAndDeleteDataMaskAction';
import duplicateDataMaskAction from './actions/duplicateDataMaskAction';
import {maskEditURL} from './utils/maskEditURL';

function isSystemMask(dataMask: DataMask) {
	return dataMask?.maskType?.key === 'system';
}

interface DataMasksFDSPropsTransformerProps {
	additionalProps: {
		createURL: string;
		editURL: string;
		viewURL: string;
	};
	[key: string]: unknown;
}

export default function DataMasksFDSPropsTransformer({
	additionalProps: {createURL, editURL, viewURL},
	...props
}: DataMasksFDSPropsTransformerProps) {
	return {
		...props,
		creationMenu: {
			primaryItems: [
				{
					label: Liferay.Language.get('new-data-mask'),
					onClick: () => navigate(createURL),
				},
			],
		},
		itemsActions: [
			{
				icon: 'view',
				isVisible: (item: DataMask) => isSystemMask(item),
				label: Liferay.Language.get('view'),
				onClick: ({itemData}: ActionContext) =>
					navigate(maskEditURL(viewURL, Number(itemData.id))),
			},
			{
				icon: 'pencil',
				isVisible: (item: DataMask) => !isSystemMask(item),
				label: Liferay.Language.get('edit'),
				onClick: ({itemData}: ActionContext) =>
					navigate(maskEditURL(editURL, Number(itemData.id))),
			},
			{
				icon: 'copy',
				label: Liferay.Language.get('duplicate'),
				onClick: duplicateDataMaskAction,
			},
			{
				icon: 'trash',
				isVisible: (item: DataMask) => !isSystemMask(item),
				label: Liferay.Language.get('delete'),
				onClick: confirmAndDeleteDataMaskAction,
			},
		],
	};
}
