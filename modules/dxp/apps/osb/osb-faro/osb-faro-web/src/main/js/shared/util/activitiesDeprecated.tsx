import moment from 'moment';
import React from 'react';
import {
	ActivityActions,
	AssetTypes,
	TimeIntervals
} from 'shared/util/constants';
import {
	countBy,
	filter,
	flattenDepth,
	flow,
	get,
	groupBy,
	map,
	mapValues,
	orderBy,
	toPairs
} from 'lodash/fp';
import {DEFAULT_ACTIVITY_MAX} from 'shared/api/activities';
import {RangeSelectors} from 'shared/types';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';

export const CHART_ACTIVITY_ID = 'activities';
export const CHART_ID = 'individualActivity';

const ACTIVITY_ACTIONS_TITLE_LANG_MAP = {
	[ActivityActions.Comments]: Liferay.Language.get('commented-on-x'),
	[ActivityActions.Downloads]: Liferay.Language.get('downloaded-x'),
	[ActivityActions.Impressions]: Liferay.Language.get('impression-made-on-x'),
	[ActivityActions.Submissions]: Liferay.Language.get('submitted-x'),
	[ActivityActions.Visits]: Liferay.Language.get('visited-x')
};

const ACTIVITY_ACTIONS_DESCRIPTION_LANG_MAP = {
	[ActivityActions.Comments]: {
		plural: Liferay.Language.get('x-comments'),
		singular: Liferay.Language.get('x-comment')
	},
	[ActivityActions.Downloads]: {
		plural: Liferay.Language.get('x-downloads'),
		singular: Liferay.Language.get('x-download')
	},
	[ActivityActions.Impressions]: {
		plural: Liferay.Language.get('x-impressions'),
		singular: Liferay.Language.get('x-impression')
	},
	[ActivityActions.Submissions]: {
		plural: Liferay.Language.get('x-submissions'),
		singular: Liferay.Language.get('x-submission')
	},
	[ActivityActions.Visits]: {
		plural: Liferay.Language.get('x-visits'),
		singular: Liferay.Language.get('x-visit')
	}
};

export const INTERVAL_MAP = {
	D: TimeIntervals.Day,
	M: TimeIntervals.Month,
	W: TimeIntervals.Week
};

/**
 * Format actvitiy metrics for use in ChangeLegend
 * @param {Object} changeMetrics - History data points.
 * @param {number} changeMetrics.activityChange - The activity count change from
 *                                                previous period.
 * @param {number} changeMetrics.activityCount - The activity count.
 * @return {Array} Activity metrics formatted for use in ChangeLegend.
 */
export function buildLegendItems({
	activityChange,
	activityCount
}: {
	activityChange: number;
	activityCount: number;
}): {change: number; id: string; secondaryInfo: string; title: string}[] {
	return [
		{
			change: activityChange,
			id: CHART_ACTIVITY_ID,
			secondaryInfo: sub(Liferay.Language.get('x-day-change'), [
				DEFAULT_ACTIVITY_MAX
			]) as string,
			title: sub(Liferay.Language.get('total-activity-count-x'), [
				activityCount.toLocaleString()
			]) as string
		}
	];
}

/**
 * Filters out activities that are not in the activity actions title lang map
 * and formats it into an array of object for a vertical timeline.
 * @param {Array} activities
 * @param {string|number} groupId
 * @param {string} channelId
 * @returns {Array.<Object>} Array of objects for a vertical timeline.
 */
function formatActivities(
	activities: any[],
	groupId: string,
	channelId: string
) {
	return activities
		.filter(({action}) => !!ACTIVITY_ACTIONS_TITLE_LANG_MAP[action])
		.map(
			({
				action,
				assetType,
				canonicalUrl,
				dataSourceAssetPK,
				id,
				name,
				startTime
			}) => {
				const assetRoute = getAssetRoute(assetType);

				const assetURL = assetRoute
					? `${toRoute(assetRoute, {
							assetId:
								assetType === AssetTypes.WebPage && canonicalUrl
									? canonicalUrl
									: dataSourceAssetPK,
							channelId,
							groupId,
							title: encodeURIComponent(name),
							touchpoint:
								assetType !== AssetTypes.WebPage
									? 'Any'
									: canonicalUrl
									? encodeURIComponent(canonicalUrl)
									: dataSourceAssetPK
					  })}`
					: null;

				return {
					subtitle: canonicalUrl,
					symbol: getObjectTypeIcon(assetType),
					time: startTime,
					title: sub(
						ACTIVITY_ACTIONS_TITLE_LANG_MAP[action],
						[<strong key={id}>{name}</strong>],
						false
					),
					url: assetURL
				};
			}
		);
}

