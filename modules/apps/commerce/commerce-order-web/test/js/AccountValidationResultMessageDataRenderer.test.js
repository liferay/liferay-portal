/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AccountValidationResultMessageDataRenderer from '../../src/main/resources/META-INF/resources/js/AccountValidationResultMessageDataRenderer';

describe('AccountValidationResultMessageDataRenderer', () => {
	it('renders the message mapped to the result message key', () => {
		expect(
			AccountValidationResultMessageDataRenderer({
				additionalProps: {resultMessages: {key1: 'message1'}},
				value: 'key1',
			})
		).toBe('message1');
	});

	it('renders a custom value instead of a mapped message', () => {
		expect(
			AccountValidationResultMessageDataRenderer({
				additionalProps: {resultMessages: {key1: 'message1'}},
				value: 'value1',
			})
		).toBe('value1');
	});

	it('renders the result message key when it is not mapped', () => {
		expect(
			AccountValidationResultMessageDataRenderer({
				additionalProps: {resultMessages: {key1: 'message1'}},
				value: 'key2',
			})
		).toBe('key2');
	});
});
