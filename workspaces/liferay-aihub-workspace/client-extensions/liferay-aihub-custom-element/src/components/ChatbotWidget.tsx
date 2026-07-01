/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {EventSource} from 'eventsource';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import {
	createEventSource,
	getChatbotConfiguration,
	postChatMessage,
} from '../api';
import {submitPositiveFeedback} from '../feedback';
import AssistantMessage from './AssistantMessage';
import ChatbotFooter from './ChatbotFooter';
import ChatbotHeader from './ChatbotHeader';
import ChatbotInput from './ChatbotInput';
import ChatbotIntro from './ChatbotIntro';
import ErrorMessage from './ErrorMessage';
import LoadingIndicator from './LoadingIndicator';
import SendFeedbackModal from './SendFeedbackModal';
import Toast from './Toast';
import UserMessage from './UserMessage';

import type {
	ChatMessage,
	ChatbotConfiguration,
	WidgetConfiguration,
} from '../types';

const FEEDBACK_TOAST_MESSAGE = 'Thanks for your feedback!';

interface ChatbotWidgetProps {
	widgetConfiguration: WidgetConfiguration;
}

interface ReportContext {
	agentDefinitionExternalReferenceCodes: string[];
	index: number;
}

export default function ChatbotWidget({
	widgetConfiguration,
}: ChatbotWidgetProps) {
	const [chatbotConfiguration, setChatbotConfiguration] =
		useState<ChatbotConfiguration | null>(null);
	const [feedbackGiven, setFeedbackGiven] = useState<Record<number, boolean>>(
		{}
	);
	const [loading, setLoading] = useState(false);
	const [messages, setMessages] = useState<ChatMessage[]>([]);
	const [notificationDismissed, setNotificationDismissed] = useState(false);
	const [open, setOpen] = useState(false);
	const [reportContext, setReportContext] = useState<ReportContext | null>(
		null
	);
	const [subscribed, setSubscribed] = useState(false);
	const [toastMessage, setToastMessage] = useState<string | null>(null);

	const eventSourceRef = useRef<EventSource | null>(null);
	const eventSourceReference = useRef<string | null>(null);
	const loadingTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(
		null
	);
	const messagesEndRef = useRef<HTMLDivElement>(null);
	const panelRef = useRef<HTMLDivElement>(null);

	useEffect(() => {
		getChatbotConfiguration(
			widgetConfiguration.chatbotExternalReferenceCode
		)
			.then(setChatbotConfiguration)
			.catch((error) => {
				console.error('Error fetching chatbot configuration:', error);
			});
	}, [widgetConfiguration.chatbotExternalReferenceCode]);

	useEffect(() => {
		if (!chatbotConfiguration?.active) {
			return;
		}

		let active = true;

		createEventSource()
			.then((eventSource) => {
				if (!active) {
					eventSource?.close();

					return;
				}

				if (!eventSource) {
					setMessages((prev) => [
						...prev,
						{sender: 'error', text: ''},
					]);
					setLoading(false);

					return;
				}

				eventSourceRef.current = eventSource;

				eventSource.addEventListener('Chat Message Sent', (event) => {
					if (loadingTimeoutRef.current) {
						clearTimeout(loadingTimeoutRef.current);
						loadingTimeoutRef.current = null;
					}

					try {
						const data = JSON.parse((event as MessageEvent).data);

						setMessages((prev) => [
							...prev,
							{
								agentDefinitionExternalReferenceCodes:
									data.agentDefinitionExternalReferenceCodes ??
									[],
								sender: 'assistant',
								text: data.data,
							},
						]);
					}
					catch (error) {
						console.error('Error parsing chat message:', error);

						setMessages((prev) => [
							...prev,
							{sender: 'error', text: ''},
						]);
					}

					setLoading(false);
				});

				eventSource.addEventListener(
					'Agent Invocation Failed',
					(event) => {
						if (loadingTimeoutRef.current) {
							clearTimeout(loadingTimeoutRef.current);
							loadingTimeoutRef.current = null;
						}

						let text = '';

						try {
							text =
								JSON.parse((event as MessageEvent).data).data ??
								'';
						}
						catch (error) {
							console.error(
								'Error parsing agent invocation failure:',
								error
							);
						}

						setMessages((prev) => [
							...prev,
							{sender: 'error', text},
						]);
						setLoading(false);
					}
				);

				eventSource.addEventListener('Subscribe', (event) => {
					eventSourceReference.current = (event as MessageEvent).data;
					setSubscribed(true);
				});

				eventSource.addEventListener('error', () => {
					setSubscribed(false);

					if (loadingTimeoutRef.current) {
						clearTimeout(loadingTimeoutRef.current);
						loadingTimeoutRef.current = null;

						setMessages((prev) => [
							...prev,
							{sender: 'error', text: ''},
						]);
						setLoading(false);
					}
					else if (eventSource.readyState === EventSource.CLOSED) {
						console.error('EventSource connection closed');

						setMessages((prev) => [
							...prev,
							{sender: 'error', text: ''},
						]);
					}
				});
			})
			.catch((error) => {
				console.error('Failed to create event source:', error);

				setMessages((prev) => [...prev, {sender: 'error', text: ''}]);
				setLoading(false);
			});

		return () => {
			active = false;

			if (loadingTimeoutRef.current) {
				clearTimeout(loadingTimeoutRef.current);
			}

			eventSourceRef.current?.close();
			eventSourceRef.current = null;
			setSubscribed(false);
		};
	}, [chatbotConfiguration]);

	useEffect(() => {
		if (open) {
			panelRef.current?.focus();
		}
	}, [open]);

	useEffect(() => {
		messagesEndRef.current?.scrollIntoView({behavior: 'smooth'});
	}, [messages, loading]);

	const handleToggle = useCallback(() => {
		setOpen((prev) => !prev);
		setNotificationDismissed(true);
	}, []);

	const sendMessage = useCallback(
		async (text: string) => {
			if (!eventSourceReference.current) {
				return;
			}

			setMessages((prev) => [...prev, {sender: 'user', text}]);
			setLoading(true);

			try {
				const response = await postChatMessage(
					widgetConfiguration.chatbotExternalReferenceCode,
					eventSourceReference.current,
					text
				);

				if (!response.ok) {
					throw new Error('Failed to post message');
				}

				loadingTimeoutRef.current = setTimeout(() => {
					setMessages((prev) => [
						...prev,
						{sender: 'error', text: ''},
					]);
					setLoading(false);
				}, 30000);
			}
			catch (error) {
				console.error('Failed to send message:', error);

				setMessages((prev) => [...prev, {sender: 'error', text: ''}]);
				setLoading(false);
			}
		},
		[widgetConfiguration.chatbotExternalReferenceCode]
	);

	const handleThumbsDown = (index: number, message: ChatMessage) => {
		setReportContext({
			agentDefinitionExternalReferenceCodes:
				message.agentDefinitionExternalReferenceCodes ?? [],
			index,
		});
	};

	const handleThumbsUp = (index: number, message: ChatMessage) => {
		if (feedbackGiven[index]) {
			return;
		}

		setFeedbackGiven((prev) => ({...prev, [index]: true}));

		submitPositiveFeedback(
			{
				agentDefinitionExternalReferenceCodes:
					message.agentDefinitionExternalReferenceCodes ?? [],
				chatbotExternalReferenceCode:
					widgetConfiguration.chatbotExternalReferenceCode,
				surface: 'clickToChat',
			},
			() => setToastMessage(FEEDBACK_TOAST_MESSAGE)
		);
	};

	const localized = useMemo(() => {
		if (!chatbotConfiguration) {
			return null;
		}

		return {
			disclaimerMessage: chatbotConfiguration.disclaimerMessage ?? '',
			introMessage: chatbotConfiguration.introMessage ?? '',
			notificationMessage: chatbotConfiguration.notificationMessage ?? '',
			placeholderMessage: chatbotConfiguration.placeholderMessage ?? '',
			title: chatbotConfiguration.title ?? '',
		};
	}, [chatbotConfiguration]);

	if (!chatbotConfiguration?.active || !localized) {
		return null;
	}

	const avatarURL = chatbotConfiguration.avatar?.fileURL;

	return (
		<>
			<div
				className={`aihub-panel${open ? ' open' : ''}`}
				ref={panelRef}
				tabIndex={-1}
			>
				<ChatbotHeader
					avatar={avatarURL}
					onClose={handleToggle}
					title={localized.title}
				/>

				<div aria-live="polite" className="aihub-messages">
					<ChatbotIntro
						avatar={avatarURL}
						introMessage={localized.introMessage}
						title={localized.title}
					/>

					{messages.map((msg, index) => {
						if (msg.sender === 'assistant') {
							return (
								<AssistantMessage
									avatar={avatarURL}
									feedbackGiven={Boolean(
										feedbackGiven[index]
									)}
									key={index}
									onThumbsDown={() =>
										handleThumbsDown(index, msg)
									}
									onThumbsUp={() =>
										handleThumbsUp(index, msg)
									}
									text={msg.text}
									title={localized.title}
								/>
							);
						}

						if (msg.sender === 'error') {
							return <ErrorMessage key={index} />;
						}

						return <UserMessage key={index} text={msg.text} />;
					})}

					{loading && <LoadingIndicator />}

					<div ref={messagesEndRef} />
				</div>

				<ChatbotInput
					disabled={
						loading || !subscribed || !eventSourceReference.current
					}
					onSubmit={sendMessage}
					placeholder={localized.placeholderMessage}
				/>

				<ChatbotFooter
					disclaimerMessage={localized.disclaimerMessage}
				/>
			</div>

			{!open &&
				!notificationDismissed &&
				localized.notificationMessage && (
					<div className="aihub-notification">
						<span>{localized.notificationMessage}</span>

						<button
							aria-label="Dismiss"
							className="aihub-notification-close"
							onClick={() => setNotificationDismissed(true)}
						>
							<ClayIcon symbol="times" />
						</button>
					</div>
				)}

			<button
				aria-label={open ? 'Close AI Assistant' : 'Open AI Assistant'}
				className="aihub-toggle"
				onClick={handleToggle}
			>
				{open ? (
					<ClayIcon symbol="times" />
				) : (
					<ClayIcon symbol="comments" />
				)}
			</button>

			{reportContext !== null && (
				<SendFeedbackModal
					agentDefinitionExternalReferenceCodes={
						reportContext.agentDefinitionExternalReferenceCodes
					}
					chatbotExternalReferenceCode={
						widgetConfiguration.chatbotExternalReferenceCode
					}
					onClose={() => setReportContext(null)}
					onSubmitted={() => {
						setFeedbackGiven((previousFeedbackGiven) => ({
							...previousFeedbackGiven,
							[reportContext.index]: true,
						}));
						setReportContext(null);
						setToastMessage(FEEDBACK_TOAST_MESSAGE);
					}}
				/>
			)}

			{toastMessage && (
				<Toast
					message={toastMessage}
					onDismiss={() => setToastMessage(null)}
				/>
			)}
		</>
	);
}
