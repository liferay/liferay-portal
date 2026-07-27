/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';
import {fireEvent, render as renderTL, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import AIAssistant, {
	close,
	getState,
	open,
	releaseHost,
} from '../../../src/main/resources/META-INF/resources/js/AIAssistantChat/AIAssistant';
import AIAssistantTriggerButton from '../../../src/main/resources/META-INF/resources/js/AIAssistantChat/AIAssistantTriggerButton';

jest.mock('@liferay/frontend-js-react-web', () => ({
	render: jest.fn(),
}));

const HOST_SELECTOR = '#ai-assistant-host-root';

function hostCount() {
	return document.querySelectorAll(HOST_SELECTOR).length;
}

function command(triggerId: string) {
	return {instructionDefinitionScope: 'cms', triggerId};
}

afterEach(() => {
	close();
});

describe('AIAssistant single-host invariants', () => {
	it('mounts exactly one host, no matter how many opens', () => {
		expect(hostCount()).toBe(0);

		open(command('a'));
		open(command('b'));
		open(command('c'));

		expect(hostCount()).toBe(1);
		expect(render).toHaveBeenCalledTimes(1);
	});

	it('opening another trigger replaces the active one (never two open)', () => {
		open(command('a'));

		expect(getState().command?.triggerId).toBe('a');

		open(command('b'));

		expect(getState().command?.triggerId).toBe('b');
	});

	it('remounts the host after an SPA navigation unmounts it', () => {
		open(command('a'));

		expect(render).toHaveBeenCalledTimes(1);

		releaseHost();

		expect(getState().command).toBeNull();

		open(command('b'));

		expect(hostCount()).toBe(1);
		expect(render).toHaveBeenCalledTimes(2);
		expect(getState().command?.triggerId).toBe('b');
	});

	it('close clears the command', () => {
		open(command('a'));

		expect(getState().command).not.toBeNull();

		close();

		expect(getState().command).toBeNull();
	});

	it('routes the public aiAssistant:command event to the one host', () => {
		const [, handler] =
			(Liferay.on as jest.Mock).mock.calls.find(
				([event]) => event === 'aiAssistant:command'
			) ?? [];

		expect(handler).toBeDefined();

		handler(command('from-event'));

		expect(getState().command?.triggerId).toBe('from-event');
		expect(hostCount()).toBe(1);
	});

	it('exposes a stable default controller API', () => {
		expect(typeof AIAssistant.open).toBe('function');
		expect(typeof AIAssistant.close).toBe('function');
		expect(typeof AIAssistant.subscribe).toBe('function');
	});
});

describe('AIAssistantTriggerButton', () => {
	it('marks only the trigger that is driving the host as expanded', () => {
		renderTL(
			<>
				<AIAssistantTriggerButton
					instructionDefinitionScope="cms"
					label="Toolbar"
					triggerId="toolbar"
				/>
				<AIAssistantTriggerButton
					instructionDefinitionScope="cms"
					label="Attachment"
					triggerId="attachment"
				/>
			</>
		);

		const toolbar = screen.getByRole('button', {name: 'Toolbar'});
		const attachment = screen.getByRole('button', {name: 'Attachment'});

		expect(toolbar).toHaveAttribute('aria-expanded', 'false');

		fireEvent.click(toolbar);

		expect(toolbar).toHaveAttribute('aria-expanded', 'true');
		expect(attachment).toHaveAttribute('aria-expanded', 'false');

		fireEvent.click(attachment);

		expect(toolbar).toHaveAttribute('aria-expanded', 'false');
		expect(attachment).toHaveAttribute('aria-expanded', 'true');

		fireEvent.click(attachment);

		expect(attachment).toHaveAttribute('aria-expanded', 'false');
		expect(getState().command).toBeNull();
	});
});
