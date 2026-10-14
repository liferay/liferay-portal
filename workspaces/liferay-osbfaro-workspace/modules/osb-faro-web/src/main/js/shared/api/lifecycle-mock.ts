import type {
	ILifecycle,
	ILifecycleDetail,
	ILifecycleStage,
	ILifecyclesPage,
} from './lifecycle';
import {
	LIFECYCLE_STAGE_ORDER,
	STAGE_DESCRIPTIONS,
} from 'lifecycle/utils/stageConfiguration';

const MOCK_AUTHOR_NAME = 'Anneta Smith';

const MOCK_MODIFIED_DATE = Date.UTC(2026, 9, 2, 17, 53);

const MOCK_FILTER_METADATA_BY_POSITION: {[position: number]: string} = {
	0: JSON.stringify({
		conditions: [
			'Inquiry',
			'Known Prospect',
			'Marketing Qualified Lead (MQL)',
		].map((conditionValue) => ({
			conditionValue,
			field: 'salesforce/lead_Funnel_Stage__c',
			fieldDataCategory: 'Text',
			fieldDataType: 'STRING',
			operator: 'is',
		})),
		matchLogic: 'any',
	}),
	3: JSON.stringify({
		conditions: [
			{
				conditionValue: '1',
				field: 'salesforce/closedWonOpportunityCount',
				fieldDataCategory: 'Number',
				fieldDataType: 'INTEGER',
				operator: 'eq',
			},
			{
				conditionValue: 'its',
				field: 'accountName',
				fieldDataCategory: 'Text',
				fieldDataType: 'STRING',
				operator: 'contains',
			},
		],
		matchLogic: 'all',
	}),
};

const MOCK_LIFECYCLE_NAMES = [
	'Growth and Retention Hub',
	'Enterprise Expansion',
	'SMB Onboarding',
	'Partner Accounts',
	'Strategic Renewals',
	'Healthcare Accounts',
	'Public Sector',
	'Financial Services',
	'Retail and Commerce',
	'Manufacturing',
	'EMEA Accounts',
	'APAC Accounts',
	'LATAM Accounts',
	'North America Accounts',
	'Trial Conversions',
	'Product-Led Growth',
	'Win-Back Campaign',
	'Upsell Pipeline',
	'Customer Advocacy',
	'Churn Prevention',
	'Education Accounts',
	'Mid-Market Accounts',
	'Self-Service Accounts',
	'Key Accounts',
];

export const MOCK_LIFECYCLES: ILifecycleDetail[] = MOCK_LIFECYCLE_NAMES.map(
	(name, index) => {
		const id = `mock-lifecycle-${index + 1}`;

		return {
			description: '',
			id,
			name,
			processedDate: null,
			stages: LIFECYCLE_STAGE_ORDER.map((stageType, stageIndex) => ({
				accountLifecycleStageRule: {
					filterMetadata: null,
					filterString: '',
				},
				description: STAGE_DESCRIPTIONS[stageType],
				displayOrder: stageIndex + 1,
				externalReferenceCode: `${id}-stage-${stageIndex + 1}`,
				id: `${id}-stage-${stageIndex + 1}`,
				maxDuration: null,
				stageType,
			})),
		};
	}
);

let mockDefaultLifecycleId: string | null = null;

export const resetMockDefaultLifecycle = () => {
	mockDefaultLifecycleId = null;
};

const withMockStageTriggers = (stages?: ILifecycleStage[]) => {
	if (!stages) {
		return stages;
	}

	const positions = [...stages]
		.sort((a, b) => a.displayOrder - b.displayOrder)
		.map(({id}) => id);

	return stages.map((stage) => {
		const filterMetadata =
			MOCK_FILTER_METADATA_BY_POSITION[positions.indexOf(stage.id)];

		return filterMetadata
			? {
					...stage,
					accountLifecycleStageRule: {
						filterString: '',
						...stage.accountLifecycleStageRule,
						filterMetadata,
					},
				}
			: stage;
	});
};

interface ILifecyclesParams {
	channelId?: string;
	defaultAccountLifecycle?: boolean;
	groupId: string;
}

interface ILifecyclesPageParams extends ILifecyclesParams {
	page: number;
	pageSize: number;
	search?: string;
}

interface ILifecycleParams {
	groupId: string;
	lifecycleId: string;
}

interface ILifecycleRequests {
	fetchLifecycle: (params: ILifecycleParams) => Promise<ILifecycleDetail>;
	fetchLifecycleList: (params: ILifecyclesParams) => Promise<unknown>;
	fetchLifecycles: (params: ILifecyclesParams) => Promise<ILifecycle[]>;
	fetchLifecyclesPage: (
		params: ILifecyclesPageParams
	) => Promise<ILifecyclesPage>;
	setDefaultLifecycle: (
		params: ILifecycleParams
	) => Promise<ILifecycleDetail>;
}

