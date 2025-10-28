/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import {FormProvider} from 'data-engine-js-components-web';
import React from 'react';

import ValidationDate from '../../../src/main/resources/META-INF/resources/js/Validation/ValidationDate';

const globalLanguageDirection = Liferay.Language.direction;

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

const generateParameter = () => ({
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
});

const parameters = generateParameter();

const localizedValue = jest.fn(() => parameters['en_US']);

const ValidationDateProvider = ({
	formBuilder = {pages: []},
	state,
	...props
}) => (
	<FormProvider initialState={{formBuilder}} value={state}>
		<ValidationDate {...props} />
	</FormProvider>
);

describe('ValidationDate', () => {
	beforeAll(() => {
		Liferay.Language.direction = {
			en_US: 'rtl',
		};
	});

	afterAll(() => {
		Liferay.Language.direction = globalLanguageDirection;
	});

	it('shows future dates validation', () => {
		const {container} = render(
			<ValidationDateProvider
				defaultLanguageId="en_US"
				editingLanguageId="en_US"
				localizedValue={localizedValue}
				name="validationDate"
				onChange={() => {}}
				parameter={parameters}
				selectedValidation={{
					label: '',
					name: 'futureDates',
					parameterMessage: '',
					template: 'futureDates({name}, "{parameter}")',
				}}
				validations={validations}
				visible={true}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('shows past dates validation', () => {
		const {container} = render(
			<ValidationDateProvider
				defaultLanguageId="en_US"
				editingLanguageId="en_US"
				localizedValue={localizedValue}
				name="validationDate"
				onChange={() => {}}
				parameter={parameters}
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

		expect(container).toMatchSnapshot();
	});

	it('shows date range validation', () => {
		const {container} = render(
			<ValidationDateProvider
				defaultLanguageId="en_US"
				editingLanguageId="en_US"
				formBuilder={{pages: []}}
				localizedValue={localizedValue}
				name="validationDate"
				onChange={() => {}}
				parameter={parameters}
				selectedValidation={{
					label: '',
					name: 'dateRange',
					parameterMessage: '',
					template: 'dateRange({name}, "{parameter}")',
				}}
				validations={validations}
				visible={true}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('shows custom date fields for Future dates', () => {
		const parameter = {
			en_US: {
				startsFrom: {
					date: 'responseDate',
					quantity: 1,
					type: 'customDate',
					unit: 'days',
				},
			},
		};

		const localizedValue = jest.fn(() => parameter['en_US']);
		const {getByText} = render(
			<ValidationDateProvider
				defaultLanguageId="en_US"
				editingLanguageId="en_US"
				localizedValue={localizedValue}
				name="validationDate"
				onChange={() => {}}
				parameter={parameter}
				selectedValidation={{
					label: '',
					name: 'futureDates',
					parameterMessage: '',
					template: 'futureDates({name}, "{parameter}")',
				}}
				validations={validations}
				visible={true}
			/>
		);

		const [acceptedDate, operation, quantity, unit] = [
			getByText('accepted-date'),
			getByText('operation'),
			getByText('quantity'),
			getByText('unit'),
		].map((element) => element.querySelector('input'));

		expect(acceptedDate).toHaveValue('futureDates');
		expect(operation).toHaveValue('plus');
		expect(unit).toHaveValue('days');
		expect(quantity).toHaveValue(1);
	});

	it('shows custom date fields for Past dates and operation minus when quantity is negative', () => {
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

		const localizedValue = jest.fn(() => parameter['en_US']);
		const {getByText} = render(
			<ValidationDateProvider
				defaultLanguageId="en_US"
				editingLanguageId="en_US"
				localizedValue={localizedValue}
				name="validationDate"
				onChange={() => {}}
				parameter={parameter}
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

		const [acceptedDate, operation, quantity, unit] = [
			getByText('accepted-date'),
			getByText('operation'),
			getByText('quantity'),
			getByText('unit'),
		].map((element) => element.querySelector('input'));

		expect(acceptedDate).toHaveValue('pastDates');
		expect(operation).toHaveValue('minus');
		expect(quantity).toHaveValue(1);
		expect(unit).toHaveValue('days');
	});

	it('shows date field', () => {
		const formBuilder = {
			pages: [
				{
					rows: [
						{
							columns: [
								{
									fields: [
										{
											fieldName: 'Date12345678',
											label: 'Date A',
											type: 'date',
										},
									],
									size: 12,
								},
							],
						},
					],
				},
			],
		};

		const parameter = {
			en_US: {
				endsOn: {
					date: 'responseDate',
					quantity: -1,
					type: 'responseDate',
					unit: 'days',
				},
			},
		};

		const localizedValue = jest.fn(() => parameter['en_US']);

		const {getAllByRole} = render(
			<ValidationDateProvider
				defaultLanguageId="en_US"
				editingLanguageId="en_US"
				formBuilder={formBuilder}
				localizedValue={localizedValue}
				name="validationDate"
				onChange={() => {}}
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

		const lastOption = [...getAllByRole('menuitem', {hidden: true})].pop();

		expect(lastOption).toHaveValue('Date12345678');
	});

	it('hides date field if it is repeatable', () => {
		const formBuilder = {
			pages: [
				{
					rows: [
						{
							columns: [
								{
									fields: [
										{
											fieldName: 'Date12345678',
											label: 'Date A',
											repeatable: true,
											type: 'date',
										},
									],
									size: 12,
								},
							],
						},
					],
				},
			],
		};

		const parameter = {
			en_US: {
				endsOn: {
					date: 'responseDate',
					quantity: -1,
					type: 'responseDate',
					unit: 'days',
				},
			},
		};

		const localizedValue = jest.fn(() => parameter['en_US']);

		const {getAllByRole} = render(
			<ValidationDateProvider
				defaultLanguageId="en_US"
				editingLanguageId="en_US"
				formBuilder={formBuilder}
				localizedValue={localizedValue}
				name="validationDate"
				onChange={() => {}}
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

		const lastOption = [...getAllByRole('menuitem', {hidden: true})].pop();

		expect(lastOption).not.toHaveValue('Date12345678');
	});

	it('shows date field from a field group', () => {
		const formBuilder = {
			pages: [
				{
					rows: [
						{
							columns: [
								{
									fields: [
										{
											nestedFields: [
												{
													fieldName: 'childDate',
													type: 'date',
												},
											],
											type: 'fieldGroup',
										},
									],
									size: 12,
								},
							],
						},
					],
				},
			],
		};

		const parameter = {
			en_US: {
				endsOn: {
					date: 'responseDate',
					quantity: -1,
					type: 'responseDate',
					unit: 'days',
				},
			},
		};

		const localizedValue = jest.fn(() => parameter['en_US']);

		const {getAllByRole} = render(
			<ValidationDateProvider
				defaultLanguageId="en_US"
				editingLanguageId="en_US"
				formBuilder={formBuilder}
				localizedValue={localizedValue}
				name="validationDate"
				onChange={() => {}}
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

		const lastOption = [...getAllByRole('menuitem', {hidden: true})].pop();

		expect(lastOption).toHaveValue('childDate');
	});

	it('hides date field if from a repeatable field group', () => {
		const formBuilder = {
			pages: [
				{
					rows: [
						{
							columns: [
								{
									fields: [
										{
											nestedFields: [
												{
													fieldName: 'childDate',
													type: 'date',
												},
											],
											repeatable: true,
											type: 'fieldGroup',
										},
									],
									size: 12,
								},
							],
						},
					],
				},
			],
		};

		const parameter = {
			en_US: {
				endsOn: {
					date: 'responseDate',
					quantity: -1,
					type: 'responseDate',
					unit: 'days',
				},
			},
		};

		const localizedValue = jest.fn(() => parameter['en_US']);

		const {getAllByRole} = render(
			<ValidationDateProvider
				defaultLanguageId="en_US"
				editingLanguageId="en_US"
				formBuilder={formBuilder}
				localizedValue={localizedValue}
				name="validationDate"
				onChange={() => {}}
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

		const lastOption = [...getAllByRole('menuitem', {hidden: true})].pop();

		expect(lastOption).not.toHaveValue('childDate');
	});

	it('shows date fields inside custom date fields for Past dates and operation minus when quantity is negative', () => {
		const formBuilder = {
			pages: [
				{
					rows: [
						{
							columns: [
								{
									fields: [
										{
											fieldName: 'Date12345678',
											label: 'Date A',
											type: 'date',
										},
									],
									size: 12,
								},
							],
						},
					],
				},
			],
		};

		const parameter = {
			en_US: {
				endsOn: {
					date: 'dateField',
					quantity: -10,
					type: 'customDate',
					unit: 'days',
				},
			},
		};

		const localizedValue = jest.fn(() => parameter['en_US']);
		const {getAllByRole, getByText} = render(
			<ValidationDateProvider
				defaultLanguageId="en_US"
				editingLanguageId="en_US"
				formBuilder={formBuilder}
				localizedValue={localizedValue}
				name="validationDate"
				onChange={() => {}}
				parameter={parameter}
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

		const [acceptedDate, operation, quantity, unit] = [
			getByText('accepted-date'),
			getByText('operation'),
			getByText('quantity'),
			getByText('unit'),
		].map((element) => element.querySelector('input'));

		const availableDates = [...getAllByRole('menuitem', {hidden: true})];

		expect(availableDates[7]).toHaveValue('Date12345678');
		expect(acceptedDate).toHaveValue('pastDates');
		expect(operation).toHaveValue('minus');
		expect(quantity).toHaveValue(10);
		expect(unit).toHaveValue('days');
	});
});
