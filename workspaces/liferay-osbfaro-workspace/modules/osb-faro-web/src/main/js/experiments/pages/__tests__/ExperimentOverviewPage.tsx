import client from 'shared/apollo/client';
import ExperimentOverviewPage from '../ExperimentOverviewPage';
import mockStore from 'test/mock-store';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {fireEvent, render} from '@testing-library/react';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {
	mockExperimentDraftReq,
	mockExperimentReq,
	mockExperimentStatusReq,
} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '2000',
		groupId: '1000',
		id: '123',
		query: {},
	}),
}));

const WrappedComponent = ({mocks}: {mocks: any[]}) => (
	<ApolloProvider client={client}>
		<Provider store={mockStore()}>
			<MemoryRouter
				initialEntries={['/workspace/1000/2000/tests/overview/123']}
			>
				<RouterRoutes>
					<Route
						element={
							<MockedProvider mocks={mocks}>
								<ExperimentOverviewPage />
							</MockedProvider>
						}
						path={`${Routes.TESTS_OVERVIEW}/*`}
					/>
				</RouterRoutes>
			</MemoryRouter>
		</Provider>
	</ApolloProvider>
);

describe('ExperimentOverviewPage', () => {
	it('renders review and delete button in the DRAFT status', async () => {
		const {container, findByRole} = render(
			<WrappedComponent
				mocks={[
					mockExperimentStatusReq({status: 'DRAFT'}),
					mockExperimentDraftReq(),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		const reviewButton = (await findByRole('link', {
			name: /review/i,
		})) as HTMLAnchorElement;
		const deleteButton = (await findByRole('link', {
			name: /delete/i,
		})) as HTMLAnchorElement;

		const header = container.querySelector('.header-root')!;

		expect(header.contains(reviewButton)).toBeTruthy();
		expect(header.contains(deleteButton)).toBeTruthy();

		expect(reviewButton).toBeInTheDocument();
		expect(reviewButton.href).toEqual(
			'https://www.beryl.com/experiment-test?segmentsExperimentKey=123&segmentsExperimentAction=reviewAndRun'
		);

		expect(deleteButton).toBeInTheDocument();
		expect(deleteButton.href).toEqual(
			'https://www.beryl.com/experiment-test?segmentsExperimentKey=123&segmentsExperimentAction=delete'
		);
	});

	it('renders terminate button in the RUNNING status', async () => {
		const {container, findByRole} = render(
			<WrappedComponent
				mocks={[
					mockExperimentStatusReq({status: 'RUNNING'}),
					mockExperimentReq({
						status: 'RUNNING',
					}),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		const terminateButton = (await findByRole('link', {
			name: /terminate/i,
		})) as HTMLAnchorElement;

		const header = container.querySelector('.header-root')!;

		expect(header.contains(terminateButton)).toBeTruthy();

		expect(terminateButton).toBeInTheDocument();
		expect(terminateButton.href).toEqual(
			'https://www.beryl.com/experiment-test?segmentsExperimentKey=123&segmentsExperimentAction=terminate'
		);
	});

	it('renders publish and delete button to experiment to status FINISHED_NO_WINNER', async () => {
		const {container, findByRole} = render(
			<WrappedComponent
				mocks={[
					mockExperimentStatusReq({status: 'FINISHED_NO_WINNER'}),
					mockExperimentReq({
						status: 'FINISHED_NO_WINNER',
					}),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		const publishButton = (await findByRole('link', {
			name: /publish/i,
		})) as HTMLAnchorElement;
		const deleteButton = (await findByRole('link', {
			name: /delete/i,
		})) as HTMLAnchorElement;

		const header = container.querySelector('.header-root')!;

		expect(header.contains(publishButton)).toBeTruthy();
		expect(header.contains(deleteButton)).toBeTruthy();

		expect(publishButton).toBeInTheDocument();
		expect(publishButton.href).toEqual(
			'https://www.beryl.com/experiment-test?segmentsExperimentKey=123&segmentsExperimentAction=publish'
		);

		expect(deleteButton).toBeInTheDocument();
		expect(deleteButton.href).toEqual(
			'https://www.beryl.com/experiment-test?segmentsExperimentKey=123&segmentsExperimentAction=delete'
		);
	});

	it('renders publishabel and delete buttons to experiment to status TERMINATED', async () => {
		const {container, findByRole} = render(
			<WrappedComponent
				mocks={[
					mockExperimentStatusReq({status: 'TERMINATED'}),
					mockExperimentReq({
						publishable: true,
						status: 'TERMINATED',
					}),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		const publishButton = (await findByRole('link', {
			name: /publish/i,
		})) as HTMLAnchorElement;
		const deleteButton = (await findByRole('link', {
			name: /delete/i,
		})) as HTMLAnchorElement;

		const header = container.querySelector('.header-root')!;

		expect(header.contains(publishButton)).toBeTruthy();
		expect(header.contains(deleteButton)).toBeTruthy();

		expect(publishButton).toBeInTheDocument();
		expect(publishButton.href).toEqual(
			'https://www.beryl.com/experiment-test?segmentsExperimentKey=123&segmentsExperimentAction=publish'
		);

		expect(deleteButton).toBeInTheDocument();
		expect(deleteButton.href).toEqual(
			'https://www.beryl.com/experiment-test?segmentsExperimentKey=123&segmentsExperimentAction=delete'
		);
	});

	it('renders publishabel and delete buttons to experiment to status FINISHED_WINNER', async () => {
		const {container, findByRole} = render(
			<WrappedComponent
				mocks={[
					mockExperimentStatusReq({status: 'FINISHED_WINNER'}),
					mockExperimentReq({
						publishable: true,
						status: 'FINISHED_WINNER',
					}),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		const publishButton = (await findByRole('link', {
			name: /publish/i,
		})) as HTMLAnchorElement;
		const deleteButton = (await findByRole('link', {
			name: /delete/i,
		})) as HTMLAnchorElement;

		const header = container.querySelector('.header-root')!;

		expect(header.contains(publishButton)).toBeTruthy();
		expect(header.contains(deleteButton)).toBeTruthy();

		expect(publishButton).toBeInTheDocument();
		expect(publishButton.href).toEqual(
			'https://www.beryl.com/experiment-test?segmentsExperimentKey=123&segmentsExperimentAction=publish'
		);

		expect(deleteButton).toBeInTheDocument();
		expect(deleteButton.href).toEqual(
			'https://www.beryl.com/experiment-test?segmentsExperimentKey=123&segmentsExperimentAction=delete'
		);
	});

	it('renders publishabel and delete buttons to experiment to status FINISHED_NO_WINNER', async () => {
		const {container, findByRole} = render(
			<WrappedComponent
				mocks={[
					mockExperimentStatusReq({status: 'FINISHED_NO_WINNER'}),
					mockExperimentReq({
						publishable: true,
						status: 'FINISHED_NO_WINNER',
					}),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		const publishButton = (await findByRole('link', {
			name: /publish/i,
		})) as HTMLAnchorElement;
		const deleteButton = (await findByRole('link', {
			name: /delete/i,
		})) as HTMLAnchorElement;

		const header = container.querySelector('.header-root')!;

		expect(header.contains(publishButton)).toBeTruthy();
		expect(header.contains(deleteButton)).toBeTruthy();

		expect(publishButton).toBeInTheDocument();
		expect(publishButton.href).toEqual(
			'https://www.beryl.com/experiment-test?segmentsExperimentKey=123&segmentsExperimentAction=publish'
		);

		expect(deleteButton).toBeInTheDocument();
		expect(deleteButton.href).toEqual(
			'https://www.beryl.com/experiment-test?segmentsExperimentKey=123&segmentsExperimentAction=delete'
		);
	});

	it('renders publishabel and delete buttons to experiment to status FINISHED_WINNER', async () => {
		const {container, findByRole} = render(
			<WrappedComponent
				mocks={[
					mockExperimentStatusReq({status: 'FINISHED_WINNER'}),
					mockExperimentReq({
						publishable: true,
						status: 'FINISHED_WINNER',
					}),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		const publishButton = (await findByRole('link', {
			name: /publish/i,
		})) as HTMLAnchorElement;
		const deleteButton = (await findByRole('link', {
			name: /delete/i,
		})) as HTMLAnchorElement;

		expect(publishButton).toBeInTheDocument();
		expect(publishButton.href).toEqual(
			'https://www.beryl.com/experiment-test?segmentsExperimentKey=123&segmentsExperimentAction=publish'
		);

		expect(deleteButton).toBeInTheDocument();
		expect(deleteButton.href).toEqual(
			'https://www.beryl.com/experiment-test?segmentsExperimentKey=123&segmentsExperimentAction=delete'
		);
	});

	it('renders publishabel and delete buttons to experiment to status FINISHED_NO_WINNER', async () => {
		const {container, findByRole} = render(
			<WrappedComponent
				mocks={[
					mockExperimentStatusReq({status: 'FINISHED_NO_WINNER'}),
					mockExperimentReq({
						publishable: true,
						status: 'FINISHED_NO_WINNER',
					}),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		const publishButton = (await findByRole('link', {
			name: /publish/i,
		})) as HTMLAnchorElement;
		const deleteButton = (await findByRole('link', {
			name: /delete/i,
		})) as HTMLAnchorElement;

		expect(publishButton).toBeInTheDocument();
		expect(publishButton.href).toEqual(
			'https://www.beryl.com/experiment-test?segmentsExperimentKey=123&segmentsExperimentAction=publish'
		);

		expect(deleteButton).toBeInTheDocument();
		expect(deleteButton.href).toEqual(
			'https://www.beryl.com/experiment-test?segmentsExperimentKey=123&segmentsExperimentAction=delete'
		);
	});

	it('renders delete button to experiment to status TERMINATED', async () => {
		const {container, findByRole} = render(
			<WrappedComponent
				mocks={[
					mockExperimentStatusReq({status: 'TERMINATED'}),
					mockExperimentReq({
						status: 'TERMINATED',
					}),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		const deleteButton = await findByRole('button', {name: /delete/i});

		expect(deleteButton).toBeInTheDocument();

		fireEvent.click(deleteButton);

		expect(document.querySelector('.modal')).toBeInTheDocument();
	});

	it('renders published label to the control variant', async () => {
		const {container, findByText} = render(
			<WrappedComponent
				mocks={[
					mockExperimentStatusReq({status: 'TERMINATED'}),
					mockExperimentReq({
						publishable: true,
						publishedDXPVariantId: 'DEFAULT' as any,
						status: 'TERMINATED',
					}),
				]}
			/>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(await findByText(/published/i)).toBeInTheDocument();
	});

	it('renders published label to the second variant', async () => {
		const {container, findByText} = render(
			<WrappedComponent
				mocks={[
					mockExperimentStatusReq({status: 'TERMINATED'}),
					mockExperimentReq({
						publishable: true,
						publishedDXPVariantId: '44167' as any,
						status: 'TERMINATED',
					}),
				]}
			/>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(await findByText(/published/i)).toBeInTheDocument();
	});

	it('renders test sessions card when test type is AB', async () => {
		const {container, queryByText} = render(
			<WrappedComponent
				mocks={[
					mockExperimentStatusReq({status: 'RUNNING'}),
					mockExperimentReq({
						status: 'RUNNING',
						type: 'AB',
					}),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(queryByText('Test Sessions')).toBeInTheDocument();

		expect(queryByText('Test Traffic')).toBeNull();
	});

	it('renders test traffic card when test type is MAB', async () => {
		const {container, queryByText} = render(
			<WrappedComponent
				mocks={[
					mockExperimentStatusReq({status: 'RUNNING'}),
					mockExperimentReq({
						status: 'RUNNING',
						type: 'MAB',
					}),
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(queryByText('Test Traffic')).toBeInTheDocument();

		expect(queryByText('Test Sessions')).toBeNull();
	});
});
