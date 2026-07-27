/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import React from 'react';

interface ContentGapCellActionsProps {
	onFilter: () => void;

	/**
	 * Left out on the uncategorized cells: their assets have no persona or no
	 * funnel stage to generate content for.
	 */
	onGenerate?: () => void;
}

export default function ContentGapCellActions({
	onFilter,
	onGenerate,
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

			{onGenerate && Liferay.FeatureFlags['LPD-62272'] && (
				<ClayButton
					className="lfr-cmp__content-gap-cell-action"
					displayType="unstyled"
					onClick={(event) => {
						event.stopPropagation();

						onGenerate();
					}}
				>
					<ClayIcon
						className="inline-item inline-item-before"
						symbol="stars"
					/>

					{Liferay.Language.get('generate')}
				</ClayButton>
			)}
		</div>
	);
}
