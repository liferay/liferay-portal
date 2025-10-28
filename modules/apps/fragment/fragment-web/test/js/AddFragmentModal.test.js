/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import React from 'react';

import AddFragmentModal from '../../src/main/resources/META-INF/resources/js/AddFragmentModal';

const renderComponent = () => {
	return render(
		<AddFragmentModal
			addFragmentEntryURL="addFragmentEntryURL"
			fieldTypes={[{key: 'bool', label: 'Boolean'}]}
			fragmentTypes={[
				{
					key: 'basic-fragment',
					title: 'Basic Fragment',
				},
				{key: 'form-fragment', title: 'Form Fragment'},
			]}
			namespace="namespace"
		/>
	);
};

describe('AddFragmentModal', () => {
	beforeAll(() => {
		jest.useFakeTimers();
	});

	it('renders AddFragmentModal and checks that radio buttons have the correct semantics', () => {
		renderComponent();

		act(() => {
			jest.runAllTimers();
		});

		expect(
			screen.getByRole('radio', {
				name: 'Basic Fragment',
			})
		).toBeInTheDocument();
		expect(
			screen.getByRole('radio', {
				name: 'Form Fragment',
			})
		).toBeInTheDocument();
		expect(
			screen.getByRole('group', {name: 'select-fragment-type'})
		).toBeInTheDocument();
	});
});
