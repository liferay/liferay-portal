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

function loadIframe(iframe: HTMLIFrameElement) {
	(iframe.contentWindow as any).Liferay = Liferay;

	fireEvent.load(iframe);
}

async function getSimulationURL() {
	const iframe = await screen.findByTitle('page-simulation');

	return new URL(iframe.getAttribute('src') as string);
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

		await userEvent.click(screen.getByTitle('simulation'));

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

		await userEvent.click(screen.getByTitle('simulation'));

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

		await userEvent.click(screen.getByTitle('simulation'));

		const iframe = await screen.findByTitle('page-simulation');

		expect(iframe.parentElement).toHaveAttribute('aria-busy', 'true');

		loadIframe(iframe as HTMLIFrameElement);

		expect(iframe.parentElement).toHaveAttribute('aria-busy', 'false');
	});

	it('keeps links in the page from navigating', async () => {
		renderElementVariationsSimulation();

		await userEvent.click(screen.getByTitle('simulation'));

		const iframe = (await screen.findByTitle(
			'page-simulation'
		)) as HTMLIFrameElement;

		const iframeDocument = iframe.contentDocument as Document;

		iframeDocument.write('<a href="/other-page">Other page</a>');
		iframeDocument.close();

		loadIframe(iframe);

		const clickEvent = new MouseEvent('click', {
			bubbles: true,
			cancelable: true,
		});

		iframeDocument.querySelector('a')?.dispatchEvent(clickEvent);

		expect(clickEvent.defaultPrevented).toBe(true);
	});

	it('has no accessibility violations', async () => {
		renderElementVariationsSimulation();

		await userEvent.click(screen.getByTitle('simulation'));

		await screen.findByTitle('page-simulation');

		await checkAccessibility({
			bestPractices: true,
			context: document.body,
		});
	});
});
