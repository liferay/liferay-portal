/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

export default function MessageHeader({message}: {message: string}) {
	return (
		<div className="ai-assistant-chat__message-header">
			<div className="ai-assistant-chat__message-header-icon">
				<ClayIcon spritemap={Liferay.Icons.spritemap} symbol="stars" />
			</div>

			<div className="ai-assistant-chat__message-header-text">
				{message}
			</div>
		</div>
	);
}
