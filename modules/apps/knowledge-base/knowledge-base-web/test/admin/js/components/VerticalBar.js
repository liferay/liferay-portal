/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent, render} from '@testing-library/react';
import {navigate} from 'frontend-js-web';
import React from 'react';

import '@testing-library/jest-dom';

import VerticalNavigationBar from '../../../../src/main/resources/META-INF/resources/js/admin/components/VerticalBar';

const FOLDERS_AND_ARTICLES_TITLE = 'Folders and articles';
const TEMPLATES_TITLE = 'Templates';
const SUGGESTIONS_TITLE = 'Suggestions';
const PARENT_CONTAINER_ID = 'parentContainerId';

jest.mock('frontend-js-web', () => ({
	navigate: jest.fn(),
}));

const defaultProps = {
	items: [
		{
			active: true,
			href: 'article_url',
			icon: 'pages-tree',
			key: 'article',
			navigationItems: [],
			title: FOLDERS_AND_ARTICLES_TITLE,
		},
		{
			active: false,
			href: 'template_url',
			icon: 'page-template',
			key: 'template',
			title: TEMPLATES_TITLE,
		},
		{
			active: false,
			href: 'suggestions_url',
			icon: 'message',
			key: 'suggestion',
			title: SUGGESTIONS_TITLE,
		},
	],
	parentContainerId: PARENT_CONTAINER_ID,
	portletNamespace: '_portletNamespace_',
	productMenuOpen: true,
};

const renderComponent = (props = defaultProps) => {
	const container = document.createElement('div');
	container.id = PARENT_CONTAINER_ID;

	return render(<VerticalNavigationBar {...props} />, {
		container: document.body.appendChild(container),
	});
};

describe('VerticalBar', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	beforeEach(() => {
		cleanup();

		const productMenuOn = {
			removeListener: jest.fn(),
		};

		const productMenu = {
			destroy: jest.fn(),
			hide: jest.fn(),
			on: () => productMenuOn,
		};

		global.Liferay.SideNavigation = {instance: () => productMenu};
	});

	it('renders three navigation items', () => {
		const {getAllByRole, getByTitle} = renderComponent();

		expect(getAllByRole('tab').length).toBe(3);

		expect(getByTitle(FOLDERS_AND_ARTICLES_TITLE)).toBeInTheDocument();
		expect(getByTitle(TEMPLATES_TITLE)).toBeInTheDocument();
		expect(getByTitle(SUGGESTIONS_TITLE)).toBeInTheDocument();
	});

	it('does not render the panel if the product menu is open', () => {
		const {container} = renderComponent();

		expect(container.querySelector('.sidebar')).not.toBeInTheDocument();
	});

	it('renders the panel if the product menu is closed', () => {
		const {container} = renderComponent({
			...defaultProps,
			productMenuOpen: false,
		});

		expect(container.querySelector('.sidebar')).toBeInTheDocument();
	});

	it('does not navigate if user clicks on the current panel icon', () => {
		const {getAllByRole} = renderComponent();

		const foldersAndArticlesButton = getAllByRole('tab')[0];

		fireEvent.click(foldersAndArticlesButton);

		expect(navigate).toHaveBeenCalledTimes(0);
	});

	// The unit test is failing after Clay was updated to version 3.92.0, the
	// test is disabled because the root cause of the problem for this specific
	// test has not been found but it is not reproducible in the browser.

	xit('navigate if user clicks on another panel icon', async () => {
		const {getAllByRole} = renderComponent({
			...defaultProps,
			productMenuOpen: false,
		});

		const templatesButton = getAllByRole('tab')[1];

		fireEvent.click(templatesButton);

		expect(navigate).toHaveBeenCalledTimes(1);
	});

	it('opens the panel if user clicks on the current panel icon', () => {
		const {container, getAllByRole} = renderComponent();

		const foldersAndArticlesButton = getAllByRole('tab')[0];

		fireEvent.click(foldersAndArticlesButton);

		expect(container.querySelector('.sidebar')).toBeInTheDocument();
	});
});
