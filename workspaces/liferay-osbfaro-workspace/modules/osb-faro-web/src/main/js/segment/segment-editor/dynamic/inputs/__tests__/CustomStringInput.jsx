import CustomStringInput from '../CustomStringInput';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {
	createCustomValueMap,
	setPropertyValue
} from '../../utils/custom-inputs';
import {Property} from 'shared/util/records';
import {PropertyTypes} from '../../utils/constants';

jest.unmock('react-dom');

const mockValue = createCustomValueMap([
	{key: 'criterionGroup', value: [{operatorName: 'eq', value: ''}]}
]);

const defaultProps = {
	displayValue: 'Address',
	operatorRenderer: () => <div>{'operator'}</div>,
	property: new Property(),
	touched: false,
	valid: true,
	value: mockValue
};

const DefaultComponent = props => (
	<CustomStringInput {...defaultProps} {...props} />
);

describe('CustomStringInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container, getAllByText, getByText} = render(
			<DefaultComponent valid={false} />
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

	it('should render with data', () => {
		const {container, getAllByText, getByText} = render(
			<DefaultComponent
				value={setPropertyValue(mockValue, 'value', 0, '123 West Road')}
			/>
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

	it('should render w/o value input when value is null', () => {
		const {queryByTestId} = render(
			<DefaultComponent
				value={setPropertyValue(mockValue, 'value', 0, null)}
			/>
		);

		expect(queryByTestId('attribute-value-string-input')).toBeNull();
	});

	it('should render w/ has-error when touched and not valid', () => {
		const {container} = render(<DefaultComponent touched valid={false} />);

		expect(container.querySelector('.has-error')).toBeTruthy();
	});

	it('should render as a regular input if autocomplete is false', () => {
		const {container} = render(<DefaultComponent autocomplete={false} />);

		expect(container.querySelector('.base-select-input-root')).toBeNull();
	});

	describe('when the property is a Channel (fixed options, is/is not only)', () => {
		const channelProperty = new Property({
			options: [
				{label: 'Direct', value: 'Direct'},
				{label: 'Organic', value: 'Organic'}
			],
			type: PropertyTypes.SessionChannel
		});

		it('should only offer is/is not operators', () => {
			const {getByText, queryByText} = render(
				<DefaultComponent property={channelProperty} />
			);
			fireEvent.click(getByText('is'));

			expect(getByText('is not')).toBeTruthy();
			expect(queryByText('contains')).toBeNull();
			expect(queryByText('does not contain')).toBeNull();
			expect(queryByText('is known')).toBeNull();
			expect(queryByText('is unknown')).toBeNull();
		});

		it('should render a fixed options Picker instead of a free text input', () => {
			const {getByText, queryByTestId} = render(
				<DefaultComponent
					property={channelProperty}
					value={setPropertyValue(mockValue, 'value', 0, 'Direct')}
				/>
			);

			expect(
				queryByTestId('attribute-value-string-input')
			).toBeNull();

			fireEvent.click(getByText('Direct'));

			expect(getByText('Organic')).toBeTruthy();
		});
	});
});
