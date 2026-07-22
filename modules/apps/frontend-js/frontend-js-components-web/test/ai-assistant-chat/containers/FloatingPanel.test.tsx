/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import, @liferay/no-extraneous-dependencies
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import ChatPanel from '../../../src/main/resources/META-INF/resources/ai-assistant-chat';
import FloatingPanel from '../../../src/main/resources/META-INF/resources/ai-assistant-chat/containers/FloatingPanel';

function renderFloatingPanel() {
	const menuRef = {current: null};

	return render(
		<FloatingPanel
			dialogId="floating-panel"
			menuRef={menuRef}
			onClose={jest.fn()}
			otherProps={{}}
		>
			<ChatPanel>
				<ChatPanel.Header title="Chat" />

				<ChatPanel.Body>Chat Content</ChatPanel.Body>
			</ChatPanel>
		</FloatingPanel>
	);
}

describe('FloatingPanel', () => {
	afterEach(() => {
		sessionStorage.clear();
	});

	it('exposes the drag handle as a focusable button with an accessible name', () => {
		renderFloatingPanel();

		const dragButton = screen.getByRole('button', {name: 'move-assistant'});

		expect(dragButton.tagName).toBe('BUTTON');
	});

	it('exposes the resize handle as a focusable button with an accessible name', () => {
		renderFloatingPanel();

		const resizeButton = screen.getByRole('button', {
			name: 'resize-assistant',
		});

		expect(resizeButton.tagName).toBe('BUTTON');
		expect(resizeButton).toHaveClass(
			'chat-container-floating-resize-handle'
		);
	});

	it('moves the panel on ArrowRight from the drag handle', async () => {
		renderFloatingPanel();

		const dragButton = screen.getByRole('button', {name: 'move-assistant'});
		const panel = dragButton.closest(
			'.chat-container-floating-panel'
		) as HTMLElement;

		const initialLeft = panel.style.left;

		dragButton.focus();

		await userEvent.keyboard('{ArrowRight}');

		expect(panel.style.left).not.toBe(initialLeft);
	});

	it('resizes the panel on ArrowDown from the resize handle', async () => {
		renderFloatingPanel();

		const resizeButton = screen.getByRole('button', {
			name: 'resize-assistant',
		});
		const panel = resizeButton.closest(
			'.chat-container-floating-panel'
		) as HTMLElement;

		const initialHeight = panel.style.height;

		resizeButton.focus();

		await userEvent.keyboard('{ArrowDown}');

		expect(panel.style.height).not.toBe(initialHeight);
	});

	it('does not scroll the page when arrow keys nudge the drag handle', async () => {
		renderFloatingPanel();

		const dragButton = screen.getByRole('button', {name: 'move-assistant'});

		dragButton.focus();

		const arrowDownEvent = new KeyboardEvent('keydown', {
			bubbles: true,
			cancelable: true,
			key: 'ArrowDown',
		});

		dragButton.dispatchEvent(arrowDownEvent);

		expect(arrowDownEvent.defaultPrevented).toBe(true);
	});

	it('drags the panel a single delta when grabbing the drag handle', () => {
		renderFloatingPanel();

		const dragButton = screen.getByRole('button', {name: 'move-assistant'});
		const panel = dragButton.closest(
			'.chat-container-floating-panel'
		) as HTMLElement;

		const initialLeft = parseInt(panel.style.left, 10);

		fireEvent(
			dragButton,
			new MouseEvent('pointerdown', {
				bubbles: true,
				button: 0,
				clientX: 0,
				clientY: 0,
			})
		);

		fireEvent(
			document,
			new MouseEvent('pointermove', {clientX: 10, clientY: 0})
		);

		fireEvent(document, new Event('pointerup'));

		expect(parseInt(panel.style.left, 10)).toBe(initialLeft + 10);
	});

	it('has no accessibility violations', async () => {
		const {container} = renderFloatingPanel();

		await checkAccessibility({bestPractices: true, context: container});
	});
});
