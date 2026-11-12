/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import UnifiedSidebarToggler from '../../src/main/resources/META-INF/resources/unified_sidebar/UnifiedSidebarToggler';

function changeState(visible: boolean) {
	const [, handleStateChanged] = (Liferay.on as jest.Mock).mock.calls.find(
		([eventName]) => eventName === 'stateChanged'
	);

	act(() => handleStateChanged({visible}));
}

function pressShortcut(
	target: Element = document.body,
	options: KeyboardEventInit = {}
) {
	fireEvent.keyDown(target, {
		altKey: true,
		code: 'KeyM',
		ctrlKey: true,
		key: 'm',
		...options,
	});
}

function renderToggler(visible: boolean) {
	render(
		<UnifiedSidebarToggler
			navigationId="navigationId"
			stateChangedEventName="stateChanged"
			stateRequestedEventName="stateRequested"
			visible={visible}
		/>
	);

	return screen.getByRole('tab');
}

describe('UnifiedSidebarToggler', () => {
	let navigation: HTMLElement;

	beforeEach(() => {
		jest.clearAllMocks();

		navigation = document.createElement('div');

		navigation.classList.add('c-slideout-show');
		navigation.id = 'navigationId';

		document.body.appendChild(navigation);
	});

	afterEach(() => {
		navigation.remove();
	});

	it('advertises the shortcut', () => {
		expect(renderToggler(true)).toHaveAttribute(
			'aria-keyshortcuts',
			'Control+Alt+M'
		);
	});

	it('announces the same state again after another change', () => {
		renderToggler(true);

		pressShortcut();

		changeState(false);
		changeState(true);

		expect(
			screen.queryByText('the-product-menu-is-closed')
		).not.toBeInTheDocument();

		pressShortcut();

		changeState(false);

		expect(
			screen.getByText('the-product-menu-is-closed')
		).toBeInTheDocument();
	});

	it('announces the state the shortcut changed', () => {
		renderToggler(true);

		changeState(false);

		expect(
			screen.queryByText('the-product-menu-is-closed')
		).not.toBeInTheDocument();

		pressShortcut();

		changeState(true);

		expect(screen.getByText('the-product-menu-is-open')).toHaveAttribute(
			'aria-live',
			'polite'
		);
	});

	it('controls the given navigation', () => {
		const toggler = renderToggler(true);

		expect(toggler).toHaveAttribute('aria-controls', 'navigationId');
		expect(toggler).toHaveAttribute('aria-expanded', 'true');
	});

	it('does not announce a click after the shortcut', async () => {
		const toggler = renderToggler(true);

		pressShortcut();

		changeState(false);

		const liveRegion = document.querySelector('[aria-live="polite"]')!;

		const observer = new MutationObserver(() => {});

		observer.observe(liveRegion, {
			characterData: true,
			characterDataOldValue: true,
			childList: true,
			subtree: true,
		});

		await userEvent.click(toggler);

		changeState(true);

		const texts = [
			...observer
				.takeRecords()
				.flatMap(({addedNodes, oldValue, removedNodes}) => [
					oldValue,
					...Array.from(addedNodes, (node) => node.textContent),
					...Array.from(removedNodes, (node) => node.textContent),
				]),
			liveRegion.textContent,
		];

		observer.disconnect();

		expect(texts).not.toContain('the-product-menu-is-open');
	});

	it('follows a navigation that rendered before it', () => {
		navigation.classList.remove('c-slideout-show');

		expect(renderToggler(true)).toHaveAttribute('aria-expanded', 'false');
	});

	it('ignores the shortcut on another key', () => {
		renderToggler(true);

		pressShortcut(document.body, {code: 'KeyN', key: 'n'});

		expect(Liferay.fire).not.toHaveBeenCalled();
	});

	it('ignores the shortcut while held down', () => {
		renderToggler(true);

		pressShortcut(document.body, {repeat: true});

		expect(Liferay.fire).not.toHaveBeenCalled();
	});

	it('ignores the shortcut while typing', () => {
		renderToggler(true);

		const editable = document.createElement('div');

		editable.setAttribute('contenteditable', 'true');

		const input = document.createElement('input');

		document.body.append(editable, input);

		try {
			pressShortcut(editable);
			pressShortcut(input);

			expect(Liferay.fire).not.toHaveBeenCalled();
		}
		finally {
			editable.remove();
			input.remove();
		}
	});

	it('ignores the shortcut with other modifiers', () => {
		renderToggler(true);

		pressShortcut(document.body, {metaKey: true});
		pressShortcut(document.body, {shiftKey: true});

		expect(Liferay.fire).not.toHaveBeenCalled();
	});

	it('ignores the shortcut without a navigation', () => {
		navigation.remove();

		renderToggler(true);

		pressShortcut();

		expect(Liferay.fire).not.toHaveBeenCalled();
	});

	it('keeps the announcement when the navigation repeats its state', () => {
		renderToggler(true);

		pressShortcut();

		changeState(false);
		changeState(false);

		expect(
			screen.getByText('the-product-menu-is-closed')
		).toBeInTheDocument();
	});

	it('moves the focus to itself when the navigation hides', async () => {
		const link = document.createElement('a');

		link.href = '#';

		navigation.appendChild(link);

		const toggler = renderToggler(true);

		link.focus();

		changeState(false);

		await waitFor(() => expect(toggler).toHaveFocus());
	});

	it('refocuses itself when it has the focus as the navigation hides', async () => {
		const toggler = renderToggler(true);

		toggler.focus();

		const blur = jest.spyOn(toggler, 'blur');
		const focus = jest.spyOn(toggler, 'focus');

		changeState(false);

		await waitFor(() => expect(focus).toHaveBeenCalled());

		expect(blur.mock.invocationCallOrder[0]).toBeLessThan(
			focus.mock.invocationCallOrder[0]
		);
		expect(toggler).toHaveFocus();
	});

	it('requests the opposite state on click', async () => {
		await userEvent.click(renderToggler(true));

		expect(Liferay.fire).toHaveBeenCalledWith('stateRequested', {
			visible: false,
		});
	});

	it('requests the opposite state with the shortcut', () => {
		renderToggler(true);

		pressShortcut();

		expect(Liferay.fire).toHaveBeenCalledWith('stateRequested', {
			visible: false,
		});
	});

	it('responds to the shortcut in a non editable part of an editor', () => {
		renderToggler(true);

		const editable = document.createElement('div');

		editable.setAttribute('contenteditable', 'true');

		const widget = document.createElement('div');

		widget.setAttribute('contenteditable', 'false');

		editable.appendChild(widget);

		document.body.appendChild(editable);

		try {
			pressShortcut(widget);

			expect(Liferay.fire).toHaveBeenCalledWith('stateRequested', {
				visible: false,
			});
		}
		finally {
			editable.remove();
		}
	});

	it('responds to the shortcut on the key labeled M', () => {
		renderToggler(true);

		pressShortcut(document.body, {code: 'Semicolon', key: 'm'});

		expect(Liferay.fire).toHaveBeenCalledWith('stateRequested', {
			visible: false,
		});
	});

	it('responds to the shortcut on the M key position', () => {
		renderToggler(true);

		pressShortcut(document.body, {key: 'µ'});
		pressShortcut(document.body, {key: 'ь'});

		expect(Liferay.fire).toHaveBeenCalledTimes(2);
	});
});
