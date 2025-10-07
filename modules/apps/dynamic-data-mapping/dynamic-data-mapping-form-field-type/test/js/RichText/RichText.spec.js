/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render} from '@testing-library/react';
import React from 'react';

import RichText from '../../../src/main/resources/META-INF/resources/js/RichText/RichText.es';

describe('RichText component', () => {
	it('does not have aria-invalid attribute on first render when it is required', () => {
		render(
			<RichText
				label="RichText Label"
				name="RichTextName"
				required={true}
			/>
		);

		const richTextContainer = document.getElementById(
			'RichTextNameContainer'
		);

		expect(richTextContainer.hasAttribute('aria-invalid')).toBe(false);
	});

	it('does not have aria-invalid attribute when it is required and has a value', () => {
		render(
			<RichText
				label="RichText Label"
				name="RichTextName"
				required={true}
				value="test"
			/>
		);

		const richTextContainer = document.getElementById(
			'RichTextNameContainer'
		);

		expect(richTextContainer.hasAttribute('aria-invalid')).toBe(false);
	});

	it('has aria-label attribute when it is rendered', () => {
		const {getByRole} = render(
			<RichText label="RichText Label" name="RichTextName" />
		);

		const richTextElement = getByRole('textbox');

		expect(richTextElement).toHaveAttribute('aria-label', 'RichText Label');
	});
});
