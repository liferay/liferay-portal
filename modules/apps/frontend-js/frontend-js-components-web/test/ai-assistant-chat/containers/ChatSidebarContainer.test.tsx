/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import, @liferay/no-extraneous-dependencies
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import {ChatSidebarContainer} from '../../../src/main/resources/META-INF/resources';
import ChatPanel from '../../../src/main/resources/META-INF/resources/ai-assistant-chat';

function ChatSidebarContainerFixture({
	onOpenChange,
	open = true,
	trigger = <button type="button">Open Chat</button>,
}: {
	onOpenChange: (open: boolean) => void;
	open?: boolean;
	trigger?: React.ReactElement & {ref?: React.Ref<HTMLElement>};
}) {
	const containerRef = React.useRef<HTMLDivElement>(null);

	return (
		<div ref={containerRef}>
			<ChatSidebarContainer
				containerRef={containerRef}
				onOpenChange={onOpenChange}
				open={open}
				trigger={trigger}
			>
				<ChatPanel>
					<ChatPanel.Header title="Chat" />

					<ChatPanel.Body>Chat Content</ChatPanel.Body>
				</ChatPanel>
			</ChatSidebarContainer>
		</div>
	);
}

function renderContainer(
	onOpenChange: (open: boolean) => void,
	options?: {
		open?: boolean;
		trigger?: React.ReactElement & {ref?: React.Ref<HTMLElement>};
	}
) {
	return render(
		<ChatSidebarContainerFixture
			onOpenChange={onOpenChange}
			open={options?.open}
			trigger={options?.trigger}
		/>
	);
}

describe('ChatSidebarContainer', () => {
	beforeEach(() => {
		jest.spyOn(
			HTMLElement.prototype,
			'offsetParent',
			'get'
		).mockReturnValue(document.body);

		jest.spyOn(HTMLElement.prototype, 'clientWidth', 'get').mockReturnValue(
			1024
		);
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('renders the trigger and the chat content', () => {
		renderContainer(jest.fn());

		expect(
			screen.getByRole('button', {name: 'Open Chat'})
		).toBeInTheDocument();
		expect(screen.getByText('Chat Content')).toBeInTheDocument();
	});

	it('renders the trigger wired to the dialog it controls', () => {
		renderContainer(jest.fn());

		const trigger = screen.getByRole('button', {name: 'Open Chat'});
		const dialog = screen.getByRole('dialog');

		expect(trigger).toHaveAttribute('aria-haspopup', 'dialog');
		expect(trigger).toHaveAttribute('aria-expanded', 'true');
		expect(trigger.getAttribute('aria-controls')).toBe(dialog.id);
	});

	it('renders the trigger with aria-expanded false when closed', () => {
		renderContainer(jest.fn(), {open: false});

		expect(screen.getByRole('button', {name: 'Open Chat'})).toHaveAttribute(
			'aria-expanded',
			'false'
		);
	});

	it("fires the trigger's own onClick since the sidebar passes none", async () => {
		const triggerOnClick = jest.fn();

		renderContainer(jest.fn(), {
			trigger: (
				<button onClick={triggerOnClick} type="button">
					Open Chat
				</button>
			),
		});

		await userEvent.click(screen.getByRole('button', {name: 'Open Chat'}));

		expect(triggerOnClick).toHaveBeenCalledTimes(1);
	});

	it('fires onOpenChange when the panel is dismissed with Escape', async () => {
		const onOpenChange = jest.fn();

		renderContainer(onOpenChange);

		await userEvent.keyboard('{Escape}');

		expect(onOpenChange).toHaveBeenCalledTimes(1);
		expect(onOpenChange).toHaveBeenCalledWith(false);
	});

	it('fires onOpenChange when the header close button is clicked', async () => {
		const onOpenChange = jest.fn();

		renderContainer(onOpenChange);

		await userEvent.click(screen.getByRole('button', {name: 'close'}));

		expect(onOpenChange).toHaveBeenCalledTimes(1);
		expect(onOpenChange).toHaveBeenCalledWith(false);
	});

	it('has no accessibility violations', async () => {
		const {container} = renderContainer(jest.fn());

		await checkAccessibility({bestPractices: true, context: container});
	});
});
