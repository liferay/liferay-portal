/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.scope;

import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "object.scope.provider.key=" + ObjectDefinitionConstants.SCOPE_SITE,
	service = ObjectScopeProvider.class
)
public class SiteObjectScopeProviderImpl implements ObjectScopeProvider {

	@Override
	public long getGroupId(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			return themeDisplay.getScopeGroupId();
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return 0;
		}

		return serviceContext.getScopeGroupId();
	}

	@Override
	public String getKey() {
		return ObjectDefinitionConstants.SCOPE_SITE;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "site");
	}

	@Override
	public String[] getRootPanelCategoryKeys() {
		return new String[] {PanelCategoryKeys.SITE_ADMINISTRATION};
	}

	@Override
	public boolean isGroupAware() {
		return true;
	}

	@Override
	public boolean isValidGroupId(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if ((group != null) && (group.isSite() || group.isUserGroup())) {
			return true;
		}

		return false;
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

}