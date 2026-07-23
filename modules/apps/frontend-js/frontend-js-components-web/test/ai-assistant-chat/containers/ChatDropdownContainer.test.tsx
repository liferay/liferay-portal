/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useInteractionFocus} from '@clayui/shared';
import {act, fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import ReactDOM from 'react-dom/client';

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import, @liferay/no-extraneous-dependencies
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import {ChatDropdownContainer} from '../../../src/main/resources/META-INF/resources';
import ChatPanel from '../../../src/main/resources/META-INF/resources/ai-assistant-chat';

function ControlledChatDropdownContainer({
	initialOpen = true,
	onOpenChange = () => {},
}: {
	initialOpen?: boolean;
	onOpenChange?: (open: boolean) => void;
}) {
	const [open, setOpen] = React.useState(initialOpen);

	return (
		<ChatDropdownContainer
			onOpenChange={(nextOpen) => {
				onOpenChange(nextOpen);
				setOpen(nextOpen);
			}}
			open={open}
			trigger={<button type="button">Open Chat</button>}
		>
			<ChatPanel>
				<ChatPanel.Header title="Chat" />

				<ChatPanel.Body>Chat Content</ChatPanel.Body>
			</ChatPanel>
		</ChatDropdownContainer>
	);
}

function renderContainer(
	onOpenChange?: (open: boolean) => void,
	initialOpen?: boolean
) {
	return render(
		<ControlledChatDropdownContainer
			initialOpen={initialOpen}
			onOpenChange={onOpenChange}
		/>
	);
}

function KeepInteractionModalityWarm() {
	useInteractionFocus();

	return null;
}

describe('ChatDropdownContainer', () => {

	// clay's useInteractionFocus tracks the last input modality (keyboard vs.
	// pointer) in a module-level singleton that never resets on unmount, and
	// only registers its document listeners while at least one consumer is
	// mounted. Keeping one mounted for the whole suite (outside Testing
	// Library's per-test cleanup) guarantees the listeners are always live,
	// so the beforeEach reset below reliably lands before every test.

	beforeAll(() => {
		act(() => {
			ReactDOM.createRoot(document.createElement('div')).render(
				<KeepInteractionModalityWarm />
			);
		});
	});

	beforeEach(() => {
		jest.spyOn(
			HTMLElement.prototype,
			'offsetParent',
			'get'
		).mockReturnValue(document.body);

		fireEvent.keyDown(document, {key: 'Tab'});
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('renders the trigger wired to the menu it controls', () => {
		renderContainer();

		const trigger = screen.getByRole('button', {
			hidden: true,
			name: 'Open Chat',
		});

		expect(trigger).toHaveAttribute('aria-haspopup', 'dialog');
		expect(trigger).toHaveAttribute('aria-expanded', 'true');

		const dialog = screen.getByRole('dialog');

		expect(trigger.getAttribute('aria-controls')).toBe(dialog.id);
	});

	it('does not focus the trigger on the first render when initially closed', () => {
		renderContainer(undefined, false);

		const trigger = screen.getByRole('button', {
			hidden: true,
			name: 'Open Chat',
		});

		expect(trigger).not.toHaveFocus();
	});

	it('does not refocus the trigger on a rerender that keeps the dropdown open', async () => {
		const {rerender} = renderContainer();

		const closeButton = screen.getByRole('button', {name: 'close'});

		await waitFor(() => expect(closeButton).toHaveFocus());

		rerender(<ControlledChatDropdownContainer />);

		const trigger = screen.getByRole('button', {
			hidden: true,
			name: 'Open Chat',
		});

		expect(trigger).not.toHaveFocus();
		expect(closeButton).toHaveFocus();
	});

	it('moves focus into the menu and does not expose a menu role', async () => {
		renderContainer();

		const closeButton = screen.getByRole('button', {name: 'close'});

		await waitFor(() => expect(closeButton).toHaveFocus());

		const dialog = screen.getByRole('dialog');

		expect(dialog).toHaveAttribute('role', 'dialog');
	});

	it('does not close when Tab is pressed', async () => {
		const onOpenChange = jest.fn();

		renderContainer(onOpenChange);

		const closeButton = screen.getByRole('button', {name: 'close'});

		await waitFor(() => expect(closeButton).toHaveFocus());

		await userEvent.tab();

		expect(onOpenChange).not.toHaveBeenCalled();
	});

	it('does not rove focus between header action buttons on arrow keys', async () => {
		renderContainer();

		const closeButton = screen.getByRole('button', {name: 'close'});

		await waitFor(() => expect(closeButton).toHaveFocus());

		await userEvent.keyboard('{ArrowDown}');

		expect(closeButton).toHaveFocus();

		await userEvent.keyboard('{ArrowUp}');

		expect(closeButton).toHaveFocus();
	});

	it('fires onOpenChange(false) when the dropdown is dismissed', async () => {
		const onOpenChange = jest.fn();

		renderContainer(onOpenChange);

		await userEvent.keyboard('{Escape}');

		expect(onOpenChange).toHaveBeenCalledWith(false);
	});

	it('restores focus to the trigger when the close button is clicked', async () => {
		renderContainer();

		const trigger = screen.getByRole('button', {
			hidden: true,
			name: 'Open Chat',
		});
		const closeButton = screen.getByRole('button', {name: 'close'});

		await userEvent.click(closeButton);

		await waitFor(() => expect(trigger).toHaveFocus());
	});

	it('fires onOpenChange(false) when the open trigger is clicked', async () => {
		const onOpenChange = jest.fn();

		renderContainer(onOpenChange);

		const trigger = screen.getByRole('button', {
			hidden: true,
			name: 'Open Chat',
		});

		await userEvent.click(trigger);

		expect(onOpenChange).toHaveBeenCalledWith(false);
	});

	it('has no accessibility violations', async () => {
		const {container} = renderContainer();

		await checkAccessibility({bestPractices: true, context: container});
	});
});
