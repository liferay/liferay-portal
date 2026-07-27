import React from 'react';
import StageConfigurationPanel from '../StageConfigurationPanel';
import {
	DEFAULT_MAX_DAYS,
	IStageConfig,
} from 'lifecycle/utils/stageConfiguration';
import {ICatalogField} from 'shared/api/catalog';
import {fireEvent, render, screen} from '@testing-library/react';
import {LifecycleStages} from 'contacts/pages/account/utils/constants';

jest.mock('shared/util/date', () => ({
	...jest.requireActual('shared/util/date'),
	getDateNow: () => jest.requireActual('moment').utc('2026-03-01'),
}));

jest.unmock('react-dom');

const baseValue: IStageConfig = {
	conditionValue: null,
	description: '',
	field: null,
	fieldDataCategory: null,
	fieldDataType: null,
	id: null,
	maxTimeDays: DEFAULT_MAX_DAYS,
	maxTimeEnabled: true,
	operator: null,
};

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
	{
		dataCategory: 'Boolean',
		dataType: 'BOOLEAN',
		description: '',
		displayName: 'Has Active Pipeline',
		id: 'account.hasActivePipeline',
		name: 'account.hasActivePipeline',
		parentField: null,
		tableName: 'account',
	},
];

const renderPanel = (
	props: Partial<React.ComponentProps<typeof StageConfigurationPanel>> = {}
) =>
	render(
		<StageConfigurationPanel
			defaultExpanded
			fields={mockFields}
			index={1}
			onChange={jest.fn()}
			stageType={LifecycleStages.AWARE}
			value={baseValue}
			{...props}
		/>
	);

