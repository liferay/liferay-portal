/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal, openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';

const ACTIONS = {
	copy(itemData) {
		openModal({
			containerProps: {
				className: 'modal-height-md',
			},
			size: 'lg',
			title: sub(Liferay.Language.get('duplicate-x'), itemData.roleName),
			url: itemData.copyRoleURL,
		});
	},
	delete(itemData) {
		openConfirmModal({
			message: itemData.confirmationMessage,
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					Liferay.Util.navigate(itemData.deleteRoleURL);
				}
			},
		});
	},
	permissions(itemData) {
		openModal({
			title: Liferay.Language.get('permissions'),
			url: itemData.permissionsURL,
		});
	},
};

export default function RoleActionPropsTransformer({items, ...props}) {
	const bindAction = (item) => {
		const action = ACTIONS[item.data?.action];

		const transformedItem = {...item};

		if (typeof action === 'function') {
			transformedItem.onClick = (event) => {
				event.preventDefault();

				action.call(ACTIONS, item.data);
			};
		}

		if (Array.isArray(item.items)) {
			transformedItem.items = item.items.map(bindAction);
		}

		return transformedItem;
	};

	return {
		...props,
		items: items?.map(bindAction),
	};
}
