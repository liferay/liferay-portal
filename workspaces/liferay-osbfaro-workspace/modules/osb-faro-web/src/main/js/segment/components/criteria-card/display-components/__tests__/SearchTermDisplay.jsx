import * as data from 'test/data';
import React from 'react';
import SearchTermDisplay from '../SearchTermDisplay';
import {
	CustomFunctionOperators,
	PropertyTypes,
	RelationalOperators
} from 'segment/segment-editor/dynamic/utils/constants';
import {List, Map} from 'immutable';
import {Property} from 'shared/util/records';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

describe('SearchTermDisplay', () => {
	const mockCriterion = {
		operatorName: CustomFunctionOperators.SearchTermsFilter,
		propertyName: 'name',
		value: Map({
			criterionGroup: Map({
				items: List([
					Map({
						operatorName: RelationalOperators.EQ,
						propertyName: 'name',
						value: 'shoes'
					}),
					Map({
						operatorName: RelationalOperators.EQ,
						propertyName: 'searching',
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
		propertykey: 'search-term',
		type: PropertyTypes.SearchTerm
	});

	it('renders', () => {
		const {container} = render(
			<SearchTermDisplay
				criterion={mockCriterion}
				property={mockProperty}
			/>
		);

		expect(container).toMatchSnapshot();
	});
});
