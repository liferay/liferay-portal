import {
	createStageCondition,
	createStageKey,
	IStageCondition,
	IStageConfig,
	MatchLogic,
} from 'lifecycle/utils/stageConfiguration';
import {LIFECYCLE_STAGE_DEFAULT_MAX_DAYS} from 'lifecycle/utils/constants';
import {ILifecycleStage} from 'shared/api/lifecycle';
import {
	Operator,
	OperatorType,
	resolveOperatorType,
} from 'lifecycle/utils/lifecycleOperators';
import {sortBy} from 'lodash';

export interface IStageRulePayload {
	filterMetadata: string;
	filterString: string;
	name: string;
}

export interface IStagePayload {
	accountLifecycleStageRule: IStageRulePayload;
	description: string;
	id?: string;
	maxDuration: number | null;
	priority: number;
	stageName: string;
}

export interface ICreateLifecyclePayload {
	channelId: string;
	description: string;
	groupId: string;
	name: string;
	stages: IStagePayload[];
}

export interface IUpdateLifecyclePayload {
	description: string;
	groupId: string;
	lifecycleId: string;
	name: string;
	stages: IStagePayload[];
}

const quote = (value: string) => `'${value.replace(/'/g, "''")}'`;

type ValueExpressionBuilder = (field: string, literal: string) => string;

const VALUE_EXPRESSION_BY_OPERATOR: Partial<
	Record<Operator, ValueExpressionBuilder>
> = {
	[Operator.After]: (field, literal) => `${field} gt ${literal}`,
	[Operator.Before]: (field, literal) => `${field} lt ${literal}`,
	[Operator.Contains]: (field, literal) => `contains(${field}, ${literal})`,
	[Operator.DoesNotContain]: (field, literal) =>
		`not contains(${field}, ${literal})`,
	[Operator.Equals]: (field, literal) => `${field} eq ${literal}`,
	[Operator.GreaterThan]: (field, literal) => `${field} gt ${literal}`,
	[Operator.Is]: (field, literal) => `${field} eq ${literal}`,
	[Operator.IsNot]: (field, literal) => `${field} ne ${literal}`,
	[Operator.LessThan]: (field, literal) => `${field} lt ${literal}`,
	[Operator.NotEquals]: (field, literal) => `${field} ne ${literal}`,
	[Operator.On]: (field, literal) => `${field} eq ${literal}`,
};

const buildExpression = (condition: IStageCondition): string => {
	const {conditionValue, field, fieldDataCategory, fieldDataType, operator} =
		condition;

	if (operator === Operator.IsKnown) {
		return `${field} ne null`;
	}

	if (operator === Operator.IsUnknown) {
		return `${field} eq null`;
	}

	if (operator === Operator.True || operator === Operator.False) {
		return `${field} eq '${operator}'`;
	}

	const buildValueExpression =
		VALUE_EXPRESSION_BY_OPERATOR[operator as Operator];

	if (!buildValueExpression) {
		return '';
	}

	const type = resolveOperatorType(fieldDataCategory, fieldDataType);

	const raw = conditionValue ?? '';

	const isNumeric =
		type === OperatorType.Number || type === OperatorType.Duration;

	if (isNumeric && (raw.trim() === '' || !Number.isFinite(Number(raw)))) {
		return '';
	}

	return buildValueExpression(field!, isNumeric ? raw.trim() : quote(raw));
};

const buildConditionFilter = (condition: IStageCondition): string => {
	if (!condition.field || !condition.operator) {
		return '';
	}

	const expression = buildExpression(condition);

	return expression ? `(${expression})` : '';
};

export const buildStageFilter = (stage: IStageConfig): string => {
	const expressions = stage.conditions
		.map(buildConditionFilter)
		.filter(Boolean);

	if (expressions.length < 2) {
		return expressions[0] ?? '';
	}

	const separator = stage.matchLogic === MatchLogic.Any ? ' or ' : ' and ';

	return `(${expressions.join(separator)})`;
};

export const buildStageFilterMetadata = (stage: IStageConfig): string =>
	JSON.stringify({
		conditions: stage.conditions.map((condition) => ({
			conditionValue: condition.conditionValue,
			field: condition.field,
			fieldDataCategory: condition.fieldDataCategory,
			fieldDataType: condition.fieldDataType,
			operator: condition.operator,
		})),
		matchLogic: stage.matchLogic,
	});

