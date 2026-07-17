/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import React from 'react';

enum SEVERITY {
	HIGH = '3',
	LOW = '1',
	MEDIUM = '2',
}

function getDisplayType(severityKey: string) {
	switch (severityKey) {
		case SEVERITY.HIGH:
			return 'danger';
		case SEVERITY.LOW:
			return 'secondary';
		case SEVERITY.MEDIUM:
			return 'warning';
		default:
			return 'secondary';
	}
}

export default function ImpactCellRenderer({
	itemData,
}: {
	itemData: {severity?: {key?: string; name?: string}};
}) {
	const severity = itemData?.severity;

	if (!severity?.name) {
		return null;
	}

	return (
		<ClayLabel
			displayType={getDisplayType(severity.key ?? '')}
			inverse
			withClose={false}
		>
			{severity.name}
		</ClayLabel>
	);
}
