/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render} from '@testing-library/react';
import React from 'react';

import ConnectStep from '../ConnectStep';

describe('Connect Step', () => {
	it('render ConnectStep without crashing', () => {
		const {container, getByText} = render(
			<ConnectStep onCancel={() => {}} onChangeStep={() => {}} />
		);

		const connectStepDescription = getByText(
			'use-the-token-generated-in-your-analytics-cloud-to-connect-this-workspace'
		);

		const connectStepTitleLang = getByText('connect-to-analytics-cloud');

		expect(connectStepDescription).toBeInTheDocument();

		expect(container.firstChild).toHaveClass('sheet');

		expect(connectStepTitleLang).toBeInTheDocument();
	});

	it('render card of conection with AC without crashing', () => {
		const {getByPlaceholderText, getByRole, getByText} = render(
			<ConnectStep onCancel={() => {}} onChangeStep={() => {}} />
		);

		const wizardCardTitle = getByText('connect-to-analytics-cloud');

		const wizardCardDescription = getByText(
			'use-the-token-generated-in-your-analytics-cloud-to-connect-this-workspace'
		);

		const wizardInputTitle = getByText('analytics-cloud-token');

		const wizardInputPlaceholder = getByPlaceholderText('paste-token-here');

		const wizardInputHelp = getByText('analytics-cloud-token-help');

		const buttonCardConnect = getByRole('button', {name: /connect/i});

		expect(wizardCardTitle).toBeInTheDocument();

		expect(wizardCardDescription).toBeInTheDocument();

		expect(wizardInputTitle).toBeInTheDocument();

		expect(wizardInputPlaceholder).toBeInTheDocument();

		expect(wizardInputHelp).toBeInTheDocument();

		expect(buttonCardConnect).toBeInTheDocument();
	});
});
