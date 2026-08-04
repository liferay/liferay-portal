import AccountIndividuals from '../AccountIndividuals';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: (props: any) => (
		<div data-testid="fds-component" id={props.id} />
	),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '456', groupId: '23', id: 'acc-1'}),
}));

describe('AccountIndividuals', () => {
	afterEach(cleanup);

	it('should render the card title', () => {
		render(<AccountIndividuals />);

		expect(screen.getByText('Account Individuals')).toBeInTheDocument();
	});

	it('should render the card description', () => {
		render(<AccountIndividuals />);

		expect(
			screen.getByText(
				'Lists all individuals associated with this account.'
			)
		).toBeInTheDocument();
	});

	it('should render the individuals data set', () => {
		render(<AccountIndividuals />);

		expect(screen.getByTestId('fds-component')).toHaveAttribute(
			'id',
			'account-individuals-dataset'
		);
	});
});
