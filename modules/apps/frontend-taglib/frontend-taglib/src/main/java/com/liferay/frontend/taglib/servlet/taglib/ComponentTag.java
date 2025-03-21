/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.frontend.js.loader.modules.extender.esm.ESImportUtil;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolvedPackageNameUtil;
import com.liferay.frontend.taglib.internal.util.ServicesProvider;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.servlet.taglib.aui.ESImport;
import com.liferay.portal.kernel.servlet.taglib.aui.JSFragment;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.taglib.util.ParamAndPropertyAncestorTagImpl;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Chema Balsas
 */
public class ComponentTag extends ParamAndPropertyAncestorTagImpl {

	@Override
	public int doEndTag() throws JspException {
		try {
			String module = getModule();

			if (ESImportUtil.isESImport(module)) {
				_renderESM(module);
			}
			else {
				_renderJavaScript(module);
			}
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

	public String getContainerId() {
		return _containerId;
	}

	public String getModule() {
		if (ESImportUtil.isESImport(_module)) {
			return _module;
		}

		return StringBundler.concat(getNamespace(), "/", _module);
	}

	public boolean isDestroyOnNavigate() {
		return _destroyOnNavigate;
	}

	@Override
	public void release() {
		super.release();

		_setServletContext = false;
	}

	public void setComponentId(String componentId) {
		_componentId = componentId;
	}

	public void setContainerId(String containerId) {
		_containerId = containerId;
	}

	public void setContext(Map<String, Object> context) {
		_context = context;
	}

	public void setDestroyOnNavigate(boolean destroyOnNavigate) {
		_destroyOnNavigate = destroyOnNavigate;
	}

	public void setModule(String module) {
		_module = module;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		super.setServletContext(servletContext);

		_setServletContext = true;
	}

	protected void cleanUp() {
		_componentId = null;
		_containerId = null;
		_context = null;
		_destroyOnNavigate = true;
		_module = null;
		_setServletContext = false;
	}

	protected Map<String, Object> getContext() {
		return _context;
	}

	protected String getNamespace() {
		ServletContext servletContext = pageContext.getServletContext();

		if (_setServletContext) {
			servletContext = getServletContext();
		}

		return NPMResolvedPackageNameUtil.get(servletContext);
	}

	protected boolean isPositionInline() {
		Boolean positionInline = null;

		HttpServletRequest httpServletRequest = getRequest();

		String fragmentId = ParamUtil.getString(httpServletRequest, "p_f_id");

		if (Validator.isNotNull(fragmentId)) {
			positionInline = true;
		}

		if (positionInline == null) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (themeDisplay.isIsolated() ||
				themeDisplay.isLifecycleResource() ||
				themeDisplay.isStateExclusive()) {

				positionInline = true;
			}

			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			String portletId = portletDisplay.getId();

			if (Validator.isNotNull(portletId) &&
				themeDisplay.isPortletEmbedded(
					themeDisplay.getScopeGroupId(), themeDisplay.getLayout(),
					portletId)) {

				positionInline = true;
			}
		}

		if (positionInline == null) {
			positionInline = false;
		}

		return positionInline;
	}

	private String _getRenderInvocation(String variableName) {
		StringBundler sb = new StringBundler(14);

		sb.append("Liferay.component('");

		String componentId = getComponentId();

		if (componentId == null) {
			componentId = _UNNAMED_COMPONENT_NAME + PortalUUIDUtil.generate();
		}

		sb.append(componentId);

		sb.append("', new ");
		sb.append(variableName);
		sb.append(".default(");

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		sb.append(
			_jsonSerializer.serializeDeep(
				HashMapBuilder.putAll(
					getContext()
				).put(
					"namespace", portletDisplay.getNamespace()
				).put(
					"spritemap", themeDisplay.getPathThemeSpritemap()
				).build()));

		String containerId = getContainerId();

		if (Validator.isNotNull(containerId)) {
			sb.append(", '");
			sb.append(containerId);
			sb.append("'");
		}

		sb.append("), { destroyOnNavigate: ");
		sb.append(_destroyOnNavigate);
		sb.append(", portletId: '");
		sb.append(portletDisplay.getId());
		sb.append("'});");

		return sb.toString();
	}

	private String _getVariableName(String module) {
		String moduleName = StringUtil.extractLast(
			module, CharPool.FORWARD_SLASH);

		return StringUtil.removeChars(moduleName, _UNSAFE_MODULE_NAME_CHARS);
	}

	private void _renderESM(String module) throws IOException {
		List<ESImport> esImports = new ArrayList<>();

		AbsolutePortalURLBuilderFactory absolutePortalURLBuilderFactory =
			ServicesProvider.getAbsolutePortalURLBuilderFactory();

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		esImports.add(
			ESImportUtil.getESImport(
				absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
					httpServletRequest),
				"ComponentModule", module));

		StringBundler contentSB = new StringBundler(12);

		contentSB.append("Liferay.component('");

		String componentId = getComponentId();

		if (componentId == null) {
			componentId = _UNNAMED_COMPONENT_NAME + PortalUUIDUtil.generate();
		}

		contentSB.append(componentId);

		contentSB.append("', new ComponentModule(");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		contentSB.append(
			_jsonSerializer.serializeDeep(
				HashMapBuilder.putAll(
					getContext()
				).put(
					"namespace", portletDisplay.getNamespace()
				).put(
					"spritemap", themeDisplay.getPathThemeSpritemap()
				).build()));

		String containerId = getContainerId();

		if (Validator.isNotNull(containerId)) {
			contentSB.append(", '");
			contentSB.append(containerId);
			contentSB.append("'");
		}

		contentSB.append("), { destroyOnNavigate: ");
		contentSB.append(_destroyOnNavigate);
		contentSB.append(", portletId: '");
		contentSB.append(portletDisplay.getId());
		contentSB.append("'});");

		String portletId = portletDisplay.getId();

		if (isPositionInline()) {
			ScriptData scriptData = new ScriptData();

			scriptData.append(
				portletId,
				new JSFragment(null, contentSB.toString(), esImports));

			JspWriter jspWriter = pageContext.getOut();

			scriptData.writeTo(jspWriter);
		}
		else {
			ScriptData scriptData = (ScriptData)httpServletRequest.getAttribute(
				WebKeys.AUI_SCRIPT_DATA);

			if (scriptData == null) {
				scriptData = new ScriptData();

				httpServletRequest.setAttribute(
					WebKeys.AUI_SCRIPT_DATA, scriptData);
			}

			scriptData.append(
				portletId,
				new JSFragment(null, contentSB.toString(), esImports));
		}
	}

