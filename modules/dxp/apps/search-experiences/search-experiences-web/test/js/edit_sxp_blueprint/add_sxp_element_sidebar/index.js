/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import AddSXPElementSidebar from '../../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/edit_sxp_blueprint/add_sxp_element_sidebar/index';
import {QUERY_SXP_ELEMENTS} from '../../mocks/sxpElements';

import '@testing-library/jest-dom';

// Suppress act warning until @testing-library/react is updated past 9
// to use screen. See https://javascript.plainenglish.io/
// you-probably-dont-need-act-in-your-react-tests-2a0bcd2ad65c

const originalError = console.error;

Liferay.ThemeDisplay.getPathContext = () => '';

beforeAll(() => {
	console.error = (...args) => {
		if (/Warning.*not wrapped in act/.test(args[0])) {
			return;
		}
		originalError.call(console, ...args);
	};
});

afterAll(() => {
	console.error = originalError;
});

function renderAddSXPElementSidebar(props) {
	return render(
		<AddSXPElementSidebar onAddSXPElement={jest.fn()} {...props} />
	);
}

describe('AddSXPElementSidebar', () => {
	it('renders the sidebar', () => {
		const {container} = renderAddSXPElementSidebar();

		expect(container).not.toBeNull();
	});

	it('renders the titles for the possible query elements', async () => {
		const {container, findByText, getByText} = renderAddSXPElementSidebar();

		await findByText(QUERY_SXP_ELEMENTS[0].elementDefinition.category);

		container
			.querySelectorAll('.lexicon-icon-angle-right')
			.forEach((header) => {
				fireEvent.click(header);
			});

		QUERY_SXP_ELEMENTS.map((sxpElement) => {
			getByText(sxpElement.title_i18n['en_US']);
		});
	});

	it('renders the descriptions for the possible query elements', async () => {
		const {container, findByText, getByText} = renderAddSXPElementSidebar();

		await findByText(QUERY_SXP_ELEMENTS[0].elementDefinition.category);

		container
			.querySelectorAll('.lexicon-icon-angle-right')
			.forEach((header) => {
				fireEvent.click(header);
			});

		QUERY_SXP_ELEMENTS.map((sxpElement) => {
			getByText(sxpElement.description_i18n['en_US']);
		});
	});

	it('displays the add button when hovering over an element item', async () => {
		const {container, findByText, queryAllByText} =
			renderAddSXPElementSidebar();

		await findByText(QUERY_SXP_ELEMENTS[0].elementDefinition.category);

		container
			.querySelectorAll('.lexicon-icon-angle-right')
			.forEach((header) => {
				fireEvent.click(header);
			});

		fireEvent.mouseOver(container.querySelectorAll('.list-group-title')[0]);

		expect(queryAllByText('add')[0]).toBeVisible();
	});
});
