/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.taglib.servlet.taglib;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.theme.NavItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.portlet.display.template.util.PortletDisplayTemplateUtil;
import com.liferay.site.navigation.taglib.internal.servlet.ServletContextUtil;
import com.liferay.site.navigation.taglib.servlet.taglib.util.NavItemUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import java.util.List;
import java.util.Map;

/**
 * @author Pavel Savinov
 */
public class NavigationMenuTag extends IncludeTag {

	public long getDdmTemplateGroupId() {
		return _ddmTemplateGroupId;
	}

	public String getDdmTemplateKey() {
		return _ddmTemplateKey;
	}

	public int getDisplayDepth() {
		return _displayDepth;
	}

	public String getExpandedLevels() {
		return _expandedLevels;
	}

	public NavigationMenuMode getNavigationMenuMode() {
		return _navigationMenuMode;
	}

	public String getRootItemId() {
		return _rootItemId;
	}

	public int getRootItemLevel() {
		return _rootItemLevel;
	}

	public String getRootItemType() {
		return _rootItemType;
	}

	public long getSiteNavigationMenuId() {
		return _siteNavigationMenuId;
	}

	public boolean isPreview() {
		return _preview;
	}

	@Override
	public int processEndTag() throws Exception {
		DDMTemplate portletDisplayDDMTemplate =
			PortletDisplayTemplateUtil.getPortletDisplayTemplateDDMTemplate(
				getDisplayStyleGroupId(),
				ClassNameLocalServiceUtil.getClassNameId(NavItem.class),
				getDisplayStyle(), true);

		if (portletDisplayDDMTemplate == null) {
			return EVAL_PAGE;
		}

		JspWriter jspWriter = pageContext.getOut();

		HttpServletRequest httpServletRequest = getRequest();

		Map<String, Object> navigationMenuContext =
			NavItemUtil.getNavigationMenuContext(
				_displayDepth, _expandedLevels, httpServletRequest,
				_navigationMenuMode, _preview, _rootItemId, _rootItemLevel,
				_rootItemType, _siteNavigationMenuId);

		jspWriter.write(
			PortletDisplayTemplateUtil.renderDDMTemplate(
				httpServletRequest,
				(HttpServletResponse)pageContext.getResponse(),
				portletDisplayDDMTemplate.getTemplateId(),
				(List<NavItem>)navigationMenuContext.get("navItems"),
				navigationMenuContext));

		return EVAL_PAGE;
	}

	public void setDdmTemplateGroupId(long ddmTemplateGroupId) {
		_ddmTemplateGroupId = ddmTemplateGroupId;
	}

	public void setDdmTemplateKey(String ddmTemplateKey) {
		_ddmTemplateKey = ddmTemplateKey;
	}

	public void setDisplayDepth(int displayDepth) {
		_displayDepth = displayDepth;
	}

	public void setExpandedLevels(String expandedLevels) {
		_expandedLevels = expandedLevels;
	}

	public void setNavigationMenuMode(NavigationMenuMode navigationMenuMode) {
		_navigationMenuMode = navigationMenuMode;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPreview(boolean preview) {
		_preview = preview;
	}

	public void setRootItemId(String rootItemId) {
		_rootItemId = rootItemId;
	}

	public void setRootItemLevel(int rootItemLevel) {
		_rootItemLevel = rootItemLevel;
	}

	public void setRootItemType(String rootItemType) {
		_rootItemType = rootItemType;
	}

	public void setSiteNavigationMenuId(long siteNavigationMenuId) {
		_siteNavigationMenuId = siteNavigationMenuId;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_ddmTemplateGroupId = 0;
		_ddmTemplateKey = null;
		_displayDepth = 0;
		_expandedLevels = "auto";
		_navigationMenuMode = NavigationMenuMode.DEFAULT;
		_preview = false;
		_rootItemId = null;
		_rootItemLevel = 1;
		_rootItemType = "absolute";
		_siteNavigationMenuId = 0;
	}

	protected String getDisplayStyle() {
		if (Validator.isNotNull(_ddmTemplateKey)) {
			PortletDisplayTemplate portletDisplayTemplate =
				ServletContextUtil.getPortletDisplayTemplate();

			return portletDisplayTemplate.getDisplayStyle(_ddmTemplateKey);
		}

		return StringPool.BLANK;
	}

	protected long getDisplayStyleGroupId() {
		if (_ddmTemplateGroupId > 0) {
			return _ddmTemplateGroupId;
		}

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return themeDisplay.getScopeGroupId();
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
	}

	private static final String _PAGE = "/navigation/page.jsp";

	private long _ddmTemplateGroupId;
	private String _ddmTemplateKey;
	private int _displayDepth;
	private String _expandedLevels = "auto";
	private NavigationMenuMode _navigationMenuMode = NavigationMenuMode.DEFAULT;
	private boolean _preview;
	private String _rootItemId;
	private int _rootItemLevel = 1;
	private String _rootItemType = "absolute";
	private long _siteNavigationMenuId;

}