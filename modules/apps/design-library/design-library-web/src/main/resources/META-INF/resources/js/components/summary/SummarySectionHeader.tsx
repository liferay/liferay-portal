/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import React from 'react';

interface SummarySectionHeaderProps {
	actionLabel?: string;
	count?: number;
	onActionClick?: () => void;
	title: string;
}

export default function SummarySectionHeader({
	actionLabel,
	count,
	onActionClick,
	title,
}: SummarySectionHeaderProps) {
	return (
		<div className="align-items-center d-flex justify-content-between mb-3">
			<h2 className="font-weight-semi-bold m-0 text-4">
				{typeof count === 'number'
					? Liferay.Util.sub(
							Liferay.Language.get('x-z'),
							title,
							String(count)
						)
					: title}
			</h2>

			{actionLabel && onActionClick && (
				<ClayButton
					className="text-3 text-weight-semi-bold"
					displayType="link"
					onClick={onActionClick}
					size="sm"
				>
					{actionLabel}
				</ClayButton>
			)}
		</div>
	);
}
