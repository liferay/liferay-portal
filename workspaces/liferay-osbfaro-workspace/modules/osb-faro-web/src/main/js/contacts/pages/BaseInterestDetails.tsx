import * as API from 'shared/api';
import Card from 'shared/components/Card';
import ClayIcon from 'shared/components/Icon';
import ClayLink from '@clayui/link';
import ClayNavigationBar from '@clayui/navigation-bar';
import InterestPagesList from 'contacts/components/InterestPagesList';
import React from 'react';
import SearchableEntityTable from 'shared/components/SearchableEntityTable';
import {Account, Segment} from 'shared/util/records';
import {
	ACCOUNTS,
	INDIVIDUALS,
	PAGES,
	Routes,
	SEGMENTS,
	setUriQueryValue,
	toRoute,
} from 'shared/util/router';
import {createOrderIOMap, NAME} from 'shared/util/pagination';
import {formatUTCDateFromUnix} from 'shared/util/date';
import {getSafeDecodedURIComponent} from 'shared/util/util';
import {individualsListColumns} from 'shared/util/table-columns';
import {sub} from 'shared/util/lang';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';

const tabIds = {
	INDIVIDUALS,
	PAGES,
};

interface IIndividualListProps {
	channelId: string;
	groupId: string;
}

const IndividualsList: React.FC<IIndividualListProps> = ({
	channelId,
	groupId,
	...otherProps
}) => {
	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME),
	});

	return (
		<SearchableEntityTable
			{...otherProps}
			columns={[
				individualsListColumns.getName({channelId, groupId}),
				individualsListColumns.email,
				individualsListColumns.accountName,
			]}
			dataSourceFn={API.individuals.search}
			delta={delta}
			entityLabel={Liferay.Language.get('individuals')}
			orderByOptions={[
				{
					label: Liferay.Language.get('name'),
					value: NAME,
				},
			]}
			orderIOMap={orderIOMap}
			page={page}
			query={query}
			rowIdentifier="id"
		/>
	);
};

interface IInterestDetailsListProp {
	channelId: string;
	className?: string;
	dataSourceParams: {[key: string]: any};
	groupId: string;
	tabId: (typeof tabIds)[keyof typeof tabIds];
}

const InterestDetailsList: React.FC<IInterestDetailsListProp> = ({
	tabId,
	...otherProps
}) => {
	if (tabId == tabIds.INDIVIDUALS) {
		return <IndividualsList {...otherProps} />;
	}
	else {
		return <InterestPagesList {...otherProps} />;
	}
};

interface IBaseInterestDetailsProps extends IInterestDetailsListProp {
	active?: string;
	entity: Account | Segment;
	id: string;
	interestDetailsRoute: string;
	interestId: string;
	type: typeof ACCOUNTS | typeof SEGMENTS;
}

const BaseInterestDetails: React.FC<IBaseInterestDetailsProps> = ({
	active: rawActive,
	channelId,
	entity,
	groupId,
	id,
	interestDetailsRoute,
	interestId,
	tabId,
	type,
}) => {
	const active = rawActive === 'true';

	const navigationItems = [
		{
			active: tabIds.INDIVIDUALS === tabId,
			href: toRoute(interestDetailsRoute, {
				channelId,
				groupId,
				id,
				interestId,
				tabId: tabIds.INDIVIDUALS,
			}),
			label: Liferay.Language.get('individuals'),
		},
		{
			active: tabIds.PAGES === tabId && active,
			href: setUriQueryValue(
				toRoute(interestDetailsRoute, {
					channelId,
					groupId,
					id,
					interestId,
					tabId: PAGES,
				}),
				'active',
				true
			),
			label: Liferay.Language.get('active-pages'),
		},
		{
			active: tabIds.PAGES === tabId && !active,
			href: setUriQueryValue(
				toRoute(interestDetailsRoute, {
					channelId,
					groupId,
					id,
					interestId,
					tabId: PAGES,
				}),
				'active',
				false
			),
			label: Liferay.Language.get('inactive-pages'),
		},
	];

	const interestName = getSafeDecodedURIComponent(interestId);

	const individualsEntityKey =
		type === ACCOUNTS ? 'accountId' : 'individualSegmentId';

	let dataSourceParams: {[key: string]: any} = {
		channelId,
		contactsEntityId: id,
		contactsEntityType: entity.type,
		groupId,
		[individualsEntityKey]: id,
		interestName,
	};

	if (tabId === tabIds.PAGES) {
		dataSourceParams = {
			...dataSourceParams,
			active,
		};
	}

	return (
		<div className="interest-details-root">
			<div className="back-button-root mb-2">
				<ClayLink
					borderless
					button
					displayType="secondary"
					href={toRoute(Routes.CONTACTS_INTERESTS, {
						channelId,
						groupId,
						id,
						type,
					})}
					small
				>
					<ClayIcon
						className="icon-root icon-size-sm mr-2"
						symbol="angle-left-small"
					/>

					{Liferay.Language.get('back-to-interests')}
				</ClayLink>
			</div>

			<Card pageDisplay>
				<Card.Header>
					<Card.Title>
						{sub(
							Liferay.Language.get('interest-x'),
							[
								<span
									className="interest-name"
									key="INTEREST_NAME"
								>
									{interestName}
								</span>,
							],
							false
						)}
					</Card.Title>
				</Card.Header>

				<Card.Header>
					<ClayNavigationBar
						className="page-subnav"
						key="subnav"
						triggerLabel="label"
					>
						{navigationItems.map(({active, href, label}) => (
							<ClayNavigationBar.Item active={active} key={label}>
								<ClayLink href={href}>
									<div className="h4">{label}</div>
								</ClayLink>
							</ClayNavigationBar.Item>
						))}
					</ClayNavigationBar>

					<div className="h4 list-title">
						{tabId === tabIds.INDIVIDUALS
							? sub(
									Liferay.Language.get(
										'members-interested-in-x-as-of-x'
									),
									[
										interestName,
										formatUTCDateFromUnix(
											Date.now() - 24 * 60 * 60 * 1000
										),
									]
								)
							: sub(
									Liferay.Language.get(
										'pages-containing-x-as-a-keyword'
									),
									[interestName]
								)}
					</div>
				</Card.Header>

				<InterestDetailsList
					channelId={channelId}
					className="interest-history-table d-flex flex-column flex-grow-1"
					dataSourceParams={dataSourceParams}
					groupId={groupId}
					tabId={tabId}
				/>
			</Card>
		</div>
	);
};

export default BaseInterestDetails;
