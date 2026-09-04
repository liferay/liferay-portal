import '@testing-library/jest-dom';
import React from 'react';
import TrailingNinetyDayRange from '../TrailingNinetyDayRange';
import {cleanup, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

describe('TrailingNinetyDayRange', () => {
	afterEach(() => {
		cleanup();
		jest.useRealTimers();
	});

	beforeEach(() => {
		jest.useFakeTimers().setSystemTime(
			new Date('2026-06-15T12:00:00.000Z')
		);
	});

	it('shows the trailing 90-day range, not including today', () => {
		render(<TrailingNinetyDayRange />);

		expect(
			screen.getByText('Mar 17, 2026 – Jun 14, 2026')
		).toBeInTheDocument();
	});

	it('renders as secondary text at the size the design gives it', () => {
		render(<TrailingNinetyDayRange />);

		const range = screen.getByText('Mar 17, 2026 – Jun 14, 2026');

		expect(range).toHaveClass('text-secondary');
		expect(range).toHaveClass('text-4');
	});
});
