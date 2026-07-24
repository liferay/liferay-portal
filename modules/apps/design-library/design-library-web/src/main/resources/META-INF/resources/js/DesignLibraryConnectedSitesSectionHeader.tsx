/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {openConnectedSitesModal} from './actions/breadcrumbActions';
import SummarySectionHeader from './components/summary/SummarySectionHeader';

interface DesignLibraryConnectedSitesSectionHeaderProps {
	count: number;
	externalReferenceCode: string;
	hasConnectSitesPermission: boolean;
}

export default function DesignLibraryConnectedSitesSectionHeader({
	count,
	externalReferenceCode,
	hasConnectSitesPermission,
}: DesignLibraryConnectedSitesSectionHeaderProps) {
	return (
		<SummarySectionHeader
			actionLabel={
				hasConnectSitesPermission && count
					? Liferay.Language.get('manage-sites')
					: undefined
			}
			count={count}
			onActionClick={
				hasConnectSitesPermission && count
					? () => openConnectedSitesModal({externalReferenceCode})
					: undefined
			}
			title={Liferay.Language.get('connected-sites')}
		/>
	);
}
