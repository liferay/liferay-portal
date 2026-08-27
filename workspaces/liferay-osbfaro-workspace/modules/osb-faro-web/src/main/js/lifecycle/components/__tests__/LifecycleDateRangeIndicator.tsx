import '@testing-library/jest-dom';
import LifecycleDateRangeIndicator from '../LifecycleDateRangeIndicator';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

describe('LifecycleDateRangeIndicator', () => {
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
		render(<LifecycleDateRangeIndicator />);

		expect(
			screen.getByText('Mar 17, 2026 – Jun 14, 2026')
		).toBeInTheDocument();
	});
});
