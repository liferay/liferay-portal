import * as API from 'shared/api';
import React from 'react';
import UtmParameterInput from '../UtmParameterInput';
import {
	act,
	cleanup,
	fireEvent,
	render,
	waitFor
} from '@testing-library/react';
import {createCustomValueMap} from '../../utils/custom-inputs';
import {Property} from 'shared/util/records';
import {PropertyTypes} from '../../utils/constants';

jest.unmock('react-dom');

// The global `jest.mock('shared/api')` automock omits the `session`
// namespace, so it has to be supplied here for `API.session` to exist.

jest.mock('shared/api', () => ({
	session: {
		fetchAcquisitionParameters: jest.fn(),
		fetchFieldValues: jest.fn()
	}
}));

const mockValue = createCustomValueMap([
	{
		key: 'criterionGroup',
		value: [
			{
				operatorName: 'eq',
				propertyName: 'context/acquisitionSource',
				value: ''
			}
		]
	}
]);

const defaultProps = {
	channelId: 'channel-1',
	displayValue: 'UTM Parameter',
	groupId: '12345',
	onChange: jest.fn(),
	property: new Property({
		entityName: 'Session',
		type: PropertyTypes.SessionUtmParameter
	}),
	touched: {customInput: false},
	valid: {customInput: false},
	value: mockValue
};

const DefaultComponent = props => (
	<UtmParameterInput {...defaultProps} {...props} />
);

/**
 * `DefaultComponent` never applies its own `onChange`, so `value` stays put
 * and the component cannot be observed reacting to it. This one feeds the
 * criterion back in, the way `CriteriaRow` does.
 */

const ControlledComponent = props => {
	const [value, setValue] = React.useState(mockValue);

	return (
		<UtmParameterInput
			{...defaultProps}
			{...props}
			onChange={({value}) => setValue(value)}
			value={value}
		/>
	);
};

