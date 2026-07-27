/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import AIAssistantMessageBalloonIcon from '../../AIAssistantChat/components/AIAssistantMessageBalloonIcon';

export default function MessageHeader({message}: {message: string}) {
	return (
		<div className="ai-assistant-chat__message-header">
			<AIAssistantMessageBalloonIcon />

			<div className="ai-assistant-chat__message-header-text">
				{message}
			</div>
		</div>
	);
}
