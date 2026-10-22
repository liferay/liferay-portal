import * as API from 'shared/api';
import * as data from 'test/data';
import IndividualAttributes from '../IndividualAttributes';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {Provider} from 'react-redux';
import {MemoryRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({timeZoneId: 'UTC'})
}));

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter>
			<IndividualAttributes groupId='23' {...props} />
		</MemoryRouter>
	</Provider>
);

describe('IndividualAttributes', () => {
	let OriginalDate;

	beforeAll(() => {
		OriginalDate = global.Date;

		global.Date = class extends Date {
			constructor() {
				super();
				return new OriginalDate(0);
			}
		};
	});

	afterAll(() => {
		global.Date = OriginalDate;
	});

	afterEach(cleanup);

	it('should render', async () => {
		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should open modal after click on fielName', async () => {
		const {container, getByText} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		fireEvent.click(getByText('testFildName0'));

		expect(
			await screen.findByRole('heading', {name: 'testFildName0'})
		).toBeInTheDocument();
	});

	it('should render the source name of an attribute with one source', async () => {
		const {container, getByText} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(
			getByText('LIFERAY-DATASOURCE-FARO-EXAMPLE-0')
		).toBeInTheDocument();
	});

	it('should render an attribute without sources or last synced date as no data', async () => {
		API.definitions.searchIndividualAttributes.mockReturnValueOnce(
			Promise.resolve({
				items: [
					data.mockIndividualAttributes(0, {
						dataSources: [],
						dateModified: null
					})
				],
				total: 1
			})
		);

		const {container, getAllByText} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		const noDataValues = getAllByText('–');

		expect(noDataValues).toHaveLength(2);

		noDataValues.forEach(noDataValue =>
			expect(noDataValue).toHaveClass('text-secondary')
		);
	});
});
