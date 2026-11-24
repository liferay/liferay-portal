import InterestBooleanInput from '../InterestBooleanInput';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {createCustomValueMap} from '../../utils/custom-inputs';
import {Property} from 'shared/util/records';
import {RelationalOperators} from '../../utils/constants';

describe('InterestBooleanInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container, getAllByText, getByText} = render(
			<InterestBooleanInput
				property={new Property({entityName: 'Foo Entity'})}
				value={createCustomValueMap([
					{
						key: 'criterionGroup',
						value: [
							{
								operatorName: RelationalOperators.EQ,
								propertyName: 'name',
								value: 'foo interest'
							},
							{
								operatorName: RelationalOperators.EQ,
								propertyName: 'score',
								value: 'true'
							}
						]
					}
				])}
			/>
		);
		fireEvent.click(getByText('is'));

		expect(getAllByText('is')[1]).toBeTruthy();
		expect(getByText('is not')).toBeTruthy();
		expect(container).toMatchSnapshot();
	});
});
