/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.application.list;

import com.liferay.application.list.GroupProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 */
@Component(service = GroupProvider.class)
public class LatentGroupProvider implements GroupProvider {

	@Override
	public Group getGroup(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getScopeGroup();

		if (!group.isControlPanel()) {
			return group;
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		long groupId = GetterUtil.getLong(
			SessionClicks.get(
				originalHttpServletRequest.getSession(), _KEY_LATENT_GROUP,
				null));

		if (groupId > 0) {
			return _groupLocalService.fetchGroup(groupId);
		}

		return null;
	}

	@Override
	public void setGroup(HttpServletRequest httpServletRequest, Group group) {
		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		SessionClicks.put(
			originalHttpServletRequest.getSession(), _KEY_LATENT_GROUP,
			String.valueOf(group.getGroupId()));
	}

	private static final String _KEY_LATENT_GROUP =
		"com.liferay.site.application.list_latentGroup";

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}