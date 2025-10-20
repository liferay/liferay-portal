import AcquisitionsQuery from 'shared/queries/AcquisitionsQuery';
import AssetAppearsOnQuery from 'shared/queries/AssetAppearsOnQuery';
import BlockedCustomEventDefinitionsQuery from 'settings/definitions/events/queries/BlockedCustomEventDefinitionsQuery';
import EventAnalysisResultQuery from 'event-analysis/queries/EventAnalysisResultQuery';
import EventAttributeDefinitionQuery, {
	EVENT_ATTRIBUTE_DEFINITION_WITH_RECENT_VALUES_QUERY
} from 'event-analysis/queries/EventAttributeDefinitionQuery';
import EventAttributeDefinitionsQuery from 'event-analysis/queries/EventAttributeDefinitionsQuery';
import EventAttributeValuesQuery from 'event-analysis/queries/EventAttributeValuesQuery';
import EventDefinitionQuery from 'event-analysis/queries/EventDefinitionQuery';
import EventDefinitionsQuery from 'event-analysis/queries/EventDefinitionsQuery';
import EventMetricQuery from 'shared/queries/EventMetricQuery';
import EventPropertiesQuery from 'segment/segment-editor/dynamic/queries/EventPropertiesQuery';
import getInterestsQuery from 'contacts/queries/InterestsQuery';
import IndividualInterestsQuery from 'shared/queries/IndividualInterestsQuery';
import IndividualMetricsQuery from 'shared/queries/IndividualMetricsQuery';
import OrganizationsQuery from 'segment/segment-editor/dynamic/queries/OrganizationsQuery';
import PagePathQuery from 'shared/queries/PagePathQuery';
import PreferenceQuery from 'shared/queries/PreferenceQuery';
import RecommendationActivitiesQuery from 'settings/recommendations/queries/RecommendationActivitiesQuery';
import RecommendationJobRunsQuery from 'settings/recommendations/queries/RecommendationJobRunsQuery';
import RecommendationPageAssetsQuery from 'settings/recommendations/queries/RecommendationPageAssetsQuery';
import RecommendationQuery from 'settings/recommendations/queries/RecommendationQuery';
import SitesDashboardQuery from 'shared/queries/SitesDashboardQuery';
import SitesTopPagesQuery from 'shared/queries/SitesTopPagesQuery';
import SuppressedUsersListQuery from 'settings/data-privacy/queries/SuppressedUsersListQuery';
import TimeRangeQuery from 'shared/queries/TimeRangeQuery';
import TouchpointsQuery from 'shared/queries/TouchpointsQuery';
import UserSessionQuery from 'shared/queries/UserSessionQuery';
import {
	AssetMetricQuery,
	AssetTabsQuery,
	SitesMetricQuery,
	SitesTabsQuery
} from 'shared/components/metric-card/queries';
import {
	AttributeTypes,
	DataTypes,
	DateGroupings
} from 'event-analysis/utils/types';
import {
	CompositionTypes,
	DATA_RETENTION_PERIOD_KEY,
	OrderByDirections,
	RangeKeyTimeRanges,
	THIRTEEN_MONTHS
} from 'shared/util/constants';
import {COUNT, NAME} from 'shared/util/pagination';
import {
	EventAnalysisListQuery,
	EventAnalysisQuery
} from 'event-analysis/queries/EventAnalysisQuery';
import {EventTypes} from 'event-analysis/utils/types';
import {
	EXPERIMENT_DRAFT_QUERY,
	EXPERIMENT_QUERY,
	EXPERIMENT_STATUS_QUERY
} from 'experiments/queries/ExperimentQuery';
import {getSafeRangeSelectors} from 'shared/util/util';
import {INTERVAL_KEY_MAP} from 'shared/util/time';
import {isArray, mapValues, range} from 'lodash';
import {PageAudienceReportQuery} from 'shared/components/audience-report/queries';
import {SegmentPageViewsQuery} from 'shared/queries/SegmentPageViewsQuery';

const METRIC_TYPENAME_MAP = {
	histogram: 'HistogramMetric',
	trend: 'Trend'
};

export function mockAssetAppearsOnReq(variables, empty) {
	let assetMetrics = [];

	if (!empty) {
		assetMetrics = [
			{
				__typename: 'BlogMetric',
				assetId:
					'http://liferay.com/web/test/abc/123/a42d8ae1-d145-40da-8150-3fe28deb04ad',
				assetTitle: 'a42d8ae1-d145-40da-8150-3fe28deb04ad',
				selectedMetrics: [
					{
						__typename: 'AssetMetric',
						name: 'viewsMetric',
						value: 113948
					}
				]
			},
			{
				__typename: 'BlogMetric',
				assetId:
					'http://liferay.com/web/test/abc/123/a9bd5ff0-d623-4743-a2b5-3dbafd8d2a86',
				assetTitle: 'a9bd5ff0-d623-4743-a2b5-3dbafd8d2a86',
				selectedMetrics: [
					{
						__typename: 'AssetMetric',
						name: 'viewsMetric',
						value: 912285
					}
				]
			},
			{
				__typename: 'BlogMetric',
				assetId:
					'http://liferay.com/web/test/abc/123/b8cbb4b5-5a1f-425d-a06b-c0544e2991ce',
				assetTitle: 'b8cbb4b5-5a1f-425d-a06b-c0544e2991ce',
				selectedMetrics: [
					{
						__typename: 'AssetMetric',
						name: 'viewsMetric',
						value: 431627
					}
				]
			},
			{
				__typename: 'BlogMetric',
				assetId:
					'http://liferay.com/web/test/abc/123/280b69a6-b2dc-4d8f-a0d4-421b450c257b',
				assetTitle: '280b69a6-b2dc-4d8f-a0d4-421b450c257b',
				selectedMetrics: [
					{
						__typename: 'AssetMetric',
						name: 'viewsMetric',
						value: 273970
					}
				]
			},
			{
				__typename: 'BlogMetric',
				assetId:
					'http://liferay.com/web/test/abc/123/a9ca649d-fc1f-4499-ab8a-c39f2ca1b024',
				assetTitle: 'a9ca649d-fc1f-4499-ab8a-c39f2ca1b024',
				selectedMetrics: [
					{
						__typename: 'AssetMetric',
						name: 'viewsMetric',
						value: 95519
					}
				]
			}
		];
	}

	return {
		request: {
			query: AssetAppearsOnQuery,
			variables: {
				assetId: 'myBlogId',
				channelId: '123',
				page: 1,
				rangeEnd: null,
				rangeKey: 30,
				rangeStart: null,
				size: 2,
				start: 0,
				title: 'Blog Title',
				...variables
			}
		},
		result: {
			data: {
				assetPages: {
					__typename: 'AssetPages',
					assetMetrics,
					total: empty ? 0 : 1000
				}
			}
		}
	};
}

