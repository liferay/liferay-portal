import * as pedantic from 'test/pedantic';
import HeaderDefault from '../HeaderDefault';
import React from 'react';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {INTERVAL_KEY_MAP} from 'shared/util/time';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq, mockTimeRangeReq} from 'test/graphql-data';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const DefaultComponent = ({
	mocks = [mockTimeRangeReq(), mockPreferenceReq()],
	onChangeInterval = jest.fn(),
	...props
}) => (
	<MemoryRouter>
		<RouterRoutes>
			<Route
				element={
					<MockedProvider addTypename={false} mocks={mocks}>
						<HeaderDefault
							label='Title'
							onChangeInterval={onChangeInterval}
							{...props}
						/>
					</MockedProvider>
				}
				path='/*'
			/>
		</RouterRoutes>
	</MemoryRouter>
);

describe('HeaderDefault', () => {
	beforeEach(() => {
		pedantic.disable();
	});

	afterEach(() => {
		pedantic.enable();
	});

	it('should render', async () => {
		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should call the onChangeInterval prop fn with "day" if the rangekey is changed to an hourly value', async () => {
		const spy = jest.fn();
		render(
			<DefaultComponent onChangeInterval={spy} rangeKey='LAST_30_DAYS' />
		);

		await waitForLoadingToBeRemoved();

		const dropdownToggle = screen.getByRole('button', {
			name: /select date range/i
		});

		fireEvent.click(dropdownToggle);

		const last24HoursBtn = screen.getByText('Last 24 hours');

		fireEvent.click(last24HoursBtn);

		await waitFor(() =>
			expect(spy).toHaveBeenCalledWith(INTERVAL_KEY_MAP.day)
		);
	});
});
