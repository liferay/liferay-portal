import AccountName from 'shared/components/table/cell-components/AccountName';
import Checkbox from 'shared/components/Checkbox';
import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import IndividualType from 'shared/components/table/cell-components/IndividualTypes';
import InfoPopover from 'shared/components/InfoPopover';
import Label from 'shared/components/Label';
import MemberCell from 'shared/components/table/cell-components/MemberCell';
import MembershipChanges from 'shared/components/table/cell-components/MembershipChanges';
import moment from 'moment';
import React from 'react';
import SegmentSticker from 'segment/components/SegmentSticker';
import SequentialEventOrderPopover from 'shared/components/SequentialEventOrderPopover';
import TextTruncate from 'shared/components/TextTruncate';
import {
	AccountNames,
	CreatedByCell,
	DateCell,
	IndividualLinkCell,
	NameCell,
	PropertyCell,
	RelativeMetricBarCell,
	SourceCell,
	WillBeRemovedCell,
} from 'shared/components/table/cell-components';
import {applyTimeZone, formatDateToTimeZone, formatUTCDate} from './date';
import {Colors} from './colors-size';
import {formatTime} from './time';
import {get, isNil, noop, pickBy} from 'lodash';
import {getSafeDecodedURIComponent} from './util';
import {Routes, setUriQueryValues, toRoute} from 'shared/util/router';
import {SegmentTypes} from './constants';
import {sub} from 'shared/util/lang';

type ChannelGroupParams = {
	channelId: string | undefined;
	groupId: string | undefined;
};

type CellRendererProps = {
	className?: string;
	data: any;
};

type RouteFn = (props: {data: any}) => string | undefined;

/**
 * Accounts List Columns
 */
export const accountsListColumns = {
	activitiesCount: {
		accessor: 'activitiesCount',
		className: 'table-column-text-end',
		dataFormatter: (data: number | string) => data.toLocaleString(),
		label: Liferay.Language.get('total-activities'),
	},
	emailAddress: {
		accessor: 'emailAddress',
		label: Liferay.Language.get('email'),
		sortable: false,
	},
	getName: ({channelId, groupId}: ChannelGroupParams) => ({
		accessor: 'name',
		cellRenderer: NameCell,
		cellRendererProps: {
			routeFn: ({data: {id}}: {data: {id: string}}) =>
				toRoute(Routes.CONTACTS_ACCOUNT, {channelId, groupId, id}),
		},
		className: 'table-cell-expand',
		label: Liferay.Language.get('name'),
	}),
	individualCount: {
		accessor: 'individualCount',
		className: 'table-column-text-end',
		dataFormatter: (data: number | string) => data.toLocaleString(),
		label: Liferay.Language.get('individuals'),
	},
	name: {
		accessor: 'name',
		cellRenderer: NameCell,
		className: 'table-cell-expand',
		label: Liferay.Language.get('account-name'),
	},
	type: {
		accessor: 'properties.accountType',
		label: Liferay.Language.get('account-type'),
	},
};

/**
 * Activity Assets List Columns
 */
export const activityAssetsListColumns = {
	commentCount: {
		accessor: 'count',
		className: 'table-column-text-end',
		dataFormatter: (data: number | string) => data.toLocaleString(),
		label: Liferay.Language.get('comments'),
	},
	downloadCount: {
		accessor: 'count',
		className: 'table-column-text-end',
		label: Liferay.Language.get('downloads'),
	},
	nameUrl: {
		accessor: 'name',
		cellRenderer: NameCell,
		cellRendererProps: {
			renderSecondaryInfo: ({
				dataSourceAssetPK,
			}: {
				dataSourceAssetPK: string;
			}) => (
				<TextTruncate
					title={getSafeDecodedURIComponent(dataSourceAssetPK)}
				/>
			),
		},
		className: 'table-cell-expand',
		label: Liferay.Language.get('name'),
		sortable: false,
	},
	submissionCount: {
		accessor: 'count',
		className: 'table-column-text-end',
		dataFormatter: (data: number | string) => data.toLocaleString(),
		label: Liferay.Language.get('submissions'),
	},
	viewCount: {
		accessor: 'count',
		className: 'table-column-text-end',
		dataFormatter: (data: number | string) => data.toLocaleString(),
		label: Liferay.Language.get('views'),
	},
};

/**
 * Assets List Columns
 */
export const assetsListColumns = {
	canonicalUrl: {
		accessor: 'canonicalUrl',
		className: 'table-cell-expand text-truncate',
		label: Liferay.Language.get('url'),
	},
	name: {
		accessor: 'name',
		cellRenderer: NameCell,
		className: 'table-cell-expand',
		label: Liferay.Language.get('name'),
	},
	type: {
		accessor: 'type',
		label: Liferay.Language.get('type'),
	},
};

