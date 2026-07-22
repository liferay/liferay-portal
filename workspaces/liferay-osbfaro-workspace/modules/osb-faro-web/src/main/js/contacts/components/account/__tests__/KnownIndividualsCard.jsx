import * as data from 'test/data';
import KnownIndividualsCard from '../KnownIndividualsCard';
import React from 'react';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const dataSourceFn = total => () =>
	Promise.resolve(data.mockSearch(data.mockIndividual, total));

const DefaultComponent = props => (
	<MemoryRouter>
		<KnownIndividualsCard
			channelId='123'
			dataSourceFn={dataSourceFn(3)}
			groupId='23'
			id='23'
			{...props}
		/>
	</MemoryRouter>
);

describe('KnownIndividualsCard', () => {
	it('should render', async () => {
		const {container} = render(<DefaultComponent />);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render w/ NoResultsDisplay', async () => {
		const {container} = render(
			<DefaultComponent dataSourceFn={dataSourceFn(0)} />
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render w/ ErrorDisplay', async () => {
		const {container, getByText} = render(
			<DefaultComponent dataSourceFn={() => Promise.reject({})} />
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(getByText('An unexpected error occurred.')).toBeTruthy();
	});
});
