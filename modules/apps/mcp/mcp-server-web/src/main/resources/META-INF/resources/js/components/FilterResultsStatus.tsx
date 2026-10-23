/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React from 'react';

interface FilterResultsStatusProps {
	matchCount: number;
	query: string;
}

export default function FilterResultsStatus({
	matchCount,
	query,
}: FilterResultsStatusProps) {
	return (
		<p
			className={query && !matchCount ? 'text-secondary' : 'sr-only'}
			role="status"
		>
			{query &&
				(matchCount
					? sub(
							matchCount === 1
								? Liferay.Language.get('x-result-found')
								: Liferay.Language.get('x-results-found'),
							matchCount
						)
					: Liferay.Language.get('no-results-found'))}
		</p>
	);
}
