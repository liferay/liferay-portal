/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.routes;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.portal.kernel.portlet.DefaultFriendlyURLMapper;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leonardo Barros
 */
@Component(
	property = {
		"com.liferay.portlet.friendly-url-routes=META-INF/friendly-url-routes/routes.xml",
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM
	},
	service = FriendlyURLMapper.class
)
public class DDMFormFriendlyURLMapper extends DefaultFriendlyURLMapper {
}