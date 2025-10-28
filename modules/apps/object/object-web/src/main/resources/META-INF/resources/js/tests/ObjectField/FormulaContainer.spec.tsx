/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {FormulaContainer} from '../../components/ObjectField/Tabs/BasicInfo/FormulaContainer';

const defaultProps = {
	errors: {},
	objectFieldSettings: [],
	setValues: jest.fn(),
	sidebarElements: [],
	values: {},
};

describe('FormulaContainer', () => {
	it('renders the help message', () => {
		render(<FormulaContainer {...defaultProps} />);
		expect(
			screen.getByText('use-expressions-to-create-a-condition')
		).toBeVisible();
	});
});
