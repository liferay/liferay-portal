import React from 'react';
import Row from '../Row';
import {mockIndividual} from 'test/data';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

const INDIVIDUAL = mockIndividual();

const INDIVIDUAL_NO_SALARY = mockIndividual(1, {
	salary: null
});

const COLUMNS = [
	{
		accessor: 'name',
		label: 'Name',
		title: true
	},
	{
		accessor: 'properties.salary',
		label: 'Salary'
	}
];

describe('Row', () => {
	it('should render', () => {
		const {container} = render(<Row />);

		expect(container).toMatchSnapshot();
	});

	it('should render with data', () => {
		const {container} = render(<Row columns={COLUMNS} data={INDIVIDUAL} />);

		expect(container).toMatchSnapshot();
	});

	it('should render with empty data if the column accessor value is null', () => {
		const {getByText} = render(
			<Row columns={COLUMNS} data={INDIVIDUAL_NO_SALARY} />
		);

		expect(getByText('–')).toHaveClass('text-secondary');
	});

	it('should render with empty data if the accessor does not exist at all on the object', () => {
		const {getByText} = render(
			<Row
				columns={COLUMNS.concat([
					{accessor: 'nonExistentAccessor', label: 'does not exist'}
				])}
				data={INDIVIDUAL}
			/>
		);

		expect(getByText('–')).toHaveClass('text-secondary');
	});

	it('should render with empty data if the column accessor value is an empty string', () => {
		const {getByText} = render(
			<Row
				columns={COLUMNS}
				data={mockIndividual(1, {salary: ''})}
			/>
		);

		expect(getByText('–')).toHaveClass('text-secondary');
	});

	it('should render a real zero', () => {
		const {getByText} = render(
			<Row columns={COLUMNS} data={mockIndividual(1, {salary: 0})} />
		);

		expect(getByText('0')).toBeTruthy();
	});
});
