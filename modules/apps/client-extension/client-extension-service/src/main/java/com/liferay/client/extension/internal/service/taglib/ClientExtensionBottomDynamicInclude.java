/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.internal.service.taglib;

import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "dynamic.include.key=/html/common/themes/bottom.jsp#post",
	service = DynamicInclude.class
)
public class ClientExtensionBottomDynamicInclude extends BaseDynamicInclude {

	@Override
	protected String getScriptLocation() {
		return "bottom";
	}

}