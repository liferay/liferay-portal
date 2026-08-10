import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayDatePicker from '@clayui/date-picker';
import ClayPopover from '@clayui/popover';
import getCN from 'classnames';
import Label from '@clayui/label';
import moment from 'moment';
import Panel from '@clayui/panel';
import React, {useState} from 'react';
import ClayForm, {ClayInput, ClayToggle} from '@clayui/form';
import {DEFAULT_DATE_FORMAT, getDateNow} from 'shared/util/date';
import {getCatalogFieldLabel, ICatalogField} from 'shared/api/catalog';
import {Icon, Option, Picker, Text} from '@clayui/core';
import {IStageConfig} from 'lifecycle/utils/stageConfiguration';
import {
	LifecycleStages,
	lifecycleStagesLabelMap,
} from 'contacts/pages/account/utils/constants';
import {
	OPERATORS_BY_TYPE,
	OperatorType,
	VALUELESS_OPERATORS,
	isStageConfigured,
	resolveOperatorType,
} from 'lifecycle/utils/lifecycleOperators';
import {sub} from 'shared/util/lang';

interface IPickerTriggerButtonProps
	extends React.ButtonHTMLAttributes<HTMLButtonElement> {
	buttonClassName?: string;
	label: string;
}

const PickerTriggerButton = React.forwardRef<
	HTMLButtonElement,
	IPickerTriggerButtonProps
>(({buttonClassName, label, ...rest}, ref) => (
	<ClayButton
		{...rest}
		className={getCN('rounded-lg', buttonClassName)}
		displayType="secondary"
		ref={ref}
		size="sm"
	>
		{label}

		<Icon className="inline-item inline-item-after" symbol="caret-double" />
	</ClayButton>
));

const selectPlaceholder = (label: string) =>
	sub(Liferay.Language.get('select-x'), [label]) as string;

interface IStageConfigurationPanelProps {
	defaultExpanded?: boolean;
	fields?: ICatalogField[];
	index: number;
	onChange: (value: IStageConfig) => void;
	stageType: LifecycleStages;
	value: IStageConfig;
}

