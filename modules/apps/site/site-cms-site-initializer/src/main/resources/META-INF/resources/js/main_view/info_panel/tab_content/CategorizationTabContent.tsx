/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext} from 'react';

import AssetCategorization from '../components/AssetCategorization';
import {AssetTypeInfoPanelContext} from '../context';

const CategorizationTabContent = () => {
	const {cmsGroupId, objectEntries = []} = useContext(
		AssetTypeInfoPanelContext
	);

	const [
		{
			actions: {get, update},
		},
	] = objectEntries;

	if (!cmsGroupId) {
		return null;
	}

	return (
		<AssetCategorization
			cmsGroupId={cmsGroupId}
			getObjectEntryURL={get.href}
			updateObjectEntryURL={update.href}
		/>
	);
};

export default CategorizationTabContent;
