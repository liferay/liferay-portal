import MembershipsCard from '../MembershipsCard';
import React from 'react';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {warmFrontendDataSet} from 'test/warm-frontend-data-set';

jest.unmock('react-dom');

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: (props: any) => (
		<div data-testid="fds-component" id={props.id} />
	),
}));

const renderCard = () =>
	render(
		<MemoryRouter>
			<MembershipsCard campaignId="7" channelId="123" groupId="23" />
		</MemoryRouter>
	);

beforeAll(warmFrontendDataSet);

describe('MembershipsCard', () => {
	afterEach(cleanup);

	it('should title the card Membership', () => {
		renderCard();

		expect(screen.getByText('Membership')).toBeInTheDocument();
	});

	it('should render both tabs with Accounts Touched active', () => {
		renderCard();

		expect(screen.getByText('Accounts Touched')).toHaveClass('active');
		expect(screen.getByText('Individuals Touched')).not.toHaveClass(
			'active'
		);
	});

	it('should render the accounts table under Accounts Touched', () => {
		renderCard();

		expect(screen.getByTestId('fds-component')).toHaveAttribute(
			'id',
			'campaign-accounts-dataset'
		);
	});

	it('should select the Individuals Touched tab when it is clicked', () => {
		renderCard();

		fireEvent.click(screen.getByText('Individuals Touched'));

		expect(screen.getByText('Individuals Touched')).toHaveClass('active');
		expect(screen.getByText('Accounts Touched')).not.toHaveClass('active');
	});

	it('should render the individuals table under Individuals Touched', () => {
		renderCard();

		fireEvent.click(screen.getByText('Individuals Touched'));

		expect(screen.getByTestId('fds-component')).toHaveAttribute(
			'id',
			'campaign-individuals-dataset'
		);
	});

	it('should render the accounts table again on returning to Accounts Touched', () => {
		renderCard();

		fireEvent.click(screen.getByText('Individuals Touched'));
		fireEvent.click(screen.getByText('Accounts Touched'));

		expect(screen.getByTestId('fds-component')).toHaveAttribute(
			'id',
			'campaign-accounts-dataset'
		);
	});
});
