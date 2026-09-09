import PaginationBar from '../PaginationBar';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

const DefaultComponent = props => (
	<MemoryRouter>
		<PaginationBar
			href=''
			page={3}
			selectedDelta={10}
			totalItems={100}
			{...props}
		/>
	</MemoryRouter>
);

describe('PaginationBar', () => {
	afterEach(cleanup);

	it('names what it counts when the caller says so', () => {
		const {container} = render(
			<DefaultComponent
				resultsMessagePlural='Showing {0} to {1} of {2} campaign entries.'
				resultsMessageSingular='Showing {0} to {1} of {2} campaign entry.'
			/>
		);

		expect(
			container.querySelector('.pagination-results')
		).toHaveTextContent(/campaign entr/i);
	});

	it('falls back to the shared message when the caller says nothing', () => {
		const {container} = render(<DefaultComponent />);

		expect(
			container.querySelector('.pagination-results')
		).toHaveTextContent(/of 100 entr/i);
	});

	it('should render', () => {
		const {container} = render(<DefaultComponent />);

		expect(
			container.querySelector('.pagination-bar-root')
		).toBeInTheDocument();
	});

	it('should render with small size', () => {
		const {container} = render(<DefaultComponent size='sm' />);

		expect(container.querySelector('.pagination-sm')).toBeTruthy();
	});

	it('should render with large size', () => {
		const {container} = render(<DefaultComponent size='lg' />);

		expect(container.querySelector('.pagination-lg')).toBeTruthy();
	});

	it('should render different deltas', () => {
		const {container} = render(
			<DefaultComponent
				deltas={[1, 2, 3, 4]}
				page={1}
				selectedDelta={1}
				totalItems={10}
			/>
		);

		expect(container.querySelector('.pagination-bar-root')).toBeTruthy();
		expect(
			container.querySelector('.pagination-items-per-page')
		).toBeTruthy();
	});
});
