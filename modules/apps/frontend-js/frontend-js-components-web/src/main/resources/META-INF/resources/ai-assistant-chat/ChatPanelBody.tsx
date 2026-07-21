/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

export default function ChatPanelBody({
	children,
	className,
	...otherProps
}: React.HTMLAttributes<HTMLDivElement>) {
	return (
		<div
			{...otherProps}
			className={classNames('chat-panel-body c-pt-2', className)}
		>
			{children}
		</div>
	);
}
