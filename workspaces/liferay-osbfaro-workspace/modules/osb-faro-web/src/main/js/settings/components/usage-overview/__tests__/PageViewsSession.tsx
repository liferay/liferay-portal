import React from 'react';
import {Map} from 'immutable';
import {PageViewsSession} from '../PageViewsSession';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({timeZoneId: 'UTC'}),
}));

const renderPageViewsSession = (pageViews = {}) =>
	render(
		<PageViewsSession
			currentPlan={{
				metrics: Map({
					pageViews: {
						count: 100,
						limit: 300,
						status: 'OK',
						...pageViews,
					},
				}),
				startDate: '2020-01-01',
			}}
		/>
	);

describe('PageViewsSession', () => {
	it('should render the available page views', () => {
		const {container} = renderPageViewsSession();

		expect(container).toHaveTextContent('200 page views are available');
	});

	it('should render unlimited available page views when the limit is unlimited', () => {
		const {container} = renderPageViewsSession({limit: -1});

		expect(container).toHaveTextContent(
			'Unlimited page views are available'
		);
	});

	it('should render zero available page views when the limit is reached', () => {
		const {container} = renderPageViewsSession({count: 300});

		expect(container).toHaveTextContent('0 page views are available');
	});
});
