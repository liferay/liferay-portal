export const DEFAULT_MAX_DAYS = 90;

export enum MatchLogic {
	All = 'all',
	Any = 'any',
}

export interface IEntityOption {
	label: string;
	value: string;
}

export interface IStageCondition {
	conditionValue: string | null;
	field: string | null;
	fieldDataCategory: string | null;
	fieldDataType: string | null;
	key: string;
	operator: string | null;
}

export interface IStageConfig {
	conditions: IStageCondition[];
	description: string;
	id: string | null;
	key: string;
	matchLogic: MatchLogic;
	maxTimeDays: number;
	maxTimeEnabled: boolean;
	name: string;
}

let conditionCount = 0;

let stageCount = 0;

export const createStageCondition = (): IStageCondition => ({
	conditionValue: null,
	field: null,
	fieldDataCategory: null,
	fieldDataType: null,
	key: `condition-${++conditionCount}`,
	operator: null,
});

export const createStageKey = () => `stage-${++stageCount}`;

export const createStageConfig = (): IStageConfig => ({
	conditions: [createStageCondition()],
	description: '',
	id: null,
	key: createStageKey(),
	matchLogic: MatchLogic.All,
	maxTimeDays: DEFAULT_MAX_DAYS,
	maxTimeEnabled: true,
	name: '',
});
