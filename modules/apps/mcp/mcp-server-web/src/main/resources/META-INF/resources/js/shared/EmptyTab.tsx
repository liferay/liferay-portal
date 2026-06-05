/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

interface EmptyTabProps {
	label: string;
}

export function EmptyTab({label}: EmptyTabProps) {
	return (
		<div className="p-4 text-secondary">
			{Liferay.Util.sub(
				Liferay.Language.get('the-x-tab-is-not-implemented-yet'),
				label
			)}
		</div>
	);
}
