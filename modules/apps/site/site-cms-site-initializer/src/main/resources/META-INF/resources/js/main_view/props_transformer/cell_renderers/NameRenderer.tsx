/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

const NameRenderer = ({
	itemData,
	value,
}: {
	itemData: {system: boolean};
	value: string;
}) => {
	return (
		<>
			{value}

			{!!itemData.system && (
				<ClayIcon className="c-ml-2 text-secondary" symbol="lock" />
			)}
		</>
	);
};

export default NameRenderer;
