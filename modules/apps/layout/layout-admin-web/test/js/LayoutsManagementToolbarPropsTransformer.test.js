/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AIAssistant} from '@liferay/ai-hub-cell-js-components-web';

import propsTransformer from '../../src/main/resources/META-INF/resources/js/LayoutsManagementToolbarPropsTransformer';

jest.mock('@liferay/ai-hub-cell-js-components-web', () => ({
	AIAssistant: {open: jest.fn()},
}));

describe('LayoutsManagementToolbarPropsTransformer', () => {
	beforeEach(() => {
		AIAssistant.open.mockClear();
	});

	it('opens the AI Assistant with the destination of the new page', () => {
		const {onCreationMenuItemClick} = propsTransformer({
			portletNamespace: '_namespace_',
		});

		onCreationMenuItemClick(new Event('click'), {
			item: {
				data: {
					action: 'generatePageWithAI',
					parentPageExternalReferenceCode: 'parent-erc',
					privateLayout: 'false',
					siteExternalReferenceCode: 'site-erc',
				},
			},
		});

		expect(AIAssistant.open).toHaveBeenCalledWith(
			expect.objectContaining({
				chatbotExternalReferenceCode: 'L_GENERATE_PAGE_CHATBOT',
				context: {
					parentPageExternalReferenceCode: 'parent-erc',
					privateLayout: false,
					siteExternalReferenceCode: 'site-erc',
				},
				initialMessage: 'generate-page',
			})
		);
	});

	it('does not open the AI Assistant for other creation menu items', () => {
		const {onCreationMenuItemClick} = propsTransformer({
			portletNamespace: '_namespace_',
		});

		onCreationMenuItemClick(new Event('click'), {
			item: {data: {}, href: 'http://localhost/add-page'},
		});

		expect(AIAssistant.open).not.toHaveBeenCalled();
	});
});
