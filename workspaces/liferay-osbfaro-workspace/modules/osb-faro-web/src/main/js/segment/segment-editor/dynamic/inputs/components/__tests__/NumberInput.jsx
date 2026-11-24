import NumberInput from '../NumberInput';
import React from 'react';
import {FunctionalOperators} from '../../../utils/constants';
import {render} from '@testing-library/react';

describe('NumberInput', () => {
	it('should render', () => {
		const {container} = render(
			<NumberInput
				onChange={jest.fn()}
				touched={false}
				valid
				value={123}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render with error', () => {
		const {container} = render(
			<NumberInput onChange={jest.fn()} touched valid={false} value='' />
		);

		expect(container.querySelector('.has-error')).toBeTruthy();
	});

	it('should render with BetweenNumberInput', () => {
		const {queryByTestId} = render(
			<NumberInput
				onChange={jest.fn()}
				operatorName={FunctionalOperators.Between}
				touched
				valid={false}
				value={{end: 20, start: 1}}
			/>
		);

		expect(queryByTestId('between-number-start-input')).toBeTruthy();
		expect(queryByTestId('between-number-end-input')).toBeTruthy();
	});
});
