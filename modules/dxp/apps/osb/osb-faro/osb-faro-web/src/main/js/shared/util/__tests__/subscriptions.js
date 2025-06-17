import {
	formatPlanData,
	getPlanAddOns,
	getPropIcon,
	getPropLabel,
	INDIVIDUALS,
	PAGEVIEWS
} from '../subscriptions';
import {fromJS} from 'immutable';
import {mockSubscription} from 'test/data';
import {Plan} from '../../util/records';

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({
		timeZoneId: 'UTC'
	})
}));

describe('subscriptions', () => {
	describe('getPlanAddOns', () => {
		it('should return the correct plan addons', () => {
			const planAddOns = getPlanAddOns(
				formatPlanData(
					fromJS(
						mockSubscription({
							individualsCount: 5000,
							name: 'Liferay Analytics Cloud Enterprise',
							pageViewsCount: 5000000
						})
					)
				)
			);

			expect(planAddOns).toEqual({
				individuals: '10,000',
				pageViews: '5,000,000'
			});
		});

		it('should not have addons for LXC customers', () => {
			const planAddOns = getPlanAddOns(
				formatPlanData(
					fromJS(
						mockSubscription({
							individualsCount: 5000,
							name: 'LXC Subscription - Engage Site',
							pageViewsCount: 5000000
						})
					)
				)
			);

			expect(planAddOns).toEqual({});
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
						name: 'Liferay Analytics Cloud Basic'
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
});
