/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EventSource} from 'eventsource';
import {useCallback, useEffect, useRef, useState} from 'react';

import {
	CATEGORIZE_EVENT,
	CategorizeEventPayload,
	REQUEST_CATEGORIZE_EVENT,
} from '../Categorization/events';
import {classifyCategorizationIntent} from '../Categorization/services/classifyCategorizationIntent';
import {ECategorizationAgent} from '../Categorization/types';
import submitPositiveReportFeedback from '../ReportFeedback/submitPositiveReportFeedback';
import {
	ChatContext,
	createEventSource,
	postChatByExternalReferenceCodeMessage,
} from './api';
import {ContentType} from './components/ContentTypeSelectorMessageBalloon';
import {subscribeToServerEvents} from './serverEvents';
import {getSpaces} from './services/getSpaces';
import {ChatMessageSentData, Message} from './types';
import buildAssistantMessage from './utils/buildAssistantMessage';
import buildContentTypeMessage from './utils/buildContentTypeMessage';

export interface AIChatReportContext {
	agentDefinitionExternalReferenceCodes: string[];
	index: number;
}

export interface AIChat {
	contextRef: React.MutableRefObject<ChatContext | undefined>;
	feedbackGiven: Record<number, boolean>;
	fileUploadSelectorRef: React.MutableRefObject<string | undefined>;
	getContextRef: React.MutableRefObject<(() => ChatContext) | undefined>;
	giveThumbsUp: (index: number, item: Message) => void;
	isGenerating: boolean;
	markFeedbackGiven: (index: number) => void;
	message: string;
	messages: Message[];
	messagesContainerRef: React.RefObject<HTMLDivElement>;
	reportContext: AIChatReportContext | null;
	runtimeContextRef: React.MutableRefObject<ChatContext>;
	scrollToBottom: () => void;
	sendMessage: (text: string) => void;
	setIsGenerating: React.Dispatch<React.SetStateAction<boolean>>;
	setMessage: (message: string) => void;
	setMessages: React.Dispatch<React.SetStateAction<Message[]>>;
	setReportContext: (reportContext: AIChatReportContext | null) => void;
	sourceLanguageIdRef: React.MutableRefObject<string>;
}

interface UseAIChatProps {
	context?: ChatContext;
	enableFreeFormCategorization?: boolean;
	getContext?: () => ChatContext;
	initialMessage?: string;
	instructionDefinitionScope: string;
	onCloseRequested?: () => void;
	onOpenRequested?: (options?: {expanded?: boolean}) => void;
	triggerRef?: React.RefObject<HTMLButtonElement | null>;
}

