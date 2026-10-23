/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import React from 'react';

const MAPPING_STATUSES = {
	complete: {
		displayType: 'success',
		label: Liferay.Language.get('status-complete'),
	},
	incomplete: {
		displayType: 'warning',
		label: Liferay.Language.get('incomplete'),
	},
} as const;

export default function MappingStatusRenderer({
	value,
}: {
	value: keyof typeof MAPPING_STATUSES;
}) {
	const {displayType, label} =
		MAPPING_STATUSES[value] ?? MAPPING_STATUSES.incomplete;

	return (
		<ClayLabel className="font-weight-normal" displayType={displayType}>
			{label}
		</ClayLabel>
	);
}
