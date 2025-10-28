/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render} from '@testing-library/react';
import React from 'react';

import BasicInformation from '../../../../src/main/resources/META-INF/resources/js/components/layout_reports/BasicInformation';

import '@testing-library/jest-dom';

const testProps = {
	defaultLanguageId: 'en-US',
	onLanguageChange: () => {},
	pageURLs: [
		{
			languageId: 'en-US',
			title: 'Home',
			url: 'http://foo.com:8080/en/web/guest/home',
		},
		{
			languageId: 'es-ES',
			title: 'Inicio',
			url: 'http://foo.com:8080/es/en/web/guest/inicio',
		},
	],
	selectedLanguageId: 'es-ES',
};

describe('BasicInformation', () => {
	afterEach(cleanup);

	it('renders page title, pageURL and languages', () => {
		const {getByText} = render(
			<BasicInformation
				defaultLanguageId={testProps.defaultLanguageId}
				onLanguageChange={testProps.onLanguageChange}
				pageURLs={testProps.pageURLs}
			/>
		);

		expect(getByText('Home')).toBeInTheDocument();
		expect(
			getByText('http://foo.com:8080/en/web/guest/home')
		).toBeInTheDocument();
	});

	it('renders corresponding page title and pageURL changed when getting a selected language id', () => {
		const {getByText} = render(
			<BasicInformation
				defaultLanguageId={testProps.defaultLanguageId}
				onLanguageChange={testProps.onLanguageChange}
				pageURLs={testProps.pageURLs}
				selectedLanguageId={testProps.selectedLanguageId}
			/>
		);

		expect(getByText('Inicio')).toBeInTheDocument();
		expect(
			getByText('http://foo.com:8080/es/en/web/guest/inicio')
		).toBeInTheDocument();
	});
});
