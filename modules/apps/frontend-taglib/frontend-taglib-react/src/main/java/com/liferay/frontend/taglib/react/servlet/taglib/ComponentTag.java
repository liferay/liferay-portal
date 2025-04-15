/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.react.servlet.taglib;

import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolvedPackageNameUtil;
import com.liferay.frontend.taglib.react.servlet.taglib.util.ServicesProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.taglib.util.ParamAndPropertyAncestorTagImpl;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.util.Collections;
import java.util.Map;

/**
 * @author Chema Balsas
 */
public class ComponentTag extends ParamAndPropertyAncestorTagImpl {

	@Override
	public int doEndTag() throws JspException {
		JspWriter jspWriter = pageContext.getOut();

		Map<String, Object> props = getProps();

		try {
			prepareProps(props);

			ComponentDescriptor componentDescriptor = new ComponentDescriptor(
				getModule(), getComponentId(), null, isPositionInLine());

			ReactRenderer reactRenderer = ServicesProvider.getReactRenderer();

			reactRenderer.renderReact(
				componentDescriptor, props, getRequest(), jspWriter);
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
		finally {
			cleanUp();
		}

		return EVAL_PAGE;
	}

	@Override
	public int doStartTag() {
		return EVAL_BODY_INCLUDE;
	}

	public String getComponentId() {
		return _componentId;
	}

	public String getModule() {
		if (_module.contains(" from ")) {
			return _module;
		}

		return StringBundler.concat(getNamespace(), "/", _module);
	}

	@Override
	public void release() {
		super.release();

		_setServletContext = false;
	}

	public void setComponentId(String componentId) {
		_componentId = componentId;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #setProps(Map)}
	 */
	@Deprecated
	public void setData(Map<String, Object> data) {
		setProps(data);
	}

	public void setModule(String module) {
		_module = module;
	}

	public void setProps(Map<String, Object> props) {
		_props = props;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		super.setServletContext(servletContext);

		_setServletContext = true;
	}

	protected void cleanUp() {
		_componentId = null;
		_module = null;
		_props = Collections.emptyMap();
		_setServletContext = false;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getProps()}
	 */
	@Deprecated
	protected Map<String, Object> getData() {
		return getProps();
	}

	protected String getNamespace() {
		ServletContext servletContext = pageContext.getServletContext();

		if (_setServletContext) {
			servletContext = getServletContext();
		}

		return NPMResolvedPackageNameUtil.get(servletContext);
	}

	protected Map<String, Object> getProps() {
		return _props;
	}

	protected boolean isPositionInLine() {
		HttpServletRequest httpServletRequest = getRequest();

		String fragmentId = ParamUtil.getString(httpServletRequest, "p_f_id");

		if (Validator.isNotNull(fragmentId)) {
			return true;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay.isIsolated() || themeDisplay.isLifecycleResource() ||
			themeDisplay.isStateExclusive()) {

			return true;
		}

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String portletId = portletDisplay.getId();

		if (Validator.isNotNull(portletId) &&
			themeDisplay.isPortletEmbedded(
				themeDisplay.getScopeGroupId(), themeDisplay.getLayout(),
				portletId)) {

			return true;
		}

		return false;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #prepareData(Map)}
	 */
	@Deprecated
	protected void prepareData(Map<String, Object> data) {
		prepareProps(data);
	}

	protected void prepareProps(Map<String, Object> props) {
	}

	private String _componentId;
	private String _module;
	private Map<String, Object> _props = Collections.emptyMap();
	private boolean _setServletContext;

}