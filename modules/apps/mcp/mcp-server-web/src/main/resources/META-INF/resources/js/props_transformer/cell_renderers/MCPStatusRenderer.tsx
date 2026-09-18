/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LabelRenderer} from '@liferay/frontend-data-set-web';
import React from 'react';

const DISPLAY_TYPES: Record<string, 'danger' | 'success'> = {
	active: 'success',
	inactive: 'danger',
};

export default function MCPStatusRenderer({
	value,
}: {
	value?: {key?: string; name?: string};
}) {
	if (!value) {
		return null;
	}

	return (
		<LabelRenderer
			value={{
				displayStyle: DISPLAY_TYPES[value.key ?? ''] ?? 'secondary',
				label: value.name,
			}}
		/>
	);
}
