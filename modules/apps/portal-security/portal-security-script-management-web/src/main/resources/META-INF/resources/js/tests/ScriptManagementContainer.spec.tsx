/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React, {useState as useStateMock} from 'react';

import ScriptManagementContainer from '../components/ScriptManagementContainer';

jest.mock('react', () => ({
	...(jest.requireActual('react') as {}),
	useState: jest.fn(),
}));

const setAllowScriptContent = jest.fn();

beforeAll(() => {

	// @ts-ignore

	useStateMock.mockImplementation((allowScriptContent: boolean) => [
		allowScriptContent,
		setAllowScriptContent,
	]);
});

describe('ScriptManagementContainer component', () => {
	it('check if checkbox label renders correctly', () => {
		const {getByLabelText} = render(
			<ScriptManagementContainer
				allowScriptContentToBeExecutedOrIncluded={false}
				baseResourceURL=""
				scriptManagementConfigurationDefined={false}
			/>
		);

		const input = getByLabelText(
			'allow-administrator-to-create-and-execute-code-in-liferay'
		);

		expect(input).toBeInTheDocument();
	});

	it('check if Script Management title renders correctly', () => {
		const {getByText} = render(
			<ScriptManagementContainer
				allowScriptContentToBeExecutedOrIncluded={false}
				baseResourceURL=""
				scriptManagementConfigurationDefined={false}
			/>
		);

		const scriptManagementTitle = getByText('script-management');

		expect(scriptManagementTitle).toBeInTheDocument();
	});

	it('check if checkbox description renders correctly', () => {
		const {getByText} = render(
			<ScriptManagementContainer
				allowScriptContentToBeExecutedOrIncluded={false}
				baseResourceURL=""
				scriptManagementConfigurationDefined={false}
			/>
		);

		const checkboxDescription = getByText(
			'administrators-can-create-and-execute-code-in-their-virtual-instance'
		);

		expect(checkboxDescription).toBeInTheDocument();
	});

	it('check if checkbox will be checked if allowScriptContentToBeExecutedOrIncluded is true', () => {
		const {getByRole} = render(
			<ScriptManagementContainer
				allowScriptContentToBeExecutedOrIncluded={true}
				baseResourceURL=""
				scriptManagementConfigurationDefined={false}
			/>
		);

		const checkboxInput = getByRole('checkbox');

		expect(checkboxInput).toBeChecked();
	});

	it('check if checkbox will be not checked if allowScriptContentToBeExecutedOrIncluded is false', () => {
		const {getByRole} = render(
			<ScriptManagementContainer
				allowScriptContentToBeExecutedOrIncluded={false}
				baseResourceURL=""
				scriptManagementConfigurationDefined={false}
			/>
		);

		const checkboxInput = getByRole('checkbox');

		expect(checkboxInput).not.toBeChecked();
	});

	it('check if configuration information alert will render correctly when scriptManagementConfigurationDefined is false', () => {
		const {getByRole, getByText} = render(
			<ScriptManagementContainer
				allowScriptContentToBeExecutedOrIncluded={false}
				baseResourceURL=""
				scriptManagementConfigurationDefined={false}
			/>
		);

		const alertHelperInfo = getByText('info:');
		const alertText = getByText(
			'this-configuration-is-not-saved-yet.-the-values-shown-are-the-default'
		);
		const infoCircleIcon = getByRole('presentation');

		expect(alertHelperInfo).toBeInTheDocument();
		expect(alertText).toBeInTheDocument();
		expect(infoCircleIcon).toHaveClass('lexicon-icon-info-circle');
	});

	it('check if configuration information alert will not render when scriptManagementConfigurationDefined is true', () => {
		const {queryByRole, queryByText} = render(
			<ScriptManagementContainer
				allowScriptContentToBeExecutedOrIncluded={false}
				baseResourceURL=""
				scriptManagementConfigurationDefined={true}
			/>
		);

		const alertHelperInfo = queryByText('info:');
		const alertText = queryByText(
			'this-configuration-is-not-saved-yet.-the-values-shown-are-the-default'
		);
		const infoCircleIcon = queryByRole('presentation');

		expect(alertHelperInfo).not.toBeInTheDocument();
		expect(alertText).not.toBeInTheDocument();
		expect(infoCircleIcon).not.toBeInTheDocument();
	});
});
