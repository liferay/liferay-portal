/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.osb.faro.constants.FaroNotificationConstants;
import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.contacts.model.constants.JSONConstants;
import com.liferay.osb.faro.contacts.service.ContactsCardTemplateLocalService;
import com.liferay.osb.faro.contacts.service.ContactsLayoutTemplateLocalService;
import com.liferay.osb.faro.engine.client.model.Workspace;
import com.liferay.osb.faro.engine.client.util.EngineServiceURLUtil;
import com.liferay.osb.faro.exception.EmailAddressDomainException;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroProjectEmailDomain;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.provisioning.client.ProvisioningClient;
import com.liferay.osb.faro.provisioning.client.constants.CorpProjectConstants;
import com.liferay.osb.faro.provisioning.client.constants.ProductConstants;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntry;
import com.liferay.osb.faro.provisioning.client.model.OSBOfferingEntry;
import com.liferay.osb.faro.service.FaroNotificationLocalService;
import com.liferay.osb.faro.service.FaroProjectEmailDomainLocalService;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.osb.faro.web.internal.annotations.Unauthenticated;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.contacts.FieldMappingController;
import com.liferay.osb.faro.web.internal.exception.FaroException;
import com.liferay.osb.faro.web.internal.exception.FaroValidationException;
import com.liferay.osb.faro.web.internal.model.display.contacts.JoinableProjectDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.ProjectDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.ProjectUsageDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.TimeZoneDisplay;
import com.liferay.osb.faro.web.internal.model.display.main.FaroSubscriptionDisplay;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.osb.faro.web.internal.util.TimeZoneUtil;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.GroupFriendlyURLException;
import com.liferay.portal.kernel.exception.LayoutFriendlyURLException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Matthew Kong
 */
