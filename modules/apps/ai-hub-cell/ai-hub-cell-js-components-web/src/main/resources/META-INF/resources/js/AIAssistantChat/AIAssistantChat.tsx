/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';
import {EventSource} from 'eventsource';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import {
	CATEGORIZE_EVENT,
	CategorizeEventPayload,
} from '../Categorization/events';
import {ECategorizationAgent} from '../Categorization/types';
import ReportFeedbackModal from '../ReportFeedback/ReportFeedbackModal';
import submitPositiveReportFeedback from '../ReportFeedback/submitPositiveReportFeedback';
import {
	ChatContext,
	createEventSource,
	postChatByExternalReferenceCodeMessage,
} from './api';
import AIAssistantFooterDisclaimer from './components/AIAssistantFooterDisclaimer';
import AIAssistantMessageBalloon from './components/AIAssistantMessageBalloon';
import CategorizationMessageBalloon from './components/CategorizationMessageBalloon';
import UserMessageBalloon from './components/UserMessageBalloon';

import './chat.scss';

interface message {
	agentDefinitionExternalReferenceCodes?: string[];
	categorization?: CategorizeEventPayload;
	error?: boolean;
	sender: string;
	text: string;
}

interface ReportContext {
	agentDefinitionExternalReferenceCodes: string[];
	index: number;
}

type AIState = 'focused' | 'result' | 'result-readonly' | 'working';

interface AIAssistantChatProps {
	aiState?: AIState;
	embedded?: boolean;
	getContext?: () => ChatContext;
	hideTriggerLabel?: boolean;
	initialMessage?: string;
	instructionDefinitionScope: string;
	quickActions?: string[];
	triggerClassName?: string;
	triggerLabel?: string;
	triggerRound?: boolean;
}

