import * as data from 'test/data';
import AssociatedSegmentsCard from '../AssociatedSegmentsCard';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const DefaultComponent = props => (
	<MemoryRouter>
		<AssociatedSegmentsCard
			groupId='23'
			id='123'
			pageUrl='/foo'
			{...props}
		/>
	</MemoryRouter>
);

describe('AssociatedSegmentsCard', () => {
	it('should render', async () => {
		const {container} = render(
			<DefaultComponent
				dataSourceFn={() =>
					Promise.resolve(data.mockSearch(data.mockSegment, 2))
				}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render with an error display', async () => {
		const {container, getByText} = render(
			<DefaultComponent dataSourceFn={() => Promise.reject({})} />
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText('An unexpected error occurred.')).toBeTruthy();
	});

	it('should render with an no results display', async () => {
		const {container} = render(
			<DefaultComponent
				dataSourceFn={() =>
					Promise.resolve(data.mockSearch(data.mockSegment, 0))
				}
				noResultsRenderer={() => <NoResultsDisplay />}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
