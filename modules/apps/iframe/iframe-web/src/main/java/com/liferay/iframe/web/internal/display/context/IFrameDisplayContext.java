/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.iframe.web.internal.display.context;

import com.liferay.iframe.web.internal.configuration.IFramePortletInstanceConfiguration;
import com.liferay.iframe.web.internal.constants.IFrameWebKeys;
import com.liferay.iframe.web.internal.util.IFrameUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.WindowState;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Juergen Kappler
 */
public class IFrameDisplayContext {

	public IFrameDisplayContext(PortletRequest request)
		throws ConfigurationException {

		_request = request;

		_themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		_iFramePortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				IFramePortletInstanceConfiguration.class, _themeDisplay);
	}

	public String getAuthType() {
		if (_authType != null) {
			return _authType;
		}

		_authType = _iFramePortletInstanceConfiguration.authType();

		return _authType;
	}

	public String getFormMethod() {
		if (_formMethod != null) {
			return _formMethod;
		}

		_formMethod = _iFramePortletInstanceConfiguration.formMethod();

		return _formMethod;
	}

	public String getHeight() {
		if (_height != null) {
			return _height;
		}

		String windowState = String.valueOf(_request.getWindowState());

		if (windowState.equals(WindowState.MAXIMIZED)) {
			_height = _iFramePortletInstanceConfiguration.heightMaximized();
		}
		else {
			_height = _iFramePortletInstanceConfiguration.heightNormal();
		}

		return _height;
	}

	public List<KeyValuePair> getHiddenVariableKVPs() {
		List<String> hiddenVariables = ListUtil.fromArray(
			StringUtil.split(getHiddenVariables(), CharPool.PIPE));

		hiddenVariables.addAll(getIFrameVariables());

		return TransformUtil.transform(
			hiddenVariables,
			hiddenVariable -> {
				int pos = hiddenVariable.indexOf(StringPool.EQUAL);

				if (pos == -1) {
					return new KeyValuePair(StringPool.BLANK, StringPool.BLANK);
				}

				return new KeyValuePair(
					hiddenVariable.substring(0, pos),
					hiddenVariable.substring(pos + 1));
			});
	}

	public String getHiddenVariables() {
		if (_hiddenVariables != null) {
			return _hiddenVariables;
		}

		_hiddenVariables = StringUtil.merge(
			_iFramePortletInstanceConfiguration.hiddenVariables(),
			StringPool.PIPE);

		return _hiddenVariables;
	}

	public String getIframeBaseSrc() {
		if (_iFrameBaseSrc != null) {
			return _iFrameBaseSrc;
		}

		_iFrameBaseSrc = getIframeSrc();

		if (_iFrameBaseSrc.length() > 6) {
			String s = _iFrameBaseSrc.substring(7);

			int index = s.lastIndexOf(StringPool.SLASH);

			if (index != -1) {
				_iFrameBaseSrc = _iFrameBaseSrc.substring(0, index + 8);
			}
		}

		return _iFrameBaseSrc;
	}

	public IFramePortletInstanceConfiguration
		getIFramePortletInstanceConfiguration() {

		return _iFramePortletInstanceConfiguration;
	}

	public String getIframeSrc() {
		if (_iFrameSrc != null) {
			return _iFrameSrc;
		}

		_iFrameSrc = StringPool.BLANK;

		if (_iFramePortletInstanceConfiguration.relative()) {
			_iFrameSrc = _themeDisplay.getPathContext();
		}

		_iFrameSrc += (String)_request.getAttribute(IFrameWebKeys.IFRAME_SRC);

		if (ListUtil.isNotEmpty(getIFrameVariables())) {
			if (_iFrameSrc.contains(StringPool.QUESTION)) {
				_iFrameSrc += StringPool.AMPERSAND;
			}
			else {
				_iFrameSrc += StringPool.QUESTION;
			}

			_iFrameSrc += StringUtil.merge(
				getIFrameVariables(), StringPool.AMPERSAND);
		}

		return _iFrameSrc;
	}

	public List<String> getIFrameVariables() {
		List<String> iFrameVariables = new ArrayList<>();

		Enumeration<String> enumeration = _request.getParameterNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			if (name.startsWith(_IFRAME_PREFIX)) {
				iFrameVariables.add(
					name.substring(_IFRAME_PREFIX.length()) + StringPool.EQUAL +
						_request.getParameter(name));
			}
		}

		return iFrameVariables;
	}

	public String getPassword() throws PortalException {
		if (_password != null) {
			return _password;
		}

		String authType = getAuthType();

		if (authType.equals("basic")) {
			_password = _iFramePortletInstanceConfiguration.basicPassword();
		}
		else {
			_password = _iFramePortletInstanceConfiguration.formPassword();
		}

		if (Validator.isNull(_password)) {
			return StringPool.BLANK;
		}

		String passwordField =
			_iFramePortletInstanceConfiguration.passwordField();

		if (Validator.isNull(passwordField)) {
			int pos = _password.indexOf(StringPool.EQUAL);

			if (pos != -1) {
				String fieldValuePair = _password;

				passwordField = fieldValuePair.substring(0, pos);

				_password = fieldValuePair.substring(pos + 1);
			}
		}

		if (Validator.isNotNull(passwordField)) {
			_password = IFrameUtil.getPassword(_request, _password);
		}

		return _password;
	}

	public String getUserName() throws PortalException {
		if (_userName != null) {
			return _userName;
		}

		String authType = getAuthType();

		if (authType.equals("basic")) {
			_userName = _iFramePortletInstanceConfiguration.basicUserName();
		}
		else {
			_userName = _iFramePortletInstanceConfiguration.formUserName();
		}

		if (Validator.isNull(_userName)) {
			return StringPool.BLANK;
		}

		String userNameField =
			_iFramePortletInstanceConfiguration.userNameField();

		if (Validator.isNull(userNameField)) {
			int pos = _userName.indexOf(StringPool.EQUAL);

			if (pos != -1) {
				String fieldValuePair = _userName;

				userNameField = fieldValuePair.substring(0, pos);

				_userName = fieldValuePair.substring(pos + 1);
			}
		}

		if (Validator.isNotNull(userNameField)) {
			_userName = IFrameUtil.getUserName(_request, _userName);
		}

		return _userName;
	}

	private static final String _IFRAME_PREFIX = "iframe_";

	private String _authType;
	private String _formMethod;
	private String _height;
	private String _hiddenVariables;
	private String _iFrameBaseSrc;
	private final IFramePortletInstanceConfiguration
		_iFramePortletInstanceConfiguration;
	private String _iFrameSrc;
	private String _password;
	private final PortletRequest _request;
	private final ThemeDisplay _themeDisplay;
	private String _userName;

}