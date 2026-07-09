import * as API from 'shared/api';
import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React, {useMemo} from 'react';
import SearchableEntityTable from 'shared/components/SearchableEntityTable';
import URLConstants from 'shared/util/url-constants';
import {
	ACCOUNT_NAME,
	COUNTRY,
	createOrderIOMap,
	FIRST_ACTIVITY_DATE,
	INDIVIDUAL_TYPE,
	LAST_ACTIVITY_DATE,
	NAME,
} from 'shared/util/pagination';
import {
	AccountTypes,
	Conjunctions,
	IndividualTypes,
	RelationalOperators,
} from 'segment/segment-editor/dynamic/utils/constants';
import {FilterByType, FilterInputType, FilterOptionType} from 'shared/types';
import {IndividualsListCDPColumns} from 'shared/util/table-columns';
import {Map, Set} from 'immutable';
import {RangeKeyTimeRanges, Sizes} from 'shared/util/constants';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';
import {useStatefulPagination} from 'shared/hooks/useStatefulPagination';

const MAX_DELTA = 500;

const ORDER_BY_OPTIONS = [
	{
		label: Liferay.Language.get('name'),
		value: NAME,
	},
	{
		label: Liferay.Language.get('account-name'),
		value: ACCOUNT_NAME,
	},
	{
		label: Liferay.Language.get('country'),
		value: COUNTRY,
	},
	{
		label: Liferay.Language.get('first-seen'),
		value: FIRST_ACTIVITY_DATE,
	},
	{
		label: Liferay.Language.get('last-active'),
		value: LAST_ACTIVITY_DATE,
	},
	{
		label: Liferay.Language.get('individual-type'),
		value: INDIVIDUAL_TYPE,
	},
];

const DEFAULT_FILTER_BY_OPTIONS: FilterOptionType[] = [
	{
		key: 'activeUsers',
		label: Liferay.Language.get('active-individuals'),
		type: 'radio' as FilterInputType,
		values: [
			{
				label: Liferay.Language.get('last-24-hours'),
				value: RangeKeyTimeRanges.Last24Hours,
			},
			{
				label: Liferay.Language.get('yesterday'),
				value: RangeKeyTimeRanges.Yesterday,
			},
			{
				label: Liferay.Language.get('last-seven-days'),
				value: RangeKeyTimeRanges.Last7Days,
			},
			{
				label: Liferay.Language.get('last-28-days'),
				value: RangeKeyTimeRanges.Last28Days,
			},
			{
				label: Liferay.Language.get('last-30-days'),
				value: RangeKeyTimeRanges.Last30Days,
			},
			{
				label: Liferay.Language.get('last-90-days'),
				value: RangeKeyTimeRanges.Last90Days,
			},
			{
				label: Liferay.Language.get('last-180-days'),
				value: RangeKeyTimeRanges.Last180Days,
			},
			{
				label: Liferay.Language.get('last-year'),
				value: RangeKeyTimeRanges.LastYear,
			},
		],
	},
	{
		key: 'individualTypes',
		label: Liferay.Language.get('individual-type'),
		values: [
			{
				label: Liferay.Language.get('known-individuals'),
				value: IndividualTypes.KNOWN,
			},
			{
				label: Liferay.Language.get('anonymous-individuals'),
				value: IndividualTypes.ANONYMOUS,
			},
		],
	},
	{
		key: 'accountTypes',
		label: Liferay.Language.get('account-type'),
		values: [
			{
				label: Liferay.Language.get('known-accounts'),
				value: AccountTypes.KNOWN,
			},
			{
				label: Liferay.Language.get('unknown-accounts'),
				value: AccountTypes.UNKNOWN,
			},
		],
	},
];

function transformCountriesInQueryString(countries: string[]) {
	if (!countries || countries.length === 0) {
		return;
	}

	return countries
		.map(
			(country) =>
				`(demographics/country/value ${RelationalOperators.EQ} '${country}')`
		)
		.join(Conjunctions.Or);
}

