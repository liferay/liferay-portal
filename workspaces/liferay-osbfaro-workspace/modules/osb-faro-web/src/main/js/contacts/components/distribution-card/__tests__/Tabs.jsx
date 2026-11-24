import React from 'react';
import Tabs from '../Tabs';
import {cleanup, render} from '@testing-library/react';
import {DistributionTab} from 'shared/util/records';
import {List} from 'immutable';

describe('DistributionCard Tabs', () => {
	afterEach(cleanup);

	it('renders', () => {
		const {container} = render(
			<Tabs
				itemsIList={
					new List([new DistributionTab({id: '123', title: 'Tab 1'})])
				}
				selectedTabIndex={0}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders w/ Add button as selected', () => {
		const {getByRole} = render(
			<Tabs
				itemsIList={
					new List([new DistributionTab({id: '123', title: 'Tab 1'})])
				}
				selectedTabIndex={0}
				showAddProperty
			/>
		);

		expect(getByRole('button', {name: /^add$/i})).toHaveAttribute(
			'aria-pressed',
			'true'
		);
	});
});