export function mockAssetMetricReq({empty, metricName, queryName, rangeKey}) {
	return {
		request: {
			query: AssetMetricQuery(queryName)(metricName),
			variables: {
				assetId: '123',
				channelId: '456',
				devices: 'Any',
				interval: 'D',
				location: 'Any',
				rangeEnd: null,
				rangeKey,
				rangeStart: null,
				title: 'My awesome asset',
				touchpoint: 'https://liferay.com'
			}
		},
		result: {
			data: {
				[queryName]: {
					__typename: 'AssetMetric',
					[metricName]: {
						__typename: 'Metric',
						histogram: {
							__typename: 'HistogramMetricBag',
							asymmetricComparison: false,
							metrics: empty
								? []
								: [
										{
											__typename: 'HistogramMetric',
											key: '2024-01-17T18:00',
											previousValue: 0,
											previousValueKey:
												'2024-01-16T18:00',
											trend: {
												__typename: 'Trend',
												percentage: null,
												trendClassification: 'NEUTRAL'
											},
											value: 0,
											valueKey: '2024-01-17T18:00'
										}
								  ],
							total: empty ? 0 : 5
						},
						previousValue: null,
						trend: {
							__typename: 'Trend',
							percentage: null,
							trendClassification: 'NEUTRAL'
						},
						value: 0
					}
				}
			}
		}
	};
}

export function mockAssetTabsReq({metrics, name, rangeKey}) {
	const assetMetrics = {};

	metrics.forEach(metric => {
		assetMetrics[metric.name] = {
			__typename: 'Metric',
			previousValue: null,
			trend: {
				__typename: 'Trend',
				percentage: null,
				trendClassification: 'NEUTRAL'
			},
			value: 100
		};
	});

	return {
		request: {
			query: AssetTabsQuery(metrics, name),
			variables: {
				assetId: '123',
				channelId: '456',
				devices: 'Any',
				interval: 'D',
				location: 'Any',
				rangeEnd: null,
				rangeKey,
				rangeStart: null,
				title: 'My awesome asset',
				touchpoint: 'https://liferay.com'
			}
		},
		result: {
			data: {
				[name]: {
					__typename: 'AssetMetric',
					...assetMetrics
				}
			}
		}
	};
}

export function mockAudienceReportReq({queryProps}) {
	return {
		request: {
			query: PageAudienceReportQuery(queryProps),
			variables: {
				channelId: '456',
				devices: 'Any',
				location: 'Any',
				rangeEnd: null,
				rangeKey: 30,
				rangeStart: null,
				title: 'Home Page',
				touchpoint: 'https://www.liferay.com'
			}
		},
		result: {
			data: {
				page: {
					__typename: 'PageMetric',
					viewsMetric: {
						__typename: 'Metric',
						audienceReport: {
							__typename: 'AudienceReport',
							anonymousUsersCount: 12804,
							knownUsersCount: 98,
							nonsegmentedKnownUsersCount: 96,
							segmentedAnonymousUsersCount: null,
							segmentedKnownUsersCount: 2
						},
						segment: {
							__typename: 'MetricBag',
							metrics: [
								{
									__typename: 'Metric',
									value: 2,
									valueKey: 'UK Visitors'
								}
							],
							total: 1
						}
					}
				}
			}
		}
	};
}

export function mockExperimentDraftReq() {
	return {
		request: {
			query: EXPERIMENT_DRAFT_QUERY,
			variables: {
				channelId: '2000',
				experimentId: '123'
			}
		},
		result: {
			data: {
				experiment: {
					__typename: 'Experiment',
					dxpExperienceName: 'Default',
					dxpSegmentName: 'Anyone',
					dxpVariants: [],
					goal: {
						__typename: 'Goal',
						metric: 'CLICK_RATE',
						target: ''
					},
					id: '123',
					name: 'draw',
					pageURL: 'https://www.beryl.com/experiment-test',
					status: 'DRAFT'
				}
			}
		}
	};
}

