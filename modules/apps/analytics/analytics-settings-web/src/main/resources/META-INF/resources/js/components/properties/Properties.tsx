/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';
import React, {useState} from 'react';

import {fetchProperties} from '../../utils/api';
import {OrderBy} from '../../utils/filter';
import TableContext, {Events, useData, useDispatch} from '../table/Context';
import {Table} from '../table/Table';
import {EColumnAlign, TColumn} from '../table/types';
import AssignModal from './AssignModal';
import CreatePropertyModal from './CreatePropertyModal';

export type TDataSource = {
	dataSourceId?: string;
	siteIds: number[];
};

export type TProperty = {
	channelId: string;
	dataSources: TDataSource[] | [];
	name: string;
};

enum EColumn {
	AssignButton = 'assignButton',
	CreateDate = 'createDate',
	Name = 'name',
	SiteIds = 'siteIds',
}

const columns: TColumn[] = [
	{
		expanded: true,
		id: EColumn.Name,
		label: Liferay.Language.get('available-properties'),
	},
	{
		align: EColumnAlign.Right,
		id: EColumn.SiteIds,
		label: Liferay.Language.get('sites'),
		sortable: false,
	},
	{
		id: EColumn.CreateDate,
		label: Liferay.Language.get('create-date'),
		show: false,
	},
	{
		align: EColumnAlign.Right,
		id: EColumn.AssignButton,
		label: '',
		sortable: false,
	},
];

const getSafeProperty = (
	property: TProperty
): {
	channelId: string;
	dataSources: TDataSource[];
	name: string;
} => {
	if (property.dataSources.length) {
		return property;
	}

	return {
		...property,
		dataSources: [
			{
				siteIds: [],
			},
		],
	};
};

const Properties: React.FC = () => {
	const {reload} = useData();
	const dispatch = useDispatch();

	const {
		observer: assignModalObserver,
		onOpenChange: onAssignModalOpenChange,
		open: assignModalOpen,
	} = useModal();
	const {
		observer: createPropertyModalObserver,
		onOpenChange: onCreatePropertyModalOpenChange,
		open: createPropertyModalOpen,
	} = useModal();

	const [selectedProperty, setSelectedProperty] = useState<TProperty>();

	return (
		<>
			<Table<TProperty>
				addItemTitle={Liferay.Language.get('create-a-new-property')}
				columns={columns}
				emptyState={{
					contentRenderer: () => (
						<ClayButton
							displayType="secondary"
							onClick={() =>
								onCreatePropertyModalOpenChange(true)
							}
						>
							{Liferay.Language.get('new-property')}
						</ClayButton>
					),
					description: Liferay.Language.get(
						'create-a-property-to-add-sites-and-channels'
					),
					noResultsTitle: Liferay.Language.get(
						'no-properties-were-found'
					),
					title: Liferay.Language.get('create-a-new-property'),
				}}
				mapperItems={(items) =>
					items.map((property) => {
						const safeProperty = getSafeProperty(property);
						const {
							channelId,
							dataSources: [{siteIds}],
							name,
						} = safeProperty;

						return {
							columns: [
								{
									id: EColumn.Name,
									value: name,
								},
								{
									id: EColumn.SiteIds,
									value: siteIds.length,
								},
								{
									id: EColumn.CreateDate,
									value: 'createDate',
								},
								{
									cellRenderer: () => (
										<ClayButton
											displayType="secondary"
											onClick={() => {
												setSelectedProperty(property);
												onAssignModalOpenChange(true);
											}}
											role="assign-button"
										>
											{Liferay.Language.get('assign')}
										</ClayButton>
									),
									id: EColumn.AssignButton,
									value: 'assignButton',
								},
							],
							id: channelId,
						};
					})
				}
				onAddItem={() => onCreatePropertyModalOpenChange(true)}
				requestFn={fetchProperties}
				showCheckbox={false}
				type="properties"
			/>

			{selectedProperty && assignModalOpen && (
				<AssignModal
					observer={assignModalObserver}
					onCancel={() => onAssignModalOpenChange(false)}
					onSubmit={({siteIds}) => {
						Liferay.Util.openToast({
							message: Liferay.Language.get(
								'properties-settings-have-been-saved'
							),
						});

						onAssignModalOpenChange(false);

						dispatch({
							payload: {
								columns: [
									{
										column: {
											value: siteIds.length,
										},
										index: 1,
									},
								],
								id: selectedProperty?.channelId,
							},
							type: Events.ChangeItem,
						});
					}}
					property={getSafeProperty(selectedProperty)}
				/>
			)}

			{createPropertyModalOpen && (
				<CreatePropertyModal
					observer={createPropertyModalObserver}
					onCancel={() => onCreatePropertyModalOpenChange(false)}
					onSubmit={() => {
						Liferay.Util.openToast({
							message: Liferay.Language.get(
								'properties-settings-have-been-saved'
							),
						});

						onCreatePropertyModalOpenChange(false);

						reload();
					}}
				/>
			)}
		</>
	);
};

const PropertiesWrapper = () => (
	<TableContext
		initialFilter={{
			type: OrderBy.Desc,
			value: EColumn.CreateDate,
		}}
	>
		<Properties />
	</TableContext>
);

export default PropertiesWrapper;
