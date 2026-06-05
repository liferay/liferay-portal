/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import React from 'react';

const LEVEL_DISPLAY_TYPES: Record<
	string,
	'danger' | 'warning' | 'info' | 'secondary'
> = {
	critical: 'danger',
	high: 'warning',
	low: 'secondary',
	medium: 'info',
};

interface LevelValue {
	key: string;
	name: string;
}

export default function LevelTableCell({value}: {value: LevelValue | null}) {
	if (!value) {
		return null;
	}

	return (
		<ClayLabel displayType={LEVEL_DISPLAY_TYPES[value.key] ?? 'secondary'}>
			{value.name}
		</ClayLabel>
	);
}
