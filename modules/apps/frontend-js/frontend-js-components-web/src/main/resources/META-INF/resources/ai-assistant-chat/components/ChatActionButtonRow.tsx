/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

export interface ChatActionButtonRowProps {
	children: React.ReactNode;
}

export default function ChatActionButtonRow({
	children,
}: ChatActionButtonRowProps) {
	return (
		<div
			className={classNames('c-gapx-2 d-flex flex-row', {
				'border-right c-mr-2 c-pr-2': children,
			})}
		>
			{children}
		</div>
	);
}
