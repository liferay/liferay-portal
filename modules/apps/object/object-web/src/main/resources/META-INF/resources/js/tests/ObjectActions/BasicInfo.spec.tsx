/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import BasicInfo from '../../components/ObjectAction/tabs/BasicInfo';

const basicInfoDefaultProps = {
	disableGroovyAction: false,
	errors: {},
	handleChange: () => {},
	isApproved: true,
	scriptManagementConfigurationPortletURL: '',
	setValues: () => {},
	values: {
		active: true,
		label: {en_US: 'Notify Adjuster'},
		name: 'notifyAdjuster',
		system: false,
	},
};

const renderComponent = (customProps = {}) =>
	render(<BasicInfo {...basicInfoDefaultProps} {...customProps} />);

describe('Object action description', () => {
	it('is disabled for a system object action', () => {
		renderComponent({
			values: {...basicInfoDefaultProps.values, system: true},
		});

		expect(screen.getByLabelText('description')).toBeDisabled();
	});

	it('is localized even when the feature flag is disabled', async () => {
		const setValues = jest.fn();

		renderComponent({setValues});

		await userEvent.type(screen.getByLabelText('description'), 'S');

		expect(setValues).toHaveBeenCalledWith({description: {en_US: 'S'}});
	});

	it('renders the help message explaining the English fallback', () => {
		renderComponent();

		expect(
			screen.getByText(
				'provide-descriptive-text-used-only-by-ai-agents-and-api-consumers'
			)
		).toBeInTheDocument();
	});

	it('shows the authored value for the default locale', () => {
		renderComponent({
			values: {
				...basicInfoDefaultProps.values,
				description: {en_US: 'Emails the assigned adjuster.'},
			},
		});

		expect(screen.getByLabelText('description')).toHaveValue(
			'Emails the assigned adjuster.'
		);
	});
});
