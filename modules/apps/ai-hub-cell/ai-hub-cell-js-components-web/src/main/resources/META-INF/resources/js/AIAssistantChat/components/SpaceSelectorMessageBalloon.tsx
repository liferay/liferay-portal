/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {ChatContext} from '../api';
import {Space} from '../services/getSpaces';
import AIAssistantMessageBalloonIcon from './AIAssistantMessageBalloonIcon';
import SpaceSelect from './SpaceSelect';

import '../chat.scss';

interface SpaceSelectorMessageBalloonProps {
	contextRef: React.MutableRefObject<ChatContext>;
	message: string;
	onSelectSpace: (space: Space) => void;
	spaces: Space[];
}

const SpaceSelectorMessageBalloon: React.FC<
	SpaceSelectorMessageBalloonProps
> = ({contextRef, message, onSelectSpace, spaces}) => {
	function handleSelectSpace(space: Space) {

		// eslint-disable-next-line react-compiler/react-compiler
		contextRef.current = {
			...contextRef.current,
			spaceId: String(space.siteId),
		};

		onSelectSpace(space);
	}

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon ai-assistant-chat__content-generation-balloon">
			<div className="ai-assistant-chat__content-generation-balloon-header">
				<AIAssistantMessageBalloonIcon />

				<span>{message}</span>
			</div>

			<div className="ai-assistant-chat__content-generation-balloon-form">
				<SpaceSelect
					onSelectSpace={handleSelectSpace}
					spaces={spaces}
				/>
			</div>
		</div>
	);
};

export default SpaceSelectorMessageBalloon;
