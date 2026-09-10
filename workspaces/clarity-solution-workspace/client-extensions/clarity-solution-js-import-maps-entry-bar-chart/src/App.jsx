/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import LiferayChart from './components/LiferayChart.jsx';

function App({
	title,
	datasetLabel,
	aggregationField,
	aggregationType,
	restContextPath,
	color,
}) {
	return (
		<>
			<LiferayChart
				title={title}
				datasetLabel={datasetLabel}
				aggregationField={aggregationField}
				aggregationType={aggregationType}
				restContextPath={restContextPath}
				color={color}
			/>
		</>
	);
}

export default App;
