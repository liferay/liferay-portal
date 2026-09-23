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
			index={1}
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

		fireEvent.change(screen.getByLabelText(/value/i), {
			target: {value: 'Retail'},
		});

		expect(onChange).toHaveBeenCalledWith({conditionValue: 'Retail'});
	});

	it('names its controls after the condition it edits', () => {
		renderRow({index: 2});

		expect(screen.getByLabelText(/condition 2/i)).toBeInTheDocument();
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
	describe('with a date attribute', () => {
		afterEach(() => {
			jest.useRealTimers();
		});

		beforeEach(() => {
			jest.useFakeTimers().setSystemTime(
				new Date('2026-06-10T14:30:00Z')
			);
		});

		it('renders the month and weekday names in the date picker', () => {
			renderRow({
				condition: buildCondition({
					conditionValue: '2026-06-10',
					field: 'account.createDate',
					fieldDataCategory: 'Date',
					fieldDataType: 'DATE',
					operator: 'eq',
				}),
			});

			fireEvent.click(screen.getByTestId('date-button'));

			const monthOptions = Array.from(
				screen.getByTestId('month-select').querySelectorAll('option')
			).map((option) => option.textContent);

			const weekdays = Array.from(
				screen.getByRole('grid').querySelectorAll('abbr')
			).map((abbr) => abbr.textContent);

			expect(monthOptions).toEqual([
				'January',
				'February',
				'March',
				'April',
				'May',
				'June',
				'July',
				'August',
				'September',
				'October',
				'November',
				'December',
			]);
			expect(weekdays).toEqual([
				'Sun',
				'Mon',
				'Tue',
				'Wed',
				'Thu',
				'Fri',
				'Sat',
			]);
		});
	});
});
