/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {APPLY_OBJECT_FIELD_VALUES_EVENT} from '../events';
import {Message} from '../types';
import {AIChat} from '../useAIChat';
import buildContentTypeMessage from '../utils/buildContentTypeMessage';
import {ResolvedMessage} from '../utils/resolveMessage';
import AIAssistantMessageBalloon from './AIAssistantMessageBalloon';
import CategorizationMessageBalloon from './CategorizationMessageBalloon';
import ContentTypeSelectorMessageBalloon from './ContentTypeSelectorMessageBalloon';
import ContentsMessageBalloon from './ContentsMessageBalloon';
import FieldValueMessageBalloon from './FieldValueMessageBalloon';
import ImageMessageBalloon from './ImageMessageBalloon';
import QuickRepliesMessageBalloon from './QuickRepliesMessageBalloon';
import SelectComponentMessageBalloon from './SelectComponentMessageBalloon';
import SpaceSelectorMessageBalloon from './SpaceSelectorMessageBalloon';
import TranslateContentMessageBalloon from './TranslateContentMessageBalloon';
import UserMessageBalloon from './UserMessageBalloon';

export interface MessageBalloonRenderContext {
	chat: AIChat;
	index: number;
	item: Message;
}

type MessageBalloonRenderers = {
	[T in ResolvedMessage['type']]: (
		context: MessageBalloonRenderContext,
		resolvedMessage: Extract<ResolvedMessage, {type: T}>
	) => JSX.Element;
};

const MESSAGE_BALLOON_RENDERERS: MessageBalloonRenderers = {
	'assistant': ({chat, index, item}) => (
		<AIAssistantMessageBalloon
			error={item.error ?? false}
			feedbackGiven={Boolean(chat.feedbackGiven[index])}
			message={item.text}
			onReport={
				!item.error
					? () =>
							chat.setReportContext({
								agentDefinitionExternalReferenceCodes:
									item.agentDefinitionExternalReferenceCodes ??
									[],
								index,
							})
					: undefined
			}
			onThumbsUp={
				!item.error ? () => chat.giveThumbsUp(index, item) : undefined
			}
		/>
	),
	'categorization': ({chat}, {categorization}) => (
		<CategorizationMessageBalloon
			{...categorization}
			setBalloonGenerating={chat.setBalloonGenerating}
		/>
	),
	'content-drafts': ({item}) => (
		<ContentsMessageBalloon message={item.text} />
	),
	'content-types': ({chat, item}, {contentTypes}) => (
		<ContentTypeSelectorMessageBalloon
			contentTypes={contentTypes}
			contextRef={chat.runtimeContextRef}
			message={item.text}
			sendMessage={chat.sendMessage}
			setIsGenerating={chat.setIsGenerating}
		/>
	),
	'field-values': ({chat, index}, {fieldValues}) => {
		const previousMessage = chat.messages[index - 1];

		return (
			<FieldValueMessageBalloon
				onApply={() =>
					Liferay.fire(APPLY_OBJECT_FIELD_VALUES_EVENT, {
						values: fieldValues,
					})
				}
				onRegenerate={() => {
					if (previousMessage?.sender === 'user') {
						chat.sendMessage(previousMessage.text);
					}
				}}
				values={fieldValues}
			/>
		);
	},
	'images': ({chat}, {images}) => {
		const chatContext = {
			...chat.contextRef.current,
			...chat.getContextRef.current?.(),
		};

		return (
			<ImageMessageBalloon
				images={images}
				saveProps={{
					fileUploadSelector:
						chatContext.fileUploadSelector ??
						chat.fileUploadSelectorRef.current,
					groupId: chatContext.groupId,
					objectEntryFolderExternalReferenceCode:
						chatContext.objectEntryFolderExternalReferenceCode,
				}}
				scrollToBottom={chat.scrollToBottom}
			/>
		);
	},
	'quick-replies': ({chat}, {component}) => (
		<QuickRepliesMessageBalloon
			component={component}
			onAction={chat.onAction}
			setIsGenerating={chat.setIsGenerating}
		/>
	),
	'select-component': ({chat}, {component}) => (
		<SelectComponentMessageBalloon
			component={component}
			onAction={chat.onAction}
			setIsGenerating={chat.setIsGenerating}
		/>
	),
	'space-selector': ({chat, item}, {contentTypes, spaces}) => (
		<SpaceSelectorMessageBalloon
			contextRef={chat.runtimeContextRef}
			message={item.text}
			onSelectSpace={(space) =>
				chat.setMessages((previousMessages) => [
					...previousMessages,
					{sender: 'user', text: space.name},
					buildContentTypeMessage(contentTypes),
				])
			}
			spaces={spaces}
		/>
	),
	'translate': ({chat}, {translate}) => (
		<TranslateContentMessageBalloon
			agentInstanceId={translate.agentInstanceId}
			availableLanguageIds={translate.availableLanguageIds}
			requestedLanguageIds={translate.targetLanguageIds}
			results={translate.results}
			setIsGenerating={chat.setIsGenerating}
			sourceLanguageIdRef={chat.sourceLanguageIdRef}
		/>
	),
	'user': ({item}) => <UserMessageBalloon message={item.text} />,
};

export function renderMessageBalloon(
	context: MessageBalloonRenderContext,
	resolvedMessage: ResolvedMessage
): JSX.Element {
	const renderer = MESSAGE_BALLOON_RENDERERS[resolvedMessage.type] as (
		context: MessageBalloonRenderContext,
		resolvedMessage: ResolvedMessage
	) => JSX.Element;

	return renderer(context, resolvedMessage);
}
