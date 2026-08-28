/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDropDown from '@clayui/drop-down';
import {ReactPortal} from '@liferay/frontend-js-react-web';
import React, {
	useCallback,
	useEffect,
	useId,
	useRef,
	useState,
	useSyncExternalStore,
} from 'react';

import ReportFeedbackModal from '../ReportFeedback/ReportFeedbackModal';
import {
	AIAssistantOpenCommand,
	close,
	getState,
	releaseHost,
	subscribe,
} from './AIAssistant';
import AIAssistantChatBody from './AIAssistantChatBody';
import AIAssistantPanelHeader from './components/AIAssistantPanelHeader';
import AIAssistantSidebar from './shells/AIAssistantSidebar';
import useAIChat from './useAIChat';

import './chat.scss';

const AIAssistantHost: React.FC = () => {
	const {command} = useSyncExternalStore(subscribe, getState);

	const [lastCommand, setLastCommand] =
		useState<AIAssistantOpenCommand | null>(null);

	const [openedByEvent, setOpenedByEvent] = useState<boolean>(false);

	const [expanded, setExpanded] = useState<boolean>(false);

	const [anchorElement, setAnchorElement] = useState<HTMLElement | null>(
		null
	);

	// Drives Clay's SidePanel open prop. It starts false so the panel mounts
	// closed and then opens on the next frame, letting Clay play its own
	// slide-in instead of appearing already open.

	const [sidebarMounted, setSidebarMounted] = useState<boolean>(false);

	const [sidebarOpen, setSidebarOpen] = useState<boolean>(false);

	const [bodyNode] = useState(() => {
		const element = document.createElement('div');

		element.style.display = 'contents';

		return element;
	});

	const sidebarId = useId();
	const anchorRef = useRef<HTMLElement | null>(null);
	const triggerElementRef = useRef<HTMLElement | null>(null);

	useEffect(() => releaseHost, []);

	useEffect(() => {
		if (command) {
			setLastCommand(command);
			setExpanded(false);
		}
		setOpenedByEvent(false);
	}, [command]);

	const activeCommand = command ?? lastCommand;
	const anchorId = activeCommand?.anchorId;
	const triggerId = activeCommand?.triggerId;

	const isOpen = command !== null || openedByEvent;

	const collapsible = activeCommand?.presentation === 'dropdown';
	const showSidebar = isOpen && (!collapsible || expanded);
	const showDropdown = isOpen && collapsible && !expanded;

	useEffect(() => {
		const element = anchorId ? document.getElementById(anchorId) : null;

		anchorRef.current = element;

		setAnchorElement(element);
	}, [anchorId]);

	useEffect(() => {
		triggerElementRef.current = triggerId
			? document.getElementById(triggerId)
			: null;
	}, [triggerId]);

	useEffect(() => {
		if (showSidebar) {
			setSidebarMounted(true);
		}
	}, [showSidebar]);

	useEffect(() => {
		if (showSidebar) {
			const frame = requestAnimationFrame(() => setSidebarOpen(true));

			return () => cancelAnimationFrame(frame);
		}

		setSidebarOpen(false);
	}, [showSidebar]);

	const handleClose = () => {
		setOpenedByEvent(false);

		close();
	};

	const handleExpand = () => setExpanded(true);

	const handleCollapse = () => setExpanded(false);

	const mountBodyNode = useCallback(
		(node: HTMLDivElement | null) => {
			node?.appendChild(bodyNode);
		},
		[bodyNode]
	);

	const chat = useAIChat({
		chatbotExternalReferenceCode: command?.chatbotExternalReferenceCode,
		context: activeCommand?.context,
		enableFreeFormCategorization:
			activeCommand?.enableFreeFormCategorization,
		getContext: activeCommand?.getContext,
		initialMessage: activeCommand?.initialMessage,
		instructionDefinitionScope:
			activeCommand?.instructionDefinitionScope ?? '',
		onAction: activeCommand?.onAction,
		onCloseRequested: handleClose,
		onOpenRequested: () => setOpenedByEvent(true),
		triggerRef: anchorRef as React.RefObject<HTMLButtonElement | null>,
	});

	const chatBody = (
		<AIAssistantChatBody
			chat={chat}
			quickActions={activeCommand?.quickActions}
			showGreeting={!activeCommand?.initialMessage}
		/>
	);

	const {reportContext} = chat;

	const reportFeedbackModal = reportContext !== null && (
		<ReportFeedbackModal
			agentDefinitionExternalReferenceCodes={
				reportContext.agentDefinitionExternalReferenceCodes
			}
			onClose={() => chat.setReportContext(null)}
			onSubmitted={() => chat.markFeedbackGiven(reportContext.index)}
			surface="aiAssistant"
		/>
	);

	return (
		<>
			{showDropdown && anchorElement && (
				<ClayDropDown.Menu
					active
					alignElementRef={anchorRef}
					className="ai-assistant-chat__panel cadmin"
					onActiveChange={(nextActive) => {
						if (!nextActive) {
							handleClose();
						}
					}}
					triggerRef={triggerElementRef}
				>
					<div className="ai-assistant ai-assistant-chat__dropdown-container">
						<AIAssistantPanelHeader
							onClose={handleClose}
							onToggleExpanded={handleExpand}
						/>

						<div
							className="ai-assistant-chat__body-slot"
							ref={mountBodyNode}
						/>
					</div>
				</ClayDropDown.Menu>
			)}

			{sidebarMounted && (
				<AIAssistantSidebar
					active={showSidebar}
					behavior={activeCommand?.sidebarBehavior}
					bodyNode={bodyNode}
					id={sidebarId}
					onCollapse={collapsible ? handleCollapse : undefined}
					onOpenChange={(nextOpen) => {
						if (!nextOpen) {
							handleClose();
						}
					}}
					open={sidebarOpen}
					pushContainer={activeCommand?.pushContainer}
					triggerRef={anchorRef as React.RefObject<HTMLElement>}
				/>
			)}

			<ReactPortal container={bodyNode} wrapper={false}>
				{chatBody}
			</ReactPortal>

			{reportFeedbackModal}
		</>
	);
};

export default AIAssistantHost;
