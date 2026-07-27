/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React from 'react';

import '../chat.scss';

interface AIAssistantMessageBalloonIconProps {
	error?: boolean;
}

const AIAssistantMessageBalloonIcon: React.FC<
	AIAssistantMessageBalloonIconProps
> = ({error = false}) => {
	return (
		<div
			className={classNames('ai-assistant-chat__message-balloon-icon', {
				'ai-assistant-chat__message-balloon-icon--error': error,
			})}
		>
			<ClayIcon
				spritemap={Liferay.Icons.spritemap}
				symbol={error ? 'exclamation-full' : 'stars'}
			/>
		</div>
	);
};

export default AIAssistantMessageBalloonIcon;
