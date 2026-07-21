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
import InitialFocus from './InitialFocus';

import './ChatContainers.scss';

export interface ChatDropdownContainerProps
	extends React.HTMLAttributes<HTMLDivElement> {
	children: React.ReactNode;
	id?: string;
	onOpenChange?: (open: boolean) => void;
	open?: boolean;
	trigger: React.ReactElement & {
		ref?: React.Ref<HTMLElement>;
	};
}

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

	/*
	 * React Compiler cannot statically prove that cloning the trigger element
	 * is safe. That's the reason we're adding the eslint-disable below. This follows
	 * the same pattern as modal/components/Modal.tsx.
	 */

	// eslint-disable-next-line react-compiler/react-compiler
	const clonedTrigger = React.cloneElement(trigger, {
		'aria-controls': contextValue.dialogId,
		'aria-expanded': open,
		'aria-haspopup': 'dialog',
		'onClick': handleTriggerClick,
		'ref': triggerRef,
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
