/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import React from 'react';

export default function FieldMappingChannelFieldRenderer({
	itemData,
	value,
}: {
	itemData: any;
	value: string;
}) {
	return (
		<span className="table-list-title">
			<ClayLink href={itemData?.href}>{value}</ClayLink>
		</span>
	);
}
