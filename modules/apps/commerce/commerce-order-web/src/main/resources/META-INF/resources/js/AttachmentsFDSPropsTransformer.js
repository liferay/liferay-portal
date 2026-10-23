/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	DSRequestStatusDataRenderer,
	formatActionUrl,
} from 'commerce-frontend-js';
import {openModal, openToast} from 'frontend-js-components-web';
import {escapeHTML, sub} from 'frontend-js-web';
import React from 'react';

import AddDSRequestModal from './AddDSRequestModal';
import CommerceOrderAttachmentRestrictedDataRenderer from './CommerceOrderAttachmentRestrictedDataRenderer';
import CommerceOrderAttachmentTitleDataRenderer from './CommerceOrderAttachmentTitleDataRenderer';

const openAddDSRequestModal = ({
	addDSRequestModalProps,
	addDSRequestURL,
	attachment,
}) => {
	openModal({
		contentComponent: ({closeModal}) =>
			React.createElement(AddDSRequestModal, {
				addDSRequestModalProps,
				addDSRequestURL,
				attachment,
				closeModal,
			}),
		size: 'lg',
	});
};

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

const openErrorToast = (error) => {
	openToast({
		message: escapeHTML(
			error?.errorMessage ||
				Liferay.Language.get('an-unexpected-error-occurred')
		),
		type: 'danger',
	});
};

const openVoidConfirmationModal = ({url}) => {
	openModal({
		bodyHTML: Liferay.Language.get(
			'are-you-sure-you-want-to-void-this-signature-request'
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
				label: Liferay.Language.get('void'),
				onClick: ({processClose}) => {
					postDSRequestAction(url)
						.then(() => {
							processClose();

							window.location.reload();
						})
						.catch(openErrorToast);
				},
			},
		],
		status: 'danger',
		title: Liferay.Language.get('void'),
	});
};

const postDSRequestAction = (url) =>
	Liferay.Util.fetch(url, {method: 'POST'})
		.then((response) => {
			if (!response.ok) {
				throw new Error(response.statusText);
			}

			return response.json();
		})
		.then(({errorMessage}) => {
			if (errorMessage) {
				throw {errorMessage};
			}

			openToast({
				message: Liferay.Language.get(
					'your-request-completed-successfully'
				),
				type: 'success',
			});
		});

const AttachmentsFDSPropsTransformer = (props) => {
	const addDSRequestModalProps =
		props.additionalProps?.addDSRequestModalProps;
	const dsRequestStatuses = props.additionalProps?.dsRequestStatuses ?? {};
	const replaceableCommerceOrderAttachmentIds =
		props.additionalProps?.replaceableCommerceOrderAttachmentIds ?? [];

	const getDSRequestStatus = (item) => dsRequestStatuses[item.id];

	const isRequestable = (item) =>
		!!addDSRequestModalProps &&
		addDSRequestModalProps.allowedFileExtensions.includes(item.extension) &&
		(!getDSRequestStatus(item) ||
			replaceableCommerceOrderAttachmentIds.includes(String(item.id)));

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

			if (actionId === 'request-signature') {
				return {
					...action,
					isVisible: isRequestable,
				};
			}

			if (actionId === 'resend' || actionId === 'void') {
				return {
					...action,
					isVisible: (item) => getDSRequestStatus(item) === 'sent',
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
			else if (actionId === 'request-signature') {
				event?.preventDefault();

				openAddDSRequestModal({
					addDSRequestModalProps,
					addDSRequestURL: formatActionUrl(
						action.data.addDSRequestURL,
						itemData
					),
					attachment: itemData,
				});
			}
			else if (actionId === 'resend') {
				event?.preventDefault();

				postDSRequestAction(
					formatActionUrl(action.data.resendDSRequestURL, itemData)
				).catch(openErrorToast);
			}
			else if (actionId === 'void') {
				event?.preventDefault();

				openVoidConfirmationModal({
					url: formatActionUrl(
						action.data.voidDSRequestURL,
						itemData
					),
				});
			}
		},
	};
};

export default AttachmentsFDSPropsTransformer;
