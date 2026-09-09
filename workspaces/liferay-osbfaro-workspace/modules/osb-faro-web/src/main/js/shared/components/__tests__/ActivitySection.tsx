import ActivitySection from '../ActivitySection';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const renderSection = (props?: {label?: string}) =>
	render(
		<ActivitySection label="Timed Activity" {...props}>
			<div>{'Session list'}</div>
		</ActivitySection>
	);

describe('ActivitySection', () => {
	afterEach(cleanup);

	it('shows its label', () => {
		renderSection();

		expect(screen.getByText(/timed.activity/i)).toBeInTheDocument();
	});

	it('renders its children below the label', () => {
		renderSection();

		expect(screen.getByText('Session list')).toBeInTheDocument();
	});

	it('keeps the label when the children render an empty state', () => {
		render(
			<ActivitySection label="Day-Level">
				<div>{'There is no activity on the selected period'}</div>
			</ActivitySection>
		);

		expect(screen.getByText(/day.level/i)).toBeInTheDocument();
		expect(
			screen.getByText(/no activity on the selected period/i)
		).toBeInTheDocument();
	});
});
