/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.toolbar.item;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.toolbar.contributor.BasePortletToolbarContributor;
import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.URLMenuItem;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.wiki.configuration.WikiGroupServiceOverriddenConfiguration;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.constants.WikiWebKeys;
import com.liferay.wiki.exception.NoSuchNodeException;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.WikiNodeService;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI,
		"mvc.render.command.name=/wiki/view_pages"
	},
	service = PortletToolbarContributor.class
)
public class WikiPortletToolbarContributor
	extends BasePortletToolbarContributor {

	@Override
	protected List<MenuItem> getPortletTitleMenuItems(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		List<MenuItem> menuItems = new ArrayList<>();

		try {
			WikiNode node = _getNode(themeDisplay, portletRequest);

			if (node != null) {
				_addPortletTitleMenuItem(
					menuItems, node, themeDisplay, portletRequest);
			}
		}
		catch (PortalException portalException) {
			_log.error("Unable to add page menu item", portalException);
		}

		return menuItems;
	}

	private void _addPortletTitleMenuItem(
			List<MenuItem> menuItems, WikiNode node, ThemeDisplay themeDisplay,
			PortletRequest portletRequest)
		throws PortalException {

		if (!_wikiNodeModelResourcePermission.contains(
				themeDisplay.getPermissionChecker(), node,
				ActionKeys.ADD_PAGE)) {

			return;
		}

		URLMenuItem urlMenuItem = new URLMenuItem();

		urlMenuItem.setIcon("icon-plus-sign-2");
		urlMenuItem.setLabel(
			_language.get(
				_portal.getHttpServletRequest(portletRequest), "add-page"));

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		urlMenuItem.setURL(
			PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					portletRequest, portletDisplay.getId(),
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/wiki/edit_page"
			).setRedirect(
				_portal.getCurrentURL(portletRequest)
			).setParameter(
				"editTitle", "1"
			).setParameter(
				"nodeId", node.getNodeId()
			).setParameter(
				"title", StringPool.BLANK
			).buildString());

		menuItems.add(urlMenuItem);
	}

	private WikiNode _getNode(
		ThemeDisplay themeDisplay, PortletRequest portletRequest) {

		WikiNode node = (WikiNode)portletRequest.getAttribute(
			WikiWebKeys.WIKI_NODE);

		if (node != null) {
			return node;
		}

		String initialNodeName = StringPool.BLANK;

		try {
			WikiGroupServiceOverriddenConfiguration
				wikiGroupServiceOverriddenConfiguration =
					_configurationProvider.getConfiguration(
						WikiGroupServiceOverriddenConfiguration.class,
						new GroupServiceSettingsLocator(
							themeDisplay.getScopeGroupId(),
							WikiConstants.SERVICE_NAME));

			initialNodeName =
				wikiGroupServiceOverriddenConfiguration.initialNodeName();
		}
		catch (ConfigurationException configurationException) {
			_log.error(
				"Unable to get initial node name for group " +
					themeDisplay.getScopeGroupId(),
				configurationException);
		}

		String name = BeanParamUtil.getString(
			node, portletRequest, "name", initialNodeName);

		if (Validator.isNotNull(name)) {
			try {
				node = _wikiNodeService.getNode(
					themeDisplay.getScopeGroupId(), name);
			}
			catch (NoSuchNodeException noSuchNodeException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchNodeException);
				}

				node = null;
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}

		return node;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WikiPortletToolbarContributor.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(target = "(model.class.name=com.liferay.wiki.model.WikiNode)")
	private ModelResourcePermission<WikiNode> _wikiNodeModelResourcePermission;

	@Reference
	private WikiNodeService _wikiNodeService;

}