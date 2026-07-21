/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import React from 'react';

export interface ChatActionButtonProps {
	ariaLabel: string;
	onClick: () => void;
	symbol: string;
}

export default function ChatActionButton({
	ariaLabel,
	onClick,
	symbol,
}: ChatActionButtonProps) {
	return (
		<ClayButtonWithIcon
			aria-label={ariaLabel}
			borderless
			displayType="secondary"
			onClick={onClick}
			size="sm"
			symbol={symbol}
		/>
	);
}
