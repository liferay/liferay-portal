import {
	formatPlanData,
	getPlanAddOns,
	getPlanLabel,
	getPropIcon,
	getPropLabel,
	INDIVIDUALS,
	isLDPPlan,
	PAGEVIEWS,
	SubscriptionNames,
} from '../subscriptions';
import {fromJS} from 'immutable';
import {mockSubscription} from 'test/data';
import {Plan} from '../../util/records';

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({
		timeZoneId: 'UTC',
	}),
}));

describe('subscriptions', () => {
	describe('getPlanAddOns', () => {
		it('should return the correct plan addons', () => {
			const planAddOns = getPlanAddOns(
				formatPlanData(
					fromJS(
						mockSubscription({
							individualsCount: 5000,
							name: SubscriptionNames.LiferayAnalyticsCloudEnterprise,
							pageViewsCount: 5000000,
						})
					)
				)
			);

			expect(planAddOns).toEqual({
				individuals: '10,000',
				pageViews: '5,000,000',
			});
		});

		it('should not have addons for LXC customers', () => {
			const planAddOns = getPlanAddOns(
				formatPlanData(
					fromJS(
						mockSubscription({
							individualsCount: 5000,
							name: SubscriptionNames.LxcSubscriptionEngageSite,
							pageViewsCount: 5000000,
						})
					)
				)
			);

			expect(planAddOns).toEqual({});
		});
	});

	describe('getPlanLabel', () => {
		it('should return the label for the Liferay Data Platform plan', () => {
			expect(getPlanLabel(SubscriptionNames.LiferayDataPlatform)).toEqual(
				'Liferay Data Platform'
			);
		});

		it('should return the label for the Liferay Data Platform Private Beta plan', () => {
			expect(
				getPlanLabel(SubscriptionNames.LiferayDataPlatformPrivateBeta)
			).toEqual('Liferay Data Platform (Private Beta)');
		});

		it('should return the label for the Liferay Data Platform Enterprise plan', () => {
			expect(
				getPlanLabel(SubscriptionNames.LiferayDataPlatformEnterprise)
			).toEqual('Liferay Data Platform Enterprise');
		});

		it('should return the label for the Liferay Digital Sales Room plan', () => {
			expect(
				getPlanLabel(SubscriptionNames.LiferayDigitalSalesRoom)
			).toEqual('Liferay Digital Sales Room');
		});

		it('should return an empty string for an unknown plan', () => {
			expect(getPlanLabel('something-unknown')).toEqual('');
		});
	});

	describe('getPropIcon', () => {
		it('should return the prop icon symbol', () => {
			const symbol = getPropIcon(INDIVIDUALS);

			expect(symbol).toEqual('ac_individual');
		});
	});

	describe('getPropLabel', () => {
		it('should return the correct prop label', () => {
			const label = getPropLabel(PAGEVIEWS);

			expect(label).toEqual('Page Views');
		});
	});

	describe('formatPlanData', () => {
		it('should format the plan data as a basic Plan record', () => {
			const plan = formatPlanData(
				fromJS(
					mockSubscription({
						name: SubscriptionNames.LiferayAnalyticsCloudBasic,
					})
				)
			);

			expect(plan).toBeInstanceOf(Plan);

			const metrics = plan.metrics;

			const individualsMetrics = metrics.get('individuals');

			expect(individualsMetrics.count).toEqual(2057);

			const pageViewsMetrics = metrics.get('pageViews');

			expect(pageViewsMetrics.count).toEqual(100023);
		});

		it('should format the plan data as an enterprise Plan record', () => {
			const plan = formatPlanData(fromJS(mockSubscription()));

			expect(plan).toBeInstanceOf(Plan);

			const metrics = plan.metrics;

			const individualsMetrics = metrics.get('individuals');

			expect(individualsMetrics.count).toEqual(2057);

			const pageViewsMetrics = metrics.get('pageViews');

			expect(pageViewsMetrics.count).toEqual(100023);
		});

		it('should format the plan data when faroSusbcription is null', () => {
			const plan = formatPlanData(null);

			expect(plan).toMatchSnapshot();
		});
	});

	describe('isLDPPlan', () => {
		it.each([
			SubscriptionNames.LiferayDataPlatform,
			SubscriptionNames.LiferayDataPlatformEnterprise,
			SubscriptionNames.LiferayDataPlatformPrivateBeta,
		])('returns true for %s', (name) => {
			expect(isLDPPlan(name)).toBe(true);
		});

		it.each([
			SubscriptionNames.LiferayAnalyticsCloudBasic,
			SubscriptionNames.LiferayAnalyticsCloudBusiness,
			SubscriptionNames.LiferayAnalyticsCloudEnterprise,
			SubscriptionNames.LiferaySaasEnterprisePlan,
			SubscriptionNames.LxcBusinessPlan,
		])('returns false for non-LDP plan %s', (name) => {
			expect(isLDPPlan(name)).toBe(false);
		});

		it('returns false when the subscription is missing (null/undefined)', () => {
			expect(isLDPPlan(null)).toBe(false);
			expect(isLDPPlan(undefined)).toBe(false);
		});
	});
});
