import ClayTabs from '@clayui/tabs';
import EventDefinitionsQuery, {
	EventDefinitionsData,
	EventDefinitionsVariables,
} from 'event-analysis/queries/EventDefinitionsQuery';
import Loading from 'shared/components/Loading';
import React, {useEffect, useMemo, useState} from 'react';
import URLConstants from 'shared/util/url-constants';
import {ClayPaginationWithBasicItems} from '@clayui/pagination';
import {convertEventToProperty} from '../utils/utils';
import {EventTypes} from 'event-analysis/utils/types';
import {filterPropertiesByLabel, renderProperties} from './criteriaProperties';
import {List} from 'immutable';
import {NAME} from 'shared/util/pagination';
import {OrderByDirections} from 'shared/util/constants';
import {PaginationBar} from '@clayui/pagination-bar';
import {Property} from 'shared/util/records';
import {useDebounce} from 'shared/hooks/useDebounce';
import {useQuery} from '@apollo/client';

const CUSTOM_EVENTS_PAGE_SIZE = 10;

const DEFAULT_TAB = 0;

const SEARCH_DEBOUNCE_DELAY = 300;

interface IEventsCriteriaTabsProps {
	defaultEvents: List<Property>;
	searchValue: string;
}

const EventsCriteriaTabs: React.FC<IEventsCriteriaTabsProps> = ({
	defaultEvents,
	searchValue,
}) => {
	const [activeTab, setActiveTab] = useState<number>(DEFAULT_TAB);
	const [page, setPage] = useState(1);

	const debouncedSearchValue = useDebounce(
		searchValue,
		SEARCH_DEBOUNCE_DELAY
	);

	useEffect(() => {
		setPage(1);
	}, [debouncedSearchValue]);

	const {data, loading} = useQuery<
		EventDefinitionsData,
		EventDefinitionsVariables
	>(EventDefinitionsQuery, {
		fetchPolicy: 'network-only',
		skip: activeTab === DEFAULT_TAB,
		variables: {
			eventType: EventTypes.Custom,
			hidden: false,
			keyword: debouncedSearchValue,
			page: page - 1,
			size: CUSTOM_EVENTS_PAGE_SIZE,
			sort: {
				column: NAME,
				type: OrderByDirections.Ascending,
			},
		},
	});

	const customEvents = useMemo(
		() =>
			List(
				(data?.eventDefinitions?.eventDefinitions ?? []).map(
					convertEventToProperty
				)
			) as unknown as List<Property>,
		[data]
	);

	const totalCount = data?.eventDefinitions?.total ?? 0;
	const totalPages = Math.ceil(totalCount / CUSTOM_EVENTS_PAGE_SIZE);

	const filteredDefaultEvents = useMemo(
		() => filterPropertiesByLabel(defaultEvents, searchValue),
		[defaultEvents, searchValue]
	);

	const renderCustomContent = () => {
		if (loading) {
			return <Loading />;
		}

		return renderProperties(customEvents, debouncedSearchValue, {
			description: Liferay.Language.get(
				'create-a-custom-event-to-get-started'
			),
			link: {
				href: URLConstants.CustomEventsDocumentation,
				label: Liferay.Language.get('learn-more-about-events'),
			},
			title: Liferay.Language.get('no-custom-events-yet'),
		});
	};

	return (
		<div className="events-criteria-tabs">
			<ClayTabs active={activeTab} onActiveChange={setActiveTab}>
				<ClayTabs.Item>{Liferay.Language.get('default')}</ClayTabs.Item>

				<ClayTabs.Item>{Liferay.Language.get('custom')}</ClayTabs.Item>
			</ClayTabs>

			<div className="events-criteria-tabs-content mt-3">
				{activeTab === DEFAULT_TAB
					? renderProperties(filteredDefaultEvents, searchValue)
					: renderCustomContent()}
			</div>

			{activeTab !== DEFAULT_TAB && totalPages > 1 && (
				<PaginationBar className="justify-content-center sidebar-pagination">
					<ClayPaginationWithBasicItems
						active={page}
						onActiveChange={setPage}
						totalPages={totalPages}
					/>
				</PaginationBar>
			)}
		</div>
	);
};

export default EventsCriteriaTabs;
