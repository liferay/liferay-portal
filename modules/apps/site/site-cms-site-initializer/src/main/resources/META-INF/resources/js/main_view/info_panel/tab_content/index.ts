/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React from 'react';

import {IAssetFile, IAssetObjectEntry} from '../../../common/types/AssetType';
import {FDS_EVENT_UPDATE_DISPLAY} from '../../../common/utils/constants';
import {openCMSModal} from '../../../common/utils/openCMSModal';
import FilePreviewerModalContent from '../../modal/FilePreviewerModalContent';
import confirmAndDeleteEntryAction from '../../props_transformer/actions/confirmAndDeleteEntryAction';
import {executeAsyncItemAction} from '../../props_transformer/utils/executeAsyncItemAction';
import {
	COPY,
	DELETE_VERSION,
	EXPIRE,
	L_CONTENTS,
	L_FILES,
	RESTORE,
	VIEW_CONTENT_VERSION_URL,
} from '../util/constants';
import CategorizationTabContent from './CategorizationTabContent';
import CommentsTabContent from './CommentsTabContent';
import DetailsTabContent from './DetailsTabContent';
import PerformanceTabContent from './PerformanceTabContent';
import ProjectsTabContent from './ProjectsTabContent';
import VersionsTabContent from './VersionsTabContent';

export const TABS = {
	CATEGORIZATION: {
		component: CategorizationTabContent,
		id: 'categorization',
		name: Liferay.Language.get('categorization'),
	},
	COMMENTS: {
		component: CommentsTabContent,
		id: 'comments',
		name: Liferay.Language.get('comments'),
	},
	DETAILS: {
		component: DetailsTabContent,
		id: 'details',
		name: Liferay.Language.get('details'),
	},
	PERFORMANCE: {
		component: PerformanceTabContent,
		id: 'performance',
		name: Liferay.Language.get('performance'),
	},
	PROJECTS: {
		component: ProjectsTabContent,
		id: 'projects',
		name: Liferay.Language.get('projects'),
	},
	VERSIONS: {
		component: VersionsTabContent,
		id: 'versions',
		name: Liferay.Language.get('versions'),
	},
};

export const VERSION_ACTIONS: any = {
	[COPY]: {
		action: (
			event: React.MouseEvent<HTMLAnchorElement>,
			objectEntry: IAssetObjectEntry,
			refreshData: () => {},
			objectEntryTitle: string,
			dataSetId?: string
		) => {
			event?.preventDefault();

			executeAsyncItemAction({
				method: objectEntry.actions.copy.method,
				refreshData: (responseData) => {
					openToast({
						message: sub(
							Liferay.Language.get(
								'version-x-successfully-copied-as-x'
							),
							objectEntry.systemProperties.version.number,
							`<strong>"${Liferay.Util.escapeHTML(responseData.title)}"</strong>`
						),
						type: 'success',
					});

					if (dataSetId) {
						Liferay.fire(FDS_EVENT_UPDATE_DISPLAY, {
							id: dataSetId,
						});
					}
				},
				showToast: false,
				url: objectEntry.actions.copy.href,
			});
		},
		icon: 'copy',
		name: Liferay.Language.get('make-a-copy'),
	},
	[DELETE_VERSION]: {
		action: (
			event: React.MouseEvent<HTMLAnchorElement>,
			objectEntry: IAssetObjectEntry,
			loadData: () => {},
			objectEntryTitle: string,
			dataSetId?: string
		) => {
			event?.preventDefault();

			confirmAndDeleteEntryAction({
				bodyHTML: sub(
					Liferay.Language.get('delete-version-confirmation'),
					`<strong>${sub(Liferay.Language.get('version-x'), objectEntry.systemProperties.version.number)}</strong>`,
					Liferay.Util.escapeHTML(objectEntryTitle)
				),
				dataSetId,
				deleteAction: objectEntry.actions.delete,
				loadData,
				successMessage: sub(
					Liferay.Language.get('delete-version-success-message'),
					`<strong>${sub(Liferay.Language.get('version-x'), objectEntry.systemProperties.version.number)}</strong>`
				),
				title: sub(
					Liferay.Language.get('delete-version-x'),
					objectEntry.systemProperties.version.number
				),
			});
		},
		icon: 'trash',
		name: Liferay.Language.get('delete'),
	},
	[EXPIRE]: {
		action: (
			event: React.MouseEvent<HTMLAnchorElement>,
			objectEntry: IAssetObjectEntry,
			refreshData: (responseData: any) => {},
			objectEntryTitle: string,
			dataSetId?: string
		) => {
			event?.preventDefault();

			executeAsyncItemAction({
				method: objectEntry.actions.expire.method,
				refreshData: (responseData) => {
					refreshData?.(responseData);

					if (dataSetId) {
						Liferay.fire(FDS_EVENT_UPDATE_DISPLAY, {
							id: dataSetId,
						});
					}
				},
				successMessage: sub(
					Liferay.Language.get('expire-version-success-message'),
					`<strong>${sub(Liferay.Language.get('version-x'), objectEntry.systemProperties.version.number)}</strong>`
				),
				url: objectEntry.actions.expire.href,
			});
		},
		icon: 'time',
		name: Liferay.Language.get('expire'),
	},
	[L_CONTENTS]: {
		action: (
			event: React.MouseEvent<HTMLAnchorElement>,
			objectEntry: IAssetObjectEntry
		) => {
			event?.preventDefault();

			openCMSModal({
				size: 'full-screen',
				title: sub(
					Liferay.Language.get('x-version-x'),
					objectEntry.title,
					`${sub(
						Liferay.Language.get('version-x'),
						objectEntry.systemProperties.version.number
					)}`
				),
				url: `${VIEW_CONTENT_VERSION_URL}/edit_content_item?objectEntryId=${objectEntry.id}&p_l_mode=read&version=${objectEntry.systemProperties.version.number}`,
			});
		},
	},
	[L_FILES]: {
		action: (
			event: React.MouseEvent<HTMLAnchorElement>,
			objectEntry: IAssetObjectEntry,
			file: IAssetFile
		) => {
			event?.preventDefault();

			openCMSModal({
				contentComponent: () =>
					FilePreviewerModalContent({
						file,
						headerName: sub(
							Liferay.Language.get('x-version-x'),
							objectEntry.title,
							`${sub(
								Liferay.Language.get('version-x'),
								objectEntry.systemProperties.version.number
							)}`
						),
					}),
				size: 'full-screen',
			});
		},
	},
	[RESTORE]: {
		action: (
			event: React.MouseEvent<HTMLAnchorElement>,
			objectEntry: IAssetObjectEntry,
			refreshData: (responseData: any) => {},
			objectEntryTitle: string,
			dataSetId?: string
		) => {
			event?.preventDefault();

			executeAsyncItemAction({
				method: objectEntry.actions.restore.method,
				refreshData: (responseData) => {
					refreshData?.(responseData);

					if (dataSetId) {
						Liferay.fire(FDS_EVENT_UPDATE_DISPLAY, {
							id: dataSetId,
						});
					}
				},
				successMessage: sub(
					Liferay.Language.get('restore-version-success-message'),
					`<strong>${sub(Liferay.Language.get('version-x'), objectEntry.systemProperties.version.number)}</strong>`
				),
				url: objectEntry.actions.restore.href,
			});
		},
		icon: 'restore',
		name: Liferay.Language.get('restore-version'),
	},
};
