/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import resolveMessageType from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/utils/resolveMessageType';

describe('resolveMessageType', () => {
	it('resolves a message carrying a select component', () => {
		expect(
			resolveMessageType({
				component: {
					options: [],
					title: 'What would you like to do next?',
					type: 'select',
				},
				sender: 'assistant',
				text: '',
			})
		).toBe('select-component');
	});

	it('returns null for a plain text message', () => {
		expect(
			resolveMessageType({sender: 'assistant', text: 'Plain answer.'})
		).toBeNull();
	});

	it('returns null for a user message', () => {
		expect(resolveMessageType({sender: 'user', text: 'Hello'})).toBeNull();
	});
});
