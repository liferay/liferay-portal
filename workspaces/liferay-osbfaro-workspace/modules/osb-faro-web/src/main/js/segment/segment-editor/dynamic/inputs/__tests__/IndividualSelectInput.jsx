import IndividualSelectInput from '../IndividualSelectInput';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {Property} from 'shared/util/records';
import {Provider} from 'react-redux';

const defaultProps = {
	displayValue: 'fooProperty',
	operatorRenderer: () => <div>{'test'}</div>,
	property: new Property(),
	touched: false,
	valid: false,
	value: '123'
};

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<IndividualSelectInput {...defaultProps} {...props} />
	</Provider>
);

describe('IndividualSelectInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});
});