@Component(service = ProjectController.class)
@Path("/project")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectController extends BaseFaroController {

	@Path("/{groupId}/activate")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public void activate(@PathParam("groupId") long groupId) throws Exception {
		FaroProject faroProject =
			_faroProjectLocalService.getFaroProjectByGroupId(groupId);

		if (!StringUtil.equals(
				faroProject.getState(),
				FaroProjectConstants.STATE_DEACTIVATED)) {

			return;
		}

		faroProject.setState(FaroProjectConstants.STATE_ACTIVATING);

		faroProjectLocalService.updateFaroProject(faroProject);
	}

	@Path("/state")
	@POST
	@RolesAllowed(StringPool.BLANK)
	public void addGlobalState(
		@FormParam("keys") FaroParam<List<String>> keysFaroParam,
		@FormParam("state") String state,
		@FormParam("startDate") Date startDate,
		@FormParam("endDate") Date endDate) {

		projectHelper.addGlobalState(
			keysFaroParam.getValue(),
			HashMapBuilder.<String, Object>put(
				"endDate", endDate
			).put(
				"startDate", startDate
			).put(
				"state", state
			).build());
	}

	@Path("/{groupId}/ip_addresses")
	@POST
	@RolesAllowed(StringPool.BLANK)
	public void addIPAddresses(
			@PathParam("groupId") long groupId,
			@FormParam("ipAddresses") FaroParam<List<String>>
				ipAddressesFaroParam)
		throws Exception {

		FaroProject faroProject =
			_faroProjectLocalService.fetchFaroProjectByGroupId(groupId);

		faroProject.setIpAddresses(
			JSONUtil.writeValueAsString(ipAddressesFaroParam.getValue()));

		_faroProjectLocalService.updateFaroProject(faroProject);
	}

	@Path("/{groupId}/configure")
	@PUT
	@RolesAllowed(RoleConstants.SITE_OWNER)
	public ProjectDisplay configure(
			@FormParam("friendlyURL") String friendlyURL,
			@PathParam("groupId") long groupId,
			@DefaultValue(JSONConstants.NULL_JSON_ARRAY)
			@FormParam("emailAddressDomains")
			FaroParam
				<List<String>> emailAddressDomainsFaroParam,
			@FormParam("incidentReportEmailAddresses") FaroParam<List<String>>
				incidentReportEmailAddressesFaroParam,
			@FormParam("name") String name,
			@DefaultValue(StringPool.BLANK) @FormParam("timeZoneId") String
				timeZoneId)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		if (!StringUtil.equals(
				faroProject.getState(),
				FaroProjectConstants.STATE_UNCONFIGURED)) {

			return new ProjectDisplay(faroProject);
		}

		User user = getUser();

		_provisioningClient.addCorpProjectUsers(
			faroProject.getCorpProjectUuid(),
			new String[] {user.getUserUuid()});

		_provisioningClient.addUserCorpProjectRoles(
			faroProject.getCorpProjectUuid(), new String[] {user.getUserUuid()},
			CorpProjectConstants.ROLE_OWNER);

		faroProject.setState(FaroProjectConstants.STATE_NOT_READY);

		faroProjectLocalService.updateFaroProject(faroProject);

		return update(
			friendlyURL, groupId, emailAddressDomainsFaroParam,
			incidentReportEmailAddressesFaroParam, name, timeZoneId);
	}

	@Path("/provisioned")
	@POST
	@RolesAllowed(StringPool.BLANK)
	public ProjectDisplay createProvisioned(
			@FormParam("corpProjectUuid") String corpProjectUuid,
			@DefaultValue(JSONConstants.NULL_JSON_ARRAY)
			@FormParam("emailAddressDomains")
			FaroParam
				<List<String>> emailAddressDomainsFaroParam,
			@FormParam("friendlyURL") String friendlyURL,
			@DefaultValue(JSONConstants.NULL_JSON_ARRAY)
			@FormParam("incidentReportEmailAddresses")
			FaroParam
				<List<String>> incidentReportEmailAddressesFaroParam,
			@FormParam("name") String name,
			@FormParam("ownerEmailAddress") String ownerEmailAddress,
			@FormParam("serverLocation") String serverLocation,
			@DefaultValue("UTC") @FormParam("timeZoneId") String timeZoneId)
		throws Exception {

		User user = getUser();

		FaroProject faroProject = _create(
			corpProjectUuid, name, emailAddressDomainsFaroParam.getValue(),
			friendlyURL, incidentReportEmailAddressesFaroParam.getValue(),
			serverLocation, FaroProjectConstants.STATE_UNCONFIGURED,
			timeZoneId);

		Role role = _roleLocalService.getRole(
			user.getCompanyId(), RoleConstants.SITE_OWNER);

		_faroUserLocalService.addFaroUser(
			getUserId(), faroProject.getGroupId(), 0, role.getRoleId(),
			ownerEmailAddress, FaroUserConstants.STATUS_PENDING, false);

		if (_shouldSendCreatedWorkspaceEmail(faroProject)) {
			_faroProjectLocalService.sendCreatedWorkspaceEmail(
				faroProject.getWeDeployKey());
		}

		return new ProjectDisplay(faroProject);
	}

	@Path("/trial")
	@POST
	@RolesAllowed(StringPool.BLANK)
	public ProjectDisplay createTrial(
			@DefaultValue(JSONConstants.NULL_JSON_ARRAY)
			@FormParam("emailAddressDomains")
			FaroParam
				<List<String>> emailAddressDomainsFaroParam,
			@FormParam("friendlyURL") String friendlyURL,
			@DefaultValue(JSONConstants.NULL_JSON_ARRAY)
			@FormParam("incidentReportEmailAddresses")
			FaroParam
				<List<String>> incidentReportEmailAddressesFaroParam,
			@FormParam("name") String name,
			@FormParam("ownerEmailAddress") String ownerEmailAddress,
			@FormParam("serverLocation") String serverLocation,
			@DefaultValue("UTC") @FormParam("timeZoneId") String timeZoneId)
		throws Exception {

		return _createUnprovisioned(
			name, null, null, null, null,
			emailAddressDomainsFaroParam.getValue(), friendlyURL,
			incidentReportEmailAddressesFaroParam.getValue(), ownerEmailAddress,
			serverLocation, timeZoneId, true);
	}

	@Path("/unprovisioned")
	@POST
	@RolesAllowed(StringPool.BLANK)
	public ProjectDisplay createUnprovisioned(
			@FormParam("accountKey") String accountKey,
			@FormParam("accountName") String accountName,
			@FormParam("corpProjectName") String corpProjectName,
			@FormParam("corpProjectUuid") String corpProjectUuid,
			@DefaultValue(JSONConstants.NULL_JSON_ARRAY)
			@FormParam("emailAddressDomains")
			FaroParam
				<List<String>> emailAddressDomainsFaroParam,
			@FormParam("friendlyURL") String friendlyURL,
			@DefaultValue(JSONConstants.NULL_JSON_ARRAY)
			@FormParam("incidentReportEmailAddresses")
			FaroParam
				<List<String>> incidentReportEmailAddressesFaroParam,
			@FormParam("name") String name,
			@FormParam("ownerEmailAddress") String ownerEmailAddress,
			@FormParam("serverLocation") String serverLocation,
			@DefaultValue("UTC") @FormParam("timeZoneId") String timeZoneId,
			@FormParam("trial") boolean trial)
		throws Exception {

		FaroProject faroProject =
			_faroProjectLocalService.fetchFaroProjectByCorpProjectUuid(
				corpProjectUuid);

		if (faroProject != null) {
			return new ProjectDisplay(faroProject);
		}

		return _createUnprovisioned(
			name, accountKey, accountName, corpProjectName, corpProjectUuid,
			emailAddressDomainsFaroParam.getValue(), friendlyURL,
			incidentReportEmailAddressesFaroParam.getValue(), ownerEmailAddress,
			serverLocation, timeZoneId, trial);
	}

	@DELETE
	@Path("/{groupId}")
	@RolesAllowed(StringPool.BLANK)
	public ProjectDisplay delete(
			@PathParam("groupId") long groupId,
			@DefaultValue("true") @QueryParam("deleteData") boolean deleteData)
		throws Exception {

		FaroProject faroProject =
			_faroProjectLocalService.fetchFaroProjectByGroupId(groupId);

		_validateLastSeenDate(faroProject);

		_contactsCardTemplateLocalService.deleteContactsCardTemplates(groupId);
		_contactsLayoutTemplateLocalService.deleteContactsLayoutTemplates(
			groupId);

		contactsEngineClient.deleteProject(faroProject, deleteData);

		return new ProjectDisplay(
			_faroProjectLocalService.deleteFaroProjectByGroupId(groupId));
	}

	@DELETE
	@Path("/state")
	@RolesAllowed(StringPool.BLANK)
	public void deleteGlobalState(
		@FormParam("keys") FaroParam<List<String>> keysFaroParam) {

		projectHelper.deleteGlobalStates(keysFaroParam.getValue());
	}

	@Path("/{groupId}/recommendations/disable")
	@POST
	@RolesAllowed(StringPool.BLANK)
	public void disableRecommendations(@PathParam("groupId") long groupId) {
		FaroProject faroProject =
			_faroProjectLocalService.fetchFaroProjectByGroupId(groupId);

		faroProject.setRecommendationsEnabled(false);

		_faroProjectLocalService.updateFaroProject(faroProject);
	}

	@Path("/{groupId}/recommendations/enable")
	@POST
	@RolesAllowed(StringPool.BLANK)
	public void enableRecommendations(@PathParam("groupId") long groupId) {
		FaroProject faroProject =
			_faroProjectLocalService.fetchFaroProjectByGroupId(groupId);

		faroProject.setRecommendationsEnabled(true);

		_faroProjectLocalService.updateFaroProject(faroProject);
	}

	@GET
	@Path("/{groupId}/email_address_domains")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<String> getEmailAddressDomains(
		@PathParam("groupId") long groupId) {

		return TransformUtil.transform(
			_faroProjectEmailDomainLocalService.
				getFaroProjectEmailDomainsByGroupId(groupId),
			FaroProjectEmailDomain::getEmailDomain);
	}

	@GET
	@Path("/{projectId}/endpoints")
	@Unauthenticated
	public Map<String, Object> getEndpoints(
			@PathParam("projectId") String projectId)
		throws Exception {

		Map<String, Object> properties = new HashMap<>();

		FaroProject faroProject =
			faroProjectLocalService.fetchFaroProjectByWeDeployKey(
				projectId + ".lfr.cloud");

		if (faroProject == null) {
			return properties;
		}

		properties.put(
			"liferayAnalyticsEndpointURL",
			EngineServiceURLUtil.getPublisherExternalURL(faroProject));
		properties.put(
			"liferayAnalyticsFaroBackendURL",
			EngineServiceURLUtil.getBackendExternalURL(faroProject));

		return properties;
	}

	@GET
	@Path("/joinable")
	public List<JoinableProjectDisplay> getJoinableProjectDisplays()
		throws PortalException {

		return TransformUtil.transform(
			_faroProjectLocalService.getJoinableFaroProjects(getUser()),
			faroProject -> new JoinableProjectDisplay(
				faroProject.getGroupId(), faroProject.getName(),
				Objects.nonNull(
					_faroUserLocalService.fetchFaroUser(
						faroProject.getGroupId(), getUserId()))));
	}

	@GET
	@Path("/{groupId}")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public ProjectDisplay getProjectDisplay(
			@PathParam("groupId") long groupId,
			@QueryParam("forceUpdate") boolean forceUpdate,
			@DefaultValue("true") @QueryParam("updateLastAccess") boolean
				updateLastAccess)
		throws Exception {

		FaroProject faroProject =
			_faroProjectLocalService.getFaroProjectByGroupId(groupId);

		long now = System.currentTimeMillis();

		if (forceUpdate ||
			Objects.equals(
				faroProject.getState(),
				FaroProjectConstants.STATE_UNAVAILABLE)) {

			faroProject.setModifiedTime(now);

			FaroSubscriptionDisplay faroSubscriptionDisplay =
				new FaroSubscriptionDisplay(getOSBAccountEntry(faroProject));

			if (_isSubscriptionPlanChanged(
					faroProject, faroSubscriptionDisplay.getName())) {

				faroProject.setSubscriptionModifiedTime(now);
			}

			try {
				if (Objects.equals(
						faroProject.getState(),
						FaroProjectConstants.STATE_UNAVAILABLE)) {

					faroProject.setState(FaroProjectConstants.STATE_READY);
				}

				faroSubscriptionDisplay.setCounts(
					faroProject, cerebroEngineClient, contactsEngineClient);

				faroProject.setSubscription(
					JSONUtil.writeValueAsString(faroSubscriptionDisplay));
			}
			catch (Exception exception) {
				_log.error(exception);

				faroProject.setState(FaroProjectConstants.STATE_UNAVAILABLE);
			}

			faroProject = _faroProjectLocalService.updateFaroProject(
				faroProject);
		}

		if (updateLastAccess &&
			((now - faroProject.getLastAccessTime()) > Time.DAY)) {

			faroProject.setLastAccessTime(now);

			faroProject = _faroProjectLocalService.updateFaroProject(
				faroProject);
		}

		return _getProjectDisplay(faroProject);
	}

	@GET
	@Path("/corpProjectUuid/{corpProjectUuid}")
	public ProjectDisplay getProjectDisplay(
			@PathParam("corpProjectUuid") String corpProjectUuid)
		throws Exception {

		_validateCorpProjectUuid(corpProjectUuid);

		FaroProject faroProject =
			_faroProjectLocalService.fetchFaroProjectByCorpProjectUuid(
				corpProjectUuid);

		if (faroProject == null) {
			return new ProjectDisplay(getOSBAccountEntry(corpProjectUuid));
		}

		return new ProjectDisplay(faroProject);
	}

	@GET
	public List<ProjectDisplay> getProjectDisplays() {
		return TransformUtil.transform(
			_faroUserLocalService.getFaroUsersByLiveUserId(
				getUserId(), FaroUserConstants.STATUS_APPROVED),
			faroUser -> {
				FaroProject faroProject =
					_faroProjectLocalService.fetchFaroProjectByGroupId(
						faroUser.getGroupId());

				try {
					return _getProjectDisplay(faroProject);
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						String project = "";

						if (faroProject != null) {
							project = faroProject.getName();
						}

						_log.warn(
							"Could not load project " + project, exception);
					}

					return null;
				}
			});
	}

	@GET
	@Path("/usage")
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public List<ProjectUsageDisplay> getProjectUsageDisplays(
		@QueryParam("groupId") Long groupId,
		@DefaultValue("true") @QueryParam("includeIndividualsCounts") boolean
			includeIndividualsCounts,
		@DefaultValue("true") @QueryParam("includeMonthlyValues") boolean
			includeMonthlyValues,
		@DefaultValue("true") @QueryParam("includePageViewsCounts") boolean
			includePageViewsCounts) {

		List<FaroProject> faroProjects = new ArrayList<>();

		if (Validator.isNotNull(groupId)) {
			faroProjects.add(
				_faroProjectLocalService.fetchFaroProjectByGroupId(groupId));
		}
		else {
			faroProjects = _faroProjectLocalService.getFaroProjects(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}

		return TransformUtil.transform(
			faroProjects,
			faroProject -> new ProjectUsageDisplay(
				faroProject, includeIndividualsCounts, includeMonthlyValues,
				includePageViewsCounts));
	}

	@GET
	@Path("/time_zones")
	public List<TimeZoneDisplay> getTimeZones() {
		return TimeZoneUtil.getTimeZoneDisplays();
	}

	@PATCH
	@Path("/{groupId}")
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public void patch(
			@DefaultValue(StringPool.BLANK) @FormParam("corpProjectName") String
				corpProjectName,
			@DefaultValue(StringPool.BLANK) @FormParam("corpProjectUuid") String
				corpProjectUuid,
			@PathParam("groupId") long groupId,
			@DefaultValue(StringPool.BLANK) @FormParam("timeZoneId") String
				timeZoneId)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		if (!Validator.isBlank(corpProjectName)) {
			faroProject.setCorpProjectName(corpProjectName);
		}

		if (!Validator.isBlank(corpProjectUuid)) {
			faroProject.setCorpProjectUuid(corpProjectUuid);
		}

		if (!Validator.isBlank(timeZoneId) &&
			!Objects.equals(faroProject.getTimeZoneId(), timeZoneId)) {

			_validateTimeZoneId(timeZoneId);

			_sendTimeZoneNotification(groupId);

			cerebroEngineClient.updateTimeZone(faroProject);

			faroProject.setTimeZoneId(timeZoneId);
		}

		faroProjectLocalService.updateFaroProject(faroProject);

		if (!Validator.isBlank(corpProjectUuid)) {
			getProjectDisplay(groupId, true, true);
		}
	}

	@GET
	@Path("/usage/reset/preview")
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public List<FaroSubscriptionDisplay> previewResetProjectUsageDisplays(
			@QueryParam("groupId") Long groupId,
			@QueryParam("startDateString") String startDateString)
		throws Exception {

		List<FaroProject> faroProjects = new ArrayList<>();

		if (groupId != null) {
			faroProjects.add(
				_faroProjectLocalService.fetchFaroProjectByGroupId(groupId));
		}
		else {
			faroProjects = _faroProjectLocalService.getFaroProjects(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}

		return TransformUtil.transform(
			faroProjects,
			faroProject -> _resetProjectUsageDisplays(
				faroProject.getGroupId(), startDateString));
	}

	@DELETE
	@Path("/usage/reset")
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public void resetProjectUsageDisplays(
			@QueryParam("groupId") Long groupId,
			@QueryParam("startDateString") String startDateString)
		throws Exception {

		ExecutorService executorService =
			_portalExecutorManager.getPortalExecutor(
				ProjectController.class.getName());

		executorService.submit(
			() -> {
				try {
					List<FaroProject> faroProjects = new ArrayList<>();

					if (Validator.isNotNull(groupId)) {
						faroProjects.add(
							_faroProjectLocalService.fetchFaroProjectByGroupId(
								groupId));
					}
					else {
						faroProjects = _faroProjectLocalService.getFaroProjects(
							QueryUtil.ALL_POS, QueryUtil.ALL_POS);
					}

					for (FaroProject faroProject : faroProjects) {
						_faroProjectLocalService.updateSubscription(
							faroProject.getFaroProjectId(),
							JSONUtil.writeValueAsString(
								_resetProjectUsageDisplays(
									faroProject.getGroupId(),
									startDateString)));
					}

					if (_log.isInfoEnabled()) {
						_log.info("Finished resetting project usage");
					}
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			});
	}

	@Path("/{groupId}/send-created-workspace-email")
	@POST
	@RolesAllowed(StringPool.BLANK)
	public void sendCreatedWorkspaceEmail(@PathParam("groupId") long groupId)
		throws Exception {

		FaroProject faroProject =
			_faroProjectLocalService.fetchFaroProjectByGroupId(groupId);

		_faroProjectLocalService.sendCreatedWorkspaceEmail(
			faroProject.getWeDeployKey());
	}

	@Path("/{groupId}")
	@PUT
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public ProjectDisplay update(
			@FormParam("friendlyURL") String friendlyURL,
			@PathParam("groupId") long groupId,
			@DefaultValue(JSONConstants.NULL_JSON_ARRAY)
			@FormParam("emailAddressDomains")
			FaroParam
				<List<String>> emailAddressDomainsFaroParam,
			@DefaultValue(JSONConstants.NULL_JSON_ARRAY)
			@FormParam("incidentReportEmailAddresses")
			FaroParam
				<List<String>> incidentReportEmailAddressesFaroParam,
			@FormParam("name") String name,
			@DefaultValue(StringPool.BLANK) @FormParam("timeZoneId") String
				timeZoneId)
		throws Exception {

		if ((friendlyURL == null) || Validator.isBlank(friendlyURL.trim())) {
			Group group = _groupLocalService.getGroup(groupId);

			group.setFriendlyURL(null);

			_groupLocalService.updateGroup(group);
		}
		else {
			_validateFriendlyURL(friendlyURL);

			try {
				_groupLocalService.updateFriendlyURL(groupId, friendlyURL);
			}
			catch (GroupFriendlyURLException groupFriendlyURLException) {
				_log.error(groupFriendlyURLException);

				throw new FaroValidationException(
					"friendlyURL",
					_getFriendlyURLErrorMessage(
						groupFriendlyURLException.getType()));
			}
		}

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		faroProject.setName(name);

		if (!Validator.isBlank(timeZoneId)) {
			_validateTimeZoneId(timeZoneId);

			if (!Objects.equals(faroProject.getTimeZoneId(), timeZoneId)) {
				_sendTimeZoneNotification(groupId);
			}

			faroProject.setTimeZoneId(timeZoneId);

			cerebroEngineClient.updateTimeZone(faroProject);
		}

		List<String> incidentReportEmailAddresses =
			incidentReportEmailAddressesFaroParam.getValue();

		if (!incidentReportEmailAddresses.isEmpty()) {
			_validateIncidentReportEmailAddresses(incidentReportEmailAddresses);

			faroProject.setIncidentReportEmailAddresses(
				JSONUtil.writeValueAsString(incidentReportEmailAddresses));
		}

		try {
			_faroProjectEmailDomainLocalService.addFaroProjectEmailDomains(
				groupId, faroProject.getFaroProjectId(),
				emailAddressDomainsFaroParam.getValue());
		}
		catch (EmailAddressDomainException emailAddressDomainException) {
			throw new FaroValidationException(
				"emailAddressDomains",
				_getEmailAddressDomainsErrorMessage(
					emailAddressDomainException.
						getInvalidEmailAddressDomains()));
		}

		return _getProjectDisplay(
			faroProjectLocalService.updateFaroProject(faroProject));
	}

	protected OSBAccountEntry createOSBAccountEntry(boolean trial) {
		return new OSBAccountEntry() {
			{
				OSBOfferingEntry osbOfferingEntry = new OSBOfferingEntry();

				if (trial) {
					osbOfferingEntry.setProductEntryId(
						ProductConstants.BASIC_PRODUCT_ENTRY_ID);
				}
				else {
					osbOfferingEntry.setProductEntryId(
						ProductConstants.ENTERPRISE_PRODUCT_ENTRY_ID);
				}

				osbOfferingEntry.setQuantity(1);
				osbOfferingEntry.setStartDate(new Date());

				setOfferingEntries(Collections.singletonList(osbOfferingEntry));
			}
		};
	}

	protected OSBAccountEntry getOSBAccountEntry(FaroProject faroProject)
		throws Exception {

		if (Validator.isNull(faroProject.getCorpProjectUuid())) {
			return new OSBAccountEntry() {
				{
					OSBOfferingEntry osbOfferingEntry = new OSBOfferingEntry();

					osbOfferingEntry.setProductEntryId(
						ProductConstants.BASIC_PRODUCT_ENTRY_ID);

					osbOfferingEntry.setQuantity(1);
					osbOfferingEntry.setStartDate(
						new Date(faroProject.getCreateTime()));

					setOfferingEntries(
						Collections.singletonList(osbOfferingEntry));
				}
			};
		}

		return _provisioningClient.getOSBAccountEntry(
			faroProject.getCorpProjectUuid());
	}

	protected OSBAccountEntry getOSBAccountEntry(String corpProjectUuid)
		throws Exception {

		if (Validator.isNull(corpProjectUuid)) {
			return createOSBAccountEntry(true);
		}

		return _provisioningClient.getOSBAccountEntry(corpProjectUuid);
	}

	private FaroProject _create(
			String corpProjectUuid, String name,
			List<String> emailAddressDomains, String friendlyURL,
			List<String> incidentReportEmailAddresses, String serverLocation,
			String state, String timeZoneId)
		throws Exception {

		_validateFriendlyURL(friendlyURL);
		_validateIncidentReportEmailAddresses(incidentReportEmailAddresses);
		_validateTimeZoneId(timeZoneId);

		OSBAccountEntry osbAccountEntry = getOSBAccountEntry(corpProjectUuid);

		FaroSubscriptionDisplay faroSubscriptionDisplay =
			new FaroSubscriptionDisplay(osbAccountEntry);

		User user = getUser();

		FaroProject faroProject = null;

		try {
			faroProject = _faroProjectLocalService.addFaroProject(
				user.getUserId(), name, osbAccountEntry.getDossieraAccountKey(),
				osbAccountEntry.getCorpEntryName(), osbAccountEntry.getName(),
				corpProjectUuid, emailAddressDomains, friendlyURL,
				JSONUtil.writeValueAsString(incidentReportEmailAddresses),
				serverLocation, JSONConstants.NULL_JSON_ARRAY, state,
				JSONUtil.writeValueAsString(faroSubscriptionDisplay),
				timeZoneId, null);
		}
		catch (EmailAddressDomainException emailAddressDomainException) {
			throw new FaroValidationException(
				"emailAddressDomains",
				_getEmailAddressDomainsErrorMessage(
					emailAddressDomainException.
						getInvalidEmailAddressDomains()));
		}
		catch (GroupFriendlyURLException groupFriendlyURLException) {
			_log.error(groupFriendlyURLException);

			throw new FaroValidationException(
				"friendlyURL",
				_getFriendlyURLErrorMessage(
					groupFriendlyURLException.getType()));
		}

		if (corpProjectUuid.equals(FaroPropsValues.FARO_PROJECT_ID)) {
			faroProject.setWeDeployKey(
				FaroPropsValues.FARO_DEFAULT_WE_DEPLOY_KEY);
		}

		String weDeployKey =
			contactsEngineClient.addProject(faroProject) + ".lfr.cloud";

		faroProject.setWeDeployKey(weDeployKey);

		return _faroProjectLocalService.updateFaroProject(faroProject);
	}

	private ProjectDisplay _createUnprovisioned(
			String name, String accountKey, String accountName,
			String corpProjectName, String corpProjectUuid,
			List<String> emailAddressDomains, String friendlyURL,
			List<String> incidentReportEmailAddresses, String ownerEmailAddress,
			String serverLocation, String timeZoneId, boolean trial)
		throws Exception {

		_validateFriendlyURL(friendlyURL);
		_validateIncidentReportEmailAddresses(incidentReportEmailAddresses);
		_validateTimeZoneId(timeZoneId);

		FaroSubscriptionDisplay faroSubscriptionDisplay =
			new FaroSubscriptionDisplay(createOSBAccountEntry(trial));

		FaroProject faroProject = null;

		try {
			faroProject = _faroProjectLocalService.addFaroProject(
				getUserId(), name, accountKey, accountName, corpProjectName,
				corpProjectUuid, emailAddressDomains, friendlyURL,
				JSONUtil.writeValueAsString(incidentReportEmailAddresses),
				serverLocation, JSONConstants.NULL_JSON_ARRAY,
				FaroProjectConstants.STATE_NOT_READY,
				JSONUtil.writeValueAsString(faroSubscriptionDisplay),
				timeZoneId, null);
		}
		catch (EmailAddressDomainException emailAddressDomainException) {
			throw new FaroValidationException(
				"emailAddressDomains",
				_getEmailAddressDomainsErrorMessage(
					emailAddressDomainException.
						getInvalidEmailAddressDomains()));
		}
		catch (GroupFriendlyURLException groupFriendlyURLException) {
			_log.error(groupFriendlyURLException);

			throw new FaroValidationException(
				"friendlyURL",
				_getFriendlyURLErrorMessage(
					groupFriendlyURLException.getType()));
		}

		User user = getUser();

		Role role = _roleLocalService.getRole(
			user.getCompanyId(), RoleConstants.SITE_OWNER);

		if (Validator.isNull(ownerEmailAddress)) {
			ownerEmailAddress = user.getEmailAddress();
		}

		_faroUserLocalService.addFaroUser(
			user.getUserId(), faroProject.getGroupId(), 0, role.getRoleId(),
			ownerEmailAddress, FaroUserConstants.STATUS_PENDING, false);

		faroProject.setWeDeployKey(
			contactsEngineClient.addProject(faroProject) + ".lfr.cloud");

		faroProject = _faroProjectLocalService.updateFaroProject(faroProject);

		if (_shouldSendCreatedWorkspaceEmail(faroProject)) {
			_faroProjectLocalService.sendCreatedWorkspaceEmail(
				faroProject.getWeDeployKey());
		}

		return new ProjectDisplay(faroProject, friendlyURL);
	}

	private String _getDeletionFailedErrorMessage(User user) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", user.getLocale(), getClass());

		return language.get(
			resourceBundle,
			"the-workspace-cannot-be-deleted-because-it-has-received-data-" +
				"recently");
	}

	private String _getEmailAddressDomainsErrorMessage(
		Collection<String> invalidEmailAddressDomains) {

		User user = getUser();

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", user.getLocale(), getClass());

		String pattern = "x-is-not-allowed-to-be-set-as-an-email-domain";

		if (invalidEmailAddressDomains.size() > 1) {
			pattern = "x-are-not-allowed-to-be-set-as-email-domains";
		}

		return language.format(
			resourceBundle, pattern,
			StringUtil.merge(invalidEmailAddressDomains));
	}

	private String _getFriendlyURLErrorMessage(int type) {
		User user = getUser();

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", user.getLocale(), getClass());

		if (type == LayoutFriendlyURLException.ADJACENT_SLASHES) {
			return language.get(
				resourceBundle,
				"please-enter-a-friendly-url-that-does-not-have-adjacent-" +
					"slashes");
		}
		else if (type == LayoutFriendlyURLException.DOES_NOT_START_WITH_SLASH) {
			return language.get(
				resourceBundle,
				"please-enter-a-friendly-url-that-begins-with-a-slash");
		}
		else if ((type == LayoutFriendlyURLException.DUPLICATE) ||
				 (type == LayoutFriendlyURLException.KEYWORD_CONFLICT) ||
				 (type == LayoutFriendlyURLException.POSSIBLE_DUPLICATE)) {

			return language.get(
				resourceBundle, "this-friendly-url-is-already-in-use");
		}
		else if (type == LayoutFriendlyURLException.ENDS_WITH_DASH) {
			return language.get(
				resourceBundle,
				"please-enter-a-friendly-url-that-does-not-end-with-a-dash");
		}
		else if (type == LayoutFriendlyURLException.ENDS_WITH_SLASH) {
			return language.get(
				resourceBundle,
				"please-enter-a-friendly-url-that-does-not-end-with-a-slash");
		}
		else if (type == LayoutFriendlyURLException.INVALID_CHARACTERS) {
			return language.get(
				resourceBundle,
				"please-enter-a-friendly-url-with-valid-characters");
		}
		else if (type == LayoutFriendlyURLException.TOO_SHORT) {
			return language.get(
				resourceBundle,
				"please-enter-a-friendly-url-that-is-at-least-two-characters-" +
					"long");
		}
		else if (type == LayoutFriendlyURLException.TOO_DEEP) {
			return language.get(
				resourceBundle, "the-friendly-url-has-too-many-slashes");
		}
		else if (type == LayoutFriendlyURLException.TOO_LONG) {
			return language.get(
				resourceBundle,
				"please-enter-a-friendly-url-that-is-at-least-two-characters-" +
					"long");
		}

		return language.get(resourceBundle, "invalid-friendly-url");
	}

	private String _getIncidentReportEmailAddressesErrorMessage() {
		User user = getUser();

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", user.getLocale(), getClass());

		return language.get(
			resourceBundle, "invalid-incident-report-email-addresses");
	}

	private ProjectDisplay _getProjectDisplay(FaroProject faroProject)
		throws Exception {

		if (StringUtil.equals(
				faroProject.getState(),
				FaroProjectConstants.STATE_UNCONFIGURED)) {

			Workspace workspace = workspaceEngineClient.getWorkspace(
				faroProject.getWeDeployKey());

			if (!workspace.isReady()) {
				return null;
			}

			return new ProjectDisplay(faroProject);
		}

		if (StringUtil.equals(
				faroProject.getState(), FaroProjectConstants.STATE_NOT_READY)) {

			try {
				_refreshProjectState(faroProject);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Could not refresh project " + faroProject.getName(),
						exception);
				}
			}
		}
		else if (StringUtil.equals(
					faroProject.getState(),
					FaroProjectConstants.STATE_ACTIVATING) &&
				 _isWorkspaceHealthy(faroProject)) {

			faroProject.setState(FaroProjectConstants.STATE_READY);

			faroProject = _faroProjectLocalService.updateFaroProject(
				faroProject);
		}

		ProjectDisplay projectDisplay = new ProjectDisplay(faroProject);

		Group group = _groupLocalService.getGroup(faroProject.getGroupId());

		projectDisplay.setFriendlyURL(group.getFriendlyURL());

		Map<String, Object> globalStateMap = projectHelper.getGlobalStateMap(
			faroProject);

		if (globalStateMap == null) {
			return projectDisplay;
		}

		String state = (String)globalStateMap.get("state");

		if (StringUtil.equals(
				projectDisplay.getState(),
				FaroProjectConstants.STATE_UNAVAILABLE) &&
			StringUtil.equals(state, FaroProjectConstants.STATE_SCHEDULED)) {

			return projectDisplay;
		}
		else if (StringUtil.equals(
					projectDisplay.getState(),
					FaroProjectConstants.STATE_READY) &&
				 StringUtil.equals(
					 state, FaroProjectConstants.STATE_MAINTENANCE)) {

			projectHelper.deleteGlobalState(faroProject.getGroupId());

			return projectDisplay;
		}

		projectDisplay.setState(state);
		projectDisplay.setStateEndDate((Date)globalStateMap.get("endDate"));
		projectDisplay.setStateStartDate((Date)globalStateMap.get("startDate"));

		return projectDisplay;
	}

	private String _getTimeZoneIdErrorMessage(User user) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", user.getLocale(), getClass());

		return language.get(resourceBundle, "invalid-time-zone-id");
	}

	private FaroProject _initializeFaroProject(FaroProject faroProject)
		throws Exception {

		long groupId = faroProject.getGroupId();

		if (_initializingGroupIds.contains(groupId)) {
			faroProject.setState(FaroProjectConstants.STATE_READY);

			return faroProject;
		}

		try {
			_initializingGroupIds.add(groupId);

			cerebroEngineClient.updateTimeZone(faroProject);

			_fieldMappingController.addDefaultFieldMappings(groupId);

			faroProject.setState(FaroProjectConstants.STATE_READY);

			return _faroProjectLocalService.updateFaroProject(faroProject);
		}
		finally {
			_initializingGroupIds.remove(groupId);
		}
	}

	private boolean _isSubscriptionPlanChanged(
			FaroProject faroProject, String subscriptionName)
		throws Exception {

		JSONObject subscriptionJSONObject = _jsonFactory.createJSONObject(
			faroProject.getSubscription());

		String oldSubscriptionName = subscriptionJSONObject.getString("name");

		oldSubscriptionName = StringUtil.replace(
			oldSubscriptionName, "LXC ", "Liferay SaaS ");

		return !Objects.equals(oldSubscriptionName, subscriptionName);
	}

	private boolean _isWorkspaceHealthy(FaroProject faroProject) {
		try {
			contactsEngineClient.getIndividuals(
				faroProject, (String)null, false, 1, 0, null);
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}

		return true;
	}

	private void _refreshProjectState(FaroProject faroProject)
		throws Exception {

		if (StringUtil.equals(
				faroProject.getCorpProjectUuid(),
				FaroPropsValues.FARO_PROJECT_ID)) {

			_initializeFaroProject(faroProject);

			return;
		}

		Workspace workspace = workspaceEngineClient.getWorkspace(
			faroProject.getWeDeployKey());

		if (workspace.isReady()) {
			_initializeFaroProject(faroProject);
		}
	}

	private FaroSubscriptionDisplay _resetProjectUsageDisplays(
			long groupId, String startDateString)
		throws Exception {

		FaroProject faroProject =
			_faroProjectLocalService.fetchFaroProjectByGroupId(groupId);

		FaroSubscriptionDisplay faroSubscriptionDisplay = JSONUtil.readValue(
			faroProject.getSubscription(), FaroSubscriptionDisplay.class);

		faroSubscriptionDisplay.setIndividualsCounts(null);
		faroSubscriptionDisplay.setPageViewsCounts(null);

		faroProject.setSubscription(
			JSONUtil.writeValueAsString(faroSubscriptionDisplay));

		Date date = new Date();

		date = new Date(date.getTime() / Time.DAY * Time.DAY);

		Calendar calendar1 = Calendar.getInstance();

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		Date startDate = dateFormat.parse(startDateString);

		calendar1.setTime(startDate);

		calendar1.set(Calendar.DATE, 1);

		Date lastAnniversaryDate =
			faroSubscriptionDisplay.getLastAnniversaryDate();

		if (DateUtil.compareTo(startDate, lastAnniversaryDate) < 0) {
			startDate = lastAnniversaryDate;

			calendar1.setTime(lastAnniversaryDate);
		}

		while (DateUtil.compareTo(date, startDate) > 0) {
			calendar1.add(Calendar.MONTH, 1);
			calendar1.set(Calendar.DATE, 1);

			Date endDate = calendar1.getTime();

			if (DateUtil.compareTo(endDate, date) > 0) {
				endDate = date;
			}

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				faroProject.getSubscription());

			jsonObject.put(
				"individualsCountSinceLastAnniversary",
				contactsEngineClient.getIndividualsCreatedBetweenCount(
					faroProject, endDate, lastAnniversaryDate)
			).put(
				"pageViewsCountSinceLastAnniversary",
				cerebroEngineClient.getPageViews(
					faroProject, lastAnniversaryDate, endDate)
			);

			faroProject.setSubscription(jsonObject.toString());

			faroSubscriptionDisplay.setUsageCounts(
				cerebroEngineClient, contactsEngineClient, endDate, faroProject,
				startDate);

			faroProject.setSubscription(
				JSONUtil.writeValueAsString(faroSubscriptionDisplay));

			startDate = calendar1.getTime();
		}

		return faroSubscriptionDisplay;
	}

	private void _sendTimeZoneNotification(long groupId) {
		List<FaroUser> faroUsers = _faroUserLocalService.getFaroUsersByStatus(
			groupId, FaroUserConstants.STATUS_APPROVED);

		for (FaroUser faroUser : faroUsers) {
			_faroNotificationLocalService.deleteUnreadFaroNotifications(
				groupId, FaroNotificationConstants.TYPE_ALERT,
				FaroNotificationConstants.SUBTYPE_TIME_ZONE_CHANGED,
				faroUser.getUserId());

			_faroNotificationLocalService.addFaroNotification(
				getUserId(), groupId, faroUser.getUserId(),
				FaroNotificationConstants.SCOPE_USER,
				FaroNotificationConstants.TYPE_ALERT,
				FaroNotificationConstants.SUBTYPE_TIME_ZONE_CHANGED);
		}
	}

	private boolean _shouldSendCreatedWorkspaceEmail(FaroProject faroProject)
		throws Exception {

		FaroSubscriptionDisplay faroSubscriptionDisplay = JSONUtil.readValue(
			faroProject.getSubscription(), FaroSubscriptionDisplay.class);

		if (StringUtil.equals(
				faroSubscriptionDisplay.getName(),
				ProductConstants.BASIC_PRODUCT_NAME) ||
			StringUtil.equals(
				faroSubscriptionDisplay.getName(),
				ProductConstants.BUSINESS_PRODUCT_NAME) ||
			StringUtil.equals(
				faroSubscriptionDisplay.getName(),
				ProductConstants.ENTERPRISE_PRODUCT_NAME)) {

			return true;
		}

		return false;
	}

	private void _validateCorpProjectUuid(String corpProjectUuid)
		throws Exception {

		if (isOmniadmin() || corpProjectUuid.contains("Test")) {
			return;
		}

		User user = getUser();

		List<OSBAccountEntry> osbAccountEntries =
			_provisioningClient.getOSBAccountEntries(
				user.getUserUuid(), ProductConstants.getProductEntryIds());

		for (OSBAccountEntry osbAccountEntry : osbAccountEntries) {
			if (StringUtil.equals(
					osbAccountEntry.getCorpProjectUuid(), corpProjectUuid)) {

				return;
			}
		}

		throw new FaroException(
			"You do not have the required permissions",
			Response.Status.FORBIDDEN);
	}

	private void _validateFriendlyURL(String friendlyURL) {
		if ((friendlyURL == null) || Validator.isBlank(friendlyURL)) {
			return;
		}

		if (friendlyURL.matches("^/\\d*$")) {
			throw new FaroValidationException(
				"friendlyURL", _getFriendlyURLErrorMessage(0));
		}
	}

	private void _validateIncidentReportEmailAddresses(
		List<String> incidentReportEmailAddresses) {

		for (String incidentReportEmailAddress : incidentReportEmailAddresses) {
			if (!Validator.isEmailAddress(incidentReportEmailAddress)) {
				throw new FaroValidationException(
					"incidentReportEmailAddresses",
					_getIncidentReportEmailAddressesErrorMessage());
			}
		}
	}

	private void _validateLastSeenDate(FaroProject faroProject) {
		Date lastSeenDate = contactsEngineClient.getLastSeenDate(faroProject);

		if (Objects.isNull(lastSeenDate)) {
			return;
		}

		Calendar calendar = new GregorianCalendar();

		calendar.setTime(new Date());

		calendar.add(Calendar.DATE, -3);

		if (lastSeenDate.after(calendar.getTime())) {
			throw new FaroValidationException(
				"lastSeenDate", _getDeletionFailedErrorMessage(getUser()));
		}
	}

	private void _validateTimeZoneId(String timeZoneId) {
		if (!TimeZoneUtil.validate(timeZoneId)) {
			throw new FaroValidationException(
				"timeZoneId", _getTimeZoneIdErrorMessage(getUser()));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProjectController.class);

	private static final CopyOnWriteArraySet<Long> _initializingGroupIds =
		new CopyOnWriteArraySet<>();

	@Reference
	private ContactsCardTemplateLocalService _contactsCardTemplateLocalService;

	@Reference
	private ContactsLayoutTemplateLocalService
		_contactsLayoutTemplateLocalService;

	@Reference
	private FaroNotificationLocalService _faroNotificationLocalService;

	@Reference
	private FaroProjectEmailDomainLocalService
		_faroProjectEmailDomainLocalService;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private FaroUserLocalService _faroUserLocalService;

	@Reference
	private FieldMappingController _fieldMappingController;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile ProvisioningClient _provisioningClient;

	@Reference
	private RoleLocalService _roleLocalService;

}