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
import {
	createStageCondition,
	IStageCondition,
	IStageConfig,
	MatchLogic,
} from 'lifecycle/utils/stageConfiguration';
import {
	LifecycleStages,
	lifecycleStagesLabelMap,
} from 'contacts/pages/account/utils/constants';
import {
	OPERATORS_BY_TYPE,
	OperatorType,
	VALUELESS_OPERATORS,
	isConditionComplete,
	isStageConfigured,
	resolveOperatorType,
} from 'lifecycle/utils/lifecycleOperators';
import {removeAtIndex} from 'shared/util/array';
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

const MATCH_LOGIC_OPTIONS = [
	{label: Liferay.Language.get('all'), value: MatchLogic.All},
	{label: Liferay.Language.get('any'), value: MatchLogic.Any},
];

const conditionOperatorType = (condition: IStageCondition) =>
	resolveOperatorType(condition.fieldDataCategory, condition.fieldDataType);

const conditionOperatorOptions = (condition: IStageCondition) => {
	const operatorType = conditionOperatorType(condition);

	return operatorType ? OPERATORS_BY_TYPE[operatorType] : [];
};

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
	const [expandedDateKey, setExpandedDateKey] = useState<string | null>(null);

	const {conditions, matchLogic} = value;

	const updateCondition = (
		conditionIndex: number,
		patch: Partial<IStageCondition>
	) =>
		onChange({
			...value,
			conditions: conditions.map((current, currentIndex) =>
				currentIndex === conditionIndex
					? {...current, ...patch}
					: current
			),
		});

	const addCondition = () =>
		onChange({
			...value,
			conditions: [...conditions, createStageCondition()],
		});

	const removeCondition = (conditionIndex: number) =>
		onChange({
			...value,
			conditions: removeAtIndex(conditions, conditionIndex),
		});

	const configured = isStageConfigured(value);

	const connectorLabel =
		matchLogic === MatchLogic.Any
			? Liferay.Language.get('or')
			: Liferay.Language.get('and');

	const renderFieldPicker = (
		condition: IStageCondition,
		conditionIndex: number
	) => {
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

					updateCondition(conditionIndex, {
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

	const renderOperatorPicker = (
		condition: IStageCondition,
		conditionIndex: number
	) => {
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
					updateCondition(conditionIndex, {
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

	const renderValueInput = (
		condition: IStageCondition,
		conditionIndex: number
	) => {
		const resolvedType = conditionOperatorType(condition);

		if (resolvedType === OperatorType.Date) {
			const today = getDateNow();

			const minDate = today.clone().subtract(1, 'year');
			const maxDate = today.clone().add(1, 'year');

			return (
				<ClayDatePicker
					className="form-control-sm"
					dateFormat="yyyy-MM-dd"
					expanded={expandedDateKey === condition.key}
					max={maxDate.format(DEFAULT_DATE_FORMAT)}
					min={minDate.format(DEFAULT_DATE_FORMAT)}
					months={moment.months()}
					onChange={(conditionValue) =>
						updateCondition(conditionIndex, {conditionValue})
					}
					onExpandedChange={(expanded) =>
						setExpandedDateKey(expanded ? condition.key : null)
					}
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
					updateCondition(conditionIndex, {
						conditionValue: event.target.value,
					})
				}
				sizing="sm"
				type={type}
				value={condition.conditionValue ?? ''}
			/>
		);
	};

	const renderCondition = (
		condition: IStageCondition,
		conditionIndex: number
	) => {
		const isValuelessOperator =
			!!condition.operator && VALUELESS_OPERATORS.has(condition.operator);

		/**
		 * A row the operator has not started yet is left alone, so a new
		 * lifecycle does not open with an error against every stage. Once an
		 * attribute is picked the row has to be finished to be saved.
		 */
		const incomplete = !!condition.field && !isConditionComplete(condition);

		return (
			<>
				<div
					className={getCN(
						'align-items-center c-gap-2 d-flex p-3 rounded stage-configuration-panel__condition',
						{'has-error': incomplete}
					)}
				>
					<Text size={3} weight="semi-bold">
						{Liferay.Language.get('account')}
					</Text>

					{renderFieldPicker(condition, conditionIndex)}

					{condition.field &&
						renderOperatorPicker(condition, conditionIndex)}

					{condition.operator &&
						!isValuelessOperator &&
						renderValueInput(condition, conditionIndex)}

					{conditionIndex > 0 && (
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get('remove')}
							className="ml-auto text-secondary"
							data-tooltip-align="top"
							displayType="unstyled"
							onClick={() => removeCondition(conditionIndex)}
							size="xs"
							symbol="times-circle"
							title={Liferay.Language.get('remove')}
						/>
					)}
				</div>

				{incomplete && (
					<div className="form-feedback-group">
						<div className="form-feedback-item">
							<span className="form-feedback-indicator">
								<Icon symbol="exclamation-full" />
							</span>

							{Liferay.Language.get(
								'finish-this-condition-by-choosing-an-operator-and-a-value'
							)}
						</div>
					</div>
				)}
			</>
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

				<div className="border mb-4 rounded stage-configuration-panel__trigger">
					<div className="align-items-center border-bottom c-gap-2 d-flex p-3 stage-configuration-panel__match">
						<Picker
							aria-label={Liferay.Language.get('match-logic')}
							as={PickerTriggerButton}
							disabled={conditions.length < 2}
							items={MATCH_LOGIC_OPTIONS}
							label={
								matchLogic === MatchLogic.Any
									? Liferay.Language.get('any')
									: Liferay.Language.get('all')
							}
							onSelectionChange={(key) =>
								onChange({
									...value,
									matchLogic: key as MatchLogic,
								})
							}
							selectedKey={matchLogic}
						>
							{(item) => (
								<Option key={item.value}>{item.label}</Option>
							)}
						</Picker>

						<Text size={3}>
							{Liferay.Language.get('of-these-criteria-are-met')}
						</Text>
					</div>

					<div className="p-3">
						{conditions.map((condition, conditionIndex) => (
							<React.Fragment key={condition.key}>
								{conditionIndex > 0 && (
									<div className="my-2 stage-configuration-panel__connector">
										{connectorLabel}
									</div>
								)}

								{renderCondition(condition, conditionIndex)}
							</React.Fragment>
						))}

						<ClayButton
							className="mt-3"
							displayType="secondary"
							onClick={addCondition}
							size="sm"
						>
							<Icon
								className="inline-item inline-item-before"
								symbol="plus"
							/>

							{Liferay.Language.get('add-trigger')}
						</ClayButton>
					</div>
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
