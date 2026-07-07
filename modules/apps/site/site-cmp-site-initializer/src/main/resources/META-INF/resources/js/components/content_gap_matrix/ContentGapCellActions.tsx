/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import React from 'react';

interface ContentGapCellActionsProps {
	onFilter: () => void;
}

export default function ContentGapCellActions({
	onFilter,
}: ContentGapCellActionsProps) {
	return (
		<div className="lfr-cmp__content-gap-cell-actions">
			<ClayButton
				className="lfr-cmp__content-gap-cell-action"
				displayType="unstyled"
				onClick={(event) => {
					event.stopPropagation();

					onFilter();
				}}
			>
				<ClayIcon
					className="inline-item inline-item-before"
					symbol="filter"
				/>

				{Liferay.Language.get('filter')}
			</ClayButton>

			{/* Placeholder for the future Generate action */}

			<ClayButton
				className="lfr-cmp__content-gap-cell-action"
				disabled
				displayType="unstyled"
			>
				<ClayIcon
					className="inline-item inline-item-before"
					symbol="stars"
				/>

				{Liferay.Language.get('generate')}
			</ClayButton>
		</div>
	);
}
