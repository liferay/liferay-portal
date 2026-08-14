import Constants, {SubscriptionStatuses} from 'shared/util/constants';
import {fromJS, List, Map} from 'immutable';
import {isNil} from 'lodash';
import {Metric, Plan} from 'shared/util/records';

export const INDIVIDUALS = 'individuals';

export const PAGEVIEWS = 'pageViews';

export const SubscriptionNames = {
	LiferayAnalyticsCloudBasic: 'Liferay Analytics Cloud Basic',
	LiferayAnalyticsCloudBusiness: 'Liferay Analytics Cloud Business',
	LiferayAnalyticsCloudBusinessContacts:
		'Liferay Analytics Cloud Business Contacts',
	LiferayAnalyticsCloudBusinessTrackedPages:
		'Liferay Analytics Cloud Business Tracked Pages',
	LiferayAnalyticsCloudEnterprise: 'Liferay Analytics Cloud Enterprise',
	LiferayAnalyticsCloudEnterpriseContacts:
		'Liferay Analytics Cloud Enterprise Contacts',
	LiferayAnalyticsCloudEnterpriseTrackedPages:
		'Liferay Analytics Cloud Enterprise Tracked Pages',
	LiferayDataPlatform: 'Liferay Data Platform',
	LiferayDataPlatformEnterprise: 'Liferay Data Platform Enterprise',
	LiferayDataPlatformPrivateBeta: 'Liferay Data Platform (Private Beta)',
	LiferayDigitalSalesRoom: 'Liferay Digital Sales Room',
	LiferaySaasBusinessPlan: 'Liferay SaaS - Business Plan',
	LiferaySaasCspCustomUserTier: 'Liferay SaaS - CSP - Custom User Tier',
	LiferaySaasCspCustomUserTierExtraUser:
		'Liferay SaaS - CSP - Custom User Tier - Extra User',
	LiferaySaasCspUpTo100Users: 'Liferay SaaS - CSP - Up to 100 Users',
	LiferaySaasCspUpTo100UsersExtraUser:
		'Liferay SaaS - CSP - Up to 100 Users - Extra User',
	LiferaySaasCspUpTo10kUsers: 'Liferay SaaS - CSP - Up to 10K Users',
	LiferaySaasCspUpTo10kUsersExtraUser:
		'Liferay SaaS - CSP - Up to 10K Users - Extra User',
	LiferaySaasCspUpTo1kUsers: 'Liferay SaaS - CSP - Up to 1K Users',
	LiferaySaasCspUpTo1kUsersExtraUser:
		'Liferay SaaS - CSP - Up to 1K Users - Extra User',
	LiferaySaasCspUpTo20kUsers: 'Liferay SaaS - CSP - Up to 20K Users',
	LiferaySaasCspUpTo20kUsersExtraUser:
		'Liferay SaaS - CSP - Up to 20K Users - Extra User',
	LiferaySaasCspUpTo500Users: 'Liferay SaaS - CSP - Up to 500 Users',
	LiferaySaasCspUpTo500UsersExtraUser:
		'Liferay SaaS - CSP - Up to 500 Users - Extra User',
	LiferaySaasCspUpTo5kUsers: 'Liferay SaaS - CSP - Up to 5K Users',
	LiferaySaasCspUpTo5kUsersExtraUser:
		'Liferay SaaS - CSP - Up to 5K Users - Extra User',
	LiferaySaasEnterprisePlan: 'Liferay SaaS - Enterprise Plan',
	LiferaySaasProPlan: 'Liferay SaaS - Pro Plan',
	LiferaySaasSubscriptionEngageSite:
		'Liferay SaaS Subscription - Engage Site',
	LiferaySaasSubscriptionSupportSite:
		'Liferay SaaS Subscription - Support Site',
	LiferaySaasSubscriptionTransactSite:
		'Liferay SaaS Subscription - Transact Site',
	LxcBusinessPlan: 'LXC - Business Plan',
	LxcCspCustomUserTier: 'LXC - CSP - Custom User Tier',
	LxcCspCustomUserTierExtraUser: 'LXC - CSP - Custom User Tier - Extra User',
	LxcCspUpTo100Users: 'LXC - CSP - Up to 100 Users',
	LxcCspUpTo100UsersExtraUser: 'LXC - CSP - Up to 100 Users - Extra User',
	LxcCspUpTo10kUsers: 'LXC - CSP - Up to 10K Users',
	LxcCspUpTo10kUsersExtraUser: 'LXC - CSP - Up to 10K Users - Extra User',
	LxcCspUpTo1kUsers: 'LXC - CSP - Up to 1K Users',
	LxcCspUpTo1kUsersExtraUser: 'LXC - CSP - Up to 1K Users - Extra User',
	LxcCspUpTo20kUsers: 'LXC - CSP - Up to 20K Users',
	LxcCspUpTo20kUsersExtraUser: 'LXC - CSP - Up to 20K Users - Extra User',
	LxcCspUpTo500Users: 'LXC - CSP - Up to 500 Users',
	LxcCspUpTo500UsersExtraUser: 'LXC - CSP - Up to 500 Users - Extra User',
	LxcCspUpTo5kUsers: 'LXC - CSP - Up to 5K Users',
	LxcCspUpTo5kUsersExtraUser: 'LXC - CSP - Up to 5K Users - Extra User',
	LxcEnterprisePlan: 'LXC - Enterprise Plan',
	LxcProPlan: 'LXC - Pro Plan',
	LxcSubscriptionEngageSite: 'LXC Subscription - Engage Site',
	LxcSubscriptionSupportSite: 'LXC Subscription - Support Site',
	LxcSubscriptionTransactSite: 'LXC Subscription - Transact Site',
};

