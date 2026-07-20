/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import LocalizedTextDataRenderer from '../../../components/FDSPropsTransformer/FDSDataRenderers/LocalizedTextDataRenderer';

describe('The LocalizedTextDataRenderer component', () => {
	const originalGetLanguageId = Liferay.ThemeDisplay.getLanguageId;

	afterEach(() => {
		Liferay.ThemeDisplay.getLanguageId = originalGetLanguageId;
	});

	it('falls back to the resolved value when the row has no i18n map', () => {
		const {container} = render(
			<LocalizedTextDataRenderer
				itemData={{}}
				options={{fieldName: 'name'}}
				value="from value prop"
			/>
		);

		expect(container).toHaveTextContent('from value prop');
	});

	it('falls back to the site default language value', () => {
		Liferay.ThemeDisplay.getLanguageId = () => 'pt_BR';

		const {container} = render(
			<LocalizedTextDataRenderer
				itemData={{name_i18n: {en_US: 'alpha'}}}
				options={{fieldName: 'name'}}
			/>
		);

		expect(container).toHaveTextContent('alpha');
	});

	it('renders nothing when neither the display nor the default language has a value', () => {
		Liferay.ThemeDisplay.getLanguageId = () => 'fr_FR';

		const {container} = render(
			<LocalizedTextDataRenderer
				itemData={{name_i18n: {de_DE: 'delta'}}}
				options={{fieldName: 'name'}}
			/>
		);

		expect(container.textContent).toBe('');
	});

	it('renders the display language value', () => {
		Liferay.ThemeDisplay.getLanguageId = () => 'pt_BR';

		const {container} = render(
			<LocalizedTextDataRenderer
				itemData={{name_i18n: {en_US: 'alpha', pt_BR: 'zulu'}}}
				options={{fieldName: 'name'}}
			/>
		);

		expect(container).toHaveTextContent('zulu');
	});
});
