/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	IFrontendDataSetProps,
	IInternalRenderer,
} from '@liferay/frontend-data-set-web';

import {openConnectedSitesModal} from '../actions/breadcrumbActions';
import SiteRenderer from './cell_renderers/SiteRenderer';

interface ConnectedSitesAdditionalProps {
	externalReferenceCode?: string;
	hasConnectSitesPermission?: boolean;
	refreshDataSetIds?: string[];
}

export default function DesignLibraryConnectedSitesFDSPropsTransformer(
	props: IFrontendDataSetProps & {
		additionalProps?: ConnectedSitesAdditionalProps;
	}
): IFrontendDataSetProps {
	const {
		externalReferenceCode = '',
		hasConnectSitesPermission = false,
		refreshDataSetIds,
	} = props.additionalProps ?? {};

	return {
		...props,
		creationMenu: hasConnectSitesPermission
			? {
					primaryItems: [
						{
							label: Liferay.Language.get('connect-sites'),
							onClick: () =>
								openConnectedSitesModal({
									externalReferenceCode,
									refreshDataSetIds,
								}),
						},
					],
				}
			: undefined,
		customRenderers: {
			tableCell: [
				{
					component: SiteRenderer,
					name: 'siteTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		hideManagementBarInEmptyState: true,
		views: [
			{
				contentRenderer: 'table',
				default: true,
				label: Liferay.Language.get('sites'),
				name: 'table',
				schema: {
					fields: [
						{
							contentRenderer: 'siteTableCellRenderer',
							fieldName: 'descriptiveName',
							label: Liferay.Language.get('sites'),
							localizeLabel: true,
						},
					],
				},
				thumbnail: 'table',
			},
		],
	};
}
