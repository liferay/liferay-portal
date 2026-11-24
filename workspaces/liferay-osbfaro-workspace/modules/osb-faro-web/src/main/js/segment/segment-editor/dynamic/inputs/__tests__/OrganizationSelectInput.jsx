import mockStore from 'test/mock-store';
import OrganizationSelectInput from '../OrganizationSelectInput';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {createCustomValueMap} from '../../utils/custom-inputs';
import {Property} from 'shared/util/records';
import {Provider} from 'react-redux';

const mockValue = createCustomValueMap([
	{key: 'criterionGroup', value: [{operatorName: 'eq', value: ''}]}
]);

const defaultProps = {
	displayValue: 'fooProperty',
	operatorRenderer: () => <div>{'test'}</div>,
	property: new Property(),
	touched: false,
	valid: false,
	value: mockValue
};

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<OrganizationSelectInput {...defaultProps} {...props} />
	</Provider>
);

describe('OrganizationSelectInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});
});
