/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.osb.faro.model.FaroPreferences;
import com.liferay.osb.faro.service.FaroPreferencesLocalService;
import com.liferay.osb.faro.web.internal.constants.FaroPreferencesConstants;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.model.display.contacts.FaroPreferencesDisplay;
import com.liferay.osb.faro.web.internal.model.preferences.DistributionCardTabPreferences;
import com.liferay.osb.faro.web.internal.model.preferences.DistributionCardTabsPreferences;
import com.liferay.osb.faro.web.internal.model.preferences.EmailReportPreferences;
import com.liferay.osb.faro.web.internal.model.preferences.IndividualDashboardPreferences;
import com.liferay.osb.faro.web.internal.model.preferences.IndividualSegmentPreferences;
import com.liferay.osb.faro.web.internal.model.preferences.WorkspacePreferences;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.osb.faro.web.internal.util.EmailReportHelper;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(service = {FaroController.class, PreferencesController.class})
@Path("/{groupId}/preferences")
@Produces(MediaType.APPLICATION_JSON)
public class PreferencesController extends BaseFaroController {

	@Path("/distribution_tabs")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public DistributionCardTabsPreferences addDistributionTabPreferences(
			@PathParam("groupId") long groupId,
			@FormParam("distributionCardTabPreferences") FaroParam
				<Map<String, Object>> distributionCardTabPreferencesFaroParam,
			@FormParam("individualSegmentId") String individualSegmentId,
			@FormParam("distributionTabId") String distributionTabId,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@FormParam("scope")
			String scope)
		throws Exception {

		long ownerId = _getOwnerId(groupId, scope);

		WorkspacePreferences workspacePreferences = _getWorkspacePreferences(
			groupId, ownerId);

		workspacePreferences.addDistributionCardTabPreferences(
			distributionTabId, individualSegmentId,
			JSONUtil.convertValue(
				distributionCardTabPreferencesFaroParam.getValue(),
				DistributionCardTabPreferences.class));

		_faroPreferencesLocalService.savePreferences(
			getUserId(), groupId, ownerId,
			JSONUtil.writeValueAsString(workspacePreferences));

		return workspacePreferences.getDistributionCardTabsPreferences(
			individualSegmentId);
	}

	@Path("/email_report")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public EmailReportPreferences addEmailReportPreference(
			@PathParam("groupId") long groupId,
			@FormParam("channelId") String channelId,
			@FormParam("enabled") Boolean enabled,
			@FormParam("frequency") String frequency,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@FormParam("scope")
			String scope)
		throws Exception {

		long ownerId = _getOwnerId(groupId, scope);

		WorkspacePreferences workspacePreferences = _getWorkspacePreferences(
			groupId, ownerId);

		Map<String, EmailReportPreferences> emailReportPreferences =
			workspacePreferences.addEmailReportPreference(
				channelId, enabled, frequency);

		_faroPreferencesLocalService.savePreferences(
			getUserId(), groupId, ownerId,
			JSONUtil.writeValueAsString(workspacePreferences));

		return emailReportPreferences.get(channelId);
	}

	@GET
	@Path("/default_channel_id")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public Map<String, String> getDefaultChannelId(
			@PathParam("groupId") long groupId,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@QueryParam("scope")
			String scope)
		throws Exception {

		WorkspacePreferences workspacePreferences = _getWorkspacePreferences(
			groupId, _getOwnerId(groupId, scope));

		return Collections.singletonMap(
			"defaultChannelId", workspacePreferences.getDefaultChannelId());
	}

	@GET
	@Path("/distribution_tabs")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public DistributionCardTabsPreferences getDistributionTabsPreferences(
			@PathParam("groupId") long groupId,
			@QueryParam("individualSegmentId") String individualSegmentId,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@QueryParam("scope")
			String scope)
		throws Exception {

		WorkspacePreferences workspacePreferences = _getWorkspacePreferences(
			groupId, _getOwnerId(groupId, scope));

		if (Validator.isNull(individualSegmentId)) {
			IndividualDashboardPreferences individualDashboardPreferences =
				workspacePreferences.getIndividualDashboardPreferences();

			return individualDashboardPreferences.
				getDistributionCardTabsPreferences();
		}

		Map<String, IndividualSegmentPreferences>
			individualSegmentPreferencesMap =
				workspacePreferences.getIndividualSegmentPreferences();

		IndividualSegmentPreferences individualSegmentPreferences =
			individualSegmentPreferencesMap.getOrDefault(
				individualSegmentId, new IndividualSegmentPreferences());

		return individualSegmentPreferences.
			getDistributionCardTabsPreferences();
	}

	@GET
	@Path("/email_report")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public Map<String, EmailReportPreferences> getEmailReportPreferences(
			@PathParam("groupId") long groupId,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@QueryParam("scope")
			String scope)
		throws Exception {

		WorkspacePreferences workspacePreferences = _getWorkspacePreferences(
			groupId, _getOwnerId(groupId, scope));

		return workspacePreferences.getEmailReportPreferences(null);
	}

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroPreferencesDisplay getFaroPreferencesDisplay(
			@PathParam("groupId") long groupId,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@QueryParam("scope")
			String scope)
		throws Exception {

		long ownerId = _getOwnerId(groupId, scope);

		FaroPreferences faroPreferences =
			_faroPreferencesLocalService.fetchFaroPreferences(groupId, ownerId);

		if (faroPreferences == null) {
			return new FaroPreferencesDisplay(groupId, ownerId);
		}

		return new FaroPreferencesDisplay(faroPreferences);
	}

