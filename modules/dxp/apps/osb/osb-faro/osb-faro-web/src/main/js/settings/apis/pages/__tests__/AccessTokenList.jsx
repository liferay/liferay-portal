import 'test/mock-modal';
import * as API from 'shared/api';
import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import {AccessTokenList} from '../AccessTokenList';
import {cleanup, fireEvent, getByText, render} from '@testing-library/react';
import {getISODate} from 'shared/util/date';
import {mockGetDateNow} from 'test/mock-date';
import {open} from 'shared/actions/modals';
import {Provider} from 'react-redux';
import {StaticRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

const defaultProps = {
	groupId: '23'
};

const DefaultComponent = props => (
	<StaticRouter>
		<Provider store={mockStore()}>
			<AccessTokenList {...defaultProps} {...props} />
		</Provider>
	</StaticRouter>
);

describe('AccessTokenList', () => {
	beforeAll(() => {
		mockGetDateNow(data.getTimestamp(0));
	});

	afterEach(cleanup);

	it('should render', async () => {
		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should display the card with the options to create a new token if there is no token', async () => {
		API.apiTokens.search.mockReturnValueOnce(Promise.resolve([]));

		const {container, queryByTestId} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(queryByTestId('generate-token-button')).toBeTruthy();
		expect(container.querySelector('.table-root')).toBeNull();
	});

	it('should show the generated token in a list and the "Generate Token" button should no longer be visible', async () => {
		API.apiTokens.search.mockReturnValueOnce(Promise.resolve([]));

		const {container, getByTestId, queryByTestId} = render(
			<DefaultComponent />
		);

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('.table-root')).toBeNull();
		expect(queryByTestId('generate-token-button')).toBeTruthy();

		fireEvent.click(getByTestId('generate-token-button'));

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('.table-root')).toMatchSnapshot();
		expect(queryByTestId('generate-token-button')).toBeNull();
	});

	it('should open a modal to confirm revoking a token', async () => {
		const {container, getByText} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		fireEvent.click(getByText('Revoke'));

		expect(open).toBeCalled();
	});

	it('should display the "Generate Token" card  above the table if the token is expired', async () => {
		API.apiTokens.search.mockReturnValueOnce(
			Promise.resolve([
				data.mockApiToken({
					expirationDate: getISODate(data.getTimestamp(-1))
				})
			])
		);

		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(
			getByText(container.querySelector('.card-body'), 'Generate Token')
		).toBeTruthy();
		expect(container.querySelector('.table-root')).toMatchSnapshot();
	});

	it('should render the correct date on expiration date column when generated token is 30 days', async () => {
		API.apiTokens.search.mockReturnValueOnce(
			Promise.resolve([
				data.mockApiToken({
					createDate: '2023-05-11T19:35:28.338Z',
					expirationDate: '2023-06-12T19:35:28.000Z'
				})
			])
		);

		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(
			getByText(
				container.querySelector('td:nth-child(3)'),
				'Jun 12, 2023'
			)
		).toMatchSnapshot();
	});

	it('should render the correct date on expiration date column when generated token is 6 months', async () => {
		API.apiTokens.search.mockReturnValueOnce(
			Promise.resolve([
				data.mockApiToken({
					createDate: '2023-05-11T19:35:28.338Z',
					expirationDate: '2023-11-12T19:35:28.000Z'
				})
			])
		);

		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(
			getByText(
				container.querySelector('td:nth-child(3)'),
				'Nov 12, 2023'
			)
		).toMatchSnapshot();
	});

	it('should render the correct date on expiration date column when generated token is 1 year', async () => {
		API.apiTokens.search.mockReturnValueOnce(
			Promise.resolve([
				data.mockApiToken({
					createDate: '2023-05-11T19:35:28.338Z',
					expirationDate: '2024-05-12T19:35:28.000Z'
				})
			])
		);

		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(
			getByText(
				container.querySelector('td:nth-child(3)'),
				'May 12, 2024'
			)
		).toMatchSnapshot();
	});

	it('should render indefinite on expiration date column when generated token is indefinite', async () => {
		API.apiTokens.search.mockReturnValueOnce(
			Promise.resolve([
				data.mockApiToken({
					createDate: '2023-05-11T19:35:28.338Z',
					expirationDate: '2123-05-12T19:35:28.000Z'
				})
			])
		);

		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(
			getByText(container.querySelector('td:nth-child(3)'), 'Indefinite')
		).toMatchSnapshot();
	});
});
