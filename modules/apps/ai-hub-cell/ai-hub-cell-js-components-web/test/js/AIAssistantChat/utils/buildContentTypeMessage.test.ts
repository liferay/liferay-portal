/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import buildContentTypeMessage from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/utils/buildContentTypeMessage';

describe('buildContentTypeMessage', () => {
	it('asks the user which content type to generate', () => {
		const contentTypes = [
			{
				externalReferenceCode: 'BLOG',
				label: 'Blog',
				name: 'blog',
			},
		];

		expect(buildContentTypeMessage(contentTypes)).toEqual({
			contentTypes,
			sender: 'assistant',
			text: 'what-type-of-content-do-you-want-to-generate',
		});
	});
});