const StageConfigurationPanel: React.FC<IStageConfigurationPanelProps> = ({
	defaultExpanded = false,
	fields = [],
	index,
	onChange,
	stageType,
	value,
}) => {
	const [dateExpanded, setDateExpanded] = useState(false);

	const resolvedType = resolveOperatorType(
		value.fieldDataCategory,
		value.fieldDataType
	);

	const isValuelessOperator =
		!!value.operator && VALUELESS_OPERATORS.has(value.operator);

	const configured = isStageConfigured(value);

	const operatorOptions = resolvedType ? OPERATORS_BY_TYPE[resolvedType] : [];

	const renderFieldPicker = () => {
		const selectedField = fields.find(
			(field) => field.name === value.field
		);

		const selectableFields = fields.filter((field) =>
			resolveOperatorType(field.dataCategory, field.dataType)
		);

		return (
			<Picker
				aria-label={selectPlaceholder(Liferay.Language.get('field'))}
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
						: selectPlaceholder(Liferay.Language.get('field'))
				}
				onSelectionChange={(key) => {
					const field = fields.find(
						(catalogField) => catalogField.name === String(key)
					);

					onChange({
						...value,
						conditionValue: null,
						field: String(key),
						fieldDataCategory: field?.dataCategory ?? null,
						fieldDataType: field?.dataType ?? null,
						operator: null,
					});
				}}
				searchable
				selectedKey={value.field ?? undefined}
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
		const selectedOperator = operatorOptions.find(
			(option) => option.value === value.operator
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
						...value,
						conditionValue: null,
						operator: String(key),
					})
				}
				searchable
				selectedKey={value.operator ?? undefined}
			>
				{(item) => <Option key={item.value}>{item.label}</Option>}
			</Picker>
		);
	};

	const renderValueInput = () => {
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
					onChange={(conditionValue) =>
						onChange({...value, conditionValue})
					}
					onExpandedChange={setDateExpanded}
					placeholder={Liferay.Language.get('yyyy-mm-dd')}
					value={value.conditionValue ?? ''}
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
					onChange({
						...value,
						conditionValue: event.target.value,
					})
				}
				sizing="sm"
				type={type}
				value={value.conditionValue ?? ''}
			/>
		);
	};

	return (
		<Panel
			className="border mb-3 rounded stage-configuration-panel"
			collapsable
			defaultExpanded={defaultExpanded}
			displayTitle={
				<Panel.Title className="align-items-center d-flex">
					<span className="mr-3">
						<Text size={6} weight="bold">
							{sub(Liferay.Language.get('stage-x'), [index])}
						</Text>
					</span>

					<Label
						displayType={configured ? 'success' : 'secondary'}
						inverse
					>
						{configured
							? Liferay.Language.get('configured')
							: Liferay.Language.get('unconfigured')}
					</Label>
				</Panel.Title>
			}
		>
			<Panel.Body>
				<Text weight="semi-bold">
					{lifecycleStagesLabelMap[stageType].label}
				</Text>

				<ClayForm.Group className="mb-4 mt-3">
					<label htmlFor={`stage-description-${index}`}>
						{Liferay.Language.get('description')}

						<span className="reference-mark">
							<Icon symbol="asterisk" />
						</span>
					</label>

					<ClayInput
						id={`stage-description-${index}`}
						onChange={(event) =>
							onChange({
								...value,
								description: event.target.value,
							})
						}
						sizing="sm"
						value={value.description}
					/>
				</ClayForm.Group>

				<div className="border-bottom font-weight-semi-bold mb-3 pb-2 text-secondary">
					{Liferay.Language.get('stage-conditions').toUpperCase()}
				</div>

				<div className="font-weight-semi-bold mb-2">
					{Liferay.Language.get('trigger')}
				</div>

				<div className="align-items-center c-gap-2 d-flex mb-4">
					<Text size={3} weight="semi-bold">
						{Liferay.Language.get('account')}
					</Text>

					{renderFieldPicker()}

					{value.field && renderOperatorPicker()}

					{value.operator &&
						!isValuelessOperator &&
						renderValueInput()}
				</div>

				<div className="align-items-center d-flex font-weight-semi-bold mb-2">
					{Liferay.Language.get('max-time-in-stage')}

					<ClayPopover
						alignPosition="top"
						closeOnClickOutside
						header={Liferay.Language.get('max-time-in-stage')}
						trigger={
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get(
									'max-time-in-stage-help'
								)}
								className="ml-1 text-secondary"
								displayType="unstyled"
								size="xs"
								symbol="question-circle-full"
							/>
						}
					>
						{Liferay.Language.get('max-time-in-stage-help')}
					</ClayPopover>
				</div>

				<div className="align-items-center c-gap-2 d-flex">
					<ClayToggle
						containerProps={{className: 'mb-0'}}
						onToggle={(maxTimeEnabled) =>
							onChange({...value, maxTimeEnabled})
						}
						toggled={value.maxTimeEnabled}
					/>

					<span className="font-weight-semi-bold">
						{Liferay.Language.get('set-limit-to')}
					</span>

					<ClayInput
						aria-label={Liferay.Language.get('max-time-in-stage')}
						className="w-auto"
						disabled={!value.maxTimeEnabled}
						min={1}
						onChange={(
							event: React.ChangeEvent<HTMLInputElement>
						) =>
							onChange({
								...value,
								maxTimeDays:
									Number(event.target.value) ||
									value.maxTimeDays,
							})
						}
						sizing="sm"
						type="number"
						value={value.maxTimeDays}
					/>

					<span>{Liferay.Language.get('days').toLowerCase()}</span>
				</div>
			</Panel.Body>
		</Panel>
	);
};

export default StageConfigurationPanel;