/**
 * Attribute List Columns
 */

export const attributeListColumns = {
	dataType: {
		accessor: 'dataType',
		cellRenderer: ({data: {dataType}}: {data: {dataType: string}}) => (
			<td>
				<Label display="info" size="lg" uppercase>
					{dataType}
				</Label>
			</td>
		),
		label: Liferay.Language.get('data-typecast'),
		sortable: false,
	},
	description: {
		accessor: 'description',
		className: 'table-cell-expand text-truncate',
		dataFormatter: (value: string) =>
			value || <i>{Liferay.Language.get('no-description')}</i>,
		label: Liferay.Language.get('description'),
		sortable: false,
	},
	displayName: {
		accessor: 'displayName',
		className: 'table-cell-expand-small text-truncate',
		label: Liferay.Language.get('display-name'),
	},
	getName: ({channelId, groupId}: ChannelGroupParams) => ({
		accessor: 'name',
		cellRenderer: NameCell,
		cellRendererProps: {
			routeFn: ({data: {id}}: {data: {id: string}}) =>
				toRoute(Routes.SETTINGS_DEFINITIONS_EVENT_ATTRIBUTES_VIEW, {
					attributeId: id,
					channelId,
					groupId,
				}),
		},
		className: 'table-cell-expand-small',
		label: Liferay.Language.get('attribute-name'),
	}),
	name: {
		accessor: 'name',
		className: 'table-cell-expand-small text-truncate',
		label: Liferay.Language.get('attribute-name'),
	},
	sampleValue: {
		accessor: 'sampleValue',
		className: 'table-cell-expand-smaller text-truncate',
		label: Liferay.Language.get('sample-raw-data'),
		sortable: false,
	},
};

export const membershipChangesColumns = {
	accountNames: {
		accessor: 'accountName',
		cellRenderer: AccountName,
		label: Liferay.Language.get('account-name'),
		sortable: true,
	},
	firstSeen: {
		accessor: 'firstSeenTime',
		cellRenderer: ({
			className,
			data: {firstSeenTime},
		}: {
			className?: string;
			data: {firstSeenTime: string | number};
		}) => (
			<td className={getCN('name-cell-root', className)}>
				<div className="text-truncate">
					{formatUTCDate(firstSeenTime) || '-'}
				</div>
			</td>
		),
		label: Liferay.Language.get('first-seen'),
		sortable: true,
	},
	individualName: {
		accessor: 'name',
		cellRenderer: MemberCell,
		className: 'table-cell-expand',
		label: `${Liferay.Language.get('member-name')} | ${Liferay.Language.get(
			'email'
		)}`,
		sortable: true,
	},
	lastActive: {
		accessor: 'lastActivityTime',
		cellRenderer: ({
			className,
			data: {lastActivityTime},
		}: {
			className?: string;
			data: {lastActivityTime: string | number};
		}) => (
			<td className={getCN('name-cell-root', className)}>
				<div className="text-truncate">
					{formatUTCDate(lastActivityTime) || '-'}
				</div>
			</td>
		),
		label: Liferay.Language.get('last-active'),
		sortable: true,
	},
	membershipChanges: {
		accessor: 'type',
		cellRenderer: MembershipChanges,
		label: Liferay.Language.get('membership-change'),
		sortable: true,
	},
	profileType: {
		accessor: 'profileType',
		cellRenderer: IndividualType,
		label: Liferay.Language.get('profile-type'),
		sortable: true,
	},
};

