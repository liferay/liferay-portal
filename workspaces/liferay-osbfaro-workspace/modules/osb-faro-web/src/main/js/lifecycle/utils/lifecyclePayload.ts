import {
	createDefaultStageConfigs,
	DEFAULT_MAX_DAYS,
	IStageConfig,
	LIFECYCLE_STAGE_ORDER,
} from 'lifecycle/utils/stageConfiguration';
import {ILifecycleStage} from 'shared/api/lifecycle';
import {
	Operator,
	OperatorType,
	resolveOperatorType,
} from 'lifecycle/utils/lifecycleOperators';

export interface IStageRulePayload {
	filterMetadata: string;
	filterString: string;
	name: string;
}

export interface IStagePayload {
	accountLifecycleStageRule: IStageRulePayload;
	description: string;
	displayOrder: number;
	id?: string;
	maxDuration: number | null;
	stageType: string;
}

export interface ICreateLifecyclePayload {
	channelId: string;
	groupId: string;
	name: string;
	stages: IStagePayload[];
}

export interface IUpdateLifecyclePayload {
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

const buildExpression = (stage: IStageConfig): string => {
	const {conditionValue, field, operator} = stage;

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

	const type = resolveOperatorType(
		stage.fieldDataCategory,
		stage.fieldDataType
	);

	const raw = conditionValue ?? '';

	const isNumeric =
		type === OperatorType.Number || type === OperatorType.Duration;

	if (isNumeric && (raw.trim() === '' || !Number.isFinite(Number(raw)))) {
		return '';
	}

	return buildValueExpression(field!, isNumeric ? raw.trim() : quote(raw));
};

export const buildStageFilter = (stage: IStageConfig): string => {
	if (!stage.field || !stage.operator) {
		return '';
	}

	const expression = buildExpression(stage);

	return expression ? `(${expression})` : '';
};

export const buildStageFilterMetadata = (stage: IStageConfig): string =>
	JSON.stringify({
		conditionValue: stage.conditionValue,
		field: stage.field,
		fieldDataCategory: stage.fieldDataCategory,
		fieldDataType: stage.fieldDataType,
		operator: stage.operator,
	});

export const buildStageRuleName = (lifecycleName: string, stageType: string) =>
	`${lifecycleName} Stage ${stageType} Criteria`;

const buildStagePayload = (
	stage: IStageConfig,
	index: number,
	lifecycleName: string
): IStagePayload => ({
	accountLifecycleStageRule: {
		filterMetadata: buildStageFilterMetadata(stage),
		filterString: buildStageFilter(stage),
		name: buildStageRuleName(lifecycleName, LIFECYCLE_STAGE_ORDER[index]),
	},
	description: stage.description,
	displayOrder: index + 1,
	maxDuration: stage.maxTimeEnabled ? stage.maxTimeDays : null,
	stageType: LIFECYCLE_STAGE_ORDER[index],
});

export const buildCreateLifecyclePayload = ({
	channelId,
	groupId,
	name,
	stageConfigs,
}: {
	channelId: string;
	groupId: string;
	name: string;
	stageConfigs: IStageConfig[];
}): ICreateLifecyclePayload => ({
	channelId,
	groupId,
	name,
	stages: stageConfigs.map((stage, index) =>
		buildStagePayload(stage, index, name)
	),
});

export const buildUpdateLifecyclePayload = ({
	groupId,
	lifecycleId,
	name,
	stageConfigs,
}: {
	groupId: string;
	lifecycleId: string;
	name: string;
	stageConfigs: IStageConfig[];
}): IUpdateLifecyclePayload => ({
	groupId,
	lifecycleId,
	name,
	stages: stageConfigs.map((stage, index) => ({
		...buildStagePayload(stage, index, name),
		...(stage.id ? {id: stage.id} : {}),
	})),
});

interface IStageFilterMetadata {
	conditionValue?: string | null;
	field?: string | null;
	fieldDataCategory?: string | null;
	fieldDataType?: string | null;
	operator?: string | null;
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

export const stageConfigsFromLifecycle = (
	stages: ILifecycleStage[] = []
): IStageConfig[] => {
	const defaults = createDefaultStageConfigs();

	return LIFECYCLE_STAGE_ORDER.map((stageType, index) => {
		const stage = stages.find((current) => current.stageType === stageType);

		if (!stage) {
			return defaults[index];
		}

		const metadata = parseFilterMetadata(
			stage.accountLifecycleStageRule?.filterMetadata
		);

		return {
			conditionValue: metadata.conditionValue ?? null,
			description: stage.description || defaults[index].description,
			field: metadata.field ?? null,
			fieldDataCategory: metadata.fieldDataCategory ?? null,
			fieldDataType: metadata.fieldDataType ?? null,
			id: stage.id,
			maxTimeDays: stage.maxDuration ?? DEFAULT_MAX_DAYS,
			maxTimeEnabled: stage.maxDuration != null,
			operator: metadata.operator ?? null,
		};
	});
};
