/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render} from '@testing-library/react';
import React from 'react';

import NotConfigured from '../../../../src/main/resources/META-INF/resources/js/components/layout_reports/NotConfigured';

import '@testing-library/jest-dom';

import {StoreContextProvider} from '../../../../src/main/resources/META-INF/resources/js/context/StoreContext';

describe('NotConfigured', () => {
	afterEach(cleanup);

	it('renders empty view with information', () => {
		const {getByText} = render(
			<StoreContextProvider
				value={{
					data: {
						configureGooglePageSpeedURL: null,
						imagesPath: 'imagesPath',
					},
				}}
			>
				<NotConfigured />
			</StoreContextProvider>
		);

		expect(
			getByText(
				"check-issues-that-impact-on-your-page's-accessibility-and-seo"
			)
		).toBeInTheDocument();
		expect(
			getByText(
				'connect-with-google-pagespeed-from-site-settings-pages-google-pagespeed'
			)
		).toBeInTheDocument();
	});

	it('renders empty view with information and button', () => {
		const {getByText} = render(
			<StoreContextProvider
				value={{
					data: {
						configureGooglePageSpeedURL: 'validURL',
						imagesPath: 'imagesPath',
					},
				}}
			>
				<NotConfigured />
			</StoreContextProvider>
		);

		expect(
			getByText(
				"check-issues-that-impact-on-your-page's-accessibility-and-seo"
			)
		).toBeInTheDocument();
		expect(getByText('configure')).toBeInTheDocument();
		expect(
			getByText('configure-google-pagespeed-to-run-a-page-audit')
		).toBeInTheDocument();
	});

	it('renders the proper image', () => {
		const {getByAltText} = render(
			<StoreContextProvider
				value={{
					data: {
						configureGooglePageSpeedURL: 'validURL',
						imagesPath: 'imagesPath',
					},
				}}
			>
				<NotConfigured />
			</StoreContextProvider>
		);

		const image = getByAltText('default-page-audit-image-alt-description');

		expect(image.src).toContain('issues_configure');
	});
});
