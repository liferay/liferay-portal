/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.graphql.query.v1_0;

import com.liferay.osb.faro.rest.dto.v1_0.Account;
import com.liferay.osb.faro.rest.dto.v1_0.AssetSummaryMetric;
import com.liferay.osb.faro.rest.dto.v1_0.Channel;
import com.liferay.osb.faro.rest.dto.v1_0.Event;
import com.liferay.osb.faro.rest.dto.v1_0.EventMetric;
import com.liferay.osb.faro.rest.dto.v1_0.Individual;
import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegment;
import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembership;
import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembershipChange;
import com.liferay.osb.faro.rest.dto.v1_0.PageMetric;
import com.liferay.osb.faro.rest.dto.v1_0.SearchTerm;
import com.liferay.osb.faro.rest.dto.v1_0.UserSession;
import com.liferay.osb.faro.rest.dto.v1_0.Workspace;
import com.liferay.osb.faro.rest.resource.v1_0.AccountResource;
import com.liferay.osb.faro.rest.resource.v1_0.AssetSummaryMetricResource;
import com.liferay.osb.faro.rest.resource.v1_0.ChannelResource;
import com.liferay.osb.faro.rest.resource.v1_0.EventMetricResource;
import com.liferay.osb.faro.rest.resource.v1_0.EventResource;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualResource;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualSegmentMembershipChangeResource;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualSegmentMembershipResource;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualSegmentResource;
import com.liferay.osb.faro.rest.resource.v1_0.PageMetricResource;
import com.liferay.osb.faro.rest.resource.v1_0.SearchTermResource;
import com.liferay.osb.faro.rest.resource.v1_0.UserSessionResource;
import com.liferay.osb.faro.rest.resource.v1_0.WorkspaceResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class Query {

	public static void setAccountResourceComponentServiceObjects(
		ComponentServiceObjects<AccountResource>
			accountResourceComponentServiceObjects) {

		_accountResourceComponentServiceObjects =
			accountResourceComponentServiceObjects;
	}

	public static void setAssetSummaryMetricResourceComponentServiceObjects(
		ComponentServiceObjects<AssetSummaryMetricResource>
			assetSummaryMetricResourceComponentServiceObjects) {

		_assetSummaryMetricResourceComponentServiceObjects =
			assetSummaryMetricResourceComponentServiceObjects;
	}

	public static void setChannelResourceComponentServiceObjects(
		ComponentServiceObjects<ChannelResource>
			channelResourceComponentServiceObjects) {

		_channelResourceComponentServiceObjects =
			channelResourceComponentServiceObjects;
	}

	public static void setEventResourceComponentServiceObjects(
		ComponentServiceObjects<EventResource>
			eventResourceComponentServiceObjects) {

		_eventResourceComponentServiceObjects =
			eventResourceComponentServiceObjects;
	}

	public static void setEventMetricResourceComponentServiceObjects(
		ComponentServiceObjects<EventMetricResource>
			eventMetricResourceComponentServiceObjects) {

		_eventMetricResourceComponentServiceObjects =
			eventMetricResourceComponentServiceObjects;
	}

	public static void setIndividualResourceComponentServiceObjects(
		ComponentServiceObjects<IndividualResource>
			individualResourceComponentServiceObjects) {

		_individualResourceComponentServiceObjects =
			individualResourceComponentServiceObjects;
	}

	public static void setIndividualSegmentResourceComponentServiceObjects(
		ComponentServiceObjects<IndividualSegmentResource>
			individualSegmentResourceComponentServiceObjects) {

		_individualSegmentResourceComponentServiceObjects =
			individualSegmentResourceComponentServiceObjects;
	}

	public static void
		setIndividualSegmentMembershipResourceComponentServiceObjects(
			ComponentServiceObjects<IndividualSegmentMembershipResource>
				individualSegmentMembershipResourceComponentServiceObjects) {

		_individualSegmentMembershipResourceComponentServiceObjects =
			individualSegmentMembershipResourceComponentServiceObjects;
	}

	public static void
		setIndividualSegmentMembershipChangeResourceComponentServiceObjects(
			ComponentServiceObjects<IndividualSegmentMembershipChangeResource>
				individualSegmentMembershipChangeResourceComponentServiceObjects) {

		_individualSegmentMembershipChangeResourceComponentServiceObjects =
			individualSegmentMembershipChangeResourceComponentServiceObjects;
	}

	public static void setPageMetricResourceComponentServiceObjects(
		ComponentServiceObjects<PageMetricResource>
			pageMetricResourceComponentServiceObjects) {

		_pageMetricResourceComponentServiceObjects =
			pageMetricResourceComponentServiceObjects;
	}

	public static void setSearchTermResourceComponentServiceObjects(
		ComponentServiceObjects<SearchTermResource>
			searchTermResourceComponentServiceObjects) {

		_searchTermResourceComponentServiceObjects =
			searchTermResourceComponentServiceObjects;
	}

	public static void setUserSessionResourceComponentServiceObjects(
		ComponentServiceObjects<UserSessionResource>
			userSessionResourceComponentServiceObjects) {

		_userSessionResourceComponentServiceObjects =
			userSessionResourceComponentServiceObjects;
	}

	public static void setWorkspaceResourceComponentServiceObjects(
		ComponentServiceObjects<WorkspaceResource>
			workspaceResourceComponentServiceObjects) {

		_workspaceResourceComponentServiceObjects =
			workspaceResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupAccount(accountId: ___, groupId: ___){accountName, accountType, activitiesCount, annualRevenue, country, dateModified, firstActivityDate, id, industry, lastActivityDate, lifecycleStage, numberOfEmployees, website}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Fetch a single account by id from an Analytics Cloud Workspace. Use this when you already have an account id. To search accounts by name or filter, use `getWorkspaceGroupChannelAccountsPage`."
	)
	public Account workspaceGroupAccount(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("accountId") String accountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.getWorkspaceGroupAccount(
				groupId, accountId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannelAccounts(channelId: ___, groupId: ___, page: ___, pageSize: ___, rangeEnd: ___, rangeKey: ___, rangeStart: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List or search accounts synced to an Analytics Cloud workspace. Optionally narrow results to a single channel (also known as property). Use this to browse or search accounts by name. To fetch a single account by id, use `getAccount`."
	)
	public AccountPage workspaceGroupChannelAccounts(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("rangeEnd") String rangeEnd,
			@GraphQLName("rangeKey") String rangeKey,
			@GraphQLName("rangeStart") String rangeStart,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> new AccountPage(
				accountResource.getWorkspaceGroupChannelAccountsPage(
					groupId, channelId, rangeEnd, rangeKey, rangeStart, search,
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(accountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannelAssetSummaries(channelId: ___, groupId: ___, page: ___, pageSize: ___, rangeEnd: ___, rangeKey: ___, rangeStart: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List analytics asset summaries for pages, blogs, documents, forms, journal articles, and object entries. Rank summaries by the requested sort metric. Each summary includes download, impression, read, and view counts along with their period-over-period trend percentages. Optionally narrow results to a single channel (also known as property) or to a date range. For date-range filtering pass `rangeKey` as one of LAST_24_HOURS, YESTERDAY, LAST_7_DAYS, LAST_28_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_YEAR. Alternatively, pass `rangeStart` and `rangeEnd` as dates for a custom window. Use this to answer 'what content is performing best' and to pick assets for deeper drill-down via `getWorkspaceGroupChannelPagesPage`."
	)
	public AssetSummaryMetricPage workspaceGroupChannelAssetSummaries(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("rangeEnd") String rangeEnd,
			@GraphQLName("rangeKey") String rangeKey,
			@GraphQLName("rangeStart") String rangeStart,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_assetSummaryMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			assetSummaryMetricResource -> new AssetSummaryMetricPage(
				assetSummaryMetricResource.
					getWorkspaceGroupChannelAssetSummariesPage(
						groupId, channelId, rangeEnd, rangeKey, rangeStart,
						search, Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							assetSummaryMetricResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannel(channelId: ___, groupId: ___){id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Fetch a single channel (also known as property) from an Analytics Cloud workspace. To list available channels in the workspace use `getWorkspaceGroupChannelsPage`."
	)
	public Channel workspaceGroupChannel(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource -> channelResource.getWorkspaceGroupChannel(
				groupId, channelId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannels(groupId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List channels configured within an Analytics Cloud workspaces. To fetch a single channel by id, use `getWorkspaceGroupChannel`."
	)
	public ChannelPage workspaceGroupChannels(
			@GraphQLName("groupId") Long groupId)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource -> new ChannelPage(
				channelResource.getWorkspaceGroupChannelsPage(groupId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannelEvents(channelId: ___, groupId: ___, includeAnonymousUsers: ___, page: ___, pageSize: ___, rangeEnd: ___, rangeKey: ___, rangeStart: ___, search: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List tracked analytics events for a specific channel (also known as property), optionally narrowed to a date range. For date-range filtering pass `rangeKey` as one of LAST_24_HOURS, YESTERDAY, LAST_7_DAYS, LAST_28_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_YEAR. Alternatively, pass `rangeStart` and `rangeEnd` as dates for a custom window. For aggregated metrics across events, prefer `getWorkspaceGroupChannelAssetSummariesPage` or `getWorkspaceGroupChannelPagesPage`."
	)
	public EventPage workspaceGroupChannelEvents(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("includeAnonymousUsers") Boolean includeAnonymousUsers,
			@GraphQLName("rangeEnd") String rangeEnd,
			@GraphQLName("rangeKey") String rangeKey,
			@GraphQLName("rangeStart") String rangeStart,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_eventResourceComponentServiceObjects,
			this::_populateResourceContext,
			eventResource -> new EventPage(
				eventResource.getWorkspaceGroupChannelEventsPage(
					groupId, channelId, includeAnonymousUsers, rangeEnd,
					rangeKey, rangeStart, search,
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannelAccountEventMetric(accountId: ___, channelId: ___, groupId: ___, interval: ___, rangeEnd: ___, rangeKey: ___, rangeStart: ___, search: ___){totalEvents, totalSessions}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Event and session totals for all individuals associated with an account over a date range, each with the previous period's total, a trend percentage, and a time series bucketed by `interval`. Use this to answer 'has engagement dropped compared to last month' style questions: compare `value` to `previousValue`, or read the `histogram` with `interval=MONTH`. For date-range filtering pass `rangeKey` as one of LAST_24_HOURS, YESTERDAY, LAST_7_DAYS, LAST_28_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_YEAR. Alternatively, pass `rangeStart` and `rangeEnd` as dates for a custom window."
	)
	public EventMetric workspaceGroupChannelAccountEventMetric(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("accountId") String accountId,
			@GraphQLName("interval") String interval,
			@GraphQLName("rangeEnd") String rangeEnd,
			@GraphQLName("rangeKey") String rangeKey,
			@GraphQLName("rangeStart") String rangeStart,
			@GraphQLName("search") String search)
		throws Exception {

		return _applyComponentServiceObjects(
			_eventMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			eventMetricResource ->
				eventMetricResource.getWorkspaceGroupChannelAccountEventMetric(
					groupId, channelId, accountId, interval, rangeEnd, rangeKey,
					rangeStart, search));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannelIndividualEventMetric(channelId: ___, groupId: ___, individualId: ___, interval: ___, rangeEnd: ___, rangeKey: ___, rangeStart: ___, search: ___){totalEvents, totalSessions}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Event and session totals for a single individual over a date range, each with the previous period's total, a trend percentage, and a time series bucketed by `interval`. Use this to answer 'has engagement dropped compared to last month' style questions: compare `value` to `previousValue`, or read the `histogram` with `interval=MONTH`. For date-range filtering pass `rangeKey` as one of LAST_24_HOURS, YESTERDAY, LAST_7_DAYS, LAST_28_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_YEAR. Alternatively, pass `rangeStart` and `rangeEnd` as dates for a custom window."
	)
	public EventMetric workspaceGroupChannelIndividualEventMetric(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("individualId") String individualId,
			@GraphQLName("interval") String interval,
			@GraphQLName("rangeEnd") String rangeEnd,
			@GraphQLName("rangeKey") String rangeKey,
			@GraphQLName("rangeStart") String rangeStart,
			@GraphQLName("search") String search)
		throws Exception {

		return _applyComponentServiceObjects(
			_eventMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			eventMetricResource ->
				eventMetricResource.
					getWorkspaceGroupChannelIndividualEventMetric(
						groupId, channelId, individualId, interval, rangeEnd,
						rangeKey, rangeStart, search));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannelIndividuals(accountId: ___, activityStatus: ___, channelId: ___, groupId: ___, includeAnonymousUsers: ___, individualSegmentId: ___, interestName: ___, page: ___, pageSize: ___, rangeEnd: ___, rangeKey: ___, rangeStart: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List individuals for an Analytics Cloud workspace. Optionally narrowed to an associated account, segment, interest, activity status, or date range, and searchable by name or email with `search`. To fetch a single individual by id, use `getWorkspaceGroupIndividual` instead."
	)
	public IndividualPage workspaceGroupChannelIndividuals(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("accountId") String accountId,
			@GraphQLName("activityStatus") String activityStatus,
			@GraphQLName("includeAnonymousUsers") Boolean includeAnonymousUsers,
			@GraphQLName("individualSegmentId") String individualSegmentId,
			@GraphQLName("interestName") String interestName,
			@GraphQLName("rangeEnd") String rangeEnd,
			@GraphQLName("rangeKey") String rangeKey,
			@GraphQLName("rangeStart") String rangeStart,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_individualResourceComponentServiceObjects,
			this::_populateResourceContext,
			individualResource -> new IndividualPage(
				individualResource.getWorkspaceGroupChannelIndividualsPage(
					groupId, channelId, accountId, activityStatus,
					includeAnonymousUsers, individualSegmentId, interestName,
					rangeEnd, rangeKey, rangeStart, search,
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(individualResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupIndividual(channelId: ___, groupId: ___, individualId: ___){accountName, activitiesCount, activityStatus, averageSessionDuration, dateCreated, dateModified, demographics, emailAddress, firstActivityDate, id, knownSinceDate, lastActivityDate, lastSessionCountry, name, profileType, sessionsCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Fetch a single Individual from an Analytics Cloud workspace. Optionally narrowed to a channel (also known as property). Use this to fetch a contact's full profile once you have an id. To search individuals by name, email, or other attributes, use `getWorkspaceGroupChannelIndividualsPage`."
	)
	public Individual workspaceGroupIndividual(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("individualId") String individualId,
			@GraphQLName("channelId") String channelId)
		throws Exception {

		return _applyComponentServiceObjects(
			_individualResourceComponentServiceObjects,
			this::_populateResourceContext,
			individualResource ->
				individualResource.getWorkspaceGroupIndividual(
					groupId, individualId, channelId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannelIndividualSegments(channelId: ___, groupId: ___, name: ___, page: ___, pageSize: ___, search: ___, status: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List individual segments within an Analytics Cloud workspace. Optionally narrowed to a channel (also known as a property). To fetch a single segment by id, use `getWorkspaceGroupIndividualSegment`. To list members of a segment, use `getWorkspaceGroupIndividualSegmentMembershipsPage`."
	)
	public IndividualSegmentPage workspaceGroupChannelIndividualSegments(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("name") String name,
			@GraphQLName("search") String search,
			@GraphQLName("status") String status,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_individualSegmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			individualSegmentResource -> new IndividualSegmentPage(
				individualSegmentResource.
					getWorkspaceGroupChannelIndividualSegmentsPage(
						groupId, channelId, name, search, status,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupIndividualIndividualSegments(channelId: ___, groupId: ___, individualId: ___, page: ___, pageSize: ___, search: ___, status: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List the individual segments a single individual belongs to, or formerly belonged to. Use this to check whether a person is already in a nurture or intent segment before recommending an action. To list all segments in a channel, use `getWorkspaceGroupChannelIndividualSegmentsPage`."
	)
	public IndividualSegmentPage workspaceGroupIndividualIndividualSegments(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("individualId") String individualId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("search") String search,
			@GraphQLName("status") String status,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_individualSegmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			individualSegmentResource -> new IndividualSegmentPage(
				individualSegmentResource.
					getWorkspaceGroupIndividualIndividualSegmentsPage(
						groupId, individualId, channelId, search, status,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupIndividualSegment(groupId: ___, individualSegmentId: ___){activeIndividualCount, anonymousIndividualCount, channelId, dateCreated, dateModified, filter, id, includeAnonymousUsers, individualCount, knownIndividualCount, lastActivityDate, name, segmentType, state, status}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Fetch a single individual segment by id from an Analytics Cloud workspace. To list segments, use `getWorkspaceGroupChannelIndividualSegmentsPage`. To list members of a segment, use `getWorkspaceGroupIndividualSegmentMembershipsPage`."
	)
	public IndividualSegment workspaceGroupIndividualSegment(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("individualSegmentId") String individualSegmentId)
		throws Exception {

		return _applyComponentServiceObjects(
			_individualSegmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			individualSegmentResource ->
				individualSegmentResource.getWorkspaceGroupIndividualSegment(
					groupId, individualSegmentId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupIndividualSegmentMemberships(groupId: ___, individualSegmentId: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List individuals currently or formerly in an individual segment. Each entry links one individual to the individual segment and records when the individual entered (and, if applicable, exited). Use the resulting `individualId` values with `getWorkspaceGroupIndividual` to retrieve the full individual record."
	)
	public IndividualSegmentMembershipPage
			workspaceGroupIndividualSegmentMemberships(
				@GraphQLName("groupId") Long groupId,
				@GraphQLName("individualSegmentId") String individualSegmentId,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_individualSegmentMembershipResourceComponentServiceObjects,
			this::_populateResourceContext,
			individualSegmentMembershipResource ->
				new IndividualSegmentMembershipPage(
					individualSegmentMembershipResource.
						getWorkspaceGroupIndividualSegmentMembershipsPage(
							groupId, individualSegmentId,
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								individualSegmentMembershipResource,
								sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupIndividualSegmentMembershipChanges(groupId: ___, individualSegmentId: ___, operation: ___, page: ___, pageSize: ___, rangeEnd: ___, rangeKey: ___, rangeStart: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List the individuals who entered or left an individual segment over a date range, most recent change first, with each individual's name and email resolved. Use this to answer 'who has exited this segment recently'. For date-range filtering pass `rangeKey` as one of LAST_24_HOURS, YESTERDAY, LAST_7_DAYS, LAST_28_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_YEAR. Alternatively, pass `rangeStart` and `rangeEnd` as dates for a custom window. For the current membership list, use `getWorkspaceGroupIndividualSegmentMembershipsPage`."
	)
	public IndividualSegmentMembershipChangePage
			workspaceGroupIndividualSegmentMembershipChanges(
				@GraphQLName("groupId") Long groupId,
				@GraphQLName("individualSegmentId") String individualSegmentId,
				@GraphQLName("operation") String operation,
				@GraphQLName("rangeEnd") String rangeEnd,
				@GraphQLName("rangeKey") String rangeKey,
				@GraphQLName("rangeStart") String rangeStart,
				@GraphQLName("search") String search,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_individualSegmentMembershipChangeResourceComponentServiceObjects,
			this::_populateResourceContext,
			individualSegmentMembershipChangeResource ->
				new IndividualSegmentMembershipChangePage(
					individualSegmentMembershipChangeResource.
						getWorkspaceGroupIndividualSegmentMembershipChangesPage(
							groupId, individualSegmentId, operation, rangeEnd,
							rangeKey, rangeStart, search,
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								individualSegmentMembershipChangeResource,
								sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannelPages(channelId: ___, dataSourceId: ___, groupId: ___, page: ___, pageSize: ___, rangeEnd: ___, rangeKey: ___, rangeStart: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List analytics metrics for tracked pages on the workspace, ranked by views or another metric, optionally narrowed to a single channel (also known as property) or data source. Returns flattened view, visitor, bounce, exit, and access-path metrics for each page. For date-range filtering pass `rangeKey` as one of LAST_24_HOURS, YESTERDAY, LAST_7_DAYS, LAST_28_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_YEAR. Alternatively, pass `rangeStart` and `rangeEnd` as dates for a custom window. Use this for 'top pages' style queries."
	)
	public PageMetricPage workspaceGroupChannelPages(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("dataSourceId") String dataSourceId,
			@GraphQLName("rangeEnd") String rangeEnd,
			@GraphQLName("rangeKey") String rangeKey,
			@GraphQLName("rangeStart") String rangeStart,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageMetricResource -> new PageMetricPage(
				pageMetricResource.getWorkspaceGroupChannelPagesPage(
					groupId, channelId, dataSourceId, rangeEnd, rangeKey,
					rangeStart, search, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(pageMetricResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannelSearchTerms(channelId: ___, groupId: ___, page: ___, pageSize: ___, rangeEnd: ___, rangeKey: ___, rangeStart: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List search terms used on pages tracked by the Analytics Cloud workspace for a date range in a single channel (also known as property). Results are ordered in descending order of number of times terms have been searched. For date-range filtering pass `rangeKey` as one of LAST_24_HOURS, YESTERDAY, LAST_7_DAYS, LAST_28_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_YEAR. Alternatively, pass `rangeStart` and `rangeEnd` as dates for a custom window. Use this for 'most searched words' style queries."
	)
	public SearchTermPage workspaceGroupChannelSearchTerms(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("rangeEnd") String rangeEnd,
			@GraphQLName("rangeKey") String rangeKey,
			@GraphQLName("rangeStart") String rangeStart,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_searchTermResourceComponentServiceObjects,
			this::_populateResourceContext,
			searchTermResource -> new SearchTermPage(
				searchTermResource.getWorkspaceGroupChannelSearchTermsPage(
					groupId, channelId, rangeEnd, rangeKey, rangeStart,
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannelAccountUserSessions(accountId: ___, channelId: ___, groupId: ___, page: ___, pageSize: ___, rangeEnd: ___, rangeKey: ___, rangeStart: ___, search: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List the browsing sessions of the individuals associated with an account over a date range, most recent first, each with the events recorded during it. Use this to see what a specific account or person actually did recently. For date-range filtering pass `rangeKey` as one of LAST_24_HOURS, YESTERDAY, LAST_7_DAYS, LAST_28_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_YEAR. Alternatively, pass `rangeStart` and `rangeEnd` as dates for a custom window."
	)
	public UserSessionPage workspaceGroupChannelAccountUserSessions(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("accountId") String accountId,
			@GraphQLName("rangeEnd") String rangeEnd,
			@GraphQLName("rangeKey") String rangeKey,
			@GraphQLName("rangeStart") String rangeStart,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_userSessionResourceComponentServiceObjects,
			this::_populateResourceContext,
			userSessionResource -> new UserSessionPage(
				userSessionResource.
					getWorkspaceGroupChannelAccountUserSessionsPage(
						groupId, channelId, accountId, rangeEnd, rangeKey,
						rangeStart, search, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannelIndividualUserSessions(channelId: ___, groupId: ___, individualId: ___, page: ___, pageSize: ___, rangeEnd: ___, rangeKey: ___, rangeStart: ___, search: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List the browsing sessions of a single individual over a date range, most recent first, each with the events recorded during it. Use this to see what a specific account or person actually did recently. For date-range filtering pass `rangeKey` as one of LAST_24_HOURS, YESTERDAY, LAST_7_DAYS, LAST_28_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_YEAR. Alternatively, pass `rangeStart` and `rangeEnd` as dates for a custom window."
	)
	public UserSessionPage workspaceGroupChannelIndividualUserSessions(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("individualId") String individualId,
			@GraphQLName("rangeEnd") String rangeEnd,
			@GraphQLName("rangeKey") String rangeKey,
			@GraphQLName("rangeStart") String rangeStart,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_userSessionResourceComponentServiceObjects,
			this::_populateResourceContext,
			userSessionResource -> new UserSessionPage(
				userSessionResource.
					getWorkspaceGroupChannelIndividualUserSessionsPage(
						groupId, channelId, individualId, rangeEnd, rangeKey,
						rangeStart, search, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaces{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List Analytics Cloud workspaces accessible to the current user. Use the resulting `groupId` values with other endpoints (e.g. `getWorkspaceGroupChannelAccountsPage`, `getWorkspaceGroupChannelsPage`) to scope requests to a specific workspace."
	)
	public WorkspacePage workspaces() throws Exception {
		return _applyComponentServiceObjects(
			_workspaceResourceComponentServiceObjects,
			this::_populateResourceContext,
			workspaceResource -> new WorkspacePage(
				workspaceResource.getWorkspacesPage()));
	}

	@GraphQLName("AccountPage")
	public class AccountPage {

		public AccountPage(Page accountPage) {
			actions = accountPage.getActions();

			items = accountPage.getItems();
			lastPage = accountPage.getLastPage();
			page = accountPage.getPage();
			pageSize = accountPage.getPageSize();
			totalCount = accountPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Account> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("AssetSummaryMetricPage")
	public class AssetSummaryMetricPage {

		public AssetSummaryMetricPage(Page assetSummaryMetricPage) {
			actions = assetSummaryMetricPage.getActions();

			items = assetSummaryMetricPage.getItems();
			lastPage = assetSummaryMetricPage.getLastPage();
			page = assetSummaryMetricPage.getPage();
			pageSize = assetSummaryMetricPage.getPageSize();
			totalCount = assetSummaryMetricPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AssetSummaryMetric> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ChannelPage")
	public class ChannelPage {

		public ChannelPage(Page channelPage) {
			actions = channelPage.getActions();

			items = channelPage.getItems();
			lastPage = channelPage.getLastPage();
			page = channelPage.getPage();
			pageSize = channelPage.getPageSize();
			totalCount = channelPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Channel> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("EventPage")
	public class EventPage {

		public EventPage(Page eventPage) {
			actions = eventPage.getActions();

			items = eventPage.getItems();
			lastPage = eventPage.getLastPage();
			page = eventPage.getPage();
			pageSize = eventPage.getPageSize();
			totalCount = eventPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Event> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("EventMetricPage")
	public class EventMetricPage {

		public EventMetricPage(Page eventMetricPage) {
			actions = eventMetricPage.getActions();

			items = eventMetricPage.getItems();
			lastPage = eventMetricPage.getLastPage();
			page = eventMetricPage.getPage();
			pageSize = eventMetricPage.getPageSize();
			totalCount = eventMetricPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<EventMetric> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("IndividualPage")
	public class IndividualPage {

		public IndividualPage(Page individualPage) {
			actions = individualPage.getActions();

			items = individualPage.getItems();
			lastPage = individualPage.getLastPage();
			page = individualPage.getPage();
			pageSize = individualPage.getPageSize();
			totalCount = individualPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Individual> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("IndividualSegmentPage")
	public class IndividualSegmentPage {

		public IndividualSegmentPage(Page individualSegmentPage) {
			actions = individualSegmentPage.getActions();

			items = individualSegmentPage.getItems();
			lastPage = individualSegmentPage.getLastPage();
			page = individualSegmentPage.getPage();
			pageSize = individualSegmentPage.getPageSize();
			totalCount = individualSegmentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<IndividualSegment> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("IndividualSegmentMembershipPage")
	public class IndividualSegmentMembershipPage {

		public IndividualSegmentMembershipPage(
			Page individualSegmentMembershipPage) {

			actions = individualSegmentMembershipPage.getActions();

			items = individualSegmentMembershipPage.getItems();
			lastPage = individualSegmentMembershipPage.getLastPage();
			page = individualSegmentMembershipPage.getPage();
			pageSize = individualSegmentMembershipPage.getPageSize();
			totalCount = individualSegmentMembershipPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<IndividualSegmentMembership> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("IndividualSegmentMembershipChangePage")
	public class IndividualSegmentMembershipChangePage {

		public IndividualSegmentMembershipChangePage(
			Page individualSegmentMembershipChangePage) {

			actions = individualSegmentMembershipChangePage.getActions();

			items = individualSegmentMembershipChangePage.getItems();
			lastPage = individualSegmentMembershipChangePage.getLastPage();
			page = individualSegmentMembershipChangePage.getPage();
			pageSize = individualSegmentMembershipChangePage.getPageSize();
			totalCount = individualSegmentMembershipChangePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<IndividualSegmentMembershipChange> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PageMetricPage")
	public class PageMetricPage {

		public PageMetricPage(Page pageMetricPage) {
			actions = pageMetricPage.getActions();

			items = pageMetricPage.getItems();
			lastPage = pageMetricPage.getLastPage();
			page = pageMetricPage.getPage();
			pageSize = pageMetricPage.getPageSize();
			totalCount = pageMetricPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PageMetric> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SearchTermPage")
	public class SearchTermPage {

		public SearchTermPage(Page searchTermPage) {
			actions = searchTermPage.getActions();

			items = searchTermPage.getItems();
			lastPage = searchTermPage.getLastPage();
			page = searchTermPage.getPage();
			pageSize = searchTermPage.getPageSize();
			totalCount = searchTermPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<SearchTerm> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("UserSessionPage")
	public class UserSessionPage {

		public UserSessionPage(Page userSessionPage) {
			actions = userSessionPage.getActions();

			items = userSessionPage.getItems();
			lastPage = userSessionPage.getLastPage();
			page = userSessionPage.getPage();
			pageSize = userSessionPage.getPageSize();
			totalCount = userSessionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<UserSession> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("WorkspacePage")
	public class WorkspacePage {

		public WorkspacePage(Page workspacePage) {
			actions = workspacePage.getActions();

			items = workspacePage.getItems();
			lastPage = workspacePage.getLastPage();
			page = workspacePage.getPage();
			pageSize = workspacePage.getPageSize();
			totalCount = workspacePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Workspace> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(AccountResource accountResource)
		throws Exception {

		accountResource.setContextAcceptLanguage(_acceptLanguage);
		accountResource.setContextCompany(_company);
		accountResource.setContextHttpServletRequest(_httpServletRequest);
		accountResource.setContextHttpServletResponse(_httpServletResponse);
		accountResource.setContextUriInfo(_uriInfo);
		accountResource.setContextUser(_user);
		accountResource.setGroupLocalService(_groupLocalService);
		accountResource.setResourceActionLocalService(
			_resourceActionLocalService);
		accountResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		accountResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			AssetSummaryMetricResource assetSummaryMetricResource)
		throws Exception {

		assetSummaryMetricResource.setContextAcceptLanguage(_acceptLanguage);
		assetSummaryMetricResource.setContextCompany(_company);
		assetSummaryMetricResource.setContextHttpServletRequest(
			_httpServletRequest);
		assetSummaryMetricResource.setContextHttpServletResponse(
			_httpServletResponse);
		assetSummaryMetricResource.setContextUriInfo(_uriInfo);
		assetSummaryMetricResource.setContextUser(_user);
		assetSummaryMetricResource.setGroupLocalService(_groupLocalService);
		assetSummaryMetricResource.setResourceActionLocalService(
			_resourceActionLocalService);
		assetSummaryMetricResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		assetSummaryMetricResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(ChannelResource channelResource)
		throws Exception {

		channelResource.setContextAcceptLanguage(_acceptLanguage);
		channelResource.setContextCompany(_company);
		channelResource.setContextHttpServletRequest(_httpServletRequest);
		channelResource.setContextHttpServletResponse(_httpServletResponse);
		channelResource.setContextUriInfo(_uriInfo);
		channelResource.setContextUser(_user);
		channelResource.setGroupLocalService(_groupLocalService);
		channelResource.setResourceActionLocalService(
			_resourceActionLocalService);
		channelResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		channelResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(EventResource eventResource)
		throws Exception {

		eventResource.setContextAcceptLanguage(_acceptLanguage);
		eventResource.setContextCompany(_company);
		eventResource.setContextHttpServletRequest(_httpServletRequest);
		eventResource.setContextHttpServletResponse(_httpServletResponse);
		eventResource.setContextUriInfo(_uriInfo);
		eventResource.setContextUser(_user);
		eventResource.setGroupLocalService(_groupLocalService);
		eventResource.setResourceActionLocalService(
			_resourceActionLocalService);
		eventResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		eventResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			EventMetricResource eventMetricResource)
		throws Exception {

		eventMetricResource.setContextAcceptLanguage(_acceptLanguage);
		eventMetricResource.setContextCompany(_company);
		eventMetricResource.setContextHttpServletRequest(_httpServletRequest);
		eventMetricResource.setContextHttpServletResponse(_httpServletResponse);
		eventMetricResource.setContextUriInfo(_uriInfo);
		eventMetricResource.setContextUser(_user);
		eventMetricResource.setGroupLocalService(_groupLocalService);
		eventMetricResource.setResourceActionLocalService(
			_resourceActionLocalService);
		eventMetricResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		eventMetricResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(IndividualResource individualResource)
		throws Exception {

		individualResource.setContextAcceptLanguage(_acceptLanguage);
		individualResource.setContextCompany(_company);
		individualResource.setContextHttpServletRequest(_httpServletRequest);
		individualResource.setContextHttpServletResponse(_httpServletResponse);
		individualResource.setContextUriInfo(_uriInfo);
		individualResource.setContextUser(_user);
		individualResource.setGroupLocalService(_groupLocalService);
		individualResource.setResourceActionLocalService(
			_resourceActionLocalService);
		individualResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		individualResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			IndividualSegmentResource individualSegmentResource)
		throws Exception {

		individualSegmentResource.setContextAcceptLanguage(_acceptLanguage);
		individualSegmentResource.setContextCompany(_company);
		individualSegmentResource.setContextHttpServletRequest(
			_httpServletRequest);
		individualSegmentResource.setContextHttpServletResponse(
			_httpServletResponse);
		individualSegmentResource.setContextUriInfo(_uriInfo);
		individualSegmentResource.setContextUser(_user);
		individualSegmentResource.setGroupLocalService(_groupLocalService);
		individualSegmentResource.setResourceActionLocalService(
			_resourceActionLocalService);
		individualSegmentResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		individualSegmentResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			IndividualSegmentMembershipResource
				individualSegmentMembershipResource)
		throws Exception {

		individualSegmentMembershipResource.setContextAcceptLanguage(
			_acceptLanguage);
		individualSegmentMembershipResource.setContextCompany(_company);
		individualSegmentMembershipResource.setContextHttpServletRequest(
			_httpServletRequest);
		individualSegmentMembershipResource.setContextHttpServletResponse(
			_httpServletResponse);
		individualSegmentMembershipResource.setContextUriInfo(_uriInfo);
		individualSegmentMembershipResource.setContextUser(_user);
		individualSegmentMembershipResource.setGroupLocalService(
			_groupLocalService);
		individualSegmentMembershipResource.setResourceActionLocalService(
			_resourceActionLocalService);
		individualSegmentMembershipResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		individualSegmentMembershipResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			IndividualSegmentMembershipChangeResource
				individualSegmentMembershipChangeResource)
		throws Exception {

		individualSegmentMembershipChangeResource.setContextAcceptLanguage(
			_acceptLanguage);
		individualSegmentMembershipChangeResource.setContextCompany(_company);
		individualSegmentMembershipChangeResource.setContextHttpServletRequest(
			_httpServletRequest);
		individualSegmentMembershipChangeResource.setContextHttpServletResponse(
			_httpServletResponse);
		individualSegmentMembershipChangeResource.setContextUriInfo(_uriInfo);
		individualSegmentMembershipChangeResource.setContextUser(_user);
		individualSegmentMembershipChangeResource.setGroupLocalService(
			_groupLocalService);
		individualSegmentMembershipChangeResource.setResourceActionLocalService(
			_resourceActionLocalService);
		individualSegmentMembershipChangeResource.
			setResourcePermissionLocalService(_resourcePermissionLocalService);
		individualSegmentMembershipChangeResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(PageMetricResource pageMetricResource)
		throws Exception {

		pageMetricResource.setContextAcceptLanguage(_acceptLanguage);
		pageMetricResource.setContextCompany(_company);
		pageMetricResource.setContextHttpServletRequest(_httpServletRequest);
		pageMetricResource.setContextHttpServletResponse(_httpServletResponse);
		pageMetricResource.setContextUriInfo(_uriInfo);
		pageMetricResource.setContextUser(_user);
		pageMetricResource.setGroupLocalService(_groupLocalService);
		pageMetricResource.setResourceActionLocalService(
			_resourceActionLocalService);
		pageMetricResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		pageMetricResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(SearchTermResource searchTermResource)
		throws Exception {

		searchTermResource.setContextAcceptLanguage(_acceptLanguage);
		searchTermResource.setContextCompany(_company);
		searchTermResource.setContextHttpServletRequest(_httpServletRequest);
		searchTermResource.setContextHttpServletResponse(_httpServletResponse);
		searchTermResource.setContextUriInfo(_uriInfo);
		searchTermResource.setContextUser(_user);
		searchTermResource.setGroupLocalService(_groupLocalService);
		searchTermResource.setResourceActionLocalService(
			_resourceActionLocalService);
		searchTermResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		searchTermResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			UserSessionResource userSessionResource)
		throws Exception {

		userSessionResource.setContextAcceptLanguage(_acceptLanguage);
		userSessionResource.setContextCompany(_company);
		userSessionResource.setContextHttpServletRequest(_httpServletRequest);
		userSessionResource.setContextHttpServletResponse(_httpServletResponse);
		userSessionResource.setContextUriInfo(_uriInfo);
		userSessionResource.setContextUser(_user);
		userSessionResource.setGroupLocalService(_groupLocalService);
		userSessionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		userSessionResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		userSessionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(WorkspaceResource workspaceResource)
		throws Exception {

		workspaceResource.setContextAcceptLanguage(_acceptLanguage);
		workspaceResource.setContextCompany(_company);
		workspaceResource.setContextHttpServletRequest(_httpServletRequest);
		workspaceResource.setContextHttpServletResponse(_httpServletResponse);
		workspaceResource.setContextUriInfo(_uriInfo);
		workspaceResource.setContextUser(_user);
		workspaceResource.setGroupLocalService(_groupLocalService);
		workspaceResource.setResourceActionLocalService(
			_resourceActionLocalService);
		workspaceResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		workspaceResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<AccountResource>
		_accountResourceComponentServiceObjects;
	private static ComponentServiceObjects<AssetSummaryMetricResource>
		_assetSummaryMetricResourceComponentServiceObjects;
	private static ComponentServiceObjects<ChannelResource>
		_channelResourceComponentServiceObjects;
	private static ComponentServiceObjects<EventResource>
		_eventResourceComponentServiceObjects;
	private static ComponentServiceObjects<EventMetricResource>
		_eventMetricResourceComponentServiceObjects;
	private static ComponentServiceObjects<IndividualResource>
		_individualResourceComponentServiceObjects;
	private static ComponentServiceObjects<IndividualSegmentResource>
		_individualSegmentResourceComponentServiceObjects;
	private static ComponentServiceObjects<IndividualSegmentMembershipResource>
		_individualSegmentMembershipResourceComponentServiceObjects;
	private static ComponentServiceObjects
		<IndividualSegmentMembershipChangeResource>
			_individualSegmentMembershipChangeResourceComponentServiceObjects;
	private static ComponentServiceObjects<PageMetricResource>
		_pageMetricResourceComponentServiceObjects;
	private static ComponentServiceObjects<SearchTermResource>
		_searchTermResourceComponentServiceObjects;
	private static ComponentServiceObjects<UserSessionResource>
		_userSessionResourceComponentServiceObjects;
	private static ComponentServiceObjects<WorkspaceResource>
		_workspaceResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private ResourceActionLocalService _resourceActionLocalService;
	private ResourcePermissionLocalService _resourcePermissionLocalService;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}
// LIFERAY-REST-BUILDER-HASH:-2076273549