/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {initializeConfig} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/index';
import ElementVariationsSimulation from '../../../../src/main/resources/META-INF/resources/page_editor/plugins/element_variations/ElementVariationsSimulation';
import {Config} from '../../../../src/main/resources/META-INF/resources/page_editor/types/config';

const AUDIENCES = [
	{label: 'Loyal Customers', value: 'audience-1'},
	{label: 'New Visitors', value: 'audience-2'},
];

const EXPERIENCES = [
	{
		audienceEntryERCs: ['audience-2'],
		label: 'Default',
		segmentsExperienceERC: 'experience-1',
		segmentsExperienceId: 1,
	},
];

function createNavigateEvent({
	navigationType,
	url,
}: {
	navigationType: string;
	url: string;
}) {
	return Object.assign(new Event('navigate', {cancelable: true}), {
		destination: {url},
		navigationType,
	});
}

function loadIframe(iframe: HTMLIFrameElement) {
	(iframe.contentWindow as any).Liferay = Liferay;
	(iframe.contentWindow as any).navigation = new EventTarget();

	fireEvent.load(iframe);
}

async function getSimulationURL() {
	const iframe = await screen.findByTitle('page-simulation');

	return new URL(iframe.getAttribute('src') as string);
}

async function openSimulation() {
	const [button] = screen.getAllByTitle('simulation');

	await userEvent.click(button);
}

async function openNewTabSimulation() {
	const [, button] = screen.getAllByTitle('simulation');

	await userEvent.click(button);
}

function renderElementVariationsSimulation() {
	return render(
		<ElementVariationsSimulation
			audiences={AUDIENCES}
			defaultLanguageId="en_US"
			experiences={EXPERIENCES}
			languageId="en_US"
			locales={[{id: 'en_US', label: 'English', symbol: 'en-us'}]}
			previewURL="/preview?segmentsExperienceId=0"
			segmentsExperienceERC="experience-1"
		/>
	);
}

describe('ElementVariationsSimulation', () => {
	beforeEach(() => {
		const container = document.createElement('div');

		container.id = 'elementVariationsSimulationContainer';

		document.body.appendChild(container);

		initializeConfig({
			availableViewportSizes: {
				desktop: {
					icon: 'desktop',
					label: 'Desktop',
					maxWidth: '',
					minWidth: '992',
					sizeId: 'desktop',
				},
				portraitMobile: {
					icon: 'mobile-portrait',
					label: 'Portrait Phone',
					maxWidth: '540',
					minWidth: '0',
					sizeId: 'portraitMobile',
				},
			},
			portletNamespace: '_com_liferay_test_',
		} as unknown as Config);
	});

	afterEach(() => {
		document
			.getElementById('elementVariationsSimulationContainer')
			?.remove();
	});

	it('simulates the first audience of the experience', async () => {
		renderElementVariationsSimulation();

		await openSimulation();

		const simulationURL = await getSimulationURL();

		expect(simulationURL.searchParams.get('audienceEntryERCs')).toBe(
			'audience-2'
		);
		expect(simulationURL.searchParams.get('languageId')).toBe('en_US');
		expect(simulationURL.searchParams.get('segmentsExperienceId')).toBe(
			'1'
		);
	});

	it('simulates the selected audience', async () => {
		renderElementVariationsSimulation();

		await openSimulation();

		await userEvent.click(await screen.findByLabelText('audience'));

		await userEvent.click(
			screen.getByRole('option', {name: 'Loyal Customers'})
		);

		expect(
			(await getSimulationURL()).searchParams.get('audienceEntryERCs')
		).toBe('audience-1');
	});

	it('shows the page once it finishes loading', async () => {
		renderElementVariationsSimulation();

		await openSimulation();

		const iframe = await screen.findByTitle('page-simulation');

		expect(iframe.parentElement).toHaveAttribute('aria-busy', 'true');

		loadIframe(iframe as HTMLIFrameElement);

		expect(iframe.parentElement).toHaveAttribute('aria-busy', 'false');
	});

	it('keeps the page from navigating to other URLs', async () => {
		renderElementVariationsSimulation();

		await openSimulation();

		const iframe = (await screen.findByTitle(
			'page-simulation'
		)) as HTMLIFrameElement;

		loadIframe(iframe);

		const navigateEvent = createNavigateEvent({
			navigationType: 'push',
			url: 'http://localhost/other-page',
		});

		(iframe.contentWindow as any).navigation.dispatchEvent(navigateEvent);

		expect(navigateEvent.defaultPrevented).toBe(true);
	});

	it('lets the page reload', async () => {
		renderElementVariationsSimulation();

		await openSimulation();

		const iframe = (await screen.findByTitle(
			'page-simulation'
		)) as HTMLIFrameElement;

		loadIframe(iframe);

		const navigateEvent = createNavigateEvent({
			navigationType: 'reload',
			url: iframe.src,
		});

		(iframe.contentWindow as any).navigation.dispatchEvent(navigateEvent);

		expect(navigateEvent.defaultPrevented).toBe(false);
	});

	it('simulates in a new tab from the small screen button', async () => {
		const windowOpen = jest
			.spyOn(window, 'open')
			.mockImplementation(() => null);

		renderElementVariationsSimulation();

		await openNewTabSimulation();

		expect(screen.queryByTitle('page-simulation')).not.toBeInTheDocument();

		await userEvent.click(await screen.findByLabelText('audience'));

		await userEvent.click(
			screen.getByRole('option', {name: 'Loyal Customers'})
		);

		await userEvent.click(
			screen.getByRole('button', {name: 'simulate-in-a-new-tab'})
		);

		const simulationURL = new URL(windowOpen.mock.calls[0][0] as string);

		expect(simulationURL.searchParams.get('audienceEntryERCs')).toBe(
			'audience-1'
		);
		expect(simulationURL.searchParams.get('languageId')).toBe('en_US');
		expect(simulationURL.searchParams.get('segmentsExperienceId')).toBe(
			'1'
		);

		windowOpen.mockRestore();
	});

	it('has no accessibility violations', async () => {
		renderElementVariationsSimulation();

		await openSimulation();

		await screen.findByTitle('page-simulation');

		await checkAccessibility({
			bestPractices: true,
			context: document.body,
		});
	});
});
