/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInputGroupAI} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useState} from 'react';

import CharacterCounter from '../components/CharacterCounter';
import AIAssistantFooterDisclaimer from './components/AIAssistantFooterDisclaimer';
import AIAssistantMessageBalloon from './components/AIAssistantMessageBalloon';
import {renderMessageBalloon} from './components/messageBalloonRenderers';
import {MESSAGE_LENGTH_MAX} from './constants';
import {AIChat} from './useAIChat';
import resolveMessage from './utils/resolveMessage';

type AIState = 'focused' | 'result' | 'result-readonly' | 'working';

interface AIAssistantChatBodyProps {
	aiState?: AIState;
	chat: AIChat;
	quickActions?: string[];
	showGreeting: boolean;
}

const AIAssistantChatBody: React.FC<AIAssistantChatBodyProps> = ({
	aiState: controlledAIState,
	chat,
	quickActions,
	showGreeting,
}) => {
	const {
		isGenerating,
		lastMessageRef,
		message,
		messages,
		messagesContainerRef,
		scrollToBottom,
		sendMessage,
		setMessage,
	} = chat;

	const [scrolledToBottom, setScrolledToBottom] = useState(true);

	let aiState = controlledAIState;

	if (!aiState && isGenerating) {
		aiState = 'working';
	}

	function handleScroll({currentTarget}: React.UIEvent<HTMLDivElement>) {
		setScrolledToBottom(
			currentTarget.scrollHeight -
				currentTarget.scrollTop -
				currentTarget.clientHeight <=
				1
		);
	}

	function onSubmit(event: React.FormEvent<HTMLFormElement>) {
		event.preventDefault();

		sendMessage(message);
	}

	return (
		<>
			<div
				className="ai-assistant-chat__messages-container"
				onScroll={handleScroll}
				ref={messagesContainerRef}
			>
				{showGreeting && (
					<AIAssistantMessageBalloon
						error={false}
						message={Liferay.Language.get(
							'hi-i-can-help-you-generate-content-titles-tags-or-translate-your-work'
						)}
					/>
				)}

				{messages.map((item, index) => (
					<div
						key={index}
						ref={
							index === messages.length - 1
								? lastMessageRef
								: null
						}
					>
						{renderMessageBalloon(
							{chat, index, item},
							resolveMessage(item)
						)}
					</div>
				))}

				{isGenerating && (
					<div className="ai-assistant-chat__generating-balloon">
						<div className="ai-assistant-chat__generating-balloon-indicator">
							<ClayLoadingIndicator />
						</div>

						<span className="ai-assistant-chat__generating-loading-text">
							{Liferay.Language.get('generating')}
						</span>
					</div>
				)}

				{!scrolledToBottom && (
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('scroll-to-bottom')}
						className="ai-assistant-chat__scroll-to-bottom"
						displayType="primary"
						onClick={scrollToBottom}
						size="sm"
						symbol="angle-down"
						title={Liferay.Language.get('scroll-to-bottom')}
					/>
				)}
			</div>

			{!!quickActions?.length && (
				<div className="ai-assistant-chat__quick-actions">
					<span className="ai-assistant-chat__quick-actions-title">
						{Liferay.Language.get('quick-actions')}
					</span>

					<div className="ai-assistant-chat__quick-actions-list">
						{quickActions.map((quickAction) => (
							<ClayButton
								className="ai-assistant-chat__quick-action"
								disabled={isGenerating}
								displayType="secondary"
								key={quickAction}
								onClick={() => sendMessage(quickAction)}
								size="xs"
							>
								<ClayIcon
									className="ai-assistant-chat__quick-action-icon"
									symbol="stars"
								/>

								{quickAction}
							</ClayButton>
						))}
					</div>
				</div>
			)}

			<ClayForm
				className="ai-assistant-chat__form"
				onSubmit={(event) => onSubmit(event)}
			>
				<ClayInputGroupAI
					aiState={aiState}
					id="assistant-user-input"
					maxLength={MESSAGE_LENGTH_MAX}
					messages={{
						retry: Liferay.Language.get('retry'),
						submit: Liferay.Language.get('submit'),
						working: Liferay.Language.get('working-on-it'),
					}}
					onChange={(event) => setMessage(event.target.value)}
					placeholder={Liferay.Language.get('ask-me-anything')}
					readOnly={isGenerating}
					value={message}
				/>

				<CharacterCounter
					count={message.length}
					max={MESSAGE_LENGTH_MAX}
				/>

				{(aiState === 'result' || aiState === 'result-readonly') && (
					<ClayForm.FeedbackGroup>
						<ClayForm.Text>
							<ClayForm.FeedbackIndicator symbol="stars" />

							{Liferay.Language.get('suggestion')}
						</ClayForm.Text>
					</ClayForm.FeedbackGroup>
				)}
			</ClayForm>

			<AIAssistantFooterDisclaimer />
		</>
	);
};

export default AIAssistantChatBody;
