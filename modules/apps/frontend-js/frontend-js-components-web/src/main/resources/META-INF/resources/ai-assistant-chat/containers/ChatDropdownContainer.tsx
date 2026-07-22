/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	Overlay,
	useIsFirstRender,
	useOverlayPosition,
	usePrevious,
} from '@clayui/shared';
import classNames from 'classnames';
import React, {useCallback, useEffect, useRef} from 'react';

import {ChatPanelContext} from '../ChatPanelContext';
import useChatContainer from '../hooks/useChatContainer';
import useClonedTrigger from '../hooks/useClonedTrigger';
import {ChatContainerProps} from './ChatContainerProps';
import InitialFocus from './InitialFocus';

import './ChatContainers.scss';

export interface ChatDropdownContainerProps extends ChatContainerProps {}

export default function ChatDropdownContainer({
	children,
	className,
	id,
	onOpenChange = () => {},
	open = false,
	trigger,
	...otherProps
}: ChatDropdownContainerProps) {
	const menuRef = useRef<HTMLDivElement | null>(null);
	const triggerRef = useRef<HTMLElement | null>(null);

	const isFirstRender = useIsFirstRender();
	const previousOpen = usePrevious(open);

	const contextValue = useChatContainer({
		id,
		onClose: () => onOpenChange(false),
	});

	const handleTriggerClick = useCallback(
		(event: React.MouseEvent<HTMLElement>) => {
			trigger.props.onClick?.(event);

			onOpenChange(!open);
		},
		[trigger, onOpenChange, open]
	);

	useEffect(() => {
		if (!isFirstRender && previousOpen && !open) {
			triggerRef.current?.focus();
		}
	}, [isFirstRender, previousOpen, open]);

	useOverlayPosition({isOpen: open, ref: menuRef, triggerRef});

	const clonedTrigger = useClonedTrigger(trigger, {
		dialogId: contextValue.dialogId,
		onClick: handleTriggerClick,
		open,
		triggerRef,
	});

	return (
		<>
			{clonedTrigger}

			{open && (
				<Overlay
					isCloseOnInteractOutside
					isKeyboardDismiss
					isModal
					isOpen={open}
					menuRef={menuRef}
					onClose={() => onOpenChange(false)}
					suppress={[menuRef, triggerRef]}
					triggerRef={triggerRef}
				>
					<div
						className="border chat-container-dropdown-menu rounded-lg shadow"
						ref={menuRef}
					>
						<InitialFocus menuRef={menuRef}>
							<ChatPanelContext.Provider value={contextValue}>
								<div
									{...otherProps}
									className={classNames(
										'chat-container chat-container-dropdown',
										className
									)}
								>
									{children}
								</div>
							</ChatPanelContext.Provider>
						</InitialFocus>
					</div>
				</Overlay>
			)}
		</>
	);
}
