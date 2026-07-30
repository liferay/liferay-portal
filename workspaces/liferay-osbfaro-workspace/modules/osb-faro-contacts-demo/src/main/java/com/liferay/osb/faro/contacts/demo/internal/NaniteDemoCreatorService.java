/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.demo.internal;

import com.liferay.osb.faro.contacts.demo.internal.data.creator.AnalyticEventsDataCreator;
import com.liferay.osb.faro.contacts.demo.internal.data.creator.LiferayAssociationsDataCreator;
import com.liferay.osb.faro.contacts.demo.internal.data.creator.LiferayExperimentsDataCreator;
import com.liferay.osb.faro.contacts.demo.internal.data.creator.LiferayGroupsDataCreator;
import com.liferay.osb.faro.contacts.demo.internal.data.creator.LiferayOrganizationsDataCreator;
import com.liferay.osb.faro.contacts.demo.internal.data.creator.LiferayRolesDataCreator;
import com.liferay.osb.faro.contacts.demo.internal.data.creator.LiferayTeamsDataCreator;
import com.liferay.osb.faro.contacts.demo.internal.data.creator.LiferayUserGroupsDataCreator;
import com.liferay.osb.faro.contacts.demo.internal.data.creator.LiferayUsersDataCreator;
import com.liferay.osb.faro.contacts.demo.internal.data.creator.MembershipChangesDataCreator;
import com.liferay.osb.faro.contacts.demo.internal.data.creator.PageContextsDataCreator;
import com.liferay.osb.faro.contacts.demo.internal.data.creator.SalesforceAccountsDataCreator;
import com.liferay.osb.faro.contacts.demo.internal.data.creator.SalesforceIndividualsDataCreator;
import com.liferay.osb.faro.engine.client.model.Author;
import com.liferay.osb.faro.engine.client.model.Channel;
import com.liferay.osb.faro.engine.client.model.ChannelsConfiguration;
import com.liferay.osb.faro.engine.client.model.Credentials;
import com.liferay.osb.faro.engine.client.model.DataSource;
import com.liferay.osb.faro.engine.client.model.IndividualSegment;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembershipChange;
import com.liferay.osb.faro.engine.client.model.Provider;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.model.credentials.OAuth2Credentials;
import com.liferay.osb.faro.engine.client.model.credentials.TokenCredentials;
import com.liferay.osb.faro.engine.client.model.provider.LiferayProvider;
import com.liferay.osb.faro.engine.client.model.provider.SalesforceProvider;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.util.DateUtil;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.osb.faro.util.FaroThreadLocal;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 * @author Matthew Kong
 */
@Component(service = NaniteDemoCreatorService.class)
public class NaniteDemoCreatorService extends DemoCreatorService {

	@Override
	public void createData() throws Exception {
		LiferayUsersDataCreator liferayUsersDataCreator = createLiferayData();

		createSalesforceData(liferayUsersDataCreator);

		String channelId = getChannelId();

		createIndividualSegments(channelId);

		contactsEngineClient.addNanite(
			faroProject, "DXPEntitiesNanite", Collections.emptyMap());

		PageContextsDataCreator pageContextsDataCreator =
			new PageContextsDataCreator();

		pageContextsDataCreator.create(10, true);

		createLiferayExperiments(
			liferayUsersDataCreator.getDataSourceId(), pageContextsDataCreator);

		AnalyticEventsDataCreator analyticEventsDataCreator =
			new AnalyticEventsDataCreator(
				contactsEngineClient, faroProject, pageContextsDataCreator);

		createAnalyticEvents(
			analyticEventsDataCreator, liferayUsersDataCreator);

		createIdentities(liferayUsersDataCreator);

		FaroThreadLocal.setCacheEnabled(false);

		poll(
			() -> contactsEngineClient.getIndividuals(
				faroProject, null, null, null, channelId, null, null, null,
				false, null, null, null, null, null, null, null, null, 1, 0,
				null),
			_LIFERAY_INDIVIDUALS_COUNT,
			_LIFERAY_INDIVIDUALS_COUNT * 2 * Time.SECOND, "individuals");

		createMembershipChanges(channelId, _individualSegments.size());

		createMembershipChanges(null, _SALESFORCE_ACCOUNTS_COUNT);

		createLiferayAssociations(channelId, liferayUsersDataCreator);

		curateInterests();
	}

