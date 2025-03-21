/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.nested.portlets.web.internal.layout.portlet;

import com.liferay.layout.portlet.PortletManager;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.util.PortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "jakarta.portlet.name=" + PortletKeys.NESTED_PORTLETS,
	service = PortletManager.class
)
public class NestedPortletsPortletManager implements PortletManager {

	@Override
	public boolean isVisible(Layout layout) {
		if (layout.isTypeAssetDisplay() || layout.isTypeContent()) {
			return false;
		}

		return true;
	}

}