/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Icon from '@clayui/icon';
import React from 'react';

interface IProps {
	spritemap?: string;
	symbol: string;
}

export function AlertIndicator({spritemap, symbol}: IProps) {
	return (
		<span className="alert-indicator">
			<Icon spritemap={spritemap} symbol={symbol} />
		</span>
	);
}
