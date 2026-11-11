import React from 'react';
import {MemoryRouter} from 'react-router-dom';
import {render} from '@testing-library/react';
import {useRangeQueryValues} from 'shared/hooks/useRangeQueryValues';

jest.unmock('react-dom');

const renderRangeQueryValues = (queryString = '') => {
	let rangeQueryValues;

	const TestComponent = () => {
		rangeQueryValues = useRangeQueryValues();

		return null;
	};

	render(
		<MemoryRouter
			initialEntries={[
				`/workspace/23/321321/contacts/accounts/123123/interests/test${queryString}`,
			]}
		>
			<TestComponent />
		</MemoryRouter>
	);

	return rangeQueryValues;
};

describe('useRangeQueryValues', () => {
	it('should keep the custom range key on the route params only', () => {
		const {apiRangeParams, routeRangeParams} = renderRangeQueryValues(
			'?rangeEnd=2026-02-20&rangeKey=CUSTOM&rangeStart=2026-02-10'
		);

		expect(apiRangeParams).toEqual({
			rangeEnd: '2026-02-20',
			rangeStart: '2026-02-10',
		});
		expect(routeRangeParams).toEqual({
			rangeEnd: '2026-02-20',
			rangeKey: 'CUSTOM',
			rangeStart: '2026-02-10',
		});
	});

	it('should default to the last 30 days with no empty bounds', () => {
		const {apiRangeParams, rangeSelectors, routeRangeParams} =
			renderRangeQueryValues();

		expect(rangeSelectors.rangeKey).toBe('30');
		expect(apiRangeParams).toEqual({rangeKey: '30'});
		expect(routeRangeParams).toEqual({rangeKey: '30'});
	});
});