describe('StageConfigurationPanel', () => {
	it('shows "Unconfigured" until the condition value is selected', () => {
		renderPanel();

		expect(screen.getByText('Unconfigured')).toBeInTheDocument();
		expect(screen.queryByText('Configured')).toBeNull();
	});

	it('shows "Configured" once the condition value is selected', () => {
		renderPanel({
			value: {
				...baseValue,
				conditionValue: 'true',
				description: 'Cold accounts',
				field: 'HasActivePipeline',
				operator: 'is',
			},
		});

		expect(screen.getByText('Configured')).toBeInTheDocument();
	});

	it('renders the stage name and trigger', () => {
		renderPanel();

		expect(screen.getByText('Aware')).toBeInTheDocument();
		expect(screen.getByText('Trigger')).toBeInTheDocument();
	});

	it('renders the stage description input and persists typed text', () => {
		const onChange = jest.fn();

		renderPanel({onChange});

		const descriptionInput = screen.getByLabelText('Description');

		expect(descriptionInput).toBeInTheDocument();

		fireEvent.change(descriptionInput, {
			target: {value: 'Cold accounts'},
		});

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({description: 'Cold accounts'})
		);
	});

	it('renders the Account entity label and the field picker', () => {
		renderPanel();

		expect(screen.getByText('Account')).toBeInTheDocument();
		expect(screen.queryByText('Select Entity')).toBeNull();
		expect(screen.getByText('Select Field')).toBeInTheDocument();
		expect(screen.getByRole('spinbutton')).toHaveValue(DEFAULT_MAX_DAYS);
	});

	it('does not cap the max-time value at the default number of days', () => {
		const onChange = jest.fn();

		renderPanel({onChange});

		fireEvent.change(screen.getByRole('spinbutton'), {
			target: {value: '120'},
		});

		expect(onChange).toHaveBeenCalledWith({
			...baseValue,
			maxTimeDays: 120,
		});
	});

	it('does not set an upper limit on the max-time input', () => {
		renderPanel();

		expect(screen.getByRole('spinbutton')).not.toHaveAttribute('max');
	});

	it('renders the selected catalog field display name', () => {
		renderPanel({value: {...baseValue, field: 'account.annualRevenue'}});

		expect(screen.getByText('Annual Revenue')).toBeInTheDocument();
	});

	it('reveals the next condition picker once the previous is filled', () => {
		renderPanel({value: {...baseValue, field: 'account.industry'}});

		expect(screen.getByText('Select Operator')).toBeInTheDocument();
		expect(screen.queryByLabelText('Value')).toBeNull();
	});

	it('shows a numeric value input for a number field operator', () => {
		renderPanel({
			value: {
				...baseValue,
				field: 'account.annualRevenue',
				fieldDataCategory: 'Number',
				fieldDataType: 'NUMERIC',
				operator: 'gt',
			},
		});

		expect(screen.getByText('greater than')).toBeInTheDocument();
		expect(screen.getByLabelText('Value')).toHaveAttribute(
			'type',
			'number'
		);
	});

	it('shows a date picker for a date field operator', () => {
		renderPanel({
			value: {
				...baseValue,
				field: 'account.createdDate',
				fieldDataCategory: 'Date',
				fieldDataType: 'DATE',
				operator: 'before',
			},
		});

		expect(screen.getByText('is before')).toBeInTheDocument();
		expect(screen.getByPlaceholderText('YYYY-MM-DD')).toBeInTheDocument();
		expect(screen.queryByLabelText('Value')).toBeNull();
	});

	const dateFieldValue = {
		...baseValue,
		field: 'account.createdDate',
		fieldDataCategory: 'Date' as const,
		fieldDataType: 'DATE',
		operator: 'before',
	};

	it('persists a typed date as the condition value', () => {
		const onChange = jest.fn();

		renderPanel({onChange, value: dateFieldValue});

		fireEvent.change(screen.getByPlaceholderText('YYYY-MM-DD'), {
			target: {value: '2026-03-15'},
		});

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({conditionValue: '2026-03-15'})
		);
	});

	it('persists a picked day as a yyyy-MM-dd condition value', () => {
		const onChange = jest.fn();

		const {container} = renderPanel({onChange, value: dateFieldValue});

		fireEvent.click(
			container.querySelector('[data-testid="date-button"]')!
		);

		fireEvent.click(screen.getByText('15'));

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({
				conditionValue: expect.stringMatching(/^\d{4}-\d{2}-15$/),
			})
		);
	});

	it('shows a text value input for a text field operator', () => {
		renderPanel({
			value: {
				...baseValue,
				field: 'account.industry',
				fieldDataCategory: 'Text',
				fieldDataType: 'STRING',
				operator: 'contains',
			},
		});

		expect(screen.getByText('contains')).toBeInTheDocument();
		expect(screen.getByLabelText('Value')).toHaveAttribute('type', 'text');
	});

	it('updates the condition value when the value input changes', () => {
		const onChange = jest.fn();

		renderPanel({
			onChange,
			value: {
				...baseValue,
				field: 'account.annualRevenue',
				fieldDataCategory: 'Number',
				fieldDataType: 'NUMERIC',
				operator: 'gt',
			},
		});

		fireEvent.change(screen.getByLabelText('Value'), {
			target: {value: '100'},
		});

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({conditionValue: '100'})
		);
	});

	it('suppresses the value input for a boolean field', () => {
		renderPanel({
			value: {
				...baseValue,
				description: 'Cold accounts',
				field: 'account.hasActivePipeline',
				fieldDataCategory: 'Boolean',
				fieldDataType: 'BOOLEAN',
				operator: 'true',
			},
		});

		expect(screen.queryByLabelText('Value')).toBeNull();
		expect(screen.getByText('Configured')).toBeInTheDocument();
	});

	it('hides the value input and marks configured for a value-less operator', () => {
		renderPanel({
			value: {
				...baseValue,
				description: 'Cold accounts',
				field: 'account.annualRevenue',
				fieldDataCategory: 'Number',
				fieldDataType: 'NUMERIC',
				operator: 'is-unknown',
			},
		});

		expect(screen.queryByLabelText('Value')).toBeNull();
		expect(screen.getByText('Configured')).toBeInTheDocument();
	});

	it('resets the operator and value when the field changes', () => {
		const onChange = jest.fn();

		renderPanel({
			onChange,
			value: {
				...baseValue,
				conditionValue: 'true',
				field: 'account.industry',
				fieldDataCategory: 'Text',
				fieldDataType: 'STRING',
				operator: 'is',
			},
		});

		fireEvent.click(screen.getByText('Industry'));
		fireEvent.click(screen.getByText('Annual Revenue'));

		expect(onChange).toHaveBeenCalledWith({
			...baseValue,
			conditionValue: null,
			field: 'account.annualRevenue',
			fieldDataCategory: 'Number',
			fieldDataType: 'NUMERIC',
			operator: null,
		});
	});

	it('calls onChange when the max-time toggle is switched off', () => {
		const onChange = jest.fn();

		const {container} = renderPanel({onChange});

		fireEvent.click(container.querySelector('input.toggle-switch-check')!);

		expect(onChange).toHaveBeenCalledWith({
			...baseValue,
			maxTimeEnabled: false,
		});
	});
});