export function mockExperimentReq({
	publishable = false,
	publishedDXPVariantId = null,
	status = 'RUNNING',
	type = 'AB',
	...experiment
} = {}) {
	return {
		request: {
			query: EXPERIMENT_QUERY,
			variables: {
				channelId: '2000',
				experimentId: '123'
			}
		},
		result: {
			data: {
				experiment: {
					__typename: 'Experiment',
					description: 'This is a description of the experiment',
					dxpExperienceName: 'Default',
					dxpSegmentName: 'Anyone',
					dxpVariants: [
						{
							__typename: 'DXPVariant',
							changes: 0,
							control: true,
							dxpVariantId: 'DEFAULT',
							dxpVariantName: 'Control',
							sessionsHistogram: [
								{
									__typename: 'HistogramMetric',
									key: '2023-10-02T00:00',
									value: 84
								},
								{
									__typename: 'HistogramMetric',
									key: '2023-10-03T00:00',
									value: 75
								}
							],
							trafficSplit: 50.0,
							uniqueVisitors: 402
						},
						{
							__typename: 'DXPVariant',
							changes: 0,
							control: false,
							dxpVariantId: '44167',
							dxpVariantName: 'Red Button',
							sessionsHistogram: [
								{
									__typename: 'HistogramMetric',
									key: '2023-10-02T00:00',
									value: 100
								},
								{
									__typename: 'HistogramMetric',
									key: '2023-10-03T00:00',
									value: 75
								}
							],
							trafficSplit: 50.0,
							uniqueVisitors: 394
						}
					],
					finishedDate: '2023-10-23T21:52:55.714Z',
					goal: {
						__typename: 'Goal',
						metric: 'CLICK_RATE',
						target: ''
					},
					id: '123',
					metrics: {
						__typename: 'ExperimentMetrics',
						completion: 99.0,
						elapsedDays: 16,
						estimatedDaysLeft: 1,
						variantMetrics: [
							{
								__typename: 'VariantMetrics',
								confidenceInterval: [36.0, 37.0],
								dxpVariantId: 'DEFAULT',
								improvement: 0.0,
								median: 37.0,
								probabilityToWin: 63.0
							},
							{
								__typename: 'VariantMetrics',
								confidenceInterval: [28.0, 31.0],
								dxpVariantId: '44167',
								improvement: -18.91891891891892,
								median: 30.0,
								probabilityToWin: 5070.0
							}
						]
					},
					metricsHistogram: [
						{
							__typename: 'ExperimentMetrics',
							processedDate: '2020-09-30T15:00',
							variantMetrics: [
								{
									__typename: 'VariantMetrics',
									confidenceInterval: [66.0, 73.0],
									dxpVariantId: 'DEFAULT',
									improvement: 0.0,
									median: 70.0
								},
								{
									__typename: 'VariantMetrics',
									confidenceInterval: [28.0, 31.0],
									dxpVariantId: '44167',
									improvement: -57.14285714285714,
									median: 30.0
								}
							]
						},
						{
							__typename: 'ExperimentMetrics',
							processedDate: '2020-10-19T15:00',
							variantMetrics: [
								{
									__typename: 'VariantMetrics',
									confidenceInterval: [66.0, 73.0],
									dxpVariantId: 'DEFAULT',
									improvement: 0.0,
									median: 70.0
								},
								{
									__typename: 'VariantMetrics',
									confidenceInterval: [28.0, 31.0],
									dxpVariantId: '44167',
									improvement: -57.14285714285714,
									median: 30.0
								}
							]
						}
					],
					modifiedDate: '2023-10-24T09:02:38.912Z',
					name: 'draw',
					pageURL: 'https://www.beryl.com/experiment-test',
					publishable,
					publishedDXPVariantId,
					sessions: 800,
					sessionsHistogram: [
						{
							__typename: 'HistogramMetric',
							key: '2020-09-30T00:00',
							value: 99
						}
					],
					startedDate: '2020-09-30T12:00:00.000Z',
					status,
					type,
					winnerDXPVariantId: null,
					...experiment
				}
			}
		}
	};
}

export function mockExperimentStatusReq({status}) {
	return {
		request: {
			fetchPolicy: 'network-only',
			query: EXPERIMENT_STATUS_QUERY,
			variables: {
				channelId: '2000',
				experimentId: '123'
			}
		},
		result: {
			data: {
				experiment: {
					__typename: 'Experiment',
					status
				}
			}
		}
	};
}

export function mockBag({items, itemTypeName, name, typeName}) {
	return {
		[name]: {
			__typename: typeName,
			[name]: items.map(item => ({
				__typename: itemTypeName,
				...item
			})),
			total: items.length
		}
	};
}

export function mockBlockedCustomEventDefinitionsReq(
	items,
	mockVariables = {}
) {
	return {
		request: {
			query: BlockedCustomEventDefinitionsQuery,
			variables: {
				keyword: '',
				page: 0,
				size: items.length,
				sort: {
					column: 'name',
					type: 'ASC'
				},
				...mockVariables
			}
		},
		result: {
			data: mockBag({
				items,
				itemTypeName: 'BlockedCustomEventDefinition',
				name: 'blockedCustomEventDefinitions',
				typeName: 'BlockedCustomEventDefinitionBag'
			})
		}
	};
}

export function mockCompositionBag({
	compositionBagName,
	compositions = [],
	maxCount = 0,
	total = 0,
	totalCount = 0
}) {
	const result = {
		__typename: 'CompositionBag',
		compositions: compositions.map(item => ({
			__typename: 'Composition',
			...item
		})),
		maxCount,
		total,
		totalCount
	};

	return compositionBagName ? {[compositionBagName]: result} : result;
}

export function mockDataControlTaskBag(items) {
	return mockBag({
		items,
		itemTypeName: 'DataControlTask',
		name: 'dataControlTasks',
		typeName: 'DataControlTaskBag'
	});
}

export function mockDataSourcesReq(dataSources = [], variables = {type: null}) {
	return {
		request: {
			query: SitesDashboardQuery,
			variables
		},
		result: {
			data: {
				dataSources: dataSources.map(dataSource => ({
					...dataSource,
					__typename: 'DataSource'
				}))
			}
		}
	};
}

export function mockAcquisitionsReq() {
	return {
		request: {
			query: AcquisitionsQuery,
			variables: {
				activeTabId: 'CHANNEL',
				channelId: '123',
				rangeEnd: null,
				rangeKey: 30,
				rangeStart: null,
				size: 5,
				start: 0
			}
		},
		result: {
			data: {
				acquisitions: {
					__typename: 'CompositionBag',
					compositions: [
						{
							__typename: 'Composition',
							count: 2686,
							name: 'direct'
						}
					],
					maxCount: 2686,
					total: 1,
					totalCount: 2686
				}
			}
		}
	};
}

