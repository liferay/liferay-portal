/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import {openModal} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React, {
	createContext,
	useCallback,
	useContext,
	useEffect,
	useMemo,
	useRef,
	useState,
} from 'react';

import {DEFAULT_FETCH_HEADERS, FDS_DEFAULT_PROPS} from './utils/constants';
import getDataSetResourceURL from './utils/getDataSetResourceURL';
import getDataSetSnapshotResourceURL from './utils/getDataSetSnapshotResourceURL';
import openDefaultFailureToast from './utils/openDefaultFailureToast';
import openDefaultSuccessToast from './utils/openDefaultSuccessToast';

import '../css/DataSets.scss';

interface IManageUserViewsProps {
	currentURL: string;
	fdsEntryFDSSelectionFilterItemsDataProviderURL: string;
	namespace: string;
	portletId: string;
	systemDataSetEntries: Array<{name: string; title: string}>;
}

interface IDataSetLabelsContextValue {
	dataSetLabels: Record<string, string>;
	requestLabel: (fdsName: string) => void;
}

const DataSetLabelsContext = createContext<IDataSetLabelsContextValue | null>(
	null
);

const views = {
	contentRenderer: 'table',
	label: Liferay.Language.get('table'),
	name: 'table',
	schema: {
		fields: [
			{
				contentRenderer: 'dataSetNameRenderer',
				fieldName: 'dataSetName',
				label: Liferay.Language.get('data-set'),
			},
			{fieldName: 'label', label: Liferay.Language.get('user-view-name')},
			{
				fieldName: 'creator.name',
				label: Liferay.Language.get('created-by'),
			},
			{
				contentRenderer: 'dateTime',
				fieldName: 'dateModified',
				label: Liferay.Language.get('modified-date'),
			},
		],
	},
};

const DataSetNameRenderer = ({itemData}: {itemData: {fdsName?: string}}) => {
	const context = useContext(DataSetLabelsContext);
	const fdsName = itemData.fdsName || '';
	const cachedLabel =
		fdsName && context ? context.dataSetLabels[fdsName] : undefined;

	useEffect(() => {
		if (!context || !fdsName || cachedLabel !== undefined) {
			return;
		}

		context.requestLabel(fdsName);
	}, [cachedLabel, context, fdsName]);

	if (!fdsName) {
		return '';
	}

	if (cachedLabel === undefined) {
		return fdsName;
	}

	return cachedLabel || fdsName;
};

