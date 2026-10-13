/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import React from 'react';

import {sub} from '../../utils/utils';

export default function EmptyPlaceholder() {
	return (
		<div className="c-mb-0 c-p-4 empty-contributors rounded">
			<ClayEmptyState
				description={Liferay.Language.get('empty-conditions-message')}
				image={
					<ClayIcon
						className="c-empty-state-icon c-empty-state-icon-empty-state"
						illustration
						symbol="empty-state"
					/>
				}
				title={sub(Liferay.Language.get('no-x-yet'), [
					Liferay.Language.get('conditions'),
				])}
			></ClayEmptyState>
		</div>
	);
}