	protected void createAnalyticEvents(
		AnalyticEventsDataCreator analyticEventsDataCreator,
		LiferayUsersDataCreator liferayUsersDataCreator) {

		for (Map<String, Object> dxpEntity :
				liferayUsersDataCreator.getObjects()) {

			Map<String, Object> liferayUserMap =
				(Map<String, Object>)dxpEntity.get("objectJSONObject");

			analyticEventsDataCreator.createRandom(
				_LIFERAY_ANALYTIC_EVENTS_MAX_COUNT_PER_USER, false,
				new Object[] {
					liferayUsersDataCreator.getDataSourceId(),
					liferayUserMap.get("uuid")
				});
		}

		analyticEventsDataCreator.createRandom(
			_LIFERAY_ANONYMOUS_EVENTS_COUNT, false,
			new Object[] {liferayUsersDataCreator.getDataSourceId(), null});

		analyticEventsDataCreator.execute();
	}

	protected DataSource createDataSource(
		FaroProject faroProject, Credentials credentials, Provider provider,
		String name, String url) {

		Results<DataSource> results = contactsEngineClient.getDataSources(
			faroProject, null, null, name, null, null, 1, 1, null);

		if (results.getTotal() > 0) {
			List<DataSource> dataSources = results.getItems();

			return dataSources.get(0);
		}

		return contactsEngineClient.addDataSource(
			faroProject, credentials, new Author(), name, url, provider, null,
			DataSource.Status.ACTIVE.name());
	}

	protected void createIdentities(
		LiferayUsersDataCreator liferayUsersDataCreator) {

		for (Map<String, Object> dxpEntity :
				liferayUsersDataCreator.getObjects()) {

			Http.Options options = new Http.Options();

			Map<String, Object> liferayUserMap =
				(Map<String, Object>)dxpEntity.get("objectJSONObject");

			String uuid = (String)liferayUserMap.get("uuid");

			options.setBody(
				JSONUtil.put(
					"dataSourceId", liferayUsersDataCreator.getDataSourceId()
				).put(
					"id", StringUtil.removeChar(uuid, CharPool.DASH)
				).put(
					"identity",
					JSONUtil.put("email", liferayUserMap.get("emailAddress"))
				).put(
					"userId", uuid
				).toString(),
				ContentTypes.APPLICATION_JSON, StandardCharsets.UTF_8.name());

			options.setHeaders(
				HashMapBuilder.put(
					"Content-Type", ContentTypes.APPLICATION_JSON
				).put(
					"OSB-Asah-Project-ID", faroProject.getProjectId()
				).build());
			options.setLocation(
				FaroPropsValues.OSB_ASAH_PUBLISHER_URL + "/identity");
			options.setPost(true);

			try {
				String responseString = _http.URLtoString(options);

				Http.Response response = options.getResponse();

				if (response.getResponseCode() != 200) {
					log.error(
						StringBundler.concat(
							"Unable to add an identity for ",
							liferayUserMap.get("emailAddress"), ": ",
							response.getResponseCode(), " ", responseString));
				}
			}
			catch (Exception exception) {
				log.error(exception);
			}
		}
	}

	protected void createIndividualSegments(String channelId) throws Exception {
		User user = userLocalService.getUserByEmailAddress(
			portal.getDefaultCompanyId(), "test@liferay.com");

		for (Map.Entry<String, String> individualSegment :
				_individualSegments.entrySet()) {

			contactsEngineClient.addIndividualSegment(
				faroProject, user.getUserId(), channelId, null,
				individualSegment.getValue(), false, individualSegment.getKey(),
				IndividualSegment.Type.BATCH.name(), false,
				IndividualSegment.Status.ACTIVE.name());
		}
	}

