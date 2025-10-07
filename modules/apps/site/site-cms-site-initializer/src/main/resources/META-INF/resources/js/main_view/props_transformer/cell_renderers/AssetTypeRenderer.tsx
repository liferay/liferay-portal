/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

const AssetTypeRenderer = ({
	itemData,
	value,
}: {
	itemData: {entryClassName: string};
	value: string;
}) => {
	return (
		<>
			{itemData.entryClassName ===
				'com.liferay.object.model.ObjectEntryFolder' &&
				Liferay.Language.get('folder')}{' '}
			{value}
		</>
	);
};

export default AssetTypeRenderer;
