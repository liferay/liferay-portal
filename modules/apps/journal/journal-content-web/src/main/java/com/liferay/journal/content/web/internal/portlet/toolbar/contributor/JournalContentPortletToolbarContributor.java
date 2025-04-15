/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.portlet.toolbar.contributor;

import com.liferay.dynamic.data.mapping.item.selector.DDMStructureItemSelectorCriterion;
import com.liferay.dynamic.data.mapping.item.selector.DDMStructureItemSelectorReturnType;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.util.comparator.StructureCreateDateComparator;
import com.liferay.item.selector.ItemSelector;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.content.web.internal.configuration.JournalContentPortletInstanceConfiguration;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.UnicodeLanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.toolbar.contributor.BasePortletToolbarContributor;
import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.JavaScriptMenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.URLMenuItem;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalContentPortletKeys.JOURNAL_CONTENT,
		"mvc.path=-", "mvc.path=/view.jsp"
	},
	service = PortletToolbarContributor.class
)
public class JournalContentPortletToolbarContributor
	extends BasePortletToolbarContributor {

	@Override
	protected List<MenuItem> getPortletTitleMenuItems(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (!_hasAddArticlePermission(themeDisplay) ||
			layout.isLayoutPrototypeLinkActive()) {

			return Collections.emptyList();
		}

		List<MenuItem> menuItems = new ArrayList<>();

		try {
			_addPortletTitleAddJournalArticleMenuItems(
				menuItems, themeDisplay, portletRequest, portletResponse);
		}
		catch (Exception exception) {
			_log.error("Unable to add folder menu item", exception);
		}

		return menuItems;
	}

	private void _addPortletTitleAddJournalArticleMenuItems(
			List<MenuItem> menuItems, ThemeDisplay themeDisplay,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		List<DDMStructure> ddmStructures = null;

		long[] currentAndAncestorSiteGroupIds =
			_portal.getCurrentAndAncestorSiteGroupIds(
				themeDisplay.getScopeGroupId());

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PortletURL portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				portletRequest, JournalPortletKeys.JOURNAL,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/journal/edit_article"
		).setRedirect(
			_portal.getLayoutFullURL(themeDisplay)
		).setPortletResource(
			portletDisplay.getId()
		).setParameter(
			"groupId", themeDisplay.getScopeGroupId()
		).setParameter(
			"refererPlid", themeDisplay.getPlid()
		).buildPortletURL();

		JournalContentPortletInstanceConfiguration
			journalContentPortletInstanceConfiguration =
				_configurationProvider.getPortletInstanceConfiguration(
					JournalContentPortletInstanceConfiguration.class,
					themeDisplay);

		if (journalContentPortletInstanceConfiguration.
				sortStructuresByByName()) {

			Locale locale = themeDisplay.getLocale();

			ddmStructures = ListUtil.sort(
				_ddmStructureService.getStructures(
					themeDisplay.getCompanyId(), currentAndAncestorSiteGroupIds,
					_portal.getClassNameId(JournalArticle.class),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					StructureCreateDateComparator.getInstance(false)),
				Comparator.comparing(
					ddmStructure -> ddmStructure.getName(locale)));

			ddmStructures = ddmStructures.subList(
				0, Math.min(ddmStructures.size(), _DEFAULT_MAX_DISPLAY_ITEMS));
		}
		else {
			ddmStructures = _ddmStructureService.getStructures(
				themeDisplay.getCompanyId(), currentAndAncestorSiteGroupIds,
				_portal.getClassNameId(JournalArticle.class), 0,
				_DEFAULT_MAX_DISPLAY_ITEMS,
				StructureCreateDateComparator.getInstance(false));
		}

		for (DDMStructure ddmStructure : ddmStructures) {
			portletURL.setParameter(
				"ddmStructureId",
				String.valueOf(ddmStructure.getStructureId()));

			URLMenuItem urlMenuItem = new URLMenuItem();

			urlMenuItem.setData(
				HashMapBuilder.<String, Object>put(
					"id",
					HtmlUtil.escape(portletDisplay.getNamespace()) + "editAsset"
				).put(
					"title",
					HtmlUtil.escape(
						_language.format(
							themeDisplay.getLocale(), "new-x",
							ddmStructure.getName(themeDisplay.getLocale())))
				).build());
			urlMenuItem.setLabel(
				ddmStructure.getUnambiguousName(
					ddmStructures, themeDisplay.getScopeGroupId(),
					themeDisplay.getLocale()));
			urlMenuItem.setURL(
				HttpComponentsUtil.addParameter(
					portletURL.toString(), "refererPlid",
					themeDisplay.getPlid()));

			menuItems.add(urlMenuItem);
		}

		int count = _ddmStructureService.getStructuresCount(
			themeDisplay.getCompanyId(), currentAndAncestorSiteGroupIds,
			_portal.getClassNameId(JournalArticle.class));

		if (count > _DEFAULT_MAX_DISPLAY_ITEMS) {
			MenuItem menuItem = menuItems.get(menuItems.size() - 1);

			menuItem.setSeparator(true);

			JavaScriptMenuItem javaScriptMenuItem = new JavaScriptMenuItem();

			javaScriptMenuItem.setLabel(
				_language.get(
					_portal.getHttpServletRequest(portletRequest),
					"show-more"));
			javaScriptMenuItem.setOnClick(
				StringBundler.concat(
					"Liferay.Util.openSelectionModal({id: '",
					portletResponse.getNamespace(), "selectDDMStructure', ",
					"onSelect: function (selectedItem) {if (selectedItem) {",
					"const itemValue = JSON.parse(selectedItem.value);",
					"Liferay.Util.navigate(Liferay.Util.addParams({",
					_portal.getPortletNamespace(JournalPortletKeys.JOURNAL),
					"ddmStructureId: itemValue.ddmstructureid}, '",
					_getEditJournalArticleURL(
						portletDisplay, portletRequest, themeDisplay),
					"'));}}, selectEventName: '",
					portletResponse.getNamespace(),
					"selectDDMStructure', title: '",
					UnicodeLanguageUtil.get(
						_portal.getHttpServletRequest(portletRequest),
						"select-structure"),
					"', url: '",
					_getSelectDDMStructureURL(portletRequest, portletResponse),
					"'});"));

			menuItems.add(javaScriptMenuItem);
		}
	}

	private String _getEditJournalArticleURL(
			PortletDisplay portletDisplay, PortletRequest portletRequest,
			ThemeDisplay themeDisplay)
		throws Exception {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				portletRequest, JournalPortletKeys.JOURNAL,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/journal/edit_article"
		).setRedirect(
			_portal.getLayoutFullURL(themeDisplay)
		).setPortletResource(
			portletDisplay.getId()
		).setParameter(
			"groupId", themeDisplay.getScopeGroupId()
		).setParameter(
			"refererPlid", themeDisplay.getPlid()
		).setGlobalParameter(
			"refererPlid", themeDisplay.getPlid()
		).buildString();
	}

	private String _getSelectDDMStructureURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		DDMStructureItemSelectorCriterion ddmStructureItemSelectorCriterion =
			new DDMStructureItemSelectorCriterion();

		ddmStructureItemSelectorCriterion.setClassNameId(
			_portal.getClassNameId(JournalArticle.class));
		ddmStructureItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new DDMStructureItemSelectorReturnType());

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(portletRequest),
				portletResponse.getNamespace() + "selectDDMStructure",
				ddmStructureItemSelectorCriterion));
	}

	private boolean _hasAddArticlePermission(ThemeDisplay themeDisplay) {
		boolean hasResourcePermission = _portletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), themeDisplay.getScopeGroupId(),
			ActionKeys.ADD_ARTICLE);

		boolean hasPortletPermission = false;

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		try {
			hasPortletPermission = PortletPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), themeDisplay.getLayout(),
				portletDisplay.getId(), ActionKeys.CONFIGURATION);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to check Journal Content portlet permission",
					portalException);
			}
		}

		boolean hasAddArticlePermission = false;

		if (hasResourcePermission && hasPortletPermission) {
			hasAddArticlePermission = true;
		}

		return hasAddArticlePermission;
	}

	private static final int _DEFAULT_MAX_DISPLAY_ITEMS = GetterUtil.getInteger(
		PropsUtil.get(PropsKeys.MENU_MAX_DISPLAY_ITEMS));

	private static final Log _log = LogFactoryUtil.getLog(
		JournalContentPortletToolbarContributor.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(resource.name=" + JournalConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}