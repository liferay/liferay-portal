import Constants, {SubscriptionStatuses} from 'shared/util/constants';
import {fromJS, List, Map} from 'immutable';
import {isNil} from 'lodash';
import {Metric, Plan} from 'shared/util/records';

export const INDIVIDUALS = 'individuals';

export const PAGEVIEWS = 'pageViews';

export const PLAN_TYPES = {
	['Liferay Analytics Cloud Basic']: 'basic',
	['Liferay Analytics Cloud Business']: 'business',
	['Liferay Analytics Cloud Business Contacts']: INDIVIDUALS,
	['Liferay Analytics Cloud Business Tracked Pages']: PAGEVIEWS,
	['Liferay Analytics Cloud Enterprise']: 'enterprise',
	['Liferay Analytics Cloud Enterprise Contacts']: INDIVIDUALS,
	['Liferay Analytics Cloud Enterprise Tracked Pages']: PAGEVIEWS,
	['Liferay SaaS - Business Plan']: 'lxcBusiness',
	['Liferay SaaS - CSP - Custom User Tier']: 'lxcCspCustomUserTier',
	['Liferay SaaS - CSP - Custom User Tier - Extra User']:
		'lxcCspCustomUserTierExtraUser',
	['Liferay SaaS - CSP - Up to 100 Users']: 'lxcCspUpTo100Users',
	['Liferay SaaS - CSP - Up to 100 Users - Extra User']:
		'lxcCspUpTo100UsersExtraUser',
	['Liferay SaaS - CSP - Up to 10K Users']: 'lxcCspUpTo10kUsers',
	['Liferay SaaS - CSP - Up to 10K Users - Extra User']:
		'lxcCspUpTo10kUsersExtraUser',
	['Liferay SaaS - CSP - Up to 1K Users']: 'lxcCspUpTo1kUsers',
	['Liferay SaaS - CSP - Up to 1K Users - Extra User']:
		'lxcCspUpTo1kUsersExtraUser',
	['Liferay SaaS - CSP - Up to 20K Users']: 'lxcCspUpTo20kUsers',
	['Liferay SaaS - CSP - Up to 20K Users - Extra User']:
		'lxcCspUpTo20kUsersExtraUser',
	['Liferay SaaS - CSP - Up to 500 Users']: 'lxcCspUpTo500Users',
	['Liferay SaaS - CSP - Up to 500 Users - Extra User']:
		'lxcCspUpTo500UsersExtraUser',
	['Liferay SaaS - CSP - Up to 5K Users']: 'lxcCspUpTo5kUsers',
	['Liferay SaaS - CSP - Up to 5K Users - Extra User']:
		'lxcCspUpTo5kUsersExtraUser',
	['Liferay SaaS - Enterprise Plan']: 'lxcEnterprise',
	['Liferay SaaS - Pro Plan']: 'lxcPro',
	['Liferay SaaS Subscription - Engage Site']: 'lxcSubscriptionEngageSite',
	['Liferay SaaS Subscription - Support Site']: 'lxcSubscriptionSupportSite',
	['Liferay SaaS Subscription - Transact Site']:
		'lxcSubscriptionTransactSite',
	['LXC - Business Plan']: 'lxcBusiness',
	['LXC - CSP - Custom User Tier']: 'lxcCspCustomUserTier',
	['LXC - CSP - Custom User Tier - Extra User']:
		'lxcCspCustomUserTierExtraUser',
	['LXC - CSP - Up to 100 Users']: 'lxcCspUpTo100Users',
	['LXC - CSP - Up to 100 Users - Extra User']: 'lxcCspUpTo100UsersExtraUser',
	['LXC - CSP - Up to 10K Users']: 'lxcCspUpTo10kUsers',
	['LXC - CSP - Up to 10K Users - Extra User']: 'lxcCspUpTo10kUsersExtraUser',
	['LXC - CSP - Up to 1K Users']: 'lxcCspUpTo1kUsers',
	['LXC - CSP - Up to 1K Users - Extra User']: 'lxcCspUpTo1kUsersExtraUser',
	['LXC - CSP - Up to 20K Users']: 'lxcCspUpTo20kUsers',
	['LXC - CSP - Up to 20K Users - Extra User']: 'lxcCspUpTo20kUsersExtraUser',
	['LXC - CSP - Up to 500 Users']: 'lxcCspUpTo500Users',
	['LXC - CSP - Up to 500 Users - Extra User']: 'lxcCspUpTo500UsersExtraUser',
	['LXC - CSP - Up to 5K Users']: 'lxcCspUpTo5kUsers',
	['LXC - CSP - Up to 5K Users - Extra User']: 'lxcCspUpTo5kUsersExtraUser',
	['LXC - Enterprise Plan']: 'lxcEnterprise',
	['LXC - Pro Plan']: 'lxcPro',
	['LXC Subscription - Engage Site']: 'lxcSubscriptionEngageSite',
	['LXC Subscription - Support Site']: 'lxcSubscriptionSupportSite',
	['LXC Subscription - Transact Site']: 'lxcSubscriptionTransactSite'
};