export function mockSitesMetricReq(metricName, {rangeKey}) {
	return {
		request: {
			query: SitesMetricQuery(metricName),
			variables: {
				channelId: '456',
				devices: 'Any',
				interval: 'D',
				location: 'Any',
				rangeEnd: '',
				rangeKey,
				rangeStart: ''
			}
		},
		result: {
			data: {
				site: {
					__typename: 'SiteMetric',
					anonymousVisitorsMetric: {
						__typename: 'Metric',
						histogram: {
							__typename: 'HistogramMetricBag',
							asymmetricComparison: false,
							metrics: [
								{
									__typename: 'HistogramMetric',
									key: '2024-01-17T18:00',
									previousValue: 0,
									previousValueKey: '2024-01-16T18:00',
									trend: {
										__typename: 'Trend',
										percentage: null,
										trendClassification: 'NEUTRAL'
									},
									value: 0,
									valueKey: '2024-01-17T18:00'
								},
								{
									__typename: 'HistogramMetric',
									key: '2024-01-17T19:00',
									previousValue: 0,
									previousValueKey: '2024-01-16T19:00',
									trend: {
										__typename: 'Trend',
										percentage: null,
										trendClassification: 'NEUTRAL'
									},
									value: 0,
									valueKey: '2024-01-17T19:00'
								},
								{
									__typename: 'HistogramMetric',
									key: '2024-01-17T20:00',
									previousValue: 0,
									previousValueKey: '2024-01-16T20:00',
									trend: {
										__typename: 'Trend',
										percentage: null,
										trendClassification: 'NEUTRAL'
									},
									value: 0,
									valueKey: '2024-01-17T20:00'
								},
								{
									__typename: 'HistogramMetric',
									key: '2024-01-17T21:00',
									previousValue: 0,
									previousValueKey: '2024-01-16T21:00',
									trend: {
										__typename: 'Trend',
										percentage: null,
										trendClassification: 'NEUTRAL'
									},
									value: 0,
									valueKey: '2024-01-17T21:00'
								},
								{
									__typename: 'HistogramMetric',
									key: '2024-01-17T22:00',
									previousValue: 0,
									previousValueKey: '2024-01-16T22:00',
									trend: {
										__typename: 'Trend',
										percentage: null,
										trendClassification: 'NEUTRAL'
									},
									value: 0,
									valueKey: '2024-01-17T22:00'
								}
							],
							total: 5
						},
						previousValue: null,
						trend: {
							__typename: 'Trend',
							percentage: null,
							trendClassification: 'NEUTRAL'
						},
						value: 0
					},
					knownVisitorsMetric: {
						__typename: 'Metric',
						histogram: {
							__typename: 'HistogramMetricBag',
							asymmetricComparison: false,
							metrics: [
								{
									__typename: 'HistogramMetric',
									key: '2024-01-17T18:00',
									previousValue: 0,
									previousValueKey: '2024-01-16T18:00',
									trend: {
										__typename: 'Trend',
										percentage: null,
										trendClassification: 'NEUTRAL'
									},
									value: 0,
									valueKey: '2024-01-17T18:00'
								},
								{
									__typename: 'HistogramMetric',
									key: '2024-01-17T19:00',
									previousValue: 0,
									previousValueKey: '2024-01-16T19:00',
									trend: {
										__typename: 'Trend',
										percentage: null,
										trendClassification: 'NEUTRAL'
									},
									value: 0,
									valueKey: '2024-01-17T19:00'
								},
								{
									__typename: 'HistogramMetric',
									key: '2024-01-17T20:00',
									previousValue: 0,
									previousValueKey: '2024-01-16T20:00',
									trend: {
										__typename: 'Trend',
										percentage: null,
										trendClassification: 'NEUTRAL'
									},
									value: 0,
									valueKey: '2024-01-17T20:00'
								},
								{
									__typename: 'HistogramMetric',
									key: '2024-01-17T21:00',
									previousValue: 0,
									previousValueKey: '2024-01-16T21:00',
									trend: {
										__typename: 'Trend',
										percentage: null,
										trendClassification: 'NEUTRAL'
									},
									value: 0,
									valueKey: '2024-01-17T21:00'
								},
								{
									__typename: 'HistogramMetric',
									key: '2024-01-17T22:00',
									previousValue: 0,
									previousValueKey: '2024-01-16T22:00',
									trend: {
										__typename: 'Trend',
										percentage: null,
										trendClassification: 'NEUTRAL'
									},
									value: 0,
									valueKey: '2024-01-17T22:00'
								}
							],
							total: 5
						},
						previousValue: null,
						trend: {
							__typename: 'Trend',
							percentage: null,
							trendClassification: 'NEUTRAL'
						},
						value: 1
					},
					visitorsMetric: {
						__typename: 'Metric',
						histogram: {
							__typename: 'HistogramMetricBag',
							asymmetricComparison: false,
							metrics: [
								{
									__typename: 'HistogramMetric',
									key: '2024-01-17T18:00',
									previousValue: 0,
									previousValueKey: '2024-01-16T18:00',
									trend: {
										__typename: 'Trend',
										percentage: null,
										trendClassification: 'NEUTRAL'
									},
									value: 0,
									valueKey: '2024-01-17T18:00'
								},
								{
									__typename: 'HistogramMetric',
									key: '2024-01-17T19:00',
									previousValue: 0,
									previousValueKey: '2024-01-16T19:00',
									trend: {
										__typename: 'Trend',
										percentage: null,
										trendClassification: 'NEUTRAL'
									},
									value: 0,
									valueKey: '2024-01-17T19:00'
								},
								{
									__typename: 'HistogramMetric',
									key: '2024-01-17T20:00',
									previousValue: 0,
									previousValueKey: '2024-01-16T20:00',
									trend: {
										__typename: 'Trend',
										percentage: null,
										trendClassification: 'NEUTRAL'
									},
									value: 0,
									valueKey: '2024-01-17T20:00'
								},
								{
									__typename: 'HistogramMetric',
									key: '2024-01-17T21:00',
									previousValue: 0,
									previousValueKey: '2024-01-16T21:00',
									trend: {
										__typename: 'Trend',
										percentage: null,
										trendClassification: 'NEUTRAL'
									},
									value: 0,
									valueKey: '2024-01-17T21:00'
								},
								{
									__typename: 'HistogramMetric',
									key: '2024-01-17T22:00',
									previousValue: 0,
									previousValueKey: '2024-01-16T22:00',
									trend: {
										__typename: 'Trend',
										percentage: null,
										trendClassification: 'NEUTRAL'
									},
									value: 0,
									valueKey: '2024-01-17T22:00'
								}
							],
							total: 5
						},
						previousValue: null,
						trend: {
							__typename: 'Trend',
							percentage: null,
							trendClassification: 'NEUTRAL'
						},
						value: 1
					}
				}
			}
		}
	};
}

export function mockSitesTabsReq({rangeKey}) {
	return {
		request: {
			query: SitesTabsQuery,
			variables: {
				channelId: '456',
				devices: 'Any',
				interval: 'D',
				location: 'Any',
				rangeEnd: '',
				rangeKey,
				rangeStart: ''
			}
		},
		result: {
			data: {
				site: {
					__typename: 'SiteMetric',
					bounceRateMetric: {
						__typename: 'Metric',
						previousValue: null,
						trend: {
							__typename: 'Trend',
							percentage: null,
							trendClassification: 'NEUTRAL'
						},
						value: 0
					},
					sessionDurationMetric: {
						__typename: 'Metric',
						previousValue: null,
						trend: {
							__typename: 'Trend',
							percentage: null,
							trendClassification: 'NEUTRAL'
						},
						value: 25184
					},
					sessionsPerVisitorMetric: {
						__typename: 'Metric',
						previousValue: null,
						trend: {
							__typename: 'Trend',
							percentage: null,
							trendClassification: 'NEUTRAL'
						},
						value: 1
					},
					visitorsMetric: {
						__typename: 'Metric',
						previousValue: null,
						trend: {
							__typename: 'Trend',
							percentage: null,
							trendClassification: 'NEUTRAL'
						},
						value: 1
					}
				}
			}
		}
	};
}

