/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.portlet.route;

import com.liferay.portal.kernel.portlet.DefaultFriendlyURLMapper;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.trash.constants.TrashPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * Provides an implementation of <code>FriendlyURLMapper</code> (in
 * <code>com.liferay.portal.kernel</code>) to use with Recycle Bin friendly URL
 * routes. To add a friendly URL mapping to the Recycle Bin portlet, add a new
 * route to the <code>META-INF/friendly-url-routes/routes.xml</code> file.
 *
 * @author Juergen Kappler
 */
@Component(
	property = {
		"com.liferay.portlet.friendly-url-routes=META-INF/friendly-url-routes/routes.xml",
		"jakarta.portlet.name=" + TrashPortletKeys.TRASH
	},
	service = FriendlyURLMapper.class
)
public class TrashFriendlyURLMapper extends DefaultFriendlyURLMapper {

	@Override
	public String getMapping() {
		return _MAPPING;
	}

	private static final String _MAPPING = "recycle_bin";

}