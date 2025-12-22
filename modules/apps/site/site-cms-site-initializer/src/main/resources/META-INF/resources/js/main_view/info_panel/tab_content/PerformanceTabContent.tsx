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
	const {
		asset: {
			externalReferenceCode,
			objectEntryFolderExternalReferenceCode,
			scopeId,
		},
		assetLibrary: {externalReferenceCode: assetLibraryERC = ''} = {},
	} = useContext<IAssetTypeInfoPanelContext>(AssetTypeInfoPanelContext);

	return (
		<CMSPerformance
			externalReferenceCode={externalReferenceCode}
			objectEntryFolderExternalReferenceCode={
				objectEntryFolderExternalReferenceCode
			}
			onConnectSites={(loadData) =>
				manageConnectedSitesAction(
					{
						externalReferenceCode: assetLibraryERC,
						hasConnectSitesPermission: true,
					},
					loadData
				)
			}
			scopeId={scopeId}
		/>
	);
};

export default PerformanceTabContent;
