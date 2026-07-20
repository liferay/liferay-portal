/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import LocalizedMultiselectPicklistDataRenderer from '../../../components/FDSPropsTransformer/FDSDataRenderers/LocalizedMultiselectPicklistDataRenderer';

describe('The LocalizedMultiselectPicklistDataRenderer component', () => {
	const originalGetLanguageId = Liferay.ThemeDisplay.getLanguageId;

	afterEach(() => {
		Liferay.ThemeDisplay.getLanguageId = originalGetLanguageId;
	});

	it('falls back to the site default language list entries', () => {
		Liferay.ThemeDisplay.getLanguageId = () => 'pt_BR';

		const {container} = render(
			<LocalizedMultiselectPicklistDataRenderer
				itemData={{
					states_i18n: {
						en_US: [
							{key: 'opt1', name: 'Option 1'},
							{key: 'opt2', name: 'Option 2'},
						],
					},
				}}
				options={{fieldName: 'states'}}
			/>
		);

		expect(container).toHaveTextContent('Option 1, Option 2');
	});

	it('renders nothing when neither the display nor the default language has a value', () => {
		Liferay.ThemeDisplay.getLanguageId = () => 'fr_FR';

		const {container} = render(
			<LocalizedMultiselectPicklistDataRenderer
				itemData={{
					states_i18n: {de_DE: [{key: 'opt4', name: 'Option 4'}]},
				}}
				options={{fieldName: 'states'}}
			/>
		);

		expect(container.textContent).toBe('');
	});

	it('renders the joined names of the display language list entries', () => {
		Liferay.ThemeDisplay.getLanguageId = () => 'pt_BR';

		const {container} = render(
			<LocalizedMultiselectPicklistDataRenderer
				itemData={{
					states_i18n: {
						en_US: [{key: 'opt1', name: 'Option 1'}],
						pt_BR: [
							{key: 'opt2', name: 'Opção 2'},
							{key: 'opt3', name: 'Opção 3'},
						],
					},
				}}
				options={{fieldName: 'states'}}
			/>
		);

		expect(container).toHaveTextContent('Opção 2, Opção 3');
	});
});
