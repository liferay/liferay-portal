/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import {openModal, openToast} from 'frontend-js-components-web';
import React from 'react';

import {ConfirmUnpublishAPIApplicationModalContent} from '../modals/ConfirmUnpublishAPIApplicationModalContent';
import {CreateAPIApplicationModalContent} from '../modals/CreateAPIApplicationModalContent';
import {DeleteAPIApplicationModalContent} from '../modals/DeleteAPIApplicationModalContent';
import {updateData} from '../utils/fetchUtil';
import {getAPIApplicationsFDSProps} from './fdsUtils/applicationsFDSProps';

interface APIApplicationsTableProps {
	apiURLPaths: APIURLPaths;
	basePath: string;
	editURL: string;
	portletId: string;
	readOnly: boolean;
}

export default function APIApplicationsTable({
	apiURLPaths,
	basePath,
	editURL,
	portletId,
	readOnly,
}: APIApplicationsTableProps) {
	const changePublicationStatus = async (
		itemData: APIApplicationItem,
		loadData: voidReturn
	) => {
		const url = itemData.actions.update.href;
		const onError = (error: string) => {
			openToast({
				message: error,
				type: 'danger',
			});
		};

		if (itemData.applicationStatus.key === 'unpublished') {
			await updateData({
				dataToUpdate: {
					applicationStatus: {key: 'published'},
				},
				method: 'PATCH',
				onError,
				onSuccess: () => {
					loadData();
					openToast({
						message: Liferay.Language.get(
							'api-application-was-published'
						),
						type: 'success',
					});
				},
				url,
			});
		}
		else if (itemData.applicationStatus.key === 'published') {
			openModal({
				center: true,
				contentComponent: ({closeModal}: {closeModal: voidReturn}) =>
					ConfirmUnpublishAPIApplicationModalContent({
						closeModal,
						handlePublish: () => {
							updateData({
								dataToUpdate: {
									applicationStatus: {key: 'unpublished'},
								},
								method: 'PATCH',
								onError,
								onSuccess: () => {
									closeModal();
									loadData();
									openToast({
										message: Liferay.Language.get(
											'api-application-was-unpublished'
										),
										type: 'success',
									});
								},
								url,
							});
						},
					}),
				id: 'ConfirmUnpublishAPIApplicationModal',
				size: 'md',
				status: 'warning',
			});
		}
	};

	const createAPIApplication = {
		label: Liferay.Language.get('add-new-api-application'),
		onClick: ({loadData}: {loadData: voidReturn}) => {
			openModal({
				center: true,
				contentComponent: ({closeModal}: {closeModal: voidReturn}) =>
					CreateAPIApplicationModalContent({
						apiApplicationsURLPath: apiURLPaths.applications,
						basePath,
						closeModal,
						editURL,
						loadData,
						portletId,
					}),
				id: 'createAPIApplicationModal',
				size: 'md',
			});
		},
	};

	const deleteAPIApplication = (
		itemData: APIApplicationItem,
		loadData: voidReturn
	) => {
		openModal({
			center: true,
			contentComponent: ({closeModal}: {closeModal: voidReturn}) =>
				DeleteAPIApplicationModalContent({
					closeModal,
					itemData,
					loadData,
				}),
			id: 'deleteAPIApplicationModal',
			size: 'md',
			status: 'danger',
		});
	};

	function onActionDropdownItemClick({
		action,
		itemData,
		loadData,
	}: FDSItem<APIApplicationItem>) {
		if (action.id === 'deleteAPIApplication') {
			deleteAPIApplication(itemData, loadData);
		}
		else if (action.id === 'changePublicationStatus') {
			changePublicationStatus(itemData, loadData);
		}
	}

	return (
		<FrontendDataSet
			{...getAPIApplicationsFDSProps(
				apiURLPaths.applications,
				editURL,
				portletId
			)}
			creationMenu={{
				primaryItems: readOnly ? [] : [createAPIApplication],
			}}
			onActionDropdownItemClick={onActionDropdownItemClick}
		/>
	);
}
