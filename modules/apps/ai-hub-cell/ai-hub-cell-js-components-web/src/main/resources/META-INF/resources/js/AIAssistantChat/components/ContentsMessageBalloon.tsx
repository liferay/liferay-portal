/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayList from '@clayui/list';
import React from 'react';

import '../chat.scss';
import parseContentDraftsMessage from '../utils/parseContentDraftsMessage';
import renderAIAssistantMessageMarkdown from '../utils/renderAIAssistantMessageMarkdown';
import AIAssistantMessageBalloonIcon from './AIAssistantMessageBalloonIcon';

interface ContentsMessageBalloonProps {
	message: string;
}

const ContentsMessageBalloon: React.FC<ContentsMessageBalloonProps> = ({
	message,
}) => {
	const {drafts, text} = parseContentDraftsMessage(message);

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon ai-assistant-chat__content-generation-balloon">
			{text && (
				<div className="ai-assistant-chat__content-generation-balloon-header">
					<AIAssistantMessageBalloonIcon />

					<div
						dangerouslySetInnerHTML={{
							__html: renderAIAssistantMessageMarkdown(text),
						}}
					/>
				</div>
			)}

			<ClayList className="ai-assistant-chat__content-generation-balloon-list">
				{drafts.map((draft) => (
					<ClayList.Item flex key={draft.editURL}>
						<ClayList.ItemField>
							<span className="ai-assistant-chat__content-generation-balloon-icon">
								<ClayIcon
									spritemap={Liferay.Icons.spritemap}
									symbol="blogs"
								/>
							</span>
						</ClayList.ItemField>

						<ClayList.ItemField expand>
							<ClayList.ItemTitle>
								<a href={draft.editURL}>{draft.title}</a>
							</ClayList.ItemTitle>

							<ClayList.ItemText>
								<ClayLabel displayType="secondary">
									{Liferay.Language.get('draft')}
								</ClayLabel>
							</ClayList.ItemText>
						</ClayList.ItemField>
					</ClayList.Item>
				))}
			</ClayList>
		</div>
	);
};

export default ContentsMessageBalloon;