export function mockSitesTopPagesReq() {
	return {
		request: {
			query: SitesTopPagesQuery,
			variables: {
				channelId: '123',
				rangeEnd: null,
				rangeKey: 30,
				rangeStart: null,
				size: 5,
				sort: {
					column: 'visitorsMetric',
					type: 'DESC'
				},
				start: 0
			}
		},
		result: {
			data: {
				pages: {
					__typename: 'AssetMetricBag',
					assetMetrics: [
						{
							__typename: 'PageMetric',
							assetId: '123',
							assetTitle: 'My asset A',
							entrancesMetric: {
								__typename: 'Metric',
								value: 10
							},
							exitRateMetric: {
								__typename: 'Metric',
								value: 15
							},
							visitorsMetric: {
								__typename: 'Metric',
								value: 20
							}
						},
						{
							__typename: 'assetMetric',
							assetId: '456',
							assetTitle: 'My asset B',
							entrancesMetric: {
								__typename: 'Metric',
								value: 10
							},
							exitRateMetric: {
								__typename: 'Metric',
								value: 15
							},
							visitorsMetric: {
								__typename: 'Metric',
								value: 20
							}
						}
					],
					total: 2
				}
			}
		}
	};
}

export function mockInterestsReq() {
	return {
		request: {
			query: getInterestsQuery(CompositionTypes.AccountInterests),
			variables: {
				active: true,
				channelId: '123',
				id: 'test',
				size: 5,
				sort: {
					column: 'count',
					type: 'DESC'
				},
				start: 0
			}
		},
		result: {
			data: {
				accountInterests: {
					__typename: 'CompositionBag',
					compositions: [
						{
							__typename: 'CompositionItem',
							count: 10,
							name: 'composition 01'
						}
					],
					maxCount: 0,
					total: 0,
					totalCount: 0
				}
			}
		}
	};
}

export function mockIndividualInterestsReq(getVariables, result) {
	const defaultVariables = {
		active: true,
		channelId: '456',
		id: '123',
		size: 5,
		sort: {
			column: COUNT,
			type: OrderByDirections.Descending
		},
		start: 0
	};

	return {
		request: {
			query: IndividualInterestsQuery,
			variables: getVariables
				? getVariables(defaultVariables)
				: defaultVariables
		},
		result: {
			data: mockCompositionBag({
				compositionBagName: 'individualInterests',
				compositions: [
					{
						count: 2,
						name: 'cutting-edge platforms'
					},
					{count: 2, name: 'mesh'},
					{
						count: 2,
						name: 'mesh synergistic schemas'
					},
					{
						count: 2,
						name: 'synergistic schemas'
					},
					{
						count: 1,
						name: 'rich e-commerce'
					}
				],
				maxCount: 2,
				total: 5,
				totalCount: 3
			}),
			...result
		}
	};
}

export function mockIndividualMetricsReq() {
	return {
		request: {
			query: IndividualMetricsQuery,
			variables: {
				channelId: '123123',
				interval: INTERVAL_KEY_MAP.week,
				...getSafeRangeSelectors({
					rangeKey: RangeKeyTimeRanges.Last30Days
				})
			}
		},
		result: {
			data: {
				individualMetric: {
					__typename: 'IndividualMetric',
					anonymousIndividualsMetric: mockMetric({
						histogram: {
							__typename: 'HistogramMetricBag',
							asymmetricComparison: false,
							metrics: [
								{
									__typename: 'HistogramMetric',
									key: '1',
									value: 1323321,
									valueKey: '1'
								}
							],
							total: 1
						},
						trend: {percentage: 0},
						value: 1323321
					}),
					knownIndividualsMetric: mockMetric({
						histogram: {
							__typename: 'HistogramMetricBag',
							asymmetricComparison: false,
							metrics: [
								{
									__typename: 'HistogramMetric',
									key: '2',
									value: 11987,
									valueKey: '1'
								}
							],
							total: 1
						},
						trend: {percentage: 12.5},
						value: 11987
					}),
					totalIndividualsMetric: mockMetric({
						histogram: {
							__typename: 'HistogramMetricBag',
							asymmetricComparison: false,
							metrics: [
								{
									__typename: 'HistogramMetric',
									key: '3',
									value: 1300000000,
									valueKey: '1'
								}
							],
							total: 1
						},
						trend: {percentage: -25},
						value: 1300000000
					})
				}
			}
		}
	};
}

export function mockDXPEntitiesBag(entityName, items) {
	return {
		[entityName]: {
			__typename: 'DXPEntityBag',
			dxpEntities: items.map(item => ({
				__typename: 'DXEntity',
				...item
			})),
			total: items.length
		}
	};
}

export function mockEventAttributeDefinitionReq(item, mockVariables = {}) {
	return {
		request: {
			query: EventAttributeDefinitionQuery,
			variables: {
				...mockVariables
			}
		},
		result: {
			data: {
				eventAttributeDefinition: {
					__typename: 'EventAttributeDefinition',
					...item
				}
			}
		}
	};
}

export function mockEventAttributeDefinitionWithRecentValuesReq(
	item,
	mockVariables = {}
) {
	return {
		request: {
			query: EVENT_ATTRIBUTE_DEFINITION_WITH_RECENT_VALUES_QUERY,
			variables: {
				...mockVariables
			}
		},
		result: {
			data: {
				eventAttributeDefinition: {
					__typename: 'EventAttributeDefinition',
					...item
				}
			}
		}
	};
}

export function mockEventAttributeDefinitionsReq(items, mockVariables = {}) {
	return {
		request: {
			query: EventAttributeDefinitionsQuery,
			variables: {
				keyword: '',
				page: 0,
				size: items.length,
				sort: {
					column: NAME,
					type: OrderByDirections.Ascending
				},
				type: AttributeTypes.All,
				...mockVariables
			}
		},
		result: {
			data: {
				eventAttributeDefinitions: {
					__typename: 'EventAttributeDefinitionBag',
					eventAttributeDefinitions: items,
					total: items.length
				}
			}
		}
	};
}

