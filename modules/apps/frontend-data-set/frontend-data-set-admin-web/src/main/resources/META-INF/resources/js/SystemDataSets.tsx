/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import React, {useContext, useState} from 'react';

import '../css/DataSets.scss';

import ClayButton from '@clayui/button';
import {ClayRadio} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLink from '@clayui/link';
import ClayList from '@clayui/list';
import ClayModal from '@clayui/modal';
import ClaySticker from '@clayui/sticker';
import {ClayTooltipProvider} from '@clayui/tooltip';
import classNames from 'classnames';
import {openModal} from 'frontend-js-components-web';
import {fetch, navigate} from 'frontend-js-web';

import Toggle from './components/Toggle';
import {DEFAULT_FETCH_HEADERS, FDS_DEFAULT_PROPS} from './utils/constants';
import getAPIExplorerURL from './utils/getAPIExplorerURL';
import getDataSetResourceURL from './utils/getDataSetResourceURL';
import openDefaultFailureToast from './utils/openDefaultFailureToast';
import openDefaultSuccessToast from './utils/openDefaultSuccessToast';
import {IDataSet, ISystemDataSet} from './utils/types';

interface IFrontendDataSetContext {
	selectItems: ({value}: {value: any}) => void;
	selectable: boolean;
	selectedItemsKey: keyof ISystemDataSet;
	selectedItemsValue: Array<any>;
}

const SystemDataSetsView = ({
	frontendDataSetContext,
	items,
	onItemSelectionChange,
}: {
	frontendDataSetContext: any;
	items: Array<ISystemDataSet>;
	onItemSelectionChange?: Function;
}) => {
	const {selectable, selectedItemsKey, selectedItemsValue} = useContext(
		frontendDataSetContext
	) as IFrontendDataSetContext;

	return (
		<ClayList>
			{items.map((item) => {
				return (
					<ClayList.Item
						className={classNames({
							disabled: item.imported,
							selectable,
							selected: selectedItemsValue?.includes(
								item[selectedItemsKey]
							),
						})}
						data-erc={item.name}
						flex
						key={item.name}
						onClick={() => {
							if (selectable) {
								onItemSelectionChange?.(item);
							}
						}}
					>
						<ClayList.ItemField className="justify-content-center selection-control">
							<ClayRadio
								checked={
									selectedItemsValue
										? selectedItemsValue
												.map((element) =>
													String(element)
												)
												.includes(
													String(
														item[selectedItemsKey]
													)
												)
										: false
								}
								onChange={() => {}}
								value=""
							/>
						</ClayList.ItemField>

						<ClayList.ItemField>
							<ClaySticker displayType="dark">
								<ClayIcon symbol={item.symbol} />
							</ClaySticker>
						</ClayList.ItemField>

						<ClayList.ItemField
							className="justify-content-center"
							expand
						>
							<ClayList.ItemTitle>
								{item.title}
							</ClayList.ItemTitle>

							<ClayList.ItemText>
								{item.description}
							</ClayList.ItemText>
						</ClayList.ItemField>

						{item.imported && (
							<ClayList.ItemField>
								<ClayLabel
									className="created-label"
									displayType="warning"
								>
									{Liferay.Language.get('created')}
								</ClayLabel>
							</ClayList.ItemField>
						)}
					</ClayList.Item>
				);
			})}
		</ClayList>
	);
};

