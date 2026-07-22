/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Overlay, useIsFirstRender, usePrevious} from '@clayui/shared';
import React, {useEffect, useId, useRef} from 'react';

import useClonedTrigger from '../hooks/useClonedTrigger';
import {ChatContainerProps} from './ChatContainerProps';
import FloatingPanel from './FloatingPanel';

export interface ChatFloatingContainerProps extends ChatContainerProps {}

export default function ChatFloatingContainer({
	children,
	className,
	id,
	onOpenChange = () => {},
	open = false,
	trigger,
	...otherProps
}: ChatFloatingContainerProps) {
	const menuRef = useRef<HTMLDivElement | null>(null);
	const triggerRef = useRef<HTMLElement | null>(null);
	const generatedId = useId();

	const stableId = id ?? generatedId;

	const isFirstRender = useIsFirstRender();
	const previousOpen = usePrevious(open);

	const handleTriggerClick = (event: React.MouseEvent<HTMLElement>) => {
		trigger.props.onClick?.(event);

		onOpenChange(!open);
	};

	useEffect(() => {
		if (!isFirstRender && previousOpen && !open) {
			triggerRef.current?.focus();
		}
	}, [isFirstRender, previousOpen, open]);

	const clonedTrigger = useClonedTrigger(trigger, {
		dialogId: stableId,
		onClick: handleTriggerClick,
		open,
		triggerRef,
	});

	return (
		<>
			{clonedTrigger}

			{open && (
				<Overlay
					isKeyboardDismiss
					isOpen={open}
					menuRef={menuRef}
					onClose={() => onOpenChange(false)}
					triggerRef={triggerRef}
				>
					<FloatingPanel
						className={className}
						dialogId={stableId}
						menuRef={menuRef}
						onClose={() => onOpenChange(false)}
						otherProps={otherProps}
					>
						{children}
					</FloatingPanel>
				</Overlay>
			)}
		</>
	);
}