export function mockEventDefinitionReq(item, mockVariables = {}) {
	return {
		request: {
			query: EventDefinitionQuery,
			variables: {
				...mockVariables
			}
		},
		result: {
			data: {
				eventDefinition: {
					__typename: 'EventDefinition',
					...item
				}
			}
		}
	};
}

export function mockEventDefinitionsReq(items, mockVariables = {}) {
	return {
		request: {
			query: EventDefinitionsQuery,
			variables: {
				eventType: EventTypes.Default,
				keyword: '',
				page: 0,
				size: items.length,
				sort: {
					column: NAME,
					type: OrderByDirections.Ascending
				},
				...mockVariables
			}
		},
		result: {
			data: {
				eventDefinitions: {
					__typename: 'EventDefinitionBag',
					eventDefinitions: items,
					total: items.length
				}
			}
		}
	};
}

export function mockEventAnalysisReq() {
	return {
		request: {
			query: EventAnalysisQuery,
			variables: {
				eventAnalysisId: '1'
			}
		},
		result: {
			data: {
				eventAnalysis: {
					__typename: 'EventAnalysis',
					analysisType: 'TOTAL',
					channelId: '123',
					compareToPrevious: true,
					eventAnalysisBreakdowns: [
						{
							__typename: 'EventAnalysisBreakdown',
							attributeId: '9',
							attributeType: 'EVENT',
							binSize: 1,
							dataType: DataTypes.String,
							dateGrouping: DateGroupings.Day,
							description: null,
							displayName: 'assetId',
							sortType: OrderByDirections.Descending
						},
						{
							__typename: 'EventAnalysisBreakdown',
							attributeId: '11',
							attributeType: 'EVENT',
							binSize: 1,
							dataType: DataTypes.String,
							dateGrouping: DateGroupings.Day,
							description: null,
							displayName: 'category',
							sortType: OrderByDirections.Descending
						}
					],
					eventAnalysisFilters: [
						{
							__typename: 'EventAnalysisFilter',
							attributeId: '26',
							attributeType: 'EVENT',
							dataType: DataTypes.String,
							description: null,
							displayName: 'pageTitle',
							operator: 'contains',
							values: ['page title']
						}
					],
					eventDefinitionId: '1',
					name: 'My First Event Analysis',
					rangeEnd: null,
					rangeKey: 90,
					rangeStart: null,
					referencedObjects: {
						__typename: 'EventAnalysisReferencedObject',
						eventAttributeDefinitions: [
							{
								__typename: 'EventAttributeDefinition',
								dataType: DataTypes.String,
								description: null,
								displayName: 'assetId',
								id: '9',
								name: 'assetId',
								sampleValue: null,
								type: AttributeTypes.Local
							},
							{
								__typename: 'EventAttributeDefinition',
								dataType: DataTypes.String,
								description: null,
								displayName: 'category',
								id: '11',
								name: 'category',
								sampleValue: null,
								type: AttributeTypes.Local
							},
							{
								__typename: 'EventAttributeDefinition',
								dataType: DataTypes.String,
								description: null,
								displayName: 'pageTitle',
								id: '26',
								name: 'pageTitle',
								sampleValue: null,
								type: AttributeTypes.Global
							}
						],
						eventDefinition: {
							__typename: 'EventDefinition',
							description: null,
							displayName: 'assetClicked',
							hidden: false,
							id: '1',
							name: 'assetClicked',
							type: EventTypes.Default
						}
					}
				}
			}
		}
	};
}

export function mockEventAnalysisResultReq(
	eventAnalysisResult,
	mockVariables = {}
) {
	return {
		request: {
			fetchPolicy: 'network-only',
			query: EventAnalysisResultQuery,
			variables: {
				analysisType: 'TOTAL',
				channelId: '123',
				compareToPrevious: true,
				eventDefinitionId: '1',
				page: 0,
				rangeEnd: null,
				rangeKey: 30,
				rangeStart: null,
				size: 2,
				...mockVariables
			}
		},
		result: {
			data: {
				eventAnalysisResult
			}
		}
	};
}

export function mockEventAnalysisListReq(items) {
	return {
		request: {
			fetchPolicy: 'network-only',
			query: EventAnalysisListQuery,
			variables: {
				channelId: '456',
				keywords: '',
				page: 0,
				size: 2,
				sort: {
					column: 'name',
					type: OrderByDirections.Ascending
				}
			}
		},
		result: {
			data: {
				eventAnalyses: {
					__typename: 'EventAnalysisBag',
					eventAnalyses: items,
					total: items.length
				}
			}
		}
	};
}

export function mockEventPropertiesReq(items, mockVariables = {}) {
	return {
		request: {
			query: EventPropertiesQuery,
			variables: {
				keyword: '',
				page: 0,
				size: items.length,
				sort: {
					column: NAME,
					type: OrderByDirections.Ascending
				},
				...mockVariables
			}
		},
		result: {
			data: {
				eventProperties: {
					__typename: 'EventPropertyBag',
					eventProperties: items,
					total: items.length
				}
			}
		}
	};
}

export function mockOrganizationsListReq(items) {
	return {
		request: {
			query: OrganizationsQuery,
			variables: {
				keywords: '',
				size: 5,
				sort: {column: 'name', type: 'ASC'},
				start: 0
			}
		},
		result: {
			data: {
				...mockDXPEntitiesBag(
					'organizations',
					items ||
						range(5).map(i => ({
							dataSourceName: `fooDataSource-${i}`,
							id: i,
							name: `fooOrganization-${i}`,
							parentName: 'fooParentOrganization',
							type: 'fooOrganizationType'
						}))
				)
			}
		}
	};
}

export function mockMetric(metrics = {}) {
	return {
		...mapValues(metrics, (value, key) => {
			const typeName = METRIC_TYPENAME_MAP[key];
			return typeName
				? isArray(value)
					? value.map(item => ({...item, __typename: typeName}))
					: {...value, __typename: typeName}
				: value;
		}),
		__typename: 'Metric'
	};
}

export function mockRecommendationReq(item = {}, mockVariables = {}) {
	return {
		request: {
			query: RecommendationQuery,
			variables: {
				jobId: '321',
				...mockVariables
			}
		},
		result: {
			data: {
				jobById: {
					...item,
					__typename: 'Job'
				}
			}
		}
	};
}

