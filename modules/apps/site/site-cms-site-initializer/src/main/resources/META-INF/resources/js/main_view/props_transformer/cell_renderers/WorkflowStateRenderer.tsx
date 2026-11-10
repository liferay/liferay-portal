/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import StateLabel from '../../../common/components/StateLabel';

const WorkflowStateRenderer = ({
	itemData,
}: {
	itemData: {embedded?: {completed?: boolean}};
}) => {
	return StateLabel({
		state: itemData.embedded?.completed
			? {
					key: 'completed',
					name: Liferay.Language.get('completed'),
				}
			: {
					key: 'pending',
					name: Liferay.Language.get('pending'),
				},
	});
};

export default WorkflowStateRenderer;
