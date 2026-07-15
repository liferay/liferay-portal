/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer, IItemsActions} from '@liferay/frontend-data-set-web';
import {openModal, openToast} from 'frontend-js-components-web';
import {sessionStorage, sub} from 'frontend-js-web';

import RoomService from '../../common/services/RoomService';
import {openFDSDeleteConfirmationModal} from '../../common/utils/openModalUtil';
import {ROOM_STATUS} from '../../common/utils/roomStatus';
import {
	displayErrorToast,
	displaySuccessToast,
} from '../../common/utils/toastUtil';
import {IRoomObjectEntry} from '../../common/utils/types';
import DuplicateRoom from '../DuplicateRoom';
import RoomInitializer from '../RoomInitializer';
import RoomShare from '../RoomShare';
import RoomNameRenderer from './cell_renderers/RoomNameRenderer';
import RoomStatusFieldRenderer from './cell_renderers/RoomStatusFieldRenderer';
import RoomStatusRenderer from './cell_renderers/RoomStatusRenderer';
import RoomTrendRenderer from './cell_renderers/RoomTrendRenderer';

export default function RoomsFDSPropsTransformer({
	additionalProps,
	creationMenu,
	itemsActions,
	...otherProps
}: {
	additionalProps: any;
	creationMenu: any;
	itemsActions: IItemsActions[];
	otherProps: any;
}) {
	const successMessage = sessionStorage.getItem(
		'com.liferay.site.dsr.site.initializer.roomSettingsSuccessMessage',
		sessionStorage.TYPES.NECESSARY
	);

	if (successMessage) {
		sessionStorage.removeItem(
			'com.liferay.site.dsr.site.initializer.roomSettingsSuccessMessage'
		);

		openToast({message: successMessage, type: 'success'});
	}

	return {
		...otherProps,
		creationMenu: {
			...creationMenu,
			primaryItems: creationMenu?.primaryItems?.map(
				(item: {data?: {action?: string}}) => {
					return {
						...item,
						onClick() {
							const action = item?.data?.action;

							if (action === 'createDigitalSalesRoom') {
								return openModal({
									containerProps: {
										className: '',
									},
									contentComponent: ({
										closeModal,
									}: {
										closeModal: () => void;
									}) =>
										RoomInitializer({
											closeModal,
											createRedirectURL:
												additionalProps.createRedirectURL ||
												'',
											numberOfSteps: 3,
											siteTemplates:
												additionalProps.siteTemplates ||
												[],
										}),
									size: 'md',
								});
							}
						},
					};
				}
			),
		},
		customRenderers: {
			tableCell: [
				{
					component: RoomNameRenderer,
					name: 'roomNameTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: RoomStatusRenderer,
					name: 'roomStatusTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: RoomStatusFieldRenderer,
					name: 'roomStatusFieldTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: RoomTrendRenderer,
					name: 'roomTrendTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		filters: [
			{
				entityFieldType: 'integer',
				id: 'roomStatus',
				items: [
					{
						label: Liferay.Language.get('active'),
						value: ROOM_STATUS.ACTIVE,
					},
					{
						label: Liferay.Language.get('archived'),
						value: ROOM_STATUS.INACTIVE,
					},
				],
				label: Liferay.Language.get('status'),
				multiple: false,
				preloadedData: {
					exclude: false,
					selectedItems: [
						{
							label: Liferay.Language.get('active'),
							value: ROOM_STATUS.ACTIVE,
						},
					],
				},
				type: 'selection',
			},
		],
		itemsActions: itemsActions.map((action) => {
			const id = action?.data?.id;

			if (id === 'archive' || id === 'edit' || id === 'settings') {
				return {
					...action,
					isVisible: (item: IRoomObjectEntry) =>
						item?.roomStatus === ROOM_STATUS.ACTIVE,
				};
			}

			if (id === 'delete') {
				return {
					...action,
					className: 'text-danger',
				};
			}

			if (id === 'restore') {
				return {
					...action,
					isVisible: (item: IRoomObjectEntry) =>
						item?.roomStatus === ROOM_STATUS.INACTIVE,
				};
			}

			if (id === 'view') {
				return {
					...action,
					isVisible: (item: IRoomObjectEntry) =>
						item?.roomStatus === ROOM_STATUS.ACTIVE ||
						additionalProps.companyAdmin ||
						additionalProps.ownedSiteIds?.includes(
							String(item?.siteId)
						),
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
			itemData: IRoomObjectEntry;
			loadData: any;
		}) {
			if (action.data.id === 'archive') {
				event?.preventDefault();

				openModal({
					bodyHTML: Liferay.Language.get(
						'archive-digital-sales-room-confirmation-body'
					),
					buttons: [
						{
							autoFocus: true,
							displayType: 'secondary',
							label: Liferay.Language.get('cancel'),
							type: 'cancel',
						},
						{
							displayType: 'warning',
							label: Liferay.Language.get('archive'),
							onClick: ({
								processClose,
							}: {
								processClose: () => void;
							}) => {
								RoomService.archiveRoom(itemData.id)
									.then(() => {
										processClose();

										displaySuccessToast();

										loadData();
									})
									.catch(() => {
										displayErrorToast();
									});
							},
						},
					],
					containerProps: {
						className: '',
					},
					status: 'warning',
					title: sub(
						Liferay.Language.get('archive-x'),
						'"' + itemData.name + '"'
					),
				});
			}
			else if (action.data.id === 'delete') {
				event?.preventDefault();

				openFDSDeleteConfirmationModal({
					bodyHTML: Liferay.Language.get(
						'delete-digital-sales-room-confirmation-body'
					),
					itemName: itemData.name,
					loadData,
					title: Liferay.Language.get(
						'delete-digital-sales-room-confirmation-title'
					),
					url: itemData.actions?.delete?.href,
				});
			}
			else if (action.data.id === 'duplicate') {
				event?.preventDefault();

				openModal({
					containerProps: {
						className: '',
					},
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						DuplicateRoom({
							closeModal,
							loadData,
							name: itemData.name,
							roomId: itemData.id,
							siteId: itemData.siteId,
						}),
					size: 'lg',
				});
			}
			else if (action.data.id === 'restore') {
				event?.preventDefault();

				RoomService.restoreRoom(itemData.id)
					.then(() => {
						displaySuccessToast();

						loadData();
					})
					.catch(() => {
						displayErrorToast();
					});
			}
			else if (action.data.id === 'share') {
				event?.preventDefault();

				openModal({
					containerProps: {
						className: '',
					},
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						RoomShare({
							closeModal,
							readOnly:
								itemData.roomStatus === ROOM_STATUS.INACTIVE &&
								!additionalProps.companyAdmin,
							roomId: itemData.id,
						}),
					size: 'lg',
				});
			}
		},
	};
}