	private void _renderJavaScript(String module) throws IOException {
		String variableName = _getVariableName(module);

		String javaScriptCode = _getRenderInvocation(variableName);

		HttpServletRequest httpServletRequest = getRequest();

		if (isPositionInline()) {
			ScriptData scriptData = new ScriptData();

			scriptData.append(
				PortalUtil.getPortletId(httpServletRequest), javaScriptCode,
				module + " as " + variableName, ScriptData.ModulesType.ES6);

			JspWriter jspWriter = pageContext.getOut();

			scriptData.writeTo(jspWriter);

			return;
		}

		ScriptData scriptData = (ScriptData)httpServletRequest.getAttribute(
			WebKeys.AUI_SCRIPT_DATA);

		if (scriptData == null) {
			scriptData = new ScriptData();

			httpServletRequest.setAttribute(
				WebKeys.AUI_SCRIPT_DATA, scriptData);
		}

		scriptData.append(
			PortalUtil.getPortletId(httpServletRequest), javaScriptCode,
			module + " as " + variableName, ScriptData.ModulesType.ES6);
	}

	private static final String _UNNAMED_COMPONENT_NAME =
		"__UNNAMED_COMPONENT__";

	private static final char[] _UNSAFE_MODULE_NAME_CHARS = {
		CharPool.PERIOD, CharPool.DASH
	};

	private String _componentId;
	private String _containerId;
	private Map<String, Object> _context;
	private boolean _destroyOnNavigate = true;
	private final JSONSerializer _jsonSerializer =
		JSONFactoryUtil.createJSONSerializer();
	private String _module;
	private boolean _setServletContext;

}