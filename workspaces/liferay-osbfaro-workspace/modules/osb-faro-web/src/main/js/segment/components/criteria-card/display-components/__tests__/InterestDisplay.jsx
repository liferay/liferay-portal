import * as data from 'test/data';
import InterestDisplay from '../InterestDisplay';
import React from 'react';
import {
	CustomFunctionOperators,
	PropertyTypes,
	RelationalOperators
} from 'segment/segment-editor/dynamic/utils/constants';
import {List, Map} from 'immutable';
import {Property} from 'shared/util/records';
import {render} from '@testing-library/react';

describe('InterestDisplay', () => {
	const mockCriterion = {
		operatorName: CustomFunctionOperators.InterestsFilter,
		propertyName: 'name',
		value: Map({
			criterionGroup: Map({
				items: List([
					Map({
						operatorName: RelationalOperators.EQ,
						propertyName: 'name',
						value: 'Tests'
					}),
					Map({
						operatorName: RelationalOperators.EQ,
						propertyName: 'score',
						value: 'true'
					})
				])
			})
		})
	};

	const mockProperty = data.getImmutableMock(Property, data.mockProperty, 1, {
		entityName: 'Individual',
		label: 'name',
		name: 'name',
		propertykey: 'interest',
		type: PropertyTypes.Interest
	});

	it('renders', () => {
		const {container} = render(
			<InterestDisplay
				criterion={mockCriterion}
				property={mockProperty}
			/>
		);

		expect(container).toMatchSnapshot();
	});
});
