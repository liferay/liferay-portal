import moment from 'moment';
import TimeZone from 'shared/util/records/TimeZone';
import {
	ActivityActions,
	AssetTypes,
	CredentialTypes,
	DataSourceProgressStatuses,
	DataSourceStates,
	DataSourceStatuses,
	DataSourceTypes,
	EntityTypes,
	JobRunDataPeriods,
	JobRunFrequencies,
	JobStatuses,
	JobTypes,
	ProjectStates,
	SegmentStates,
	SubscriptionStatuses,
	UserRoleNames,
} from 'shared/util/constants';
import {AttributeTypes} from 'event-analysis/utils/types';
import {
	Conjunctions,
	RelationalOperators,
} from 'segment/segment-editor/dynamic/utils/constants';
import {fromJS, List, Map} from 'immutable';
import {getISODate} from 'shared/util/date';
import {isArray, range, times} from 'lodash';
import {Metric} from 'shared/util/records';
import {
	NotificationSubtypes,
	NotificationTypes,
} from 'shared/util/records/Notification';
import {SubscriptionNames} from 'shared/util/subscriptions';

const BASE_TIMESTAMP = 1531263666366;

export function getRandom(min, max) {
	return Math.floor(Math.random() * (max - min)) + min;
}

export function getTimestamp(change = 0, unit = 'days') {
	return moment(BASE_TIMESTAMP).add(change, unit).valueOf();
}

export function getDate(change = 0, unit = 'days') {
	return moment(BASE_TIMESTAMP).add(change, unit).toDate();
}

export function getImmutableMock(Record, mockFn, id, config) {
	return new Record(fromJS(mockFn(id, config)));
}

export function mockApiToken(data) {
	return {
		createDate: getISODate(getTimestamp()),
		expirationDate: getISODate(getTimestamp(30)),
		lastAccessDate: getISODate(getTimestamp()),
		token: '1234ABC',
		...data,
	};
}

export function mockForm(data) {
	return {
		errors: {},
		touched: {},
		...data,
	};
}

export function mockIndividual(seed = 0, properties) {
	return {
		activitiesCount: 1000,
		colorId: String(seed),
		context: {
			browserName: 'Chrome',
			city: 'Los Angeles',
			contactId: `contact-${seed}`,
			country: 'United States',
			devicePixelRatio: '2',
			deviceType: 'Desktop',
			languageId: 'en_US',
			platformName: 'Mac OS X',
			region: 'California',
			screenHeight: '1080',
			screenWidth: '1920',
			timeZoneOffset: '-04:00',
			userAgent: 'Mozilla/5.0',
			userId: `user-${seed}`,
		},
		dateCreated: getTimestamp(-2),
		firstActivityDate: getTimestamp(-1),
		id: String(seed),
		image: '/path/to/portrait.png',
		lastActivityDate: getTimestamp(),
		name: 'Foo Bar',
		properties: {
			familyName: 'Bar',
			givenName: 'Foo',
			jobTitle: 'Developer',
			salary: '50,000',
			title: 'Developer',
			...properties,
		},
		type: EntityTypes.Individual,
	};
}

export function mockIndividualDetails(data = {}) {
	return {
		custom: {
			favorite_fruit: [
				{
					dataSourceId: '123',
					fieldType: 'Text',
					name: 'favorite_fruit',
					sourceName: 'Favorite Fruit',
					value: 'bananas',
				},
			],
			favorite_pokemon: [
				{
					dataSourceId: '123',
					fieldType: 'Text',
					name: 'favorite_pokemon',
					sourceName: 'Favorite Pokemon',
					value: 'Charmander',
				},
			],
		},
		demographics: {
			familyName: [
				{
					dataSourceId: '123',
					fieldType: 'Text',
					name: 'familyName',
					value: 'Foo',
				},
			],
			givenName: [
				{
					dataSourceId: '123',
					fieldType: 'Text',
					name: 'givenName',
					value: 'Bar',
				},
			],
			jobTitle: [
				{
					dataSourceId: '123',
					fieldType: 'Text',
					name: 'jobTitle',
					value: 'Developer',
				},
			],
			...data,
		},
	};
}