export const IndividualsListCDPColumns = {
	accountNames: {
		accessor: 'accountName',
		cellRenderer: ({
			className,
			data: {accountName},
		}: {
			className?: string;
			data: {accountName: string};
		}) => (
			<td className={getCN('name-cell-root', className)}>
				<div className="text-truncate">{accountName || null}</div>
			</td>
		),
		label: Liferay.Language.get('account-name'),
		sortable: true,
	},
	country: {
		accessor: 'countries',
		cellRenderer: ({
			className,
			data: {properties},
		}: {
			className?: string;
			data: {properties: {country?: string}};
		}) => (
			<td className={getCN('name-cell-root', className)}>
				<div className="text-truncate">
					{properties.country || null}
				</div>
			</td>
		),
		label: Liferay.Language.get('country'),
		sortable: true,
	},
	firstSeen: {
		accessor: 'firstActivityDate',
		cellRenderer: ({
			className,
			data: {firstActivityDate},
		}: {
			className?: string;
			data: {firstActivityDate: string | number};
		}) => (
			<td className={getCN('name-cell-root', className)}>
				<div className="text-truncate">
					{formatUTCDate(firstActivityDate) || '-'}
				</div>
			</td>
		),
		label: Liferay.Language.get('first-seen'),
		sortable: true,
	},
	getNameEmail: ({channelId, groupId}: ChannelGroupParams) => ({
		accessor: 'name',
		cellRenderer: MemberCell,
		cellRendererProps: {
			routeFn: ({data: {id}}: {data: {id: string}}) =>
				toRoute(Routes.CONTACTS_INDIVIDUAL, {channelId, groupId, id}),
		},
		className: 'table-cell-expand',
		label: `${Liferay.Language.get('individual')} | ${Liferay.Language.get(
			'email'
		)}`,
		sortable: true,
	}),
	lastActive: {
		accessor: 'lastActivityDate',
		cellRenderer: ({
			className,
			data: {lastActivityDate},
		}: {
			className?: string;
			data: {lastActivityDate: string | number};
		}) => (
			<td className={getCN('name-cell-root', className)}>
				<div className="text-truncate">
					{lastActivityDate ? formatUTCDate(lastActivityDate) : null}
				</div>
			</td>
		),
		label: Liferay.Language.get('last-active'),
		sortable: true,
	},
	profileType: {
		accessor: 'profileType',
		cellRenderer: IndividualType,
		label: Liferay.Language.get('profile-type'),
		sortable: true,
	},
};

/**
 * Changes List Columns
 */
export const changesListColumns = {
	getDateFirst: (timeZoneId: string) => ({
		accessor: 'dateFirst',
		dataFormatter: (value: string | number | null | undefined) =>
			!isNil(value) && formatDateToTimeZone(value, 'll', timeZoneId),
		label: Liferay.Language.get('first-seen'),
	}),
	getIndividualName: ({channelId, groupId}: ChannelGroupParams) => ({
		accessor: 'individualName',
		cellRenderer: IndividualLinkCell,
		cellRendererProps: {channelId, groupId},
		label: Liferay.Language.get('name'),
		title: true,
	}),
	getOperation: (timeZoneId: string) => ({
		accessor: 'dateChanged',
		dataFormatter: (
			value: unknown,
			{
				dateChanged,
				operation,
			}: {dateChanged: string | number; operation: string}
		) =>
			operation && [
				<span key="MEMBERSHIP_CHANGE">
					{applyTimeZone(dateChanged, timeZoneId).calendar(null, {
						sameElse: 'll',
					})}

					<Label
						className="membership-operation"
						display={operation === 'ADDED' ? 'success' : 'warning'}
						key="OPERATION"
						size="lg"
					>
						{operation}
					</Label>
				</span>,
			],
		label: Liferay.Language.get('membership-change'),
	}),
	individualEmail: {
		accessor: 'individualEmail',
		label: Liferay.Language.get('email'),
		sortable: false,
	},
};

/**
 * Composition List Columns
 */

export const compositionListColumns = {
	getName: ({
		label,
		maxWidth,
		routeFn = noop,
		sortable,
		tooltip = false,
	}: {
		label: React.ReactNode;
		maxWidth?: number | null;
		routeFn?: RouteFn | typeof noop;
		sortable?: boolean;
		tooltip?: boolean;
	}) => ({
		accessor: 'name',
		cellRenderer: NameCell,
		cellRendererProps: {
			maxWidth,
			routeFn,
			tooltip,
		},
		label,
		sortable,
	}),
	getPercentOf: ({
		metricName,
		totalCount,
	}: {
		metricName: string;
		totalCount: number;
	}) => ({
		accessor: 'count',
		className: 'table-column-text-end',
		dataFormatter: (data: number) =>
			`${((data / totalCount) * 100).toFixed(2)}%`,
		label: sub(Liferay.Language.get('percent-of-x'), [metricName]),
		sortable: false,
		title: true,
	}),
	getRelativeMetricBar: ({
		empty = false,
		label,
		maxCount,
		showName = false,
		sortable = false,
		totalCount,
	}: {
		empty?: boolean;
		label: React.ReactNode;
		maxCount: number;
		showName?: boolean;
		sortable?: boolean;
		totalCount: number;
	}) => ({
		accessor: 'count',
		cellRenderer: RelativeMetricBarCell,
		cellRendererProps: {
			empty,
			maxCount,
			showName,
			totalCount,
		},
		className: 'table-cell-expand',
		label,
		sortable,
	}),
};

/**
 * Definitions List Columns
 */
