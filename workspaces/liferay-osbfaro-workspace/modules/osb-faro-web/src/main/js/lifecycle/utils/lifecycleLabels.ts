import {getPluralMessage} from 'shared/util/lang';

export const getStageCountLabel = (count: number) =>
	getPluralMessage(
		Liferay.Language.get('x-stage'),
		Liferay.Language.get('x-stages'),
		count,
		true
	) as string;

export const getMaxTimeInStageLabel = (maxDuration: number | null) => {
	if (maxDuration == null) {
		return Liferay.Language.get('disabled');
	}

	return getPluralMessage(
		Liferay.Language.get('enabled-x-day'),
		Liferay.Language.get('enabled-x-days'),
		maxDuration,
		true
	) as string;
};
