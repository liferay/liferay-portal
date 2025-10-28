/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import DefinitionBuilder from '../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/DefinitionBuilder';

const props = {
	displayNames: ['English (United States)'],
	languageIds: ['en_US'],
	title: 'New Workflow',
	translations: {},
	version: '0',
};

describe('The DefinitionBuilder component should', () => {
	let container;

	beforeAll(async () => {
		const renderResult = render(<DefinitionBuilder {...props} />);

		container = renderResult.container;
	});

	it('Be rendered with DiagramBuilder and UpperToolbar', () => {
		const diagramBuilder = container.querySelector('div.diagram-builder');
		const upperToolbar = container.querySelector('nav.upper-toolbar');

		expect(diagramBuilder).toBeTruthy();
		expect(upperToolbar).toBeTruthy();
	});
});
