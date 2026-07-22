/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import, @liferay/no-extraneous-dependencies
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import {ChatFloatingContainer} from '../../../src/main/resources/META-INF/resources';
import ChatPanel from '../../../src/main/resources/META-INF/resources/ai-assistant-chat';

function ControlledChatFloatingContainer({
	onOpenChange = () => {},
}: {
	onOpenChange?: (open: boolean) => void;
}) {
	const [open, setOpen] = React.useState(true);

	return (
		<ChatFloatingContainer
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
		</ChatFloatingContainer>
	);
}

function renderContainer(onOpenChange?: (open: boolean) => void) {
	return render(
		<ControlledChatFloatingContainer onOpenChange={onOpenChange} />
	);
}

describe('ChatFloatingContainer', () => {
	beforeEach(() => {
		jest.spyOn(
			HTMLElement.prototype,
			'offsetParent',
			'get'
		).mockReturnValue(document.body);
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('renders the trigger wired to the dialog it controls', () => {
		renderContainer();

		const trigger = screen.getByRole('button', {
			hidden: true,
			name: 'Open Chat',
		});
		const dialog = screen.getByRole('dialog');

		expect(trigger).toHaveAttribute('aria-haspopup', 'dialog');
		expect(trigger).toHaveAttribute('aria-expanded', 'true');
		expect(trigger.getAttribute('aria-controls')).toBe(dialog.id);
	});

	it('fires onOpenChange(false) when the panel is dismissed with Escape', async () => {
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
