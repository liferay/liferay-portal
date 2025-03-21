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
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Tibor Lipusz
 */
public class NavigationTag extends IncludeTag {

	public long getDdmTemplateGroupId() {
		return _ddmTemplateGroupId;
	}

	public String getDdmTemplateKey() {
		return _ddmTemplateKey;
	}

	public int getDisplayDepth() {
		return _displayDepth;
	}

	public String getIncludedLayouts() {
		return _includedLayouts;
	}

	public int getRootLayoutLevel() {
		return _rootLayoutLevel;
	}

	public String getRootLayoutType() {
		return _rootLayoutType;
	}

	public String getRootLayoutUuid() {
		return _rootLayoutUuid;
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
				_displayDepth, _includedLayouts, httpServletRequest,
				NavigationMenuMode.DEFAULT, _preview, _rootLayoutUuid,
				_rootLayoutLevel, _rootLayoutType, 0);

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

	public void setIncludedLayouts(String includedLayouts) {
		_includedLayouts = includedLayouts;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPreview(boolean preview) {
		_preview = preview;
	}

	public void setRootLayoutLevel(int rootLayoutLevel) {
		_rootLayoutLevel = rootLayoutLevel;
	}

	public void setRootLayoutType(String rootLayoutType) {
		_rootLayoutType = rootLayoutType;
	}

	public void setRootLayoutUuid(String rootLayoutUuid) {
		_rootLayoutUuid = rootLayoutUuid;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_ddmTemplateGroupId = 0;
		_ddmTemplateKey = null;
		_displayDepth = 0;
		_includedLayouts = "auto";
		_preview = false;
		_rootLayoutLevel = 1;
		_rootLayoutType = "absolute";
		_rootLayoutUuid = null;
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
	private String _includedLayouts = "auto";
	private boolean _preview;
	private int _rootLayoutLevel = 1;
	private String _rootLayoutType = "absolute";
	private String _rootLayoutUuid;

}