	protected void createLiferayAssociations(
		String channelId, LiferayUsersDataCreator liferayUsersDataCreator) {

		// Groups

		LiferayGroupsDataCreator liferayGroupsDataCreator =
			new LiferayGroupsDataCreator(
				contactsEngineClient, faroProject,
				liferayUsersDataCreator.getDataSourceId());

		liferayGroupsDataCreator.create(5, true);

		liferayGroupsDataCreator.execute();

		contactsEngineClient.patchChannel(
			faroProject, channelId, liferayUsersDataCreator.getDataSourceId(),
			TransformUtil.transform(
				liferayGroupsDataCreator.getObjects(),
				liferayGroup -> {
					Map<String, Object> fields =
						(Map<String, Object>)liferayGroup.get("fields");

					return HashMapBuilder.put(
						"id", String.valueOf(fields.get("groupId"))
					).put(
						"name", (String)fields.get("name")
					).build();
				}));

		// Organizations

		LiferayOrganizationsDataCreator liferayOrganizationsDataCreator =
			new LiferayOrganizationsDataCreator(
				contactsEngineClient, faroProject,
				liferayUsersDataCreator.getDataSourceId());

		Map<String, Object> liferayOrganization =
			liferayOrganizationsDataCreator.create(
				true, new Object[] {"Liferay", new HashMap<>()});

		liferayOrganizationsDataCreator.create(
			true, new Object[] {"Engineering", liferayOrganization});
		liferayOrganizationsDataCreator.create(
			true, new Object[] {"Marketing", liferayOrganization});
		liferayOrganizationsDataCreator.create(
			true, new Object[] {"Sales", liferayOrganization});
		liferayOrganizationsDataCreator.create(
			true, new Object[] {"Support", liferayOrganization});

		liferayOrganizationsDataCreator.execute();

		// Roles

		LiferayRolesDataCreator liferayRolesDataCreator =
			new LiferayRolesDataCreator(
				contactsEngineClient, faroProject,
				liferayUsersDataCreator.getDataSourceId());

		liferayRolesDataCreator.create(5, true);

		liferayRolesDataCreator.execute();

		// Teams

		LiferayTeamsDataCreator liferayTeamsDataCreator =
			new LiferayTeamsDataCreator(
				contactsEngineClient, faroProject,
				liferayUsersDataCreator.getDataSourceId());

		liferayTeamsDataCreator.create(
			5, true, new Object[] {liferayGroupsDataCreator.getRandom()});

		liferayTeamsDataCreator.execute();

		// User Groups

		LiferayUserGroupsDataCreator liferayUserGroupsDataCreator =
			new LiferayUserGroupsDataCreator(
				contactsEngineClient, faroProject,
				liferayUsersDataCreator.getDataSourceId());

		liferayUserGroupsDataCreator.create(5, true);

		liferayUserGroupsDataCreator.execute();

		// Associations

		LiferayAssociationsDataCreator liferayAssociationsDataCreator =
			new LiferayAssociationsDataCreator(
				faroProject, liferayUsersDataCreator.getDataSourceId(),
				liferayGroupsDataCreator, liferayOrganizationsDataCreator,
				liferayRolesDataCreator, liferayTeamsDataCreator,
				liferayUserGroupsDataCreator);

		for (Map<String, Object> dxpEntity :
				liferayUsersDataCreator.getObjects()) {

			Map<String, Object> liferayUserMap =
				(Map<String, Object>)dxpEntity.get("objectJSONObject");

			liferayAssociationsDataCreator.create(
				new Object[] {liferayUserMap});
		}

		liferayAssociationsDataCreator.execute();
	}

