import client from 'shared/apollo/client';
import EventDefinitionsQuery, {
	EventDefinitionsData,
} from 'event-analysis/queries/EventDefinitionsQuery';
import {convertEventToProperty} from '../utils/utils';
import {Event} from 'event-analysis/utils/types';
import {EventTypes} from 'event-analysis/utils/types';
import {NAME} from 'shared/util/pagination';
import {OrderByDirections} from 'shared/util/constants';
import {PaginatedSource} from '../criterion-types/RemoteCriterionType';
import {Property} from 'shared/util/records';

/**
 * Backs the Custom tab of the Events sidebar section. The query is page
 * based and zero indexed, unlike the one based pages the sidebar shows, so
 * the translation lives here rather than in the caller.
 */
export const eventDefinitionsPagination: PaginatedSource<Event> = {
	api: async ({keywords, page, pageSize}) => {
		const {data} = await client.query<EventDefinitionsData>({
			fetchPolicy: 'network-only',
			query: EventDefinitionsQuery,
			variables: {
				eventType: EventTypes.Custom,
				hidden: false,
				keyword: keywords,
				page: page - 1,
				size: pageSize,
				sort: {
					column: NAME,
					type: OrderByDirections.Ascending,
				},
			},
		});

		return {
			items: data?.eventDefinitions?.eventDefinitions ?? [],
			totalCount: data?.eventDefinitions?.total ?? 0,
		};
	},

	createProperty: (event: Event): Property =>
		convertEventToProperty(event) as unknown as Property,
};
