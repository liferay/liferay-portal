import React from 'react';
import StageConditionRow from '../StageConditionRow';
import {createStageCondition} from 'lifecycle/utils/stageConfiguration';
import {fireEvent, render, screen} from '@testing-library/react';
import {ICatalogField} from 'shared/api/catalog';
import {IStageCondition} from 'lifecycle/utils/stageConfiguration';

jest.unmock('react-dom');

const mockFields: ICatalogField[] = [
	{
		dataCategory: 'Text',
		dataType: 'STRING',
		description: '',
		displayName: 'Industry',
		id: 'account.industry',
		name: 'account.industry',
		parentField: null,
		tableName: 'account',
	},
	{
		dataCategory: 'Number',
		dataType: 'NUMERIC',
		description: '',
		displayName: 'Annual Revenue',
		id: 'account.annualRevenue',
		name: 'account.annualRevenue',
		parentField: null,
		tableName: 'account',
	},
];

const buildCondition = (
	condition: Partial<IStageCondition> = {}
): IStageCondition => ({...createStageCondition(), ...condition});

const renderRow = (
	props: Partial<React.ComponentProps<typeof StageConditionRow>> = {}
) =>
	render(
		<StageConditionRow
			condition={buildCondition()}
			fields={mockFields}
			onChange={jest.fn()}
			{...props}
		/>
	);

describe('StageConditionRow', () => {
	it('renders the account label and the attribute picker', () => {
		renderRow();

		expect(screen.getByText('Account')).toBeInTheDocument();
		expect(screen.getByText('Select Attribute')).toBeInTheDocument();
	});

	it('patches only the condition it was given when an attribute is picked', () => {
		const onChange = jest.fn();

		renderRow({onChange});

		fireEvent.click(screen.getByText('Select Attribute'));
		fireEvent.click(screen.getByText('Industry'));

		expect(onChange).toHaveBeenCalledWith({
			conditionValue: null,
			field: 'account.industry',
			fieldDataCategory: 'Text',
			fieldDataType: 'STRING',
			operator: null,
		});
	});

	it('reveals the operator picker once an attribute is chosen', () => {
		renderRow({condition: buildCondition({field: 'account.industry'})});

		expect(screen.getByText('Select Operator')).toBeInTheDocument();
	});

	it('patches the value when the value input changes', () => {
		const onChange = jest.fn();

		renderRow({
			condition: buildCondition({
				field: 'account.industry',
				fieldDataCategory: 'Text',
				fieldDataType: 'STRING',
				operator: 'contains',
			}),
			onChange,
		});

		fireEvent.change(screen.getByLabelText('Value'), {
			target: {value: 'Retail'},
		});

		expect(onChange).toHaveBeenCalledWith({conditionValue: 'Retail'});
	});

	it('offers no remove control when it cannot be removed', () => {
		renderRow();

		expect(screen.queryByLabelText(/remove/i)).toBeNull();
	});

	it('calls onRemove when the remove control is used', () => {
		const onRemove = jest.fn();

		renderRow({onRemove});

		fireEvent.click(screen.getByLabelText(/remove/i));

		expect(onRemove).toHaveBeenCalled();
	});

	it('flags a condition left unfinished once an attribute is chosen', () => {
		const {container} = renderRow({
			condition: buildCondition({field: 'account.industry'}),
		});

		expect(container.querySelector('.has-error')).toBeInTheDocument();
	});

	it('leaves a condition not yet started unflagged', () => {
		const {container} = renderRow();

		expect(container.querySelector('.has-error')).toBeNull();
	});
});
