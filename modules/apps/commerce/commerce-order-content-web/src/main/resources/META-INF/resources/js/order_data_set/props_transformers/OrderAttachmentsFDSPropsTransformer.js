/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	DSRequestDetails,
	DSRequestStatusDataRenderer,
	formatActionUrl,
	removeDSRequestStatusColumn,
} from 'commerce-frontend-js';
import {openModal, openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React from 'react';

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

const OrderAttachmentsFDSPropsTransformer = (props) => {
	const dsRequestStatuses = props.additionalProps?.dsRequestStatuses ?? {};
	const signDSRequestURLs = props.additionalProps?.signDSRequestURLs ?? {};

	const getDSRequestStatus = (item) => dsRequestStatuses[item.id];

	return {
		...props,
		customDataRenderers: {
			dsRequestStatusDataRenderer: (rendererProps) =>
				React.createElement(DSRequestStatusDataRenderer, {
					...rendererProps,
					value: getDSRequestStatus(rendererProps.itemData),
				}),
		},
		itemsActions: props.itemsActions?.map((action) => {
			const actionId = action?.data?.id;

			if (actionId === 'delete') {
				return {
					...action,
					className: 'text-danger',
				};
			}

			if (actionId === 'sign') {
				return {
					...action,
					isVisible: (item) => !!signDSRequestURLs[item.id],
				};
			}

			if (actionId === 'view-signature-details') {
				return {
					...action,
					isVisible: (item) => !!getDSRequestStatus(item),
				};
			}

			return action;
		}),
		onActionDropdownItemClick: ({action, event, itemData, loadData}) => {
			const actionId = action?.data?.id;

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
			else if (actionId === 'sign') {
				event?.preventDefault();

				// XSS: itemData.title is escaped by React in Modal

				openModal({
					onClose: () => window.location.reload(),
					size: 'full-screen',
					title: itemData?.title,
					url: signDSRequestURLs[itemData?.id],
				});
			}
			else if (actionId === 'view-signature-details') {
				event?.preventDefault();

				openModal({
					bodyComponent: () =>
						React.createElement(DSRequestDetails, {
							url: formatActionUrl(
								action.data.dsRequestDetailsURL,
								itemData
							),
						}),
					size: 'lg',
					title: Liferay.Language.get('signature-details'),
				});
			}
		},
		views: Object.keys(dsRequestStatuses).length
			? props.views
			: removeDSRequestStatusColumn(props.views),
	};
};

export default OrderAttachmentsFDSPropsTransformer;
