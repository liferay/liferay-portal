/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.scope;

import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.portal.kernel.language.Language;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "object.scope.provider.key=" + ObjectDefinitionConstants.SCOPE_COMPANY,
	service = ObjectScopeProvider.class
)
public class CompanyInstanceObjectScopeProviderImpl
	implements ObjectScopeProvider {

	@Override
	public long getGroupId(HttpServletRequest httpServletRequest) {
		return 0;
	}

	@Override
	public String getKey() {
		return ObjectDefinitionConstants.SCOPE_COMPANY;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "company");
	}

	@Override
	public String[] getRootPanelCategoryKeys() {
		return new String[] {
			PanelCategoryKeys.CONTROL_PANEL, PanelCategoryKeys.COMMERCE,
			PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS
		};
	}

	@Override
	public boolean isGroupAware() {
		return false;
	}

	@Override
	public boolean isValidGroupId(long groupId) {
		if (groupId == 0) {
			return true;
		}

		return false;
	}

	@Reference
	private Language _language;

}