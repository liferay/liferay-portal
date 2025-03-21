/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.DummyPortletURL;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletModeFactory;
import com.liferay.portal.kernel.portlet.PortletQName;
import com.liferay.portal.kernel.portlet.WindowStateFactory;
import com.liferay.portal.kernel.portlet.constants.PortletPreferencesFactoryConstants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.ParamAndPropertyAncestorTagImpl;
import com.liferay.taglib.util.TypedParamAccessorTag;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
public class ActionURLTag
	extends ParamAndPropertyAncestorTagImpl implements TypedParamAccessorTag {

	public static PortletURL doTag(
			String lifecycle, String windowState, String portletMode,
			Boolean secure, Boolean copyCurrentRenderParameters,
			Boolean escapeXml, String name, String resourceID,
			String cacheability, long plid, long refererPlid,
			String portletName, Boolean anchor, Boolean encrypt,
			long doAsGroupId, long doAsUserId, Boolean portletConfiguration,
			Map<String, String[]> parameterMap,
			Set<String> removedParameterNames,
			HttpServletRequest httpServletRequest)
		throws Exception {

		if (portletName == null) {
			portletName = _getPortletName(httpServletRequest);
		}

		LiferayPortletURL liferayPortletURL = _getLiferayPortletURL(
			httpServletRequest, plid, portletName, lifecycle,
			copyCurrentRenderParameters);

		if (liferayPortletURL == null) {
			_log.error(
				"Render response is null because this tag is not being " +
					"called within the context of a portlet");

			return DummyPortletURL.getInstance();
		}

		if (Validator.isNotNull(windowState)) {
			liferayPortletURL.setWindowState(
				WindowStateFactory.getWindowState(windowState));
		}

		if (Validator.isNotNull(portletMode)) {
			liferayPortletURL.setPortletMode(
				PortletModeFactory.getPortletMode(portletMode));
		}

		if (secure != null) {
			liferayPortletURL.setSecure(secure.booleanValue());
		}
		else {
			liferayPortletURL.setSecure(
				PortalUtil.isSecure(httpServletRequest));
		}

		if (copyCurrentRenderParameters != null) {
			liferayPortletURL.setCopyCurrentRenderParameters(
				copyCurrentRenderParameters.booleanValue());
		}

		if (escapeXml != null) {
			liferayPortletURL.setEscapeXml(escapeXml.booleanValue());
		}

		if (lifecycle.equals(PortletRequest.ACTION_PHASE) &&
			Validator.isNotNull(name)) {

			liferayPortletURL.setParameter(ActionRequest.ACTION_NAME, name);
		}

		if (resourceID != null) {
			liferayPortletURL.setResourceID(resourceID);
		}

		if (cacheability != null) {
			liferayPortletURL.setCacheability(cacheability);
		}

		if (refererPlid > LayoutConstants.DEFAULT_PLID) {
			liferayPortletURL.setRefererPlid(refererPlid);
		}

		if (anchor != null) {
			liferayPortletURL.setAnchor(anchor.booleanValue());
		}

		if (encrypt != null) {
			liferayPortletURL.setEncrypt(encrypt.booleanValue());
		}

		if (doAsGroupId > 0) {
			liferayPortletURL.setDoAsGroupId(doAsGroupId);
		}

		if (doAsUserId > 0) {
			liferayPortletURL.setDoAsUserId(doAsUserId);
		}

		String settingsScope = null;

		if ((portletConfiguration != null) &&
			portletConfiguration.booleanValue()) {

			String returnToFullPageURL = ParamUtil.getString(
				httpServletRequest, "returnToFullPageURL");
			String portletResource = ParamUtil.getString(
				httpServletRequest, "portletResource");
			String previewWidth = ParamUtil.getString(
				httpServletRequest, "previewWidth");
			settingsScope = ParamUtil.getString(
				httpServletRequest, "settingsScope",
				PortletPreferencesFactoryConstants.
					SETTINGS_SCOPE_PORTLET_INSTANCE);

			if (Validator.isNull(name)) {
				liferayPortletURL.setParameter(
					ActionRequest.ACTION_NAME, "editConfiguration");
			}

			liferayPortletURL.setParameter(
				"mvcPath", "/edit_configuration.jsp");
			liferayPortletURL.setParameter(
				"returnToFullPageURL", returnToFullPageURL);
			liferayPortletURL.setParameter(
				"portletConfiguration", Boolean.TRUE.toString());
			liferayPortletURL.setParameter("portletResource", portletResource);
			liferayPortletURL.setParameter("previewWidth", previewWidth);
		}

		if (parameterMap != null) {
			for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
				String key = entry.getKey();

				if (key.startsWith(_ACTION_PARAMETER_NAMESPACE)) {
					key = key.substring(_ACTION_PARAMETER_NAMESPACE.length());

					String portletNamespace = PortalUtil.getPortletNamespace(
						portletName);

					if (!key.startsWith(portletNamespace)) {
						key = portletNamespace.concat(key);
					}
				}

				liferayPortletURL.setParameter(key, entry.getValue(), false);
			}
		}

		if ((settingsScope != null) &&
			((parameterMap == null) ||
			 !parameterMap.containsKey("settingsScope"))) {

			liferayPortletURL.setParameter("settingsScope", settingsScope);
		}

		liferayPortletURL.setRemovedParameterNames(removedParameterNames);

		return liferayPortletURL;
	}

	@Override
	public void addParam(String name, String type, String value) {
		if (Validator.isNotNull(name)) {
			if (Objects.equals(type, "action")) {
				name = _ACTION_PARAMETER_NAMESPACE.concat(name);
			}
			else if (Objects.equals(type, "render")) {
				name = PortletQName.PRIVATE_RENDER_PARAMETER_NAMESPACE.concat(
					name);
			}
		}

		super.addParam(name, value);
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			PortletURL portletURL = doTag(
				getLifecycle(), _windowState, _portletMode, _secure,
				_copyCurrentRenderParameters, _escapeXml, _name, _resourceID,
				_cacheability, _plid, _refererPlid, _portletName, _anchor,
				_encrypt, _doAsGroupId, _doAsUserId, _portletConfiguration,
				getParams(), getRemovedParameterNames(),
				(HttpServletRequest)pageContext.getRequest());

			if (Validator.isNotNull(_var)) {
				pageContext.setAttribute(_var, portletURL.toString());
			}
			else if (Validator.isNotNull(_varImpl)) {
				pageContext.setAttribute(_varImpl, portletURL);
			}
			else {
				JspWriter jspWriter = pageContext.getOut();

				jspWriter.write(portletURL.toString());
			}

			return EVAL_PAGE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
		finally {
			clearParams();
			clearProperties();

			_plid = LayoutConstants.DEFAULT_PLID;
		}
	}

	@Override
	public int doStartTag() {
		return EVAL_BODY_INCLUDE;
	}

	public String getLifecycle() {
		return PortletRequest.ACTION_PHASE;
	}

	public void setAnchor(boolean anchor) {
		_anchor = Boolean.valueOf(anchor);
	}

	public void setCacheability(String cacheability) {
		_cacheability = cacheability;
	}

	@Override
	public void setCopyCurrentRenderParameters(
		boolean copyCurrentRenderParameters) {

		super.setCopyCurrentRenderParameters(copyCurrentRenderParameters);

		_copyCurrentRenderParameters = Boolean.valueOf(
			copyCurrentRenderParameters);
	}

	public void setDoAsGroupId(long doAsGroupId) {
		_doAsGroupId = doAsGroupId;
	}

	public void setDoAsUserId(long doAsUserId) {
		_doAsUserId = doAsUserId;
	}

	public void setEncrypt(boolean encrypt) {
		_encrypt = Boolean.valueOf(encrypt);
	}

	public void setEscapeXml(boolean escapeXml) {
		_escapeXml = Boolean.valueOf(escapeXml);
	}

	public void setId(String resourceID) {
		_resourceID = resourceID;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setPlid(long plid) {
		_plid = plid;
	}

	public void setPortletConfiguration(boolean portletConfiguration) {
		_portletConfiguration = Boolean.valueOf(portletConfiguration);
	}

	public void setPortletMode(String portletMode) {
		_portletMode = portletMode;
	}

	public void setPortletName(String portletName) {
		_portletName = portletName;
	}

	public void setRefererPlid(long refererPlid) {
		_refererPlid = refererPlid;
	}

	public void setSecure(boolean secure) {
		_secure = Boolean.valueOf(secure);
	}

	public void setVar(String var) {
		_var = var;
	}

	public void setVarImpl(String varImpl) {
		_varImpl = varImpl;
	}

	public void setWindowState(String windowState) {
		_windowState = windowState;
	}

	private static LiferayPortletURL _getLiferayPortletURL(
		HttpServletRequest httpServletRequest, long plid, String portletName,
		String lifecycle, Boolean copyCurrentRenderParameters) {

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		if (portletRequest == null) {
			return null;
		}

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		LiferayPortletResponse liferayPortletResponse =
			PortalUtil.getLiferayPortletResponse(portletResponse);

		if (((copyCurrentRenderParameters != null) &&
			 copyCurrentRenderParameters) ||
			lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {

			return liferayPortletResponse.createLiferayPortletURL(
				plid, portletName, lifecycle, MimeResponse.Copy.ALL);
		}

		return liferayPortletResponse.createLiferayPortletURL(
			plid, portletName, lifecycle);
	}

	private static String _getPortletName(
		HttpServletRequest httpServletRequest) {

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		if (portletRequest == null) {
			return null;
		}

		LiferayPortletConfig liferayPortletConfig =
			(LiferayPortletConfig)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_CONFIG);

		return liferayPortletConfig.getPortletId();
	}

	private static final String _ACTION_PARAMETER_NAMESPACE = "p_action_p_";

	private static final Log _log = LogFactoryUtil.getLog(ActionURLTag.class);

	private Boolean _anchor;
	private String _cacheability;
	private Boolean _copyCurrentRenderParameters;
	private long _doAsGroupId;
	private long _doAsUserId;
	private Boolean _encrypt;
	private Boolean _escapeXml;
	private String _name;
	private long _plid = LayoutConstants.DEFAULT_PLID;
	private Boolean _portletConfiguration;
	private String _portletMode;
	private String _portletName;
	private long _refererPlid = LayoutConstants.DEFAULT_PLID;
	private String _resourceID;
	private Boolean _secure;
	private String _var;
	private String _varImpl;
	private String _windowState;

}