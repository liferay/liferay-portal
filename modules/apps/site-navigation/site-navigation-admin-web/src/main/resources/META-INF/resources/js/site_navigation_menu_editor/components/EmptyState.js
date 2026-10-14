/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import React from 'react';

import {AddItemDropDown} from './AddItemDropdown';

export function EmptyState() {
	return (
		<ClayEmptyState
			description={Liferay.Language.get(
				'fortunately-it-is-very-easy-to-add-new-ones'
			)}
			image={
				<ClayIcon
					className="c-empty-state-icon c-empty-state-icon-empty-state"
					illustration
					symbol="empty-state"
				/>
			}
			title={Liferay.Language.get('no-element-yet')}
		>
			<AddItemDropDown
				trigger={
					<ClayButton displayType="secondary">
						{Liferay.Language.get('new')}
					</ClayButton>
				}
			/>
		</ClayEmptyState>
	);
}
