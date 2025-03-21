/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context;

import com.liferay.fragment.web.internal.constants.FragmentTypeConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Jürgen Kappler
 */
public class FragmentManagementToolbarDisplayContextFactory {

	public static FragmentManagementToolbarDisplayContextFactory getInstance() {
		return _fragmentManagementToolbarDisplayContextFactory;
	}

	public FragmentManagementToolbarDisplayContext
		getFragmentManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			FragmentDisplayContext fragmentDisplayContext) {

		String type = fragmentDisplayContext.getFragmentType();

		if (Objects.equals(type, FragmentTypeConstants.BASIC_FRAGMENT_TYPE)) {
			return new BasicFragmentManagementToolbarDisplayContext(
				httpServletRequest, liferayPortletRequest,
				liferayPortletResponse, fragmentDisplayContext);
		}

		if (!Objects.equals(
				type, FragmentTypeConstants.INHERITED_FRAGMENT_TYPE)) {

			return null;
		}

		return new InheritedFragmentManagementToolbarDisplayContext(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			fragmentDisplayContext);
	}

	private FragmentManagementToolbarDisplayContextFactory() {
	}

	private static final FragmentManagementToolbarDisplayContextFactory
		_fragmentManagementToolbarDisplayContextFactory =
			new FragmentManagementToolbarDisplayContextFactory();

}