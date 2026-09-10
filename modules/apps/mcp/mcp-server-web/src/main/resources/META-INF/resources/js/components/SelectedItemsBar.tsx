/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayResultsBar} from '@clayui/management-toolbar';
import React from 'react';

interface SelectedItemsBarProps {
	count: number;
	onDeselectAll: () => void;
}

export default function SelectedItemsBar({
	count,
	onDeselectAll,
}: SelectedItemsBarProps) {
	return (
		<ClayResultsBar>
			<ClayResultsBar.Item expand>
				<span
					className="component-text text-truncate-inline"
					role="status"
				>
					<span className="text-truncate">
						{count
							? Liferay.Util.sub(
									count === 1
										? Liferay.Language.get(
												'x-item-selected'
											)
										: Liferay.Language.get(
												'x-items-selected'
											),
									count
								)
							: Liferay.Language.get('nothing-selected')}
					</span>
				</span>
			</ClayResultsBar.Item>

			{!!count && (
				<ClayResultsBar.Item>
					<ClayButton
						className="component-link tbar-link"
						displayType="unstyled"
						onClick={onDeselectAll}
					>
						{Liferay.Language.get('deselect-all')}
					</ClayButton>
				</ClayResultsBar.Item>
			)}
		</ClayResultsBar>
	);
}
