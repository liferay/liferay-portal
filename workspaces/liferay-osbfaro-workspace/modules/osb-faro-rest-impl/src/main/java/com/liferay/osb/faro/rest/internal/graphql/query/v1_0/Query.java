/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.graphql.query.v1_0;

import com.liferay.osb.faro.rest.dto.v1_0.Account;
import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStageTransition;
import com.liferay.osb.faro.rest.dto.v1_0.AssetSummaryMetric;
import com.liferay.osb.faro.rest.dto.v1_0.Channel;
import com.liferay.osb.faro.rest.dto.v1_0.Event;
import com.liferay.osb.faro.rest.dto.v1_0.Individual;
import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegment;
import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembership;
import com.liferay.osb.faro.rest.dto.v1_0.PageMetric;
import com.liferay.osb.faro.rest.dto.v1_0.SearchTerm;
import com.liferay.osb.faro.rest.dto.v1_0.Workspace;
import com.liferay.osb.faro.rest.resource.v1_0.AccountLifecycleStageTransitionResource;
import com.liferay.osb.faro.rest.resource.v1_0.AccountResource;
import com.liferay.osb.faro.rest.resource.v1_0.AssetSummaryMetricResource;
import com.liferay.osb.faro.rest.resource.v1_0.ChannelResource;
import com.liferay.osb.faro.rest.resource.v1_0.EventResource;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualResource;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualSegmentMembershipResource;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualSegmentResource;
import com.liferay.osb.faro.rest.resource.v1_0.PageMetricResource;
import com.liferay.osb.faro.rest.resource.v1_0.SearchTermResource;
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

	public static void
		setAccountLifecycleStageTransitionResourceComponentServiceObjects(
			ComponentServiceObjects<AccountLifecycleStageTransitionResource>
				accountLifecycleStageTransitionResourceComponentServiceObjects) {

		_accountLifecycleStageTransitionResourceComponentServiceObjects =
			accountLifecycleStageTransitionResourceComponentServiceObjects;
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

	public static void setWorkspaceResourceComponentServiceObjects(
		ComponentServiceObjects<WorkspaceResource>
			workspaceResourceComponentServiceObjects) {

		_workspaceResourceComponentServiceObjects =
			workspaceResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupAccount(accountId: ___, groupId: ___){accountName, annualRevenue, country, dateModified, id, industry, lastActivityDate, lifecycleStage}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Fetch a single account by ID from an Analytics Cloud Workspace. Use this when you already have an account ID. To search accounts by name or filter, use `getWorkspaceGroupChannelAccountsPage`."
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannelAccounts(channelId: ___, groupId: ___, lifecycleStage: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List or search accounts synced to an Analytics Cloud workspace. Optionally narrow results to a single channel (also known as property). Optionally narrow results to a single lifecycle stage with `lifecycleStage`, passing one of AT_RISK, AWARE, ENGAGED, ESTABLISHED, ONBOARDING, PIPELINE. Use this to browse or search accounts by name. To fetch a single account by ID, use `getAccount`."
	)
	public AccountPage workspaceGroupChannelAccounts(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("lifecycleStage") String lifecycleStage,
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
					groupId, channelId, lifecycleStage, search,
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(accountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupAccountLifecycleStageTransitions(accountLifecycleId: ___, country: ___, fromLifecycleStage: ___, groupId: ___, industry: ___, page: ___, pageSize: ___, rangeEnd: ___, rangeKey: ___, rangeStart: ___, segmentId: ___, sorts: ___, toLifecycleStage: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List the accounts that moved from one account lifecycle stage to another within a date range, one entry per move and most recent first. Each entry names the account, the stage it left, the stage it entered, and when the move happened. Filter by origin stage (`fromLifecycleStage`), destination stage (`toLifecycleStage`), account country or industry, or segment membership. Lifecycle stage values are matched case-insensitively against the stage type (e.g. PIPELINE or 'at risk') or the stage description. For date-range filtering pass `rangeKey` as one of LAST_24_HOURS, YESTERDAY, LAST_7_DAYS, LAST_28_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_YEAR; it defaults to LAST_90_DAYS, roughly the last quarter. Alternatively, pass `rangeStart` and `rangeEnd` as dates for a custom window."
	)
	public AccountLifecycleStageTransitionPage
			workspaceGroupAccountLifecycleStageTransitions(
				@GraphQLName("groupId") Long groupId,
				@GraphQLName("accountLifecycleId") String accountLifecycleId,
				@GraphQLName("country") String country,
				@GraphQLName("fromLifecycleStage") String fromLifecycleStage,
				@GraphQLName("industry") String industry,
				@GraphQLName("rangeEnd") String rangeEnd,
				@GraphQLName("rangeKey") String rangeKey,
				@GraphQLName("rangeStart") String rangeStart,
				@GraphQLName("segmentId") Long segmentId,
				@GraphQLName("toLifecycleStage") String toLifecycleStage,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountLifecycleStageTransitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountLifecycleStageTransitionResource ->
				new AccountLifecycleStageTransitionPage(
					accountLifecycleStageTransitionResource.
						getWorkspaceGroupAccountLifecycleStageTransitionsPage(
							groupId, accountLifecycleId, country,
							fromLifecycleStage, industry, rangeEnd, rangeKey,
							rangeStart, segmentId, toLifecycleStage,
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								accountLifecycleStageTransitionResource,
								sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannelAssetSummaries(accountId: ___, channelId: ___, groupId: ___, individualId: ___, page: ___, pageSize: ___, rangeEnd: ___, rangeKey: ___, rangeStart: ___, search: ___, segmentId: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List analytics asset summaries for pages, blogs, documents, forms, journal articles, and object entries. Rank summaries by the requested sort metric. Each summary includes download, impression, read, and view counts along with their period-over-period trend percentages. Optionally narrow results to a single channel (also known as property) or to a date range. Optionally narrow results further with `accountId`, `individualId`, or `segmentId` to report only on the activity of the individuals in that account, that individual, or the individuals in that segment. For date-range filtering pass `rangeKey` as one of LAST_24_HOURS, YESTERDAY, LAST_7_DAYS, LAST_28_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_YEAR. Alternatively, pass `rangeStart` and `rangeEnd` as dates for a custom window. Use this to answer 'what content is performing best' and to pick assets for deeper drill-down via `getWorkspaceGroupChannelPagesPage`."
	)
	public AssetSummaryMetricPage workspaceGroupChannelAssetSummaries(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("accountId") String accountId,
			@GraphQLName("individualId") String individualId,
			@GraphQLName("rangeEnd") String rangeEnd,
			@GraphQLName("rangeKey") String rangeKey,
			@GraphQLName("rangeStart") String rangeStart,
			@GraphQLName("search") String search,
			@GraphQLName("segmentId") String segmentId,
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
						groupId, channelId, accountId, individualId, rangeEnd,
						rangeKey, rangeStart, search, segmentId,
						Pagination.of(page, pageSize),
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
		description = "List channels configured within an Analytics Cloud workspaces. To fetch a single channel by ID, use `getWorkspaceGroupChannel`."
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannelEvents(accountId: ___, channelId: ___, groupId: ___, includeAnonymousUsers: ___, individualId: ___, page: ___, pageSize: ___, rangeEnd: ___, rangeKey: ___, rangeStart: ___, search: ___, segmentId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List tracked analytics events for a specific channel (also known as property), optionally narrowed to a date range. Optionally narrow results further with `accountId`, `individualId`, or `segmentId` to list only the events from the individuals in that account, from that individual, or from the individuals in that segment. For date-range filtering pass `rangeKey` as one of LAST_24_HOURS, YESTERDAY, LAST_7_DAYS, LAST_28_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_YEAR. Alternatively, pass `rangeStart` and `rangeEnd` as dates for a custom window. For aggregated metrics across events, prefer `getWorkspaceGroupChannelAssetSummariesPage` or `getWorkspaceGroupChannelPagesPage`."
	)
	public EventPage workspaceGroupChannelEvents(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("accountId") String accountId,
			@GraphQLName("includeAnonymousUsers") Boolean includeAnonymousUsers,
			@GraphQLName("individualId") String individualId,
			@GraphQLName("rangeEnd") String rangeEnd,
			@GraphQLName("rangeKey") String rangeKey,
			@GraphQLName("rangeStart") String rangeStart,
			@GraphQLName("search") String search,
			@GraphQLName("segmentId") String segmentId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_eventResourceComponentServiceObjects,
			this::_populateResourceContext,
			eventResource -> new EventPage(
				eventResource.getWorkspaceGroupChannelEventsPage(
					groupId, channelId, accountId, includeAnonymousUsers,
					individualId, rangeEnd, rangeKey, rangeStart, search,
					segmentId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannelIndividuals(accountId: ___, channelId: ___, groupId: ___, includeAnonymousUsers: ___, individualSegmentId: ___, interestName: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List individuals for an Analytics Cloud workspace. Optionally narrowed to an associated account, channel, segment, or interest. Use this for queries to find a person. To fetch a single individual by ID, use `getWorkspaceGroupIndividual` instead."
	)
	public IndividualPage workspaceGroupChannelIndividuals(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("accountId") String accountId,
			@GraphQLName("includeAnonymousUsers") Boolean includeAnonymousUsers,
			@GraphQLName("individualSegmentId") String individualSegmentId,
			@GraphQLName("interestName") String interestName,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_individualResourceComponentServiceObjects,
			this::_populateResourceContext,
			individualResource -> new IndividualPage(
				individualResource.getWorkspaceGroupChannelIndividualsPage(
					groupId, channelId, accountId, includeAnonymousUsers,
					individualSegmentId, interestName,
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(individualResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupIndividual(channelId: ___, groupId: ___, individualId: ___){accountName, activitiesCount, dateCreated, dateModified, demographics, firstActivityDate, id, lastActivityDate, lastSessionCountry, profileType}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Fetch a single Individual from an Analytics Cloud workspace. Optionally narrowed to a channel (also known as property). Use this to fetch a contact's full profile once you have an ID. To search individuals by name, email, or other attributes, use `getWorkspaceGroupChannelIndividualsPage`."
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
		description = "List individual segments within an Analytics Cloud workspace. Optionally narrowed to a channel (also known as a property). To fetch a single segment by ID, use `getWorkspaceGroupIndividualSegment`. To list members of a segment, use `getWorkspaceGroupIndividualSegmentMembershipsPage`."
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupIndividualSegment(groupId: ___, individualSegmentId: ___){activeIndividualCount, anonymousIndividualCount, channelId, dateCreated, dateModified, filter, id, includeAnonymousUsers, individualCount, knownIndividualCount, lastActivityDate, name, segmentType, state, status}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Fetch a single individual segment by ID from an Analytics Cloud workspace. To list segments, use `getWorkspaceGroupChannelIndividualSegmentsPage`. To list members of a segment, use `getWorkspaceGroupIndividualSegmentMembershipsPage`."
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workspaceGroupChannelPages(accountId: ___, channelId: ___, dataSourceId: ___, groupId: ___, individualId: ___, page: ___, pageSize: ___, rangeEnd: ___, rangeKey: ___, rangeStart: ___, search: ___, segmentId: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "List analytics metrics for tracked pages on the workspace, ranked by views or another metric, optionally narrowed to a single channel (also known as property) or data source. Returns flattened view, visitor, bounce, exit, and access-path metrics for each page. Optionally narrow results further with `accountId`, `individualId`, or `segmentId` to report only on the activity of the individuals in that account, that individual, or the individuals in that segment. For date-range filtering pass `rangeKey` as one of LAST_24_HOURS, YESTERDAY, LAST_7_DAYS, LAST_28_DAYS, LAST_30_DAYS, LAST_90_DAYS, LAST_180_DAYS, LAST_YEAR. Alternatively, pass `rangeStart` and `rangeEnd` as dates for a custom window. Use this for 'top pages' style queries."
	)
	public PageMetricPage workspaceGroupChannelPages(
			@GraphQLName("groupId") Long groupId,
			@GraphQLName("channelId") String channelId,
			@GraphQLName("accountId") String accountId,
			@GraphQLName("dataSourceId") String dataSourceId,
			@GraphQLName("individualId") String individualId,
			@GraphQLName("rangeEnd") String rangeEnd,
			@GraphQLName("rangeKey") String rangeKey,
			@GraphQLName("rangeStart") String rangeStart,
			@GraphQLName("search") String search,
			@GraphQLName("segmentId") String segmentId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageMetricResource -> new PageMetricPage(
				pageMetricResource.getWorkspaceGroupChannelPagesPage(
					groupId, channelId, accountId, dataSourceId, individualId,
					rangeEnd, rangeKey, rangeStart, search, segmentId,
					Pagination.of(page, pageSize),
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

	@GraphQLName("AccountLifecycleStageTransitionPage")
	public class AccountLifecycleStageTransitionPage {

		public AccountLifecycleStageTransitionPage(
			Page accountLifecycleStageTransitionPage) {

			actions = accountLifecycleStageTransitionPage.getActions();

			items = accountLifecycleStageTransitionPage.getItems();
			lastPage = accountLifecycleStageTransitionPage.getLastPage();
			page = accountLifecycleStageTransitionPage.getPage();
			pageSize = accountLifecycleStageTransitionPage.getPageSize();
			totalCount = accountLifecycleStageTransitionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AccountLifecycleStageTransition> items;

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
			AccountLifecycleStageTransitionResource
				accountLifecycleStageTransitionResource)
		throws Exception {

		accountLifecycleStageTransitionResource.setContextAcceptLanguage(
			_acceptLanguage);
		accountLifecycleStageTransitionResource.setContextCompany(_company);
		accountLifecycleStageTransitionResource.setContextHttpServletRequest(
			_httpServletRequest);
		accountLifecycleStageTransitionResource.setContextHttpServletResponse(
			_httpServletResponse);
		accountLifecycleStageTransitionResource.setContextUriInfo(_uriInfo);
		accountLifecycleStageTransitionResource.setContextUser(_user);
		accountLifecycleStageTransitionResource.setGroupLocalService(
			_groupLocalService);
		accountLifecycleStageTransitionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		accountLifecycleStageTransitionResource.
			setResourcePermissionLocalService(_resourcePermissionLocalService);
		accountLifecycleStageTransitionResource.setRoleLocalService(
			_roleLocalService);
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
	private static ComponentServiceObjects
		<AccountLifecycleStageTransitionResource>
			_accountLifecycleStageTransitionResourceComponentServiceObjects;
	private static ComponentServiceObjects<AssetSummaryMetricResource>
		_assetSummaryMetricResourceComponentServiceObjects;
	private static ComponentServiceObjects<ChannelResource>
		_channelResourceComponentServiceObjects;
	private static ComponentServiceObjects<EventResource>
		_eventResourceComponentServiceObjects;
	private static ComponentServiceObjects<IndividualResource>
		_individualResourceComponentServiceObjects;
	private static ComponentServiceObjects<IndividualSegmentResource>
		_individualSegmentResourceComponentServiceObjects;
	private static ComponentServiceObjects<IndividualSegmentMembershipResource>
		_individualSegmentMembershipResourceComponentServiceObjects;
	private static ComponentServiceObjects<PageMetricResource>
		_pageMetricResourceComponentServiceObjects;
	private static ComponentServiceObjects<SearchTermResource>
		_searchTermResourceComponentServiceObjects;
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
// LIFERAY-REST-BUILDER-HASH:1638010399