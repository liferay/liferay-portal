import {
	IStageConfig,
	LIFECYCLE_STAGE_ORDER,
} from 'lifecycle/utils/stageConfiguration';
import {
	Operator,
	OperatorType,
	resolveOperatorType,
} from 'lifecycle/utils/lifecycleOperators';

export interface IStageSegmentPayload {
	filter: string;
	filterMetadata: string;
}

export interface IStagePayload {
	description: string;
	displayOrder: number;
	maxDuration: number | null;
	segment: IStageSegmentPayload;
	stageType: string;
}

export interface ICreateLifecyclePayload {
	channelId: string;
	groupId: string;
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
	stages: stageConfigs.map((stage, index) => ({
		description: stage.description,
		displayOrder: index + 1,
		maxDuration: stage.maxTimeEnabled ? stage.maxTimeDays : null,
		segment: {
			filter: buildStageFilter(stage),
			filterMetadata: buildStageFilterMetadata(stage),
		},
		stageType: LIFECYCLE_STAGE_ORDER[index],
	})),
});
