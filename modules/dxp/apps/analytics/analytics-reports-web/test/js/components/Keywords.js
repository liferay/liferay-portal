/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import Keywords from '../../../src/main/resources/META-INF/resources/js/components/Keywords';

import '@testing-library/jest-dom';

describe('Keywords', () => {
	it('renders message no best keywords when content was published today', () => {
		const mockCurrentPage = {
			data: {
				countrySearchKeywords: [
					{
						countryCode: 'us',
						countryName: 'United States',
						keywords: [],
					},
				],
				helpMessage:
					'This number refers to the volume of people that find your page through a search engine.',
				name: 'organic',
				share: 0,
				title: 'Organic',
				value: 0,
			},
			view: 'organic',
		};

		const {getByText} = render(<Keywords currentPage={mockCurrentPage} />);

		expect(getByText('there-are-no-best-keywords-yet')).toBeInTheDocument();
	});
});
