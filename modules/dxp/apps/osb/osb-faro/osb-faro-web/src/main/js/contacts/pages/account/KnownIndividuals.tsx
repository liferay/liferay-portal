import * as API from 'shared/api';
import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import SearchableEntityTable from 'shared/components/SearchableEntityTable';
import URLConstants from 'shared/util/url-constants';
import {
	ACTIVITIES_COUNT,
	createOrderIOMap,
	JOB_TITLE,
	LAST_ACTIVITY_DATE,
	NAME
} from 'shared/util/pagination';
import {connect, ConnectedProps} from 'react-redux';
import {individualsListColumns} from 'shared/util/table-columns';
import {RootState} from 'shared/store';
import {Sizes} from 'shared/util/constants';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';

const fetchIndividuals = ({id, ...otherParams}) =>
	API.individuals.search({
		...otherParams,
		accountId: id
	});

const connector = connect((store: RootState, {groupId}: {groupId: string}) => ({
	timeZoneId: store.getIn([
		'projects',
		groupId,
		'data',
		'timeZone',
		'timeZoneId'
	])
}));

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IKnownIndividualsProps extends PropsFromRedux {
	channelId: string;
	groupId: string;
	id: string;
	timeZoneId: string;
}

const KnownIndividuals: React.FC<IKnownIndividualsProps> = ({
	channelId,
	groupId,
	id,
	timeZoneId
}) => {
	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME)
	});

	return (
		<Card pageDisplay>
			<SearchableEntityTable
				columns={[
					individualsListColumns.getNameEmail({
						channelId,
						groupId
					}),
					individualsListColumns.jobTitle,
					individualsListColumns.activitiesCount,
					individualsListColumns.getLastActivityDate(timeZoneId)
				]}
				dataSourceFn={fetchIndividuals}
				dataSourceParams={{channelId, groupId, id}}
				delta={delta}
				entityLabel={Liferay.Language.get('individuals')}
				noResultsRenderer={() => (
					<NoResultsDisplay
						description={
							<>
								<span className='mr-1'>
									{Liferay.Language.get(
										'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
									)}
								</span>

								<ClayLink
									href={
										URLConstants.AccountIndividualsDocumentationLink
									}
									key='DOCUMENTATION'
									target='_blank'
								>
									{Liferay.Language.get(
										'learn-more-about-individuals'
									)}
								</ClayLink>
							</>
						}
						icon={{
							border: false,
							size: Sizes.XXXLarge,
							symbol: 'ac_satellite'
						}}
						title={Liferay.Language.get(
							'there-are-no-individuals-found'
						)}
					/>
				)}
				orderByOptions={[
					{
						label: Liferay.Language.get('name'),
						value: NAME
					},
					{
						label: Liferay.Language.get('title'),
						value: JOB_TITLE
					},
					{
						label: Liferay.Language.get('activities'),
						value: ACTIVITIES_COUNT
					},
					{
						label: Liferay.Language.get('last-activity'),
						value: LAST_ACTIVITY_DATE
					}
				]}
				orderIOMap={orderIOMap}
				page={page}
				query={query}
				rowIdentifier='id'
			/>
		</Card>
	);
};

export default connector(KnownIndividuals);
