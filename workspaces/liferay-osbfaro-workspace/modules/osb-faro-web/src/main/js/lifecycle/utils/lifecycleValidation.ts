import {
	isConditionComplete,
	isStageConfigured,
} from 'lifecycle/utils/lifecycleOperators';
import {IStageConfig} from 'lifecycle/utils/stageConfiguration';

export const isAtCharacterLimit = (value: string, maxLength: number) =>
	value.length >= maxLength;

const normalizeStageName = (name: string) =>
	name.trim().replace(/\s+/g, ' ').toLowerCase();

export const getDuplicateStageKeys = (stageConfigs: IStageConfig[]) => {
	const keysByName = new Map<string, string[]>();

	stageConfigs.forEach(({key, name}) => {
		const normalizedName = normalizeStageName(name);

		if (normalizedName) {
			keysByName.set(normalizedName, [
				...(keysByName.get(normalizedName) ?? []),
				key,
			]);
		}
	});

	return Array.from(keysByName.values())
		.filter((keys) => keys.length > 1)
		.flat();
};

export interface IStageErrors {
	description: boolean;
	incompleteTrigger: boolean;
	name: boolean;
	trigger: boolean;
}

export const getStageErrors = (stage: IStageConfig): IStageErrors => ({
	description: !stage.description.trim(),
	incompleteTrigger: stage.conditions.some(
		(condition) => !!condition.field && !isConditionComplete(condition)
	),
	name: !stage.name.trim(),
	trigger: !isStageConfigured(stage),
});

export const hasStageErrors = (stage: IStageConfig) =>
	Object.values(getStageErrors(stage)).some(Boolean);

export const hasStageChipError = (stage: IStageConfig) =>
	getStageErrors(stage).name;

const countStages = (stageConfigs: IStageConfig[], error: keyof IStageErrors) =>
	stageConfigs.filter((stage) => getStageErrors(stage)[error]).length;

const pickMessage = (count: number, singular: string, plural: string) => {
	if (!count) {
		return [];
	}

	return [count === 1 ? singular : plural];
};

const getUniqueNameMessages = (duplicate: boolean) =>
	duplicate
		? [
				Liferay.Language.get(
					'stage-names-should-be-unique.-please-try-a-different-one'
				),
			]
		: [];

export const getStageErrorMessages = (
	stage: IStageConfig,
	duplicateName = false
): string[] => {
	const errors = getStageErrors(stage);

	return [
		...(errors.name
			? [
					Liferay.Language.get(
						'the-stage-name-field-is-required-and-must-be-filled-out'
					),
				]
			: []),
		...getUniqueNameMessages(duplicateName),
		...(errors.description
			? [
					Liferay.Language.get(
						'the-stage-description-field-is-required-and-must-be-filled-out'
					),
				]
			: []),
		...(errors.trigger
			? [
					Liferay.Language.get(
						'stage-triggers-are-required-and-must-be-filled-out'
					),
				]
			: []),
		...(errors.incompleteTrigger
			? [
					Liferay.Language.get(
						'incomplete-stage-triggers-must-be-completed-or-removed'
					),
				]
			: []),
	];
};

export const getLifecycleErrorMessages = ({
	description,
	name,
	stageConfigs,
}: {
	description: string;
	name: string;
	stageConfigs: IStageConfig[];
}): string[] => [
	...(name.trim()
		? []
		: [
				Liferay.Language.get(
					'the-lifecycle-name-field-is-required-and-must-be-filled-out'
				),
			]),
	...(description.trim()
		? []
		: [
				Liferay.Language.get(
					'the-lifecycle-description-field-is-required-and-must-be-filled-out'
				),
			]),
	...(stageConfigs.length
		? []
		: [Liferay.Language.get('add-at-least-one-stage')]),
	...pickMessage(
		countStages(stageConfigs, 'name'),
		Liferay.Language.get(
			'the-stage-name-field-is-required-and-must-be-filled-out'
		),
		Liferay.Language.get(
			'stage-name-fields-are-required-and-must-be-filled-out'
		)
	),
	...getUniqueNameMessages(!!getDuplicateStageKeys(stageConfigs).length),
	...pickMessage(
		countStages(stageConfigs, 'description'),
		Liferay.Language.get(
			'the-stage-description-field-is-required-and-must-be-filled-out'
		),
		Liferay.Language.get(
			'stage-description-fields-are-required-and-must-be-filled-out'
		)
	),
	...pickMessage(
		countStages(stageConfigs, 'trigger'),
		Liferay.Language.get(
			'the-stage-trigger-is-required-and-must-be-filled-out'
		),
		Liferay.Language.get(
			'stage-triggers-are-required-and-must-be-filled-out'
		)
	),
	...(countStages(stageConfigs, 'incompleteTrigger')
		? [
				Liferay.Language.get(
					'incomplete-stage-triggers-must-be-completed-or-removed'
				),
			]
		: []),
];
