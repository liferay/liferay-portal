/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

export default function OmniSearchResultHeader({
	icon,
	label,
}: {
	icon: string;
	label: string;
}) {
	return (
		<div className="omni-search-section-header text-secondary">
			<ClayIcon className="c-mr-2" symbol={icon} />

			<span className="text-uppercase">{label}</span>
		</div>
	);
}