export const PLAN_TYPES = {
	[SubscriptionNames.LiferayAnalyticsCloudBasic]: 'basic',
	[SubscriptionNames.LiferayAnalyticsCloudBusiness]: 'business',
	[SubscriptionNames.LiferayAnalyticsCloudBusinessContacts]: INDIVIDUALS,
	[SubscriptionNames.LiferayAnalyticsCloudBusinessTrackedPages]: PAGEVIEWS,
	[SubscriptionNames.LiferayAnalyticsCloudEnterprise]: 'enterprise',
	[SubscriptionNames.LiferayAnalyticsCloudEnterpriseContacts]: INDIVIDUALS,
	[SubscriptionNames.LiferayAnalyticsCloudEnterpriseTrackedPages]: PAGEVIEWS,
	[SubscriptionNames.LiferayDataPlatform]: 'dataPlatform',
	[SubscriptionNames.LiferayDataPlatformEnterprise]: 'dataPlatformEnterprise',
	[SubscriptionNames.LiferayDataPlatformPrivateBeta]: 'dataPlatform',
	[SubscriptionNames.LiferayDigitalSalesRoom]: 'digitalSalesRoom',
	[SubscriptionNames.LiferaySaasBusinessPlan]: 'lxcBusiness',
	[SubscriptionNames.LiferaySaasCspCustomUserTier]: 'lxcCspCustomUserTier',
	[SubscriptionNames.LiferaySaasCspCustomUserTierExtraUser]:
		'lxcCspCustomUserTierExtraUser',
	[SubscriptionNames.LiferaySaasCspUpTo100Users]: 'lxcCspUpTo100Users',
	[SubscriptionNames.LiferaySaasCspUpTo100UsersExtraUser]:
		'lxcCspUpTo100UsersExtraUser',
	[SubscriptionNames.LiferaySaasCspUpTo10kUsers]: 'lxcCspUpTo10kUsers',
	[SubscriptionNames.LiferaySaasCspUpTo10kUsersExtraUser]:
		'lxcCspUpTo10kUsersExtraUser',
	[SubscriptionNames.LiferaySaasCspUpTo1kUsers]: 'lxcCspUpTo1kUsers',
	[SubscriptionNames.LiferaySaasCspUpTo1kUsersExtraUser]:
		'lxcCspUpTo1kUsersExtraUser',
	[SubscriptionNames.LiferaySaasCspUpTo20kUsers]: 'lxcCspUpTo20kUsers',
	[SubscriptionNames.LiferaySaasCspUpTo20kUsersExtraUser]:
		'lxcCspUpTo20kUsersExtraUser',
	[SubscriptionNames.LiferaySaasCspUpTo500Users]: 'lxcCspUpTo500Users',
	[SubscriptionNames.LiferaySaasCspUpTo500UsersExtraUser]:
		'lxcCspUpTo500UsersExtraUser',
	[SubscriptionNames.LiferaySaasCspUpTo5kUsers]: 'lxcCspUpTo5kUsers',
	[SubscriptionNames.LiferaySaasCspUpTo5kUsersExtraUser]:
		'lxcCspUpTo5kUsersExtraUser',
	[SubscriptionNames.LiferaySaasEnterprisePlan]: 'lxcEnterprise',
	[SubscriptionNames.LiferaySaasProPlan]: 'lxcPro',
	[SubscriptionNames.LiferaySaasSubscriptionEngageSite]:
		'lxcSubscriptionEngageSite',
	[SubscriptionNames.LiferaySaasSubscriptionSupportSite]:
		'lxcSubscriptionSupportSite',
	[SubscriptionNames.LiferaySaasSubscriptionTransactSite]:
		'lxcSubscriptionTransactSite',
	[SubscriptionNames.LxcBusinessPlan]: 'lxcBusiness',
	[SubscriptionNames.LxcCspCustomUserTier]: 'lxcCspCustomUserTier',
	[SubscriptionNames.LxcCspCustomUserTierExtraUser]:
		'lxcCspCustomUserTierExtraUser',
	[SubscriptionNames.LxcCspUpTo100Users]: 'lxcCspUpTo100Users',
	[SubscriptionNames.LxcCspUpTo100UsersExtraUser]:
		'lxcCspUpTo100UsersExtraUser',
	[SubscriptionNames.LxcCspUpTo10kUsers]: 'lxcCspUpTo10kUsers',
	[SubscriptionNames.LxcCspUpTo10kUsersExtraUser]:
		'lxcCspUpTo10kUsersExtraUser',
	[SubscriptionNames.LxcCspUpTo1kUsers]: 'lxcCspUpTo1kUsers',
	[SubscriptionNames.LxcCspUpTo1kUsersExtraUser]:
		'lxcCspUpTo1kUsersExtraUser',
	[SubscriptionNames.LxcCspUpTo20kUsers]: 'lxcCspUpTo20kUsers',
	[SubscriptionNames.LxcCspUpTo20kUsersExtraUser]:
		'lxcCspUpTo20kUsersExtraUser',
	[SubscriptionNames.LxcCspUpTo500Users]: 'lxcCspUpTo500Users',
	[SubscriptionNames.LxcCspUpTo500UsersExtraUser]:
		'lxcCspUpTo500UsersExtraUser',
	[SubscriptionNames.LxcCspUpTo5kUsers]: 'lxcCspUpTo5kUsers',
	[SubscriptionNames.LxcCspUpTo5kUsersExtraUser]:
		'lxcCspUpTo5kUsersExtraUser',
	[SubscriptionNames.LxcEnterprisePlan]: 'lxcEnterprise',
	[SubscriptionNames.LxcProPlan]: 'lxcPro',
	[SubscriptionNames.LxcSubscriptionEngageSite]: 'lxcSubscriptionEngageSite',
	[SubscriptionNames.LxcSubscriptionSupportSite]:
		'lxcSubscriptionSupportSite',
	[SubscriptionNames.LxcSubscriptionTransactSite]:
		'lxcSubscriptionTransactSite',
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
		[PAGEVIEWS]: {},
	};

	const PLANS = {};

	const hasKeyProperty = (key) =>
		Object.prototype.hasOwnProperty.call(allPlans, key);

	for (const key in allPlans) {
		if (hasKeyProperty(key)) {
			const {
				baseSubscriptionPlan,
				individualsLimit,
				name,
				pageViewsLimit,
			} = allPlans[key];

			const planType = PLAN_TYPES[key];

			const formattedPlan = {
				baseSubscriptionPlan,
				limits: {
					[INDIVIDUALS]: individualsLimit,
					[PAGEVIEWS]: pageViewsLimit,
				},
				name,
			};

			const parentPlanType = PLAN_TYPES[baseSubscriptionPlan];

			if (baseSubscriptionPlan) {
				ADD_ONS[planType][parentPlanType] = formattedPlan;
			}
			else {
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
	[SubscriptionStatuses.Over]: 'danger',
};

export const DEFAULT_ADDONS = {
	[INDIVIDUALS]: ADD_ONS[INDIVIDUALS].business,
	[PAGEVIEWS]: ADD_ONS[PAGEVIEWS].business,
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
				[name]: totalLimit ? totalLimit.toLocaleString() : '-',
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

		case SubscriptionNames.LiferayDataPlatform:
			return Liferay.Language.get('liferay-data-platform');

		case SubscriptionNames.LiferayDataPlatformPrivateBeta:
			return Liferay.Language.get('liferay-data-platform-private-beta');

		case SubscriptionNames.LiferayDataPlatformEnterprise:
			return Liferay.Language.get('liferay-data-platform-enterprise');

		case SubscriptionNames.LiferayDigitalSalesRoom:
			return Liferay.Language.get('liferay-digital-sales-room');

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

		case SubscriptionNames.LiferayDataPlatform:
			return Liferay.Language.get('liferay-data-platform');

		case SubscriptionNames.LiferayDataPlatformEnterprise:
			return Liferay.Language.get('liferay-data-platform-enterprise');

		case SubscriptionNames.LiferayDataPlatformPrivateBeta:
			return Liferay.Language.get('liferay-data-platform-private-beta');

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
					}, {}),
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
					),
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
					),
				}),
				syncedIndividualsCount: subscriptionIMap.get(
					'syncedIndividualsCount'
				),
			},
			name: subscriptionIMap.get('name'),
			startDate: basicPlan
				? subscriptionIMap.get('startDate')
				: subscriptionIMap.get('lastAnniversaryDate'),
		})
	);
}

export function isBasicPlan(currentPlan) {
	return (
		PLAN_TYPES[currentPlan.name] === 'basic' ||
		PLAN_TYPES[currentPlan.name] === 'dataPlatform' ||
		PLAN_TYPES[currentPlan.name] === 'lxcPro'
	);
}

export function isLDPPlan(subscriptionName) {
	return subscriptionName?.includes('Data Platform') ?? false;
}