export default function useAIChat({
	context,
	enableFreeFormCategorization = false,
	getContext,
	initialMessage,
	instructionDefinitionScope,
	onCloseRequested,
	onOpenRequested,
	triggerRef,
}: UseAIChatProps): AIChat {
	const [feedbackGiven, setFeedbackGiven] = useState<Record<number, boolean>>(
		{}
	);
	const [isGenerating, setIsGenerating] = useState<boolean>(false);
	const [messages, setMessages] = useState<Message[]>([]);
	const [message, setMessage] = useState<string>('');
	const [reportContext, setReportContext] =
		useState<AIChatReportContext | null>(null);

	const enableFreeFormCategorizationRef = useRef<boolean>(
		enableFreeFormCategorization
	);
	const eventSourceRef = useRef<EventSource | null>(null);
	const eventSourceReference = useRef<string | null>(null);
	const contextRef = useRef<ChatContext | undefined>(context);
	const getContextRef = useRef<(() => ChatContext) | undefined>(getContext);
	const runtimeContextRef = useRef<ChatContext>({});
	const initialMessageRef = useRef<string | undefined>(initialMessage);
	const initialMessageSentRef = useRef<boolean>(false);
	const instructionDefinitionScopeRef = useRef<string>(
		instructionDefinitionScope
	);
	const messagesContainerRef = useRef<HTMLDivElement | null>(null);
	const sourceLanguageIdRef = useRef<string>(
		Liferay.ThemeDisplay.getLanguageId()
	);
	const fileUploadSelectorRef = useRef<string | undefined>(undefined);
	const onCloseRequestedRef = useRef<(() => void) | undefined>(
		onCloseRequested
	);
	const onOpenRequestedRef = useRef<
		((options?: {expanded?: boolean}) => void) | undefined
	>(onOpenRequested);

	useEffect(() => {
		if (context !== undefined) {
			contextRef.current = context;
		}

		enableFreeFormCategorizationRef.current = enableFreeFormCategorization;
		getContextRef.current = getContext;
		instructionDefinitionScopeRef.current = instructionDefinitionScope;
		onCloseRequestedRef.current = onCloseRequested;
		onOpenRequestedRef.current = onOpenRequested;
	}, [
		context,
		enableFreeFormCategorization,
		getContext,
		instructionDefinitionScope,
		onCloseRequested,
		onOpenRequested,
	]);

	useEffect(() => {
		const fieldId = triggerRef?.current
			?.closest('[data-ai-assistant-field-id]')
			?.getAttribute('data-ai-assistant-field-id');

		fileUploadSelectorRef.current = fieldId
			? `[data-ai-assistant-field-id="${fieldId}"]`
			: '[data-ai-assistant-field-id]';
	}, [triggerRef]);

	const scrollToBottom = useCallback(() => {
		const container = messagesContainerRef.current;

		container?.scrollTo?.({
			behavior: 'smooth',
			top: container.scrollHeight,
		});
	}, []);

	useEffect(() => {
		scrollToBottom();
	}, [isGenerating, messages, scrollToBottom]);

	useEffect(() => {
		const onLocaleChanged = ({languageId}: {languageId: string}) => {
			sourceLanguageIdRef.current = languageId;
		};

		Liferay.on('localizationSelect:localeChanged', onLocaleChanged);

		return () => {
			Liferay.detach('localizationSelect:localeChanged', onLocaleChanged);
		};
	}, []);

	const sendMessage = useCallback((text: string) => {
		if (!text.trim()) {
			return;
		}

		setMessages((previousMessages) => [
			...previousMessages,
			{sender: 'user', text},
		]);

		setMessage('');

		if (!eventSourceReference.current) {
			setIsGenerating(false);

			return;
		}

		setIsGenerating(true);

		const postToChat = () => {
			postChatByExternalReferenceCodeMessage({
				chatContext: {
					...contextRef.current,
					...getContextRef.current?.(),
					...runtimeContextRef.current,
				},
				eventSourceReference: eventSourceReference.current as string,
				instructionDefinitionScope:
					instructionDefinitionScopeRef.current,
				message: text,
			}).catch(() => setIsGenerating(false));
		};

		if (!enableFreeFormCategorizationRef.current) {
			postToChat();

			return;
		}

		classifyCategorizationIntent(text)
			.then((verdict) => {
				if (verdict.passthrough || !verdict.actions.length) {
					postToChat();

					return;
				}

				setIsGenerating(false);

				Liferay.fire(REQUEST_CATEGORIZE_EVENT, {
					actions: verdict.actions,
				});
			})
			.catch(() => postToChat());
	}, []);

	useEffect(() => {
		initialMessageRef.current = initialMessage;

		if (
			initialMessage &&
			!initialMessageSentRef.current &&
			eventSourceReference.current
		) {
			initialMessageSentRef.current = true;

			sendMessage(initialMessage);
		}
	}, [initialMessage, sendMessage]);

	const giveThumbsUp = useCallback(
		(index: number, item: Message) => {
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
		},
		[feedbackGiven]
	);

	const markFeedbackGiven = useCallback((index: number) => {
		setFeedbackGiven((previousFeedbackGiven) => ({
			...previousFeedbackGiven,
			[index]: true,
		}));
	}, []);

	const openAIAssistantChatConnection = useCallback(() => {
		createEventSource().then((eventSource) => {
			if (!eventSource) {
				return;
			}

			eventSourceRef.current = eventSource;

			subscribeToServerEvents(eventSource);

			eventSourceRef.current.addEventListener(
				'Chat Message Sent',
				(event) => {
					try {
						const dataJSON: ChatMessageSentData = JSON.parse(
							event.data
						);

						const assistantMessage =
							buildAssistantMessage(dataJSON);

						setMessages((previousMessages) => {
							const lastMessage = previousMessages.at(-1);
							const messages = previousMessages.slice(0, -1);

							if (
								lastMessage?.images?.length &&
								assistantMessage?.images?.length
							) {
								return [
									...messages,
									{
										...assistantMessage,
										images: [
											...lastMessage.images,
											...assistantMessage.images,
										],
									},
								];
							}

							return [...previousMessages, assistantMessage];
						});

						setMessage('');
					}
					catch {
						setMessages((previousMessages) => [
							...previousMessages,
							{error: true, sender: 'assistant', text: ''},
						]);

						return;
					}
					finally {
						setIsGenerating(false);
					}
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

					setMessages((previousMessages) => [
						...previousMessages,
						{
							error: true,
							sender: 'assistant',
							text,
						},
					]);

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
		const handleOpen = (payload: {
			contentTypes?: ContentType[];
			context?: ChatContext;
			expanded?: boolean;
			message?: string;
		}) => {
			onOpenRequestedRef.current?.({
				expanded: payload?.expanded ?? true,
			});

			runtimeContextRef.current = payload?.context ?? {};

			if (payload?.context) {
				contextRef.current = {
					...contextRef.current,
					...payload.context,
				};
			}

			if (payload?.contentTypes?.length) {
				const contentTypes = payload.contentTypes;

				const askForContentType = () =>
					setMessages((previousMessages) => [
						...previousMessages,
						buildContentTypeMessage(contentTypes),
					]);

				setMessages((previousMessages) => [
					...previousMessages,
					{
						sender: 'user',
						text: Liferay.Language.get('generate-content'),
					},
				]);

				getSpaces()
					.then((spaces) => {
						if (spaces.length > 1) {
							setMessages((previousMessages) => [
								...previousMessages,
								{
									contentTypes,
									sender: 'assistant',
									spaces,
									text: Liferay.Language.get(
										'in-which-space-do-you-want-to-generate-the-content'
									),
								},
							]);

							return;
						}

						if (spaces.length === 1) {
							runtimeContextRef.current = {
								...runtimeContextRef.current,
								spaceId: String(spaces[0].siteId),
							};
						}

						askForContentType();
					})
					.catch(() => {
						Liferay.Util.openToast({
							message: Liferay.Language.get(
								'the-spaces-could-not-be-loaded'
							),
							type: 'danger',
						});

						askForContentType();
					});
			}
			else if (payload?.message) {
				sendMessage(payload.message);
			}
		};

		Liferay.on('openAIAssistantChat', handleOpen);

		return () => {
			Liferay.detach('openAIAssistantChat', handleOpen);
		};
	}, [sendMessage]);

	useEffect(() => {
		const handleCategorize = (payload: CategorizeEventPayload) => {
			onOpenRequestedRef.current?.();

			setMessages((previousMessages) => {
				const categorizationMessage = {
					categorization: payload,
					sender: 'assistant',
					text: '',
				};

				if (payload.suppressUserMessage) {
					return [...previousMessages, categorizationMessage];
				}

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
					categorizationMessage,
				];
			});
		};

		Liferay.on(CATEGORIZE_EVENT, handleCategorize);

		return () => {
			Liferay.detach(CATEGORIZE_EVENT, handleCategorize);
		};
	}, []);

	return {
		contextRef,
		feedbackGiven,
		fileUploadSelectorRef,
		getContextRef,
		giveThumbsUp,
		isGenerating,
		markFeedbackGiven,
		message,
		messages,
		messagesContainerRef,
		reportContext,
		runtimeContextRef,
		scrollToBottom,
		sendMessage,
		setIsGenerating,
		setMessage,
		setMessages,
		setReportContext,
		sourceLanguageIdRef,
	};
}
