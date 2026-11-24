import NumberInput from '../NumberInput';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {Property} from 'shared/util/records';

describe('NumberInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<NumberInput
				operatorRenderer={() => <div>{'operator'}</div>}
				property={new Property()}
				touched={false}
				valid={false}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render with data', () => {
		const {container} = render(
			<NumberInput
				displayValue='Name'
				operatorRenderer={() => <div>{'operator'}</div>}
				property={new Property()}
				touched={false}
				valid
				value='Test Test'
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render w/o value input when value is null', () => {
		const {queryByTestId} = render(
			<NumberInput
				displayValue='Name'
				operatorRenderer={() => <div>{'operator'}</div>}
				property={new Property()}
				touched={false}
				valid
				value={null}
			/>
		);

		expect(queryByTestId('number-input')).toBeNull();
	});

	it('should render w/ has-error when touched and not valid', () => {
		const {container} = render(
			<NumberInput
				displayValue='Name'
				operatorRenderer={() => <div>{'operator'}</div>}
				property={new Property()}
				touched
				valid={false}
				value=''
			/>
		);

		expect(container.querySelector('.has-error')).toBeTruthy();
	});
});