export function mockAccount(seed = 1, data = {}) {
	return {
		activitiesCount: 100,
		id: String(seed),
		individualCount: 10,
		name: `account${seed}`,
		properties: {
			accountNumber: '123123123',
			accountType: 'customer',
			annualRevenue: 'USD $1,000,000,000',
			billingAddress: '123 Fairy Lane Diamond Bar, CA 91765 USA',
			description: 'Foo company description',
			fax: '321-321-4321',
			industry: 'Agriculture',
			numberOfEmployees: '9001',
			ownership: 'private',
			phone: '123-123-1234',
			shippingAddress: '123 Fairy Lane Diamond Bar, CA 91765 USA',
			website: 'www.liferay.com',
			yearStarted: '2003',
		},
		type: EntityTypes.Account,
		...data,
	};
}

export function mockAccountDetails(data = {}) {
	return {
		industry: [
			{
				context: 'organization',
				dataSourceId: '123',
				dataSourceName: 'test data source',
				dateModified: moment().subtract(7, 'days'),
				fieldType: 'Text',
				id: null,
				label: null,
				name: 'industry',
				ownerId: '23',
				ownerType: 'account',
				sourceName: 'industry',
				value: 'Agriculture',
			},
		],
		ownership: [
			{
				context: 'organization',
				dataSourceId: '123',
				dataSourceName: 'test data source',
				dateModified: moment().subtract(7, 'days'),
				fieldType: 'Text',
				id: null,
				label: null,
				name: 'name',
				ownerId: '23',
				ownerType: 'account',
				sourceName: 'businessOwnership',
				value: 'Northern Parts',
			},
			{
				context: 'organization',
				dataSourceId: '123',
				dataSourceName: 'test data source',
				dateModified: moment().subtract(7, 'days'),
				fieldType: 'Text',
				id: null,
				label: null,
				name: 'name',
				ownerId: '23',
				ownerType: 'account',
				sourceName: 'businessOwnership',
				value: 'Southern Parts',
			},
		],
		yearStarted: [
			{
				context: 'organization',
				dataSourceId: '123',
				dataSourceName: 'test data source',
				dateModified: moment().subtract(7, 'days'),
				fieldType: 'Text',
				id: null,
				label: null,
				name: 'yearStarted',
				ownerId: '23',
				ownerType: 'account',
				sourceName: 'yearStarted',
				value: '2003',
			},
		],
		...data,
	};
}

export function mockAsset(seed = 0, data = {}) {
	return {
		canonicalUrl: `http://www.foo.com/${seed}`,
		dataSourceAssetPK: `asset-pk-${seed}`,
		dataSourceId: `dataSourceId${seed}`,
		description: null,
		id: String(seed),
		name: 'Test Asset',
		type: 'Form',
		...data,
	};
}

export function mockCardTemplate(seed = 0, data = {}) {
	return {
		id: String(seed),
		layoutId: String(seed),
		name: '',
		size: 2,
		type: null,
		...data,
	};
}

export function mockCompany(seed = 0, data = {}) {
	return {
		companyId: 20155,
		id: String(seed),
		installationId: 13183,
		name: `Test Company ${seed}`,
		status: 1,
		url: 'http://www.liferay.com',
		version: '7.1',
		...data,
	};
}

export function mockBlockedKeyword(seed = 0, data = {}) {
	return {
		createDate: 1575394162758,
		duplicate: false,
		id: '391941272912793194',
		keyword: `bar-${seed}`,
		...data,
	};
}

export function mockChannel(seed = 1, permissionType = 0, data = {}) {
	return {
		createTime: 1581697505321,
		id: seed,
		name: `Channel ${seed}`,
		permissionType,
		tokenAuth: true,
		...data,
	};
}

