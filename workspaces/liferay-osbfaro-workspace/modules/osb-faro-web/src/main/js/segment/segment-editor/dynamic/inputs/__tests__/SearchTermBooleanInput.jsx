import React from 'react';
import SearchTermBooleanInput from '../SearchTermBooleanInput';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {createCustomValueMap} from '../../utils/custom-inputs';
import {Property} from 'shared/util/records';
import {RelationalOperators} from '../../utils/constants';

jest.unmock('react-dom');

describe('SearchTermBooleanInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container, getAllByText, getByText} = render(
			<SearchTermBooleanInput
				property={new Property({entityName: 'Foo Entity'})}
				value={createCustomValueMap([
					{
						key: 'criterionGroup',
						value: [
							{
								operatorName: RelationalOperators.EQ,
								propertyName: 'name',
								value: 'shoes'
							},
							{
								operatorName: RelationalOperators.EQ,
								propertyName: 'searching',
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
