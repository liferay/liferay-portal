/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 * @author Marco Leo
 */
public class ScreenNavigationTag extends IncludeTag {

	@Override
	public int doEndTag() throws JspException {
		if (ListUtil.isEmpty(_screenNavigationCategories)) {
			return SKIP_PAGE;
		}

		return super.doEndTag();
	}

	@Override
	public int doStartTag() throws JspException {
		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_screenNavigationCategories =
			ScreenNavigationRegistryUtil.getScreenNavigationCategories(
				_key, themeDisplay.getUser(), getModelContext());

		return super.doStartTag();
	}

	public String getContainerCssClass() {
		return _containerCssClass;
	}

	public String getContainerWrapperCssClass() {
		return _containerWrapperCssClass;
	}

	public Object getContext() {
		return _context;
	}

	public String getFullContainerCssClass() {
		return _fullContainerCssClass;
	}

	public String getHeaderContainerCssClass() {
		return _headerContainerCssClass;
	}

	public String getId() {
		return _id;
	}

	public String getKey() {
		return _key;
	}

	public String getMenubarCssClass() {
		return _menubarCssClass;
	}

	public Object getModelBean() {
		return _modelBean;
	}

	public Object getModelContext() {
		if (Validator.isNotNull(_modelBean)) {
			return _modelBean;
		}

		return _context;
	}

	public String getNavBarCssClass() {
		return _navBarCssClass;
	}

