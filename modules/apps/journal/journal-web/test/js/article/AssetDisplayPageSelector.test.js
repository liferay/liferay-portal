/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import AssetDisplayPageSelector from '../../../src/main/resources/META-INF/resources/js/article/AssetDisplayPageSelector';

const DEFAULT_PROPS = {
	assetDisplayPageSelected: null,
	disabled: false,
	namespace: 'namespace',
	selectAssetDisplayPageEventName: 'selectAssetDisplayPage',
	selectAssetDisplayPageURL: 'http://localhost/select-asset-display-page',
	selectedSite: null,
	setAssetDisplayPageSelected: jest.fn(),
};

const renderComponent = () =>
	render(<AssetDisplayPageSelector {...DEFAULT_PROPS} />);

describe('AssetDisplayPageSelector', () => {
	it('gives the display page field an accessible name from its label', () => {
		renderComponent();

		expect(
			screen.getByRole('textbox', {name: 'display-page'})
		).toBeInTheDocument();
	});

	it('has no accessibility violations', async () => {
		const {container} = renderComponent();

		await checkAccessibility({bestPractices: true, context: container});
	});
});
