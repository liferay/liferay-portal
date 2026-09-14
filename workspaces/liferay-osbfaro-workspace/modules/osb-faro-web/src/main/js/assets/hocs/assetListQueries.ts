import knownAccountsListAssetQuery from 'shared/queries/knownAccountsListAssetQuery';
import knownIndividualsListAssetQuery from 'shared/queries/knownIndividualsListAssetQuery';
import ObjectEntryKnownAccountsListQuery from 'shared/queries/ObjectEntryKnownAccountsListQuery';
import ObjectEntryKnownIndividualsListQuery from 'shared/queries/ObjectEntryKnownIndividualsListQuery';
import {DocumentNode} from '@apollo/client';
import {
	DOWNLOADS_METRIC,
	SUBMISSIONS_METRIC,
	VIEWS_METRIC,
} from 'shared/util/pagination';

export interface AssetListQueries {
	accountsMetric: string;
	accountsQuery: DocumentNode;
	individualsMetric: string;
	individualsQuery: DocumentNode;
}

/**
 * The account and individual lists each read one metric off the asset, and the
 * two are not always the same one: forms rank accounts by views but
 * individuals by submissions. Object entries are queried through their own
 * documents, which leave out the `channelId` and `title` arguments the shared
 * factory sends.
 */
const ASSET_LIST_QUERIES: Record<string, AssetListQueries> = {
	blog: {
		accountsMetric: VIEWS_METRIC,
		accountsQuery: knownAccountsListAssetQuery('blog', VIEWS_METRIC),
		individualsMetric: VIEWS_METRIC,
		individualsQuery: knownIndividualsListAssetQuery('blog', VIEWS_METRIC),
	},
	document: {
		accountsMetric: DOWNLOADS_METRIC,
		accountsQuery: knownAccountsListAssetQuery(
			'document',
			DOWNLOADS_METRIC
		),
		individualsMetric: DOWNLOADS_METRIC,
		individualsQuery: knownIndividualsListAssetQuery(
			'document',
			DOWNLOADS_METRIC
		),
	},
	form: {
		accountsMetric: VIEWS_METRIC,
		accountsQuery: knownAccountsListAssetQuery('form', VIEWS_METRIC),
		individualsMetric: SUBMISSIONS_METRIC,
		individualsQuery: knownIndividualsListAssetQuery(
			'form',
			SUBMISSIONS_METRIC
		),
	},
	journal: {
		accountsMetric: VIEWS_METRIC,
		accountsQuery: knownAccountsListAssetQuery('journal', VIEWS_METRIC),
		individualsMetric: VIEWS_METRIC,
		individualsQuery: knownIndividualsListAssetQuery(
			'journal',
			VIEWS_METRIC
		),
	},
	objectEntry: {
		accountsMetric: VIEWS_METRIC,
		accountsQuery: ObjectEntryKnownAccountsListQuery,
		individualsMetric: VIEWS_METRIC,
		individualsQuery: ObjectEntryKnownIndividualsListQuery,
	},
};

export const getAssetListQueries = (graphQLType: string): AssetListQueries =>
	ASSET_LIST_QUERIES[graphQLType] ?? ASSET_LIST_QUERIES.objectEntry;
