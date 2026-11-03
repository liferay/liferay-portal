/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IFrontendDataSetProps} from '@liferay/frontend-data-set-web';

import {openEditURL} from '../../utils/urlUtil';
import {baseFDSProps, getEmptyState} from './baseFDSProps';
import {getAPIApplicationsFDSFilters} from './fdsFilters';
import {itemStatusRenderer, itemURLRenderer} from './fdsRenderers';

export function getAPIApplicationsFDSProps(
	apiApplicationsURLPath: string,
	editURL: string,
	portletId: string
): IFrontendDataSetProps {
	return {
		...baseFDSProps,
		apiURL: apiApplicationsURLPath,
		customDataRenderers: {
			itemStatusRenderer,
			itemURLRenderer,
		},
		emptyState: getEmptyState(
			Liferay.Language.get('no-api-application-found')
		),
		filters: getAPIApplicationsFDSFilters(),
		id: portletId,
		itemsActions: [
			{
				data: {
					id: 'editAPIApplication',
				},
				icon: 'pencil',
				label: Liferay.Language.get('edit'),
				onClick: ({itemData}: FDSItem<APIApplicationItem>) =>
					openEditURL({editURL, id: itemData.id, portletId}),
			},
			{
				icon: 'trash',
				id: 'deleteAPIApplication',
				label: Liferay.Language.get('delete'),
			},
			{
				icon: 'change',
				id: 'changePublicationStatus',
				label: Liferay.Language.get('change-publication-status'),
			},
		],
		views: [
			{
				contentRenderer: 'table',
				label: 'Table',
				name: 'table',
				schema: {
					fields: [
						{
							actionId: 'editAPIApplication',
							contentRenderer: 'actionLink',
							fieldName: 'title',
							label: Liferay.Language.get('title'),
							localizeLabel: true,
							sortable: true,
						},
						{
							contentRenderer: 'itemURLRenderer',
							expand: false,
							fieldName: 'baseURL',
							label: Liferay.Language.get('url'),
							localizeLabel: true,
						},
						{
							fieldName: 'description',
							label: Liferay.Language.get('description'),
							localizeLabel: true,
							truncate: true,
						},
						{
							contentRenderer: 'dateTime',
							fieldName: 'dateModified',
							label: Liferay.Language.get('last-updated'),
							localizeLabel: true,
							sortable: true,
						},
						{
							contentRenderer: 'itemStatusRenderer',
							fieldName: 'applicationStatus',
							label: Liferay.Language.get('status'),
							sortable: true,
						},
					],
				},
				thumbnail: 'table',
			},
		],
	};
}