export function mockChannels() {
	return {
		disableSearch: false,
		items: [
			{
				createTime: 1695241914644,
				groupsCount: 6,
				id: '643280225365059871',
				name: 'Liferay DXP',
				permissionType: 0,
				tokenAuth: false,
			},
		],
		total: 1,
	};
}

export function generateCriterion(customValues) {
	return {
		operatorName: RelationalOperators.EQ,
		propertyName: 'firstName',
		touched: false,
		valid: true,
		value: 'test',
		...customValues,
	};
}

export function mockNewCriteria(numOfItems = 1, criterionParams) {
	return {
		conjunctionName: Conjunctions.And,
		groupId: 'group_01',
		items: range(numOfItems).map(() => generateCriterion(criterionParams)),
	};
}

export function mockNewCriteriaNested() {
	return {
		conjunctionName: Conjunctions.And,
		groupId: 'group_01',
		items: [
			{
				conjunctionName: Conjunctions.Or,
				groupId: 'group_02',
				items: [
					{
						conjunctionName: Conjunctions.And,
						groupId: 'group_03',
						items: [
							{
								conjunctionName: Conjunctions.Or,
								groupId: 'group_04',
								items: range(2).map(generateCriterion),
							},
							generateCriterion(),
						],
					},
					generateCriterion(),
				],
			},
			generateCriterion(),
		],
	};
}

export function mockCSVDataSource(seed = 1, data = {}) {
	return {
		createDate: getTimestamp(-2),
		disabled: false,
		event: null,
		fileName: null,
		id: String(seed),
		name: `CSV${seed}`,
		properties: {},
		provider: {
			type: DataSourceTypes.Csv,
		},
		providerType: DataSourceTypes.Csv,
		state: DataSourceStates.Ready,
		status: DataSourceStatuses.Active,
		type: EntityTypes.DataSource,
		url: 'liferay.example.faro.com',
		...data,
	};
}

export function mockLiferayDataSource(seed = 1, data = {}) {
	return {
		createDate: getTimestamp(-2),
		credentials: {
			login: `LiferayUser${seed}`,
		},
		disabled: false,
		event: null,
		fileName: null,
		id: String(seed),
		name: `Liferay${seed}`,
		properties: {},
		provider: {
			analyticsConfiguration: null,
			contactsConfiguration: null,
			instanceInfo: {},
			type: DataSourceTypes.Liferay,
		},
		providerType: DataSourceTypes.Liferay,
		state: DataSourceStates.CredentialsValid,
		status: DataSourceStatuses.Active,
		type: EntityTypes.DataSource,
		url: `https://www.faro-${seed}.io`,
		...data,
	};
}

export function mockMarketoCampaignDataSource(seed = 1, data = {}) {
	return {
		createDate: getTimestamp(-2),
		credentials: {
			oAuthClientId: `oAuthMockClientId-${seed}`,
			oAuthClientSecret: `oAuthMockClientSecret-${seed}`,
			type: CredentialTypes.OAuth2,
		},
		disabled: false,
		event: null,
		fileName: null,
		id: String(seed),
		name: `Marketo Campaign ${seed}`,
		properties: {},
		provider: {
			channelsConfiguration: null,
			contactsConfiguration: {
				enableAllLeads: false,
			},
			type: DataSourceTypes.MarketoCampaign,
		},
		providerType: DataSourceTypes.MarketoCampaign,
		state: DataSourceStates.CredentialsValid,
		status: DataSourceStatuses.Active,
		type: EntityTypes.DataSource,
		url: 'https://123-ABC-456.mktorest.com',
		...data,
	};
}