const IndividualsList: React.FC = () => {
	const {channelId = '', groupId = ''} = useParams<{
		channelId: string;
		groupId: string;
	}>();

	const paginationParams = useStatefulPagination(undefined, {
		initialFilterBy: Map({
			activeUsers: Set([RangeKeyTimeRanges.Last30Days]),
			individualTypes: Set([IndividualTypes.KNOWN]),
		}) as FilterByType,
		initialOrderIOMap: createOrderIOMap(LAST_ACTIVITY_DATE),
	});

	const {data: countriesData, loading: countriesLoading} = useRequest({
		dataSourceFn: API.individuals.fetchFieldValues,
		variables: {
			channelId,
			delta: MAX_DELTA,
			fieldMappingFieldName: 'country',
			groupId,
		},
	});

	const FILTER_BY_OPTIONS: FilterOptionType[] = useMemo(() => {
		const countries = countriesData?.items;

		if (countries?.length) {
			return [
				{
					key: 'countries',
					label: Liferay.Language.get('country'),
					values: countries.map((country: string) => ({
						label: country,
						value: country,
					})),
				},
				...DEFAULT_FILTER_BY_OPTIONS,
			];
		}

		return DEFAULT_FILTER_BY_OPTIONS;
	}, [countriesData, countriesLoading]);

	const activeUsersValue =
		paginationParams.filterBy.get('activeUsers')?.first() ?? null;

	const rangeKey = activeUsersValue ? parseInt(activeUsersValue) : null;

	const selectedFilters = {
		accountTypes:
			paginationParams.filterBy.get('accountTypes')?.toArray() || [],
		filter: transformCountriesInQueryString(
			paginationParams.filterBy.get('countries')?.toArray()
		),
		individualTypes:
			paginationParams.filterBy.get('individualTypes')?.toArray() || [],
	};

	const renderNoResults = () => (
		<NoResultsDisplay
			description={
				<>
					{Liferay.Language.get(
						'connect-a-data-source-with-people-data'
					)}

					<ClayLink
						className="d-block mb-3"
						href={URLConstants.DataSourceConnection}
						key="DOCUMENTATION"
						target="_blank"
					>
						{Liferay.Language.get(
							'access-our-documentation-to-learn-more'
						)}
					</ClayLink>
				</>
			}
			icon={{
				border: false,
				size: Sizes.XXXLarge,
				symbol: 'ac_satellite',
			}}
			spacer
			title={Liferay.Language.get(
				'no-individuals-synced-from-data-sources'
			)}
		/>
	);

	return (
		<Card>
			<Card.Title className="card-header">
				{Liferay.Language.get('individual-profiles')}
			</Card.Title>
			<Card.Body className="no-padding">
				<div className="individuals-dashboard-known-individuals-root">
					<SearchableEntityTable
						{...paginationParams}
						columns={[
							IndividualsListCDPColumns.getNameEmail({
								channelId,
								groupId,
							}),
							IndividualsListCDPColumns.accountNames,
							IndividualsListCDPColumns.country,
							IndividualsListCDPColumns.firstSeen,
							IndividualsListCDPColumns.lastActive,
							IndividualsListCDPColumns.profileType,
						]}
						dataSourceFn={API.individuals.search}
						dataSourceParams={{
							accountTypes: selectedFilters.accountTypes.length
								? selectedFilters.accountTypes
								: undefined,
							channelId,
							filter: selectedFilters.filter,
							groupId,
							individualTypes: selectedFilters.individualTypes
								.length
								? selectedFilters.individualTypes
								: undefined,
							rangeEnd: null,
							rangeKey,
							rangeStart: null,
						}}
						filterByOptions={FILTER_BY_OPTIONS}
						key="individuals-list-table"
						noResultsRenderer={renderNoResults}
						orderByOptions={ORDER_BY_OPTIONS}
						rowIdentifier="id"
					/>
				</div>
			</Card.Body>
		</Card>
	);
};

export default IndividualsList;
