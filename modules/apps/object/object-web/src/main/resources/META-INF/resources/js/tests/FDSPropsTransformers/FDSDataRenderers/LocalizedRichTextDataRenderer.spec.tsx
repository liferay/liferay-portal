/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import LocalizedRichTextDataRenderer from '../../../components/FDSPropsTransformer/FDSDataRenderers/LocalizedRichTextDataRenderer';

describe('The LocalizedRichTextDataRenderer component', () => {
	const originalGetLanguageId = Liferay.ThemeDisplay.getLanguageId;

	afterEach(() => {
		Liferay.ThemeDisplay.getLanguageId = originalGetLanguageId;
	});

	it('falls back to the site default language value', () => {
		Liferay.ThemeDisplay.getLanguageId = () => 'pt_BR';

		const {container} = render(
			<LocalizedRichTextDataRenderer
				itemData={{description_i18n: {en_US: '<p>fallback</p>'}}}
				options={{fieldName: 'description'}}
			/>
		);

		expect(container).toHaveTextContent('fallback');
	});

	it('renders nothing when neither the display nor the default language has a value', () => {
		Liferay.ThemeDisplay.getLanguageId = () => 'fr_FR';

		const {container} = render(
			<LocalizedRichTextDataRenderer
				itemData={{description_i18n: {de_DE: '<p>delta</p>'}}}
				options={{fieldName: 'description'}}
			/>
		);

		expect(container.textContent).toBe('');
	});

	it('strips the markup from the display language value', () => {
		const {container} = render(
			<LocalizedRichTextDataRenderer
				itemData={{
					description_i18n: {
						en_US: '<p>hello <strong>world</strong></p>',
					},
				}}
				options={{fieldName: 'description'}}
			/>
		);

		expect(container).toHaveTextContent('hello world');
		expect(container.querySelector('strong')).toBeNull();
	});
});
