import client from 'shared/apollo/client';
import SearchTermsQuery from 'shared/queries/SearchTermsQuery';
import {createSearchTermProperty} from '../utils/utils';
import {Property} from 'shared/util/records';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {
	RemoteCriterionSearchParams,
	RemoteCriterionSearchResult,
} from '../criterion-types/RemoteCriterionType';

/**
 * Gives the Search Terms sidebar section the same paginated,
 * search-as-you-type UX tags and vocabularies get, without joining the
 * `RemoteCriterionType` registry (`../criterion-types/registry.ts`): that
 * registry also drives `odata.ts`'s remote-filter serialization
 * (`buildRemoteFilterString`) and `extract.ts`'s reload backfill, both built
 * around an entity id/name pair and an is/is-not *operator* pair. Search
 * Terms has neither — it's a single operator with an inner boolean, same as
 * Interest (see `findPropertyByCriterion`'s `SearchTermsFilter` branch,
 * which resolves a reloaded criterion without any of that machinery).
 *
 * `keywords` is already sent to `SearchTermsQuery` — the backend doesn't
 * recognize that argument yet (LPD-104562 has asked for it), so this fails
 * server-side until it ships. This PR isn't merging until it does, so
 * there's no window where a broken query reaches anyone; the `.catch`
 * exists to fail quietly (empty page) rather than to bridge a gap in
 * production. Once the backend ships it, this needs no change at all.
 */
export const searchTermPagination = {
	api: async ({
		channelId,
		keywords,
		page = 1,
		pageSize = 0,
	}: RemoteCriterionSearchParams): Promise<RemoteCriterionSearchResult> => {
		const {data} = await client
			.query({
				query: SearchTermsQuery,
				variables: {
					channelId,
					keywords: keywords || undefined,
					rangeKey: parseInt(RangeKeyTimeRanges.AllTime),
					size: pageSize,
					start: (page - 1) * pageSize,
				},
			})
			.catch(() => ({data: undefined}));

		const compositions: Array<{name: string}> =
			data?.searchTerms?.compositions ?? [];

		return {
			items: compositions.map(({name}) => ({id: name, name})),
			totalCount: data?.searchTerms?.totalCount ?? 0,
		};
	},

	createProperty: ({name}: {id: string; name: string}): Property =>
		createSearchTermProperty(name),
};
