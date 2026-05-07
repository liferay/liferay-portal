/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const activityLogDevEnvData = {
	userSessions: {
		totalEvents: 3,
		userSessions: [
			{
				userName: 'John Doe',
				userSessionEvents: [
					{
						assetTitle: 'document_a',
						createDate: '2026-03-06T08:00:00Z',
						emailAddressHashed: 'john.doe@example.com',
						name: 'commentPosted',
					},
					{
						assetTitle: 'document_b',
						createDate: '2026-03-06T09:00:00Z',
						emailAddressHashed: 'john.doe@example.com',
						name: 'documentUploaded',
					},
				],
			},
			{
				userName: 'Paul Gerome',
				userSessionEvents: [
					{
						assetTitle: 'document_c',
						createDate: '2026-03-07T10:00:00Z',
						emailAddressHashed: 'paul.gerome@example.com',
						name: 'pageViewed',
					},
				],
			},
		],
	},
};

export const recentEngagementChartDevEnvData = {
	siteVisitors: {
		histogram: {
			asymmetricComparison: 0,
			histogramMetrics: [
				{
					key: '2026-02-20T00:00:00Z',
					value: 400,
					valueKey: 'numberOfVisits',
				},
				{
					key: '2026-02-21T00:00:00Z',
					value: 300,
					valueKey: 'numberOfVisits',
				},
				{
					key: '2026-02-22T00:00:00Z',
					value: 320,
					valueKey: 'numberOfVisits',
				},
				{
					key: '2026-02-23T00:00:00Z',
					value: 200,
					valueKey: 'numberOfVisits',
				},
				{
					key: '2026-02-24T00:00:00Z',
					value: 278,
					valueKey: 'numberOfVisits',
				},
				{
					key: '2026-02-25T00:00:00Z',
					value: 189,
					valueKey: 'numberOfVisits',
				},
				{
					key: '2026-02-26T00:00:00Z',
					value: 199,
					valueKey: 'numberOfVisits',
				},
			],
			total: 1886,
		},
	},
};

export const timelineEngagementChartDevEnvData = {
	siteSessions: {
		histogram: {
			asymmetricComparison: 0,
			histogramMetrics: [
				{
					key: '2026-02-20T00:00:00Z',
					value: 400,
					valueKey: 'timeSpent',
				},
				{
					key: '2026-02-21T00:00:00Z',
					value: 300,
					valueKey: 'timeSpent',
				},
				{
					key: '2026-02-22T00:00:00Z',
					value: 320,
					valueKey: 'timeSpent',
				},
				{
					key: '2026-02-23T00:00:00Z',
					value: 200,
					valueKey: 'timeSpent',
				},
				{
					key: '2026-02-24T00:00:00Z',
					value: 278,
					valueKey: 'timeSpent',
				},
				{
					key: '2026-02-25T00:00:00Z',
					value: 189,
					valueKey: 'timeSpent',
				},
				{
					key: '2026-02-26T00:00:00Z',
					value: 199,
					valueKey: 'timeSpent',
				},
			],
			total: 1886,
		},
	},
};

export const frequencyChartDevEnvData = {
	visitFrequency: {
		totalCount: 650,
		visitFrequencyItems: [
			{count: 320, name: 'DAILY'},
			{count: 30, name: 'WEEKLY'},
			{count: 100, name: 'BIWEEKLY'},
			{count: 200, name: 'MONTHLY'},
		],
	},
};

export const latestActivityDevEnvData = {
	events: {
		eventEntries: [
			{
				createDate: '2026-03-26T14:30:00Z',
				emailAddressHashed: 'john.doe@example.com',
				individualName: 'John Doe',
				name: 'Created a new document',
			},
		],
	},
};

export const mostActiveVisitorsDevEnvData = {
	mostActiveVisitors: {
		mostActiveVisitors: [
			{
				activitiesCount: 150,
				emailAddress: 'john.doe@liferay.com',
				firstName: 'John',
				id: '1',
				lastName: 'Doe',
			},
		],
		total: 1,
	},
};

export const roomDocumentsStatisticsDevEnvData = {
	documents: {
		documentMetrics: [
			{
				assetId: '1',
				assetTitle: 'pdf_test',
				commentsMetric: {value: 0},
				downloadsMetric: {value: 324},
				impressionMadeMetric: {value: 89},
				lastViewedMetric: {value: 1740929400000},
				ratingsMetric: {value: 0},
				urls: ['pdf_test.pdf'],
				usersInvolvedMetric: {value: 4},
			},
			{
				assetId: '2',
				assetTitle: 'doc_test2',
				commentsMetric: {value: 0},
				downloadsMetric: {value: 342},
				impressionMadeMetric: {value: 34},
				lastViewedMetric: {value: 1741015800000},
				ratingsMetric: {value: 0},
				urls: ['doc_test2.docx'],
				usersInvolvedMetric: {value: 3},
			},
			{
				assetId: '3',
				assetTitle: 'pdf_test2',
				commentsMetric: {value: 0},
				downloadsMetric: {value: 45},
				impressionMadeMetric: {value: 34},
				lastViewedMetric: {value: 1743694200000},
				ratingsMetric: {value: 0},
				urls: ['pdf_test2.pdf'],
				usersInvolvedMetric: {value: 4},
			},
			{
				assetId: '4',
				assetTitle: 'document_test',
				commentsMetric: {value: 0},
				downloadsMetric: {value: 23},
				impressionMadeMetric: {value: 23},
				lastViewedMetric: {value: 1741015800000},
				ratingsMetric: {value: 0},
				urls: ['document_test.docx'],
				usersInvolvedMetric: {value: 2},
			},
			{
				assetId: '5',
				assetTitle: 'pdf_test3',
				commentsMetric: {value: 0},
				downloadsMetric: {value: 768},
				impressionMadeMetric: {value: 67},
				lastViewedMetric: {value: 1741534200000},
				ratingsMetric: {value: 0},
				urls: ['pdf_test3.pdf'],
				usersInvolvedMetric: {value: 4},
			},
			{
				assetId: '6',
				assetTitle: 'pdf_test4',
				commentsMetric: {value: 0},
				downloadsMetric: {value: 324},
				impressionMadeMetric: {value: 85},
				lastViewedMetric: {value: 1741188600000},
				ratingsMetric: {value: 0},
				urls: ['pdf_test4.pdf'],
				usersInvolvedMetric: {value: 3},
			},
		],
		total: 6,
	},
};

export const roomStatisticsDevEnvData = {
	identityActivity: {count: 10},
	identityComment: {count: 5},
	siteVisitorBehavior: {
		knownVisitors: 20,
		totalSessionDuration: 45,
		visitors: 100,
	},
};
