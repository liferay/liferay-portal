import {
	IEntityOption,
	IStageCondition,
	IStageConfig,
	MatchLogic,
} from 'lifecycle/utils/stageConfiguration';

export enum Operator {
	After = 'after',
	Before = 'before',
	Contains = 'contains',
	DoesNotContain = 'does-not-contain',
	Equals = 'eq',
	False = 'false',
	GreaterThan = 'gt',
	Is = 'is',
	IsKnown = 'is-known',
	IsNot = 'is-not',
	IsUnknown = 'is-unknown',
	LessThan = 'lt',
	NotEquals = 'neq',
	On = 'on',
	True = 'true',
}

export enum OperatorType {
	Boolean = 'Boolean',
	Date = 'Date',
	Duration = 'Duration',
	Number = 'Number',
	Text = 'Text',
}

export const CONNECTOR_LABEL_BY_MATCH_LOGIC: Record<MatchLogic, string> = {
	[MatchLogic.All]: Liferay.Language.get('and'),
	[MatchLogic.Any]: Liferay.Language.get('or'),
};

export const MATCH_LOGIC_OPTIONS: IEntityOption[] = [
	{label: Liferay.Language.get('all'), value: MatchLogic.All},
	{label: Liferay.Language.get('any'), value: MatchLogic.Any},
];

const NUMBER_OPERATORS: IEntityOption[] = [
	{label: 'is equal to', value: Operator.Equals},
	{label: 'greater than', value: Operator.GreaterThan},
	{label: 'less than', value: Operator.LessThan},
	{label: 'is not equal to', value: Operator.NotEquals},
	{label: 'is known', value: Operator.IsKnown},
	{label: 'is unknown', value: Operator.IsUnknown},
];

export const OPERATORS_BY_TYPE: Record<OperatorType, IEntityOption[]> = {
	[OperatorType.Boolean]: [
		{label: 'true', value: Operator.True},
		{label: 'false', value: Operator.False},
	],
	[OperatorType.Date]: [
		{label: 'is before', value: Operator.Before},
		{label: 'is on', value: Operator.On},
		{label: 'is after', value: Operator.After},
	],
	[OperatorType.Duration]: NUMBER_OPERATORS,
	[OperatorType.Number]: NUMBER_OPERATORS,
	[OperatorType.Text]: [
		{label: 'is', value: Operator.Is},
		{label: 'is not', value: Operator.IsNot},
		{label: 'contains', value: Operator.Contains},
		{label: 'does not contain', value: Operator.DoesNotContain},
		{label: 'is known', value: Operator.IsKnown},
		{label: 'is unknown', value: Operator.IsUnknown},
	],
};

export const VALUELESS_OPERATORS = new Set<string>([
	Operator.False,
	Operator.IsKnown,
	Operator.IsUnknown,
	Operator.True,
]);

export const isConditionComplete = (condition: IStageCondition): boolean => {
	if (!condition.field || !condition.operator) {
		return false;
	}

	if (VALUELESS_OPERATORS.has(condition.operator)) {
		return true;
	}

	const conditionValue = condition.conditionValue?.trim() ?? '';

	if (!conditionValue) {
		return false;
	}

	const operatorType = resolveOperatorType(
		condition.fieldDataCategory,
		condition.fieldDataType
	);

	if (
		operatorType === OperatorType.Duration ||
		operatorType === OperatorType.Number
	) {
		return Number.isFinite(Number(conditionValue));
	}

	return true;
};

export const isStageConfigured = (stage: IStageConfig): boolean =>
	!!stage.description.trim() &&
	!!stage.conditions.length &&
	stage.conditions.every(isConditionComplete);

export const resolveOperatorType = (
	dataCategory: string | null,
	dataType: string | null
): OperatorType | null => {
	if (!dataCategory) {
		return null;
	}

	if (dataType?.toUpperCase() === 'DURATION') {
		return OperatorType.Duration;
	}

	const operatorType = Object.keys(OPERATORS_BY_TYPE).find(
		(key) => key.toLowerCase() === dataCategory.toLowerCase()
	);

	return (operatorType as OperatorType) ?? null;
};
