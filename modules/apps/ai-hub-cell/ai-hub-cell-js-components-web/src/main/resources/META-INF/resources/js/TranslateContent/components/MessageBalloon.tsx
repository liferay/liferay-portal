/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

export default function MessageBalloon({
	children,
}: {
	children: React.ReactNode;
}) {
	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon">
			{children}
		</div>
	);
}
