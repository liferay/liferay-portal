import BaseEditPage from 'shared/components/base-edit-page';
import Breadcrumbs from 'shared/components/Breadcrumbs';
import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import ClayPopover from '@clayui/popover';
import Label from '@clayui/label';
import LifecycleFieldRow from 'lifecycle/components/LifecycleFieldRow';
import Panel from '@clayui/panel';
import PickerTriggerButton from 'shared/components/PickerTriggerButton';
import React from 'react';
import StageConditionRow from 'lifecycle/components/StageConditionRow';
import {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput, ClayToggle} from '@clayui/form';
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
import {getFieldErrorMessage} from 'lifecycle/utils/lifecycleValidation';
import {
	LIFECYCLE_STAGE_DESCRIPTION_MAX_LENGTH,
	LIFECYCLE_STAGE_NAME_MAX_LENGTH,
} from 'lifecycle/utils/constants';
import {noop} from 'lodash';
import {removeAtIndex} from 'shared/util/array';
import {sub} from 'shared/util/lang';

interface IStageSectionProps {
	children: React.ReactNode;
	description: string;
	title: string;
}

const StageSection: React.FC<IStageSectionProps> = ({
	children,
	description,
	title,
}) => (
	<Panel
		collapsable
		collapseHeaderClassNames="border-bottom px-0"
		defaultExpanded
		displayTitle={
			<Panel.Title>
				<Text color="secondary" size={4} weight="semi-bold">
					{title}
				</Text>
			</Panel.Title>
		}
		displayType="unstyled"
		showCollapseIcon
	>
		<Panel.Body className="px-0">
			<p className="mb-4 text-secondary">{description}</p>

			{children}
		</Panel.Body>
	</Panel>
);

interface IStageConfigurationPanelProps {
	fields?: ICatalogField[];
	isFieldErrorVisible?: (id: string) => boolean;
	lifecycleName: string;
	nameTaken?: boolean;
	onChange: (value: IStageConfig) => void;
	onFieldBlur?: (id: string) => void;
	onSelectSummary?: () => void;
	value: IStageConfig;
}

const StageConfigurationPanel: React.FC<IStageConfigurationPanelProps> = ({
	fields = [],
	isFieldErrorVisible = () => false,
	lifecycleName,
	nameTaken = false,
	onChange,
	onFieldBlur = noop,
	onSelectSummary,
	value,
}) => {
	const {conditions, key, matchLogic} = value;

	const nameId = `stageName-${key}`;

	const descriptionId = `stageDescription-${key}`;

	const descriptionErrorMessage = getFieldErrorMessage({
		maxLength: LIFECYCLE_STAGE_DESCRIPTION_MAX_LENGTH,
		requiredMessage: Liferay.Language.get(
			'the-description-field-is-required'
		),
		showErrors: isFieldErrorVisible(descriptionId),
		value: value.description,
	});

	const nameErrorMessage = getFieldErrorMessage({
		maxLength: LIFECYCLE_STAGE_NAME_MAX_LENGTH,
		requiredMessage: Liferay.Language.get('the-name-field-is-required'),
		showErrors: isFieldErrorVisible(nameId),
		takenMessage: nameTaken
			? Liferay.Language.get('enter-a-unique-name')
			: undefined,
		value: value.name,
	});

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
		<>
			<nav aria-label={Liferay.Language.get('breadcrumb')}>
				<Breadcrumbs
					items={[
						{label: lifecycleName},
						{
							active: true,
							label:
								value.name || Liferay.Language.get('new-stage'),
						},
					]}
					onClick={onSelectSummary}
				/>
			</nav>

			<div className="mb-5">
				<BaseEditPage.Title
					errorMessage={nameErrorMessage}
					id={nameId}
					label={Liferay.Language.get('name')}
					maxLength={LIFECYCLE_STAGE_NAME_MAX_LENGTH}
					onBlur={() => onFieldBlur(nameId)}
					onChange={(event) =>
						onChange({...value, name: event.target.value})
					}
					placeholder={Liferay.Language.get('new-stage')}
					required
					value={value.name}
				/>
			</div>

			<LifecycleFieldRow
				errorMessage={descriptionErrorMessage}
				icon="align-left"
				id={descriptionId}
				label={Liferay.Language.get('description')}
				required
			>
				<ClayInput
					aria-describedby={
						descriptionErrorMessage
							? `${descriptionId}Error`
							: undefined
					}
					id={descriptionId}
					maxLength={LIFECYCLE_STAGE_DESCRIPTION_MAX_LENGTH}
					onBlur={() => onFieldBlur(descriptionId)}
					onChange={(event) =>
						onChange({...value, description: event.target.value})
					}
					required
					value={value.description}
				/>
			</LifecycleFieldRow>

			<Card className="mt-4">
				<Card.Body>
					<div className="align-items-center c-gap-2 d-flex mb-4">
						<Text size={6} weight="semi-bold">
							{Liferay.Language.get('stage-conditions')}
						</Text>

						<Label
							displayType={configured ? 'success' : 'secondary'}
							inverse
						>
							{configured
								? Liferay.Language.get('configured')
								: Liferay.Language.get('unconfigured')}
						</Label>
					</div>

					<StageSection
						description={Liferay.Language.get(
							'define-how-accounts-move-between-lifecycle-stages'
						)}
						title={Liferay.Language.get('progression')}
					>
						<div className="align-items-center c-gap-2 d-flex mb-4">
							<ClayToggle
								containerProps={{className: 'mb-0'}}
								label={Liferay.Language.get(
									'enable-max-time-in-stage'
								)}
								onToggle={(maxTimeEnabled) =>
									onChange({...value, maxTimeEnabled})
								}
								toggled={value.maxTimeEnabled}
							/>

							<ClayPopover
								alignPosition="top"
								closeOnClickOutside
								header={Liferay.Language.get(
									'max-time-in-stage'
								)}
								trigger={
									<ClayButtonWithIcon
										aria-label={Liferay.Language.get(
											'max-time-in-stage-help'
										)}
										className="text-secondary"
										displayType="unstyled"
										size="xs"
										symbol="question-circle-full"
									/>
								}
							>
								{Liferay.Language.get('max-time-in-stage-help')}
							</ClayPopover>

							<ClayInput
								aria-label={Liferay.Language.get(
									'max-time-in-stage'
								)}
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

							<span className="text-secondary">
								{Liferay.Language.get('days').toLowerCase()}
							</span>
						</div>
					</StageSection>

					<StageSection
						description={Liferay.Language.get(
							'define-entry-triggers-for-each-lifecycle-stage.-an-account-moves-to-a-stage-when-it-meets-the-selected-triggers'
						)}
						title={Liferay.Language.get('triggers')}
					>
						<div className="border overflow-hidden rounded">
							<div className="align-items-center border-bottom c-gap-2 d-flex p-3 stage-configuration-panel__match text-secondary">
								{sub(
									Liferay.Language.get(
										'x-of-these-criteria-must-be-met'
									),
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
													? Liferay.Language.get(
															'any'
														)
													: Liferay.Language.get(
															'all'
														)
											}
											onSelectionChange={(key) =>
												onChange({
													...value,
													matchLogic:
														key as MatchLogic,
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
												updateCondition(
													conditionIndex,
													patch
												)
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
					</StageSection>
				</Card.Body>
			</Card>
		</>
	);
};

export default StageConfigurationPanel;
