/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import LocalizedPicklistDataRenderer from '../../../components/FDSPropsTransformer/FDSDataRenderers/LocalizedPicklistDataRenderer';

describe('The LocalizedPicklistDataRenderer component', () => {
	const originalGetLanguageId = Liferay.ThemeDisplay.getLanguageId;

	afterEach(() => {
		Liferay.ThemeDisplay.getLanguageId = originalGetLanguageId;
	});

	it('falls back to the resolved name when the row has no i18n map', () => {
		const {container} = render(
			<LocalizedPicklistDataRenderer
				itemData={{}}
				options={{fieldName: 'state.name'}}
				value="Related Option"
			/>
		);

		expect(container).toHaveTextContent('Related Option');
	});

	it('falls back to the site default language list entry', () => {
		Liferay.ThemeDisplay.getLanguageId = () => 'pt_BR';

		const {container} = render(
			<LocalizedPicklistDataRenderer
				itemData={{
					state_i18n: {en_US: {key: 'opt1', name: 'Option 1'}},
				}}
				options={{fieldName: 'state'}}
			/>
		);

		expect(container).toHaveTextContent('Option 1');
	});

	it('renders nothing when neither the display nor the default language has a value', () => {
		Liferay.ThemeDisplay.getLanguageId = () => 'fr_FR';

		const {container} = render(
			<LocalizedPicklistDataRenderer
				itemData={{
					state_i18n: {de_DE: {key: 'opt3', name: 'Option 3'}},
				}}
				options={{fieldName: 'state'}}
			/>
		);

		expect(container.textContent).toBe('');
	});

	it('renders the name of the display language list entry', () => {
		Liferay.ThemeDisplay.getLanguageId = () => 'pt_BR';

		const {container} = render(
			<LocalizedPicklistDataRenderer
				itemData={{
					state_i18n: {
						en_US: {key: 'opt1', name: 'Option 1'},
						pt_BR: {key: 'opt2', name: 'Opção 2'},
					},
				}}
				options={{fieldName: 'state'}}
			/>
		);

		expect(container).toHaveTextContent('Opção 2');
	});
});
