/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {useId} from 'react';

import {useChatPanelContext} from './ChatPanelContext';

export type ChatPanelProps = React.HTMLAttributes<HTMLDivElement>;

export default function ChatPanel({
	children,
	className,
	...otherProps
}: ChatPanelProps) {
	const {dialogId, titleId} = useChatPanelContext();

	const fallbackId = useId();

	return (
		<div
			{...otherProps}
			aria-labelledby={titleId || undefined}
			className={classNames('c-p-3 chat-panel', className)}
			id={dialogId || fallbackId}
			role="dialog"
			tabIndex={-1}
		>
			{children}
		</div>
	);
}