export const definitionsListColumns = {
	restrictAccess: (authorized: boolean | undefined) => ({
		accessor: 'restricted',
		cellRenderer: ({data: {restricted}}: {data: {restricted: boolean}}) => (
			<td>
				<div className="d-flex justify-content-center">
					<Checkbox
						checked={restricted}
						disabled={!authorized}

						// TODO: LRAC-4204 Connect restrict access api once implemented

						onChange={() => {}}
					/>
				</div>
			</td>
		),
		label: (
			<div>
				<span className="mr-2">
					{Liferay.Language.get('restrict-external-access')}
				</span>

				<InfoPopover
					content={Liferay.Language.get(
						'restricting-external-access-will-remove-this-field-from-appearing-in-exports'
					)}
					title={Liferay.Language.get('restrict-access')}
				/>
			</div>
		),
		sortable: false,
	}),
};

/**
 * Details List Columns
 */
export const detailsListColumns = {
	getDataSourceName: (groupId: string | undefined) => ({
		accessor: 'dataSourceName',
		cellRenderer: SourceCell,
		cellRendererProps: {groupId},
		label: Liferay.Language.get('data-source'),
	}),
	getDateModified: (timeZoneId: string | undefined) => ({
		accessor: 'dateModified',
		dataFormatter: (dateModified: string | number | null | undefined) =>
			!isNil(dateModified) &&
			applyTimeZone(dateModified, timeZoneId).fromNow(),
		label: Liferay.Language.get('last-modified'),
	}),
	name: {
		accessor: 'name',
		cellRenderer: PropertyCell,
		className: 'table-cell-expand',
		label: Liferay.Language.get('attribute[noun]'),
	},
	sourceName: {
		accessor: 'sourceName',
		className: 'table-cell-expand-small',
		label: Liferay.Language.get('source-name'),
	},
};

/**
 * Event List Columns
 */
export const eventListColumns = {
	description: {
		accessor: 'description',
		cellRenderer: ({
			className,
			data: {description, hidden},
		}: {
			className?: string;
			data: {description: string; hidden: boolean};
		}) => (
			<td className={getCN(className, {'table-cell-secondary': hidden})}>
				{description}
			</td>
		),
		className: 'table-cell-expand text-truncate',
		label: Liferay.Language.get('description'),
		sortable: false,
	},
	displayName: {
		accessor: 'displayName',
		cellRenderer: ({
			className,
			data: {displayName, hidden},
		}: {
			className?: string;
			data: {displayName: string; hidden: boolean};
		}) => (
			<td className={getCN(className, {'table-cell-secondary': hidden})}>
				{displayName}
			</td>
		),
		className: 'table-cell-expand-small text-truncate',
		label: Liferay.Language.get('display-name'),
	},
	getLastSeenDate: (timeZoneId: string) => ({
		accessor: 'lastSeenDate',
		cellRenderer: ({className, data}: CellRendererProps) => (
			<DateCell
				className={getCN(className, {
					'table-cell-secondary': data.hidden,
				})}
				data={data}
				dateFormatter={(date: string | number) =>
					formatDateToTimeZone(date, 'll', timeZoneId)
				}
				datePath="lastSeenDate"
			/>
		),
		label: Liferay.Language.get('last-seen'),
	}),
	getName: ({groupId}: {groupId: string}) => ({
		accessor: 'name',
		cellRenderer: ({className, data, ...otherProps}: CellRendererProps) => (
			<NameCell
				{...otherProps}
				className={getCN(className, {
					'table-cell-secondary': data.hidden,
				})}
				data={data}
			/>
		),
		cellRendererProps: {
			routeFn: ({data: {id}}: {data: {id: string}}) =>
				toRoute(Routes.SETTINGS_DEFINITIONS_EVENTS_VIEW, {
					eventId: id,
					groupId,
				}),
		},
		className: 'table-cell-expand-small',
		label: Liferay.Language.get('event-name'),
	}),
	hidden: {
		accessor: 'hidden',
		cellRenderer: ({data: {hidden}}: {data: {hidden: boolean}}) => (
			<td className="text-right">
				{hidden && (
					<ClayIcon
						className={getCN('icon-root', Colors.Secondary)}
						symbol="ac_hidden"
					/>
				)}
			</td>
		),
		sortable: false,
	},
	lastSeenURL: {
		accessor: 'lastSeenURL',
		cellRenderer: ({
			className,
			data: {hidden, lastSeenURL},
		}: {
			className?: string;
			data: {hidden: boolean; lastSeenURL: string};
		}) => (
			<td className={getCN(className, {'table-cell-secondary': hidden})}>
				{lastSeenURL}
			</td>
		),
		className: 'table-cell-expand text-truncate',
		label: Liferay.Language.get('last-seen-url'),
		sortable: false,
	},
	name: {
		accessor: 'name',
		cellRenderer: ({className, data, ...otherProps}: CellRendererProps) => (
			<NameCell
				{...otherProps}
				className={getCN(className, {
					'table-cell-secondary': data.hidden,
				})}
				data={data}
			/>
		),
		className: 'table-cell-expand-small',
		label: Liferay.Language.get('event-name'),
	},
};

