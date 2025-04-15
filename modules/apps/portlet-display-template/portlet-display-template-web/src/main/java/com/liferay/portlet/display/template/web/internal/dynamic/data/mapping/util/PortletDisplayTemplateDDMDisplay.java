/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.display.template.web.internal.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.util.BaseDDMDisplay;
import com.liferay.dynamic.data.mapping.util.DDMDisplay;
import com.liferay.dynamic.data.mapping.util.DDMDisplayTabItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portlet.display.template.PortletDisplayTemplate;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = "jakarta.portlet.name=" + PortletKeys.PORTLET_DISPLAY_TEMPLATE,
	service = DDMDisplay.class
)
public class PortletDisplayTemplateDDMDisplay extends BaseDDMDisplay {

	@Override
	public String getEditTemplateBackURL(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, long classNameId,
			long classPK, long resourceClassNameId, String portletResource)
		throws Exception {

		return getViewTemplatesURL(
			liferayPortletRequest, liferayPortletResponse, classNameId, classPK,
			resourceClassNameId);
	}

	@Override
	public String getPortletId() {
		return PortletKeys.PORTLET_DISPLAY_TEMPLATE;
	}

	@Override
	public List<DDMDisplayTabItem> getTabItems() {
		return Arrays.asList();
	}

	@Override
	public long[] getTemplateClassPKs(
			long companyId, long classNameId, long classPK)
		throws Exception {

		return null;
	}

	@Override
	public long[] getTemplateGroupIds(
			ThemeDisplay themeDisplay, boolean includeAncestorTemplates)
		throws Exception {

		if (includeAncestorTemplates) {
			return _portal.getCurrentAndAncestorSiteGroupIds(
				themeDisplay.getScopeGroupId());
		}

		return new long[] {
			portletDisplayTemplate.getDDMTemplateGroupId(
				themeDisplay.getScopeGroupId())
		};
	}

	@Override
	public String getTemplateType() {
		return DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY;
	}

	@Override
	public String getTemplateType(DDMTemplate template, Locale locale) {
		String type = template.getType();

		if (!type.equals(DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY)) {
			return type;
		}

		TemplateHandler templateHandler =
			TemplateHandlerRegistryUtil.getTemplateHandler(
				template.getClassNameId());

		return templateHandler.getName(locale);
	}

	@Override
	public String getViewTemplatesBackURL(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, long classPK)
		throws Exception {

		return StringPool.BLANK;
	}

	@Override
	public Set<String> getViewTemplatesExcludedColumnNames() {
		return _viewTemplateExcludedColumnNames;
	}

	@Override
	public String getViewTemplatesTitle(
		DDMStructure structure, boolean controlPanel, boolean search,
		Locale locale) {

		if (search) {
			return _language.get(locale, "templates");
		}

		if (controlPanel) {
			return StringPool.BLANK;
		}

		return super.getViewTemplatesTitle(
			structure, controlPanel, search, locale);
	}

	@Override
	public boolean isShowBackURLInTitleBar() {
		return true;
	}

	@Override
	protected String getDefaultEditTemplateTitle(Locale locale) {
		return _language.get(locale, "new-widget-template");
	}

	@Override
	protected String getDefaultViewTemplateTitle(Locale locale) {
		return _language.get(locale, "widget-templates");
	}

	@Override
	protected String getViewTemplatesURL(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, long classNameId,
			long classPK, long resourceClassNameId)
		throws Exception {

		String portletName = liferayPortletRequest.getPortletName();

		PortletURL portletURL = null;

		if (portletName.equals(PortletKeys.PORTLET_DISPLAY_TEMPLATE)) {
			portletURL = _portal.getControlPanelPortletURL(
				liferayPortletRequest, PortletKeys.PORTLET_DISPLAY_TEMPLATE,
				PortletRequest.RENDER_PHASE);
		}
		else {
			portletURL = PortletURLBuilder.createRenderURL(
				liferayPortletResponse
			).setMVCPath(
				"/view_template.jsp"
			).setParameter(
				"classNameId", classNameId
			).setParameter(
				"classPK", classPK
			).setParameter(
				"groupId", _portal.getScopeGroupId(liferayPortletRequest)
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildPortletURL();
		}

		return portletURL.toString();
	}

	@Reference
	protected PortletDisplayTemplate portletDisplayTemplate;

	private static final Set<String> _viewTemplateExcludedColumnNames =
		SetUtil.fromArray("language", "mode", "structure");

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}