export function mockMetricFragment(seed = 0, data = {}) {
	return {
		histogram: {
			asymmetricComparison: false,
			metrics: range(30).map((i) => ({
				key: `${moment(getTimestamp(i)).format('YYYY-MM-DD')}T00:00`,
				previousValue: seed * i * 2,
				previousValueKey: `${moment(getTimestamp(i - 30)).format(
					'YYYY-MM-DD'
				)}T00:00`,
				trend: {
					percentage: 0,
					trend: {
						percentage: -50,
						trendClassification: 'NEGATIVE',
					},
				},
				value: seed * i,
				valueKey: `${moment(getTimestamp(i)).format(
					'YYYY-MM-DD'
				)}T00:00`,
			})),
		},
		previousValue: seed * 0.75,
		trend: {
			percentage: 33.3,
			trendClassification: 'POSITIVE',
		},
		value: seed,
		...data,
	};
}

export function mockProgress(data) {
	return {
		accounts: {
			dateRecorded: getTimestamp(),
			processedOperations: 100,
			status: DataSourceProgressStatuses.Completed,
			totalOperations: 100,
		},
		individuals: {
			dateRecorded: getTimestamp(),
			processedOperations: 100,
			status: DataSourceProgressStatuses.Completed,
			totalOperations: 100,
		},
		...data,
	};
}

export function mockSalesforceDataSource(seed = 1, data = {}) {
	return {
		createDate: getTimestamp(-2),
		credentials: {
			oAuthClientId: `oAuthMockClientId-${seed}`,
			oAuthClientSecret: `oAuthMockClientSecret-${seed}`,
			type: CredentialTypes.OAuth2,
		},
		disabled: false,
		event: null,
		fileName: null,
		id: String(seed),
		name: 'Salesforce{seed}',
		properties: {},
		provider: {
			analyticsConfiguration: null,
			contactsConfiguration: null,
			type: DataSourceTypes.Salesforce,
		},
		providerType: DataSourceTypes.Salesforce,
		state: DataSourceStates.CredentialsValid,
		status: DataSourceStatuses.Active,
		type: EntityTypes.DataSource,
		url: 'https://login.salesforce.com',
		...data,
	};
}

export function mockDistribution(seed = 0) {
	return {
		count: seed,
		values: [`value${seed}`],
	};
}

export function mockFieldMapping(seed = 0, data = {}) {
	return {
		context: 'demographics',
		dataSourceFieldNames: {},
		id: String(seed),
		name: `Test${seed}`,
		ownerType: 'individual',
		rawType: 'Text',
		type: 'Text',
		values: [`value${seed}`],
		...data,
	};
}

export function mockNullFieldMapping(seed = 0, data = {}) {
	return mockFieldMapping(seed, {
		value: null,
		...data,
	});
}

export function mockIndividualAttributes(seed = 0, data = {}) {
	return {
		dataSources: range(seed + 1).map((i) => ({
			dataSourceFieldName: `testMiddleName${i}`,
			dataSourceName: `LIFERAY-DATASOURCE-FARO-EXAMPLE-${i}`,
		})),
		dateModified: getTimestamp(),
		fieldName: `testFildName${seed}`,
		...data,
	};
}

export function mockInterestData(seed = 0) {
	return {
		interestAggregations: [
			{
				intervalInitDate: getTimestamp(-1),
				scoreAvg: 1.6,
				totalElements: 1,
				viewsSum: 2,
			},
			{
				intervalInitDate: getTimestamp(),
				scoreAvg: 5,
				totalElements: 1,
				viewsSum: 2,
			},
			{
				intervalInitDate: getTimestamp(1),
				scoreAvg: 2,
				totalElements: 1,
				viewsSum: 2,
			},
		],
		name: `Bar${seed}`,
		pagesViewCount: 2,
		relatedPagesCount: 8,
		score: 1.6,
	};
}

export function mockInterestObject(seed = 0, data = {}) {
	return {
		individualCount: 5,
		name: `interest${seed}`,
		...data,
	};
}

