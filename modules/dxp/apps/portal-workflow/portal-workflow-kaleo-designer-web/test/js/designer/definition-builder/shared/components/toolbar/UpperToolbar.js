/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import UpperToolbar from '../../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/shared/components/toolbar/UpperToolbar';
import MockDefinitionBuilderContext from '../../../../../../mock/MockDefinitionBuilderContext';

const props = {
	displayNames: ['English (United States)'],
	languageIds: ['en_US'],
	title: 'New Workflow',
	translations: {},
	version: '0',
};

describe('The UpperToolbar component should', () => {
	let container;

	beforeAll(() => {
		const renderResult = render(
			<MockDefinitionBuilderContext>
				<UpperToolbar {...props} />
			</MockDefinitionBuilderContext>
		);

		container = renderResult.container;
	});

	it('Be rendered with all buttons and title input', () => {
		const tbarItems = container.querySelectorAll('li.tbar-item');

		expect(tbarItems.length).toBe(7);
		expect(tbarItems[0]).toHaveTextContent('en_US');
		const inputTitle = tbarItems[1].querySelector('input#definition-title');

		expect(inputTitle).toBeTruthy();

		expect(tbarItems[2].children[0].children[0]).toHaveClass(
			'lexicon-icon-info-circle-open'
		);
		expect(tbarItems[3]).toHaveTextContent('cancel');
		expect(tbarItems[4]).toHaveTextContent('save');
		expect(tbarItems[5]).toHaveTextContent('publish');
		const sourceButton = tbarItems[6].querySelector(
			'svg.lexicon-icon-code'
		);

		expect(sourceButton).toBeTruthy();
	});
});
