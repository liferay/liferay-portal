/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.BaseBodyTagSupport;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.tagext.BodyTag;

/**
 * @author Eduardo Lundgren
 */
public class PositionTagSupport extends BaseBodyTagSupport implements BodyTag {

	public String getPosition() {
		return getPositionValue();
	}

	public boolean isPositionAuto() {
		String position = getPosition();

		if (position.equals(_POSITION_AUTO)) {
			return true;
		}

		return false;
	}

	public boolean isPositionInLine() {
		String position = getPosition();

		if (position.equals(_POSITION_INLINE)) {
			return true;
		}

		return false;
	}

	public void setPosition(String position) {
		_position = position;
	}

	protected void cleanUp() {
		_position = null;
	}

	protected String getPositionValue() {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		String position = _position;

		String fragmentId = ParamUtil.getString(httpServletRequest, "p_f_id");

		if (Validator.isNotNull(fragmentId)) {
			position = _POSITION_INLINE;
		}

		if (Validator.isNotNull(position)) {
			return position;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			position = _POSITION_INLINE;
		}
		else {
			if (themeDisplay.isIsolated() ||
				themeDisplay.isLifecycleResource() ||
				themeDisplay.isStateExclusive()) {

				position = _POSITION_INLINE;
			}
			else {
				position = _POSITION_AUTO;
			}

			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			String portletId = portletDisplay.getId();

			if (Validator.isNotNull(portletId) &&
				themeDisplay.isPortletEmbedded(
					themeDisplay.getScopeGroupId(), themeDisplay.getLayout(),
					portletId)) {

				position = _POSITION_INLINE;
			}
		}

		return position;
	}

	private static final String _POSITION_AUTO = "auto";

	private static final String _POSITION_INLINE = "inline";

	private String _position;

}