export function mockLayout(seed = 0, faroEntity = mockSegment()) {
	return {
		activitiesCount: 1000,
		contactsCardData: {1: {id: 'cardData1'}, 2: {id: 'cardData2'}},
		contactsLayoutTemplate: {
			contactsCardTemplatesList: [
				[mockCardTemplate('cardTemplate1')],
				[mockCardTemplate('cardTemplate2')],
			],
			headerContactsCardTemplates: [
				mockCardTemplate('headerCardTemplate1'),
			],
			id: seed,
			name: 'layoutTemplate',
		},
		faroEntity,
	};
}

export function mockLiferaySyncCounts(data = {}) {
	return {
		allUsersCount: 1000,
		currentUsersCount: 248,
		organizationsUsersCount: 140,
		totalUsersCount: 448,
		userGroupsUsersCount: 260,
		...data,
	};
}

export function mockMembershipChange(seed = 0, data = {}) {
	return {
		dateChanged: getTimestamp(),
		dateFirst: getTimestamp(-2),
		individualDeleted: false,
		individualEmail: 'test@faro.io',
		individualId: `id${seed}`,
		individualName: `Test${seed}`,
		individualsCount: 1,
		individualSegmentId: `segmentid${seed}`,
		operation: 'ADDED',
		...data,
	};
}

export function mockMembershipChangeAggregation(seed = 0, data = {}) {
	return {
		addedIndividualsCount: 1,
		anonymousIndividualsCount: 1,
		individualsCount: seed,
		intervalInitDate: getTimestamp(),
		knownIndividualsCount: 1,
		removedIndividualsCount: 0,
		...data,
	};
}

export function mockNotification(seed = 0, data = {}) {
	return {
		id: String(seed),
		modifiedTime: Date.now(),
		subtype: NotificationSubtypes.TimeZoneChanged,
		type: NotificationTypes.Alert,
		...data,
	};
}

export function mockGraphqlOrganization(seed = 0, data = {}) {
	return {
		id: String(seed),
		name: `Test Organization ${seed}`,
		parentName: 'Foo Parent Organization',
		type: 'organization',
		...data,
	};
}

export function mockOrganization(seed = 0, data = {}) {
	return {
		id: String(seed),
		name: `Test Organization ${seed}`,
		usersCount: seed,
		...data,
	};
}

export function mockPageVisited(seed = 0, data = {}) {
	return {
		day: 0,
		description: 'Test Description',
		id: String(seed),
		interestName: `Bar${seed}`,
		title: 'Page Visited Title',
		url: 'https://www.liferay.com',
		viewCount: 1,
		...data,
	};
}

export function mockSearch(mockEntity, total = 1, data = []) {
	return {
		disableSearch: false,
		items: range(total).map((i) =>
			isArray(data) ? mockEntity(i, ...data) : mockEntity(i, data)
		),
		total,
	};
}

export function mockSegment(seed = 0, data = {}) {
	return {
		activeIndividualCount: 0,
		activitiesCount: 1000,
		anonymousIndividualCount: 0,
		dateCreated: getTimestamp(-1),
		dateModified: getTimestamp(),
		id: String(seed),
		includeAnonymousUsers: false,
		individualCount: 10,
		knownIndividualCount: 0,
		lastActivityDate: getTimestamp(),
		name: `Seattle${seed}`,
		properties: {},
		state: SegmentStates.Ready,
		type: EntityTypes.IndividualsSegment,
		...data,
	};
}

export function mockSubscription(data = {}) {
	return {
		addOns: new List([
			new Map({
				name: SubscriptionNames.LiferayAnalyticsCloudEnterpriseContacts,
				quantity: 2,
			}),
			new Map({
				name: SubscriptionNames.LiferayAnalyticsCloudEnterpriseTrackedPages,
				quantity: 1,
			}),
		]),
		endDate: getTimestamp(),
		individualsCountSinceLastAnniversary: 2057,
		individualsLimit: 105000,
		individualsStatus: SubscriptionStatuses.Ok,
		lastAnniversaryDate: getTimestamp(-2),
		name: SubscriptionNames.LiferayAnalyticsCloudEnterprise,
		pageViewsCountSinceLastAnniversary: 100023,
		pageViewsLimit: 7000000,
		pageViewsStatus: SubscriptionStatuses.Ok,
		startDate: getTimestamp(-2),
		...data,
	};
}

