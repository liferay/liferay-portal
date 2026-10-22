import React from 'react';
import {Map} from 'immutable';
import {KnownIndividualsSession} from '../KnownIndividualsSession';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({timeZoneId: 'UTC'}),
}));

jest.mock('../UsageMetricBarChart', () => ({
	UsageMetricBarChart: ({
		items,
	}: {
		items: {[key: string]: {label: string}};
	}) => (
		<ul>
			{Object.values(items).map(({label}) => (
				<li key={label}>{label}</li>
			))}
		</ul>
	),
}));

const renderKnownIndividualsSession = (metrics = {}) =>
	render(
		<KnownIndividualsSession
			currentPlan={{
				metrics: Map({
					individuals: {count: 100, limit: 300, status: 'OK'},
					syncedIndividualsCount: 20,
					...metrics,
				}),
				startDate: '2020-01-01',
			}}
		/>
	);

describe('KnownIndividualsSession', () => {
	it('should render the available known individuals', () => {
		const {container} = renderKnownIndividualsSession();

		expect(container).toHaveTextContent(
			'200 known individuals are available'
		);
	});

	it('should render unlimited available known individuals when the limit is unlimited', () => {
		const {container} = renderKnownIndividualsSession({
			individuals: {count: 100, limit: -1, status: 'OK'},
		});

		expect(container).toHaveTextContent(
			'Unlimited known individuals are available'
		);
	});

	it('should render zero available known individuals when the limit is reached', () => {
		const {container} = renderKnownIndividualsSession({
			individuals: {count: 300, limit: 300, status: 'OK'},
		});

		expect(container).toHaveTextContent(
			'0 known individuals are available'
		);
	});

	it('should render the synced individuals count', () => {
		const {container} = renderKnownIndividualsSession();

		expect(container).toHaveTextContent(
			'Individuals Synced to Analytics Cloud: 20'
		);
	});

	it('should render a zero synced individuals count', () => {
		const {container} = renderKnownIndividualsSession({
			syncedIndividualsCount: 0,
		});

		expect(container).toHaveTextContent(
			'Individuals Synced to Analytics Cloud: 0'
		);
	});
});
