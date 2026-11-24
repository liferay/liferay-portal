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

describe('InterestDetails', () => {
	it('should render', async () => {
		const {container, getByText} = render(
			<MemoryRouter>
				<InterestDetails {...defaultProps} />
			</MemoryRouter>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText(/^back to interests$/i)).toBeInTheDocument();
	});

	it('should render an active pages list tab', async () => {
		const {container, getByRole, getByText} = render(
			<MemoryRouter>
				<InterestDetails {...defaultProps} />
			</MemoryRouter>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText(/^active pages$/i)).toBeTruthy();
		expect(
			getByRole('link', {name: /^active pages$/i})
		).toHaveAttribute('aria-current', 'page');
	});

	it('should render an inactive pages list tab', async () => {
		const {container, getByRole, getByText} = render(
			<MemoryRouter>
				<InterestDetails {...defaultProps} active='false' />
			</MemoryRouter>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText(/^inactive pages$/i)).toBeTruthy();
		expect(
			getByRole('link', {name: /^inactive pages$/i})
		).toHaveAttribute('aria-current', 'page');
	});
});