/**
 * Individuals List Columns
 */
export const individualsListColumns = {
	accountName: {
		accessor: 'accountName',
		cellRenderer: AccountNames,
		label: Liferay.Language.get('account-names'),
		sortable: false,
	},
	activitiesCount: {
		accessor: 'activitiesCount',
		className: 'table-column-text-end',
		dataFormatter: (data: number | string) => data.toLocaleString(),
		label: Liferay.Language.get('total-activities'),
	},
	email: {
		accessor: 'properties.email',
		label: Liferay.Language.get('email'),
		sortable: false,
	},
	getDateCreated: (timeZoneId: string) => ({
		accessor: 'dateCreated',
		cellRenderer: ({data}: CellRendererProps) => (
			<DateCell
				data={data}
				dateFormatter={(date: string | number) =>
					formatDateToTimeZone(date, 'll', timeZoneId)
				}
				datePath="dateCreated"
			/>
		),
		label: Liferay.Language.get('first-seen'),
	}),
	getLastActivityDate: (timeZoneId: string) => ({
		accessor: 'lastActivityDate',
		dataFormatter: (data: string | number | null | undefined) =>
			!isNil(data) && formatDateToTimeZone(data, 'll', timeZoneId),
		label: Liferay.Language.get('last-activity'),
	}),
	getName: ({channelId, groupId}: ChannelGroupParams) => ({
		accessor: 'name',
		cellRenderer: IndividualLinkCell,
		cellRendererProps: {channelId, groupId},
		label: Liferay.Language.get('name'),
		title: true,
	}),
	getNameEmail: ({channelId, groupId}: ChannelGroupParams) => ({
		accessor: 'name',
		cellRenderer: NameCell,
		cellRendererProps: {
			renderSecondaryInfo: (data: any) => get(data, 'properties.email'),
			routeFn: ({data: {id}}: {data: {id: string}}) =>
				toRoute(Routes.CONTACTS_INDIVIDUAL, {channelId, groupId, id}),
		},
		className: 'table-cell-expand',
		label: Liferay.Language.get('name-email'),
	}),
	getNameJobTitle: ({channelId, groupId}: ChannelGroupParams) => ({
		cellRenderer: NameCell,
		cellRendererProps: {
			renderSecondaryInfo: (data: any) =>
				get(data, 'properties.jobTitle'),
			routeFn: ({data: {id}}: {data: {id: string}}) =>
				toRoute(Routes.CONTACTS_INDIVIDUAL, {channelId, groupId, id}),
		},
		className: 'name',
		label: Liferay.Language.get('individual'),
		sortable: false,
	}),
	jobTitle: {
		accessor: 'properties.jobTitle',
		className: 'table-cell-expand',
		label: Liferay.Language.get('job-title'),
	},
	name: {
		accessor: 'name',
		cellRenderer: NameCell,
		cellRendererProps: {
			renderSecondaryInfo: (data: any) => get(data, 'properties.email'),
		},
		className: 'table-cell-expand',
		label: Liferay.Language.get('name-email'),
	},
	willBeRemoved: {
		accessor: 'dataSourceIndividualPKs',
		cellRenderer: WillBeRemovedCell,
		className: 'table-cell-expand table-column-text-center',
	},
};

/**
 * Interest List Columns
 */

export const interestListColumns = {
	getInterestMetricBar: ({
		countKey,
		total,
	}: {
		countKey: string;
		total: number;
	}) => ({
		cellRenderer: RelativeMetricBarCell,
		cellRendererProps: {countKey, total},
		className: 'table-cell-expand',
		sortable: false,
	}),
	getName: ({
		channelId,
		groupId,
		id,
		maxWidth,
		routeFn = ({data: {name}}: {data: {name: string}}) =>
			name &&
			toRoute(Routes.CONTACTS_INTEREST_DETAILS, {
				channelId,
				groupId,
				id,
				interestId: name,
				type,
			}),
		type,
	}: {
		channelId: string | undefined;
		groupId: string | undefined;
		id: string | undefined;
		maxWidth?: number | null;
		routeFn?: (props: {data: {name: string}}) => string | undefined;
		type: string;
	}) => ({
		accessor: 'name',
		cellRenderer: NameCell,
		cellRendererProps: {
			maxWidth,
			routeFn,
		},
		label: Liferay.Language.get('interest'),
	}),
	getPercentOf: ({
		metricName,
		total,
	}: {
		metricName: string;
		total: number;
	}) => ({
		accessor: 'count',
		className: 'table-column-text-end',
		dataFormatter: (data: number) =>
			`${((data / total) * 100).toFixed(2)}%`,
		label: sub(Liferay.Language.get('percent-of-x'), [metricName]),
		sortable: false,
		title: true,
	}),
	interestedMembers: {
		accessor: 'individualCount',
		className: 'table-column-text-end',
		label: Liferay.Language.get('interested-members'),
		title: true,
	},
	sessions: {
		accessor: 'sessionsCount',
		className: 'table-column-text-end',
		label: Liferay.Language.get('sessions'),
		title: true,
	},
};

