import OperatorSelect from '../OperatorSelect';
import React from 'react';
import {ATTRIBUTES_NUMBER_OPERATOR_LONGHAND_LABELS_MAP} from '../utils';
import {DataTypes, Operators} from 'event-analysis/utils/types';
import {fireEvent, render} from '@testing-library/react';

jest.unmock('react-dom');

describe('OperatorSelect', () => {
	it('should render', () => {
		const {container, getByText} = render(
			<OperatorSelect
				dataType={DataTypes.Number}
				onChange={jest.fn()}
				operatorsName={
					ATTRIBUTES_NUMBER_OPERATOR_LONGHAND_LABELS_MAP[Operators.EQ]
				}
			/>
		);
		fireEvent.click(getByText('Select an option'));

		expect(getByText('greater than')).toBeTruthy();
		expect(getByText('less than')).toBeTruthy();
		expect(getByText('is equal to')).toBeTruthy();
		expect(getByText('is not equal to')).toBeTruthy();

		expect(container).toMatchSnapshot();
	});

	it('should apply the form-control-sm class when small is true', () => {
		const {container} = render(
			<OperatorSelect
				dataType={DataTypes.Number}
				onChange={jest.fn()}
				operatorsName={
					ATTRIBUTES_NUMBER_OPERATOR_LONGHAND_LABELS_MAP[Operators.EQ]
				}
				small
			/>
		);

		expect(
			container.querySelector('.operator-input.form-control-sm')
		).toBeTruthy();
	});

	it('should not apply the form-control-sm class when small is not set', () => {
		const {container} = render(
			<OperatorSelect
				dataType={DataTypes.Number}
				onChange={jest.fn()}
				operatorsName={
					ATTRIBUTES_NUMBER_OPERATOR_LONGHAND_LABELS_MAP[Operators.EQ]
				}
			/>
		);

		expect(
			container.querySelector('.form-control-sm')
		).toBeNull();
	});
});
