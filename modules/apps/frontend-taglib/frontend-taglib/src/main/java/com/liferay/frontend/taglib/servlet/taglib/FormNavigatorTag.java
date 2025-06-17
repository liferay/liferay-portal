/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.form.navigator.FormNavigatorCategoryProvider;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntryProvider;
import com.liferay.frontend.taglib.form.navigator.constants.FormNavigatorConstants;
import com.liferay.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class FormNavigatorTag extends IncludeTag {

	@Override
	public int doStartTag() {
		return EVAL_BODY_INCLUDE;
	}

	public String getBackURL() {
		return _backURL;
	}

	public String getFieldSetCssClass() {
		return _fieldSetCssClass;
	}

	public Object getFormModelBean() {
		return _formModelBean;
	}

	public String getId() {
		return _id;
	}

	public FormNavigatorConstants.FormNavigatorType getType() {
		return _type;
	}

	public boolean isShowButtons() {
		return _showButtons;
	}

	public void setBackURL(String backURL) {
		_backURL = backURL;
	}

	public void setFieldSetCssClass(String fieldSetCssClass) {
		_fieldSetCssClass = fieldSetCssClass;
	}

	public void setFormModelBean(Object formModelBean) {
		_formModelBean = formModelBean;
	}

	public void setId(String id) {
		_id = id;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setShowButtons(boolean showButtons) {
		_showButtons = showButtons;
	}

	public void setType(FormNavigatorConstants.FormNavigatorType type) {
		_type = type;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_backURL = null;
		_fieldSetCssClass = null;
		_formModelBean = null;
		_id = null;
		_showButtons = true;
		_type = FormNavigatorConstants.FormNavigatorType.DEFAULT;
	}

	@Override
	protected String getPage() {
		return "/form_navigator/page.jsp";
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-frontend:form-navigator:backURL", _getBackURL());
		httpServletRequest.setAttribute(
			"liferay-frontend:form-navigator:categoryKeys", _getCategoryKeys());
		httpServletRequest.setAttribute(
			"liferay-frontend:form-navigator:fieldSetCssClass",
			_fieldSetCssClass);
		httpServletRequest.setAttribute(
			"liferay-frontend:form-navigator:formModelBean", _formModelBean);
		httpServletRequest.setAttribute(
			"liferay-frontend:form-navigator:id", _id);
		httpServletRequest.setAttribute(
			"liferay-frontend:form-navigator:showButtons",
			String.valueOf(_showButtons));
		httpServletRequest.setAttribute(
			"liferay-frontend:form-navigator:type", _type);
	}

	private String _getBackURL() {
		String backURL = _backURL;

		HttpServletRequest httpServletRequest = getRequest();

		if (Validator.isNull(backURL)) {
			backURL = ParamUtil.getString(httpServletRequest, "redirect");
		}

		if (Validator.isNull(backURL)) {
			PortletResponse portletResponse =
				(PortletResponse)httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_RESPONSE);

			LiferayPortletResponse liferayPortletResponse =
				PortalUtil.getLiferayPortletResponse(portletResponse);

			backURL = String.valueOf(liferayPortletResponse.createRenderURL());
		}

		return backURL;
	}

	private String[] _getCategoryKeys() {
		List<String> categoryKeys = new ArrayList<>();

		FormNavigatorCategoryProvider formNavigatorCategoryProvider =
			ServletContextUtil.getFormNavigatorCategoryProvider();
		FormNavigatorEntryProvider formNavigatorEntryProvider =
			ServletContextUtil.getFormNavigatorEntryProvider();

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		for (String categoryKey : formNavigatorCategoryProvider.getKeys(_id)) {
			if (ListUtil.isNotEmpty(
					formNavigatorEntryProvider.getFormNavigatorEntries(
						_id, categoryKey, themeDisplay.getUser(),
						_formModelBean))) {

				categoryKeys.add(categoryKey);
			}
		}

		return ArrayUtil.toStringArray(categoryKeys);
	}

	private String _backURL;
	private String _fieldSetCssClass;
	private Object _formModelBean;
	private String _id;
	private boolean _showButtons = true;
	private FormNavigatorConstants.FormNavigatorType _type =
		FormNavigatorConstants.FormNavigatorType.DEFAULT;

}