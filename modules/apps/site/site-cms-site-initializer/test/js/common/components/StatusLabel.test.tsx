/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import React from 'react';

import StatusLabel from '../../../../src/main/resources/META-INF/resources/js/common/components/StatusLabel';

describe('StatusLabel', () => {
	it.each([
		['approved', 'success'],
		['denied', 'danger'],
		['draft', 'secondary'],
		['expired', 'danger'],
		['in-trash', 'info'],
		['inactive', 'secondary'],
		['incomplete', 'warning'],
		['pending', 'info'],
		['scheduled', 'info'],
	])('renders with the "%s" status', (label, displayType) => {
		render(<StatusLabel label={label} />);

		const labelElement = screen.getByText(label);

		expect(labelElement).toBeInTheDocument();
		expect(labelElement.parentElement).toHaveClass(
			'label',
			`label-${displayType}`
		);
	});
});
