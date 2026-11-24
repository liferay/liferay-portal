import {CustomFunctionOperators, NotOperators} from './constants';
import {Criterion, CriterionGroup} from './types';
import {EntityType, ReferencedEntities} from '../context/referencedObjects';
import {getCampaignIds} from './custom-inputs';
import {isCriterionGroup} from './utils';

const CAMPAIGN_OPERATORS: string[] = [
	CustomFunctionOperators.CampaignsFilter,
	NotOperators.NotCampaignsFilter,
];

export const isCampaignCriterion = (criterion: Criterion): boolean =>
	CAMPAIGN_OPERATORS.includes(String(criterion.operatorName));

/**
 * The segment's referenced objects hold every campaign that still exists, so a
 * campaign missing from them was deleted by the campaign sync.
 */
export const getUnavailableCampaignIds = (
	criterion: Criterion,
	referencedEntities: ReferencedEntities
): string[] =>
	isCampaignCriterion(criterion) && criterion.value
		? getCampaignIds(criterion.value).filter(
				(id) => !referencedEntities.hasIn([EntityType.Campaigns, id])
			)
		: [];

export const hasUnavailableCampaign = (
	criteria: Criterion | CriterionGroup | null | undefined,
	referencedEntities: ReferencedEntities
): boolean => {
	if (!criteria) {
		return false;
	}

	if (isCriterionGroup(criteria)) {
		return criteria.items.some((item) =>
			hasUnavailableCampaign(item, referencedEntities)
		);
	}

	return getUnavailableCampaignIds(criteria, referencedEntities).length > 0;
};
