/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render} from '@testing-library/react';
import React from 'react';

import Translation from '../../../../src/main/resources/META-INF/resources/js/components/layout_reports/Translation';

import '@testing-library/jest-dom';

const noop = () => {};

describe('Translation', () => {
	afterEach(cleanup);

	it('renders all available languages', () => {
		const testProps = {
			defaultLanguageId: 'en-US',
			pageURLs: [
				{
					languageId: 'en-US',
					languageLabel: 'English',
					title: 'Home',
					url: 'http://foo.com:8080/en/web/guest/home',
				},
				{
					languageId: 'es-ES',
					languageLabel: 'Spanish',
					title: 'Inicio',
					url: 'http://foo.com:8080/es/en/web/guest/inicio',
				},
			],
			selectedLanguageId: 'en-US',
		};

		const {getAllByText, getByText} = render(
			<Translation
				defaultLanguageId={testProps.defaultLanguageId}
				onSelectedLanguageId={noop}
				pageURLs={testProps.pageURLs}
				selectedLanguageId={testProps.selectedLanguageId}
			/>
		);

		const defaultLanguageId = getAllByText('English');
		expect(defaultLanguageId.length === 1).toBe(true);
		expect(defaultLanguageId[0]).toBeInTheDocument();

		expect(getByText('Spanish')).toBeInTheDocument();
	});
});
