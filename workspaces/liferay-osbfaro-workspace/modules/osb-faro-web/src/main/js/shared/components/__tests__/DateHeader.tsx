import DateHeader from '../DateHeader';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

describe('DateHeader', () => {
	afterEach(cleanup);

	it('shows the day title and its event count', () => {
		render(<DateHeader title="Yesterday" totalEvents={3} />);

		expect(screen.getByText('Yesterday')).toBeInTheDocument();
		expect(screen.getByText('3')).toBeInTheDocument();
	});

	it('omits the count when there is no event total', () => {
		const {container} = render(<DateHeader title="Yesterday" />);

		expect(screen.getByText('Yesterday')).toBeInTheDocument();
		expect(
			container.querySelector('.event-count-pill')
		).not.toBeInTheDocument();
	});
});
