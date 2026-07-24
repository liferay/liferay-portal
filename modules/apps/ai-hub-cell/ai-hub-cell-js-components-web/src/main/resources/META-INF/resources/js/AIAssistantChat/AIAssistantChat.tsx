/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ReactPortal} from '@liferay/frontend-js-react-web';
import React, {useId, useRef, useState} from 'react';

import ReportFeedbackModal from '../ReportFeedback/ReportFeedbackModal';
import AIAssistantChatBody from './AIAssistantChatBody';
import {ChatContext} from './api';
import AIAssistantTrigger from './components/AIAssistantTrigger';
import AIAssistantDropdown from './shells/AIAssistantDropdown';
import AIAssistantSidebar from './shells/AIAssistantSidebar';
import useAIChat from './useAIChat';

import './chat.scss';

type AIState = 'focused' | 'result' | 'result-readonly' | 'working';

export type AIAssistantDisplayMode = 'dropdown' | 'sidebar' | 'toggle';

interface AIAssistantChatProps {
	aiState?: AIState;
	context?: ChatContext;
	displayMode?: AIAssistantDisplayMode;
	enableFreeFormCategorization?: boolean;
	getContext?: () => ChatContext;
	hideTriggerLabel?: boolean;
	initialMessage?: string;
	instructionDefinitionScope: string;
	pushContainer?: string;
	quickActions?: string[];
	sidebarBehavior?: 'overlay' | 'push';
	triggerClassName?: string;
	triggerLabel?: string;
	triggerRound?: boolean;
}

// Renders the AI Assistant chat behind a trigger. The chat body is rendered
// once into a stable node; the active shell (dropdown or sidebar, chosen by
// displayMode) reparents that node, so switching shells never remounts it.

const AIAssistantChat: React.FC<AIAssistantChatProps> = ({
	aiState,
	context,
	displayMode = 'toggle',
	enableFreeFormCategorization = false,
	getContext,
	hideTriggerLabel = false,
	initialMessage,
	instructionDefinitionScope,
	pushContainer,
	quickActions,
	sidebarBehavior,
	triggerClassName,
	triggerLabel,
	triggerRound = true,
}) => {
	const [expanded, setExpanded] = useState<boolean>(false);
	const [open, setOpen] = useState<boolean>(false);

	const [bodyNode] = useState(() => {
		const element = document.createElement('div');

		element.style.display = 'contents';

		return element;
	});

	const sidebarId = useId();
	const triggerRef = useRef<HTMLButtonElement | null>(null);

	const handleOpenChange = (nextOpen: boolean) => {
		setOpen(nextOpen);

		if (!nextOpen) {
			setExpanded(false);
		}
	};

	const chat = useAIChat({
		context,
		enableFreeFormCategorization,
		getContext,
		initialMessage,
		instructionDefinitionScope,
		onCloseRequested: () => handleOpenChange(false),
		onOpenRequested: (options) => {
			if (options?.expanded) {
				setExpanded(true);
			}

			handleOpenChange(true);
		},
		triggerRef,
	});

	const chatBody = (
		<AIAssistantChatBody
			aiState={aiState}
			chat={chat}
			quickActions={quickActions}
			showGreeting={!initialMessage}
		/>
	);

	const reportFeedbackModal = chat.reportContext !== null && (
		<ReportFeedbackModal
			agentDefinitionExternalReferenceCodes={
				chat.reportContext.agentDefinitionExternalReferenceCodes
			}
			onClose={() => chat.setReportContext(null)}
			onSubmitted={() =>
				chat.markFeedbackGiven(chat.reportContext!.index)
			}
			surface="aiAssistant"
		/>
	);

	const trigger = (
		<AIAssistantTrigger
			className={triggerClassName}
			hideLabel={hideTriggerLabel}
			label={triggerLabel}
			ref={triggerRef}
			round={triggerRound}
		/>
	);

	const sidebarActive =
		displayMode === 'sidebar' || (displayMode === 'toggle' && expanded);

	const sidebarEnabled = displayMode !== 'dropdown';

	return (
		<>
			{sidebarActive ? (
				React.cloneElement(trigger, {
					'aria-controls': sidebarId,
					'aria-expanded': open,
					'onClick': () => handleOpenChange(!open),
				})
			) : (
				<AIAssistantDropdown
					active={open}
					bodyNode={bodyNode}
					onActiveChange={handleOpenChange}
					onExpand={
						displayMode === 'toggle'
							? () => setExpanded(true)
							: undefined
					}
					trigger={trigger}
				/>
			)}

			{sidebarEnabled && (
				<AIAssistantSidebar
					active={sidebarActive}
					behavior={sidebarBehavior}
					bodyNode={bodyNode}
					id={sidebarId}
					onCollapse={
						displayMode === 'toggle'
							? () => {
									setExpanded(false);

									requestAnimationFrame(() =>
										triggerRef.current?.focus()
									);
								}
							: undefined
					}
					onOpenChange={handleOpenChange}
					open={open && sidebarActive}
					pushContainer={pushContainer}
					triggerRef={triggerRef}
				/>
			)}

			<ReactPortal container={bodyNode} wrapper={false}>
				{chatBody}
			</ReactPortal>

			{reportFeedbackModal}
		</>
	);
};

export default AIAssistantChat;
