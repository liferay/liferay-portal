/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.display.context;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemListBuilder;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferenceValueTable;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.PortletPreferencesTable;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.PortletDisplayTemplate;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class WidgetTemplatesTemplateViewUsagesDisplayContext {

	public WidgetTemplatesTemplateViewUsagesDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public DDMTemplate getDDMTemplate() {
		if (_ddmTemplate != null) {
			return _ddmTemplate;
		}

		_ddmTemplate = DDMTemplateLocalServiceUtil.fetchDDMTemplate(
			getDDMTemplateId());

		return _ddmTemplate;
	}

	public long getDDMTemplateId() {
		if (_ddmTemplateId != null) {
			return _ddmTemplateId;
		}

		_ddmTemplateId = ParamUtil.getLong(_renderRequest, "ddmTemplateId");

		return _ddmTemplateId;
	}

	public String getDDMTemplateUsageName(Layout layout) {
		if (layout == null) {
			return StringPool.DASH;
		}

		String ddmTemplateUsageName = layout.getName(_themeDisplay.getLocale());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_fetchLayoutPageTemplateEntry(layout);

		if (layoutPageTemplateEntry != null) {
			ddmTemplateUsageName = layoutPageTemplateEntry.getName();
		}

		if (layout.isDraftLayout()) {
			return StringBundler.concat(
				ddmTemplateUsageName, " (",
				LanguageUtil.get(_themeDisplay.getLocale(), "draft"), ")");
		}

		return ddmTemplateUsageName;
	}

	public String getDDMTemplateUsageType(Layout layout) {
		if (layout == null) {
			return "embedded";
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_fetchLayoutPageTemplateEntry(layout);

		if (layoutPageTemplateEntry != null) {
			if (layoutPageTemplateEntry.getType() ==
					LayoutPageTemplateEntryTypeConstants.BASIC) {

				return "page-template";
			}
			else if (layoutPageTemplateEntry.getType() ==
						LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE) {

				return "display-page-template";
			}
			else if (layoutPageTemplateEntry.getType() ==
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT) {

				return "master-page";
			}
		}

		return "page";
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_renderRequest, "redirect");

		return _redirect;
	}

	public int getUsagesCount() {
		return PortletPreferenceValueLocalServiceUtil.
			getPortletPreferenceValuesCount(
				_ddmTemplate.getCompanyId(), "displayStyle",
				PortletDisplayTemplate.DISPLAY_STYLE_PREFIX +
					HtmlUtil.escape(_ddmTemplate.getTemplateKey()));
	}

	public VerticalNavItemList getVerticalItemList() {
		return VerticalNavItemListBuilder.add(
			verticalNavItem -> {
				verticalNavItem.setActive(true);
				verticalNavItem.setId(
					LanguageUtil.format(
						_httpServletRequest, "all-x", getUsagesCount(), false));
				verticalNavItem.setLabel(
					LanguageUtil.format(
						_httpServletRequest, "all-x", getUsagesCount(), false));
			}
		).build();
	}

	public SearchContainer<PortletPreferences>
		getWidgetTemplatesUsagesSearchContainer() {

		if (_widgetTemplatesUsagesSearchContainer != null) {
			return _widgetTemplatesUsagesSearchContainer;
		}

		SearchContainer<PortletPreferences>
			widgetTemplatesUsagesSearchContainer = new SearchContainer<>(
				_renderRequest, _renderResponse.createRenderURL(), null,
				"there-are-no-usages");

		DDMTemplate ddmTemplate = getDDMTemplate();

		widgetTemplatesUsagesSearchContainer.setResultsAndTotal(
			() -> _getWidgetTemplatesUsages(
				widgetTemplatesUsagesSearchContainer.getStart(),
				widgetTemplatesUsagesSearchContainer.getEnd()),
			PortletPreferenceValueLocalServiceUtil.
				getPortletPreferenceValuesCount(
					ddmTemplate.getCompanyId(), "displayStyle",
					PortletDisplayTemplate.DISPLAY_STYLE_PREFIX +
						HtmlUtil.escape(ddmTemplate.getTemplateKey())));

		_widgetTemplatesUsagesSearchContainer =
			widgetTemplatesUsagesSearchContainer;

		return _widgetTemplatesUsagesSearchContainer;
	}

	private LayoutPageTemplateEntry _fetchLayoutPageTemplateEntry(
		Layout layout) {

		long layoutPageTemplateEntryPlid = layout.getPlid();

		if (layout.isDraftLayout()) {
			layoutPageTemplateEntryPlid = layout.getClassPK();
		}

		return LayoutPageTemplateEntryLocalServiceUtil.
			fetchLayoutPageTemplateEntryByPlid(layoutPageTemplateEntryPlid);
	}

	private List<PortletPreferences> _getWidgetTemplatesUsages(
		int start, int end) {

		DDMTemplate ddmTemplate = getDDMTemplate();

		return PortletPreferencesLocalServiceUtil.dslQuery(
			DSLQueryFactoryUtil.select(
				PortletPreferencesTable.INSTANCE
			).from(
				PortletPreferencesTable.INSTANCE
			).innerJoinON(
				PortletPreferenceValueTable.INSTANCE,
				PortletPreferencesTable.INSTANCE.portletPreferencesId.eq(
					PortletPreferenceValueTable.INSTANCE.portletPreferencesId)
			).where(
				PortletPreferenceValueTable.INSTANCE.companyId.eq(
					_themeDisplay.getCompanyId()
				).and(
					PortletPreferenceValueTable.INSTANCE.name.eq("displayStyle")
				).and(
					PortletPreferenceValueTable.INSTANCE.smallValue.eq(
						PortletDisplayTemplate.DISPLAY_STYLE_PREFIX +
							HtmlUtil.escape(ddmTemplate.getTemplateKey()))
				)
			).limit(
				start, end
			));
	}

	private DDMTemplate _ddmTemplate;
	private Long _ddmTemplateId;
	private final HttpServletRequest _httpServletRequest;
	private String _redirect;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;
	private SearchContainer<PortletPreferences>
		_widgetTemplatesUsagesSearchContainer;

}