/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.taglib.servlet.taglib;

import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.List;

/**
 * @author Fabio Diego Mastrorilli
 */
public class HeaderTag extends IncludeTag {

	public List<HeaderActionModel> getActions() {
		return _actions;
	}

	public String getAdditionalStatusLabel() {
		return _additionalStatusLabel;
	}

	public String getAdditionalStatusLabelStyle() {
		return _additionalStatusLabelStyle;
	}

	public String getAssignerModalUrl() {
		return _assignerModalUrl;
	}

	public Object getBean() {
		return _bean;
	}

	public String getBeanIdLabel() {
		return _beanIdLabel;
	}

	public String getBeanLabel() {
		return _beanIdLabel;
	}

	public String getCssClasses() {
		return _cssClasses;
	}

	public long getDisplayBeanId() {
		return _displayBeanId;
	}

	public List<DropdownItem> getDropdownItems() {
		return _dropdownItems;
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	public String getExternalReferenceCodeEditUrl() {
		return _externalReferenceCodeEditUrl;
	}

	public boolean getFullWidth() {
		return _fullWidth;
	}

	public Class<?> getModel() {
		return _model;
	}

	public String getPreviewUrl() {
		return _previewUrl;
	}

	public String getSpritemap() {
		return _spritemap;
	}

	public String getThumbnailUrl() {
		return _thumbnailUrl;
	}

	public String getTitle() {
		return _title;
	}

	public PortletURL getTransitionPortletURL() {
		return _transitionPortletURL;
	}

	public String getVersion() {
		return _version;
	}

	public String getWrapperCssClasses() {
		return _wrapperCssClasses;
	}

	public void setActions(List<HeaderActionModel> actions) {
		_actions = actions;
	}

	public void setAdditionalStatusLabel(String additionalStatusLabel) {
		_additionalStatusLabel = additionalStatusLabel;
	}

	public void setAdditionalStatusLabelStyle(
		String additionalStatusLabelStyle) {

		_additionalStatusLabelStyle = additionalStatusLabelStyle;
	}

	public void setAssignerModalUrl(String assignerModalUrl) {
		_assignerModalUrl = assignerModalUrl;
	}

	public void setBean(Object bean) {
		_bean = bean;
	}

	public void setBeanIdLabel(String beanIdLabel) {
		_beanIdLabel = beanIdLabel;
	}

	public void setCssClasses(String cssClasses) {
		_cssClasses = cssClasses;
	}

	public void setDisplayBeanId(long displayBeanId) {
		_displayBeanId = displayBeanId;
	}

	public void setDropdownItems(List<DropdownItem> dropdownItems) {
		_dropdownItems = dropdownItems;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		_externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCodeEditUrl(
		String externalReferenceCodeEditUrl) {

		_externalReferenceCodeEditUrl = externalReferenceCodeEditUrl;
	}

	public void setFullWidth(boolean fullWidth) {
		_fullWidth = fullWidth;
	}

	public void setModel(Class<?> model) {
		_model = model;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPreviewUrl(String previewUrl) {
		_previewUrl = previewUrl;
	}

	public void setSpritemap(String spritemap) {
		_spritemap = spritemap;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		_thumbnailUrl = thumbnailUrl;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setTransitionPortletURL(PortletURL transitionPortletURL) {
		_transitionPortletURL = transitionPortletURL;
	}

	public void setVersion(String version) {
		_version = version;
	}

	public void setWrapperCssClasses(String wrapperCssClasses) {
		_wrapperCssClasses = wrapperCssClasses;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_actions = null;
		_additionalStatusLabel = null;
		_additionalStatusLabelStyle = null;
		_assignerModalUrl = null;
		_bean = null;
		_beanIdLabel = null;
		_cssClasses = null;
		_displayBeanId = 0;
		_dropdownItems = null;
		_externalReferenceCode = null;
		_externalReferenceCodeEditUrl = null;
		_fullWidth = false;
		_model = null;
		_previewUrl = null;
		_spritemap = null;
		_thumbnailUrl = null;
		_title = null;
		_transitionPortletURL = null;
		_version = null;
		_wrapperCssClasses = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest = getRequest();

		if (Validator.isNull(_spritemap)) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_spritemap = themeDisplay.getPathThemeSpritemap();
		}

		httpServletRequest.setAttribute(
			"liferay-commerce:header:actions", _actions);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:additionalStatusLabel",
			_additionalStatusLabel);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:additionalStatusLabelStyle",
			_additionalStatusLabelStyle);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:assignerModalUrl", _assignerModalUrl);
		httpServletRequest.setAttribute("liferay-commerce:header:bean", _bean);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:beanIdLabel", _beanIdLabel);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:cssClasses", _cssClasses);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:displayBeanId", _displayBeanId);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:dropdownItems", _dropdownItems);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:externalReferenceCode",
			_externalReferenceCode);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:externalReferenceCodeEditUrl",
			_externalReferenceCodeEditUrl);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:fullWidth", _fullWidth);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:hasWorkflow", Boolean.FALSE);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:model", _model);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:previewUrl", _previewUrl);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:spritemap", _spritemap);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:thumbnailUrl", _thumbnailUrl);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:title", _title);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:transitionPortletURL",
			_transitionPortletURL);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:version", _version);
		httpServletRequest.setAttribute(
			"liferay-commerce:header:wrapperCssClasses", _wrapperCssClasses);
	}

	private static final String _PAGE = "/header/page.jsp";

	private List<HeaderActionModel> _actions;
	private String _additionalStatusLabel;
	private String _additionalStatusLabelStyle;
	private String _assignerModalUrl;
	private Object _bean;
	private String _beanIdLabel;
	private String _cssClasses;
	private long _displayBeanId;
	private List<DropdownItem> _dropdownItems;
	private String _externalReferenceCode;
	private String _externalReferenceCodeEditUrl;
	private boolean _fullWidth;
	private Class<?> _model;
	private String _previewUrl;
	private String _spritemap;
	private String _thumbnailUrl;
	private String _title;
	private PortletURL _transitionPortletURL;
	private String _version;
	private String _wrapperCssClasses;

}