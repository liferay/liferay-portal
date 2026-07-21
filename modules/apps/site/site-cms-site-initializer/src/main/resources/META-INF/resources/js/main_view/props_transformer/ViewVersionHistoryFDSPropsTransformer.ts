/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	IBulkActionItem,
	IInternalRenderer,
	replaceTokens,
} from '@liferay/frontend-data-set-web';
import {navigate, sessionStorage, sub} from 'frontend-js-web';

import StatusLabel from '../../common/components/StatusLabel';
import {openCMSModal} from '../../common/utils/openCMSModal';
import FilePreviewerModalContent from '../modal/FilePreviewerModalContent';
import confirmAndDeleteEntryAction from './actions/confirmAndDeleteEntryAction';
import deleteAssetVersionBulkAction from './actions/deleteAssetVersionBulkAction';
import expireEntriesBulkAction from './actions/expireEntriesBulkAction';
import AssetVersionRenderer from './cell_renderers/AssetVersionRenderer';
import AuthorRenderer from './cell_renderers/AuthorRenderer';
import VersionRenderer from './cell_renderers/VersionRenderer';
import {executeAsyncItemAction} from './utils/executeAsyncItemAction';
import transformFDSBulkActions from './utils/transformFDSBulkActions';

export default function ViewVersionHistoryFDSPropsTransformer({
	additionalProps,
	bulkActions = [],
	itemsActions = [],
	...otherProps
}: {
	additionalProps: any;
	apiURL?: string;
	bulkActions: Array<IBulkActionItem>;
	id?: string;
	itemsActions?: any[];
}) {
	return {
		...otherProps,
		bulkActions: transformFDSBulkActions(bulkActions),
		customRenderers: {
			tableCell: [
				{
					component: AuthorRenderer,
					name: 'authorTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: AssetVersionRenderer,
					name: 'assetVersionTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: VersionRenderer,
					name: 'versionTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({value}) => StatusLabel(value),
					name: 'statusTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		hideManagementBarInEmptyState: true,
		itemsActions: itemsActions.map((action) => {
			if (action?.data?.id === 'download') {
				return {
					...action,
					isVisible: (item: any) => Boolean(item?.file?.link?.href),
				};
			}
			else if (action?.data?.id === 'view-content') {
				return {
					...action,
					isVisible: (item: any) => Boolean(!item?.file),
				};
			}
			else if (action?.data?.id === 'view-file') {
				return {
					...action,
					isVisible: (item: any) => Boolean(item?.file?.thumbnailURL),
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
			action: {data: {id: string; successMessage: string}; href: string};
			event: Event;
			itemData: {
				actions: {
					copy: {href: string; method: string};
					delete: {href: string; method: string};
					expire: {href: string; method: string};
					restore: {href: string; method: string};
				};
				file: any;
				systemProperties: {
					version: {
						number: number;
					};
				};
				title: string;
			};
			loadData: () => {};
		}) {
			if (action.data.id === 'addToLaunch') {
				event?.preventDefault();

				Liferay.fire('addToLaunch', {
					className: additionalProps.className,
					classPK: additionalProps.classPK,
					classVersion: String(
						itemData.systemProperties.version.number
					),
				});
			}
			else if (action.data.id === 'copy') {
				event?.preventDefault();

				executeAsyncItemAction({
					method: itemData.actions.copy.method,
					refreshData: (responseData) => {
						sessionStorage.setItem(
							'com.liferay.site.cms.site.initializer.successMessage',
							sub(
								Liferay.Language.get(
									'version-x-successfully-copied-as-x'
								),
								itemData.systemProperties.version.number,
								`<strong>"${responseData.title}"</strong>`
							),
							sessionStorage.TYPES.NECESSARY
						);

						navigate(additionalProps.backURL);
					},
					showToast: false,
					url: itemData.actions.copy.href,
				});
			}
			else if (action.data.id === 'delete') {
				event?.preventDefault();

				confirmAndDeleteEntryAction({
					bodyHTML: sub(
						Liferay.Language.get('delete-version-confirmation'),
						`<strong>${sub(Liferay.Language.get('version-x'), itemData.systemProperties.version.number)}</strong>`,
						additionalProps.objectEntryTitle
					),
					deleteAction: itemData.actions.delete,
					loadData,
					successMessage: sub(
						Liferay.Language.get('delete-version-success-message'),
						`<strong>${sub(Liferay.Language.get('version-x'), itemData.systemProperties.version.number)}</strong>`
					),
					title: sub(
						Liferay.Language.get('delete-version-x'),
						itemData.systemProperties.version.number
					),
				});
			}
			else if (action.data.id === 'expire') {
				event?.preventDefault();

				executeAsyncItemAction({
					method: itemData.actions.expire.method,
					refreshData: loadData,
					successMessage: sub(
						Liferay.Language.get('expire-version-success-message'),
						`<strong>${sub(Liferay.Language.get('version-x'), itemData.systemProperties.version.number)}</strong>`
					),
					url: itemData.actions.expire.href,
				});
			}
			else if (action.data.id === 'restore') {
				event?.preventDefault();

				executeAsyncItemAction({
					method: itemData.actions.restore.method,
					refreshData: loadData,
					successMessage: sub(
						Liferay.Language.get('restore-version-success-message'),
						`<strong>${sub(Liferay.Language.get('version-x'), itemData.systemProperties.version.number)}</strong>`
					),
					url: itemData.actions.restore.href,
				});
			}
			else if (action?.data?.id === 'view-content') {
				event?.preventDefault();

				openCMSModal({
					size: 'full-screen',
					title: sub(
						Liferay.Language.get('x-version-x'),
						itemData.title,
						`${sub(
							Liferay.Language.get('version-x'),
							itemData.systemProperties.version.number
						)}`
					),
					url: replaceTokens(action.href, itemData),
				});
			}
			else if (action?.data?.id === 'view-file') {
				openCMSModal({
					contentComponent: () =>
						FilePreviewerModalContent({
							file: itemData.file,
							headerName: sub(
								Liferay.Language.get('x-version-x'),
								itemData.title,
								`${sub(
									Liferay.Language.get('version-x'),
									itemData.systemProperties.version.number
								)}`
							),
						}),
					size: 'full-screen',
				});
			}
		},
		onBulkActionItemClick: async ({
			action,
			selectedData,
		}: {
			action: any;
			selectedData: any;
		}) => {
			if (action?.data.id === 'delete') {
				deleteAssetVersionBulkAction({
					apiURL: otherProps.apiURL,
					className: additionalProps.className,
					classPK: additionalProps.classPK,
					dataSetId: otherProps.id,
					entryClassName: additionalProps.entryClassName,
					objectEntryCurrentVersion:
						additionalProps.objectEntryCurrentVersion,
					objectEntryTitle: additionalProps.objectEntryTitle,
					objectEntryVersionsCount:
						additionalProps.objectEntryVersionsCount,
					selectedData,
				});
			}
			else if (action?.data.id === 'expire') {
				expireEntriesBulkAction({
					apiURL: otherProps.apiURL,
					dataSetId: otherProps.id,
					selectedData,
				});
			}
		},
	};
}
