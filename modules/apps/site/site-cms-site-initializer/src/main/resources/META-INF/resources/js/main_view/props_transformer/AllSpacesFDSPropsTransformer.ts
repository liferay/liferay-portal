/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';
import {openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';

import {defaultPermissionsBulkAction} from '../default_permission/BulkDefaultPermissionModalContent';
import DefaultPermissionModalContent from '../default_permission/DefaultPermissionModalContent';
import {permissionsBulkAction} from '../default_permission/SpacesBulkPermissionModalContent';
import deleteEntryAction from './actions/deleteEntryAction';
import manageConnectedSitesAction, {
	ManageConnectedSitesData,
} from './actions/manageConnectedSitesAction';
import manageMembersAction, {
	ManageMembersData,
} from './actions/manageMembersAction';
import SpaceRenderer from './cell_renderers/SpaceRenderer';
import addOnClickToCreationMenuItems from './utils/addOnClickToCreationMenuItems';

const ACTIONS = {};
const DEPOT_CLASS_NAME = 'com.liferay.depot.model.DepotEntry';

export default function AllSpacesFDSPropsTransformer({
	additionalProps,
	creationMenu,
	itemsActions = [],
	...otherProps
}: {
	additionalProps: any;
	creationMenu: any;
	itemsActions?: any[];
	otherProps: any;
}) {
	return {
		...otherProps,
		creationMenu: {
			...creationMenu,
			primaryItems: addOnClickToCreationMenuItems(
				creationMenu.primaryItems,
				ACTIONS
			),
		},
		customRenderers: {
			tableCell: [
				{
					component: ({itemData, value}) =>
						SpaceRenderer({
							href: additionalProps.baseSpaceURL + itemData.id,
							itemData,
							size: 'sm',
							value,
						}),
					name: 'spaceTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		itemsActions: itemsActions.map((action) => {
			const pinnedAssetLibraryIds = additionalProps.pinnedAssetLibraryIds;

			if (action?.data?.id === 'pin') {
				return {
					...action,
					isVisible: (item: any) =>
						!pinnedAssetLibraryIds?.includes(item.id.toString()),
				};
			}

			if (action?.data?.id === 'unpin') {
				return {
					...action,
					isVisible: (item: any) =>
						!!pinnedAssetLibraryIds?.includes(item.id.toString()),
				};
			}

			return action;
		}),
		onActionDropdownItemClick: ({
			action,
			event,
			itemData,
			loadData,
		}: {
			action: {
				data: {
					id: string;
					permissionKey: string | null;
				};
			};
			event: Event;
			itemData: {
				actions: {
					delete: {href: string; method: string};
				};
				creatorUserId: string;
				externalReferenceCode: string;
				id: string;
				name: string;
				siteId: string;
			};
			loadData: () => {};
		}) => {
			if (action.data.id === 'default-permissions') {
				openModal({
					containerProps: {
						className: '',
					},
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						DefaultPermissionModalContent({
							...(additionalProps.defaultPermissionAdditionalProps ||
								{}),
							apiURL:

								// @ts-ignore

								otherProps?.apiURL ||
								otherProps?.otherProps?.apiURL,
							classExternalReferenceCode:
								itemData.externalReferenceCode,
							className: DEPOT_CLASS_NAME,
							closeModal,
						}),
					size: 'full-screen',
				});
			}
			else if (action.data.id === 'delete') {
				event?.preventDefault();

				deleteEntryAction({
					bodyHTML: Liferay.Language.get(
						'delete-space-confirmation-body'
					),
					deleteAction: itemData.actions.delete,
					loadData,
					successMessage: sub(
						Liferay.Language.get('x-was-successfully-deleted'),
						itemData.name
					),
					title: sub(
						Liferay.Language.get('delete-space-confirmation-title'),
						itemData.name
					),
				});
			}
			else if (action.data.id === 'pin' || action.data.id === 'unpin') {
				window.location.reload();
			}
			else if (action.data.id === 'view-members') {
				const hasAssignMembersPermission =
					action.data.permissionKey === 'assign-members';
				const assetLibraryCreatorUserId = itemData.creatorUserId;
				const externalReferenceCode = itemData.externalReferenceCode;

				const data: ManageMembersData = {
					assetLibraryCreatorUserId,
					externalReferenceCode,
					hasAssignMembersPermission,
					title: Liferay.Language.get('all-members'),
				};

				manageMembersAction(data, loadData);
			}
			else if (action.data.id === 'view-connected-sites') {
				const hasConnectSitesPermission =
					action.data.permissionKey === 'connect-sites';

				const data: ManageConnectedSitesData = {
					externalReferenceCode: itemData.externalReferenceCode,
					hasConnectSitesPermission,
				};

				manageConnectedSitesAction(data, loadData);
			}
		},
		onBulkActionItemClick: ({
			action,
			selectedData,
		}: {
			action: any;
			selectedData: any;
		}) => {
			if (action?.data?.id === 'default-permissions') {
				defaultPermissionsBulkAction({
					apiURL:

						// @ts-ignore

						otherProps?.apiURL || otherProps?.otherProps?.apiURL,
					className: DEPOT_CLASS_NAME,
					defaultPermissionAdditionalProps:
						additionalProps.defaultPermissionAdditionalProps || {},
					selectedData,
				});
			}
			else if (action?.data?.id === 'permissions') {
				permissionsBulkAction({
					apiURL:

						// @ts-ignore

						otherProps?.apiURL || otherProps?.otherProps?.apiURL,
					className: DEPOT_CLASS_NAME,
					selectedData,
					spacePermissionAdditionalProps:
						additionalProps.spacePermissionAdditionalProps || {},
				});
			}
		},
	};
}
