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
	CRITICAL: 'danger',
	HIGH: 'warning',
	LOW: 'secondary',
	MEDIUM: 'info',
};

function getLabel(value: string): string {
	switch (value) {
		case 'CRITICAL':
			return Liferay.Language.get('critical');
		case 'HIGH':
			return Liferay.Language.get('high');
		case 'LOW':
			return Liferay.Language.get('low');
		case 'MEDIUM':
			return Liferay.Language.get('medium');
		default:
			return value;
	}
}

export default function LevelTableCell({value}: {value: string | null}) {
	if (!value) {
		return null;
	}

	return (
		<ClayLabel displayType={LEVEL_DISPLAY_TYPES[value] ?? 'secondary'}>
			{getLabel(value)}
		</ClayLabel>
	);
}