const AIAssistantChat: React.FC<AIAssistantChatProps> = ({
	aiState,
	embedded = false,
	getContext = () => ({}),
	hideTriggerLabel = false,
	initialMessage,
	instructionDefinitionScope,
	quickActions,
	triggerRound = true,
	triggerClassName,
	triggerLabel = Liferay.Language.get('ai-assistant'),
}) => {
	const [active, setActive] = useState<boolean>(false);
	const [feedbackGiven, setFeedbackGiven] = useState<Record<number, boolean>>(
		{}
	);
	const [isGenerating, setIsGenerating] = useState<boolean>(false);
	const [messages, setMessages] = useState<message[]>([]);
	const [message, setMessage] = useState<string>('');
	const [reportContext, setReportContext] = useState<ReportContext | null>(
		null
	);

	const handleThumbsUp = (index: number, item: message) => {
		if (feedbackGiven[index]) {
			return;
		}

		setFeedbackGiven((previousFeedbackGiven) => ({
			...previousFeedbackGiven,
			[index]: true,
		}));

		submitPositiveReportFeedback({
			agentDefinitionExternalReferenceCodes:
				item.agentDefinitionExternalReferenceCodes ?? [],
			surface: 'aiAssistant',
		});
	};
	const eventSourceRef = useRef<EventSource | null>(null);
	const eventSourceReference = useRef<string | null>(null);
	const getContextRef = useRef<() => ChatContext>(getContext);
	const initialMessageRef = useRef<string | undefined>(initialMessage);
	const initialMessageSentRef = useRef<boolean>(false);
	const instructionDefinitionScopeRef = useRef<string>(
		instructionDefinitionScope
	);
	const messagesEndRef = useRef<HTMLDivElement | null>(null);
	const triggerRef = useRef<HTMLButtonElement | null>(null);
	const textAreaRef = useRef<HTMLTextAreaElement | null>(null);

	useEffect(() => {
		getContextRef.current = getContext;
		instructionDefinitionScopeRef.current = instructionDefinitionScope;
	}, [getContext, instructionDefinitionScope]);

	const sendMessage = useCallback((text: string) => {
		if (!text.trim()) {
			return;
		}

		setMessages((previousMessages) => {
			setTimeout(() => {
				messagesEndRef.current?.scrollIntoView({behavior: 'smooth'});
			}, 0);

			return [...previousMessages, {sender: 'user', text}];
		});

		setMessage('');

		if (eventSourceReference.current) {
			setIsGenerating(true);

			const getCurrentContext = getContextRef.current;

			postChatByExternalReferenceCodeMessage({
				chatContext: getCurrentContext(),
				eventSourceReference: eventSourceReference.current,
				instructionDefinitionScope:
					instructionDefinitionScopeRef.current,
				message: text,
			}).catch(() => setIsGenerating(false));
		}
	}, []);

	function onSubmit(event: React.FormEvent<HTMLFormElement>) {
		event.preventDefault();

		sendMessage(message);
	}

	function adjustTextAreaHeight(element: HTMLTextAreaElement) {
		const textArea = element ?? textAreaRef.current;

		if (!textArea) {
			return;
		}

		const style = window.getComputedStyle(textArea);
		const lineHeight =
			parseFloat(style.lineHeight) || parseFloat(style.fontSize) * 1.2;
		const maxHeight = lineHeight * 4;

		textArea.style.height = 'auto';
		const newHeight = Math.min(textArea.scrollHeight, maxHeight);
		textArea.style.height = `${newHeight}px`;
		textArea.style.overflowY =
			textArea.scrollHeight > maxHeight ? 'auto' : 'hidden';
	}

	function handleTextAreaKeyDown(
		event: React.KeyboardEvent<HTMLTextAreaElement>
	) {
		if (event.key !== 'Enter') {
			event.stopPropagation();

			return;
		}

		if (event.shiftKey) {
			setTimeout(
				() => adjustTextAreaHeight(event.target as HTMLTextAreaElement),
				0
			);

			return;
		}

		event.preventDefault();

		const form = (event.target as HTMLElement).closest(
			'form'
		) as HTMLFormElement | null;

		if (form?.requestSubmit) {
			form.requestSubmit();
		}
		else {
			form?.dispatchEvent(
				new Event('submit', {
					bubbles: true,
					cancelable: true,
				})
			);
		}
	}

	const openAIAssistantChatConnection = useCallback(() => {
		createEventSource().then((eventSource) => {
			if (!eventSource) {
				return;
			}

			eventSourceRef.current = eventSource;

			eventSourceRef.current.addEventListener(
				'Chat Message Sent',
				(event) => {
					try {
						const dataJSON = JSON.parse(event.data);

						setMessages((previousMessages) => {
							setTimeout(() => {
								messagesEndRef.current?.scrollIntoView({
									behavior: 'smooth',
								});
							}, 0);

							return [
								...previousMessages,
								{
									agentDefinitionExternalReferenceCodes:
										dataJSON[
											'agentDefinitionExternalReferenceCodes'
										] ?? [],
									sender: 'assistant',
									text: dataJSON['data'],
								},
							];
						});

						setMessage('');
					}
					catch {
						setMessages((previousMessages) => [
							...previousMessages,
							{error: true, sender: 'assistant', text: ''},
						]);
					}

					setIsGenerating(false);
				}
			);

			eventSourceRef.current.addEventListener('Subscribe', (event) => {
				eventSourceReference.current = event.data;

				if (
					initialMessageRef.current &&
					!initialMessageSentRef.current
				) {
					initialMessageSentRef.current = true;

					sendMessage(initialMessageRef.current);
				}
			});

			eventSourceRef.current.addEventListener(
				'Agent Invocation Failed',
				(event) => {
					let text = '';

					try {
						text = JSON.parse(event.data)['data'];
					}
					catch {
						text = '';
					}

					setMessages((previousMessages) => {
						setTimeout(() => {
							messagesEndRef.current?.scrollIntoView({
								behavior: 'smooth',
							});
						}, 0);

						return [
							...previousMessages,
							{
								error: true,
								sender: 'assistant',
								text,
							},
						];
					});

					setIsGenerating(false);
				}
			);
		});
	}, [sendMessage]);

	const closeAIAssistantChatConnection = useCallback(() => {
		eventSourceRef.current?.close();

		eventSourceRef.current = null;
	}, []);

	useEffect(() => {
		openAIAssistantChatConnection();

		return () => {
			closeAIAssistantChatConnection();
		};
	}, [closeAIAssistantChatConnection, openAIAssistantChatConnection]);

	useEffect(() => {
		const handleCategorize = (payload: CategorizeEventPayload) => {
			setActive(true);

			setMessages((previousMessages) => {
				setTimeout(() => {
					messagesEndRef.current?.scrollIntoView({
						behavior: 'smooth',
					});
				}, 0);

				return [
					...previousMessages,
					{
						sender: 'user',
						text:
							payload.agent ===
							ECategorizationAgent.AUTO_CATEGORIZE
								? Liferay.Language.get('add-categories')
								: Liferay.Language.get('generate-tags'),
					},
					{categorization: payload, sender: 'assistant', text: ''},
				];
			});
		};

		Liferay.on(CATEGORIZE_EVENT, handleCategorize);

		return () => {
			Liferay.detach(CATEGORIZE_EVENT, handleCategorize);
		};
	}, []);

	const chatSurface = (
		<>
			<div className="ai-assistant-chat__messages-container flex-grow-1 overflow-auto px-3">
				{!initialMessage && (
					<AIAssistantMessageBalloon
						error={false}
						message="Hi! I can help you generate content, titles, tags, or
						translate your work. What would you like to do?"
					/>
				)}

				{messages.map((item, index) => {
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
									? () => handleThumbsUp(index, item)
									: undefined
							}
						/>
					);
				})}

				{isGenerating && (
					<div className="ai-assistant-chat-balloon d-flex flex-row mb-2 rounded">
						<div className="align-items-center d-flex ml-2">
							<ClayLoadingIndicator />
						</div>

						<span className="ai-assistant-chat__generating-loading-text font-weight-semi-bold m-2 tex">
							{Liferay.Language.get('generating')}
						</span>
					</div>
				)}

				<div ref={messagesEndRef} />
			</div>

			{!!quickActions?.length && (
				<div className="ai-assistant-chat__quick-actions flex-shrink-0 px-3">
					<span className="ai-assistant-chat__quick-actions-title small text-secondary">
						{Liferay.Language.get('quick-actions')}
					</span>

					<div className="d-flex flex-wrap">
						{quickActions.map((quickAction) => (
							<ClayButton
								className="mb-1 mr-1"
								disabled={isGenerating}
								displayType="secondary"
								key={quickAction}
								onClick={() => sendMessage(quickAction)}
								size="xs"
							>
								<ClayIcon
									className="mr-1"
									height={12}
									spritemap={Liferay.Icons.spritemap}
									symbol="stars"
									width={12}
								/>

								{quickAction}
							</ClayButton>
						))}
					</div>
				</div>
			)}

			<ClayForm
				className="flex-shrink-0 pt-3 px-3"
				onSubmit={(event) => onSubmit(event)}
			>
				<div
					className="align-items-end d-flex flex-row"
					data-ai-state={aiState}
				>
					<textarea
						className="ai-assistant-chat__input form-control mr-2"
						id="assistant-user-input"
						onChange={(event) => {
							setMessage(event.target.value);
							adjustTextAreaHeight(event.target);
						}}
						onKeyDown={(
							event: React.KeyboardEvent<HTMLTextAreaElement>
						) => {
							handleTextAreaKeyDown(event);
						}}
						placeholder="Ask me anything..."
						readOnly={isGenerating || !!aiState}
						ref={textAreaRef}
						rows={1}
						value={message}
					/>

					<ClayButton
						disabled={!message.trim()}
						displayType="primary"
						type="submit"
					>
						<ClayIcon
							height={12}
							spritemap={Liferay.Icons.spritemap}
							symbol={isGenerating ? 'square' : 'order-arrow-up'}
							width={12}
						/>
					</ClayButton>
				</div>
			</ClayForm>

			<AIAssistantFooterDisclaimer />
		</>
	);

	if (embedded) {
		return (
			<div className="ai-assistant ai-assistant-chat__embedded d-flex flex-column pt-3">
				{chatSurface}
			</div>
		);
	}

	return (
		<ClayDropDown
			active={active}
			alignmentPosition={4}
			className="d-flex p-0"
			hasRightSymbols={false}
			menuElementAttrs={{
				className: 'cadmin',
				style: {
					height: 552,
					maxHeight: 'none',
					maxWidth: 'none',
					overflow: 'hidden',
					width: 448,
				},
			}}
			onActiveChange={setActive}
			trigger={
				<ClayButton
					aria-label={triggerLabel}
					borderless
					className={classNames(
						'ai-assistant-chat__trigger',
						triggerClassName
					)}
					displayType="secondary"
					monospaced={triggerRound && hideTriggerLabel}
					ref={triggerRef}
					rounded={triggerRound}
				>
					<ClayIcon
						height={16}
						spritemap={Liferay.Icons.spritemap}
						symbol="stars"
						width={16}
					/>

					{!hideTriggerLabel && (
						<span className="ai-assistant-chat__trigger-label ml-2">
							{triggerLabel}
						</span>
					)}
				</ClayButton>
			}
		>
			<div className="ai-assistant ai-assistant-chat__dropdown-container d-flex flex-column">
				<div className="flex-shrink-0 p-3">
					<ClayLayout.ContentRow className="align-items-center border-bottom justify-content-between mb-3 pb-2">
						<ClayLayout.ContentCol className="ai-assistant-chat__dropdown-title font-weight-semi-bold">
							{Liferay.Language.get('ai-assistant')}
						</ClayLayout.ContentCol>

						<ClayLayout.ContentCol>
							<ClayButton
								aria-label={Liferay.Language.get('close')}
								borderless
								displayType="unstyled"
								onClick={() => setActive(false)}
							>
								<ClayIcon
									className="ai-assistant-chat__dropdown-close-button"
									spritemap={Liferay.Icons.spritemap}
									symbol="times"
								/>
							</ClayButton>
						</ClayLayout.ContentCol>
					</ClayLayout.ContentRow>
				</div>

				{chatSurface}
			</div>

			{reportContext !== null && (
				<ReportFeedbackModal
					agentDefinitionExternalReferenceCodes={
						reportContext.agentDefinitionExternalReferenceCodes
					}
					onClose={() => setReportContext(null)}
					onSubmitted={() =>
						setFeedbackGiven((previousFeedbackGiven) => ({
							...previousFeedbackGiven,
							[reportContext.index]: true,
						}))
					}
					surface="aiAssistant"
				/>
			)}
		</ClayDropDown>
	);
};

export default AIAssistantChat;
