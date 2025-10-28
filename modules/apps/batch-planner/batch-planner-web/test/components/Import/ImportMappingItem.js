/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import ImportMappingItem from '../../../src/main/resources/META-INF/resources/js/import/ImportMappingItem';

const BASE_PROPS = {
	dbField: {
		name: 'nameTest',
	},
	fileFields: ['first name', 'last name', 'address'],
	formEvaluated: false,
	portletNamespace: 'test',
	required: true,
	selectedFileField: 'first name',
	updateFieldMapping: () => {},
};

describe('ImportMappingItem', () => {
	afterEach(cleanup);

	it('must render', () => {
		render(<ImportMappingItem {...BASE_PROPS} />);
	});

	it('must call the updateFieldMapping method when a user selects a value', () => {
		const onChangeMock = jest.fn();

		const {getByLabelText} = render(
			<ImportMappingItem
				{...BASE_PROPS}
				updateFieldMapping={onChangeMock}
			/>
		);

		act(() => {
			fireEvent.change(getByLabelText(/nameTest/), {
				target: {value: 'address'},
			});
		});

		expect(onChangeMock).toBeCalledWith('address');
	});

	it('must have a error status when the form is evaluated, the field is required and no value is selected', () => {
		const {container} = render(
			<ImportMappingItem
				{...BASE_PROPS}
				formEvaluated={true}
				selectedFileField=""
			/>
		);

		expect(
			container.querySelector('.form-group.has-error')
		).toBeInTheDocument();
	});

	it('must have a success status when the form is evaluated and the field is not required', () => {
		const {container} = render(
			<ImportMappingItem
				{...BASE_PROPS}
				dbField={{
					name: 'name',
				}}
				formEvaluated={true}
				required={false}
				selectedFileField=""
			/>
		);

		expect(
			container.querySelector('.form-group.has-success')
		).toBeInTheDocument();
	});
});
