import ClayTabs from '@clayui/tabs';
import Loading from 'shared/components/Loading';
import React, {useMemo, useState} from 'react';
import SidebarPagination from './SidebarPagination';
import URLConstants from 'shared/util/url-constants';
import {eventDefinitionsPagination} from './eventDefinitionsPagination';
import {filterPropertiesByLabel, renderProperties} from './criteriaProperties';
import {List} from 'immutable';
import {Property} from 'shared/util/records';
import {usePaginatedProperties} from './usePaginatedProperties';

const CUSTOM_EVENTS_PAGE_SIZE = 10;

const DEFAULT_TAB = 0;

interface IEventsCriteriaTabsProps {
	channelId: string;
	defaultEvents: List<Property>;
	groupId: string;
	searchValue: string;
}

const EventsCriteriaTabs: React.FC<IEventsCriteriaTabsProps> = ({
	channelId,
	defaultEvents,
	groupId,
	searchValue,
}) => {
	const [activeTab, setActiveTab] = useState<number>(DEFAULT_TAB);

	const isCustomTab = activeTab !== DEFAULT_TAB;

	const {
		items: customEvents,
		keywords,
		loading,
		page,
		setPage,
		totalPages,
	} = usePaginatedProperties({
		channelId,
		enabled: isCustomTab,
		groupId,
		keywords: searchValue,
		pageSize: CUSTOM_EVENTS_PAGE_SIZE,
		source: eventDefinitionsPagination,
	});

	const filteredDefaultEvents = useMemo(
		() => filterPropertiesByLabel(defaultEvents, searchValue),
		[defaultEvents, searchValue]
	);

	const renderCustomContent = () => {
		if (loading) {
			return <Loading />;
		}

		return renderProperties(customEvents, keywords, {
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
				{isCustomTab
					? renderCustomContent()
					: renderProperties(filteredDefaultEvents, searchValue)}
			</div>

			{isCustomTab && (
				<SidebarPagination
					activePage={page}
					onPageChange={setPage}
					totalPages={totalPages}
				/>
			)}
		</div>
	);
};

export default EventsCriteriaTabs;
