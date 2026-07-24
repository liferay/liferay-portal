import {AssetTypes} from 'shared/util/constants';
import {RangeSelectors} from 'shared/types';
import {Routes, setUriQueryValues, toRoute} from 'shared/util/router';
import {UserSessionEvent} from 'shared/queries/UserSessionQuery';

/**
 * Maps an event's `applicationId` to the asset dashboard route, the trailing
 * `:type` segment value, and the event property that holds the asset id (which
 * varies by asset type — e.g. `articleId` for web content, `formId` for forms).
 */
const ASSET_APPLICATIONS: Record<
	string,
	{idProperty: string; route: string; type: string}
> = {
	[AssetTypes.Blog]: {
		idProperty: 'entryId',
		route: Routes.ASSETS_BLOGS_OVERVIEW,
		type: 'blog',
	},
	[AssetTypes.Document]: {
		idProperty: 'fileEntryId',
		route: Routes.ASSETS_DOCUMENTS_AND_MEDIA_OVERVIEW,
		type: 'document',
	},
	[AssetTypes.Form]: {
		idProperty: 'formId',
		route: Routes.ASSETS_FORMS_OVERVIEW,
		type: 'form',
	},
	[AssetTypes.WebContent]: {
		idProperty: 'articleId',
		route: Routes.ASSETS_WEB_CONTENT_OVERVIEW,
		type: 'webContent',
	},
};

export interface EventDashboardContext {
	accountId?: string;
	accountName?: string;
	channelId?: string;
	groupId?: string;
	isWebhook?: boolean;
	rangeSelectors?: RangeSelectors;
}

const getProperty = (
	event: UserSessionEvent,
	name: string
): string | undefined =>
	event.properties?.find((property) => property.name === name)?.value;

const buildQuery = (rangeSelectors?: RangeSelectors): string => {
	if (!rangeSelectors) {
		return '';
	}

	const {rangeEnd, rangeKey, rangeStart} = rangeSelectors;

	const params = [
		rangeKey != null && `rangeKey=${rangeKey}`,
		rangeStart && `rangeStart=${rangeStart}`,
		rangeEnd && `rangeEnd=${rangeEnd}`,
	].filter(Boolean);

	return params.length ? `?${params.join('&')}` : '';
};

/**
 * Builds the dashboard link for an activity-stream event's title. Shared by the
 * account and individual timelines (the event behavior is not account-specific).
 *
 * - Webhook events are only logs, so they have no dashboard (returns undefined).
 * - Asset events (identified by their `applicationId`) link to the asset
 *   dashboard, using the type-specific id property as the asset id; the
 *   touchpoint is always "Any".
 * - Object-entry events link to the object-entry dashboard, using the entry's
 *   external reference code as the asset id and the object definition name as
 *   the dashboard type.
 * - pageViewed events link to the page (touchpoint) dashboard, using the
 *   canonical URL as the touchpoint. The account timeline additionally passes
 *   `accountId`/`accountName`, so the page dashboard opens pre-filtered by
 *   that account; the individual timeline never provides them.
 * - Anything else (e.g. unmapped application ids) returns undefined.
 */
export const getEventDashboardUrl = (
	event: UserSessionEvent,
	{
		accountId,
		accountName,
		channelId,
		groupId,
		isWebhook,
		rangeSelectors,
	}: EventDashboardContext
): string | undefined => {
	if (isWebhook || !channelId || !groupId) {
		return undefined;
	}

	const assetApplication = ASSET_APPLICATIONS[event.applicationId];

	if (assetApplication) {
		const assetId = getProperty(event, assetApplication.idProperty);

		if (!assetId) {
			return undefined;
		}

		const title = event.assetTitle || event.pageTitle;

		return `${toRoute(assetApplication.route, {
			assetId,
			channelId,
			groupId,
			touchpoint: 'Any',
			type: assetApplication.type,
			...(title && {title}),
		})}${buildQuery(rangeSelectors)}`;
	}

	if (event.applicationId === AssetTypes.ObjectEntry) {
		const assetId = getProperty(event, 'externalReferenceCode');
		const type = getProperty(event, 'objectDefinitionName');

		if (!assetId || !type) {
			return undefined;
		}

		const title = event.assetTitle || event.pageTitle;

		return `${toRoute(Routes.ASSETS_OBJECT_ENTRY_OVERVIEW, {
			assetId,
			channelId,
			groupId,
			touchpoint: 'Any',
			type,
			...(title && {title}),
		})}${buildQuery(rangeSelectors)}`;
	}

	if (event.applicationId === AssetTypes.WebPage && event.canonicalUrl) {
		const title = event.pageTitle || event.assetTitle;

		const href = `${toRoute(Routes.SITES_TOUCHPOINTS_OVERVIEW, {
			channelId,
			groupId,
			touchpoint: event.canonicalUrl,
			...(title && {title}),
		})}${buildQuery(rangeSelectors)}`;

		return accountId || accountName
			? setUriQueryValues({accountId, accountName}, href)
			: href;
	}

	return undefined;
};

export default getEventDashboardUrl;
