import ClayDatePicker from '@clayui/date-picker';
import getCN from 'classnames';
import Label from '@clayui/label';
import moment from 'moment';
import PickerTriggerButton from 'lifecycle/components/PickerTriggerButton';
import React, {useState} from 'react';
import {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import {DEFAULT_DATE_FORMAT, getDateNow} from 'shared/util/date';
import {getCatalogFieldLabel, ICatalogField} from 'shared/api/catalog';
import {IStageCondition} from 'lifecycle/utils/stageConfiguration';
import {Option, Picker, Text} from '@clayui/core';
import {
	OPERATORS_BY_TYPE,
	OperatorType,
	VALUELESS_OPERATORS,
	isConditionComplete,
	resolveOperatorType,
} from 'lifecycle/utils/lifecycleOperators';
import {sub} from 'shared/util/lang';

const selectPlaceholder = (label: string) =>
	sub(Liferay.Language.get('select-x'), [label]) as string;

const conditionOperatorType = (condition: IStageCondition) =>
	resolveOperatorType(condition.fieldDataCategory, condition.fieldDataType);

const conditionOperatorOptions = (condition: IStageCondition) => {
	const operatorType = conditionOperatorType(condition);

	return operatorType ? OPERATORS_BY_TYPE[operatorType] : [];
};

interface IStageConditionRowProps {
	condition: IStageCondition;
	fields?: ICatalogField[];
	onChange: (patch: Partial<IStageCondition>) => void;
	onRemove?: () => void;
}

const StageConditionRow: React.FC<IStageConditionRowProps> = ({
	condition,
	fields = [],
	onChange,
	onRemove,
}) => {
	const [dateExpanded, setDateExpanded] = useState(false);

	const isValuelessOperator =
		!!condition.operator && VALUELESS_OPERATORS.has(condition.operator);

	const incomplete = !!condition.field && !isConditionComplete(condition);

	const renderFieldPicker = () => {
		const selectedField = fields.find(
			(field) => field.name === condition.field
		);

		const selectableFields = fields.filter((field) =>
			resolveOperatorType(field.dataCategory, field.dataType)
		);

		return (
			<Picker
				aria-label={selectPlaceholder(
					Liferay.Language.get('attribute')
				)}
				as={PickerTriggerButton}
				items={selectableFields
					.map((field) => ({
						isCalculated: !!field.parentField,
						label: getCatalogFieldLabel(field),
						value: field.name,
					}))
					.sort((a, b) => a.label.localeCompare(b.label))}
				label={
					selectedField
						? getCatalogFieldLabel(selectedField)
						: selectPlaceholder(Liferay.Language.get('attribute'))
				}
				onSelectionChange={(key) => {
					const field = fields.find(
						(catalogField) => catalogField.name === String(key)
					);

					onChange({
						conditionValue: null,
						field: String(key),
						fieldDataCategory: field?.dataCategory ?? null,
						fieldDataType: field?.dataType ?? null,
						operator: null,
					});
				}}
				searchable
				selectedKey={condition.field ?? undefined}
			>
				{(item) => (
					<Option key={item.value} textValue={item.label}>
						{item.isCalculated ? (
							<span className="align-items-center c-gap-2 d-flex">
								{item.label}

								<Label
									className="my-0"
									displayType="secondary"
									inverse
								>
									{Liferay.Language.get('calculated-field')}
								</Label>
							</span>
						) : (
							item.label
						)}
					</Option>
				)}
			</Picker>
		);
	};

	const renderOperatorPicker = () => {
		const operatorOptions = conditionOperatorOptions(condition);

		const selectedOperator = operatorOptions.find(
			(option) => option.value === condition.operator
		);

		return (
			<Picker
				aria-label={selectPlaceholder(Liferay.Language.get('operator'))}
				as={PickerTriggerButton}
				items={operatorOptions}
				label={
					selectedOperator
						? selectedOperator.label
						: selectPlaceholder(Liferay.Language.get('operator'))
				}
				onSelectionChange={(key) =>
					onChange({
						conditionValue: null,
						operator: String(key),
					})
				}
				searchable
				selectedKey={condition.operator ?? undefined}
			>
				{(item) => <Option key={item.value}>{item.label}</Option>}
			</Picker>
		);
	};

	const renderValueInput = () => {
		const resolvedType = conditionOperatorType(condition);

		if (resolvedType === OperatorType.Date) {
			const today = getDateNow();

			const minDate = today.clone().subtract(1, 'year');
			const maxDate = today.clone().add(1, 'year');

			return (
				<ClayDatePicker
					className="form-control-sm"
					dateFormat="yyyy-MM-dd"
					expanded={dateExpanded}
					max={maxDate.format(DEFAULT_DATE_FORMAT)}
					min={minDate.format(DEFAULT_DATE_FORMAT)}
					months={moment.months()}
					onChange={(conditionValue) => onChange({conditionValue})}
					onExpandedChange={setDateExpanded}
					placeholder={Liferay.Language.get('yyyy-mm-dd')}
					value={condition.conditionValue ?? ''}
					weekdaysShort={moment.weekdaysShort()}
					years={{
						end: maxDate.year(),
						start: minDate.year(),
					}}
				/>
			);
		}

		const type =
			resolvedType === OperatorType.Number ||
			resolvedType === OperatorType.Duration
				? 'number'
				: 'text';

		return (
			<ClayInput
				aria-label={Liferay.Language.get('value')}
				className="w-auto"
				onChange={(event) =>
					onChange({conditionValue: event.target.value})
				}
				sizing="sm"
				type={type}
				value={condition.conditionValue ?? ''}
			/>
		);
	};

	return (
		<div
			className={getCN(
				'align-items-center bg-white c-gap-2 d-flex p-3 rounded stage-configuration-panel__condition',
				{'has-error': incomplete}
			)}
		>
			<Text size={3} weight="semi-bold">
				{Liferay.Language.get('account')}
			</Text>

			{renderFieldPicker()}

			{condition.field && renderOperatorPicker()}

			{condition.operator && !isValuelessOperator && renderValueInput()}

			{onRemove && (
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('remove')}
					borderless
					className="ml-auto"
					data-tooltip-align="top"
					displayType="secondary"
					onClick={onRemove}
					size="xs"
					symbol="times-circle"
					title={Liferay.Language.get('remove')}
				/>
			)}
		</div>
	);
};

export default StageConditionRow;
