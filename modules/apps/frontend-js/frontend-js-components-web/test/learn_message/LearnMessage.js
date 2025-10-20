/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import LearnMessage, {
	LearnResourcesContext,
} from '../../src/main/resources/META-INF/resources/learn_message/LearnMessage';

const MESSAGE = 'Learn more.';
const URL =
	'https://learn.liferay.com/dxp/latest/en/using-search/search-pages-and-widgets/search-bar-suggestions.html';

const LEARN_RESOURCES_MOCK_DATA = {
	'portal-search-web': {
		'search-bar-suggestions': {
			en_US: {
				message: MESSAGE,
				url: URL,
			},
		},
	},
};

const LearnMessageWithContext = (props) => {
	return (
		<LearnResourcesContext.Provider value={LEARN_RESOURCES_MOCK_DATA}>
			<LearnMessage {...props} />
		</LearnResourcesContext.Provider>
	);
};

describe('LearnMessage', () => {
	it('displays the localized message string and url', () => {
		const {getByText} = render(
			<LearnMessageWithContext
				resource="portal-search-web"
				resourceKey="search-bar-suggestions"
			/>
		);

		const element = getByText(MESSAGE);

		expect(element).not.toBeNull();

		expect(element.href).toEqual(URL);
	});

	it('displays aria label and shortcut icon', () => {
		const {getByLabelText} = render(
			<LearnMessageWithContext
				resource="portal-search-web"
				resourceKey="search-bar-suggestions"
			/>
		);

		const element = getByLabelText('x-opens-new-window');

		expect(element).not.toBeNull();

		expect(
			element
				.querySelector('svg')
				.classList.contains('lexicon-icon-shortcut')
		).toBe(true);
	});

	it('displays nothing if LearnResourcesContext is missing', () => {
		const {container} = render(
			<LearnMessage
				resource="portal-search-web"
				resourceKey="search-bar-suggestions"
			/>
		);

		expect(container.firstChild).toBeNull();
	});

	it('displays nothing if resource and resourceKey props are not defined', () => {
		const {container} = render(<LearnMessageWithContext />);

		expect(container.firstChild).toBeNull();
	});

	it('displays nothing if resource is not defined', () => {
		const {container} = render(
			<LearnMessageWithContext resourceKey="search-bar-suggestions" />
		);

		expect(container.firstChild).toBeNull();
	});

	it('displays nothing if resourceKey is not defined', () => {
		const {container} = render(
			<LearnMessageWithContext resource="portal-search-web" />
		);

		expect(container.firstChild).toBeNull();
	});
});
