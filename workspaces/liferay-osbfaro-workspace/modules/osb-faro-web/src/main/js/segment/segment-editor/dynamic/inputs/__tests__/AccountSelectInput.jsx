import * as API from 'shared/api';
import AccountSelectInput from '../AccountSelectInput';
import React, {useState} from 'react';
import {cleanup, fireEvent, render, waitFor} from '@testing-library/react';
import {createCustomValueMap} from '../../utils/custom-inputs';
import {Property} from 'shared/util/records';
import {PropertyTypes} from '../../utils/constants';

jest.unmock('react-dom');

const getMockValue = (propertyName, operatorName, value) =>
	createCustomValueMap([
		{key: 'criterionGroup', value: [{operatorName, propertyName, value}]}
	]);

const defaultProps = {
	displayValue: 'Lifecycle',
	groupId: '123',
	onChange: jest.fn(),
	operatorRenderer: () => <div>{'is'}</div>,
	property: new Property({
		entityName: 'Account',
		label: 'Lifecycle',
		name: 'lifecycleStatus',
		type: PropertyTypes.AccountSelectText
	}),
	value: getMockValue('lifecycleStatus', 'eq', '')
};

const DefaultComponent = props => (
	<AccountSelectInput {...defaultProps} {...props} />
);

const StatefulComponent = ({value: initialValue}) => {
	const [value, setValue] = useState(initialValue);

	return (
		<DefaultComponent
			onChange={({value}) => setValue(value)}
			value={value}
		/>
	);
};

const getItems = (onChange, call = 0) =>
	onChange.mock.calls[call][0].value
		.getIn(['criterionGroup', 'items'])
		.toJS();

