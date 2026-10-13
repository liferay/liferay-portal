/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import React from 'react';

import './RightSidebarEmpty.scss';

export function RightSidebarEmpty() {
	return (
		<div className="lfr-objects__model-builder-right-sidebar-empty-state">
			<ClayEmptyState
				description={Liferay.Language.get(
					'select-an-object-or-relationship-to-activate-this-panel'
				)}
				image={
					<ClayIcon
						className="c-empty-state-icon c-empty-state-icon-empty-state"
						illustration
						symbol="empty-state"
					/>
				}
				small
				title={Liferay.Language.get('select-an-object-or-relationship')}
			/>
		</div>
	);
}
