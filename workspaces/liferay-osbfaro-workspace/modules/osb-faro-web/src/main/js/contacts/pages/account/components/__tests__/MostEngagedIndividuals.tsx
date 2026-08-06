import MostEngagedIndividuals from '../MostEngagedIndividuals';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {useParams} from 'react-router-dom';

jest.unmock('react-dom');

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: (props: any) => (
		<div data-testid="fds-component" id={props.id} />
	),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: jest.fn(),
}));

const mockedUseParams = useParams as jest.Mock;

describe('MostEngagedIndividuals', () => {
	afterEach(cleanup);

	beforeEach(() => {
		mockedUseParams.mockReturnValue({
			channelId: '456',
			groupId: '23',
			id: 'acc-1',
		});
	});

	it('should render the card title', () => {
		render(<MostEngagedIndividuals />);

		expect(
			screen.getByText('MOST ENGAGED INDIVIDUALS')
		).toBeInTheDocument();
	});

	it('should render the individuals data set in preview mode', () => {
		render(<MostEngagedIndividuals />);

		expect(screen.getByTestId('fds-component')).toHaveAttribute(
			'id',
			'most-engaged-individuals-dataset'
		);
	});

	it('should link to the account profile for the full list', () => {
		render(<MostEngagedIndividuals />);

		expect(
			screen.getByRole('link', {name: 'View All'}).getAttribute('href')
		).toContain('/contacts/accounts/acc-1/profile');
	});

	it('should render no link while the account id is missing', () => {
		mockedUseParams.mockReturnValue({channelId: '456', groupId: '23'});

		render(<MostEngagedIndividuals />);

		expect(
			screen.queryByRole('link', {name: 'View All'})
		).not.toBeInTheDocument();
	});
});
