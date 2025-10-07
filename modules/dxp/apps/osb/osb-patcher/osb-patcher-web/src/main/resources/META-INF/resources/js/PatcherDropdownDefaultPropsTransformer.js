/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal, openModal} from 'frontend-js-components-web';

const ACTIONS = {
	confirm(itemData) {
		openConfirmModal({
			message: itemData.message,
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					window.location.href = itemData.url;
				}
			},
		});
	},
	delete(itemData) {
		openConfirmModal({
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-this'
			),
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					submitForm(document.hrefFm, itemData.url);
				}
			},
		});
	},
	openModal(itemData) {
		openModal({
			title: itemData.title,
			url: itemData.url,
		});
	},
	submitForm(itemData) {
		submitForm(document.hrefFm, itemData.url);
	},
};

export default function propsTransformer({items, ...props}) {
	return {
		...props,
		items: items.map((item) => {
			return {
				...item,
				onClick(event) {
					const action = item.data?.action;

					if (action) {
						event.preventDefault();

						ACTIONS[action](item.data);
					}
				},
			};
		}),
	};
}
