/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SidePanel} from '@clayui/core';
import classNames from 'classnames';
import React from 'react';

import {ChatPanelContext} from '../ChatPanelContext';
import useChatContainer from '../hooks/useChatContainer';
import useClonedTrigger from '../hooks/useClonedTrigger';
import {ChatContainerProps} from './ChatContainerProps';

import './ChatContainers.scss';

export interface ChatSidebarContainerProps extends ChatContainerProps {
	containerRef: React.RefObject<HTMLElement>;
}

export default function ChatSidebarContainer({
	children,
	className,
	containerRef,
	id,
	onOpenChange = () => {},
	open = false,
	trigger,
	...otherProps
}: ChatSidebarContainerProps) {
	const triggerRef = React.useRef<HTMLElement | null>(null);

	const contextValue = useChatContainer({
		id,
		onClose: () => onOpenChange(false),
	});

	const {dialogId} = contextValue;

	const clonedTrigger = useClonedTrigger(trigger, {
		dialogId,
		open,
		triggerRef,
	});

	return (
		<>
			{clonedTrigger}

			<SidePanel
				containerRef={containerRef}
				onOpenChange={onOpenChange}
				open={open}
				triggerRef={triggerRef}
			>
				<ChatPanelContext.Provider value={contextValue}>
					<div
						{...otherProps}
						className={classNames(
							'chat-container chat-container-sidebar',
							className
						)}
					>
						{children}
					</div>
				</ChatPanelContext.Provider>
			</SidePanel>
		</>
	);
}
