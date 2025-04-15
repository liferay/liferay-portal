/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.layout.admin.web.internal.util.LayoutPageTemplatePortletUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.LayoutTypeControllerTracker;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jürgen Kappler
 */
public class SelectLayoutPageTemplateEntryDisplayContext {

	public SelectLayoutPageTemplateEntryDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getBackURL() {
		if (_backURL != null) {
			return _backURL;
		}

		String backURL = ParamUtil.getString(_httpServletRequest, "backURL");

		if (Validator.isNull(backURL)) {
			backURL = getRedirect();
		}

		_backURL = backURL;

		return _backURL;
	}

	public Map<String, Object> getComponentContext() {
		return HashMapBuilder.<String, Object>put(
			"eventName",
			() -> {
				String eventName = ParamUtil.getString(
					_httpServletRequest, "eventName",
					_liferayPortletResponse.getNamespace() +
						"selectMasterLayout");

				return HtmlUtil.escape(eventName);
			}
		).put(
			"selector", ".select-master-layout-option"
		).build();
	}

	public List<LayoutPageTemplateEntry> getGlobalLayoutPageTemplateEntries() {
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator =
			LayoutPageTemplatePortletUtil.
				getLayoutPageTemplateEntryOrderByComparator("name", "asc");

		return LayoutPageTemplateEntryServiceUtil.getLayoutPageTemplateEntries(
			_themeDisplay.getCompanyGroupId(),
			LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE,
			WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, orderByComparator);
	}

	public int getGlobalLayoutPageTemplateEntriesCount() {
		return LayoutPageTemplateEntryServiceUtil.
			getLayoutPageTemplateEntriesCount(
				_themeDisplay.getCompanyGroupId(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE,
				WorkflowConstants.STATUS_APPROVED);
	}

	public long getLayoutPageTemplateCollectionId() {
		if (_layoutPageTemplateCollectionId != null) {
			return _layoutPageTemplateCollectionId;
		}

		_layoutPageTemplateCollectionId = ParamUtil.getLong(
			_httpServletRequest, "layoutPageTemplateCollectionId");

		return _layoutPageTemplateCollectionId;
	}

	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		int start, int end) {

		return LayoutPageTemplateEntryServiceUtil.getLayoutPageTemplateEntries(
			_themeDisplay.getScopeGroupId(),
			getLayoutPageTemplateCollectionId(),
			WorkflowConstants.STATUS_APPROVED, start, end);
	}

	public int getLayoutPageTemplateEntriesCount() {
		return LayoutPageTemplateEntryServiceUtil.
			getLayoutPageTemplateEntriesCount(
				_themeDisplay.getScopeGroupId(),
				getLayoutPageTemplateCollectionId(),
				WorkflowConstants.STATUS_APPROVED);
	}

	public Map<String, Object> getLayoutPageTemplateEntryCardProps(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return HashMapBuilder.<String, Object>put(
			"addLayoutURL",
			_getLayoutPageTemplateEntryAddLayoutURL(layoutPageTemplateEntry)
		).put(
			"getLayoutPageTemplateEntryListURL",
			() -> {
				LiferayPortletURL getLayoutPageTemplateEntryListURL =
					(LiferayPortletURL)
						_liferayPortletResponse.createResourceURL();

				getLayoutPageTemplateEntryListURL.
					setCopyCurrentRenderParameters(false);
				getLayoutPageTemplateEntryListURL.setParameter(
					"layoutPageTemplateCollectionId",
					String.valueOf(getLayoutPageTemplateCollectionId()));
				getLayoutPageTemplateEntryListURL.setResourceID(
					"/layout_admin/get_layout_page_template_entry_list");

				return getLayoutPageTemplateEntryListURL.toString();
			}
		).put(
			"layoutPageTemplateEntryId",
			String.valueOf(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId())
		).put(
			"subtitle",
			() -> {
				if (Objects.equals(
						layoutPageTemplateEntry.getType(),
						LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)) {

					return LanguageUtil.get(
						_httpServletRequest, "widget-page-template");
				}

				return LanguageUtil.get(
					_httpServletRequest, "content-page-template");
			}
		).put(
			"thumbnailURL",
			layoutPageTemplateEntry.getImagePreviewURL(_themeDisplay)
		).put(
			"title", HtmlUtil.escape(layoutPageTemplateEntry.getName())
		).build();
	}

