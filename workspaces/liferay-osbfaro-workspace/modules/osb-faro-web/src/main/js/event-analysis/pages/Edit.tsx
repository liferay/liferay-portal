import BaseEventAnalysisPage from '../components/BaseEventAnalysisPage';
import ErrorPage from 'shared/pages/ErrorPage';
import Loading from 'shared/components/Loading';
import React, {useMemo} from 'react';
import {Attribute, Breakdown, Filter} from 'event-analysis/utils/types';
import {AttributesProvider} from '../components/event-analysis-editor/context/attributes';
import {AttributesState} from '../components/event-analysis-editor/context/attributes';
import {deletePropertyFromObject} from 'shared/util/object';
import {
	EventAnalysisData,
	EventAnalysisQuery,
	EventAnalysisVariables,
} from '../queries/EventAnalysisQuery';
import {normalizeRangeSelectors} from 'shared/util/util';
import {Routes, toRoute} from 'shared/util/router';
import {uniqueId} from 'lodash';
import {useParams} from 'react-router-dom';
import {useQuery} from '@apollo/client';

function normalizeItems<T extends {id: string; __typename?: string}>(
	data: T[]
): {[key: string]: T} {
	return data.reduce(
		(acc, item) => ({
			...acc,
			[item.id]: deletePropertyFromObject('__typename', item),
		}),
		{}
	);
}

function getItemsWithUniqueId<T>(
	items: T[],
	key: string
): Array<T & {id: string}> {
	return items.map((item) => ({
		...item,
		id: uniqueId(key),
	}));
}

interface BreakdownWithId extends Breakdown {
	id: string;
}

interface FilterWithId extends Filter {
	id: string;
}

const Edit: React.FC<React.HTMLAttributes<HTMLElement>> = () => {
	const {
		channelId,
		groupId,
		id: eventAnalysisId = '',
	} = useParams<{
		channelId: string;
		groupId: string;
		id: string;
	}>();
	const {data, error, loading} = useQuery<
		EventAnalysisData,
		EventAnalysisVariables
	>(EventAnalysisQuery, {
		fetchPolicy: 'network-only',
		variables: {
			eventAnalysisId,
		},
	});

	const initialAttributesState = useMemo(() => {
		if (data) {
			const {
				eventAnalysis: {
					eventAnalysisBreakdowns,
					eventAnalysisFilters,
					referencedObjects: {eventAttributeDefinitions},
				},
			} = data;

			const breakdowns: BreakdownWithId[] =
				getItemsWithUniqueId<Breakdown>(
					eventAnalysisBreakdowns,
					'breakdown'
				);
			const filters: FilterWithId[] = getItemsWithUniqueId<Filter>(
				eventAnalysisFilters,
				'filter'
			);

			const attributesState: AttributesState = {
				attributes: normalizeItems<Attribute>(
					eventAttributeDefinitions
				),
				breakdownOrder: breakdowns.map(({id}) => id),
				breakdowns: normalizeItems<BreakdownWithId>(breakdowns),
				filterOrder: filters.map(({id}) => id),
				filters: normalizeItems<FilterWithId>(filters),
			};

			return attributesState;
		}
	}, [
		data?.eventAnalysis?.eventAnalysisBreakdowns,
		data?.eventAnalysis?.eventAnalysisFilters,
	]);

	if (loading) {
		return <Loading key="LOADING" />;
	}

	if (error) {
		return (
			<ErrorPage
				href={toRoute(Routes.EVENT_ANALYSIS, {
					channelId,
					groupId,
				})}
				linkLabel={Liferay.Language.get('go-to-event-analysis')}
				message={Liferay.Language.get(
					'the-analysis-you-are-looking-for-does-not-exist'
				)}
				subtitle={Liferay.Language.get('analysis-was-not-found')}
			/>
		);
	}

	const {
		eventAnalysis: {
			compareToPrevious,
			name,
			rangeEnd,
			rangeKey,
			rangeStart,
			referencedObjects: {eventDefinition},
		},
	} = data!;

	return (
		<AttributesProvider initialState={initialAttributesState}>
			<BaseEventAnalysisPage
				compareToPrevious={compareToPrevious}
				event={eventDefinition}
				name={name}
				rangeSelectors={normalizeRangeSelectors({
					rangeEnd,
					rangeKey,
					rangeStart,
				})}
			/>
		</AttributesProvider>
	);
};

export default Edit;