function formatSubscriptions(allPlans) {
	const ADD_ONS = {
		[INDIVIDUALS]: {},
		['lxcCspUpTo100UsersExtraUser']: {},
		['lxcCspUpTo10kUsersExtraUser']: {},
		['lxcCspUpTo1kUsers']: {},
		['lxcCspUpTo1kUsersExtraUser']: {},
		['lxcCspUpTo20kUsers']: {},
		['lxcCspUpTo20kUsersExtraUser']: {},
		['lxcCspUpTo500UsersExtraUser']: {},
		['lxcCspUpTo5kUsersExtraUser']: {},
		['lxcSubscriptionEngageSite']: {},
		['lxcSubscriptionSupportSite']: {},
		['lxcSubscriptionTransactSite']: {},
		[PAGEVIEWS]: {}
	};

	const PLANS = {};

	const hasKeyProperty = key =>
		Object.prototype.hasOwnProperty.call(allPlans, key);

	for (const key in allPlans) {
		if (hasKeyProperty(key)) {
			const {
				baseSubscriptionPlan,
				individualsLimit,
				name,
				pageViewsLimit
			} = allPlans[key];

			const planType = PLAN_TYPES[key];

			const formattedPlan = {
				baseSubscriptionPlan,
				limits: {
					[INDIVIDUALS]: individualsLimit,
					[PAGEVIEWS]: pageViewsLimit
				},
				name
			};

			const parentPlanType = PLAN_TYPES[baseSubscriptionPlan];

			if (baseSubscriptionPlan) {
				ADD_ONS[planType][parentPlanType] = formattedPlan;
			} else {
				PLANS[planType] = formattedPlan;
			}
		}
	}

	return {ADD_ONS, PLANS};
}

const {ADD_ONS, PLANS} = formatSubscriptions(Constants.subscriptionPlans);

export {ADD_ONS, PLANS};

export const STATUS_DISPLAY_MAP = {
	[SubscriptionStatuses.Ok]: 'primary',
	[SubscriptionStatuses.Approaching]: 'warning',
	[SubscriptionStatuses.Over]: 'danger'
};

export const DEFAULT_ADDONS = {
	[INDIVIDUALS]: ADD_ONS[INDIVIDUALS].business,
	[PAGEVIEWS]: ADD_ONS[PAGEVIEWS].business
};

export function getPlanAddOns(currentPlan) {
	if (isBasicPlan(currentPlan)) {
		return {};
	}

	const planType = PLAN_TYPES[currentPlan.name];

	return [ADD_ONS[INDIVIDUALS][planType], ADD_ONS[PAGEVIEWS][planType]]
		.filter(Boolean)
		.reduce((acc, plan) => {
			const name = PLAN_TYPES[plan.name];
			const quantity = currentPlan.getIn(['addOns', name, 'quantity']);
			const limit = plan.limits[name];
			const totalLimit = quantity ? limit * quantity : null;

			return {
				...acc,
				[name]: totalLimit ? totalLimit.toLocaleString() : '-'
			};
		}, {});
}

export function getPlanLabel(name) {
	switch (name) {
		case PLANS.basic.name:
			return Liferay.Language.get('basic-plan');

		case PLANS.business.name:
			return Liferay.Language.get('business-plan');

		case PLANS.enterprise.name:
			return Liferay.Language.get('enterprise-plan');

		case PLANS.lxcCspCustomUserTier.name:
			return Liferay.Language.get('lxc-csp-custom-user-tier');

		case PLANS.lxcCspUpTo100Users.name:
			return Liferay.Language.get('lxc-csp-up-to-100-user');

		case PLANS.lxcCspUpTo500Users.name:
			return Liferay.Language.get('lxc-csp-up-to-500-users');

		case PLANS.lxcCspUpTo1kUsers.name:
			return Liferay.Language.get('lxc-csp-up-to-1k-users');

		case PLANS.lxcCspUpTo5kUsers.name:
			return Liferay.Language.get('lxc-csp-up-to-5k-users');

		case PLANS.lxcCspUpTo10kUsers.name:
			return Liferay.Language.get('lxc-csp-up-to-10k-users');

		case PLANS.lxcCspUpTo20kUsers.name:
			return Liferay.Language.get('lxc-csp-up-to-20k-users');

		case PLANS.lxcSubscriptionEngageSite.name:
			return Liferay.Language.get('lxc-subscription-engage-site');

		case PLANS.lxcSubscriptionSupportSite.name:
			return Liferay.Language.get('lxc-subscription-support-site');

		case PLANS.lxcSubscriptionTransactSite.name:
			return Liferay.Language.get('lxc-subscription-transact-site');

		case PLANS.lxcPro.name:
			return Liferay.Language.get('basic-plan');

		case PLANS.lxcBusiness.name:
			return Liferay.Language.get('business-plan');

		case PLANS.lxcEnterprise.name:
			return Liferay.Language.get('enterprise-plan');

		default:
			return '';
	}
}

