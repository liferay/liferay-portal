/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	DSRequestStatusDataRenderer,
	removeDSRequestStatusColumn,
} from 'commerce-frontend-js';
import {openModal, openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React from 'react';

import CommerceOrderAttachmentRestrictedDataRenderer from './CommerceOrderAttachmentRestrictedDataRenderer';
import CommerceOrderAttachmentTitleDataRenderer from './CommerceOrderAttachmentTitleDataRenderer';

const openDeleteConfirmationModal = ({itemName, loadData, url}) => {
	openModal({
		bodyHTML: Liferay.Language.get(
			'are-you-sure-you-want-to-delete-this-attachment'
		),
		buttons: [
			{
				autoFocus: true,
				displayType: 'secondary',
				label: Liferay.Language.get('cancel'),
				type: 'cancel',
			},
			{
				displayType: 'danger',
				label: Liferay.Language.get('delete'),
				onClick: ({processClose}) => {
					if (!url) {
						openToast({
							message: Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
							type: 'danger',
						});

						return;
					}

					Liferay.Util.fetch(url, {method: 'DELETE'})
						.then((response) => {
							if (!response.ok) {
								throw new Error(response.statusText);
							}

							processClose();

							openToast({
								message: Liferay.Language.get(
									'your-request-completed-successfully'
								),
								type: 'success',
							});

							loadData();
						})
						.catch(() => {
							openToast({
								message: Liferay.Language.get(
									'an-unexpected-error-occurred'
								),
								type: 'danger',
							});
						});
				},
			},
		],
		containerProps: {
			className: '',
		},
		status: 'danger',
		title: sub(
			Liferay.Language.get('delete-x'),
			'"' + (itemName || '') + '"'
		),
	});
};

const AttachmentsFDSPropsTransformer = (props) => {
	const dsRequestStatuses = props.additionalProps?.dsRequestStatuses ?? {};

	return {
		...props,
		customDataRenderers: {
			commerceOrderAttachmentRestrictedDataRenderer:
				CommerceOrderAttachmentRestrictedDataRenderer,
			commerceOrderAttachmentTitleDataRenderer:
				CommerceOrderAttachmentTitleDataRenderer,
			dsRequestStatusDataRenderer: (rendererProps) =>
				React.createElement(DSRequestStatusDataRenderer, {
					...rendererProps,
					dsRequestStatuses,
				}),
		},
		itemsActions: props.itemsActions?.map((action) => {
			if (action?.data?.id === 'delete') {
				return {
					...action,
					className: 'text-danger',
				};
			}

			return action;
		}),
		onActionDropdownItemClick: ({
			action: {
				data: {id: actionId},
			},
			event,
			itemData,
			loadData,
		}) => {
			if (actionId === 'delete') {
				event?.preventDefault();

				openDeleteConfirmationModal({
					itemName: itemData?.title,
					loadData,
					url: itemData?.actions?.delete?.href,
				});
			}
			else if (actionId === 'download') {
				event?.preventDefault();

				const fileURL = itemData?.url;

				if (!fileURL) {
					return;
				}

				window.location.href = fileURL;
			}
		},
		views: Object.keys(dsRequestStatuses).length
			? props.views
			: removeDSRequestStatusColumn(props.views),
	};
};

export default AttachmentsFDSPropsTransformer;
