/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.portal.kernel.servlet.taglib.aui.ValidatorTag;
import com.liferay.taglib.aui.base.BaseFormTag;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Julio Camarero
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 */
public class FormTag extends BaseFormTag {

	@Override
	public String getAction() {
		return super.getAction();
	}

	public void setAction(PortletURL portletURL) {
		if (portletURL != null) {
			setAction(portletURL.toString());
		}
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_checkboxNames.clear();

		if (_validatorTagsMap != null) {
			for (List<ValidatorTag> validatorTags :
					_validatorTagsMap.values()) {

				for (ValidatorTag validatorTag : validatorTags) {
					validatorTag.cleanUp();
				}
			}

			_validatorTagsMap.clear();
		}
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return _CLEAN_UP_SET_ATTRIBUTES;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		httpServletRequest.setAttribute(
			"LIFERAY_SHARED_aui:form:checkboxNames", _checkboxNames);
		httpServletRequest.setAttribute(
			"LIFERAY_SHARED_aui:form:validatorTagsMap", _validatorTagsMap);
	}

	private static final boolean _CLEAN_UP_SET_ATTRIBUTES = true;

	private final List<String> _checkboxNames = new ArrayList<>();
	private final Map<String, List<ValidatorTag>> _validatorTagsMap =
		new HashMap<>();

}