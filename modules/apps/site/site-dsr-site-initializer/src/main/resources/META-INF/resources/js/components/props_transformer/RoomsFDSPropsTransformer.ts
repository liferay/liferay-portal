/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer, IItemsActions} from '@liferay/frontend-data-set-web';

import {openFDSDeleteConfirmationModal} from '../../common/utils/openModalUtil';
import {IRoom} from '../../common/utils/types';
import RoomNameRenderer from './cell_renderers/RoomNameRenderer';

export default function RoomsFDSPropsTransformer({
	itemsActions,
	...otherProps
}: {
	itemsActions: IItemsActions[];
	otherProps: any;
}) {
	return {
		...otherProps,
		customRenderers: {
			tableCell: [
				{
					component: ({itemData}: {itemData: IRoom}) =>
						RoomNameRenderer({
							data: itemData.embedded,
						}),
					name: 'roomNameTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		itemsActions: itemsActions.map((action) => {
			if (action?.data?.id === 'delete') {
				return {
					...action,
					className: 'text-danger',
				};
			}

			return action;
		}),
		onActionDropdownItemClick({
			action,
			event,
			itemData,
			loadData,
		}: {
			action: {data: {id: string}};
			event: Event;
			itemData: IRoom;
			loadData: any;
		}) {
			if (action.data.id === 'delete') {
				event?.preventDefault();

				openFDSDeleteConfirmationModal({
					bodyHTML: Liferay.Language.get(
						'delete-digital-sales-room-confirmation-body'
					),
					itemName: itemData.embedded.name,
					loadData,
					title: Liferay.Language.get(
						'delete-digital-sales-room-confirmation-title'
					),
					url: itemData.actions?.delete?.href,
				});
			}
		},
	};
}
