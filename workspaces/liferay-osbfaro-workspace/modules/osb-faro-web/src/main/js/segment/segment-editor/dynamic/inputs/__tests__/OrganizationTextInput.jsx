import OrganizationTextInput from '../OrganizationTextInput';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {createCustomValueMap} from '../../utils/custom-inputs';
import {Property} from 'shared/util/records';

const mockValue = createCustomValueMap([
	{key: 'criterionGroup', value: [{operatorName: 'eq', value: ''}]}
]);

const defaultProps = {
	displayValue: 'Address',
	operatorRenderer: () => <div>{'operator'}</div>,
	property: new Property(),
	touched: false,
	valid: false,
	value: mockValue
};

describe('OrganizationTextInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container, getAllByText, getByText} = render(
			<OrganizationTextInput {...defaultProps} />
		);

		fireEvent.click(getByText('is'));

		expect(getAllByText('is')[1]).toBeTruthy();
		expect(getByText('is not')).toBeTruthy();
		expect(getByText('contains')).toBeTruthy();
		expect(getByText('does not contain')).toBeTruthy();
		expect(getByText('is known')).toBeTruthy();
		expect(getByText('is unknown')).toBeTruthy();
		expect(container).toMatchSnapshot();
	});
});
