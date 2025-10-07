/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CMSPerformance} from '@liferay/analytics-reports-js-components-web';
import React, {useContext} from 'react';

import manageConnectedSitesAction from '../../props_transformer/actions/manageConnectedSitesAction';
import {
	AssetTypeInfoPanelContext,
	IAssetTypeInfoPanelContext,
} from '../context';

const PerformanceTabContent = () => {
	const selectedAsset = useContext<IAssetTypeInfoPanelContext>(
		AssetTypeInfoPanelContext
	);
	const {embedded} = selectedAsset?.objectEntries?.[0] ?? {};

	return (
		<CMSPerformance
			externalReferenceCode={embedded?.externalReferenceCode}
			objectEntryFolderExternalReferenceCode={
				embedded?.objectEntryFolderExternalReferenceCode
			}
			onConnectSites={(loadData) =>
				manageConnectedSitesAction(
					{
						externalReferenceCode:
							selectedAsset.assetLibrary?.externalReferenceCode ??
							'',
						hasConnectSitesPermission: true,
					},
					loadData
				)
			}
			scopeId={embedded?.scopeId}
		/>
	);
};

export default PerformanceTabContent;
