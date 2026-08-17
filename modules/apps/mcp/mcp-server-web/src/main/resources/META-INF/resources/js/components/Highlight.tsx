/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

interface IHighlightProps {
	query: string;
	text: string;
}

export default function Highlight({query, text}: IHighlightProps) {
	const index = query ? text.toLowerCase().indexOf(query.toLowerCase()) : -1;

	if (index < 0) {
		return <>{text}</>;
	}

	return (
		<>
			{text.substring(0, index)}

			<mark className="bg-transparent border-0 font-weight-bold p-0 shadow-none">
				{text.substring(index, index + query.length)}
			</mark>

			{text.substring(index + query.length)}
		</>
	);
}
