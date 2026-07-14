/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useState} from 'react';

import {SpaceOption, SpacePicker, initialSpace} from '../common/SpacePicker';

export default function GovernanceDashboard() {
	const [selectedSpace, setSelectedSpace] =
		useState<SpaceOption>(initialSpace);

	return (
		<div className="mb-4">
			<SpacePicker
				onSelectSpace={setSelectedSpace}
				selectedSpace={selectedSpace}
			/>
		</div>
	);
}
