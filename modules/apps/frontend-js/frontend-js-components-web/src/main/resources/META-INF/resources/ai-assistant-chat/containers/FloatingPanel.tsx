/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import classNames from 'classnames';
import React, {useCallback, useMemo} from 'react';

import {ChatPanelContext} from '../ChatPanelContext';
import useChatContainer from '../hooks/useChatContainer';
import useFloatingPanel, {
	NUDGE_STEP,
	RESIZE_STEP,
} from '../hooks/useFloatingPanel';
import createPointerDragHandler from '../utils/createPointerDragHandler';
import InitialFocus from './InitialFocus';

import './ChatContainers.scss';

function arrowKeyToDelta(key: string, step: number): [number, number] | null {
	switch (key) {
		case 'ArrowDown':
			return [0, step];
		case 'ArrowLeft':
			return [-step, 0];
		case 'ArrowRight':
			return [step, 0];
		case 'ArrowUp':
			return [0, -step];
		default:
			return null;
	}
}

interface FloatingPanelProps {
	children: React.ReactNode;
	className?: string;
	dialogId: string;
	menuRef: React.MutableRefObject<HTMLDivElement | null>;
	onClose: () => void;
	otherProps: React.HTMLAttributes<HTMLDivElement>;
}

export default function FloatingPanel({
	children,
	className,
	dialogId,
	menuRef,
	onClose,
	otherProps,
}: FloatingPanelProps) {
	const {drag, resize, state} = useFloatingPanel(dialogId);

	const handleDragPointerDown = useMemo(
		() => createPointerDragHandler(drag),
		[drag]
	);

	const handleResizePointerDown = useMemo(
		() => createPointerDragHandler(resize),
		[resize]
	);

	const handleDragKeyDown = useCallback(
		(event: React.KeyboardEvent) => {
			const delta = arrowKeyToDelta(event.key, NUDGE_STEP);

			if (delta) {
				event.preventDefault();
				drag(...delta);
			}
		},
		[drag]
	);

	const handleResizeKeyDown = useCallback(
		(event: React.KeyboardEvent) => {
			const delta = arrowKeyToDelta(event.key, RESIZE_STEP);

			if (delta) {
				event.preventDefault();
				resize(...delta);
			}
		},
		[resize]
	);

	const titleBarProps = useMemo(
		() => ({onPointerDown: handleDragPointerDown}),
		[handleDragPointerDown]
	);

	const contextValue = useChatContainer({
		id: dialogId,
		onClose,
		titleBarLeading: (
			<ClayButtonWithIcon
				aria-label={Liferay.Language.get('move-assistant')}
				borderless
				className="chat-container-floating-drag-handle"
				displayType="secondary"
				onKeyDown={handleDragKeyDown}
				size="sm"
				symbol="drag"
			/>
		),
		titleBarProps,
	});

	return (
		<div
			className="border chat-container-floating-panel rounded-lg shadow"
			ref={menuRef}
			style={{
				height: state.height,
				left: state.x,
				top: state.y,
				width: state.width,
			}}
		>
			<InitialFocus menuRef={menuRef}>
				<ChatPanelContext.Provider value={contextValue}>
					<div
						{...otherProps}
						className={classNames(
							'chat-container chat-container-floating',
							className
						)}
					>
						{children}
					</div>

					<button
						aria-label={Liferay.Language.get('resize-assistant')}
						className="chat-container-floating-resize-handle"
						onKeyDown={handleResizeKeyDown}
						onPointerDown={handleResizePointerDown}
						type="button"
					/>
				</ChatPanelContext.Provider>
			</InitialFocus>
		</div>
	);
}
