/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import {FormProvider} from 'data-engine-js-components-web';
import React from 'react';

import SelectDateType from '../../../src/main/resources/META-INF/resources/js/Validation/SelectDateType';

const validations = [
	{
		checked: false,
		label: 'Future Dates',
		name: 'futureDates',
		parameterMessage: '',
		template: 'futureDates({name}, "{parameter}")',
		value: 'futureDates',
	},
	{
		checked: false,
		label: 'Past Dates',
		name: 'pastDates',
		parameterMessage: '',
		template: 'pastDates({name}, "{parameter}")',
		value: 'pastDates',
	},
	{
		checked: false,
		label: 'Range',
		name: 'dateRange',
		parameterMessage: '',
		template: 'dateRange({name}, "{parameter}")',
		value: 'dateRange',
	},
];

const parameters = {
	en_US: {
		endsOn: {
			date: 'responseDate',
			dateFieldName: 'Date1234',
			quantity: 1,
			type: 'customDate',
			unit: 'days',
		},
		startsFrom: {
			date: 'responseDate',
			dateFieldName: 'Date1234',
			quantity: 1,
			type: 'customDate',
			unit: 'days',
		},
	},
};

const SelectDateTypeProvider = ({builderPages = [], state, ...props}) => (
	<FormProvider initialState={{builderPages}} value={state}>
		<SelectDateType {...props} />
	</FormProvider>
);

describe('SelectDateType', () => {
	afterEach(cleanup);

	it('checks options and date field options', () => {
		const parameter = {
			en_US: {
				endsOn: {
					date: 'responseDate',
					quantity: -1,
					type: 'customDate',
					unit: 'days',
				},
			},
		};

		const options = [
			{
				checked: false,
				label: 'response-date',
				name: 'responseDate',
				value: 'responseDate',
			},
		];

		const dateFieldOptions = [
			{
				checked: false,
				label: 'Date A',
				name: 'Date12345678',
				value: 'Date12345678',
			},
		];

		const localizedValue = jest.fn(() => parameter['en_US']);

		const {getAllByRole} = render(
			<SelectDateTypeProvider
				dateFieldOptions={dateFieldOptions}
				defaultLanguageId="en_US"
				editingLanguageId="en_US"
				localizedValue={localizedValue}
				name="validationDate"
				onChange={() => {}}
				options={options}
				parameters={parameters}
				selectedValidation={{
					label: '',
					name: 'pastDates',
					parameterMessage: '',
					template: 'pastDates({name}, "{parameter}")',
				}}
				validations={validations}
				visible={true}
			/>
		);

		const [responseDate, dateField] = [
			...getAllByRole('menuitem', {hidden: true}),
		];

		expect(responseDate).toHaveValue('responseDate');
		expect(dateField).toHaveValue('Date12345678');
	});
});
