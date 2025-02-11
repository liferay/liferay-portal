/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.scope;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Portal;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "object.scope.provider.key=" + ObjectDefinitionConstants.SCOPE_DEPOT,
	service = ObjectScopeProvider.class
)
public class DepotObjectScopeProviderImpl implements ObjectScopeProvider {

	@Override
	public long getGroupId(HttpServletRequest httpServletRequest)
		throws PortalException {

		return _portal.getScopeGroupId(httpServletRequest);
	}

	@Override
	public String getKey() {
		return ObjectDefinitionConstants.SCOPE_DEPOT;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "asset-library");
	}

	@Override
	public String[] getRootPanelCategoryKeys() {
		return new String[0];
	}

	@Override
	public boolean isGroupAware() {
		return true;
	}

	@Override
	public boolean isValidGroupId(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if ((group != null) && group.isDepot()) {
			return true;
		}

		return false;
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}