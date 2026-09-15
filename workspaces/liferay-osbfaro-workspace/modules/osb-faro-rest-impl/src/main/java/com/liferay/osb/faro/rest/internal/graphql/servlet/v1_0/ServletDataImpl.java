/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.graphql.servlet.v1_0;

import com.liferay.osb.faro.rest.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.osb.faro.rest.internal.graphql.query.v1_0.Query;
import com.liferay.osb.faro.rest.internal.resource.v1_0.AccountResourceImpl;
import com.liferay.osb.faro.rest.internal.resource.v1_0.AssetSummaryMetricResourceImpl;
import com.liferay.osb.faro.rest.internal.resource.v1_0.ChannelResourceImpl;
import com.liferay.osb.faro.rest.internal.resource.v1_0.EventMetricResourceImpl;
import com.liferay.osb.faro.rest.internal.resource.v1_0.EventResourceImpl;
import com.liferay.osb.faro.rest.internal.resource.v1_0.IndividualResourceImpl;
import com.liferay.osb.faro.rest.internal.resource.v1_0.IndividualSegmentMembershipChangeResourceImpl;
import com.liferay.osb.faro.rest.internal.resource.v1_0.IndividualSegmentMembershipResourceImpl;
import com.liferay.osb.faro.rest.internal.resource.v1_0.IndividualSegmentResourceImpl;
import com.liferay.osb.faro.rest.internal.resource.v1_0.PageMetricResourceImpl;
import com.liferay.osb.faro.rest.internal.resource.v1_0.SearchTermResourceImpl;
import com.liferay.osb.faro.rest.internal.resource.v1_0.UserSessionResourceImpl;
import com.liferay.osb.faro.rest.internal.resource.v1_0.WorkspaceResourceImpl;
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
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Leslie Wong
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Query.setAccountResourceComponentServiceObjects(
			_accountResourceComponentServiceObjects);
		Query.setAssetSummaryMetricResourceComponentServiceObjects(
			_assetSummaryMetricResourceComponentServiceObjects);
		Query.setChannelResourceComponentServiceObjects(
			_channelResourceComponentServiceObjects);
		Query.setEventResourceComponentServiceObjects(
			_eventResourceComponentServiceObjects);
		Query.setEventMetricResourceComponentServiceObjects(
			_eventMetricResourceComponentServiceObjects);
		Query.setIndividualResourceComponentServiceObjects(
			_individualResourceComponentServiceObjects);
		Query.setIndividualSegmentResourceComponentServiceObjects(
			_individualSegmentResourceComponentServiceObjects);
		Query.setIndividualSegmentMembershipResourceComponentServiceObjects(
			_individualSegmentMembershipResourceComponentServiceObjects);
		Query.
			setIndividualSegmentMembershipChangeResourceComponentServiceObjects(
				_individualSegmentMembershipChangeResourceComponentServiceObjects);
		Query.setPageMetricResourceComponentServiceObjects(
			_pageMetricResourceComponentServiceObjects);
		Query.setSearchTermResourceComponentServiceObjects(
			_searchTermResourceComponentServiceObjects);
		Query.setUserSessionResourceComponentServiceObjects(
			_userSessionResourceComponentServiceObjects);
		Query.setWorkspaceResourceComponentServiceObjects(
			_workspaceResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Faro.Rest";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/faro-rest-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"query#workspaceGroupAccount",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"getWorkspaceGroupAccount"));
					put(
						"query#workspaceGroupChannelAccounts",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"getWorkspaceGroupChannelAccountsPage"));
					put(
						"query#workspaceGroupChannelAssetSummaries",
						new ObjectValuePair<>(
							AssetSummaryMetricResourceImpl.class,
							"getWorkspaceGroupChannelAssetSummariesPage"));
					put(
						"query#workspaceGroupChannel",
						new ObjectValuePair<>(
							ChannelResourceImpl.class,
							"getWorkspaceGroupChannel"));
					put(
						"query#workspaceGroupChannels",
						new ObjectValuePair<>(
							ChannelResourceImpl.class,
							"getWorkspaceGroupChannelsPage"));
					put(
						"query#workspaceGroupChannelEvents",
						new ObjectValuePair<>(
							EventResourceImpl.class,
							"getWorkspaceGroupChannelEventsPage"));
					put(
						"query#workspaceGroupChannelAccountEventMetric",
						new ObjectValuePair<>(
							EventMetricResourceImpl.class,
							"getWorkspaceGroupChannelAccountEventMetric"));
					put(
						"query#workspaceGroupChannelIndividualEventMetric",
						new ObjectValuePair<>(
							EventMetricResourceImpl.class,
							"getWorkspaceGroupChannelIndividualEventMetric"));
					put(
						"query#workspaceGroupChannelIndividuals",
						new ObjectValuePair<>(
							IndividualResourceImpl.class,
							"getWorkspaceGroupChannelIndividualsPage"));
					put(
						"query#workspaceGroupIndividual",
						new ObjectValuePair<>(
							IndividualResourceImpl.class,
							"getWorkspaceGroupIndividual"));
					put(
						"query#workspaceGroupChannelIndividualSegments",
						new ObjectValuePair<>(
							IndividualSegmentResourceImpl.class,
							"getWorkspaceGroupChannelIndividualSegmentsPage"));
					put(
						"query#workspaceGroupIndividualIndividualSegments",
						new ObjectValuePair<>(
							IndividualSegmentResourceImpl.class,
							"getWorkspaceGroupIndividualIndividualSegmentsPage"));
					put(
						"query#workspaceGroupIndividualSegment",
						new ObjectValuePair<>(
							IndividualSegmentResourceImpl.class,
							"getWorkspaceGroupIndividualSegment"));
					put(
						"query#workspaceGroupIndividualSegmentMemberships",
						new ObjectValuePair<>(
							IndividualSegmentMembershipResourceImpl.class,
							"getWorkspaceGroupIndividualSegmentMembershipsPage"));
					put(
						"query#workspaceGroupIndividualSegmentMembershipChanges",
						new ObjectValuePair<>(
							IndividualSegmentMembershipChangeResourceImpl.class,
							"getWorkspaceGroupIndividualSegmentMembershipChangesPage"));
					put(
						"query#workspaceGroupChannelPages",
						new ObjectValuePair<>(
							PageMetricResourceImpl.class,
							"getWorkspaceGroupChannelPagesPage"));
					put(
						"query#workspaceGroupChannelSearchTerms",
						new ObjectValuePair<>(
							SearchTermResourceImpl.class,
							"getWorkspaceGroupChannelSearchTermsPage"));
					put(
						"query#workspaceGroupChannelAccountUserSessions",
						new ObjectValuePair<>(
							UserSessionResourceImpl.class,
							"getWorkspaceGroupChannelAccountUserSessionsPage"));
					put(
						"query#workspaceGroupChannelIndividualUserSessions",
						new ObjectValuePair<>(
							UserSessionResourceImpl.class,
							"getWorkspaceGroupChannelIndividualUserSessionsPage"));
					put(
						"query#workspaces",
						new ObjectValuePair<>(
							WorkspaceResourceImpl.class, "getWorkspacesPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AccountResource>
		_accountResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AssetSummaryMetricResource>
		_assetSummaryMetricResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ChannelResource>
		_channelResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<EventResource>
		_eventResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<EventMetricResource>
		_eventMetricResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<IndividualResource>
		_individualResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<IndividualSegmentResource>
		_individualSegmentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<IndividualSegmentMembershipResource>
		_individualSegmentMembershipResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<IndividualSegmentMembershipChangeResource>
		_individualSegmentMembershipChangeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PageMetricResource>
		_pageMetricResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<SearchTermResource>
		_searchTermResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<UserSessionResource>
		_userSessionResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WorkspaceResource>
		_workspaceResourceComponentServiceObjects;

}
// LIFERAY-REST-BUILDER-HASH:-886469587