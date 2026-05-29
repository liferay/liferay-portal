/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Icon from '@clayui/icon';
import React from 'react';

interface CardProps {
	label: string;
	symbol: string;
	value: string;
}

export default function Card({label, symbol, value}: CardProps) {
	return (
		<div className="card h-100">
			<div className="align-items-start card-body d-flex justify-content-between">
				<div>
					<p className="mb-2 text-secondary">{label}</p>

					<p className="h2 mb-0">{value}</p>
				</div>

				<span className="sticker sticker-primary">
					<Icon symbol={symbol} />
				</span>
			</div>
		</div>
	);
}