export function mockMapping(seed = 0, data = {}) {
	return {
		mapping: null,
		name: `Test${seed}`,
		suggestions: [],
		values: [],
		...data,
	};
}

export function mockSession(seed = 0, data = {}, actionData) {
	return {
		browserName: 'Chrome',
		completedDate: getTimestamp() + 10000000,
		createDate: getTimestamp(),
		day: getTimestamp(),
		events: times(seed + (1 % 10), (i) => mockEvent(i, actionData)),
		id: `activity_id_${seed}`,
		name: `activity_${seed}`,
		...data,
	};
}

export function mockAction(seed = 0, data = {}) {
	return {
		action: ActivityActions.Visits,
		activityKey: `key_${seed}`,
		assetType: AssetTypes.Form,
		canonicalUrl: `https://www.liferay${seed}.com`,
		dataSourceAssetPK: String(seed),
		day: seed,
		eventId: String(seed),
		groupName: `group_${seed}`,
		id: `action_${seed}`,
		name: `Asset ${seed}`,
		startTime: seed + 100000,
		url: `https://www.liferay${seed}.com`,
		...data,
	};
}

export function mockActivity(seed = 0, data = {}, actionData) {
	return {
		activities: times(seed + (1 % 10), (i) => mockAction(i, actionData)),
		day: getTimestamp(),
		endTime: getTimestamp() + 10000000,
		id: `activity_id_${seed}`,
		name: `activity_${seed}`,
		startTime: getTimestamp(),
		...data,
	};
}

export function mockActivityHistory(data = {}) {
	return {
		activityAggregations: [
			{
				intervalInitDate: getTimestamp(-1),
				totalElements: 1,
			},
			{
				intervalInitDate: getTimestamp(),
				totalElements: 2,
			},
		],
		change: 1.28,
		count: 123,
		prevCount: 54,
		...data,
	};
}

export function mockEvent(seed = 0) {
	return {
		applicationId: 'Page',
		assetTitle: 'Page Title',
		canonicalUrl: `https://www.liferay${seed}.com`,
		createDate: seed + 100000,
		name: `Asset ${seed}`,
		pageDescription: 'Page Description',
		pageKeywords: '',
		pageTitle: 'Page Title',
		referrer: 'www.liferay.com',
		url: `https://www.liferay${seed}.com`,
	};
}

export function mockBlockedCustomEventDefinition(seed = 0, data = {}) {
	return {
		hidden: false,
		id: String(seed),
		lastSeenDate: getISODate(getTimestamp()),
		lastSeenURL: `https//:www.liferay.com/${seed}`,
		name: `name-${seed}`,
		...data,
	};
}

export function mockEventAttributeDefinition(seed = 0, data = {}) {
	return {
		dataType: 'STRING',
		description: null,
		displayName: `displayName-${seed}`,
		id: String(seed),
		name: `name-${seed}`,
		sampleValue: `samplevalue-${seed}`,
		type: AttributeTypes.Global,
		...data,
	};
}

export function mockEventDefinition(seed = 0, data = {}) {
	return {
		description: null,
		displayName: `displayName-${seed}`,
		eventAttributeDefinitions: [mockEventAttributeDefinition(1)],
		hidden: false,
		id: String(seed),
		name: `name-${seed}`,
		type: 'DEFAULT',
		...data,
	};
}

