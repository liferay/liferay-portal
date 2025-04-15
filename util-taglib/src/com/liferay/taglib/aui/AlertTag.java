/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.aui.base.BaseAlertTag;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.frontend.taglib.clay.servlet.taglib.AlertTag}
 */
@Deprecated
public class AlertTag extends BaseAlertTag {

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		String id = getId();

		if (Validator.isNull(id)) {
			id = StringUtil.randomId();
		}

		setId(id);

		setNamespacedAttribute(httpServletRequest, "id", id);
	}

}