/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayResultsBar} from '@clayui/management-toolbar';
import {sub} from 'frontend-js-web';
import React, {useId} from 'react';

interface SelectedItemsBarProps {
	count: number;
	onClear: () => void;
}

export default function SelectedItemsBar({
	count,
	onClear,
}: SelectedItemsBarProps) {
	const statusId = useId();

	return (
		<ClayResultsBar role="none">
			<ClayResultsBar.Item expand>
				<span
					className="component-text text-truncate-inline"
					id={statusId}
					role="status"
				>
					<span className="text-truncate">
						{count
							? sub(
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
						aria-describedby={statusId}
						className="component-link tbar-link"
						displayType="unstyled"
						onClick={onClear}
					>
						{Liferay.Language.get('clear')}
					</ClayButton>
				</ClayResultsBar.Item>
			)}
		</ClayResultsBar>
	);
}
