import * as API from 'shared/api';
import AccountSelectInput from '../AccountSelectInput';
import React from 'react';
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

		expect(getByText('Account')).toBeInTheDocument();
		expect(getAllByText('Lifecycle')[0]).toBeInTheDocument();
		expect(getByText('is')).toBeInTheDocument();
	});

	it('should match the selected lifecycle at any stage', async () => {
		const onChange = jest.fn();

		const {getAllByRole, getByLabelText} = render(
			<DefaultComponent onChange={onChange} />
		);

		await waitFor(() =>
			expect(getByLabelText('Lifecycle')).not.toBeDisabled()
		);

		fireEvent.click(getByLabelText('Lifecycle'));

		const options = getAllByRole('option');

		expect(options[0]).toHaveTextContent('Customer');
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
			expect(getByLabelText('Lifecycle')).not.toBeDisabled()
		);

		expect(getByText('Add Stage').closest('button')).toBeDisabled();
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

		await findByText('Customer');

		fireEvent.click(getByText('Add Stage'));

		const items = getAllByRole('menuitem');

		expect(items).toHaveLength(2);
		expect(items[0]).toHaveTextContent('Aware');
		expect(items[1]).toHaveTextContent('Engaged');

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

		await findByText('Engaged');

		fireEvent.click(getByText('Add Stage'));

		const items = getAllByRole('menuitem');

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

		await findByText('Engaged');

		fireEvent.click(getByLabelText('Close'));

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

		await findByText('Engaged');

		expect(getByText('Aware')).toBeInTheDocument();

		fireEvent.click(getByLabelText('Clear'));

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

		await findByText('Customer');

		fireEvent.click(getByText('Customer'));
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

		await findByText('Customer');

		fireEvent.click(getByText('Customer'));
		fireEvent.click(getAllByRole('option')[0]);

		expect(onChange).not.toHaveBeenCalled();
		expect(getByText('Engaged')).toBeInTheDocument();
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
			expect(getByLabelText('Lifecycle')).not.toBeDisabled()
		);

		expect(queryByText('erc-stage-3001')).toBeNull();
		expect(queryByLabelText('Clear')).toBeNull();
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

		await findByText('Undefined Stage');

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
