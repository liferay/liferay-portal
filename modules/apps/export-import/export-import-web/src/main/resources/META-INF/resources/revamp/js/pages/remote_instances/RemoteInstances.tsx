/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import {escapeHTML} from 'frontend-js-web';
import React, {useState} from 'react';

import {
	REMOTE_INSTANCES_API_URL,
	deleteRemoteInstance,
} from '../../services/remoteInstances';
import {RemoteInstance} from '../../types/remoteInstance';
import RemoteInstanceForm from './RemoteInstanceForm';

enum View {
	FORM,
	LIST,
}

export function RemoteInstances() {
	const [remoteInstance, setRemoteInstance] = useState<RemoteInstance | null>(
		null
	);
	const [view, setView] = useState(View.LIST);

	const openForm = (remoteInstance: RemoteInstance | null) => {
		setRemoteInstance(remoteInstance);
		setView(View.FORM);
	};

	if (view === View.FORM) {
		return (
			<RemoteInstanceForm
				onBack={() => setView(View.LIST)}
				remoteInstance={remoteInstance}
			/>
		);
	}

	return (
		<div className="sheet sheet-lg">
			<h2 className="sheet-title">
				{Liferay.Language.get('remote-environments')}
			</h2>

			<FrontendDataSet
				apiURL={REMOTE_INSTANCES_API_URL}
				creationMenu={{
					primaryItems: [
						{
							label: Liferay.Language.get(
								'add-remote-environment'
							),
							onClick: () => openForm(null),
						},
					],
				}}
				customDataRenderers={{
					portRenderer: ({value}: {value: number}) => value || '',
				}}
				emptyState={{
					title: Liferay.Language.get(
						'no-remote-environments-have-been-added'
					),
				}}
				id="remoteInstances"
				itemsActions={[
					{
						data: {id: 'edit', permissionKey: 'update'},
						icon: 'pencil',
						label: Liferay.Language.get('edit'),
						onClick: ({itemData}: {itemData: RemoteInstance}) =>
							openForm(itemData),
					},
					{
						data: {
							confirmationMessage: Liferay.Language.get(
								'are-you-sure-you-want-to-delete-this'
							),
							id: 'delete',
							permissionKey: 'delete',
							status: 'warning',
						},
						icon: 'trash',
						label: Liferay.Language.get('delete'),

						onClick: async ({
							itemData,
							loadData,
						}: {
							itemData: RemoteInstance;
							loadData: () => void;
						}) => {
							const {error} = await deleteRemoteInstance(
								itemData.id
							);

							if (error !== null) {
								Liferay.Util.openToast({
									message: escapeHTML(error),
									type: 'danger',
								});

								return;
							}

							loadData();
						},
					},
				]}
				style="fluid"
				views={[
					{
						contentRenderer: 'table',
						label: Liferay.Language.get('table'),
						name: 'table',
						schema: {
							fields: [
								{
									actionId: 'edit',
									contentRenderer: 'actionLink',
									expand: true,
									fieldName: 'name',
									label: Liferay.Language.get('name'),
								},
								{
									expand: true,
									fieldName: 'hostname',
									label: Liferay.Language.get('hostname'),
								},
								{
									contentRenderer: 'portRenderer',
									fieldName: 'port',
									label: Liferay.Language.get('port'),
								},
							],
						},
						thumbnail: 'table',
					},
				]}
			/>
		</div>
	);
}