export function mockPlan({data = {}, individuals = {}, pageViews = {}} = {}) {
	return {
		addOns: {
			individuals: {
				name: SubscriptionNames.LiferayAnalyticsCloudEnterpriseContacts,
				quantity: 2,
			},
			pageViews: {
				name: SubscriptionNames.LiferayAnalyticsCloudEnterpriseTrackedPages,
				quantity: 1,
			},
		},
		endDate: getTimestamp(),
		lastAnniversaryDate: getTimestamp(-2),
		metrics: {
			individuals: new Metric({
				count: 2057,
				limit: 105000,
				status: SubscriptionStatuses.Ok,
				...individuals,
			}),
			pageViews: new Metric({
				count: 0,
				limit: 7000000,
				status: SubscriptionStatuses.Ok,
				...pageViews,
			}),
		},
		name: SubscriptionNames.LiferayAnalyticsCloudEnterprise,
		startDate: getTimestamp(-2),
		...data,
	};
}

export function mockProject(seed = 1, data = {}) {
	return {
		accountKey: `accountKey${seed}`,
		accountName: `accountName${seed}`,
		addOnsIList: new Map([
			{
				baseSubscriptionPlan:
					SubscriptionNames.LiferayAnalyticsCloudEnterprise,
				limits: {
					individuals: 5000,
					pageViews: 0,
				},
				name: SubscriptionNames.LiferayAnalyticsCloudEnterpriseContacts,
			},
			{
				baseSubscriptionPlan:
					SubscriptionNames.LiferayAnalyticsCloudEnterprise,
				limits: {
					individuals: 0,
					pageViews: 5000000,
				},
				name: SubscriptionNames.LiferayAnalyticsCloudEnterpriseTrackedPages,
			},
		]),
		corpProjectName: `corpProjectName${seed}`,
		corpProjectUuid: `corpProjectUuid${seed}`,
		faroSubscription: new Map(),
		groupId: seed,
		name: `project${seed}`,
		ownerEmailAddress: `test${seed}@liferay.com`,
		percentageComplete: 0,
		recommendationsEnabled: true,
		state: ProjectStates.Ready,
		timeZone: new TimeZone(),
		userId: seed,
		...data,
	};
}

export function mockProperty(seed = 1, data = {}) {
	return {
		entityName: `fooProperty-${seed}`,
		entityType: '',
		id: null,
		label: '',
		name: '',
		propertyKey: '',
		type: '',
		...data,
	};
}

export function mockRecommendationJobRun(seed = 0, data = {}) {
	return {
		completedDate: '2020-04-24',
		context: [
			{key: 'userItemInteractionsDatasetCount', value: 321},
			{key: 'itemsDatasetCount', value: 123},
		],
		id: seed,
		status: 'COMPLETED',
		...data,
	};
}

export function mockRecommendationJobRunsMonthlyStatistics(data = {}) {
	return {
		availableJobRuns: 3,
		completedJobRuns: 4,
		failedJobRuns: 1,
		runningJobRuns: 1,
		scheduledJobRuns: 3,
		...data,
	};
}

export function mockRecommendationPageAsset(seed = 0, data = {}) {
	return {
		canonicalUrl: `https://www.test${seed}.com`,
		description: 'Test description',
		id: seed,
		keywords: [],
		title: 'Test Title',
		url: `https://www.test${seed}.com`,
		...data,
	};
}

export function mockRecommendationJob(seed = 0, data = {}) {
	return {
		id: String(seed),
		name: `Recommendation Job Name ${seed}`,
		nextRunDate: getTimestamp(0),
		parameters: [],
		runDataPeriod: JobRunDataPeriods.Last30Days,
		runDate: getTimestamp(-2),
		runFrequency: JobRunFrequencies.Every30Days,
		status: JobStatuses.Ready,
		type: JobTypes.ItemSimilarity,
		...data,
	};
}

export function mockSite(seed = 0, data = {}) {
	return {
		friendlyURL: `/guest/${seed}`,
		id: String(seed),
		name: `Test - ${seed}`,
		parentGroupId: '23',
		...data,
	};
}

