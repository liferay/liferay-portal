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

import {ChatPanel} from '../../src/main/resources/META-INF/resources';
import {ChatPanelContext} from '../../src/main/resources/META-INF/resources/ai-assistant-chat/ChatPanelContext';

function renderChatPanel(onClose: () => void) {
	return render(
		<ChatPanelContext.Provider
			value={{
				dialogId: 'd1',
				onClose,
				titleId: 't1',
			}}
		>
			<ChatPanel>
				<ChatPanel.Header title="Chat" />

				<ChatPanel.Body>Hello</ChatPanel.Body>
			</ChatPanel>
		</ChatPanelContext.Provider>
	);
}

describe('ChatPanel', () => {
	it('renders the header title and body content', () => {
		renderChatPanel(jest.fn());

		expect(screen.getByText('Chat')).toBeInTheDocument();
		expect(screen.getByText('Hello')).toBeInTheDocument();
	});

	it('exposes an accessible name on the close button', () => {
		renderChatPanel(jest.fn());

		expect(screen.getByRole('button', {name: 'close'})).toBeInTheDocument();
	});

	it('renders dialog semantics labelled by the header title', () => {
		renderChatPanel(jest.fn());

		const dialog = screen.getByRole('dialog');
		const title = screen.getByText('Chat');

		expect(dialog).toHaveAccessibleName('Chat');
		expect(dialog).toHaveAttribute('aria-labelledby', title.id);
	});

	it('fires onClose when the close button is clicked', async () => {
		const onClose = jest.fn();

		renderChatPanel(onClose);

		await userEvent.click(screen.getByRole('button', {name: 'close'}));

		expect(onClose).toHaveBeenCalledTimes(1);
	});

	it('has no accessibility violations', async () => {
		const {container} = renderChatPanel(jest.fn());

		await checkAccessibility({bestPractices: true, context: container});
	});
});
