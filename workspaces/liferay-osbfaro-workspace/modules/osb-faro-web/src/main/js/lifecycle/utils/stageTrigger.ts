import {
	conditionOperatorOptions,
	CONNECTOR_LABEL_BY_MATCH_LOGIC,
	VALUELESS_OPERATORS,
} from 'lifecycle/utils/lifecycleOperators';
import {ILifecycleStage} from 'shared/api/lifecycle';
import {IStageCondition} from 'lifecycle/utils/stageConfiguration';
import {stageRuleFromStage} from 'lifecycle/utils/lifecyclePayload';

export interface IStageTriggerCondition {
	field: string;
	key: string;
	operator: string;
	value: string | null;
}

export interface IStageTrigger {
	conditions: IStageTriggerCondition[];
	connector: string;
}

const getOperatorLabel = (condition: IStageCondition) =>
	conditionOperatorOptions(condition).find(
		({value}) => value === condition.operator
	)?.label ?? condition.operator!;

const toTriggerCondition = (
	condition: IStageCondition
): IStageTriggerCondition => ({
	field: condition.field!,
	key: condition.key,
	operator: getOperatorLabel(condition),
	value: VALUELESS_OPERATORS.has(condition.operator!)
		? null
		: condition.conditionValue,
});

export const getStageTrigger = (stage: ILifecycleStage): IStageTrigger => {
	const {conditions, matchLogic} = stageRuleFromStage(stage);

	return {
		conditions: conditions
			.filter(({field, operator}) => field && operator)
			.map(toTriggerCondition),
		connector: CONNECTOR_LABEL_BY_MATCH_LOGIC[matchLogic],
	};
};