/**
 * Metrics List Columns
 */

export const metricsListColumns = {
	abandonmentsMetric: {
		accessor: 'abandonmentsMetric',
		className: 'table-column-text-end',
		dataFormatter: (data: number) => `${(data * 100).toFixed(2)}%`,
		label: Liferay.Language.get('abandonment'),
	},
	avgTimeOnPageMetric: {
		accessor: 'avgTimeOnPageMetric',
		className: 'table-column-text-end',
		dataFormatter: formatTime,
		label: Liferay.Language.get('time-on-page'),
	},
	bounceRateMetric: {
		accessor: 'bounceRateMetric',
		className: 'table-column-text-end',
		dataFormatter: (data: number) => `${(data * 100).toFixed(1)}%`,
		label: Liferay.Language.get('bounce-rate'),
	},
	commentsMetric: {
		accessor: 'commentsMetric',
		className: 'table-column-text-end',
		dataFormatter: (data: number | string) => data.toLocaleString(),
		label: Liferay.Language.get('comments'),
	},
	completionTimeMetric: {
		accessor: 'completionTimeMetric',
		className: 'table-column-text-end',
		dataFormatter: formatTime,
		label: Liferay.Language.get('completion-time'),
	},
	downloadsMetric: {
		accessor: 'downloadsMetric',
		className: 'table-column-text-end',
		dataFormatter: (data: number | string) => data.toLocaleString(),
		label: Liferay.Language.get('downloads'),
	},
	entrancesMetric: {
		accessor: 'entrancesMetric',
		className: 'table-column-text-end',
		dataFormatter: (data: number | string) => data.toLocaleString(),
		label: Liferay.Language.get('entrances'),
	},
	exitRateMetric: {
		accessor: 'exitRateMetric',
		className: 'table-column-text-end',
		dataFormatter: (data: number) => `${(data * 100).toFixed(2)}%`,
		label: Liferay.Language.get('exit-percentage'),
	},
	getCreateDate: (timeZoneId: string) => ({
		accessor: 'createDate',
		cellRenderer: ({data}: CellRendererProps) => (
			<DateCell
				data={data}
				dateFormatter={(date: string | number) =>
					formatDateToTimeZone(date, 'll', timeZoneId)
				}
				datePath="createDate"
			/>
		),
		label: Liferay.Language.get('added'),
	}),
	getNameEmail: ({
		channelId,
		groupId,
		route,
	}: ChannelGroupParams & {route: string}) => ({
		accessor: 'name',
		cellRenderer: NameCell,
		cellRendererProps: {
			renderSecondaryInfo: ({email}: {email: string}) => (
				<TextTruncate title={email} />
			),
			routeFn: ({data: {id}}: {data: {id: string}}) =>
				toRoute(route, {
					channelId,
					groupId,
					id,
				}),
		},
		className: 'table-cell-expand',
		label: Liferay.Language.get('name-email'),
		sortable: false,
	}),
	getTitleId: ({
		channelId,
		groupId,
		label,
		rangeSelectors,
		route,
	}: ChannelGroupParams & {
		label: React.ReactNode;
		rangeSelectors: Record<string, any>;
		route: string;
	}) => ({
		accessor: 'assetTitle',
		cellRenderer: NameCell,
		cellRendererProps: {
			nameKey: 'assetTitle',
			renderSecondaryInfo: ({assetId}: {assetId: string}) => (
				<TextTruncate title={assetId} />
			),
			routeFn: ({
				data: {assetId, assetTitle, id},
			}: {
				data: {assetId?: string; assetTitle?: string; id?: string};
			}) => {
				if (assetId) {
					return setUriQueryValues(
						pickBy(rangeSelectors),
						toRoute(route, {
							assetId,
							channelId,
							groupId,
							touchpoint: 'Any',
							...(assetTitle && {
								title: encodeURIComponent(assetTitle),
							}),
							...(id && {id}),
						})
					);
				}
			},
		},
		className: 'table-cell-expand',
		label,
		sortable: false,
	}),
	impressionMadeMetric: {
		accessor: 'impressionMadeMetric',
		className: 'table-column-text-end',
		dataFormatter: (data: number | string) => data.toLocaleString(),
		label: Liferay.Language.get('impressions'),
	},
	modifiedDate: {
		accessor: 'modifiedDate',
		cellRenderer: ({
			data: {modifiedByUserName, modifiedDate},
		}: {
			data: {
				modifiedByUserName: string;
				modifiedDate: string | number | null | undefined;
			};
		}) => {
			const date =
				!isNil(modifiedDate) && moment(modifiedDate).format('ll');

			return (
				<td>
					{date
						? sub(Liferay.Language.get('x-last-modified-by-x'), [
								date,
								modifiedByUserName,
							])
						: '-'}
				</td>
			);
		},
		label: Liferay.Language.get('last-modified'),
	},
	ratingsMetric: {
		accessor: 'ratingsMetric',
		className: 'table-column-text-end',
		dataFormatter: (data: number) => `${(data * 10).toFixed(2)}/10`,
		label: Liferay.Language.get('rating'),
	},
	readingTimeMetric: {
		accessor: 'readingTimeMetric',
		className: 'table-column-text-end',
		dataFormatter: formatTime,
		label: Liferay.Language.get('reading-time'),
	},
	submissionsMetric: {
		accessor: 'submissionsMetric',
		className: 'table-column-text-end',
		dataFormatter: (data: number | string) => data.toLocaleString(),
		label: Liferay.Language.get('submissions'),
	},
	viewsMetric: {
		accessor: 'viewsMetric',
		className: 'table-column-text-end',
		dataFormatter: (data: number | string) => data.toLocaleString(),
		label: Liferay.Language.get('views'),
	},
	visitorsMetric: {
		accessor: 'visitorsMetric',
		className: 'table-column-text-end',
		dataFormatter: (data: number | string) => data.toLocaleString(),
		label: Liferay.Language.get('unique-visitors'),
	},
};