const SelectSystemDataSetModalContent = ({
	closeModal,
	getSystemDataSetsURL,
	importSystemDataSetURL,
	loadData,
	namespace,
}: {
	closeModal: Function;
	getSystemDataSetsURL: string;
	importSystemDataSetURL: string;
	loadData: Function;
	namespace: string;
}) => {
	const [createButtonDisabled, setCreateButtonDisabled] = useState(true);
	const [selectedSystemDataSet, setSelectedSystemDataSet] =
		useState<ISystemDataSet | null>(null);

	const onCreateButtonClick = async () => {
		if (!selectedSystemDataSet) {
			return;
		}

		setCreateButtonDisabled(true);

		const formData = new FormData();

		formData.append(`${namespace}name`, selectedSystemDataSet.name);

		const response = await fetch(importSystemDataSetURL, {
			body: formData,
			method: 'POST',
		});

		if (response.ok) {
			closeModal();

			openDefaultSuccessToast();

			loadData();
		}
		else {
			openDefaultFailureToast();

			setCreateButtonDisabled(false);
		}
	};

	return (
		<>
			<ClayModal.Header className="select-system-data-set-modal-header">
				{Liferay.Language.get('create-system-data-set-customization')}
			</ClayModal.Header>

			<ClayModal.Body>
				<div className="modal-height-full select-system-data-set-modal-body">
					<FrontendDataSet
						{...FDS_DEFAULT_PROPS}
						apiURL={getSystemDataSetsURL}
						id="SystemDataSets"
						onSelectedItemsChange={(
							selectedItems: Array<ISystemDataSet>
						) => {
							setSelectedSystemDataSet(selectedItems[0]);
						}}
						selectedItems={[selectedSystemDataSet]}
						selectedItemsKey="name"
						selectionType="single"
						views={[
							{
								component: SystemDataSetsView,
								contentRenderer: 'custom',
								name: 'custom',
							},
						]}
					/>
				</div>
			</ClayModal.Body>

			<ClayModal.Footer
				className="select-system-data-set-modal-footer"
				last={
					<ClayButton.Group spaced>
						<ClayButton
							className="btn-cancel"
							displayType="secondary"
							onClick={() => closeModal()}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={
								createButtonDisabled && !selectedSystemDataSet
							}
							onClick={onCreateButtonClick}
						>
							{Liferay.Language.get('create')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
};

const SystemDataSets = ({
	editDataSetURL,
	getSystemDataSetsURL,
	importSystemDataSetURL,
	namespace,
	systemDataSets,
}: {
	editDataSetURL: string;
	getSystemDataSetsURL: string;
	importSystemDataSetURL: string;
	namespace: string;
	systemDataSets: Array<ISystemDataSet>;
}) => {
	const [toggleDisabled, setToogleDisabled] = useState(false);

	const getAPIURL = () => {
		if (!systemDataSets.length) {
			return undefined;
		}

		const systemDataSetNames: string = systemDataSets
			.map((systemDataSet) => `'${systemDataSet.name}'`)
			.join(',');

		return getDataSetResourceURL({
			params: {
				filter: `externalReferenceCode in (${systemDataSetNames})`,
			},
		});
	};

	const getEditURL = (itemData: IDataSet) => {
		const url = new URL(editDataSetURL);

		url.searchParams.set(
			`${namespace}dataSetERC`,
			itemData.externalReferenceCode
		);
		url.searchParams.set(`${namespace}dataSetLabel`, itemData.label);

		return url;
	};

	const onDeleteClick = ({
		itemData,
		loadData,
	}: {
		itemData: IDataSet;
		loadData: Function;
	}) => {
		openModal({
			bodyHTML: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-this'
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
					onClick: ({processClose}: {processClose: Function}) => {
						processClose();

						fetch(itemData.actions.delete.href, {
							headers: DEFAULT_FETCH_HEADERS,
							method: itemData.actions.delete.method,
						})
							.then(() => {
								openDefaultSuccessToast();

								loadData();
							})
							.catch(openDefaultFailureToast);
					},
				},
			],
			status: 'danger',
			title: Liferay.Language.get('delete-data-set'),
		});
	};

	const updateActive = async ({
		itemData,
		onItemsChange,
	}: {
		itemData: IDataSet;
		onItemsChange: ({items}: {items: Array<IDataSet>}) => void;
	}) => {
		setToogleDisabled(true);

		const url = getDataSetResourceURL({
			dataSetERC: itemData.externalReferenceCode,
		});

		const response = await fetch(url, {
			body: JSON.stringify({active: !itemData.active}),
			headers: DEFAULT_FETCH_HEADERS,
			method: 'PATCH',
		});

		if (!response.ok) {
			openDefaultFailureToast();

			return;
		}

		const systemDataSet: IDataSet = await response.json();

		if (systemDataSet?.id) {
			onItemsChange({items: [systemDataSet]});

			openDefaultSuccessToast();
		}
		else {
			openDefaultFailureToast();
		}

		setToogleDisabled(false);
	};

	const creationMenu = {
		primaryItems: [
			{
				label: Liferay.Language.get(
					'create-system-data-set-customization'
				),
				onClick: ({loadData}: {loadData: Function}) => {
					openModal({
						contentComponent: ({
							closeModal,
						}: {
							closeModal: Function;
						}) => (
							<SelectSystemDataSetModalContent
								closeModal={closeModal}
								getSystemDataSetsURL={getSystemDataSetsURL}
								importSystemDataSetURL={importSystemDataSetURL}
								loadData={loadData}
								namespace={namespace}
							/>
						),
						size: 'lg',
					});
				},
			},
		],
	};

	const restApplicationRenderer = function ({
		itemData,
	}: {
		itemData: IDataSet;
	}) {
		const apiExplorerURL = getAPIExplorerURL(itemData.restApplication);

		return (
			<ClayTooltipProvider>
				<ClayLink
					data-tooltip-align="top"
					decoration="underline"
					displayType="tertiary"
					href={apiExplorerURL}
					rel="noopener noreferrer"
					target="_blank"
					title={apiExplorerURL}
				>
					<span className="inline-item inline-item-before">
						<ClayIcon
							className="mr-1 text-2 text-secondary"
							symbol="shortcut"
						/>
					</span>

					{itemData.restApplication}
				</ClayLink>
			</ClayTooltipProvider>
		);
	};

	const toggleRenderer = function ({
		itemData,
		onItemsChange,
	}: {
		itemData: IDataSet;
		onItemsChange: ({items}: {items: Array<IDataSet>}) => void;
	}) {
		if (itemData.actions.update) {
			return Toggle({
				disabled: toggleDisabled,
				item: itemData,
				toggleChange: () => updateActive({itemData, onItemsChange}),
			});
		}

		return (
			<ClayLabel displayType={itemData.active ? 'success' : 'secondary'}>
				{itemData.active
					? Liferay.Language.get('active')
					: Liferay.Language.get('inactive')}
			</ClayLabel>
		);
	};

	const views = [
		{
			contentRenderer: 'table',
			name: 'table',
			schema: {
				fields: [
					{
						actionId: 'edit',
						contentRenderer: 'actionLink',
						fieldName: 'label',
						label: Liferay.Language.get('name'),
						sortable: true,
					},
					{
						contentRenderer: 'restApplicationRenderer',
						fieldName: 'restApplication',
						label: Liferay.Language.get('rest-application'),
						sortable: true,
					},
					{
						fieldName: 'restSchema',
						label: Liferay.Language.get('rest-schema'),
						sortable: true,
					},
					{
						fieldName: 'restEndpoint',
						label: Liferay.Language.get('rest-endpoint'),
						sortable: true,
					},
					{
						fieldName: 'status',
						label: Liferay.Language.get('status'),
						sortable: true,
					},
					{
						contentRenderer: 'dateTime',
						fieldName: 'dateModified',
						label: Liferay.Language.get('modified-date'),
						sortable: true,
					},
					{
						contentRenderer: 'toggleRenderer',
						fieldName: 'active',
						label: Liferay.Language.get('status'),
						name: 'active',
					},
				],
			},
		},
	];

	return (
		<div className="data-sets system-data-sets">
			<FrontendDataSet
				{...FDS_DEFAULT_PROPS}
				apiURL={getAPIURL()}
				creationMenu={creationMenu}
				customDataRenderers={{restApplicationRenderer, toggleRenderer}}
				emptyState={{
					description: Liferay.Language.get(
						'start-creating-one-to-show-your-data'
					),
					image: '/states/empty_state.svg',
					title: Liferay.Language.get('no-system-data-sets-created'),
				}}
				id="CreatedSystemDataSets"
				itemsActions={[
					{
						data: {
							id: 'edit',
							permissionKey: 'update',
						},
						icon: 'pencil',
						label: Liferay.Language.get('edit'),
						onClick: ({itemData}: {itemData: IDataSet}) => {
							navigate(getEditURL(itemData));
						},
					},
					{
						data: {
							permissionKey: 'delete',
						},
						icon: 'trash',
						label: Liferay.Language.get('delete'),
						onClick: onDeleteClick,
					},
				]}
				sorts={[{direction: 'desc', key: 'dateCreated'}]}
				views={views}
			/>
		</div>
	);
};

export default SystemDataSets;