/**
 * Formats datetime to today or the current date.
 * @param {Date|string|number} datetime - Any value accepeted by Moment.
 * @returns {Moment} Date label to be displayed.
 */
export function formatGroupingTime(
	datetime: Date | string | number
): moment.Moment {
	const time = moment(datetime);

	return time.isSame(moment(), 'day')
		? Liferay.Language.get('today')
		: time.utc().format('ll');
}

/**
 * Format sessions into a format usable by the VerticalTimeline component while grouping them by day.
 * @param {Array} sessions
 * @param {string} groupId
 * @param {string} channelId
 * @returns {Array.<Object>} An array of session objects.
 */
export function formatSessions(
	sessions: any[],
	groupId: string,
	channelId: string
): any[] {
	return flow(
		groupBy(({day}) => moment.utc(day).startOf('day').format()),
		mapValues(items =>
			items.map(({activities, id, individual, startTime}) => ({
				id,
				individual,
				nestedItems: formatActivities(activities, groupId, channelId),
				subtitle: getActivitiesSummary(activities),
				time: startTime,
				title: sub(Liferay.Language.get('visited-x'), [
					new URL(activities[0].url).hostname
				])
			}))
		),
		toPairs,
		orderBy([([time]) => moment(time).unix()], ['desc']),
		map(([time, items]: any[]) => [
			{header: true, title: formatGroupingTime(time)},
			items
		]),
		flattenDepth(2)
	)(sessions);
}

/**
 * Gets the summary of activity types for a session. Wraps items in <span> for
 * styling. Example of output displayed: '3 Downloads 2 Visits'.
 * @returns {Array} Description to display
 */
const getActivitiesSummary: (activities: any[]) => React.ReactNode[] = flow(
	filter(({action}) => !!ACTIVITY_ACTIONS_DESCRIPTION_LANG_MAP[action]),
	countBy(({action}) => action),
	toPairs,
	map(([action, count]) => [
		<span key={action}>
			{sub(
				get(
					[action, count === 1 ? 'singular' : 'plural'],
					ACTIVITY_ACTIONS_DESCRIPTION_LANG_MAP
				),
				[count]
			)}
		</span>
	]),
	flattenDepth(2)
);

/**
 * Helper function get the correct pluralization of count label.
 * @param {Number} totalElements
 * @returns {Array} Label to be displayed.
 */
export function getActivityLabel(totalElements: number): React.ReactNode[] {
	return sub(
		totalElements === 1
			? Liferay.Language.get('activity-x')
			: Liferay.Language.get('activities-x'),
		[<b key='ACTIVITIES'>{totalElements}</b>],
		false
	) as React.ReactNode[];
}

/**
 * Get the asset route from the assetType.
 * @param {string} assetType
 * @return {string} Route to assetType page.
 */
function getAssetRoute(assetType: string): string {
	switch (assetType) {
		case AssetTypes.Blog:
			return Routes.ASSETS_BLOGS_OVERVIEW;
		case AssetTypes.Document:
			return Routes.ASSETS_DOCUMENTS_AND_MEDIA_OVERVIEW;
		case AssetTypes.Form:
			return Routes.ASSETS_FORMS_OVERVIEW;
		case AssetTypes.WebPage:
			return Routes.SITES_TOUCHPOINTS_OVERVIEW;
		default:
			return null;
	}
}

/**
 * Get the icon name from the assetType.
 * @param {string} assetType
 * @return {string} Name of icon.
 */
function getObjectTypeIcon(assetType: string): string {
	switch (assetType) {
		case AssetTypes.Blog:
			return 'ac_blogs';
		case AssetTypes.Document:
			return 'ac_documents_and_media';
		case AssetTypes.Form:
			return 'forms';
		case AssetTypes.WebPage:
			return 'page';
		default:
			return 'folder';
	}
}

export const getSafeRangeKey = (
	rangeKey: RangeSelectors['rangeKey']
): RangeSelectors['rangeKey'] | null => {
	if (rangeKey === 'CUSTOM') {
		return null;
	}

	return rangeKey;
};
