/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	openConfirmModal,
	openModal,
	openSelectionModal,
	openToast,
} from 'frontend-js-components-web';
import {addParams, fetch, navigate, sub} from 'frontend-js-web';

import openDeleteLayoutModal from './openDeleteLayoutModal';

export default function propsTransformer({portletNamespace, ...otherProps}) {
	const convertSelectedPages = (itemData) => {
		openConfirmModal({
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-convert-the-selected-pages'
			),
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					const form = document.getElementById(
						`${portletNamespace}fm`
					);

					if (form) {
						submitForm(form, itemData?.convertLayoutURL);
					}
				}
			},
		});
	};

	const deleteSelectedPages = (itemData) => {
		openDeleteLayoutModal({
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-the-selected-pages-if-the-selected-pages-have-child-pages-they-will-also-be-removed'
			),
			multiple: true,
			onDelete: () => {
				const keys = getSelectedKeys(portletNamespace);

				const url = new URL(itemData?.deleteLayoutURL);

				return fetch(
					addParams(
						{
							[`_${url.searchParams.get('p_p_id')}_rowIds`]:
								keys.join(','),
						},
						itemData?.deleteLayoutURL
					),
					{
						method: 'post',
					}
				)
					.then((response) => response.json())
					.then(({errorMessage, redirectURL}) => {
						if (errorMessage) {
							openToast({
								message: errorMessage,
								title: Liferay.Language.get('error'),
								type: 'danger',
							});
						}
						else {
							navigate(redirectURL);
						}
					})
					.catch(() =>
						openToast({
							message: Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
							title: Liferay.Language.get('error'),
							type: 'danger',
						})
					);
			},
		});
	};

	const changePermissions = (itemData) => {
		const keys = getSelectedKeys(portletNamespace);

		if (keys.length > itemData.maxItemsToShowInfoMessage) {
			openModal({
				bodyHTML: `<p class="text-secondary">
					${sub(
						Liferay.Language.get(
							'you-have-selected-more-than-x-x-info-message'
						),
						itemData.maxItemsToShowInfoMessage,
						Liferay.Language.get('pages')
					)}
				</p>`,
				buttons: [
					{
						displayType: 'secondary',
						label: Liferay.Language.get('cancel'),
						type: 'cancel',
					},
					{
						displayType: 'info',
						label: Liferay.Language.get('continue'),
						onClick: ({processClose}) => {
							processClose();
							openChangePermissionsSelectionModal(itemData, keys);
						},
						type: 'button',
					},
				],
				status: 'info',
				title: Liferay.Language.get('bulk-action-performance'),
			});
		}
		else {
			openChangePermissionsSelectionModal(itemData, keys);
		}
	};

	const openChangePermissionsSelectionModal = (itemData, keys) => {
		const url = new URL(itemData?.changePermissionsURL);

		openSelectionModal({
			title: Liferay.Language.get('permissions'),
			url: addParams(
				{
					[`_${url.searchParams.get('p_p_id')}_resourcePrimKey`]:
						keys.join(','),
				},
				itemData?.changePermissionsURL
			),
		});
	};

	const exportTranslation = ({exportTranslationURL}) => {
		const keys = getSelectedKeys(portletNamespace);

		const url = new URL(exportTranslationURL);

		navigate(
			addParams(
				{
					[`_${url.searchParams.get('p_p_id')}_classPK`]:
						keys.join(','),
				},
				exportTranslationURL
			)
		);
	};

	return {
		...otherProps,
		onActionButtonClick: (event, {item}) => {
			const data = item?.data;

			const action = data?.action;

			if (action === 'convertSelectedPages') {
				convertSelectedPages(data);
			}
			else if (action === 'deleteSelectedPages') {
				deleteSelectedPages(data);
			}
			else if (action === 'exportTranslation') {
				exportTranslation(data);
			}
			else if (action === 'changePermissions') {
				changePermissions(data);
			}
		},
	};
}

function getSelectedKeys(portletNamespace) {
	return Array.from(
		document.querySelectorAll(`[name=${portletNamespace}rowIds]:checked`)
	).map(({value}) => value);
}