/**
 * Organizations List Columns
 */

export const organizationsListColumns = [
	{
		accessor: 'name',
		cellRenderer: NameCell,
		label: Liferay.Language.get('name'),
		sortable: true,
	},
	{
		accessor: 'parentName',
		label: Liferay.Language.get('parent-organization'),
		sortable: true,
	},
	{
		accessor: 'type',
		label: Liferay.Language.get('type'),
		sortable: true,
	},
];

/**
 * Site Pages List Columns
 */

export const sitePagesListColumns = {
	getTitleUrl: ({
		channelId,
		groupId,
		rangeSelectors,
		route,
	}: ChannelGroupParams & {
		rangeSelectors: Record<string, any>;
		route: string;
	}) => ({
		accessor: 'assetTitle',
		cellRenderer: NameCell,
		cellRendererProps: {
			nameKey: 'assetTitle',
			renderSecondaryInfo: ({assetId}: {assetId: string}) => (
				<TextTruncate title={getSafeDecodedURIComponent(assetId)} />
			),
			routeFn: ({
				data: {assetId, assetTitle},
			}: {
				data: {assetId: string; assetTitle?: string};
			}) =>
				setUriQueryValues(
					pickBy(rangeSelectors),
					toRoute(route, {
						channelId,
						groupId,
						touchpoint: assetId,
						...(assetTitle && {
							title: assetTitle,
						}),
					})
				),
		},
		className: 'table-cell-expand',
		label: `${Liferay.Language.get('page-title')} | ${Liferay.Language.get(
			'canonical-url'
		)}`,
		sortable: false,
	}),
};

/**
 * Pages List Columns
 */
