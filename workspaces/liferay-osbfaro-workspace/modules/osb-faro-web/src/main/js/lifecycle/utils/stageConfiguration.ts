import {LifecycleStages} from '../../contacts/pages/account/utils/constants';

export const LIFECYCLE_STAGE_ORDER: LifecycleStages[] = [
	LifecycleStages.AWARE,
	LifecycleStages.ENGAGED,
	LifecycleStages.PIPELINE,
	LifecycleStages.ONBOARDING,
	LifecycleStages.ESTABLISHED,
	LifecycleStages.AT_RISK,
];

export const DEFAULT_MAX_DAYS = 90;

export const STAGE_DESCRIPTIONS: Record<LifecycleStages, string> = {
	[LifecycleStages.AT_RISK]: Liferay.Language.get(
		'accounts-with-decreasing-product-usage-or-signs-of-churn-risk.-action-is-required'
	),
	[LifecycleStages.AWARE]: Liferay.Language.get(
		'this-stage-identifies-cold-accounts-showing-early-intent-so-marketing-can-run-targeted-ads'
	),
	[LifecycleStages.ENGAGED]: Liferay.Language.get(
		'the-buying-committee-is-researching-us.-this-triggers-warm-call-alerts-to-sales'
	),
	[LifecycleStages.ESTABLISHED]: Liferay.Language.get(
		'the-account-is-healthy-and-realizing-roi.-it-is-safe-to-pitch-expansion-or-add-ons'
	),
	[LifecycleStages.ONBOARDING]: Liferay.Language.get(
		'the-contract-is-signed.-a-90-day-use-clock-starts-to-ensure-the-software-or-machinery-is-used'
	),
	[LifecycleStages.PIPELINE]: Liferay.Language.get(
		'this-is-an-active-deal.-the-stage-automatically-halts-generic-marketing-spend-so-sales-can-work-the-account'
	),
};

export interface IEntityOption {
	label: string;
	value: string;
}

export interface IStageConfig {
	conditionValue: string | null;
	description: string;
	field: string | null;
	fieldDataCategory: string | null;
	fieldDataType: string | null;
	id: string | null;
	maxTimeDays: number;
	maxTimeEnabled: boolean;
	operator: string | null;
}

export const createDefaultStageConfigs = (): IStageConfig[] =>
	LIFECYCLE_STAGE_ORDER.map((stageType) => ({
		conditionValue: null,
		description: STAGE_DESCRIPTIONS[stageType],
		field: null,
		fieldDataCategory: null,
		fieldDataType: null,
		id: null,
		maxTimeDays: DEFAULT_MAX_DAYS,
		maxTimeEnabled: true,
		operator: null,
	}));
