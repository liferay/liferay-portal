import ClayButton from '@clayui/button';
import ClayPopover from '@clayui/popover';
import Label from '@clayui/label';
import Panel from '@clayui/panel';
import React from 'react';
import StageConditionRow from 'lifecycle/components/StageConditionRow';
import ClayForm, {ClayInput, ClayToggle} from '@clayui/form';
import PickerTriggerButton from 'shared/components/PickerTriggerButton';
import {ClayButtonWithIcon} from '@clayui/button';
import {ICatalogField} from 'shared/api/catalog';
import {Icon, Option, Picker, Text} from '@clayui/core';
import {
	createStageCondition,
	IStageCondition,
	IStageConfig,
	MatchLogic,
} from 'lifecycle/utils/stageConfiguration';
import {
	CONNECTOR_LABEL_BY_MATCH_LOGIC,
	MATCH_LOGIC_OPTIONS,
	isStageConfigured,
} from 'lifecycle/utils/lifecycleOperators';
import {
	LifecycleStages,
	lifecycleStagesLabelMap,
} from 'contacts/pages/account/utils/constants';
import {removeAtIndex} from 'shared/util/array';
import {sub} from 'shared/util/lang';

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

	const connectorLabel = CONNECTOR_LABEL_BY_MATCH_LOGIC[matchLogic];

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

				<div className="border mb-4 overflow-hidden rounded">
					<div className="align-items-center border-bottom c-gap-2 d-flex p-3 stage-configuration-panel__match text-secondary">
						{sub(
							Liferay.Language.get('x-of-these-criteria-are-met'),
							[
								<Picker
									aria-label={Liferay.Language.get(
										'match-logic'
									)}
									as={PickerTriggerButton}
									disabled={conditions.length < 2}
									items={MATCH_LOGIC_OPTIONS}
									key="matchLogic"
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
									size="xs"
								>
									{(item) => (
										<Option key={item.value}>
											{item.label}
										</Option>
									)}
								</Picker>,
							],
							false
						)}
					</div>

					<div className="p-3">
						{conditions.map((condition, conditionIndex) => (
							<React.Fragment key={condition.key}>
								{conditionIndex > 0 && (
									<div className="font-weight-semi-bold my-2 text-secondary text-uppercase">
										{connectorLabel}
									</div>
								)}

								<StageConditionRow
									condition={condition}
									fields={fields}
									index={conditionIndex + 1}
									onChange={(patch) =>
										updateCondition(conditionIndex, patch)
									}
									onRemove={
										conditionIndex > 0
											? () =>
													removeCondition(
														conditionIndex
													)
											: undefined
									}
								/>
							</React.Fragment>
						))}

						<ClayButton
							className="mt-3"
							displayType="secondary"
							onClick={addCondition}
							size="xs"
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
