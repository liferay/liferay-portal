import * as data from 'test/data';
import InterestDetails from '../InterestDetails';
import React from 'react';
import {Individual} from 'shared/util/records';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

const defaultProps = {
	active: 'true',
	groupId: '23',
	id: 'test',
	individual: new Individual(data.mockIndividual()),
	interestId: 1
};

jest.unmock('react-dom');

describe('InterestDetails', () => {
	it('should render', async () => {
		const {container, getByText} = render(
			<MemoryRouter>
				<InterestDetails {...defaultProps} />
			</MemoryRouter>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Back to Interests')).toBeInTheDocument();
	});

	it('should render an active pages list tab', async () => {
		const {container, getByText} = render(
			<MemoryRouter>
				<InterestDetails {...defaultProps} />
			</MemoryRouter>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Active Pages')).toBeTruthy();
		expect(getByText('Active Pages').parentElement).toHaveClass('active');
	});

	it('should render an inactive pages list tab', async () => {
		const {container, getByText} = render(
			<MemoryRouter>
				<InterestDetails {...defaultProps} active='false' />
			</MemoryRouter>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Inactive Pages')).toBeTruthy();
		expect(getByText('Inactive Pages').parentElement).toHaveClass('active');
	});
});
