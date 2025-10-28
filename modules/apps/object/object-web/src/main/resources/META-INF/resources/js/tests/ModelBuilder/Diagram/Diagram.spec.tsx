/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import Diagram from '../../../components/ModelBuilder/Diagram/Diagram';
import {mockReactFlow} from './reactFlowMocks';

jest.mock(
	'../../../components/ModelBuilder/ModelBuilderContext/objectFolderContext',
	() => ({
		useObjectFolderContext: () => [
			{
				baseResourceURL: 'http://base-resource-url',
				elements: [],
				isLoadingObjectFolder: false,
				learnResourceContext: {},
				selectedObjectFolder: {
					externalReferenceCode: 'externalReferenceCode',
					id: 1,
					label: 'label',
					name: 'name',
					objectFolderItems: [],
				},
				showChangesSaved: false,
				showSidebars: false,
			},
			jest.fn(),
		],
	})
);

describe('The Diagram component should', () => {
	beforeEach(() => {
		mockReactFlow();
	});

	it('maintain ltr direction in rtl environment', () => {
		document.documentElement.setAttribute('dir', 'rtl');

		const {container} = render(<Diagram />);

		const reactFlowDiagram = container.querySelector('.react-flow');

		expect(reactFlowDiagram).toBeVisible();
		expect((reactFlowDiagram as Element).getAttribute('dir')).toBe('ltr');
	});
});