export const buildStageRuleName = (lifecycleName: string, stageName: string) =>
	`${lifecycleName} Stage ${stageName} Criteria`;

const buildStagePayload = (
	stage: IStageConfig,
	index: number,
	lifecycleName: string
): IStagePayload => ({
	accountLifecycleStageRule: {
		filterMetadata: buildStageFilterMetadata(stage),
		filterString: buildStageFilter(stage),
		name: buildStageRuleName(lifecycleName, stage.name),
	},
	description: stage.description,
	...(stage.id ? {id: stage.id} : {}),
	maxDuration: stage.maxTimeEnabled ? stage.maxTimeDays : null,
	priority: index + 1,
	stageName: stage.name,
});

const buildStagePayloads = (stageConfigs: IStageConfig[], name: string) =>
	stageConfigs.map((stage, index) => buildStagePayload(stage, index, name));

export const buildCreateLifecyclePayload = ({
	channelId,
	description,
	groupId,
	name,
	stageConfigs,
}: {
	channelId: string;
	description: string;
	groupId: string;
	name: string;
	stageConfigs: IStageConfig[];
}): ICreateLifecyclePayload => ({
	channelId,
	description,
	groupId,
	name,
	stages: buildStagePayloads(stageConfigs, name),
});

export const buildUpdateLifecyclePayload = ({
	description,
	groupId,
	lifecycleId,
	name,
	stageConfigs,
}: {
	description: string;
	groupId: string;
	lifecycleId: string;
	name: string;
	stageConfigs: IStageConfig[];
}): IUpdateLifecyclePayload => ({
	description,
	groupId,
	lifecycleId,
	name,
	stages: buildStagePayloads(stageConfigs, name),
});

interface IStageConditionMetadata {
	conditionValue?: string | null;
	field?: string | null;
	fieldDataCategory?: string | null;
	fieldDataType?: string | null;
	operator?: string | null;
}

interface IStageFilterMetadata extends IStageConditionMetadata {
	conditions?: IStageConditionMetadata[];
	matchLogic?: string | null;
}

const parseFilterMetadata = (
	filterMetadata?: string | null
): IStageFilterMetadata => {
	if (!filterMetadata) {
		return {};
	}

	try {
		return JSON.parse(filterMetadata) as IStageFilterMetadata;
	}
	catch {
		return {};
	}
};

const toStageCondition = (
	metadata: IStageConditionMetadata
): IStageCondition => ({
	...createStageCondition(),
	conditionValue: metadata.conditionValue ?? null,
	field: metadata.field ?? null,
	fieldDataCategory: metadata.fieldDataCategory ?? null,
	fieldDataType: metadata.fieldDataType ?? null,
	operator: metadata.operator ?? null,
});

const conditionsFromMetadata = (
	metadata: IStageFilterMetadata
): IStageCondition[] => {
	if (metadata.conditions?.length) {
		return metadata.conditions.map(toStageCondition);
	}

	return [toStageCondition(metadata)];
};

const matchLogicFromMetadata = (metadata: IStageFilterMetadata): MatchLogic =>
	metadata.matchLogic === MatchLogic.Any ? MatchLogic.Any : MatchLogic.All;

export const stageRuleFromStage = (
	stage: ILifecycleStage
): {conditions: IStageCondition[]; matchLogic: MatchLogic} => {
	const metadata = parseFilterMetadata(
		stage.accountLifecycleStageRule?.filterMetadata
	);

	return {
		conditions: conditionsFromMetadata(metadata),
		matchLogic: matchLogicFromMetadata(metadata),
	};
};

const stageConfigFromStage = (stage: ILifecycleStage): IStageConfig => ({
	...stageRuleFromStage(stage),
	description: stage.description ?? '',
	id: stage.id,
	key: createStageKey(),
	maxTimeDays: stage.maxDuration ?? LIFECYCLE_STAGE_DEFAULT_MAX_DAYS,
	maxTimeEnabled: stage.maxDuration != null,
	name: stage.stageName ?? '',
});

export const stageConfigsFromLifecycle = (
	stages: ILifecycleStage[] = []
): IStageConfig[] => sortBy(stages, 'priority').map(stageConfigFromStage);