	public List<LayoutPageTemplateEntry> getMasterLayoutPageTemplateEntries() {
		List<LayoutPageTemplateEntry> masterLayoutPageTemplateEntries =
			new ArrayList<>();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				createLayoutPageTemplateEntry(0);

		layoutPageTemplateEntry.setName(
			LanguageUtil.get(_httpServletRequest, "blank"));
		layoutPageTemplateEntry.setStatus(WorkflowConstants.STATUS_APPROVED);

		masterLayoutPageTemplateEntries.add(layoutPageTemplateEntry);

		Group scopeGroup = _themeDisplay.getScopeGroup();

		long scopeGroupId = _themeDisplay.getScopeGroupId();

		if (scopeGroup.isLayoutPrototype()) {
			LayoutPageTemplateEntry layoutPrototypeLayoutPageTemplateEntry =
				LayoutPageTemplateEntryLocalServiceUtil.
					fetchFirstLayoutPageTemplateEntry(scopeGroup.getClassPK());

			scopeGroupId = layoutPrototypeLayoutPageTemplateEntry.getGroupId();
		}

		masterLayoutPageTemplateEntries.addAll(
			LayoutPageTemplateEntryServiceUtil.getLayoutPageTemplateEntries(
				scopeGroupId,
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null));

		return masterLayoutPageTemplateEntries;
	}

	public long getMasterLayoutPlid() {
		if (_masterLayoutPlid != null) {
			return _masterLayoutPlid;
		}

		_masterLayoutPlid = ParamUtil.getLong(
			_httpServletRequest, "masterLayoutPlid");

		return _masterLayoutPlid;
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		return _redirect;
	}

	public String getSelectedTab() {
		if (_selectedTab != null) {
			return _selectedTab;
		}

		_selectedTab = ParamUtil.getString(
			_httpServletRequest, "selectedTab", "basic-templates");

		return _selectedTab;
	}

	public String getType() {
		if (_type != null) {
			return _type;
		}

		_type = ParamUtil.getString(_httpServletRequest, "type");

		return _type;
	}

	public List<String> getTypes() {
		if (_types != null) {
			return _types;
		}

		_types = ListUtil.filter(
			ListUtil.fromArray(LayoutTypeControllerTracker.getTypes()),
			type -> {
				LayoutTypeController layoutTypeController =
					LayoutTypeControllerTracker.getLayoutTypeController(type);

				return layoutTypeController.isInstanceable() &&
					   !layoutTypeController.isPrimaryType();
			});

		return _types;
	}

	public int getTypesCount() {
		List<String> types = getTypes();

		return types.size();
	}

	public boolean isBasicTemplates() {
		if ((getLayoutPageTemplateCollectionId() != 0) ||
			!Objects.equals(getSelectedTab(), "basic-templates")) {

			return false;
		}

		return true;
	}

	public boolean isContentPages() {
		if (getLayoutPageTemplateCollectionId() != 0) {
			return true;
		}

		return false;
	}

	public boolean isGlobalTemplates() {
		if ((getLayoutPageTemplateCollectionId() != 0) ||
			!Objects.equals(getSelectedTab(), "global-templates")) {

			return false;
		}

		return true;
	}

	private String _getLayoutPageTemplateEntryAddLayoutURL(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/layout_admin/add_layout"
		).setBackURL(
			ParamUtil.getString(_httpServletRequest, "redirect")
		).setParameter(
			"layoutPageTemplateEntryId",
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
		).setParameter(
			"privateLayout",
			ParamUtil.getBoolean(_httpServletRequest, "privateLayout")
		).setParameter(
			"selPlid", ParamUtil.getLong(_httpServletRequest, "selPlid")
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private String _backURL;
	private final HttpServletRequest _httpServletRequest;
	private Long _layoutPageTemplateCollectionId;
	private final LiferayPortletResponse _liferayPortletResponse;
	private Long _masterLayoutPlid;
	private String _redirect;
	private String _selectedTab;
	private final ThemeDisplay _themeDisplay;
	private String _type;
	private List<String> _types;

}