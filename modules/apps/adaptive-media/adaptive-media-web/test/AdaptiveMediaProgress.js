/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import React from 'react';

import AdaptiveMediaProgress from '../src/main/resources/META-INF/resources/js/AdaptiveMediaProgress';

function createMenu({adapting = false, uuid = 'uuid'} = {}) {
	const menu = document.createElement('ul');

	const entryClass = adapting ? 'disabled' : '';
	const onClickAttribute = adapting ? 'data-onclick' : 'onclick';

	menu.innerHTML = `
		<li class="${entryClass}">
			<a id="_portlet_icon-adapt-remaining${uuid}" ${onClickAttribute}="adaptRemaining()">Adapt Remaining</a>
		</li>
		<li class="${entryClass}">
			<a id="_portlet_icon-disable-${uuid}" ${onClickAttribute}="disable()">Disable</a>
		</li>
	`;

	return menu;
}

async function adaptRemainingAfterMenuCloses(menu) {
	render(
		<AdaptiveMediaProgress
			adaptedImages={0}
			adaptiveMediaProgressComponentId="adaptiveMediaProgress"
			namespace="_portlet_"
			percentageUrl="http://localhost/percentage"
			totalImages={2}
			uuid="uuid"
		/>
	);

	document.body.appendChild(menu);

	const [, {startProgress}] = Liferay.component.mock.calls.find(
		([id]) => id === 'adaptiveMediaProgress'
	);

	fetch.mockResponseOnce('');

	act(() => {
		startProgress('http://localhost/background-task');
	});

	const disableEntry = screen.getByText('Disable');

	expect(disableEntry.parentElement).toHaveClass('disabled');

	menu.remove();

	fetch.mockResponseOnce(
		JSON.stringify({adaptedImages: 2, errors: 0, totalImages: 2})
	);

	await act(async () => {
		jest.runOnlyPendingTimers();
	});

	expect(screen.getByRole('progressbar')).toHaveAttribute(
		'aria-valuenow',
		'100'
	);

	expect(disableEntry.parentElement).not.toHaveClass('disabled');
	expect(disableEntry).toHaveAttribute('onclick', 'disable()');
}

describe('AdaptiveMediaProgress', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		jest.useFakeTimers();
	});

	afterEach(() => {
		document.body.innerHTML = '';

		jest.useRealTimers();
	});

	it('enables the disable entry when adapting ends after the menu is closed', async () => {
		const menu = createMenu();

		document.body.appendChild(menu);

		await adaptRemainingAfterMenuCloses(menu);
	});

	it('enables the disable entry when the menu is added after mount', async () => {
		await adaptRemainingAfterMenuCloses(createMenu());
	});

	it('enables the disable entry when an autostarted progress ends after the menu is closed', async () => {
		const menu = createMenu({adapting: true, uuid: 'auto'});

		document.body.appendChild(menu);

		fetch.mockResponseOnce(
			JSON.stringify({adaptedImages: 1, errors: 0, totalImages: 2})
		);

		render(
			<AdaptiveMediaProgress
				adaptedImages={1}
				adaptiveMediaProgressComponentId="adaptiveMediaProgress"
				autoStartProgress
				namespace="_portlet_"
				percentageUrl="http://localhost/percentage"
				totalImages={2}
				uuid="auto"
			/>
		);

		await act(async () => {
			await Promise.resolve();
		});

		const disableEntry = screen.getByText('Disable');

		expect(disableEntry.parentElement).toHaveClass('disabled');

		menu.remove();

		fetch.mockResponseOnce(
			JSON.stringify({adaptedImages: 2, errors: 0, totalImages: 2})
		);

		await act(async () => {
			jest.runOnlyPendingTimers();
		});

		expect(screen.getByRole('progressbar')).toHaveAttribute(
			'aria-valuenow',
			'100'
		);

		expect(disableEntry.parentElement).not.toHaveClass('disabled');
		expect(disableEntry).toHaveAttribute('onclick', 'disable()');
	});
});
