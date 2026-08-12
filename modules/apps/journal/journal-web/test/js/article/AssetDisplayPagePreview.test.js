/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import AssetDisplayPagePreview from '../../../src/main/resources/META-INF/resources/js/article/AssetDisplayPagePreview';

const DEFAULT_PROPS = {
	newArticle: true,
	portletNamespace: 'namespace',
	previewURL: 'http://localhost/preview',
	saveAsDraftURL: 'http://localhost/save-as-draft',
	selectAssetDisplayPageEventName: 'selectAssetDisplayPage',
	selectAssetDisplayPageURL: 'http://localhost/select-asset-display-page',
	selectSiteEventName: 'selectSite',
	siteItemSelectorURL: 'http://localhost/select-site',
	sites: [{groupId: 1, name: 'Site 1'}],
	sitesCount: 1,
};

const renderComponent = () =>
	render(<AssetDisplayPagePreview {...DEFAULT_PROPS} />);

describe('AssetDisplayPagePreview', () => {
	beforeEach(() => {
		const articleIdInput = document.createElement('input');

		articleIdInput.id = `${DEFAULT_PROPS.portletNamespace}articleId`;

		document.body.appendChild(articleIdInput);

		Liferay.componentReady = jest.fn(() => Promise.resolve({}));
	});

	afterEach(() => {
		document
			.getElementById(`${DEFAULT_PROPS.portletNamespace}articleId`)
			?.remove();

		delete Liferay.componentReady;
	});

	it('names the site selector after its label when no site is selected', () => {
		renderComponent();

		expect(
			screen.getByRole('button', {name: 'site - not-selected -'})
		).toBeInTheDocument();
	});

	it('includes the selected site in the site selector accessible name', async () => {
		renderComponent();

		await userEvent.click(screen.getByRole('button', {name: /site/}));
		await userEvent.click(screen.getByRole('menuitem', {name: 'Site 1'}));

		expect(
			screen.getByRole('button', {name: 'site Site 1'})
		).toBeInTheDocument();
	});

	it('has no accessibility violations', async () => {
		const {container} = renderComponent();

		await checkAccessibility({bestPractices: true, context: container});
	});
});
