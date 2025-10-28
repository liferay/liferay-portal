import React from 'react';
import Table, {getRowIdentifierValue} from '../index';
import {mockIndividual} from 'test/data';
import {render} from '@testing-library/react';
import {StaticRouter} from 'react-router';
import {times} from 'lodash';

jest.unmock('react-dom');

const INDIVIDUALS = times(5, i => mockIndividual(i));

const COLUMNS = [
	{
		accessor: 'name',
		label: 'Name'
	},
	{
		accessor: 'properties.salary',
		label: 'Salary'
	}
];

describe('Table', () => {
	it('should render', () => {
		const {container} = render(
			<StaticRouter>
				<Table
					columns={COLUMNS}
					items={INDIVIDUALS}
					rowIdentifier='id'
				/>
			</StaticRouter>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render without items', () => {
		const {container} = render(
			<StaticRouter>
				<Table columns={COLUMNS} rowIdentifier='id' />
			</StaticRouter>
		);

		expect(container.querySelector('tbody')).toBeNull();
	});

	it('should render with borders', () => {
		const {container} = render(
			<StaticRouter>
				<Table
					bordered
					columns={COLUMNS}
					items={INDIVIDUALS}
					rowIdentifier='id'
				/>
			</StaticRouter>
		);

		expect(container.querySelector('.table-head-bordered')).toBeTruthy();
	});

	it('should render with nowrap rows', () => {
		const {container} = render(
			<StaticRouter>
				<Table
					columns={COLUMNS}
					items={INDIVIDUALS}
					nowrap
					rowIdentifier='id'
				/>
			</StaticRouter>
		);

		expect(container.querySelector('.table-nowrap')).toBeTruthy();
	});

	it('should render w/ loading', () => {
		const {container} = render(
			<StaticRouter>
				<Table
					columns={COLUMNS}
					items={INDIVIDUALS}
					loading
					rowIdentifier='id'
				/>
			</StaticRouter>
		);

		expect(container.querySelector('.loading-root')).toBeTruthy();
	});

	describe('getRowIdentifierValue', () => {
		it('should return a combination of the items specified in the rowIdentifier', () => {
			expect(
				getRowIdentifierValue(
					{company: 'Testers, Inc.', name: 'Test', title: 'tester'},
					['name', 'title']
				)
			).toBe('Testtester');
		});
	});
});