	public String getNavCssClass() {
		return _navCssClass;
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	public boolean isInverted() {
		return _inverted;
	}

	public void setContainerCssClass(String containerCssClass) {
		_containerCssClass = containerCssClass;
	}

	public void setContainerWrapperCssClass(String containerWrapperCssClass) {
		_containerWrapperCssClass = containerWrapperCssClass;
	}

	public void setContext(Object context) {
		_context = context;
	}

	public void setFullContainerCssClass(String fullContainerCssClass) {
		_fullContainerCssClass = fullContainerCssClass;
	}

	public void setHeaderContainerCssClass(String headerContainerCssClass) {
		_headerContainerCssClass = headerContainerCssClass;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setInverted(boolean inverted) {
		_inverted = inverted;
	}

	public void setKey(String key) {
		_key = key;
	}

	public void setMenubarCssClass(String menubarCssClass) {
		_menubarCssClass = menubarCssClass;
	}

	public void setModelBean(Object modelBean) {
		_modelBean = modelBean;
	}

	public void setNavBarCssClass(String navBarCssClass) {
		_navBarCssClass = navBarCssClass;
	}

	public void setNavCssClass(String navCssClass) {
		_navCssClass = navCssClass;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPortletURL(PortletURL portletURL) {
		_portletURL = portletURL;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_containerCssClass = "col-md-9";
		_containerWrapperCssClass = StringPool.BLANK;
		_context = null;
		_fullContainerCssClass = StringPool.BLANK;
		_headerContainerCssClass = StringPool.BLANK;
		_id = null;
		_inverted = false;
		_key = null;
		_menubarCssClass =
			"menubar menubar-transparent menubar-vertical-expand-md";
		_modelBean = null;
		_navBarCssClass = StringPool.BLANK;
		_navCssClass = "col-md-3";
		_portletURL = null;
		_screenNavigationCategories = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return _CLEAN_UP_SET_ATTRIBUTES;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-frontend:screen-navigation:containerCssClass",
			_containerCssClass);
		httpServletRequest.setAttribute(
			"liferay-frontend:screen-navigation:fullContainerCssClass",
			_fullContainerCssClass);
		httpServletRequest.setAttribute(
			"liferay-frontend:screen-navigation:headerContainerCssClass",
			_headerContainerCssClass);

		String id = _id;

		if (Validator.isNotNull(id)) {
			PortletResponse portletResponse =
				(PortletResponse)httpServletRequest.getAttribute(
					JavaConstants.JAVAX_PORTLET_RESPONSE);

			String namespace = StringPool.BLANK;

			if (portletResponse != null) {
				namespace = portletResponse.getNamespace();
			}

			id = PortalUtil.getUniqueElementId(
				getOriginalServletRequest(), namespace, id);
		}
		else {
			id = PortalUtil.generateRandomKey(
				httpServletRequest, ScreenNavigationTag.class.getName());
		}

		httpServletRequest.setAttribute(
			"liferay-frontend:screen-navigation:id", id);

		httpServletRequest.setAttribute(
			"liferay-frontend:screen-navigation:containerWrapperCssClass",
			_containerWrapperCssClass);
		httpServletRequest.setAttribute(
			"liferay-frontend:screen-navigation:inverted", _inverted);
		httpServletRequest.setAttribute(
			"liferay-frontend:screen-navigation:menubarCssClass",
			_menubarCssClass);
		httpServletRequest.setAttribute(
			"liferay-frontend:screen-navigation:modelContext",
			getModelContext());
		httpServletRequest.setAttribute(
			"liferay-frontend:screen-navigation:navBarCssClass",
			_navBarCssClass);
		httpServletRequest.setAttribute(
			"liferay-frontend:screen-navigation:navCssClass", _navCssClass);
		httpServletRequest.setAttribute(
			"liferay-frontend:screen-navigation:portletURL", _portletURL);
		httpServletRequest.setAttribute(
			"liferay-frontend:screen-navigation:screenNavigationCategories",
			_screenNavigationCategories);
		httpServletRequest.setAttribute(
			"liferay-frontend:screen-navigation:screenNavigationEntries",
			_getScreenNavigationEntries());
		httpServletRequest.setAttribute(
			"liferay-frontend:screen-navigation:" +
				"selectedScreenNavigationCategory",
			_getSelectedScreenNavigationCategory());
		httpServletRequest.setAttribute(
			"liferay-frontend:screen-navigation:selectedScreenNavigationEntry",
			_getSelectedScreenNavigationEntry());
	}

	private String _getDefaultScreenNavigationCategoryKey() {
		if (ListUtil.isEmpty(_screenNavigationCategories)) {
			return null;
		}

		ScreenNavigationCategory screenNavigationCategory =
			_screenNavigationCategories.get(0);

		return screenNavigationCategory.getCategoryKey();
	}

	private String _getDefaultScreenNavigationEntryKey() {
		List<ScreenNavigationEntry<Object>> screenNavigationEntries =
			_getScreenNavigationEntries();

		if (ListUtil.isEmpty(screenNavigationEntries)) {
			return null;
		}

		ScreenNavigationEntry<Object> screenNavigationEntry =
			screenNavigationEntries.get(0);

		return screenNavigationEntry.getEntryKey();
	}

	private List<ScreenNavigationEntry<Object>> _getScreenNavigationEntries() {
		ScreenNavigationCategory selectedScreenNavigationCategory =
			_getSelectedScreenNavigationCategory();

		if (selectedScreenNavigationCategory == null) {
			return null;
		}

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return ScreenNavigationRegistryUtil.getScreenNavigationEntries(
			selectedScreenNavigationCategory, themeDisplay.getUser(),
			getModelContext());
	}

	private ScreenNavigationCategory _getSelectedScreenNavigationCategory() {
		String screenNavigationCategoryKey = ParamUtil.getString(
			getRequest(), "screenNavigationCategoryKey",
			_getDefaultScreenNavigationCategoryKey());

		for (ScreenNavigationCategory screenNavigationCategory :
				_screenNavigationCategories) {

			if (Objects.equals(
					screenNavigationCategory.getCategoryKey(),
					screenNavigationCategoryKey)) {

				return screenNavigationCategory;
			}
		}

		return null;
	}

	private ScreenNavigationEntry<?> _getSelectedScreenNavigationEntry() {
		String screenNavigationEntryKey = ParamUtil.getString(
			getRequest(), "screenNavigationEntryKey");

		if (Validator.isNull(screenNavigationEntryKey)) {
			screenNavigationEntryKey = _getDefaultScreenNavigationEntryKey();
		}

		List<ScreenNavigationEntry<Object>> screenNavigationEntries =
			_getScreenNavigationEntries();

		if (ListUtil.isEmpty(screenNavigationEntries)) {
			return null;
		}

		for (ScreenNavigationEntry<Object> screenNavigationEntry :
				screenNavigationEntries) {

			if (Objects.equals(
					screenNavigationEntry.getEntryKey(),
					screenNavigationEntryKey)) {

				return screenNavigationEntry;
			}
		}

		return null;
	}

	private static final boolean _CLEAN_UP_SET_ATTRIBUTES = true;

	private static final String _PAGE = "/screen_navigation/page.jsp";

	private String _containerCssClass = "col-md-9";
	private String _containerWrapperCssClass = StringPool.BLANK;
	private Object _context;
	private String _fullContainerCssClass = StringPool.BLANK;
	private String _headerContainerCssClass = StringPool.BLANK;
	private String _id;
	private boolean _inverted;
	private String _key;
	private String _menubarCssClass =
		"menubar menubar-transparent menubar-vertical-expand-md";
	private Object _modelBean;
	private String _navBarCssClass = StringPool.BLANK;
	private String _navCssClass = "col-md-3";
	private PortletURL _portletURL;
	private List<ScreenNavigationCategory> _screenNavigationCategories;

}