import AttributesCriteriaTabs from './AttributesCriteriaTabs';
import ClayDropDown from '@clayui/drop-down';
import CriteriaSidebarCollapse from './CriteriaSidebarCollapse';
import CriteriaSidebarSearchBar from './CriteriaSidebarSearchBar';
import EventsCriteriaTabs from './EventsCriteriaTabs';
import Loading from 'shared/components/Loading';
import React, {useContext, useEffect, useMemo, useState} from 'react';
import SidebarPagination from './SidebarPagination';
import {extractRemoteCriterionEntries} from '../criterion-types/extract';
import {FieldOwnerTypes, SegmentTypes} from 'shared/util/constants';
import {getPaginatedSection} from './paginatedSections';
import {List} from 'immutable';
import {Option, Picker} from '@clayui/core';
import {Property, PropertyGroup, PropertySubgroup} from 'shared/util/records';
import {ReferencedObjectsContext} from '../context/referencedObjects';
import {translateQueryToCriteria} from '../utils/odata';
import {usePaginatedProperties} from './usePaginatedProperties';

const REMOTE_PAGE_SIZE = 12;

const EVENTS_PROPERTY_KEY = 'web';

const PROPERTY_KEY_TO_GROUP: Record<string, string> = {
	account: 'attributes',
	individual: 'attributes',
	interest: 'intent-signals',
	organization: 'attributes',
	'search-term': 'intent-signals',
	session: 'attributes',
	tag: 'asset-categorization',
	vocabulary: 'asset-categorization',
	web: 'behavioral',
};

const GROUP_ORDER = [
	'behavioral',
	'attributes',
	'asset-categorization',
	'intent-signals',
];

const GROUP_LABELS: Record<string, string> = {
	'asset-categorization': Liferay.Language.get('asset-categorization'),
	attributes: Liferay.Language.get('attributes'),
	behavioral: Liferay.Language.get('behavioral'),
	'intent-signals': Liferay.Language.get('intent-signals'),
};

interface IPickerGroup {
	items: Array<{label: string; value: string}>;
	label: string;
}

interface ICriteriaSidebarProps {
	channelId: string;
	criteriaString?: string;
	groupId: string;
	propertyGroupsIList: List<PropertyGroup>;
	type: string;
}