export function mockPagePathReq(data = [], {rangeKey = 30}) {
	return {
		request: {
			query: PagePathQuery,
			variables: {
				canonicalUrl: 'https://liferay.com/home',
				channelId: '123',
				rangeEnd: null,
				rangeKey,
				rangeStart: null,
				title: 'Liferay DXP - Home'
			}
		},
		result: {
			data
		}
	};
}

export function mockRecommendationActivitiesReq(items, mockVariables = {}) {
	return {
		request: {
			query: RecommendationActivitiesQuery,
			variables: {
				applicationId: 'Page',
				eventContextPropertyFilters: [
					{filter: '.*custom-assets', negate: false}
				],
				eventId: 'pageUnloaded',
				rangeKey: '30',
				size: 0,
				start: 0,
				...mockVariables
			}
		},
		result: {
			data: {
				activities: {
					__typename: 'ActivityBag',
					activities: items,
					total: items.length
				}
			}
		}
	};
}

export function mockRecommendationJobRunsReq(items, mockVariables = {}) {
	return {
		request: {
			query: RecommendationJobRunsQuery,
			variables: {
				jobId: '321',
				size: 5,
				sort: {column: 'id', type: 'DESC'},
				start: 0,
				...mockVariables
			}
		},
		result: {
			data: {
				jobRuns: {
					__typename: 'JobRunBag',
					jobRuns: items,
					total: items.length
				}
			}
		}
	};
}

export function mockRecommendationPageAssetsReq(items, mockVariables = {}) {
	return {
		request: {
			query: RecommendationPageAssetsQuery,
			variables: {
				propertyFilters: [{filter: '.*custom-assets', negate: false}],
				size: 5,
				sort: {column: 'title', type: 'DESC'},
				start: 0,
				...mockVariables
			}
		},
		result: {
			data: {
				pageAssets: {
					__typename: 'PageAssetBag',
					pageAssets: items,
					total: items.length
				}
			}
		}
	};
}

export function mockSearchStringListReq() {
	return {
		request: {
			query: PreferenceQuery,
			variables: {
				key: 'search-query-strings'
			}
		},
		result: {
			data: {
				preference: {
					__typename: 'Preference',
					key: 'search-query-strings',
					value: JSON.stringify(['jackson'])
				}
			}
		}
	};
}

export function mockSegmentPageViewsReq({segmentPageViews}) {
	return {
		request: {
			fetchPolicy: 'network-only',
			query: SegmentPageViewsQuery,
			variables: {
				canonicalUrl: 'http://liferay.com',
				channelId: '456',
				rangeEnd: null,
				rangeKey: 0,
				rangeStart: null,
				segmentIds: segmentPageViews.map(segment => segment.segmentId),
				title: 'Liferay DXP - Home'
			}
		},
		result: {
			data: {
				segmentPageViews: segmentPageViews.map(segment => ({
					...segment,
					__typename: 'SegmentPageView'
				}))
			}
		}
	};
}

export function mockSuppressedUsersListReq(items, mockVariables = {}) {
	return {
		request: {
			query: SuppressedUsersListQuery,
			variables: {
				keywords: '',
				size: 5,
				sort: {column: 'createDate', type: 'DESC'},
				start: 0,
				...mockVariables
			}
		},
		result: {
			data: {
				suppressions: {
					__typename: 'SuppressionBag',
					suppressions: items,
					total: items.length
				}
			}
		}
	};
}

export function mockEventAttributeValues() {
	return {
		request: {
			query: EventAttributeValuesQuery,
			variables: {
				channelId: '123',
				eventAttributeDefinitionId: '456',
				eventDefinitionId: '789',
				keywords: '',
				size: 100,
				start: 0
			}
		},
		result: {
			data: {
				eventAttributeValues: {
					eventAttributeValues: ['test1', 'test2'],
					total: 2
				}
			}
		}
	};
}

export function mockPreferenceReq(value = THIRTEEN_MONTHS) {
	return {
		request: {
			query: PreferenceQuery,
			variables: {
				key: DATA_RETENTION_PERIOD_KEY
			}
		},
		result: {
			data: {
				preference: {
					__typename: 'Preference',
					key: DATA_RETENTION_PERIOD_KEY,
					value
				}
			}
		}
	};
}

export function mockTimeRangeReq() {
	return {
		request: {
			query: TimeRangeQuery
		},
		result: {
			data: {
				timeRange: [
					{
						__typename: 'TimeRange',
						default: false,
						endDate: '2020-05-08T23:00',
						rangeKey: 0,
						startDate: '2020-05-08T00:00'
					},
					{
						__typename: 'TimeRange',
						default: false,
						endDate: '2020-05-07T23:00',
						rangeKey: 1,
						startDate: '2020-05-07T00:00'
					},
					{
						__typename: 'TimeRange',
						default: false,
						endDate: '2020-05-07T23:59:59.999999999',
						rangeKey: 7,
						startDate: '2020-05-01T00:00'
					},
					{
						__typename: 'TimeRange',
						default: false,
						endDate: '2020-05-07T23:59:59.999999999',
						rangeKey: 90,
						startDate: '2020-02-08T00:00'
					},
					{
						__typename: 'TimeRange',
						default: false,
						endDate: '2020-05-07T23:59:59.999999999',
						rangeKey: 28,
						startDate: '2020-04-10T00:00'
					},
					{
						__typename: 'TimeRange',
						default: true,
						endDate: '2020-05-07T23:59:59.999999999',
						rangeKey: 30,
						startDate: '2020-04-08T00:00'
					}
				]
			}
		}
	};
}

export function mockTouchpointsReq(items, mockVariables = {}) {
	return {
		request: {
			query: TouchpointsQuery,
			variables: {
				channelId: '321321',
				devices: 'Any',
				keywords: '',
				location: 'Any',
				size: 5,
				sort: {column: 'visitorsMetric', type: 'DESC'},
				start: 0,
				terms: 'test',
				title: '',
				touchpoint: null,
				...getSafeRangeSelectors({
					rangeKey: RangeKeyTimeRanges.Last30Days
				}),
				...mockVariables
			}
		},
		result: {
			data: {
				pages: {
					__typename: 'AssetMetricBag',
					assetMetrics: items,
					total: items.length
				}
			}
		}
	};
}

export function mockJobBag(items) {
	return mockBag({
		items,
		itemTypeName: 'Job',
		name: 'jobs',
		typeName: 'JobBag'
	});
}

