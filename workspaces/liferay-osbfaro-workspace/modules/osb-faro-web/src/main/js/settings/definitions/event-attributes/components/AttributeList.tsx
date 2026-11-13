import EventAttributeDefinitionsQuery, {
	EventAttributeDefinitionsData,
	EventAttributeDefinitionsVariables,
} from 'event-analysis/queries/EventAttributeDefinitionsQuery';
import ListComponent from 'shared/hoc/ListComponent';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import {Attribute, AttributeTypes} from 'event-analysis/utils/types';
import {attributeListColumns} from 'shared/util/table-columns';
import {
	createOrderIOMap,
	getSortFromOrderIOMap,
	NAME,
} from 'shared/util/pagination';
import {mapListResultsToProps} from 'shared/util/mappers';
import {Sort} from 'shared/types';
import {useParams} from 'react-router-dom';
import {useQuery} from '@apollo/client';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';

interface EventAttributeDefinitionsResult {
	eventAttributeDefinitions: {
		eventAttributeDefinitions: Attribute[];
		total: number;
	};
}

const AttributeList: React.FC = () => {
	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME),
	});

	const {channelId = '', groupId = ''} = useParams<{
		channelId: string;
		groupId: string;
	}>();

	const response = useQuery<
		EventAttributeDefinitionsData,
		EventAttributeDefinitionsVariables
	>(EventAttributeDefinitionsQuery, {
		variables: {
			keyword: query,
			page: page - 1,
			size: delta,
			sort: getSortFromOrderIOMap(orderIOMap) as Sort,
			type: AttributeTypes.Local,
		},
	});

	return (
		<ListComponent
			{...mapListResultsToProps(response, (result) => {
				const typedResult =
					result as unknown as EventAttributeDefinitionsResult;

				return {
					items: typedResult.eventAttributeDefinitions
						.eventAttributeDefinitions,
					total: typedResult.eventAttributeDefinitions.total,
				};
			})}
			columns={[
				attributeListColumns.getName({channelId, groupId}),
				attributeListColumns.displayName,
				attributeListColumns.description,
				attributeListColumns.sampleValue,
				attributeListColumns.dataType,
			]}
			delta={delta}
			entityLabel={Liferay.Language.get('attributes').toLowerCase()}
			noResultsRenderer={
				<NoResultsDisplay
					title={Liferay.Language.get(
						'no-visitor-data-has-been-received-from-your-pages'
					)}
				/>
			}
			orderIOMap={orderIOMap}
			page={page}
			query={query}
			rowIdentifier="id"
			showFilterAndOrder={false}
		/>
	);
};

export default AttributeList;
