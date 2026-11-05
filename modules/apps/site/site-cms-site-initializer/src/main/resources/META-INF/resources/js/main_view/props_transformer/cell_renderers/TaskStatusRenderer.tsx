/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import React from 'react';

const TaskStatusRenderer = ({
	itemData,
}: {
	itemData: {embedded?: {completed?: boolean}};
}) => {
	const completed = itemData.embedded?.completed;

	return (
		<ClayLabel displayType={completed ? 'success' : 'info'} inverse>
			{completed
				? Liferay.Language.get('completed')
				: Liferay.Language.get('pending')}
		</ClayLabel>
	);
};

export default TaskStatusRenderer;
