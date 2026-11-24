import BetweenNumberInput from '../BetweenNumberInput';
import React from 'react';
import {fireEvent, render} from '@testing-library/react';

describe('BetweenNumberInput', () => {
	it('should render', () => {
		const {container} = render(
			<BetweenNumberInput
				onChange={jest.fn()}
				value={{end: 123, start: 1}}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	fit('should render with error', () => {
		const {container, queryByTestId} = render(
			<BetweenNumberInput
				onChange={jest.fn()}
				value={{end: 12, start: 1}}
			/>
		);

		const startInput = queryByTestId('between-number-start-input');

		fireEvent.change(startInput, {
			target: {value: ''}
		});

		jest.runAllTimers();

		expect(container.querySelector('.has-error')).toBeTruthy();
	});
});