export default function CriteriaSidebar({
	channelId,
	criteriaString,
	groupId,
	propertyGroupsIList,
	type,
}: ICriteriaSidebarProps) {
	const [searchValue, setSearchValue] = useState('');
	const [selectedPropertyKey, setSelectedPropertyKey] = useState<
		string | null
	>(() => propertyGroupsIList.first()?.propertyKey ?? null);

	const {addProperty} = useContext(ReferencedObjectsContext);

	const selectedRemoteCriterionType =
		getPaginatedSection(selectedPropertyKey);
	const isRemoteSection = !!selectedRemoteCriterionType;

	const {
		items: remoteItems,
		loading: remoteLoading,
		page: remotePage,
		setPage: setRemotePage,
		totalPages: remoteTotalPages,
	} = usePaginatedProperties({
		channelId,
		enabled: type === SegmentTypes.Batch,
		groupId,
		keywords: searchValue,
		pageSize: REMOTE_PAGE_SIZE,
		source: selectedRemoteCriterionType,
	});

	useEffect(() => {
		remoteItems.forEach((property) => property && addProperty?.(property));
	}, [remoteItems]);

	useEffect(() => {
		if (type !== SegmentTypes.Batch || !criteriaString || !addProperty) {
			return;
		}

		extractRemoteCriterionEntries(
			translateQueryToCriteria(criteriaString)
		).forEach(({criterionType, id, name}) => {
			addProperty(criterionType.createProperty({id, name}));
		});
	}, []);

	const effectivePropertyGroupsIList = useMemo(
		() =>
			propertyGroupsIList
				.map((group) => {
					if (!group || !getPaginatedSection(group.propertyKey)) {
						return group as PropertyGroup;
					}

					return group.set(
						'propertySubgroups',
						List([new PropertySubgroup({properties: remoteItems})])
					) as PropertyGroup;
				})
				.toList(),
		[propertyGroupsIList, remoteItems]
	);

	const groupedBySection = useMemo(
		() =>
			propertyGroupsIList
				.toArray()
				.reduce<Record<string, PropertyGroup[]>>((acc, pg) => {
					const groupKey =
						PROPERTY_KEY_TO_GROUP[pg.propertyKey] ?? 'attributes';

					if (!acc[groupKey]) {
						acc[groupKey] = [];
					}

					acc[groupKey].push(pg);

					return acc;
				}, {}),
		[propertyGroupsIList]
	);

	const pickerItems: IPickerGroup[] = useMemo(
		() =>
			GROUP_ORDER.filter(
				(groupKey) => groupedBySection[groupKey]?.length > 0
			).map((groupKey) => ({
				items: groupedBySection[groupKey].map(
					({label, propertyKey}) => ({
						label,
						value: propertyKey,
					})
				),
				label: GROUP_LABELS[groupKey] ?? groupKey,
			})),
		[groupedBySection]
	);

	const isEventsSection = selectedPropertyKey === EVENTS_PROPERTY_KEY;

	const eventsGroup = propertyGroupsIList.find(
		(group) => group?.propertyKey === EVENTS_PROPERTY_KEY
	);

	const defaultEvents =
		eventsGroup?.propertySubgroups.first()?.properties ?? List<Property>();

	const isAttributesSection =
		selectedPropertyKey === FieldOwnerTypes.Individual ||
		selectedPropertyKey === FieldOwnerTypes.Organization;

	const attributesGroup = propertyGroupsIList.find(
		(group) => group?.propertyKey === selectedPropertyKey
	);

	const renderCriteria = () => {
		if (isEventsSection) {
			return (
				<EventsCriteriaTabs
					channelId={channelId}
					defaultEvents={defaultEvents}
					groupId={groupId}
					searchValue={searchValue}
				/>
			);
		}

		if (isAttributesSection) {
			return (
				<AttributesCriteriaTabs
					customProperties={
						attributesGroup?.propertySubgroups.get(1)?.properties ??
						List<Property>()
					}
					defaultProperties={
						attributesGroup?.propertySubgroups.first()
							?.properties ?? List<Property>()
					}
					searchValue={searchValue}
				/>
			);
		}

		if (isRemoteSection && remoteLoading) {
			return <Loading overlay />;
		}

		return (
			<CriteriaSidebarCollapse
				propertyGroupsIList={effectivePropertyGroupsIList}
				propertyKey={selectedPropertyKey ?? ''}
				searchValue={isRemoteSection ? '' : searchValue}
			/>
		);
	};

	return (
		<div className="criteria-sidebar-root">
			<div className="sidebar-title">
				{Liferay.Language.get('segment-criteria')}
			</div>

			{type !== SegmentTypes.RealTime && (
				<div className="sidebar-header">
					<Picker
						items={pickerItems}
						onSelectionChange={(key) => {
							setSelectedPropertyKey(key as string);
						}}
						selectedKey={selectedPropertyKey ?? undefined}
					>
						{(group: IPickerGroup) => (
							<ClayDropDown.Group
								header={group.label}
								items={group.items}
							>
								{(item: {label: string; value: string}) => (
									<Option key={item.value}>
										{item.label}
									</Option>
								)}
							</ClayDropDown.Group>
						)}
					</Picker>
				</div>
			)}

			<div className="sidebar-search">
				<CriteriaSidebarSearchBar
					onChange={setSearchValue}
					searchValue={searchValue}
				/>
			</div>

			<div className="sidebar-collapse">{renderCriteria()}</div>

			{isRemoteSection && (
				<SidebarPagination
					activePage={remotePage}
					onPageChange={setRemotePage}
					totalPages={remoteTotalPages}
				/>
			)}
		</div>
	);
}