export function mockUser(seed = 0, data = {}) {
	return {
		emailAddress: 'test@liferay.com',
		id: seed,
		name: 'Test Test',
		roleName: UserRoleNames.Owner,
		status: 1,
		...data,
	};
}

export function mockMemberUser(seed, data) {
	return mockUser(seed, {roleName: UserRoleNames.Member, ...data});
}

export function mockUserGroup(seed = 0, data = {}) {
	return {
		id: String(seed),
		name: `Test User Group ${seed}`,
		usersCount: seed,
		...data,
	};
}

export function getTreeCount(id) {
	return (id * 4) % 10;
}

export function mockTreeItem(seed = 0, parentId = 0) {
	const id = parentId * 10 + seed;
	const count = getTreeCount(id);

	return {
		count,
		description: count ? `${count} Assets` : '',
		id,
		name: `Child${id}`,
		parentId: parentId || undefined,
	};
}

export function getDummyEvent(
	event,
	value = getRandom(100, 2000),
	previousValue = getRandom(100, 2000)
) {
	return [
		{
			breakdownItems: [
				{
					name: 'All Individuals',
					previousValue,
					value,
				},
			],
			isLeafNode: true,
			name: event.name,
			previousValue,
			value,
		},
	];
}

export function getDummyBreakdown(i, event, breakdowns, order, currentLevel) {
	const isNextLeaf = currentLevel === order.length;
	const attribute = breakdowns[order[currentLevel - 1]];

	const breakdownItems = isNextLeaf
		? getDummyEvent(event)
		: getDummyBreakdowns(event, order, breakdowns, ++currentLevel);

	return {
		breakdownItems,
		isLeafNode: false,
		name: `${attribute.name} [${i}]`,
		previousValue: getRandom(1000, 10000),
		value: getRandom(1000, 10000),
	};
}

export function getDummyBreakdowns(event, order, breakdowns, currentLevel = 1) {
	const count = getRandom(0, 5);
	const breakdownsArr = [];

	for (let index = 0; index < count; index++) {
		breakdownsArr.push(
			getDummyBreakdown(index, event, breakdowns, order, currentLevel)
		);
	}

	return breakdownsArr;
}

export function getDummyBreakdownData(event, breakdowns, order) {
	return {
		breakdownItems: order.length
			? getDummyBreakdowns(event, order, breakdowns)
			: getDummyEvent(event),
		count: 25,
		page: 1,
		value: 100000,
	};
}

export const mockBreakdownData = (
	comparePrevious = false,
	compareSegment = false,
	compareEvent = false
) => ({
	breakdownItems: [
		{
			breakdownItems: [
				{
					breakdownItems: [
						{
							name: 'All Individuals',
							value: 1717,
							...(comparePrevious ? {previousValue: 2633} : {}),
						},
						...(compareSegment
							? [
									{
										name: 'Segmented',
										value: 1650,
										...(comparePrevious
											? {previousValue: 2400}
											: {}),
									},
								]
							: []),
					],
					leafNode: true,
					name: 'View Article',
					value: 3367,
					...(comparePrevious ? {previousValue: 5033} : {}),
				},

				...(compareEvent
					? [
							{
								breakdownItems: [
									{
										name: 'All Individuals',
										value: 700,
										...(comparePrevious
											? {previousValue: 800}
											: {}),
									},
									...(compareSegment
										? [
												{
													name: 'Segmented',
													value: 500,
													...(comparePrevious
														? {previousValue: 700}
														: {}),
												},
											]
										: []),
								],
								leafNode: true,
								name: 'Read Article',
								value: 1200,
								...(comparePrevious
									? {previousValue: 1500}
									: {}),
							},
						]
					: []),
			],
			leafNode: false,
			name: 'articleTitle [0]',
			value: 3367,
			...(comparePrevious ? {previousValue: 5033} : {}),
		},
	],
	count: 1,
	page: 1,
	value: 5033,
	...(comparePrevious ? {previousValue: 2400} : {}),
});
