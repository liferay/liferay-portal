import * as API from 'shared/api';
import EmailReports from '../EmailReports';
import mockStore from 'test/mock-store';
import React from 'react';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';

import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '2000',
	}),
}));

const WrappedComponent = ({reports, ...otherProps}: any) => {

	// @ts-ignore
	API.preferences.fetchEmailReport = jest.fn(() => Promise.resolve(reports));

	return (

		// @ts-ignore
		<Provider store={mockStore()}>
			<EmailReports channelId="1234" {...otherProps} />
		</Provider>
	);
};

describe('EmailReports', () => {
	it('should render', async () => {
		const {container} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render with config btn enabled', async () => {
		const {container} = render(<WrappedComponent sitesSynced />);

		await waitForLoadingToBeRemoved(container);

		const configBtn = container.querySelector('button');

		expect(container).toContainElement(configBtn);
		expect(configBtn).toBeInTheDocument();
		expect(configBtn).toBeEnabled();
	});

	it('should render email report message w/ status disabled', async () => {
		const {container, getByText} = render(
			<WrappedComponent
				reports={{
					1234: {enabled: false},
				}}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Email Reports: Disabled'));
	});

	it('should render email report message w/ status enabled', async () => {
		const {container, getByText} = render(
			<WrappedComponent
				reports={{
					1234: {enabled: true},
				}}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Email Reports: Enabled'));
	});

	it('should render with config btn disabled', async () => {
		const {container} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(container);

		const configBtn = container.querySelector('button');

		expect(container).toContainElement(configBtn);
		expect(configBtn).toBeInTheDocument();
		expect(configBtn).toBeDisabled();
	});
});
