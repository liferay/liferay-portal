/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen, within} from '@testing-library/react';
import React from 'react';

import AssetTypeInfoPanelContent from '../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/AssetTypeInfoPanelContent';
import {DOCUMENT_OBJECT_ENTRY} from './mocks';

describe.skip('CMS Asset Type Info Panel', () => {
	const {Liferay: originalLiferay} = window;

	beforeEach(() => {
		window['Liferay'] = {
			...originalLiferay,

			// @ts-ignore

			detach: (name, fn) => {

				// @ts-ignore

				window.removeEventListener(name, fn);
			},
			fire: (name, payload) => {
				const event = document.createEvent('CustomEvent');

				event.initCustomEvent(name);

				if (payload) {
					Object.keys(payload).forEach((key: string) => {

						// @ts-ignore

						event[key] = payload[key];
					});
				}

				window.dispatchEvent(event);
			},

			// @ts-ignore

			on: (name, fn) => {

				// @ts-ignore

				window.addEventListener(name, fn);
			},
		};
	});

	afterEach(() => {
		cleanup();

		window.Liferay = originalLiferay;

		jest.resetAllMocks();
	});

	it('renders the component for Basic Web Content asset type', async () => {
		const {container} = render(
			<AssetTypeInfoPanelContent
				additionalProps={{}}
				items={[DOCUMENT_OBJECT_ENTRY] as any}
			/>
		);
		let href = null;

		expect(container).toBeInTheDocument();

		const assetElement: HTMLElement | null =
			container.querySelector('.asset-title');

		expect(assetElement?.innerHTML.includes('591.pdf')).toBe(true);

		const useElement = container.querySelector('svg.lexicon-icon use');

		if (useElement) {
			href = useElement.getAttribute('href');
		}

		expect(href).toContain('document-image');

		const tabList = screen.getByRole('tablist');

		const tabElements = within(tabList).getAllByRole('tab');

		expect(tabElements.length).toBe(4);

		const tabTexts = tabElements.map((element) => element.textContent);

		expect(tabTexts).toEqual([
			'details',
			'categorization',
			'performance',
			'versions',
		]);
	});
});