export function getPropIcon(name) {
	switch (name) {
		case INDIVIDUALS:
			return 'ac_individual';
		case PAGEVIEWS:
			return 'faro_page_views';
		default:
			return '';
	}
}

export function getPropLabel(name) {
	switch (name) {
		case INDIVIDUALS:
		case `${INDIVIDUALS}Limit`:
			return Liferay.Language.get('individuals');

		case PAGEVIEWS:
		case `${PAGEVIEWS}Limit`:
			return Liferay.Language.get('page-views');

		case PLANS.basic.name:
			return Liferay.Language.get('basic');

		case PLANS.business.name:
			return Liferay.Language.get('business');

		case PLANS.enterprise.name:
			return Liferay.Language.get('enterprise');

		case PLANS.lxcCspCustomUserTier.name:
			return Liferay.Language.get('lxc-csp-custom-user-tier');

		case PLANS.lxcCspUpTo100Users.name:
			return Liferay.Language.get('lxc-csp-up-to-100-user');

		case PLANS.lxcCspUpTo500Users.name:
			return Liferay.Language.get('lxc-csp-up-to-500-users');

		case PLANS.lxcCspUpTo1kUsers.name:
			return Liferay.Language.get('lxc-csp-up-to-1k-users');

		case PLANS.lxcCspUpTo5kUsers.name:
			return Liferay.Language.get('lxc-csp-up-to-5k-users');

		case PLANS.lxcCspUpTo10kUsers.name:
			return Liferay.Language.get('lxc-csp-up-to-10k-users');

		case PLANS.lxcCspUpTo20kUsers.name:
			return Liferay.Language.get('lxc-csp-up-to-20k-users');

		case PLANS.lxcSubscriptionEngageSite.name:
			return Liferay.Language.get('lxc-subscription-engage-site');

		case PLANS.lxcSubscriptionSupportSite.name:
			return Liferay.Language.get('lxc-subscription-support-site');

		case PLANS.lxcSubscriptionTransactSite.name:
			return Liferay.Language.get('lxc-subscription-transact-site');

		case PLANS.lxcPro.name:
			return Liferay.Language.get('lxc-pro');

		case PLANS.lxcBusiness.name:
			return Liferay.Language.get('lxc-business');

		case PLANS.lxcEnterprise.name:
			return Liferay.Language.get('lxc-enterprise');

		default:
			return '';
	}
}

export function formatPlanData(subscriptionIMap) {
	if (isNil(subscriptionIMap)) {
		subscriptionIMap = new Map();
	}

	const basicPlan = isBasicPlan({name: subscriptionIMap.get('name')});

	return new Plan(
		fromJS({
			addOns: {
				...subscriptionIMap
					.get('addOns', new List())
					.reduce((acc, addOn) => {
						acc[PLAN_TYPES[addOn.get('name')]] = addOn;
						return acc;
					}, {})
			},
			endDate: subscriptionIMap.get('endDate'),
			metrics: {
				individuals: new Metric({
					count: subscriptionIMap.get(
						'individualsCountSinceLastAnniversary',
						0
					),
					limit: subscriptionIMap.get('individualsLimit', 0),
					status: subscriptionIMap.get(
						'individualsStatus',
						SubscriptionStatuses.Ok
					)
				}),
				pageViews: new Metric({
					count: subscriptionIMap.get(
						'pageViewsCountSinceLastAnniversary',
						0
					),
					limit: subscriptionIMap.get('pageViewsLimit', 0),
					status: subscriptionIMap.get(
						'pageViewsStatus',
						SubscriptionStatuses.Ok
					)
				}),
				syncedIndividualsCount: subscriptionIMap.get(
					'syncedIndividualsCount'
				)
			},
			name: subscriptionIMap.get('name'),
			startDate: basicPlan
				? subscriptionIMap.get('startDate')
				: subscriptionIMap.get('lastAnniversaryDate')
		})
	);
}

export function isBasicPlan(currentPlan) {
	return (
		PLAN_TYPES[currentPlan.name] === 'basic' ||
		PLAN_TYPES[currentPlan.name] === 'lxcPro'
	);
}
