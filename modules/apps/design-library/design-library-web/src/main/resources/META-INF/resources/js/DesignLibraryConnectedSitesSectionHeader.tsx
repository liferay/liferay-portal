/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';
import React from 'react';

import {openConnectedSitesModal} from './actions/breadcrumbActions';
import SummarySectionHeader from './components/summary/SummarySectionHeader';
import useRefreshedCount from './hooks/useRefreshedCount';

interface DesignLibraryConnectedSitesSectionHeaderProps {
	count: number;
	externalReferenceCode: string;
	hasConnectSitesPermission: boolean;
	refreshDataSetIds?: string[];
}

export default function DesignLibraryConnectedSitesSectionHeader({
	count,
	externalReferenceCode,
	hasConnectSitesPermission,
	refreshDataSetIds,
}: DesignLibraryConnectedSitesSectionHeaderProps) {
	const refreshedCount = useRefreshedCount(
		count,
		refreshDataSetIds ?? [],
		() =>
			fetch(
				`/o/headless-asset-library/v1.0/asset-libraries/${externalReferenceCode}/connected-sites?page=1&pageSize=1`
			)
				.then((response) => response.json())
				.then((data) => data.totalCount ?? 0)
	);

	return (
		<SummarySectionHeader
			actionLabel={
				hasConnectSitesPermission && refreshedCount
					? Liferay.Language.get('manage-sites')
					: undefined
			}
			count={refreshedCount}
			onActionClick={
				hasConnectSitesPermission && refreshedCount
					? () =>
							openConnectedSitesModal({
								externalReferenceCode,
								refreshDataSetIds,
							})
					: undefined
			}
			title={Liferay.Language.get('connected-sites')}
		/>
	);
}
