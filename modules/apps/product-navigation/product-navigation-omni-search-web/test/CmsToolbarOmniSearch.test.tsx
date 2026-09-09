/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import CmsToolbarOmniSearch from '../src/main/resources/META-INF/resources/js/CmsToolbarOmniSearch';

const APPLICATIONS_MENU_PORTLET_ID =
	'com_liferay_product_navigation_applications_menu_web_internal_portlet_' +
	'ProductNavigationApplicationsMenuPortlet';

const RESULTS_URL = '/omni-search-results?p_p_id=foo';

const DEFAULT_PROPS = {
	applicationsMenuPortletId: APPLICATIONS_MENU_PORTLET_ID,
	resultsURL: RESULTS_URL,
};

function addPageBar({applicationsMenu = true} = {}) {
	const nav = document.createElement('nav');

	nav.className = 'cms-control-menu';

	nav.innerHTML = `
		<div class="container-fluid">
			<ul class="tbar-nav">
				<li class="tbar-item">Sidebar Trigger</li>
				<li class="tbar-item">Site Logo</li>
				<li class="tbar-item tbar-item-expand">Liferay CMS</li>
				<li class="tbar-item">AI Assistant</li>
				<li class="tbar-item">Bulk Actions Monitor</li>
				${
					applicationsMenu
						? `<li class="tbar-item"><div class="portlet-boundary portlet-boundary_${APPLICATIONS_MENU_PORTLET_ID}_">Applications Menu</div></li>`
						: ''
				}
				<li class="tbar-item">User Personal Bar</li>
			</ul>
		</div>
	`;

	document.body.appendChild(nav);

	return nav;
}

function getOmniSearchItem() {
	return screen
		.getByRole('button', {name: /omni-search/})
		.closest('.tbar-item');
}

describe('CmsToolbarOmniSearch', () => {
	afterEach(() => {
		document.body.innerHTML = '';
	});

	it('adds the omni search button to the CMS page bar', () => {
		addPageBar();

		render(<CmsToolbarOmniSearch {...DEFAULT_PROPS} />);

		expect(
			screen.getByRole('button', {name: /omni-search/})
		).toBeInTheDocument();
	});

	it('places the omni search button before the applications menu', () => {
		addPageBar();

		render(<CmsToolbarOmniSearch {...DEFAULT_PROPS} />);

		const applicationsMenuItem = screen
			.getByText('Applications Menu')
			.closest('.tbar-item');

		expect(applicationsMenuItem?.previousElementSibling).toBe(
			getOmniSearchItem()
		);
	});

	it('places the omni search button before the last item when the applications menu is missing', () => {
		addPageBar({applicationsMenu: false});

		render(<CmsToolbarOmniSearch {...DEFAULT_PROPS} />);

		const userPersonalBarItem = screen
			.getByText('User Personal Bar')
			.closest('.tbar-item');

		expect(userPersonalBarItem?.previousElementSibling).toBe(
			getOmniSearchItem()
		);
	});

	it('renders nothing when there is no CMS page bar', () => {
		render(<CmsToolbarOmniSearch {...DEFAULT_PROPS} />);

		expect(
			screen.queryByRole('button', {name: /omni-search/})
		).not.toBeInTheDocument();
	});

	it('adds a single omni search button when mounted more than once', () => {
		addPageBar();

		render(<CmsToolbarOmniSearch {...DEFAULT_PROPS} />);
		render(<CmsToolbarOmniSearch {...DEFAULT_PROPS} />);

		expect(
			screen.getAllByRole('button', {name: /omni-search/})
		).toHaveLength(1);
	});

	it('has no accessibility violations', async () => {
		const nav = addPageBar();

		render(<CmsToolbarOmniSearch {...DEFAULT_PROPS} />);

		await checkAccessibility({bestPractices: true, context: nav});
	});
});