export const pagesListColumns = {
	canonicalUrl: {
		accessor: 'canonicalUrl',
		className: 'table-cell-expand text-truncate',
		label: Liferay.Language.get('url'),
	},
	getTitleUrl: ({
		channelId,
		groupId,
		route,
	}: ChannelGroupParams & {route: string}) => ({
		accessor: 'title',
		cellRenderer: NameCell,
		cellRendererProps: {
			nameKey: 'title',
			renderSecondaryInfo: ({assetId}: {assetId: string}) => (
				<TextTruncate title={assetId} />
			),
			routeFn: ({
				data: {dataSourceId, title, url},
			}: {
				data: {dataSourceId: string; title: string; url: string};
			}) =>
				toRoute(route, {
					channelId,
					groupId,
					siteId: dataSourceId,
					title: encodeURIComponent(title),
					touchpoint: encodeURIComponent(url),
				}),
		},
		label: Liferay.Language.get('page-title'),
	}),
	inactiveViewCount: {
		accessor: 'viewCount',
		className: 'view-count table-column-text-end',
		dataFormatter: () => '-',
		label: Liferay.Language.get('views'),
		sortable: false,
	},
	name: {
		accessor: 'name',
		cellRenderer: NameCell,
		className: 'table-cell-expand',
		label: Liferay.Language.get('title'),
	},
	title: {
		accessor: 'title',
		dataFormatter: (data: string) => <TextTruncate title={data} />,
		label: Liferay.Language.get('page-title'),
		title: true,
	},
	url: {
		accessor: 'url',
		className: 'table-cell-expand',
		dataFormatter: (data: string) => <TextTruncate title={data} />,
		label: Liferay.Language.get('url'),
	},
	viewCount: {
		accessor: 'viewCount',
		className: 'view-count table-column-text-end',
		label: Liferay.Language.get('views'),
	},
};

/**
 * Segments List Columns
 */
export const segmentsListColumns = {
	getDateCreated: (timeZoneId: string) => ({
		accessor: 'dateCreated',
		cellRenderer: ({data}: CellRendererProps) => (
			<DateCell
				data={data}
				dateFormatter={(date: string | number) =>
					formatDateToTimeZone(date, 'll', timeZoneId)
				}
				datePath="dateCreated"
			/>
		),
		label: Liferay.Language.get('date-created'),
	}),
	getName: ({channelId, groupId}: ChannelGroupParams) => ({
		accessor: 'name',
		cellRenderer: NameCell,
		cellRendererProps: {
			routeFn: ({data: {id}}: {data: {id: string}}) =>
				toRoute(Routes.CONTACTS_SEGMENT, {
					channelId,
					groupId,
					id,
				}),
		},
		className: 'table-cell-expand',
		label: Liferay.Language.get('name'),
	}),
	getOwnerName: (timeZoneId: string) => ({
		accessor: 'dateModified',
		cellRenderer: CreatedByCell,
		cellRendererProps: {timeZoneId},
		label: Liferay.Language.get('last-modified'),
	}),
	getSegmentType: (LDPEnabled: boolean) => {
		if (!LDPEnabled) return null;

		return {
			accessor: 'segmentType',
			cellRenderer: ({data}: CellRendererProps) => {
				const segmentTypeMap: Record<string, string> = {
					BATCH: Liferay.Language.get('batch'),
					REAL_TIME: Liferay.Language.get('real-time'),
				};

				return (
					<td>
						{segmentTypeMap[data.segmentType]}

						{data.segmentType === SegmentTypes.RealTime &&
							data.sequential && <SequentialEventOrderPopover />}
					</td>
				);
			},
			label: Liferay.Language.get('type'),
			sortable: false,
		};
	},
	individualAddedDate: {
		cellRenderer: DateCell,
		cellRendererProps: {
			dateFormatter: (date: string | number) =>
				moment(date).calendar(null, {
					sameElse: 'll',
				}),
			datePath: 'individualAddedDate',
		},
		label: Liferay.Language.get('date-added'),
		sortable: false,
	},
	name: {
		accessor: 'name',
		cellRenderer: NameCell,
		cellRendererProps: {renderIcon: SegmentSticker},
		className: 'table-cell-expand',
		label: Liferay.Language.get('segment-name'),
	},
};

/**
 * Users List Columns
 */
export const usersListColumns = {
	emailAddress: {
		accessor: 'emailAddress',
		className: 'table-cell-expand',
		label: Liferay.Language.get('email'),
		sortable: false,
	},
	getLastLoginDate: (timeZoneId: string) => ({
		accessor: 'lastLoginDate',
		cellRenderer: DateCell,
		cellRendererProps: {
			dateFormatter: (date: string | number) =>
				applyTimeZone(date, timeZoneId).calendar(null, {
					sameElse: 'll',
				}),
			datePath: 'lastLoginDate',
		},
		className: 'table-cell-expand',
		label: Liferay.Language.get('last-login'),
		sortable: false,
	}),
	name: {
		accessor: 'name',
		className: 'table-cell-expand',
		label: Liferay.Language.get('name'),
		sortable: false,
		title: true,
	},
	nameEmailAddress: {
		accessor: 'name',
		cellRenderer: NameCell,
		cellRendererProps: {
			renderSecondaryInfo: (data: any) => get(data, 'emailAddress'),
		},
		className: 'table-cell-expand',
		label: Liferay.Language.get('name-email'),
	},
};
