jest.mock('../event-analysis-editor', () => ({
	__esModule: true,
	default: jest.fn(() => 'event analysis editor')
}));

jest.mock('shared/components/download-report/DownloadPDFReport', () => ({
	__esModule: true,
	ReportContainer: jest.requireActual(
		'shared/components/download-report/DownloadPDFReport'
	).ReportContainer,
	default: () => null
}));

jest.mock('shared/components/NavigationWarning', () => () => null);

jest.mock('shared/components/modals/LoadingModal', () => ({title}) => (
	<div data-testid='loading-modal'>{title}</div>
));

import BaseEventAnalysisPage from '../BaseEventAnalysisPage';
import DataSourcesProvider from 'shared/context/dataSources';
import EventAnalysisEditor from '../event-analysis-editor';
import mockStore, {mockStoreData} from 'test/mock-store';
import React from 'react';
import {CreateEventAnalysisMutation} from 'event-analysis/queries/EventAnalysisQuery';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

const EVENT = {displayName: 'Page Viewed', id: '1', name: 'pageViewed'};

const renderPage = () =>
	render(
		<Provider store={mockStore(mockStoreData)}>
			<MockedProvider
				addTypename={false}
				mocks={[
					{
						request: {query: CreateEventAnalysisMutation},
						result: {data: {createEventAnalysis: {id: '1'}}},
						variableMatcher: () => true
					}
				]}
			>
				<MemoryRouter
					initialEntries={['/workspace/23/1/event-analysis/create']}
				>
					<RouterRoutes>
						<Route
							element={
								<DataSourcesProvider groupId="23">
									<BaseEventAnalysisPage event={EVENT} />
								</DataSourcesProvider>
							}
							path="workspace/:groupId/:channelId/event-analysis/create"
						/>

						<Route
							element={<p>{'event analysis list'}</p>}
							path="workspace/:groupId/:channelId/event-analysis"
						/>
					</RouterRoutes>
				</MemoryRouter>
			</MockedProvider>
		</Provider>
	);

describe('BaseEventAnalysisPage', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		EventAnalysisEditor.mockImplementation(() => 'event analysis editor');
	});

	it('shows the creating title while saving a new analysis', async () => {
		renderPage();

		fireEvent.change(screen.getByLabelText(/^title/i), {
			target: {value: 'My Analysis'}
		});

		fireEvent.click(screen.getByText('Save Analysis'));

		expect(await screen.findByTestId('loading-modal')).toHaveTextContent(
			'Creating...'
		);

		expect(
			await screen.findByText('event analysis list')
		).toBeInTheDocument();
	});

	it('does not rerender the editor while typing the title', async () => {
		renderPage();

		const editorRenders = EventAnalysisEditor.mock.calls.length;

		const title = screen.getByLabelText(/^title/i);

		fireEvent.change(title, {target: {value: 'M'}});
		fireEvent.change(title, {target: {value: 'My'}});

		await waitFor(() => expect(title).toHaveValue('My'));

		expect(EventAnalysisEditor.mock.calls.length).toBe(editorRenders);
	});

	it('lets the report download wait for the editor to load', () => {
		EventAnalysisEditor.mockImplementation(() => (
			<span className="loading-animation" />
		));

		renderPage();

		expect(
			document.querySelector('.page-container .loading-animation')
		).toBeInTheDocument();
	});

	it('places the title and the editor in the BaseEditPage main content region', () => {
		renderPage();

		const main = screen
			.getByText('event analysis editor')
			.closest('.base-edit-page-main');

		expect(main).toContainElement(screen.getByLabelText(/^title/i));
		expect(main.parentElement).toHaveClass('base-edit-page-body');
	});
});
