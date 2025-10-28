/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import FilterDisplay from '../../../../src/main/resources/META-INF/resources/js/components/list/FilterDisplay.es';

import '@testing-library/jest-dom';

function renderTestFilterDisplay(props) {
	return render(
		<FilterDisplay
			onClear={jest.fn()}
			searchBarTerm="example"
			totalResultsCount={250}
			{...props}
		/>
	);
}

describe('FilterDisplay', () => {
	it('renders', () => {
		const {container} = renderTestFilterDisplay();

		expect(container).not.toBeNull();
	});

	it('displays the total results count', () => {
		const {getByText} = renderTestFilterDisplay();

		expect(getByText('250', {exact: false})).toBeInTheDocument();
	});

	it('displays the search bar term', () => {
		const {getByText} = renderTestFilterDisplay();

		expect(getByText('example', {exact: false})).toBeInTheDocument();
	});

	it('calls the onClear function when clicking on Clear', () => {
		const onClear = jest.fn();

		const {getByText} = renderTestFilterDisplay({onClear});

		fireEvent.click(getByText('clear'));

		expect(onClear).toHaveBeenCalledTimes(1);
	});
});
