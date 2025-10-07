/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';
import {openModal} from 'frontend-js-components-web';
import {navigate, sessionStorage, sub} from 'frontend-js-web';

import formatActionURL from '../../common/utils/formatActionURL';
import FilePreviewerModalContent from '../modal/FilePreviewerModalContent';
import deleteEntryAction from './actions/deleteEntryAction';
import AuthorRenderer from './cell_renderers/AuthorRenderer';
import NameRenderer from './cell_renderers/NameRenderer';
import VersionRenderer from './cell_renderers/VersionRenderer';
import {executeAsyncItemAction} from './utils/executeAsyncItemAction';

export default function ViewVersionHistoryFDSPropsTransformer({
	additionalProps,
	itemsActions = [],
	...otherProps
}: {
	additionalProps: any;
	itemsActions?: any[];
}) {
	return {
		...otherProps,
		customRenderers: {
			tableCell: [
				{
					component: AuthorRenderer,
					name: 'authorTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: NameRenderer,
					name: 'nameTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: VersionRenderer,
					name: 'versionTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
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
					isVisible: (item: any) => Boolean(!item?.file?.link?.href),
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
			if (action.data.id === 'copy') {
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

				deleteEntryAction({
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

				openModal({
					containerProps: {
						className: '',
					},
					size: 'full-screen',
					title: sub(
						Liferay.Language.get('x-version-x'),
						itemData.title,
						`${sub(
							Liferay.Language.get('version-x'),
							itemData.systemProperties.version.number
						)}`
					),
					url: formatActionURL(itemData, action.href),
				});
			}
			else if (action?.data?.id === 'view-file') {
				openModal({
					containerProps: {
						className: '',
					},
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
	};
}
