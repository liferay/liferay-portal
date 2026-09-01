import React from 'react';
import StageConfigurationPanel from '../StageConfigurationPanel';
import {
	createStageCondition,
	DEFAULT_MAX_DAYS,
	IStageCondition,
	IStageConfig,
	MatchLogic,
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
	conditions: [createStageCondition()],
	description: '',
	id: null,
	matchLogic: MatchLogic.All,
	maxTimeDays: DEFAULT_MAX_DAYS,
	maxTimeEnabled: true,
};

const withCondition = (
	condition: Partial<IStageCondition>,
	stage: Partial<IStageConfig> = {}
): IStageConfig => ({
	...baseValue,
	conditions: [{...createStageCondition(), ...condition}],
	...stage,
});

const withConditions = (
	conditions: Partial<IStageCondition>[],
	stage: Partial<IStageConfig> = {}
): IStageConfig => ({
	...baseValue,
	conditions: conditions.map((condition) => ({
		...createStageCondition(),
		...condition,
	})),
	...stage,
});

const textCondition = {
	conditionValue: 'Retail',
	field: 'account.industry',
	fieldDataCategory: 'Text',
	fieldDataType: 'STRING',
	operator: 'is',
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
			value: withCondition(
				{
					conditionValue: 'true',
					field: 'HasActivePipeline',
					operator: 'is',
				},
				{description: 'Cold accounts'}
			),
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
		expect(screen.getByText('Select Attribute')).toBeInTheDocument();
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
		renderPanel({value: withCondition({field: 'account.annualRevenue'})});

		expect(screen.getByText('Annual Revenue')).toBeInTheDocument();
	});

	it('reveals the next condition picker once the previous is filled', () => {
		renderPanel({value: withCondition({field: 'account.industry'})});

		expect(screen.getByText('Select Operator')).toBeInTheDocument();
		expect(screen.queryByLabelText('Value')).toBeNull();
	});

	it('shows a numeric value input for a number field operator', () => {
		renderPanel({
			value: withCondition({
				field: 'account.annualRevenue',
				fieldDataCategory: 'Number',
				fieldDataType: 'NUMERIC',
				operator: 'gt',
			}),
		});

		expect(screen.getByText('greater than')).toBeInTheDocument();
		expect(screen.getByLabelText('Value')).toHaveAttribute(
			'type',
			'number'
		);
	});

	it('shows a date picker for a date field operator', () => {
		renderPanel({
			value: withCondition({
				field: 'account.createdDate',
				fieldDataCategory: 'Date',
				fieldDataType: 'DATE',
				operator: 'before',
			}),
		});

		expect(screen.getByText('is before')).toBeInTheDocument();
		expect(screen.getByPlaceholderText('YYYY-MM-DD')).toBeInTheDocument();
		expect(screen.queryByLabelText('Value')).toBeNull();
	});

	const dateFieldValue = withCondition({
		field: 'account.createdDate',
		fieldDataCategory: 'Date',
		fieldDataType: 'DATE',
		operator: 'before',
	});

	it('persists a typed date as the condition value', () => {
		const onChange = jest.fn();

		renderPanel({onChange, value: dateFieldValue});

		fireEvent.change(screen.getByPlaceholderText('YYYY-MM-DD'), {
			target: {value: '2026-03-15'},
		});

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({
				conditions: [
					expect.objectContaining({conditionValue: '2026-03-15'}),
				],
			})
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
				conditions: [
					expect.objectContaining({
						conditionValue:
							expect.stringMatching(/^\d{4}-\d{2}-15$/),
					}),
				],
			})
		);
	});

	it('shows a text value input for a text field operator', () => {
		renderPanel({
			value: withCondition({
				field: 'account.industry',
				fieldDataCategory: 'Text',
				fieldDataType: 'STRING',
				operator: 'contains',
			}),
		});

		expect(screen.getByText('contains')).toBeInTheDocument();
		expect(screen.getByLabelText('Value')).toHaveAttribute('type', 'text');
	});

	it('updates the condition value when the value input changes', () => {
		const onChange = jest.fn();

		renderPanel({
			onChange,
			value: withCondition({
				field: 'account.annualRevenue',
				fieldDataCategory: 'Number',
				fieldDataType: 'NUMERIC',
				operator: 'gt',
			}),
		});

		fireEvent.change(screen.getByLabelText('Value'), {
			target: {value: '100'},
		});

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({
				conditions: [expect.objectContaining({conditionValue: '100'})],
			})
		);
	});

	it('suppresses the value input for a boolean field', () => {
		renderPanel({
			value: withCondition(
				{
					field: 'account.hasActivePipeline',
					fieldDataCategory: 'Boolean',
					fieldDataType: 'BOOLEAN',
					operator: 'true',
				},
				{description: 'Cold accounts'}
			),
		});

		expect(screen.queryByLabelText('Value')).toBeNull();
		expect(screen.getByText('Configured')).toBeInTheDocument();
	});

	it('hides the value input and marks configured for a value-less operator', () => {
		renderPanel({
			value: withCondition(
				{
					field: 'account.annualRevenue',
					fieldDataCategory: 'Number',
					fieldDataType: 'NUMERIC',
					operator: 'is-unknown',
				},
				{description: 'Cold accounts'}
			),
		});

		expect(screen.queryByLabelText('Value')).toBeNull();
		expect(screen.getByText('Configured')).toBeInTheDocument();
	});

	it('resets the operator and value when the field changes', () => {
		const onChange = jest.fn();

		renderPanel({
			onChange,
			value: withCondition({
				conditionValue: 'true',
				field: 'account.industry',
				fieldDataCategory: 'Text',
				fieldDataType: 'STRING',
				operator: 'is',
			}),
		});

		fireEvent.click(screen.getByText('Industry'));
		fireEvent.click(screen.getByText('Annual Revenue'));

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({
				conditions: [
					expect.objectContaining({
						conditionValue: null,
						field: 'account.annualRevenue',
						fieldDataCategory: 'Number',
						fieldDataType: 'NUMERIC',
						operator: null,
					}),
				],
			})
		);
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

	const unresolvableField = {
		dataCategory: null,
		dataType: 'STRING',
		description: '',
		displayName: 'Uncategorized Field',
		id: 'account.uncategorized',
		name: 'account.uncategorized',
		parentField: null,
		tableName: 'account',
	} as unknown as ICatalogField;

	it('offers a field whose data category only differs in casing', () => {
		renderPanel({
			fields: [
				{
					...mockFields[0],
					dataCategory: 'TEXT',
				} as unknown as ICatalogField,
			],
		});

		fireEvent.click(screen.getByText('Select Attribute'));

		expect(screen.getByText('Industry')).toBeInTheDocument();
	});

	it('leaves out a field no condition can be built from', () => {
		renderPanel({fields: [...mockFields, unresolvableField]});

		fireEvent.click(screen.getByText('Select Attribute'));

		expect(screen.getByText('Industry')).toBeInTheDocument();
		expect(screen.queryByText('Uncategorized Field')).toBeNull();
	});

	const unlabeledField: ICatalogField = {
		dataCategory: 'Text',
		dataType: 'STRING',
		description: null,
		displayName: null,
		id: 'accountName',
		name: 'accountName',
		parentField: null,
		tableName: 'account',
	};

	it('falls back to the field name when the catalog omits a label', () => {
		renderPanel({fields: [...mockFields, unlabeledField]});

		fireEvent.click(screen.getByText('Select Attribute'));

		expect(screen.getByText('accountName')).toBeInTheDocument();
	});

	it('names a selected field that has no label', () => {
		renderPanel({
			fields: [...mockFields, unlabeledField],
			value: withCondition({field: 'accountName'}),
		});

		expect(screen.getByText('accountName')).toBeInTheDocument();
	});

	it('orders the offered fields by their visible label', () => {
		renderPanel({fields: [...mockFields, unlabeledField]});

		fireEvent.click(screen.getByText('Select Attribute'));

		const labels = screen
			.getAllByRole('option')
			.map((option) => option.textContent);

		expect(labels).toEqual(
			[...labels].sort((a, b) => a!.localeCompare(b!))
		);
	});

	it('still names a selected field that is no longer offered', () => {
		renderPanel({
			fields: [...mockFields, unresolvableField],
			value: withCondition({field: 'account.uncategorized'}),
		});

		expect(screen.getByText('Uncategorized Field')).toBeInTheDocument();
	});
	it('adds a condition row when Add Trigger is clicked', () => {
		const onChange = jest.fn();

		renderPanel({onChange});

		fireEvent.click(screen.getByText(/add.trigger/i));

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({
				conditions: [
					expect.objectContaining({field: null}),
					expect.objectContaining({field: null}),
				],
			})
		);
	});

	it('joins the conditions with AND while matching on all', () => {
		renderPanel({value: withConditions([textCondition, textCondition])});

		expect(screen.getByText('And')).toBeInTheDocument();
		expect(screen.queryByText('Or')).toBeNull();
	});

	it('joins the conditions with OR while matching on any', () => {
		renderPanel({
			value: withConditions([textCondition, textCondition], {
				matchLogic: MatchLogic.Any,
			}),
		});

		expect(screen.getByText('Or')).toBeInTheDocument();
		expect(screen.queryByText('And')).toBeNull();
	});

	it('shows no connector while the stage holds a single condition', () => {
		renderPanel({value: withCondition(textCondition)});

		expect(screen.queryByText('And')).toBeNull();
		expect(screen.queryByText('Or')).toBeNull();
	});

	it('disables the match logic picker while a stage holds one condition', () => {
		renderPanel({value: withCondition(textCondition)});

		expect(screen.getByLabelText(/match.logic/i)).toBeDisabled();
	});

	it('enables the match logic picker once a stage holds two conditions', () => {
		renderPanel({value: withConditions([textCondition, textCondition])});

		expect(screen.getByLabelText(/match.logic/i)).toBeEnabled();
	});

	it('persists the chosen match logic', () => {
		const onChange = jest.fn();

		renderPanel({
			onChange,
			value: withConditions([textCondition, textCondition]),
		});

		fireEvent.click(screen.getByLabelText(/match.logic/i));
		fireEvent.click(screen.getByText('Any'));

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({matchLogic: MatchLogic.Any})
		);
	});

	it('offers no remove control while a stage holds one condition', () => {
		renderPanel({value: withCondition(textCondition)});

		expect(screen.queryByLabelText(/remove/i)).toBeNull();
	});

	it('offers a remove control on every condition but the first', () => {
		renderPanel({value: withConditions([textCondition, textCondition])});

		expect(screen.getAllByLabelText(/remove/i)).toHaveLength(1);
	});

	it('removes the condition its remove control belongs to', () => {
		const onChange = jest.fn();

		renderPanel({
			onChange,
			value: withConditions([
				textCondition,
				{...textCondition, conditionValue: 'Finance'},
			]),
		});

		fireEvent.click(screen.getByLabelText(/remove/i));

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({
				conditions: [
					expect.objectContaining({conditionValue: 'Retail'}),
				],
			})
		);
	});

	it('edits the condition the changed row belongs to', () => {
		const onChange = jest.fn();

		renderPanel({
			onChange,
			value: withConditions([
				textCondition,
				{...textCondition, conditionValue: 'Finance'},
			]),
		});

		fireEvent.change(screen.getAllByLabelText('Value')[1], {
			target: {value: 'Energy'},
		});

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({
				conditions: [
					expect.objectContaining({conditionValue: 'Retail'}),
					expect.objectContaining({conditionValue: 'Energy'}),
				],
			})
		);
	});

	it('flags a condition left unfinished once an attribute is chosen', () => {
		renderPanel({value: withCondition({field: 'account.industry'})});

		expect(screen.getByText(/finish this condition/i)).toBeInTheDocument();
	});

	it('leaves a condition not yet started unflagged', () => {
		renderPanel();

		expect(screen.queryByText(/finish this condition/i)).toBeNull();
	});

	it('leaves a finished condition unflagged', () => {
		renderPanel({value: withCondition(textCondition)});

		expect(screen.queryByText(/finish this condition/i)).toBeNull();
	});
});
