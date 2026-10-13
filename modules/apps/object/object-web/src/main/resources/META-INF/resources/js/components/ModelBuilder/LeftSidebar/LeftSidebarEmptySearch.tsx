/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import React from 'react';

export function LeftSidebarEmptySearch() {
	return (
		<div className="lfr-objects__model-builder-left-sidebar-empty-search">
			<ClayEmptyState
				description={Liferay.Language.get(
					'try-again-with-a-different-search'
				)}
				image={
					<ClayIcon
						className="c-empty-state-icon c-empty-state-icon-search-state"
						illustration
						symbol="search-state"
					/>
				}
				small
				title={Liferay.Language.get('no-results-found')}
			/>
		</div>
	);
}
