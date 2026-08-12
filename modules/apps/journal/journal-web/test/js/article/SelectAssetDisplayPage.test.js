/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import SelectAssetDisplayPage from '../../../src/main/resources/META-INF/resources/js/article/SelectAssetDisplayPage';

const DISPLAY_PAGE_TYPE_NONE = 0;

const DEFAULT_PROPS = {
	assetDisplayPageId: '',
	assetDisplayPageType: DISPLAY_PAGE_TYPE_NONE,
	defaultDisplayPageName: '',
	layoutUuid: '',
	newArticle: true,
	portletNamespace: 'namespace',
	saveAsDraftURL: 'http://localhost/save-as-draft',
	selectAssetDisplayPageEventName: 'selectAssetDisplayPage',
	selectAssetDisplayPageURL: 'http://localhost/select-asset-display-page',
	specificAssetDisplayPageName: '',
};

const renderComponent = () =>
	render(<SelectAssetDisplayPage {...DEFAULT_PROPS} />);

describe('SelectAssetDisplayPage', () => {
	it('submits the display page fields as native hidden inputs', () => {
		const {container} = renderComponent();

		['assetDisplayPageId', 'displayPageType', 'layoutUuid'].forEach(
			(name) => {
				expect(
					container.querySelector(
						`input[name="${DEFAULT_PROPS.portletNamespace}${name}"]`
					)
				).toHaveAttribute('type', 'hidden');
			}
		);
	});

	it('has no accessibility violations', async () => {
		const {container} = renderComponent();

		await checkAccessibility({bestPractices: true, context: container});
	});
});