describe('AccountSelectInput', () => {
	afterEach(cleanup);

	beforeEach(() => {
		API.lifecycle.fetchChannelLifecycles.mockReturnValue(
			Promise.resolve([
				{
					externalReferenceCode: 'erc-lifecycle-100',
					id: '100',
					name: 'Customer',
					accountLifecycleStages: [
						{
							externalReferenceCode: 'erc-stage-1002',
							id: '1002',
							name: 'Engaged',
							position: 2
						},
						{
							externalReferenceCode: 'erc-stage-1001',
							id: '1001',
							name: 'Aware',
							position: 1
						}
					]
				},
				{
					externalReferenceCode: 'erc-lifecycle-200',
					id: '200',
					name: 'Partner',
					accountLifecycleStages: [
						{
							externalReferenceCode: 'erc-stage-2001',
							id: '2001',
							name: 'Recruited',
							position: 1
						}
					]
				}
			])
		);
	});

	it('should render the entity, the property and the operator of the criterion', () => {
		const {getAllByText, getByText} = render(<DefaultComponent />);

		expect(getByText(/^account$/i)).toBeInTheDocument();
		expect(getAllByText(/^lifecycle$/i)[0]).toBeInTheDocument();
		expect(getByText('is')).toBeInTheDocument();
	});

	it('should match the selected lifecycle at any stage', async () => {
		const onChange = jest.fn();

		const {getAllByRole, getByLabelText} = render(
			<DefaultComponent onChange={onChange} />
		);

		await waitFor(() =>
			expect(getByLabelText(/^lifecycle$/i)).not.toBeDisabled()
		);

		fireEvent.click(getByLabelText(/^lifecycle$/i));

		const options = getAllByRole('option');

		expect(options[0]).toHaveTextContent(/customer/i);
		expect(options[1]).toHaveTextContent('Partner');

		fireEvent.click(options[1]);

		expect(getItems(onChange)).toEqual([
			{
				operatorName: 'ne',
				propertyName: 'lifecycleStatus@erc-lifecycle-200',
				value: null
			}
		]);
		expect(onChange.mock.calls[0][0].valid).toBe(true);
	});

	it('should disable the stages until a lifecycle is selected', async () => {
		const {getByLabelText, getByText} = render(<DefaultComponent />);

		await waitFor(() =>
			expect(getByLabelText(/^lifecycle$/i)).not.toBeDisabled()
		);

		expect(getByText(/^add stage$/i).closest('button')).toBeDisabled();
	});

	it('should list the stages of the selected lifecycle sorted by display order', async () => {
		const onChange = jest.fn();

		const {findByText, getAllByRole, getByText} = render(
			<DefaultComponent
				onChange={onChange}
				value={getMockValue(
					'lifecycleStatus@erc-lifecycle-100',
					'ne',
					null
				)}
			/>
		);

		await findByText(/^customer$/i);

		fireEvent.click(getByText(/^add stage$/i));

		const items = getAllByRole('option');

		expect(items).toHaveLength(2);
		expect(items[0]).toHaveTextContent(/aware/i);
		expect(items[1]).toHaveTextContent(/engaged/i);

		fireEvent.click(items[1]);

		expect(getItems(onChange)).toEqual([
			{
				operatorName: 'eq',
				propertyName: 'lifecycleStatus@erc-lifecycle-100',
				value: 'erc-stage-1002'
			}
		]);
	});

	it('should add a stage to the selected stages', async () => {
		const onChange = jest.fn();

		const {findByText, getAllByRole, getByRole} = render(
			<DefaultComponent
				onChange={onChange}
				value={getMockValue(
					'lifecycleStatus@erc-lifecycle-100',
					'eq',
					'erc-stage-1002'
				)}
			/>
		);

		await findByText(/^engaged$/i);

		fireEvent.focus(getByRole('combobox', {name: /^stages$/i}));

		const items = getAllByRole('option');

		expect(items).toHaveLength(1);

		fireEvent.click(items[0]);

		expect(getItems(onChange)).toEqual([
			{
				conjunctionName: 'or',
				items: [
					{
						operatorName: 'eq',
						propertyName: 'lifecycleStatus@erc-lifecycle-100',
						value: 'erc-stage-1002'
					},
					{
						operatorName: 'eq',
						propertyName: 'lifecycleStatus@erc-lifecycle-100',
						value: 'erc-stage-1001'
					}
				]
			}
		]);
	});

	it('should show the stages input instead of the Add Stage button when clicking it', async () => {
		const {findByText, getByRole, getByText, queryByRole, queryByText} =
			render(
				<DefaultComponent
					value={getMockValue(
						'lifecycleStatus@erc-lifecycle-100',
						'ne',
						null
					)}
				/>
			);

		await findByText(/^customer$/i);

		expect(queryByRole('combobox', {name: /^stages$/i})).toBeNull();

		fireEvent.click(getByText(/^add stage$/i));

		expect(getByRole('combobox', {name: /^stages$/i})).toHaveFocus();
		expect(queryByText(/^add stage$/i)).toBeNull();
	});

	it('should show the Add Stage button when closing the stages input without selecting a stage', async () => {
		const {findByText, getByRole, getByText, queryByRole} = render(
			<DefaultComponent
				value={getMockValue(
					'lifecycleStatus@erc-lifecycle-100',
					'ne',
					null
				)}
			/>
		);

		await findByText(/^customer$/i);

		fireEvent.click(getByText(/^add stage$/i));

		fireEvent.keyDown(getByRole('combobox', {name: /^stages$/i}), {
			key: 'Escape'
		});

		expect(queryByRole('combobox', {name: /^stages$/i})).toBeNull();
		expect(getByText(/^add stage$/i)).toBeInTheDocument();
	});

	it('should keep the stages input after selecting the first stage', async () => {
		const {findByText, getByRole, getByText} = render(
			<StatefulComponent
				value={getMockValue(
					'lifecycleStatus@erc-lifecycle-100',
					'ne',
					null
				)}
			/>
		);

		await findByText(/^customer$/i);

		fireEvent.click(getByText(/^add stage$/i));
		fireEvent.click(getByRole('option', {name: /^aware$/i}));

		expect(getByRole('combobox', {name: /^stages$/i})).toBeInTheDocument();
		expect(getByText(/^aware$/i)).toBeInTheDocument();
	});

	it('should show the Add Stage button after removing every stage', async () => {
		const {findByText, getByLabelText, getByText, queryByRole} = render(
			<StatefulComponent
				value={getMockValue(
					'lifecycleStatus@erc-lifecycle-100',
					'eq',
					'erc-stage-1002'
				)}
			/>
		);

		await findByText(/^engaged$/i);

		fireEvent.click(getByLabelText('Remove Engaged'));

		expect(queryByRole('combobox', {name: /^stages$/i})).toBeNull();
		expect(getByText(/^add stage$/i)).toBeInTheDocument();
	});

	it('should show the Add Stage button after clearing every stage', async () => {
		const {findByText, getByLabelText, getByText, queryByRole} = render(
			<StatefulComponent
				value={getMockValue(
					'lifecycleStatus@erc-lifecycle-100',
					'eq',
					'erc-stage-1002'
				)}
			/>
		);

		await findByText(/^engaged$/i);

		fireEvent.click(getByLabelText(/^clear all$/i));

		expect(queryByRole('combobox', {name: /^stages$/i})).toBeNull();
		expect(getByText(/^add stage$/i)).toBeInTheDocument();
	});

	it('should show the Add Stage button after changing the lifecycle while adding stages', async () => {
		const {findByText, getByLabelText, getByRole, getByText, queryByRole} =
			render(
				<StatefulComponent
					value={getMockValue(
						'lifecycleStatus@erc-lifecycle-100',
						'ne',
						null
					)}
				/>
			);

		await findByText(/^customer$/i);

		fireEvent.click(getByText(/^add stage$/i));
		fireEvent.click(getByLabelText(/^lifecycle$/i));
		fireEvent.click(getByRole('option', {name: 'Partner'}));

		expect(queryByRole('combobox', {name: /^stages$/i})).toBeNull();
		expect(getByText(/^add stage$/i)).toBeInTheDocument();
	});

	it('should match the lifecycle at any stage after removing every stage', async () => {
		const onChange = jest.fn();

		const {findByText, getByLabelText} = render(
			<DefaultComponent
				onChange={onChange}
				value={getMockValue(
					'lifecycleStatus@erc-lifecycle-100',
					'eq',
					'erc-stage-1002'
				)}
			/>
		);

		await findByText(/^engaged$/i);

		fireEvent.click(getByLabelText('Remove Engaged'));

		expect(getItems(onChange)).toEqual([
			{
				operatorName: 'ne',
				propertyName: 'lifecycleStatus@erc-lifecycle-100',
				value: null
			}
		]);
	});

	it('should match the lifecycle at any stage after clearing the stages', async () => {
		const onChange = jest.fn();

		const {findByText, getByLabelText, getByText} = render(
			<DefaultComponent
				onChange={onChange}
				value={createCustomValueMap([
					{
						key: 'criterionGroup',
						value: [
							{
								conjunctionName: 'or',
								items: [
									{
										operatorName: 'eq',
										propertyName:
											'lifecycleStatus@erc-lifecycle-100',
										value: 'erc-stage-1001'
									},
									{
										operatorName: 'eq',
										propertyName:
											'lifecycleStatus@erc-lifecycle-100',
										value: 'erc-stage-1002'
									}
								]
							}
						]
					}
				])}
			/>
		);

		await findByText(/^engaged$/i);

		expect(getByText(/^aware$/i)).toBeInTheDocument();

		fireEvent.click(getByLabelText(/^clear all$/i));

		expect(getItems(onChange)).toEqual([
			{
				operatorName: 'ne',
				propertyName: 'lifecycleStatus@erc-lifecycle-100',
				value: null
			}
		]);
	});

	it('should clear the stages when the lifecycle changes', async () => {
		const onChange = jest.fn();

		const {findByText, getAllByRole, getByText} = render(
			<DefaultComponent
				onChange={onChange}
				value={getMockValue(
					'lifecycleStatus@erc-lifecycle-100',
					'eq',
					'erc-stage-1002'
				)}
			/>
		);

		await findByText(/^customer$/i);

		fireEvent.click(getByText(/^customer$/i));
		fireEvent.click(getAllByRole('option')[1]);

		expect(getItems(onChange)).toEqual([
			{
				operatorName: 'ne',
				propertyName: 'lifecycleStatus@erc-lifecycle-200',
				value: null
			}
		]);
	});

	it('should keep the stages when selecting the same lifecycle', async () => {
		const onChange = jest.fn();

		const {findByText, getAllByRole, getByText} = render(
			<DefaultComponent
				onChange={onChange}
				value={getMockValue(
					'lifecycleStatus@erc-lifecycle-100',
					'eq',
					'erc-stage-1002'
				)}
			/>
		);

		await findByText(/^customer$/i);

		fireEvent.click(getByText(/^customer$/i));
		fireEvent.click(getAllByRole('option')[0]);

		expect(onChange).not.toHaveBeenCalled();
		expect(getByText(/^engaged$/i)).toBeInTheDocument();
	});

	it('should not show the stages of a lifecycle that does not exist', async () => {
		const {getByLabelText, queryByLabelText, queryByText} = render(
			<DefaultComponent
				value={getMockValue(
					'lifecycleStatus@erc-lifecycle-300',
					'eq',
					'erc-stage-3001'
				)}
			/>
		);

		await waitFor(() =>
			expect(getByLabelText(/^lifecycle$/i)).not.toBeDisabled()
		);

		expect(queryByText('erc-stage-3001')).toBeNull();
		expect(queryByLabelText(/^clear all$/i)).toBeNull();
	});

	it('should show a stage that does not exist as undefined', async () => {
		const {findByText, queryByText} = render(
			<DefaultComponent
				value={getMockValue(
					'lifecycleStatus@erc-lifecycle-100',
					'eq',
					'deleted-stage'
				)}
			/>
		);

		await findByText(/^undefined stage$/i);

		expect(queryByText('deleted-stage')).toBeNull();
	});

	it('should select the lifecycle of a stage saved without its lifecycle', async () => {
		const {findByText, getByText} = render(
			<DefaultComponent
				value={getMockValue('lifecycleStatus', 'eq', 'erc-stage-2001')}
			/>
		);

		await findByText('Partner');

		expect(getByText('Recruited')).toBeInTheDocument();
	});
});