describe('UtmParameterInput', () => {
	beforeEach(() => {

		// The default is an empty discovery response, which is what a
		// channel that has captured no acquisition parameter yet returns.
		// Tests that need a populated list override it.

		API.session.fetchAcquisitionParameters.mockResolvedValue({
			items: [],
			total: 0
		});
		API.session.fetchFieldValues.mockResolvedValue({items: [], total: 0});
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	afterEach(cleanup);

	it('should render the entity name, display value, default parameter, operator, and value input', () => {
		const {getByText} = render(<DefaultComponent />);

		expect(getByText('Session')).toBeInTheDocument();
		expect(getByText('UTM Parameter')).toBeInTheDocument();
		expect(getByText('UTM Source')).toBeInTheDocument();
		expect(getByText('is')).toBeInTheDocument();
	});

	it('should offer the full STRING operator set', () => {
		const {getByText} = render(<DefaultComponent />);
		fireEvent.click(getByText('is'));

		expect(getByText('is not')).toBeTruthy();
		expect(getByText('contains')).toBeTruthy();
		expect(getByText('does not contain')).toBeTruthy();
		expect(getByText('is known')).toBeTruthy();
		expect(getByText('is unknown')).toBeTruthy();
	});

	it('should offer every default UTM parameter in the parameter picker', () => {
		const {getByText} = render(<DefaultComponent />);
		fireEvent.click(getByText('UTM Source'));

		expect(getByText('UTM Medium')).toBeTruthy();
		expect(getByText('UTM Campaign')).toBeTruthy();
		expect(getByText('UTM Term')).toBeTruthy();
		expect(getByText('UTM Content')).toBeTruthy();
	});

	it('should switch the underlying attribute and reset the value when a different parameter is selected', () => {
		const onChange = jest.fn();
		const {getByText} = render(<DefaultComponent onChange={onChange} />);

		fireEvent.click(getByText('UTM Source'));
		fireEvent.click(getByText('UTM Medium'));

		const [{value}] = onChange.mock.calls[onChange.mock.calls.length - 1];

		expect(value.getIn(['criterionGroup', 'items', 0, 'propertyName'])).toBe(
			'context/acquisitionMedium'
		);
		expect(value.getIn(['criterionGroup', 'items', 0, 'value'])).toBe('');
	});

	it('should update the operator when a different one is selected', () => {
		const onChange = jest.fn();
		const {getByText} = render(<DefaultComponent onChange={onChange} />);

		fireEvent.click(getByText('is'));
		fireEvent.click(getByText('contains'));

		const [{value}] = onChange.mock.calls[onChange.mock.calls.length - 1];

		expect(
			value.getIn(['criterionGroup', 'items', 0, 'operatorName'])
		).toBe('contains');
	});

	it('should hide the value input when the value is null (is known/is unknown)', () => {
		const {queryByTestId} = render(
			<DefaultComponent
				value={createCustomValueMap([
					{
						key: 'criterionGroup',
						value: [
							{
								operatorName: 'eq',
								propertyName: 'context/acquisitionSource',
								value: null
							}
						]
					}
				])}
			/>
		);

		expect(queryByTestId('attribute-value-string-input')).toBeNull();
	});

	it('should update the value when the value input changes', () => {
		const onChange = jest.fn();
		const {getByTestId} = render(<DefaultComponent onChange={onChange} />);

		fireEvent.change(getByTestId('attribute-value-string-input'), {
			target: {value: 'google'}
		});

		const [{value}] = onChange.mock.calls[onChange.mock.calls.length - 1];

		expect(value.getIn(['criterionGroup', 'items', 0, 'value'])).toBe(
			'google'
		);
	});

	it('should keep the null value, and stay valid, when the parameter changes on an is-unknown criterion', () => {
		const onChange = jest.fn();
		const {getByText} = render(
			<DefaultComponent
				onChange={onChange}
				value={createCustomValueMap([
					{
						key: 'criterionGroup',
						value: [
							{
								operatorName: 'eq',
								propertyName: 'context/acquisitionSource',
								value: null
							}
						]
					}
				])}
			/>
		);

		fireEvent.click(getByText('UTM Source'));
		fireEvent.click(getByText('UTM Medium'));

		const [{valid, value}] =
			onChange.mock.calls[onChange.mock.calls.length - 1];

		expect(value.getIn(['criterionGroup', 'items', 0, 'propertyName'])).toBe(
			'context/acquisitionMedium'
		);

		// A null value is what "is unknown" is read back from, so clearing
		// it would flip the operator to "is".

		expect(
			value.getIn(['criterionGroup', 'items', 0, 'value'])
		).toBeNull();
		expect(valid.customInput).toBe(true);
	});

	describe('acquisition parameter discovery', () => {
		it('should keep the default parameters when the endpoint returns an empty list', async () => {
			const {getByText} = render(<DefaultComponent />);

			await waitFor(() =>
				expect(
					API.session.fetchAcquisitionParameters
				).toHaveBeenCalled()
			);

			expect(getByText('UTM Source')).toBeInTheDocument();

			fireEvent.click(getByText('UTM Source'));

			expect(getByText('UTM Medium')).toBeTruthy();
			expect(getByText('UTM Campaign')).toBeTruthy();
		});

		it('should keep the default parameters when the endpoint omits items', async () => {
			API.session.fetchAcquisitionParameters.mockResolvedValue({});

			const {getByText} = render(<DefaultComponent />);

			await waitFor(() =>
				expect(
					API.session.fetchAcquisitionParameters
				).toHaveBeenCalled()
			);

			// The trigger keeps its label either way, so the dropdown is
			// what shows whether the defaults survived.

			fireEvent.click(getByText('UTM Source'));

			expect(getByText('UTM Medium')).toBeTruthy();
			expect(getByText('UTM Campaign')).toBeTruthy();
		});

		it('should offer a custom parameter returned by the endpoint', async () => {
			API.session.fetchAcquisitionParameters.mockResolvedValue({
				items: [
					{fieldName: 'context/acquisitionSource', name: 'utm_source'},
					{fieldName: 'context/utm_cid', name: 'utm_cid'}
				],
				total: 2
			});

			const {getByText, queryByText} = render(<DefaultComponent />);

			await waitFor(() => expect(getByText('UTM Source')).toBeTruthy());

			fireEvent.click(getByText('UTM Source'));

			// A custom parameter has no translation, so it shows its raw
			// name, and the discovered list replaces the seeded defaults.

			await waitFor(() => expect(getByText('utm_cid')).toBeTruthy());

			expect(queryByText('UTM Campaign')).toBeNull();
		});

		it('should refetch the suggested values when the parameter changes', async () => {
			const {getByText} = render(<ControlledComponent />);

			await waitFor(() =>
				expect(API.session.fetchFieldValues).toHaveBeenCalled()
			);

			const fieldNames = () =>
				API.session.fetchFieldValues.mock.calls.map(
					([{fieldName}]) => fieldName
				);

			expect(fieldNames()).toContain('context/acquisitionSource');

			fireEvent.click(getByText('UTM Source'));
			fireEvent.click(getByText('UTM Medium'));

			// The typed query is unchanged, so the field name is the only
			// thing that can invalidate the suggestions.

			await waitFor(() =>
				expect(fieldNames()).toContain('context/acquisitionMedium')
			);
		});

		it('should not set state when the response resolves after unmount', async () => {
			let resolveFetch;

			API.session.fetchAcquisitionParameters.mockReturnValue(
				new Promise(resolve => {
					resolveFetch = resolve;
				})
			);

			let instance;

			// React 18 dropped the unmounted-setState warning, so the guard
			// is observable only on the instance itself.

			const {unmount} = render(
				<UtmParameterInput
					{...defaultProps}
					ref={value => {
						instance = value || instance;
					}}
				/>
			);

			const setState = jest.spyOn(instance, 'setState');

			unmount();

			await act(async () => {
				resolveFetch({
					items: [
						{fieldName: 'context/utm_cid', name: 'utm_cid'}
					],
					total: 1
				});
			});

			expect(setState).not.toHaveBeenCalled();
		});
	});
});
