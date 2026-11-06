import * as data from 'test/data';
import DisplayComponent from '../index';
import React from 'react';
import {List, Map} from 'immutable';
import {Property} from 'shared/util/records';
import {RelationalOperators} from 'segment/segment-editor/dynamic/utils/constants';
import {render} from '@testing-library/react';
import {SegmentTypes} from 'shared/util/constants';

jest.unmock('react-dom');

describe('DisplayComponent', () => {
	it.each`
		propertyKey     | entityName
		${'account'}    | ${'Account'}
		${'session'}    | ${'Session'}
		${'interest'}   | ${'Internet'}
		${'individual'} | ${'Individual'}
	`('renders $displayName for $propertyKey', ({entityName, propertyKey}) => {
		const mockCriterion = {
			operatorName: 'test-operator',
			propertyName: 'foo/bar',
			value: Map({
				criterionGroup: Map({
					items: List([
						Map({
							operatorName: RelationalOperators.EQ,
							propertyName: 'foo/bar',
							value: 'this is a description'
						}),
						Map({
							operatorName: RelationalOperators.EQ,
							propertyName: 'score',
							value: 'true'
						})
					])
				}),
				operator: RelationalOperators.GE,
				value: 32
			})
		};

		const mockProperty = data.getImmutableMock(
			Property,
			data.mockProperty,
			1,
			{
				entityName,
				label: 'description',
				name: 'foo/bar',
				propertyKey,
				type: 'text'
			}
		);

		const {container, getByText} = render(
			<DisplayComponent
				criterion={mockCriterion}
				property={mockProperty}
				segmentType={SegmentTypes.Batch}
			/>
		);

		expect(getByText(entityName)).toBeTruthy();
		expect(container).toMatchSnapshot();
	});
});
