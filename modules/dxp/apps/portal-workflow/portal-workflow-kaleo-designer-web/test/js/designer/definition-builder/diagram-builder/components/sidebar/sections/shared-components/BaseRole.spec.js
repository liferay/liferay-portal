/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, waitFor} from '@testing-library/react';
import * as frontendJSWebModule from 'frontend-js-web';
import React from 'react';

import BaseRole from '../../../../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/components/sidebar/sections/shared-components/BaseRole';

describe('The BaseRole component should', () => {
	const frontendJSWebFetchSpier = jest.spyOn(frontendJSWebModule, 'fetch');
	const defaultProps = {
		defaultFieldValue: {
			id: 'id',
			name: 'name',
		},
		inputLabel: 'input',
		selectLabel: 'select',
		updateSelectedItem: jest.fn(),
	};

	afterAll(() => {
		frontendJSWebFetchSpier.mockRestore();
	});

	it('Call /o/headless-admin-user/v1.0/roles endpoint with the desired arguments', async () => {
		frontendJSWebFetchSpier.mockResolvedValue({
			json: async () => ({items: []}),
		});

		render(<BaseRole {...defaultProps} />);

		await waitFor(() => {
			expect(frontendJSWebFetchSpier).toHaveBeenCalledTimes(1);
		});

		expect(frontendJSWebFetchSpier).toHaveBeenCalledWith(
			'/o/headless-admin-user/v1.0/roles?pageSize=-1&restrictedFields=rolePermissions',
			expect.any(Object)
		);
	});
});
