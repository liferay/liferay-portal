/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import InputComponent from '../../../src/main/resources/META-INF/resources/js/LocalizableText/InputComponent.es';

const languageDirection = {
	ar_SA: 'rtl',
	en_US: 'ltr',
};

describe('Input Component', () => {
	it('has the input direction as left to right when using english', () => {
		const {container} = render(
			<InputComponent
				dir={languageDirection['en_US']}
				fieldName="textField"
				readOnly={false}
			/>
		);

		const localizableTextInput = container.querySelector('.ddm-field-text');

		expect(localizableTextInput.getAttribute('dir')).toBe('ltr');
	});

	it('has the input direction as right to left when using arabic', () => {
		const {container} = render(
			<InputComponent
				dir={languageDirection['ar_SA']}
				fieldName="textField"
				readOnly={false}
			/>
		);

		const localizableTextInput = container.querySelector('.ddm-field-text');

		expect(localizableTextInput.getAttribute('dir')).toBe('rtl');
	});
});
