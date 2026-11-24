import {CAMPAIGN_PROPERTY} from '../properties';
import {fromJS, List} from 'immutable';
import {getDefaultValue} from '../../criteria-sidebar/CriteriaSidebarCollapse';
import {
	getUnavailableCampaignIds,
	hasUnavailableCampaign,
	isCampaignCriterion,
} from '../campaigns';

const referencedEntities = fromJS({
	campaigns: {c1: {campaignName: 'Spring Sale', id: 'c1'}},
});

const createCampaignCriterion = (ids, operatorName = 'campaigns-filter') => ({
	operatorName,
	propertyName: 'id',
	value: getDefaultValue(CAMPAIGN_PROPERTY).setIn(
		['criterionGroup', 'items', 0, 'value'],
		List(ids)
	),
});

describe('campaigns', () => {
	it.each(['campaigns-filter', 'not-campaigns-filter'])(
		'should recognize a campaign criterion compared with %s',
		(operatorName) => {
			expect(
				isCampaignCriterion(
					createCampaignCriterion(['c1'], operatorName)
				)
			).toBe(true);
		}
	);

	it('should not take another criterion for a campaign one', () => {
		expect(
			isCampaignCriterion({operatorName: 'eq', propertyName: 'email'})
		).toBe(false);
	});

	it('should return the campaigns missing from the referenced objects', () => {
		expect(
			getUnavailableCampaignIds(
				createCampaignCriterion(['c1', 'deleted']),
				referencedEntities
			)
		).toEqual(['deleted']);
	});

	it('should treat every campaign as missing when none is referenced', () => {
		expect(
			getUnavailableCampaignIds(
				createCampaignCriterion(['c1']),
				fromJS({})
			)
		).toEqual(['c1']);
	});

	it('should find a missing campaign nested in a criteria group', () => {
		const criteria = {
			conjunctionName: 'and',
			criteriaGroupId: 'group_0',
			items: [
				{operatorName: 'eq', propertyName: 'email', value: 'a@b.com'},
				{
					conjunctionName: 'or',
					criteriaGroupId: 'group_1',
					items: [createCampaignCriterion(['deleted'])],
				},
			],
		};

		expect(hasUnavailableCampaign(criteria, referencedEntities)).toBe(true);
	});

	it('should not flag criteria whose campaigns all exist', () => {
		const criteria = {
			conjunctionName: 'and',
			criteriaGroupId: 'group_0',
			items: [createCampaignCriterion(['c1'])],
		};

		expect(hasUnavailableCampaign(criteria, referencedEntities)).toBe(
			false
		);
	});
});
