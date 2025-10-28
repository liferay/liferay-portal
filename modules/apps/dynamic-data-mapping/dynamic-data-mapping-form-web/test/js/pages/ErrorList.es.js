/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, getByText, render} from '@testing-library/react';
import React from 'react';

import ErrorList from '../../../src/main/resources/META-INF/resources/js/pages/ErrorList';

describe('Error List', () => {
	afterEach(cleanup);

	it('shows an error message when trying to preview a form without fields', () => {
		const {container} = render(
			<ErrorList
				errorMessages={['Please add at least one field']}
				onRemove={null}
				sidebarOpen={true}
			/>
		);

		const errorList = container.querySelector(
			'.ddm-form-web__exception-container'
		);

		expect(errorList).toBeInTheDocument();
		expect(
			getByText(container, 'Please add at least one field')
		).toBeInTheDocument();
	});

	it('does not show an error message when trying to preview a form with fields', () => {
		const {container} = render(
			<ErrorList errorMessages={[]} onRemove={null} sidebarOpen={true} />
		);

		const errorList = container.querySelector(
			'.ddm-form-web__exception-container'
		);

		expect(errorList).toBeNull();
	});
});
