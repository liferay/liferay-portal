/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInputGroupAI} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useEffect} from 'react';

import AIAssistantFooterDisclaimer from './components/AIAssistantFooterDisclaimer';
import AIAssistantMessageBalloon from './components/AIAssistantMessageBalloon';
import CategorizationMessageBalloon from './components/CategorizationMessageBalloon';
import ContentTypeSelectorMessageBalloon from './components/ContentTypeSelectorMessageBalloon';
import ContentsMessageBalloon from './components/ContentsMessageBalloon';
import FieldValueMessageBalloon from './components/FieldValueMessageBalloon';
import ImageMessageBalloon from './components/ImageMessageBalloon';
import TranslateContentMessageBalloon from './components/TranslateContentMessageBalloon';
import UserMessageBalloon from './components/UserMessageBalloon';
import MESSAGE_BALLOON_COMPONENTS from './components/messageBalloonComponents';
import {
	APPLY_OBJECT_FIELD_VALUES_EVENT,
	GENERATE_FIELD_VALUE_AGENT_EXTERNAL_REFERENCE_CODE,
} from './events';
import {AIChat} from './useAIChat';
import getGeneratedFieldValues from './utils/getGeneratedFieldValues';
import parseContentDraftsMessage from './utils/parseContentDraftsMessage';
import resolveMessageType from './utils/resolveMessageType';

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
		contextRef,
		feedbackGiven,
		fileUploadSelectorRef,
		getContextRef,
		giveThumbsUp,
		isGenerating,
		message,
		messages,
		messagesEndRef,
		runtimeContextRef,
		sendMessage,
		setIsGenerating,
		setMessage,
		setReportContext,
		sourceLanguageIdRef,
	} = chat;

	let aiState = controlledAIState;

	if (!aiState && isGenerating) {
		aiState = 'working';
	}

	useEffect(() => {
		messagesEndRef.current?.scrollIntoView();
	}, [messagesEndRef]);

	function onSubmit(event: React.FormEvent<HTMLFormElement>) {
		event.preventDefault();

		sendMessage(message);
	}

	return (
		<>
			<div className="ai-assistant-chat__messages-container">
				{showGreeting && (
					<AIAssistantMessageBalloon
						error={false}
						message={Liferay.Language.get(
							'hi-i-can-help-you-generate-content-titles-tags-or-translate-your-work'
						)}
					/>
				)}

				{messages.map((item, index) => {
					const messageType = resolveMessageType(item);

					if (messageType === 'select-component' && item.component) {
						const SelectComponentMessageBalloon =
							MESSAGE_BALLOON_COMPONENTS[messageType];

						return (
							<SelectComponentMessageBalloon
								component={item.component}
								key={index}
								setIsGenerating={setIsGenerating}
							/>
						);
					}

					if (item.sender === 'user') {
						return (
							<UserMessageBalloon
								key={index}
								message={item.text}
							/>
						);
					}

					if (item.categorization) {
						return (
							<CategorizationMessageBalloon
								key={index}
								{...item.categorization}
							/>
						);
					}

					if (item.images?.length) {
						const context = {
							...contextRef.current,
							...getContextRef.current?.(),
						};

						return (
							<ImageMessageBalloon
								images={item.images}
								key={index}
								saveProps={{
									fileUploadSelector:
										context.fileUploadSelector ??
										fileUploadSelectorRef.current,
									groupId: context.groupId,
									objectEntryFolderExternalReferenceCode:
										context.objectEntryFolderExternalReferenceCode,
								}}
							/>
						);
					}

					if (item.contentTypes) {
						return (
							<ContentTypeSelectorMessageBalloon
								contentTypes={item.contentTypes}
								contextRef={runtimeContextRef}
								key={index}
								message={item.text}
								sendMessage={sendMessage}
							/>
						);
					}

					if (parseContentDraftsMessage(item.text).drafts.length) {
						return (
							<ContentsMessageBalloon
								key={index}
								message={item.text}
							/>
						);
					}

					try {
						const json = JSON.parse(
							item.text
								.trim()
								.replace(/^```(?:json)?/i, '')
								.replace(/```$/, '')
								.trim()
						);

						if (json?.action === 'translate') {
							const {
								agentInstanceId,
								availableLanguageIds,
								results,
								targetLanguageIds,
							} = json;

							return (
								<TranslateContentMessageBalloon
									agentInstanceId={agentInstanceId}
									availableLanguageIds={availableLanguageIds}
									key={index}
									requestedLanguageIds={targetLanguageIds}
									results={results}
									setIsGenerating={setIsGenerating}
									sourceLanguageIdRef={sourceLanguageIdRef}
								/>
							);
						}
					}
					catch {}

					const fieldValues =
						!item.error &&
						item.agentDefinitionExternalReferenceCodes?.includes(
							GENERATE_FIELD_VALUE_AGENT_EXTERNAL_REFERENCE_CODE
						)
							? getGeneratedFieldValues(item.text)
							: {};

					if (Object.keys(fieldValues).length) {
						const previousMessage = messages[index - 1];

						return (
							<FieldValueMessageBalloon
								key={index}
								onApply={() =>
									Liferay.fire(
										APPLY_OBJECT_FIELD_VALUES_EVENT,
										{
											values: fieldValues,
										}
									)
								}
								onRegenerate={() => {
									if (previousMessage?.sender === 'user') {
										sendMessage(previousMessage.text);
									}
								}}
								values={fieldValues}
							/>
						);
					}

					return (
						<AIAssistantMessageBalloon
							error={item.error ?? false}
							feedbackGiven={Boolean(feedbackGiven[index])}
							key={index}
							message={item.text}
							onReport={
								!item.error
									? () =>
											setReportContext({
												agentDefinitionExternalReferenceCodes:
													item.agentDefinitionExternalReferenceCodes ??
													[],
												index,
											})
									: undefined
							}
							onThumbsUp={
								!item.error
									? () => giveThumbsUp(index, item)
									: undefined
							}
						/>
					);
				})}

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

				<div ref={messagesEndRef} />
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
