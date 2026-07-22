import BasePage from 'shared/components/base-page';
import mockStore from 'test/mock-store';
import React from 'react';
import VisitorsByTimeCard, {
	formatHour,
	renderTooltip
} from '../VisitorsByTimeCard';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq, mockTimeRangeReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const MOCK_CONTEXT = {
	rangeKey: {defaultValue: '30'},
	router: {
		params: {
			channelId: '123',
			groupId: '2000'
		},
		query: {
			rangeKey: '30'
		}
	}
};

const WrappedComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter initialEntries={['/workspace/2000/123']}>
			<RouterRoutes>
				<Route
					element={
						<BasePage.Context.Provider value={MOCK_CONTEXT}>
							<MockedProvider
								cache={
									new InMemoryCache({
										addTypename: false,
										freezeResults: false
									})
								}
								mocks={[mockTimeRangeReq(), mockPreferenceReq()]}
							>
								<VisitorsByTimeCard {...props} />
							</MockedProvider>
						</BasePage.Context.Provider>
					}
					path='/workspace/:groupId/:channelId/*'
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('VisitorsByTimeCard', () => {
	it('render', async () => {
		const {container} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});

describe('renderTooltip', () => {
	it('render', () => {
		const {container} = render(
			renderTooltip({column: 'Sunday', row: 12, value: 98})
		);

		expect(container).toMatchSnapshot();
	});
});

describe('formatHour', () => {
	it.each`
		hour  | retVal
		${0}  | ${'12 AM'}
		${6}  | ${'6 AM'}
		${11} | ${'11 AM'}
		${12} | ${'12 PM'}
		${18} | ${'6 PM'}
	`('return $retVal when formatting $hour', ({hour, retVal}) => {
		expect(formatHour(hour)).toBe(retVal);
	});
});