	protected LiferayUsersDataCreator createLiferayData() {
		DataSource dataSource = createDataSource(
			faroProject, new TokenCredentials(), getLiferayProvider(),
			_LIFERAY_DATA_SOURCE_NAME, "beryl.com");

		updateDataSourceDetails(dataSource.getId());

		// Individuals

		LiferayUsersDataCreator liferayUsersDataCreator =
			new LiferayUsersDataCreator(
				contactsEngineClient, faroProject, dataSource.getId());

		liferayUsersDataCreator.create(_LIFERAY_INDIVIDUALS_COUNT, true);

		liferayUsersDataCreator.execute();

		return liferayUsersDataCreator;
	}

	protected void createLiferayExperiments(
		String dataSourceId, PageContextsDataCreator pageContextsDataCreator) {

		LiferayExperimentsDataCreator liferayExperimentsDataCreator =
			new LiferayExperimentsDataCreator(
				contactsEngineClient, faroProject, getChannelId(),
				dataSourceId);

		for (int i = 0; i < _LIFERAY_EXPERIMENTS_COUNT; i++) {
			liferayExperimentsDataCreator.create(
				new Object[] {pageContextsDataCreator.getRandom()});
		}

		liferayExperimentsDataCreator.execute();
	}

	protected void createMembershipChanges(String channelId, int expectedCount)
		throws Exception {

		poll(
			() -> contactsEngineClient.getIndividualSegments(
				faroProject, channelId, null, null, null, null, null, null,
				null, 1, 10000, null),
			expectedCount,
			results -> {
				for (IndividualSegment individualSegment : results.getItems()) {
					if (!StringUtil.equals(
							individualSegment.getState(),
							IndividualSegment.State.READY.name())) {

						return false;
					}
				}

				return true;
			},
			expectedCount * Time.MINUTE, "individual segments");

		MembershipChangesDataCreator membershipChangesDataCreator =
			new MembershipChangesDataCreator(contactsEngineClient, faroProject);

		Results<IndividualSegment> individualSegmentResults =
			contactsEngineClient.getIndividualSegments(
				faroProject, channelId, null, null, null, null, null, null,
				null, 1, 10000, null);

		for (IndividualSegment individualSegment :
				individualSegmentResults.getItems()) {

			Results<IndividualSegmentMembershipChange>
				individualSegmentMembershipChangeResults =
					contactsEngineClient.getIndividualSegmentMembershipChanges(
						faroProject, individualSegment.getId(), null, null,
						null, 1, 10000, null);

			for (IndividualSegmentMembershipChange
					individualSegmentMembershipChange :
						individualSegmentMembershipChangeResults.getItems()) {

				if ((individualSegmentMembershipChange.getDateChanged() ==
						null) ||
					(individualSegmentMembershipChange.getDateFirst() ==
						null)) {

					continue;
				}

				membershipChangesDataCreator.create(
					new Object[] {individualSegmentMembershipChange});
			}

			contactsEngineClient.addNanite(
				faroProject, "UpdateDynamicMembershipsNanite",
				HashMapBuilder.<String, Object>put(
					"dateModified",
					() -> DateUtil.formatDate(
						new Date(System.currentTimeMillis() - Time.MONTH),
						"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
				).put(
					"individualSegmentJSONObject",
					HashMapBuilder.<String, Object>put(
						"channelId", individualSegment.getChannelId()
					).put(
						"externalReferenceCode",
						individualSegment.getExternalReferenceCode()
					).put(
						"filter", individualSegment.getFilterString()
					).put(
						"id", individualSegment.getId()
					).put(
						"includeAnonymousUsers",
						individualSegment.isIncludeAnonymousUsers()
					).put(
						"sequential", individualSegment.isSequential()
					).build()
				).build());
		}

		membershipChangesDataCreator.execute();
	}

	protected void createSalesforceData(
		LiferayUsersDataCreator liferayUsersDataCreator) {

		DataSource dataSource = createDataSource(
			faroProject, new OAuth2Credentials(), getSalesforceProvider(),
			_SALESFORCE_DATA_SOURCE_NAME, "http://salesforce.example.faro.com");

		// Accounts

		SalesforceAccountsDataCreator salesforceAccountsDataCreator =
			new SalesforceAccountsDataCreator(
				contactsEngineClient, faroProject, dataSource.getId());

		salesforceAccountsDataCreator.create(_SALESFORCE_ACCOUNTS_COUNT, true);

		salesforceAccountsDataCreator.execute();

		// Individuals

		SalesforceIndividualsDataCreator salesforceIndividualsDataCreator =
			new SalesforceIndividualsDataCreator(
				contactsEngineClient, faroProject, dataSource.getId());

		for (Map<String, Object> dxpEntity :
				liferayUsersDataCreator.getObjects()) {

			salesforceIndividualsDataCreator.create(
				new Object[] {
					dxpEntity.get("objectJSONObject"),
					salesforceAccountsDataCreator.getRandom()
				});
		}

		salesforceIndividualsDataCreator.create(
			_SALESFORCE_INDIVIDUALS_COUNT, false);

		salesforceIndividualsDataCreator.execute();

		// Nanites

		Map<String, Object> salesforceNaniteContext =
			HashMapBuilder.<String, Object>put(
				"dataSourceId", dataSource.getId()
			).put(
				"type", "audit-events"
			).build();

		contactsEngineClient.addNanite(
			faroProject, "SalesforceAccountsNanite", salesforceNaniteContext);
		contactsEngineClient.addNanite(
			faroProject, "SalesforceIndividualsNanite",
			salesforceNaniteContext);
	}

	protected void curateInterests() {
		contactsEngineClient.addNanites(
			faroProject,
			Collections.singletonList("InterestThresholdScoreNanite"));

		contactsEngineClient.addNanites(
			faroProject, Collections.singletonList("InterestTopicsNanite"));

		contactsEngineClient.addNanites(
			faroProject,
			Collections.singletonList("IndividualInterestScoresNanite"));
	}

	protected String getChannelId() {
		Results results = contactsEngineClient.getChannels(
			faroProject, 0, 1, null, null);

		List<Channel> channels = results.getItems();

		if (channels.isEmpty()) {
			return null;
		}

		Channel channel = channels.get(0);

		return channel.getId();
	}

	protected LiferayProvider getLiferayProvider() {
		LiferayProvider liferayProvider = new LiferayProvider();

		LiferayProvider.AnalyticsConfiguration analyticsConfiguration =
			new LiferayProvider.AnalyticsConfiguration();

		analyticsConfiguration.setEnableAllSites(true);
		analyticsConfiguration.setSites(Collections.emptyList());

		liferayProvider.setAnalyticsConfiguration(analyticsConfiguration);

		LiferayProvider.ContactsConfiguration contactsConfiguration =
			new LiferayProvider.ContactsConfiguration();

		contactsConfiguration.setEnableAllContacts(true);

		liferayProvider.setContactsConfiguration(contactsConfiguration);

		return liferayProvider;
	}

	protected SalesforceProvider getSalesforceProvider() {
		SalesforceProvider salesforceProvider = new SalesforceProvider();

		SalesforceProvider.AccountsConfiguration accountsConfiguration =
			new SalesforceProvider.AccountsConfiguration();

		accountsConfiguration.setEnableAllAccounts(true);

		salesforceProvider.setAccountsConfiguration(accountsConfiguration);

		SalesforceProvider.CampaignsConfiguration campaignsConfiguration =
			new SalesforceProvider.CampaignsConfiguration();

		campaignsConfiguration.setEnableAllCampaigns(false);

		salesforceProvider.setCampaignsConfiguration(campaignsConfiguration);

		ChannelsConfiguration channelsConfiguration =
			new ChannelsConfiguration();

		channelsConfiguration.setEnableAllChannels(true);

		salesforceProvider.setChannelsConfiguration(channelsConfiguration);

		SalesforceProvider.ContactsConfiguration contactsConfiguration =
			new SalesforceProvider.ContactsConfiguration();

		contactsConfiguration.setEnableAllContacts(true);
		contactsConfiguration.setEnableAllLeads(true);

		salesforceProvider.setContactsConfiguration(contactsConfiguration);

		SalesforceProvider.OpportunitiesConfiguration
			opportunitiesConfiguration =
				new SalesforceProvider.OpportunitiesConfiguration();

		opportunitiesConfiguration.setEnableAllOpportunities(false);

		salesforceProvider.setOpportunitiesConfiguration(
			opportunitiesConfiguration);

		return salesforceProvider;
	}

	protected <T> void poll(
			Supplier<Results<T>> resultsSupplier, long expectedCount,
			Function<Results<T>, Boolean> checkResultsFunction, long timeout,
			String entityName)
		throws Exception {

		long startTime = System.currentTimeMillis();

		while ((System.currentTimeMillis() - startTime) < timeout) {
			Results results = resultsSupplier.get();

			if (log.isInfoEnabled()) {
				log.info(
					StringBundler.concat(
						"Processed ", results.getTotal(), StringPool.SLASH,
						expectedCount, StringPool.SPACE, entityName));
			}

			if (checkResultsFunction.apply(results)) {
				return;
			}

			Thread.sleep(10 * Time.SECOND);
		}
	}

	protected <T> void poll(
			Supplier<Results<T>> resultsSupplier, long expectedCount,
			long timeout, String entityName)
		throws Exception {

		poll(
			resultsSupplier, expectedCount,
			results -> results.getTotal() >= expectedCount, timeout,
			entityName);
	}

	protected void updateDataSourceDetails(String dataSourceId) {
		Http.Options options = new Http.Options();

		options.setBody(
			JSONUtil.put(
				"contactsSelected", true
			).toString(),
			ContentTypes.APPLICATION_JSON, StandardCharsets.UTF_8.name());
		options.setHeaders(
			HashMapBuilder.put(
				"Content-Type", ContentTypes.APPLICATION_JSON
			).put(
				"OSB-Asah-Project-ID", faroProject.getProjectId()
			).build());
		options.setLocation(
			StringBundler.concat(
				FaroPropsValues.OSB_ASAH_BACKEND_URL, "/api/1.0/data-sources/",
				dataSourceId, "/details"));
		options.setPut(true);

		try {
			String responseString = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() != 200) {
				log.error(
					"Unable to enable contact synchronization: " +
						responseString);
			}
		}
		catch (Exception exception) {
			log.error(exception);
		}
	}

	private static final int _LIFERAY_ANALYTIC_EVENTS_MAX_COUNT_PER_USER = 50;

	private static final int _LIFERAY_ANONYMOUS_EVENTS_COUNT = 1000;

	private static final String _LIFERAY_DATA_SOURCE_NAME = "Beryl Commerce";

	private static final int _LIFERAY_EXPERIMENTS_COUNT = 5;

	private static final int _LIFERAY_INDIVIDUALS_COUNT = 100;

	private static final int _SALESFORCE_ACCOUNTS_COUNT = 50;

	private static final String _SALESFORCE_DATA_SOURCE_NAME =
		"Beryl Salesforce";

	private static final int _SALESFORCE_INDIVIDUALS_COUNT = 100;

	private static final Map<String, String> _individualSegments =
		HashMapBuilder.put(
			"Annual Revenue > $500K",
			"(accounts.filter(filter='(" +
				"organization/annualRevenue/value gt 500000)'))"
		).put(
			"managers", "contains(demographics/jobTitle/value, 'manager')"
		).build();

	@Reference
	private Http _http;

}