export const mockEventMetrics = variables => ({
	request: {
		query: EventMetricQuery,
		variables: {
			channelId: '123123',
			entityId: '0',
			entityType: 'INDIVIDUAL',
			interval: 'D',
			keywords: '',
			rangeEnd: null,
			rangeKey: 30,
			rangeStart: null,
			...variables
		}
	},
	result: {
		data: {
			eventMetric: {
				__typename: 'EventMetric',
				totalEventsMetric: {
					__typename: 'Metric',
					histogram: {
						__typename: 'HistogramMetricBag',
						metrics: [
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T14:00',
								value: 0.0,
								valueKey: '2021-12-09T14:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T15:00',
								value: 0.0,
								valueKey: '2021-12-09T15:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T16:00',
								value: 0.0,
								valueKey: '2021-12-09T16:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T17:00',
								value: 0.0,
								valueKey: '2021-12-09T17:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T18:00',
								value: 0.0,
								valueKey: '2021-12-09T18:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T19:00',
								value: 0.0,
								valueKey: '2021-12-09T19:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T20:00',
								value: 0.0,
								valueKey: '2021-12-09T20:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T21:00',
								value: 0.0,
								valueKey: '2021-12-09T21:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T22:00',
								value: 0.0,
								valueKey: '2021-12-09T22:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T23:00',
								value: 0.0,
								valueKey: '2021-12-09T23:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T00:00',
								value: 0.0,
								valueKey: '2021-12-10T00:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T01:00',
								value: 0.0,
								valueKey: '2021-12-10T01:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T02:00',
								value: 0.0,
								valueKey: '2021-12-10T02:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T03:00',
								value: 0.0,
								valueKey: '2021-12-10T03:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T04:00',
								value: 0.0,
								valueKey: '2021-12-10T04:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T05:00',
								value: 0.0,
								valueKey: '2021-12-10T05:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T06:00',
								value: 0.0,
								valueKey: '2021-12-10T06:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T07:00',
								value: 0.0,
								valueKey: '2021-12-10T07:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T08:00',
								value: 0.0,
								valueKey: '2021-12-10T08:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T09:00',
								value: 0.0,
								valueKey: '2021-12-10T09:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T10:00',
								value: 0.0,
								valueKey: '2021-12-10T10:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T11:00',
								value: 0.0,
								valueKey: '2021-12-10T11:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T12:00',
								value: 0.0,
								valueKey: '2021-12-10T12:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T13:00',
								value: 0.0,
								valueKey: '2021-12-10T13:00'
							}
						],
						total: 24
					},
					value: 0.0
				},
				totalSessionsMetric: {
					__typename: 'Metric',
					histogram: {
						__typename: 'HistogramMetricBag',
						metrics: [
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T14:00',
								value: 0.0,
								valueKey: '2021-12-09T14:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T15:00',
								value: 0.0,
								valueKey: '2021-12-09T15:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T16:00',
								value: 0.0,
								valueKey: '2021-12-09T16:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T17:00',
								value: 0.0,
								valueKey: '2021-12-09T17:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T18:00',
								value: 0.0,
								valueKey: '2021-12-09T18:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T19:00',
								value: 0.0,
								valueKey: '2021-12-09T19:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T20:00',
								value: 0.0,
								valueKey: '2021-12-09T20:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T21:00',
								value: 0.0,
								valueKey: '2021-12-09T21:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T22:00',
								value: 0.0,
								valueKey: '2021-12-09T22:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-09T23:00',
								value: 0.0,
								valueKey: '2021-12-09T23:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T00:00',
								value: 0.0,
								valueKey: '2021-12-10T00:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T01:00',
								value: 0.0,
								valueKey: '2021-12-10T01:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T02:00',
								value: 0.0,
								valueKey: '2021-12-10T02:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T03:00',
								value: 0.0,
								valueKey: '2021-12-10T03:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T04:00',
								value: 0.0,
								valueKey: '2021-12-10T04:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T05:00',
								value: 0.0,
								valueKey: '2021-12-10T05:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T06:00',
								value: 0.0,
								valueKey: '2021-12-10T06:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T07:00',
								value: 0.0,
								valueKey: '2021-12-10T07:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T08:00',
								value: 0.0,
								valueKey: '2021-12-10T08:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T09:00',
								value: 0.0,
								valueKey: '2021-12-10T09:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T10:00',
								value: 0.0,
								valueKey: '2021-12-10T10:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T11:00',
								value: 0.0,
								valueKey: '2021-12-10T11:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T12:00',
								value: 0.0,
								valueKey: '2021-12-10T12:00'
							},
							{
								__typename: 'HistogramMetric',
								key: '2021-12-10T13:00',
								value: 0.0,
								valueKey: '2021-12-10T13:00'
							}
						],
						total: 24
					},
					value: 0.0
				}
			}
		}
	}
});

export function mockCommerceTotalOrderValueReq({data, Query, variables}) {
	return {
		request: {
			query: Query,
			variables
		},
		result: {
			data
		}
	};
}

export const mockSessions = variables => ({
	request: {
		query: UserSessionQuery,
		variables: {
			channelId: '123123',
			entityId: '0',
			entityType: 'INDIVIDUAL',
			keywords: '',
			page: 0,
			rangeEnd: null,
			rangeKey: 30,
			rangeStart: null,
			size: 50,
			...variables
		}
	},
	result: {
		data: {
			eventsByUserSessions: {
				__typename: 'EventsByUserSession',
				totalEvents: 14314,
				userSessions: [
					{
						__typename: 'UserSession',
						browserName: 'Chrome',
						completeDate: 'Mon Dec 06 18:02:32 GMT 2021',
						contentLanguageId: 'en-US',
						createDate: 'Mon Dec 06 17:01:27 GMT 2021',
						devicePixelRatio: '0.8999999761581421',
						deviceType: 'Desktop',
						events: [
							{
								__typename: 'Event',
								applicationId: 'Page',
								assetTitle: 'Home - Liferay DXP',
								canonicalUrl: 'http://localhost:8080',
								createDate: 'Mon Dec 06 17:28:48 GMT 2021',
								name: 'tabBlurred',
								pageDescription: '',
								pageKeywords: '',
								pageTitle: 'Home - Liferay DXP',
								referrer: '',
								url: 'http://localhost:8080/'
							}
						],
						languageId: 'en-US',
						screenHeight: '1080',
						screenWidth: '2132',
						timezoneOffset: '-03:00',
						userAgent:
							'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36'
					}
				]
			}
		}
	}
});
