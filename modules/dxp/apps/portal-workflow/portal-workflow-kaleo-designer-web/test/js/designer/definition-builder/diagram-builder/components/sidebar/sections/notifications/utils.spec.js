/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getRecipientType} from '../../../../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/components/sidebar/sections/notifications/utils';

describe('Getting the recipient type of a notification', () => {
	it('Treats users referenced by ID or by email address as users and users without a reference as the asset creator', () => {
		expect(
			getRecipientType({assignmentType: ['user'], userId: ['1']})
		).toBe('user');
		expect(
			getRecipientType({
				assignmentType: ['user'],
				emailAddress: ['user@liferay.com'],
			})
		).toBe('user');
		expect(getRecipientType({assignmentType: ['user']})).toBe(
			'assetCreator'
		);
		expect(getRecipientType({assignmentType: ['roleId'], roleId: 1})).toBe(
			'role'
		);
	});
});
