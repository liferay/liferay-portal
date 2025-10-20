module.exports = {
	collectCoverage: true,
	collectCoverageFrom: [
		'!<rootDir>/src/main/js/(api-docs|ui-kit)/**/*.{js,jsx,tsx}',
		'!<rootDir>/src/main/js/shared/api/*.{js,ts}',
		'<rootDir>/src/main/js/**/components/**/*.{js,jsx,tsx}',
		'<rootDir>/src/main/js/**/hocs/mappers/**/*.{js,jsx,tsx}',
		'<rootDir>/src/main/js/**/util/**/*.{js,jsx,tsx}',
		'<rootDir>/src/main/js/(contacts|settings|sites)/(hoc|pages)/**/*.{js,jsx,tsx}',
		'<rootDir>/src/main/js/shared/**/*.{js,jsx,tsx}'
	],
	coverageThreshold: {
		global: {
			branches: 60,
			functions: 60,
			lines: 75,
			statements: 70
		}
	},
	globals: {
		CEREBRO_PATHS_GEOMAP_KEY: '',
		FARO_DEV_MODE: false,
		FARO_ENV: 'local',
		faroConstants: {
			activityActions: {
				comments: 3,
				downloads: 0,
				impressions: 4,
				submissions: 1,
				visits: 2
			},
			channelPermissionTypes: {
				allUsers: 0,
				selectUsers: 1
			},
			clauseOperators: {
				0: {
					id: 0,
					labels: ['value'],
					name: 'after',
					supportedTypes: ['Date']
				},
				1: {
					id: 1,
					labels: ['value'],
					name: 'before',
					supportedTypes: ['Date']
				},
				10: {
					id: 10,
					labels: [],
					name: 'is-not-known',
					supportedTypes: ['Boolean', 'Date', 'Number', 'Text']
				},
				13: {
					id: 13,
					labels: ['action', 'timeSpan'],
					name: 'behavior-equals',
					supportedTypes: []
				},
				14: {
					id: 14,
					labels: ['value'],
					name: 'greater-than-or-equals',
					supportedTypes: ['Number']
				},
				15: {
					id: 15,
					labels: ['value'],
					name: 'less-than-or-equals',
					supportedTypes: ['Number']
				},
				16: {
					id: 16,
					labels: ['action', 'timeSpan'],
					name: 'behavior-not-equals',
					supportedTypes: []
				},
				2: {
					id: 2,
					labels: ['start-date', 'end-date'],
					name: 'between',
					supportedTypes: ['Date']
				},
				3: {
					id: 3,
					labels: ['value'],
					name: 'contains',
					supportedTypes: ['Text']
				},
				4: {
					id: 4,
					labels: ['value'],
					name: 'equals',
					supportedTypes: ['Boolean', 'Date', 'Number']
				},
				5: {
					id: 5,
					labels: ['value'],
					name: 'greater-than',
					supportedTypes: ['Number']
				},
				6: {
					id: 6,
					labels: [],
					name: 'is-known',
					supportedTypes: ['Boolean', 'Date', 'Number', 'Text']
				},
				7: {
					id: 7,
					labels: ['value'],
					name: 'less-than',
					supportedTypes: ['Number']
				},
				8: {
					id: 8,
					labels: ['value'],
					name: 'does-not-contain',
					supportedTypes: ['Text']
				},
				9: {
					id: 9,
					labels: ['value'],
					name: 'not-equals',
					supportedTypes: []
				}
			},
			contactsCardTemplateTypes: {
				cardTypes: {
					associatedSegments: 10,
					conversionHealth: 9,
					coworkers: 11,
					info: 1,
					interests: 12,
					profile: 0,
					segmentDistribution: 3,
					segmentGrowth: 4,
					segmentMembership: 2,
					similar: 13
				},
				graphTypes: {
					bar: 1,
					pie: 2
				},
				profileCardLayoutTypes: {
					horizontal: 0,
					noAvatar: 1,
					vertical: 2
				},
				segmentsMembershipCardOrders: {
					alphabetical: 0,
					numberOfMembers: 1
				}
			},
			credentialTypes: {
				oAuth1: 'OAuth 1 Authentication',
				oAuth2: 'OAuth 2 Authentication'
			},
			criterionOperators: {
				operatorAnd: 11,
				operatorOr: 12
			},
			criterionTypes: {
				behavior: 1,
				demographic: 2,
				logical: 0
			},
			dataSourceProgressStatuses: {
				completed: 'COMPLETED',
				failed: 'FAILED',
				inProgress: 'IN_PROGRESS',
				started: 'STARTED'
			},
			dataSourceStatuses: {
				active: 'ACTIVE',
				inactive: 'INACTIVE'
			},
			dataSourceTypes: {
				csv: 'CSV',
				liferay: 'LIFERAY',
				salesforce: 'SALESFORCE'
			},
			documentationURLs: {
				addLiferayDataSource:
					'https://learn.liferay.com/analytics-cloud/latest/en/connecting-data-sources/connecting-liferay-dxp-to-analytics-cloud.html',
				base: 'https://help.liferay.com/hc/en-us'
			},
			entityTypes: {
				account: 0,
				accountsSegment: 3,
				asset: 5,
				dataSource: 1,
				individual: 2,
				individualsSegment: 4,
				page: 6
			},
			faroURL: 'http://localhost:3000',
			fieldContexts: {
				behaviors: 'behaviors',
				demographics: 'demographics',
				organization: 'organization'
			},
			fieldOwnerTypes: {
				account: 'account',
				individual: 'individual',
				organization: 'organization'
			},
			fieldTypes: {
				boolean: 'Boolean',
				date: 'Date',
				number: 'Number',
				string: 'Text'
			},
			pagination: {
				cur: 1,
				delta: 2,
				deltaValues: [1, 2, 3, 4],
				orderAscending: 'asc',
				orderDefault: 'asc',
				orderDescending: 'desc'
			},
			preferencesScopes: {
				group: 'group',
				user: 'user'
			},
			projectLocations: {
				AS1: 'MUMBAI, INDIA',
				EU2: 'LONDON, ENGLAND',
				EU3: 'FRANKFURT, GERMANY',
				SA: 'SÃO PAULO, BRAZIL',
				US: 'OREGON, USA'
			},
			projectOperations: {
				restart: 'restart',
				stop: 'stop'
			},
			projectStates: {
				autoRedeployFailed: 'AUTO_REDEPLOY_FAILED',
				maintenance: 'MAINTENANCE',
				notReady: 'NOT READY',
				ready: 'READY',
				scheduled: 'SCHEDULED',
				unavailable: 'UNAVAILABLE'
			},
			segmentStates: {
				disabled: 'DISABLED',
				inProgress: 'IN_PROGRESS',
				ready: 'READY'
			},
			segmentTypes: {
				dynamic: 'BATCH'
			},
			subscriptionPlans: {
				['Liferay Analytics Cloud Basic']: {
					baseSubscriptionPlan: null,
					individualsLimit: 1000,
					name: 'Liferay Analytics Cloud Basic',
					pageViewsLimit: 300000,
					price: 0
				},
				['Liferay Analytics Cloud Business']: {
					baseSubscriptionPlan: null,
					individualsLimit: 10000,
					name: 'Liferay Analytics Cloud Business',
					pageViewsLimit: 5000000,
					price: 7500
				},
				['Liferay Analytics Cloud Business Contacts']: {
					baseSubscriptionPlan: 'Liferay Analytics Cloud Business',
					individualsLimit: 5000,
					name: 'Liferay Analytics Cloud Business Contacts',
					pageViewsLimit: 0,
					price: 1500
				},
				['Liferay Analytics Cloud Business Tracked Pages']: {
					baseSubscriptionPlan: 'Liferay Analytics Cloud Business',
					individualsLimit: 0,
					name: 'Liferay Analytics Cloud Business Tracked Pages',
					pageViewsLimit: 5000000,
					price: 750
				},
				['Liferay Analytics Cloud Enterprise']: {
					baseSubscriptionPlan: null,
					individualsLimit: 100000,
					name: 'Liferay Analytics Cloud Enterprise',
					pageViewsLimit: 60000000,
					price: 20000
				},
				['Liferay Analytics Cloud Enterprise Contacts']: {
					baseSubscriptionPlan: 'Liferay Analytics Cloud Enterprise',
					individualsLimit: 5000,
					name: 'Liferay Analytics Cloud Enterprise Contacts',
					pageViewsLimit: 0,
					price: 500
				},
				['Liferay Analytics Cloud Enterprise Tracked Pages']: {
					baseSubscriptionPlan: 'Liferay Analytics Cloud Enterprise',
					individualsLimit: 0,
					name: 'Liferay Analytics Cloud Enterprise Tracked Pages',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS - Business Plan']: {
					baseSubscriptionPlan: null,
					individualsLimit: 10000,
					name: 'Liferay SaaS - Business Plan',
					pageViewsLimit: 5000000,
					price: 7500
				},
				['Liferay SaaS - CSP - Custom User Tier']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'Liferay SaaS - CSP - Custom User Tier',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS - CSP - Custom User Tier - Extra User']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'Liferay SaaS - CSP - Custom User Tier - Extra User',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS - CSP - Up to 100 Users']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'Liferay SaaS - CSP - Up to 100 Users',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS - CSP - Up to 100 Users - Extra User']: {
					baseSubscriptionPlan: 'Liferay SaaS - CSP - Up to 100 Users',
					individualsLimit: 0,
					name: 'Liferay SaaS - CSP - Up to 100 Users - Extra User',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS - CSP - Up to 10K Users']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'Liferay SaaS - CSP - Up to 10K Users',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS - CSP - Up to 10K Users - Extra User']: {
					baseSubscriptionPlan: 'Liferay SaaS - CSP - Up to 10K Users',
					individualsLimit: 0,
					name: 'Liferay SaaS - CSP - Up to 10K Users - Extra User',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS - CSP - Up to 1K Users']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'Liferay SaaS - CSP - Up to 1K Users',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS - CSP - Up to 1K Users - Extra User']: {
					baseSubscriptionPlan: 'Liferay SaaS - CSP - Up to 1K Users',
					individualsLimit: 0,
					name: 'Liferay SaaS - CSP - Up to 1K Users - Extra User',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS - CSP - Up to 20K Users']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'Liferay SaaS - CSP - Up to 20K Users',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS - CSP - Up to 20K Users - Extra User']: {
					baseSubscriptionPlan: 'Liferay SaaS - CSP - Up to 20K Users',
					individualsLimit: 0,
					name: 'Liferay SaaS - CSP - Up to 20K Users - Extra User',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS - CSP - Up to 500 Users']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'Liferay SaaS - CSP - Up to 500 Users',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS - CSP - Up to 500 Users - Extra User']: {
					baseSubscriptionPlan: 'Liferay SaaS - CSP - Up to 500 Users',
					individualsLimit: 0,
					name: 'Liferay SaaS - CSP - Up to 500 Users - Extra User',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS - CSP - Up to 5K Users']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'Liferay SaaS - CSP - Up to 5K Users',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS - CSP - Up to 5K Users - Extra User']: {
					baseSubscriptionPlan: 'Liferay SaaS - CSP - Up to 5K Users',
					individualsLimit: 0,
					name: 'Liferay SaaS - CSP - Up to 5K Users - Extra User',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS - Enterprise Plan']: {
					baseSubscriptionPlan: null,
					individualsLimit: 100000,
					name: 'Liferay SaaS - Enterprise Plan',
					pageViewsLimit: 60000000,
					price: 20000
				},
				['Liferay SaaS - Pro Plan']: {
					baseSubscriptionPlan: null,
					individualsLimit: 1000,
					name: 'Liferay SaaS - Pro Plan',
					pageViewsLimit: 300000,
					price: 0
				},
				['Liferay SaaS Subscription - Engage Site']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'Liferay SaaS Subscription - Engage Site',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS Subscription - Support Site']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'Liferay SaaS Subscription - Support Site',
					pageViewsLimit: 5000000,
					price: 250
				},
				['Liferay SaaS Subscription - Transact Site']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'Liferay SaaS Subscription - Transact Site',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC - Business Plan']: {
					baseSubscriptionPlan: null,
					individualsLimit: 10000,
					name: 'LXC - Business Plan',
					pageViewsLimit: 5000000,
					price: 7500
				},
				['LXC - CSP - Custom User Tier']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'LXC - CSP - Custom User Tier',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC - CSP - Custom User Tier - Extra User']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'LXC - CSP - Custom User Tier - Extra User',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC - CSP - Up to 100 Users']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'LXC - CSP - Up to 100 Users',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC - CSP - Up to 100 Users - Extra User']: {
					baseSubscriptionPlan: 'LXC - CSP - Up to 100 Users',
					individualsLimit: 0,
					name: 'LXC - CSP - Up to 100 Users - Extra User',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC - CSP - Up to 10K Users']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'LXC - CSP - Up to 10K Users',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC - CSP - Up to 10K Users - Extra User']: {
					baseSubscriptionPlan: 'LXC - CSP - Up to 10K Users',
					individualsLimit: 0,
					name: 'LXC - CSP - Up to 10K Users - Extra User',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC - CSP - Up to 1K Users']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'LXC - CSP - Up to 1K Users',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC - CSP - Up to 1K Users - Extra User']: {
					baseSubscriptionPlan: 'LXC - CSP - Up to 1K Users',
					individualsLimit: 0,
					name: 'LXC - CSP - Up to 1K Users - Extra User',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC - CSP - Up to 20K Users']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'LXC - CSP - Up to 20K Users',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC - CSP - Up to 20K Users - Extra User']: {
					baseSubscriptionPlan: 'LXC - CSP - Up to 20K Users',
					individualsLimit: 0,
					name: 'LXC - CSP - Up to 20K Users - Extra User',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC - CSP - Up to 500 Users']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'LXC - CSP - Up to 500 Users',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC - CSP - Up to 500 Users - Extra User']: {
					baseSubscriptionPlan: 'LXC - CSP - Up to 500 Users',
					individualsLimit: 0,
					name: 'LXC - CSP - Up to 500 Users - Extra User',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC - CSP - Up to 5K Users']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'LXC - CSP - Up to 5K Users',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC - CSP - Up to 5K Users - Extra User']: {
					baseSubscriptionPlan: 'LXC - CSP - Up to 5K Users',
					individualsLimit: 0,
					name: 'LXC - CSP - Up to 5K Users - Extra User',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC - Enterprise Plan']: {
					baseSubscriptionPlan: null,
					individualsLimit: 100000,
					name: 'LXC - Enterprise Plan',
					pageViewsLimit: 60000000,
					price: 20000
				},
				['LXC - Pro Plan']: {
					baseSubscriptionPlan: null,
					individualsLimit: 1000,
					name: 'LXC - Pro Plan',
					pageViewsLimit: 300000,
					price: 0
				},
				['LXC Subscription - Engage Site']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'LXC Subscription - Engage Site',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC Subscription - Support Site']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'LXC Subscription - Support Site',
					pageViewsLimit: 5000000,
					price: 250
				},
				['LXC Subscription - Transact Site']: {
					baseSubscriptionPlan: null,
					individualsLimit: 0,
					name: 'LXC Subscription - Transact Site',
					pageViewsLimit: 5000000,
					price: 250
				}
			},
			subscriptionStatuses: {
				approaching: 1,
				ok: 0,
				over: 2
			},
			timeIntervals: {
				day: 'day',
				month: 'month',
				quarter: 'quarter',
				week: 'week',
				year: 'year'
			},
			timeSpans: {
				'1YearAgo': 'lastYear',
				'30DaysAgo': 'last30days',
				'7DaysAgo': 'last7days',
				allTime: 'ever',
				today: 'today'
			},
			userName: 'Test Test',
			userRoleNames: {
				administrator: 'Site Administrator',
				member: 'Site Member',
				owner: 'Site Owner'
			},
			userStatuses: {
				approved: 0,
				pending: 1
			}
		},
		'ts-jest': {
			tsconfig: 'tsconfig.test.json'
		}
	},
	moduleNameMapper: {
		'^assets(.*)$': '<rootDir>/src/main/js/assets$1',
		'^cerebro-shared(.*)$': '<rootDir>/src/main/js/cerebro-shared$1',
		'^clay-charts-react(.*)$': '<rootDir>/src/main/js/clay-charts-react$1',
		'^commerce(.*)$': '<rootDir>/src/main/js/commerce$1',
		'^contacts(.*)$': '<rootDir>/src/main/js/contacts$1',
		'^dnd-core$': 'dnd-core/dist/cjs',
		'^event-analysis(.*)$': '<rootDir>/src/main/js/event-analysis$1',
		'^experiments(.*)$': '<rootDir>/src/main/js/experiments$1',
		'^home(.*)$': '<rootDir>/src/main/js/home$1',
		'^individual(.*)$': '<rootDir>/src/main/js/individual$1',
		'^react-dnd$': 'react-dnd/dist/cjs',
		'^react-dnd-html5-backend$': 'react-dnd-html5-backend/dist/cjs',
		'^route-middleware(.*)$': '<rootDir>/src/main/js/route-middleware$1',
		'^segment(.*)$': '<rootDir>/src/main/js/segment$1',
		'^settings(.*)$': '<rootDir>/src/main/js/settings$1',
		'^shared(.*)$': '<rootDir>/src/main/js/shared$1',
		'^sites(.*)': '<rootDir>/src/main/js/sites$1',
		'^test(.*)': '<rootDir>/src/main/js/test$1',
		'^touchpoints(.*)': '<rootDir>/src/main/js/touchpoints$1',
		'^ui-kit(.*)$': '<rootDir>/src/main/js/ui-kit$1'
	},
	setupFilesAfterEnv: ['<rootDir>/src/main/js/test/setup.js'],
	testEnvironment: 'jest-environment-jsdom-fifteen',
	testResultsProcessor: '@liferay/jest-junit-reporter',
	testURL: 'http://liferay.com',
	timers: 'fake',
	transform: {
		'^.+\\.jsx?$': 'babel-jest',
		'^.+\\.tsx?$': 'ts-jest'
	}
};