export default function ManageUserViews({
	currentURL,
	fdsEntryFDSSelectionFilterItemsDataProviderURL,
	namespace,
	portletId,
	systemDataSetEntries = [],
}: IManageUserViewsProps) {
	const apiURL = getDataSetSnapshotResourceURL();

	const initialSystemDataSetLabels = useMemo(() => {
		return systemDataSetEntries.reduce<Record<string, string>>(
			(accumulator, entry) => {
				if (entry?.name && entry?.title) {
					accumulator[entry.name] = entry.title;
				}

				return accumulator;
			},
			{}
		);
	}, [systemDataSetEntries]);

	const [dataSetLabels, setDataSetLabels] = useState<Record<string, string>>(
		() => initialSystemDataSetLabels
	);
	const labelRequestsRef = useRef<Set<string>>(new Set());

	const deleteUserViews = useCallback(
		({
			loadData,
			snapshotERCs,
		}: {
			loadData: Function;
			snapshotERCs: Array<string>;
		}) => {
			openModal({
				bodyHTML: Liferay.Language.get(
					'deleting-a-user-view-is-an-action-that-cannot-be-reversed'
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
						onClick: async ({
							processClose,
						}: {
							processClose: Function;
						}) => {
							processClose();

							const responses = await Promise.allSettled(
								snapshotERCs.map((snapshotERC) =>
									fetch(
										getDataSetSnapshotResourceURL({
											snapshotERC,
										}),
										{
											headers: DEFAULT_FETCH_HEADERS,
											method: 'DELETE',
										}
									)
								)
							);

							if (
								responses.every(
									(response) =>
										response.status === 'fulfilled' &&
										response.value.ok
								)
							) {
								openDefaultSuccessToast();

								loadData();

								return;
							}

							openDefaultFailureToast();
						},
					},
				],
				status: 'danger',
				title: Liferay.Language.get('delete-user-view'),
			});
		},
		[]
	);

	const requestLabel = useCallback(
		(fdsName: string) => {
			if (dataSetLabels[fdsName] !== undefined) {
				return;
			}

			if (labelRequestsRef.current.has(fdsName)) {
				return;
			}

			labelRequestsRef.current.add(fdsName);

			const url = getDataSetResourceURL({
				dataSetERC: fdsName,
				params: {fields: 'label'},
			});

			fetch(url, {headers: DEFAULT_FETCH_HEADERS})
				.then((response) => (response.ok ? response.json() : null))
				.then((responseJSON) => {
					const label = responseJSON?.label || '';

					setDataSetLabels((previous) => ({
						...previous,
						[fdsName]: label,
					}));
				})
				.catch(() => {
					setDataSetLabels((previous) => ({
						...previous,
						[fdsName]: '',
					}));
				});
		},
		[dataSetLabels]
	);

	const filters = useMemo(
		() => [
			{
				apiURL: '/o/headless-admin-user/v1.0/user-accounts?sort=name:asc',
				autocompleteEnabled: true,
				entityFieldType: 'integer',
				id: 'creatorId',
				inputPlaceholder: Liferay.Language.get('search'),
				itemKey: 'id',
				itemLabel: 'name',
				items: [],
				label: Liferay.Language.get('created-by'),
				multiple: true,
				type: 'selection',
			},
			{
				apiURL: fdsEntryFDSSelectionFilterItemsDataProviderURL,
				autocompleteEnabled: true,
				entityFieldType: 'string',
				id: 'fdsName',
				inputPlaceholder: Liferay.Language.get('search'),
				itemKey: 'itemKey',
				itemLabel: 'itemLabel',
				items: [],
				label: Liferay.Language.get('data-set'),
				multiple: true,
				type: 'selection',
			},
			{
				entityFieldType: 'date-time',
				id: 'dateModified',
				label: Liferay.Language.get('date'),
				type: 'dateRange',
			},
		],
		[fdsEntryFDSSelectionFilterItemsDataProviderURL]
	);

	return (
		<DataSetLabelsContext.Provider
			value={{
				dataSetLabels,
				requestLabel,
			}}
		>
			<div className="data-sets manage-user-views">
				<FrontendDataSet
					{...FDS_DEFAULT_PROPS}
					apiURL={apiURL}
					bulkActions={[
						{
							href: getDataSetSnapshotResourceURL(),
							icon: 'trash',
							label: Liferay.Language.get('delete'),
							method: 'delete',
						},
					]}
					currentURL={currentURL}
					customDataRenderers={{
						dataSetNameRenderer: DataSetNameRenderer,
					}}
					emptyState={{
						description: Liferay.Language.get(
							'no-user-views-were-found'
						),
						symbol: 'empty-state',
						title: Liferay.Language.get('no-results-found'),
					}}
					filters={filters}
					id={`${namespace}ManageUserViews`}
					itemsActions={[
						{
							className: 'component-action',
							data: {
								confirmationMessage: Liferay.Language.get(
									'deleting-a-user-view-is-an-action-that-cannot-be-reversed'
								),
								errorMessage: Liferay.Language.get(
									'an-unexpected-error-occurred'
								),
								status: 'danger',
								successMessage: Liferay.Language.get(
									'view-was-deleted-successfully'
								),
								title: Liferay.Language.get('delete-user-view'),
							},
							href: '{actions.delete.href}',
							icon: 'trash',
							isVisible: (item) => !!item.actions?.delete?.href,
							label: Liferay.Language.get('delete'),
							method: 'delete',
							target: 'headless',
						},
					]}
					onBulkActionItemClick={({
						loadData,
						selectedData: {keyValues},
					}: {
						loadData: Function;
						selectedData: {keyValues: Array<string>};
					}) => {
						deleteUserViews({
							loadData,
							snapshotERCs: keyValues,
						});
					}}
					portletId={portletId}
					selectedItemsKey="externalReferenceCode"
					selectionType="multiple"
					sorts={[
						{
							direction: 'asc',
							key: 'fdsName',
							label: Liferay.Language.get('data-set'),
						},
						{
							direction: 'asc',
							key: 'creator',
							label: Liferay.Language.get('created-by'),
						},
						{
							direction: 'asc',
							key: 'dateModified',
							label: Liferay.Language.get('modified-date'),
						},
					]}
					style="fluid"
					views={[views]}
				/>
			</div>
		</DataSetLabelsContext.Provider>
	);
}
