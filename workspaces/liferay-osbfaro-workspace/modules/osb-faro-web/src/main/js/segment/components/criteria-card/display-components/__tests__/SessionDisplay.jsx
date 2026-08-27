import * as data from 'test/data';
import React from 'react';
import SessionDisplay from '../SessionDisplay';
import {cleanup, render} from '@testing-library/react';
import {
	CustomFunctionOperators,
	PropertyTypes,
	RelationalOperators,
	TimeSpans
} from 'segment/segment-editor/dynamic/utils/constants';
import {
	CHANNEL_OPTIONS,
	DEFAULT_UTM_PARAMETER_OPTIONS
} from 'segment/segment-editor/dynamic/utils/properties/session-properties';
import {List, Map} from 'immutable';
import {Property} from 'shared/util/records';

jest.unmock('react-dom');

describe('SessionDisplay', () => {
	const mockCriterion = {
		operatorName: CustomFunctionOperators.SessionsFilter,
		propertyName: 'context/browserName',
		value: Map({
			criterionGroup: Map({
				items: List([
					Map({
						operatorName: RelationalOperators.EQ,
						propertyName: 'context/browserName',
						value: 'Chrome'
					}),
					Map({
						operatorName: RelationalOperators.GT,
						propertyName: 'completeDate',
						value: TimeSpans.Last7Days
					})
				])
			})
		})
	};

	afterEach(cleanup);

	const mockProperty = data.getImmutableMock(Property, data.mockProperty, 1, {
		entityName: 'Individual',
		label: 'name',
		name: 'name',
		propertykey: 'session',
		type: PropertyTypes.SessionText
	});

	it('renders', () => {
		const {container} = render(
			<SessionDisplay criterion={mockCriterion} property={mockProperty} />
		);

		expect(container).toMatchSnapshot();
	});

	it('renders a Channel criterion w/ its operator and option label, and w/o a time period', () => {
		const criterion = {
			operatorName: CustomFunctionOperators.SessionsFilter,
			propertyName: 'context/channel',
			value: Map({
				criterionGroup: Map({
					items: List([
						Map({
							operatorName: RelationalOperators.EQ,
							propertyName: 'context/channel',
							value: 'direct'
						})
					])
				})
			})
		};

		// Built the way the catalog builds it: an Immutable Record keeps
		// `options` as the plain array it is handed, which is what the
		// value lookup relies on.

		const property = new Property({
			entityName: 'Session',
			label: 'Channel',
			name: 'context/channel',
			options: CHANNEL_OPTIONS,
			propertyKey: 'session',
			type: PropertyTypes.SessionChannel
		});

		const {container, getByText} = render(
			<SessionDisplay criterion={criterion} property={property} />
		);

		expect(getByText('is')).toBeTruthy();
		expect(getByText('"Direct"')).toBeTruthy();
		expect(container).not.toHaveTextContent('ever');
	});

	it('renders a UTM Parameter criterion w/ the parameter it filters on, and w/o a time period', () => {
		const [{fieldName}] = DEFAULT_UTM_PARAMETER_OPTIONS.filter(
			({name}) => name === 'utm_campaign'
		);

		const criterion = {
			operatorName: CustomFunctionOperators.SessionsFilter,
			propertyName: fieldName,
			value: Map({
				criterionGroup: Map({
					items: List([
						Map({
							operatorName: RelationalOperators.EQ,
							propertyName: fieldName,
							value: 'summer-sale'
						})
					])
				})
			})
		};

		const property = new Property({
			entityName: 'Session',
			label: 'UTM Parameter',
			name: 'attribute/utmParameter',
			propertyKey: 'session',
			type: PropertyTypes.SessionUtmParameter
		});

		const {container, getByText} = render(
			<SessionDisplay criterion={criterion} property={property} />
		);

		expect(getByText('UTM Campaign')).toBeTruthy();
		expect(getByText('is')).toBeTruthy();
		expect(getByText('"summer-sale"')).toBeTruthy();
		expect(container).not.toHaveTextContent('ever');
	});

	it('renders w/ a knownType', () => {
		const criterion = {...mockCriterion};

		criterion.value = criterion.value.setIn(
			['criterionGroup', 'items', 0, 'value'],
			null
		);

		const {getByText} = render(
			<SessionDisplay criterion={criterion} property={mockProperty} />
		);

		expect(getByText('is unknown')).toBeTruthy();
	});
});
