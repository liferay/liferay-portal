import ListView from '../ListView';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {mockIndividual} from 'test/data';
import {times} from 'lodash';

const items = times(3, i => mockIndividual(i, {total: 123}));

describe('ListView', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<ListView
				itemRenderer={() => 'foo item'}
				items={items}
				onClick={jest.fn()}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render quick actions', () => {
		const {container} = render(
			<ListView
				itemRenderer={() => 'foo item'}
				items={items}
				quickActions={[
					{
						symbol: 'foo'
					},
					{
						symbol: 'bar'
					}
				]}
			/>
		);

		expect(container.querySelectorAll('.quick-action-item').length).toBe(6);
	});
});
