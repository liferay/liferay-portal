/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.LayoutSetService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SITE_SETTINGS,
		"mvc.command.name=/site_admin/edit_site_url"
	},
	service = MVCActionCommand.class
)
public class EditSiteURLMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long liveGroupId = ParamUtil.getLong(actionRequest, "liveGroupId");

		Group liveGroup = _groupLocalService.getGroup(liveGroupId);

		String friendlyURL = ParamUtil.getString(
			actionRequest, "groupFriendlyURL", liveGroup.getFriendlyURL());

		boolean redirect = false;

		if ((themeDisplay.getScopeGroupId() == liveGroup.getGroupId()) &&
			!Objects.equals(friendlyURL, liveGroup.getFriendlyURL())) {

			redirect = true;
		}

		liveGroup = _groupService.updateFriendlyURL(
			liveGroup.getGroupId(), friendlyURL);

		Set<Locale> availableLocales = _language.getAvailableLocales(
			liveGroup.getGroupId());

		_layoutSetService.updateVirtualHosts(
			liveGroup.getGroupId(), false,
			ActionUtil.toTreeMap(
				actionRequest, "publicVirtualHost", availableLocales));

		_layoutSetService.updateVirtualHosts(
			liveGroup.getGroupId(), true,
			ActionUtil.toTreeMap(
				actionRequest, "privateVirtualHost", availableLocales));

		if (liveGroup.hasStagingGroup()) {
			Group stagingGroup = liveGroup.getStagingGroup();

			friendlyURL = ParamUtil.getString(
				actionRequest, "stagingFriendlyURL",
				stagingGroup.getFriendlyURL());

			if ((themeDisplay.getScopeGroupId() == stagingGroup.getGroupId()) &&
				!Objects.equals(friendlyURL, stagingGroup.getFriendlyURL())) {

				redirect = true;
			}

			_groupService.updateFriendlyURL(
				stagingGroup.getGroupId(), friendlyURL);

			_layoutSetService.updateVirtualHosts(
				stagingGroup.getGroupId(), false,
				ActionUtil.toTreeMap(
					actionRequest, "stagingPublicVirtualHost",
					availableLocales));

			_layoutSetService.updateVirtualHosts(
				stagingGroup.getGroupId(), true,
				ActionUtil.toTreeMap(
					actionRequest, "stagingPrivateVirtualHost",
					availableLocales));
		}

		if (!redirect) {
			return;
		}

		actionRequest.setAttribute(
			WebKeys.REDIRECT,
			_getSiteAdministrationURL(liveGroup, themeDisplay));
	}

	private String _getSiteAdministrationURL(
			Group group, ThemeDisplay themeDisplay)
		throws Exception {

		Group scopeGroup = themeDisplay.getScopeGroup();

		if (scopeGroup.isStagingGroup()) {
			group = group.getStagingGroup();
		}

		String siteAdministrationURL = _portal.getSiteAdminURL(
			themeDisplay.getPortalURL(), group,
			ConfigurationAdminPortletKeys.SITE_SETTINGS, null);

		String namespace = _portal.getPortletNamespace(
			ConfigurationAdminPortletKeys.SITE_SETTINGS);

		siteAdministrationURL = HttpComponentsUtil.addParameter(
			siteAdministrationURL, namespace + "mvcRenderCommandName",
			"/configuration_admin/view_configuration_screen");

		String configurationScreenKey = "site-configuration-site-url";

		if (group.isPrivateLayoutsEnabled()) {
			configurationScreenKey =
				"site-configuration-public-private-site-url";
		}

		return HttpComponentsUtil.addParameter(
			siteAdministrationURL, namespace + "configurationScreenKey",
			configurationScreenKey);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupService _groupService;

	@Reference
	private Language _language;

	@Reference
	private LayoutSetService _layoutSetService;

	@Reference
	private Portal _portal;

}