interface ILifecycleSource {
	lifecycles: ILifecycleDetail[];
	mock: boolean;
	paged: boolean;
}

const sortByName = <T extends ILifecycle>(lifecycles: T[]) =>
	[...lifecycles].sort((a, b) =>
		(a.name ?? '').localeCompare(b.name ?? '', undefined, {
			sensitivity: 'base',
		})
	);

const MOCK_SOURCE: ILifecycleSource = {
	lifecycles: sortByName(MOCK_LIFECYCLES),
	mock: true,
	paged: false,
};

const toLifecycleSource = (response: unknown): ILifecycleSource => {
	if (Array.isArray(response) && response.length) {
		return {lifecycles: sortByName(response), mock: false, paged: false};
	}

	const items = (response as ILifecyclesPage | undefined)?.items;

	if (Array.isArray(items) && items.length) {
		return {lifecycles: items, mock: false, paged: true};
	}

	return MOCK_SOURCE;
};

const getDefaultLifecycle = ({lifecycles}: ILifecycleSource) =>
	lifecycles.find(({id}) => id === mockDefaultLifecycleId) ?? lifecycles[0];

const withMockTriggers = (lifecycle: ILifecycleDetail): ILifecycleDetail => ({
	...lifecycle,
	stages: withMockStageTriggers(lifecycle.stages),
});

const withMockFields = (
	lifecycle: ILifecycleDetail,
	source: ILifecycleSource
): ILifecycleDetail =>
	withMockTriggers({
		authorName: MOCK_AUTHOR_NAME,
		modifiedDate: MOCK_MODIFIED_DATE,
		...lifecycle,
		defaultAccountLifecycle:
			lifecycle.id === getDefaultLifecycle(source).id,
	});

const pageLifecycles = (
	source: ILifecycleSource,
	{
		defaultAccountLifecycle,
		page,
		pageSize,
		search = '',
	}: ILifecyclesPageParams
): ILifecyclesPage => {
	const query = search.trim().toLowerCase();

	const items = source.lifecycles
		.map((lifecycle) => withMockFields(lifecycle, source))
		.filter(
			(lifecycle) =>
				(!defaultAccountLifecycle ||
					lifecycle.defaultAccountLifecycle) &&
				[lifecycle.authorName, lifecycle.name].some((value) =>
					value?.toLowerCase().includes(query)
				)
		);

	return {
		items: items.slice((page - 1) * pageSize, page * pageSize),
		lastPage: Math.max(1, Math.ceil(items.length / pageSize)),
		page,
		pageSize,
		totalCount: items.length,
	};
};

export const createMockLifecycleApi = ({
	fetchLifecycle,
	fetchLifecycleList,
	fetchLifecycles,
	fetchLifecyclesPage,
	setDefaultLifecycle,
}: ILifecycleRequests) => {
	const fetchLifecycleSource = async ({
		channelId,
		groupId,
	}: ILifecyclesParams) => {
		try {
			return toLifecycleSource(
				await fetchLifecycleList({channelId, groupId})
			);
		}
		catch {
			return MOCK_SOURCE;
		}
	};

	const fetchMockLifecycle = async ({
		groupId,
		lifecycleId,
	}: ILifecycleParams): Promise<ILifecycleDetail> => {
		const source = await fetchLifecycleSource({groupId});

		if (source.mock) {
			const mockLifecycle = MOCK_LIFECYCLES.find(
				({id}) => id === lifecycleId
			);

			if (mockLifecycle) {
				return withMockFields(mockLifecycle, source);
			}
		}

		const lifecycle = await fetchLifecycle({groupId, lifecycleId});

		return source.paged
			? withMockTriggers(lifecycle)
			: withMockFields(lifecycle, source);
	};

	return {
		fetchLifecycle: fetchMockLifecycle,
		fetchLifecycles: async (
			params: ILifecyclesParams
		): Promise<ILifecycleDetail[]> => {
			const source = await fetchLifecycleSource(params);

			if (source.paged) {
				return (await fetchLifecycles(params)).map(withMockTriggers);
			}

			return pageLifecycles(source, {...params, page: 1, pageSize: 1})
				.items;
		},
		fetchLifecyclesPage: async (
			params: ILifecyclesPageParams
		): Promise<ILifecyclesPage> => {
			const source = await fetchLifecycleSource(params);

			if (!source.paged) {
				return pageLifecycles(source, params);
			}

			const lifecyclesPage = await fetchLifecyclesPage(params);

			return {
				...lifecyclesPage,
				items: lifecyclesPage.items.map(withMockTriggers),
			};
		},
		setDefaultLifecycle: async (
			params: ILifecycleParams
		): Promise<ILifecycleDetail> => {
			const source = await fetchLifecycleSource({
				groupId: params.groupId,
			});

			if (source.paged) {
				return withMockTriggers(await setDefaultLifecycle(params));
			}

			mockDefaultLifecycleId = params.lifecycleId;

			return fetchMockLifecycle(params);
		},
	};
};
