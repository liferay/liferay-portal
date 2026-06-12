/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import React from 'react';

import {DATA_MASKS_URL} from '../services/dataMasksURL';
import {DATA_SET_ID, FILTERS, SORTS, VIEWS} from './constants';
import {ActionContext, DataMask} from './types';

function isSystemMask(dataMask: DataMask) {
	return dataMask?.maskType?.key === 'system';
}

interface NameLinkProps {
	itemData: DataMask;
	onEdit: (dataMask: DataMask) => void;
	onView: (dataMask: DataMask) => void;
}

function NameLink({itemData, onEdit, onView}: NameLinkProps) {
	return (
		<ClayButton
			className="c-p-0 text-left"
			displayType="link"
			onClick={() =>
				isSystemMask(itemData) ? onView(itemData) : onEdit(itemData)
			}
		>
			{itemData.name}
		</ClayButton>
	);
}

interface DataMaskListProps {
	onCreate: () => void;
	onDelete: (context: ActionContext) => void;
	onDuplicate: (context: ActionContext) => void;
	onEdit: (dataMask: DataMask) => void;
	onView: (dataMask: DataMask) => void;
}

export function DataMaskList({
	onCreate,
	onDelete,
	onDuplicate,
	onEdit,
	onView,
}: DataMaskListProps) {
	return (
		<FrontendDataSet
			apiURL={DATA_MASKS_URL}
			creationMenu={{
				primaryItems: [
					{
						label: Liferay.Language.get('new-data-mask'),
						onClick: onCreate,
					},
				],
			}}
			customRenderers={{
				tableCell: [
					{
						component: (props: {itemData: DataMask}) => (
							<NameLink
								{...props}
								onEdit={onEdit}
								onView={onView}
							/>
						),
						name: 'nameLink',
						type: 'internal',
					},
				],
			}}
			filters={FILTERS}
			id={DATA_SET_ID}
			itemsActions={[
				{
					icon: 'view',
					isVisible: (item: DataMask) => isSystemMask(item),
					label: Liferay.Language.get('view'),
					onClick: ({itemData}: ActionContext) => onView(itemData),
				},
				{
					icon: 'pencil',
					isVisible: (item: DataMask) => !isSystemMask(item),
					label: Liferay.Language.get('edit'),
					onClick: ({itemData}: ActionContext) => onEdit(itemData),
				},
				{
					icon: 'copy',
					label: Liferay.Language.get('duplicate'),
					onClick: onDuplicate,
				},
				{
					icon: 'trash',
					isVisible: (item: DataMask) => !isSystemMask(item),
					label: Liferay.Language.get('delete'),
					onClick: onDelete,
				},
			]}
			selectedItemsKey="id"
			selectionType="multiple"
			sorts={SORTS}
			views={VIEWS}
		/>
	);
}
