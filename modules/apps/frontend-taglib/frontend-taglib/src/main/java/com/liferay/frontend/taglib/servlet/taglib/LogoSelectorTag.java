/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Sandro Chinea
 */
public class LogoSelectorTag extends IncludeTag {

	public int getAspectRatio() {
		return _aspectRatio;
	}

	public String getCurrentLogoURL() {
		return _currentLogoURL;
	}

	public String getDefaultLogoURL() {
		return _defaultLogoURL;
	}

	public String getDescription() {
		return _description;
	}

	public String getLabel(HttpServletRequest httpServletRequest) {
		if (Validator.isNull(_label)) {
			return LanguageUtil.get(httpServletRequest, "logo");
		}

		return _label;
	}

	public String getPortletNamespace() {
		return _portletNamespace;
	}

	public boolean isDisabled() {
		return _disabled;
	}

	public boolean isPreserveRatio() {
		return _preserveRatio;
	}

	public void setAspectRatio(int aspectRatio) {
		_aspectRatio = aspectRatio;
	}

	public void setCurrentLogoURL(String currentLogoURL) {
		_currentLogoURL = currentLogoURL;
	}

	public void setDefaultLogoURL(String defaultLogoURL) {
		_defaultLogoURL = defaultLogoURL;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setLabel(String label) {
		_label = label;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPortletNamespace(String portletNamespace) {
		_portletNamespace = portletNamespace;
	}

	public void setPreserveRatio(boolean preserveRatio) {
		_preserveRatio = preserveRatio;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_aspectRatio = 0;
		_currentLogoURL = null;
		_defaultLogoURL = null;
		_description = null;
		_disabled = false;
		_label = null;
		_portletNamespace = null;
		_preserveRatio = false;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-frontend:logo-selector:defaultLogoURL", _defaultLogoURL);
		httpServletRequest.setAttribute(
			"liferay-frontend:logo-selector:description", _description);
		httpServletRequest.setAttribute(
			"liferay-frontend:logo-selector:disabled", _disabled);
		httpServletRequest.setAttribute(
			"liferay-frontend:logo-selector:label",
			getLabel(httpServletRequest));
		httpServletRequest.setAttribute(
			"liferay-frontend:logo-selector:logoURL",
			_getLogoURL(httpServletRequest));
		httpServletRequest.setAttribute(
			"liferay-frontend:logo-selector:portletNamespace",
			_portletNamespace);

		String randomKey = PortalUtil.generateRandomKey(
			httpServletRequest, "taglib_ui_logo_selector");

		String randomNamespace = randomKey + StringPool.UNDERLINE;

		httpServletRequest.setAttribute(
			"liferay-frontend:logo-selector:selectLogoURL",
			_getSelectLogoURL(httpServletRequest, randomNamespace));
	}

	private String _getLogoURL(HttpServletRequest httpServletRequest) {
		boolean deleteLogo = ParamUtil.getBoolean(
			httpServletRequest, "deleteLogo");

		if (deleteLogo) {
			return getDefaultLogoURL();
		}

		long fileEntryId = ParamUtil.getLong(httpServletRequest, "fileEntryId");

		if (fileEntryId > 0) {
			return ResourceURLBuilder.createResourceURL(
				PortletURLFactoryUtil.create(
					httpServletRequest, PortletKeys.IMAGE_UPLOADER,
					PortletRequest.RESOURCE_PHASE)
			).setMVCResourceCommandName(
				"/image_uploader/upload_image"
			).setCMD(
				Constants.GET_TEMP
			).buildString();
		}

		return getCurrentLogoURL();
	}

	private String _getSelectLogoURL(
		HttpServletRequest httpServletRequest, String randomNamespace) {

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				httpServletRequest, PortletKeys.IMAGE_UPLOADER,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/image_uploader/upload_image"
		).setParameter(
			"aspectRatio", getAspectRatio()
		).setParameter(
			"ctCollectionId",
			ParamUtil.getLong(
				httpServletRequest, "ctCollectionId",
				CTCollectionThreadLocal.getCTCollectionId())
		).setParameter(
			"currentLogoURL", "[$CURRENT_LOGO_URL$]"
		).setParameter(
			"preserveRatio", isPreserveRatio()
		).setParameter(
			"randomNamespace", randomNamespace
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private static final String _PAGE = "/logo_selector/page.jsp";

	private int _aspectRatio;
	private String _currentLogoURL;
	private String _defaultLogoURL;
	private String _description;
	private boolean _disabled;
	private String _label;
	private String _portletNamespace;
	private boolean _preserveRatio;

}