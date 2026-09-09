import ActivitySectionEmptyState from '../ActivitySectionEmptyState';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const renderEmptyState = (props?: {description?: string; title?: string}) =>
	render(
		<ActivitySectionEmptyState
			linkHref="https://learn.liferay.com/accounts"
			linkLabel="Learn more about accounts"
			{...props}
		/>
	);

describe('ActivitySectionEmptyState', () => {
	afterEach(cleanup);

	it('speaks about the period rather than a data source', () => {
		renderEmptyState();

		expect(
			screen.getByText(/no activity on the selected period/i)
		).toBeInTheDocument();
		expect(
			screen.getByText(/check back later.*different date range/i)
		).toBeInTheDocument();
	});

	it('opens its documentation link in a new tab', () => {
		renderEmptyState();

		const link = screen.getByText(/learn more about accounts/i);

		expect(link).toHaveAttribute(
			'href',
			'https://learn.liferay.com/accounts'
		);
		expect(link).toHaveAttribute('target', '_blank');
	});

	it('lets a surface override the copy', () => {
		renderEmptyState({title: 'Nothing for this day'});

		expect(screen.getByText('Nothing for this day')).toBeInTheDocument();
	});
});
