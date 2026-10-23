/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBadge from '@clayui/badge';
import {sub} from 'frontend-js-web';
import React from 'react';

interface CountBadgeProps {
	count: number;
	id: string;
	selected?: boolean;
}

export default function CountBadge({
	count,
	id,
	selected = false,
}: CountBadgeProps) {
	return (
		<span id={id}>
			<ClayBadge
				aria-hidden="true"
				displayType={selected ? 'primary' : 'secondary'}
				label={count}
			/>

			<span className="sr-only">
				{sub(
					count === 1
						? Liferay.Language.get('x-item')
						: Liferay.Language.get('x-items'),
					count
				)}
			</span>
		</span>
	);
}
