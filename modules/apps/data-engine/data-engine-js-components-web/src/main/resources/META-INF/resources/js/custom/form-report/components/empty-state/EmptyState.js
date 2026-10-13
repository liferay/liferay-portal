/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import React from 'react';

export default function EmptyState({
	description = Liferay.Language.get(
		'the-report-will-show-up-once-the-first-entry-is-submitted'
	),
	title = Liferay.Language.get('there-are-no-entries'),
}) {
	return (
		<ClayEmptyState
			description={description}
			image={
				<ClayIcon
					className="c-empty-state-icon c-empty-state-icon-empty-state"
					illustration
					symbol="empty-state"
				/>
			}
			title={title}
		/>
	);
}
