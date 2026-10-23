/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import React from 'react';

interface AttachmentDataRendererProps {
	value?: {
		link?: {
			href: string;
			label: string;
		};
		size?: string;
	};
}

export default function AttachmentDataRenderer({
	value,
}: AttachmentDataRendererProps) {
	if (!value?.link) {
		return null;
	}

	return (
		<ClayLink className="table-list-link" href={value.link.href}>
			{value.link.label}

			{value.size && <span className="d-block">{value.size}</span>}
		</ClayLink>
	);
}
