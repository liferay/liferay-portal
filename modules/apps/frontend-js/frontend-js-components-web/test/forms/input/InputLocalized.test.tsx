/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {InputLocalized} from '../../../src/main/resources/META-INF/resources';

describe('InputLocalized component', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(global as any).Liferay.Language.direction = {
			en_US: 'rtl',
		};
	});

	it('renders an input text control with a Clay language picker', () => {
		render(
			<InputLocalized
				id="name"
				label="Name"
				onChange={() => null}
				translations={{}}
			/>
		);

		expect(screen.getByLabelText('Name')).toBeVisible();
		expect(screen.getByTitle('en_US')).toBeVisible();
	});

	it('renders an asterisk Clay icon when defined as "required"', () => {
		const {container} = render(
			<InputLocalized
				id="name"
				label="Name"
				onChange={() => null}
				required
				translations={{}}
			/>
		);

		expect(screen.getByLabelText('Namemandatory')).toBeVisible();
		expect(
			container.getElementsByClassName('lexicon-icon-asterisk')[0]
		).toBeInTheDocument();
	});

	it('renders a validation error message when the "error" is defined', () => {
		const {container} = render(
			<InputLocalized
				error="This field is required"
				id="name"
				label="Name"
				onChange={() => null}
				required
				translations={{}}
			/>
		);

		const errorMessage =
			container.getElementsByClassName('form-feedback-item');

		expect(screen.getByLabelText('Namemandatory')).toBeVisible();
		expect(errorMessage[0]).toHaveAttribute('aria-live', 'assertive');
		expect(errorMessage[0]).toHaveTextContent('This field is required');
	});
});
