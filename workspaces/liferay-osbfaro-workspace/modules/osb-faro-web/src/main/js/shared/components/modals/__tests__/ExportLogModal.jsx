import ExportLogModal from '../ExportLogModal';
import mockStore from 'test/mock-store';
import React from 'react';
import {act, fireEvent, render, screen, waitFor} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq} from 'test/graphql-data';
import {noop} from 'lodash';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

// Mock DateRangeInput to simplify the test and avoid complex portal/calendar logic
jest.mock('shared/components/DateRangeInput', () => ({onChange}) => (
	<div data-testid='mock-date-range-input'>
		<button
			onClick={() =>
				onChange({
					end: require('moment')('2024-01-02'),
					start: require('moment')('2024-01-01')
				})
			}
		>
			{'Set Date Range'}
		</button>
	</div>
));

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({
		timeZoneId: 'UTC'
	})
}));

const Wrapper = ({children, mocks = [mockPreferenceReq()]}) => (
	<Provider store={mockStore()}>
		<MemoryRouter initialEntries={['/workspace/123/456/2000']}>
			<Routes>
				<Route
					element={
						<MockedProvider
							cache={
								new InMemoryCache({
									addTypename: false
								})
							}
							mocks={mocks}
						>
							{children}
						</MockedProvider>
					}
					path='/workspace/:groupId/:channelId/:assetId'
				/>
			</Routes>
		</MemoryRouter>
	</Provider>
);

describe('ExportLogModal', () => {
	it('renders', () => {
		const {container} = render(
			<Wrapper>
				<ExportLogModal
					description='Test description'
					onClose={noop}
					title='Test Title'
				/>
			</Wrapper>
		);

		expect(container).toMatchSnapshot();
	});

	it('should have a loading state when download is triggered', async () => {
		let resolveSubmit;
		const onSubmit = jest.fn(
			() =>
				new Promise(resolve => {
					resolveSubmit = resolve;
				})
		);

		const {container} = render(
			<Wrapper>
				<ExportLogModal
					description='Test description'
					onClose={noop}
					onSubmit={onSubmit}
					title='Test Title'
				/>
			</Wrapper>
		);

		// Set date range via mock
		fireEvent.click(screen.getByText('Set Date Range'));

		const downloadBtn = screen.getByRole('button', {name: /download/i});

		// Wait for the button to be enabled
		await waitFor(() => expect(downloadBtn).not.toBeDisabled());

		fireEvent.click(downloadBtn);

		// Verify loading indicator appears in the button
		expect(container.querySelector('.loading-root')).toBeTruthy();

		// Resolve the submission
		await act(async () => {
			resolveSubmit('csv-data');
		});

		// Verify loading indicator is removed
		await waitFor(() =>
			expect(container.querySelector('.loading-root')).toBeNull()
		);
	});

	it('should stop loading if the download failed', async () => {
		let rejectSubmit;
		const onSubmit = jest.fn(
			() =>
				new Promise((_, reject) => {
					rejectSubmit = reject;
				})
		);

		const {container} = render(
			<Wrapper>
				<ExportLogModal
					description='Test description'
					onClose={noop}
					onSubmit={onSubmit}
					title='Test Title'
				/>
			</Wrapper>
		);

		// Set date range via mock
		fireEvent.click(screen.getByText('Set Date Range'));

		const downloadBtn = screen.getByRole('button', {name: /download/i});

		await waitFor(() => expect(downloadBtn).not.toBeDisabled());

		fireEvent.click(downloadBtn);

		expect(container.querySelector('.loading-root')).toBeTruthy();

		// Reject the submission
		await act(async () => {
			rejectSubmit(new Error('fail'));
		});

		await waitFor(() =>
			expect(container.querySelector('.loading-root')).toBeNull()
		);
	});
});