	@GET
	@Path("/upgrade_modal_seen")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public boolean isUpgradeModalSeen(
			@PathParam("groupId") long groupId,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@QueryParam("scope")
			String scope)
		throws Exception {

		WorkspacePreferences workspacePreferences = _getWorkspacePreferences(
			groupId, _getOwnerId(groupId, scope));

		return workspacePreferences.isUpgradeModalSeen();
	}

	@DELETE
	@Path("/distribution_tabs")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public DistributionCardTabsPreferences removeDistributionTabPreferences(
			@PathParam("groupId") long groupId,
			@FormParam("individualSegmentId") String individualSegmentId,
			@FormParam("distributionTabId") String distributionTabId,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@FormParam("scope")
			String scope)
		throws Exception {

		long ownerId = _getOwnerId(groupId, scope);

		WorkspacePreferences workspacePreferences = _getWorkspacePreferences(
			groupId, ownerId);

		workspacePreferences.removeDistributionCardTabPreferences(
			distributionTabId, individualSegmentId);

		_faroPreferencesLocalService.savePreferences(
			getUserId(), groupId, ownerId,
			JSONUtil.writeValueAsString(workspacePreferences));

		return workspacePreferences.getDistributionCardTabsPreferences(
			individualSegmentId);
	}

	public void removeIndividualSegmentPreferences(
			long groupId, String individualSegmentId, String scope)
		throws Exception {

		long ownerId = _getOwnerId(groupId, scope);

		WorkspacePreferences workspacePreferences = _getWorkspacePreferences(
			groupId, ownerId);

		workspacePreferences.removeIndividualSegmentPreference(
			individualSegmentId);

		_faroPreferencesLocalService.savePreferences(
			getUserId(), groupId, ownerId,
			JSONUtil.writeValueAsString(workspacePreferences));
	}

	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroPreferencesDisplay save(
			@PathParam("groupId") long groupId,
			@FormParam("preferences") FaroParam<Map<String, Object>>
				preferencesFaroParam,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@FormParam("scope")
			String scope)
		throws Exception {

		return new FaroPreferencesDisplay(
			_faroPreferencesLocalService.savePreferences(
				getUserId(), groupId, _getOwnerId(groupId, scope),
				JSONUtil.writeValueAsString(preferencesFaroParam.getValue())));
	}

	@Path("/default_channel_id")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public Map<String, String> saveDefaultChannelId(
			@PathParam("groupId") long groupId,
			@FormParam("defaultChannelId") String defaultChannelId,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@FormParam("scope")
			String scope)
		throws Exception {

		long ownerId = _getOwnerId(groupId, scope);

		WorkspacePreferences workspacePreferences = _getWorkspacePreferences(
			groupId, ownerId);

		workspacePreferences.setDefaultChannelId(defaultChannelId);

		_faroPreferencesLocalService.savePreferences(
			getUserId(), groupId, ownerId,
			JSONUtil.writeValueAsString(workspacePreferences));

		return Collections.singletonMap("defaultChannelId", defaultChannelId);
	}

	@Path("/upgrade_modal_seen")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public boolean saveUpgradeModalSeen(
			@PathParam("groupId") long groupId,
			@FormParam("upgradeModalSeen") Boolean upgradeModalSeen,
			@DefaultValue(FaroPreferencesConstants.SCOPE_USER)
			@QueryParam("scope")
			String scope)
		throws Exception {

		long ownerId = _getOwnerId(groupId, scope);

		WorkspacePreferences workspacePreferences = _getWorkspacePreferences(
			groupId, ownerId);

		workspacePreferences.setUpgradeModalSeen(upgradeModalSeen);

		_faroPreferencesLocalService.savePreferences(
			getUserId(), groupId, ownerId,
			JSONUtil.writeValueAsString(workspacePreferences));

		return upgradeModalSeen;
	}

	@Path("/send_email_report")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public boolean sendEmailReport(
			@PathParam("groupId") long groupId,
			@FormParam("channelId") String channelId,
			@FormParam("frequency") String frequency,
			@FormParam("userId") long userId)
		throws Exception {

		_emailReportHelper.sendEmail(channelId, frequency, groupId, userId);

		return true;
	}

	private long _getOwnerId(long groupId, String scope) throws Exception {
		if (StringUtil.equals(scope, FaroPreferencesConstants.SCOPE_GROUP)) {
			return groupId;
		}
		else if (StringUtil.equals(
					scope, FaroPreferencesConstants.SCOPE_USER)) {

			return getUserId();
		}

		throw new Exception("Invalid scope " + scope);
	}

	private WorkspacePreferences _getWorkspacePreferences(
			long groupId, long ownerId)
		throws Exception {

		FaroPreferences faroPreferences =
			_faroPreferencesLocalService.fetchFaroPreferences(groupId, ownerId);

		if (faroPreferences == null) {
			return new WorkspacePreferences();
		}

		return JSONUtil.readValue(
			faroPreferences.getPreferences(), WorkspacePreferences.class);
	}

	@Reference
	private EmailReportHelper _emailReportHelper;

	@Reference
	private FaroPreferencesLocalService _faroPreferencesLocalService;

}