/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useResource} from '@clayui/data-provider';
import {render} from '@testing-library/react';
import React from 'react';

import SelectObjectDefinition from '../../components/ObjectRelationship/SelectObjectDefinition';

jest.mock('@clayui/data-provider', () => {
	const originalModule = jest.requireActual('@clayui/data-provider');

	return {
		...originalModule,
		useResource: jest.fn(),
	};
});

describe('SelectObjectDefinition', () => {
	beforeEach(() => {
		(useResource as jest.Mock).mockReturnValue({resource: null});
	});

	it('requests object definitions with the context path prefixed', () => {
		(Liferay.ThemeDisplay.getPathContext as jest.Mock).mockReturnValueOnce(
			'/myportal'
		);

		render(
			<SelectObjectDefinition reverseOrder={false} setValues={() => {}} />
		);

		expect(useResource).toHaveBeenCalledWith(
			expect.objectContaining({
				link: 'http://localhost:8080/myportal/o/object-admin/v1.0/object-definitions',
			})
		);
	});

	it('requests object definitions without a prefix at the root context', () => {
		(Liferay.ThemeDisplay.getPathContext as jest.Mock).mockReturnValueOnce(
			''
		);

		render(
			<SelectObjectDefinition reverseOrder={false} setValues={() => {}} />
		);

		expect(useResource).toHaveBeenCalledWith(
			expect.objectContaining({
				link: 'http://localhost:8080/o/object-admin/v1.0/object-definitions',
			})
		);
	});
});
