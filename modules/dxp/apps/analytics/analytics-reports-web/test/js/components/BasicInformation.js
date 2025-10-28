/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import BasicInformation from '../../../src/main/resources/META-INF/resources/js/components/BasicInformation';
import {StoreStateContext} from '../../../src/main/resources/META-INF/resources/js/context/StoreContext';

import '@testing-library/jest-dom';

jest.mock('frontend-js-web', () => ({
	sub: jest.fn((langKey, arg) => langKey.replace('x', arg)),
}));

describe('BasicInformation', () => {
	it('renders author, publish date and title', () => {
		const testProps = {
			author: {
				authorId: '',
				name: 'John Tester',
				url: '',
			},
			canonicalURL:
				'http://localhost:8080/en/web/guest/-/basic-web-content',
			onSelectedLanguageClick: () => {},
			publishDate: 'Thu Sep 20 08:17:57 GMT 2021',
			title: 'A testing page',
			viewURLs: [
				{
					default: true,
					languageId: 'en-US',
					languageLabel: 'English (United States)',
					selected: true,
					viewURL:
						'http://localhost:8080/en/web/guest/-/basic-web-content',
				},
				{
					default: false,
					languageId: 'es-ES',
					languageLabel: 'Spanish (Spain)',
					selected: false,
					viewURL:
						'http://localhost:8080/es/web/guest/-/contenido-web-basico',
				},
			],
		};

		const {getByText} = render(
			<StoreStateContext.Provider value={{languageTag: 'en-US'}}>
				<BasicInformation {...testProps} />
			</StoreStateContext.Provider>
		);

		expect(getByText(testProps.title)).toBeInTheDocument();

		expect(getByText(testProps.canonicalURL)).toBeInTheDocument();

		const formattedPublishDate = 'September 20, 2021';

		expect(
			getByText('published-on-' + formattedPublishDate)
		).toBeInTheDocument();

		expect(
			getByText('authored-by-' + testProps.author.name)
		).toBeInTheDocument();
	});
});
