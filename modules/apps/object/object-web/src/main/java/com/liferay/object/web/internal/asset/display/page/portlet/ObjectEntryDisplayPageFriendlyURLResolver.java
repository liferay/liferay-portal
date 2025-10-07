/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.asset.display.page.portlet;

import com.liferay.asset.display.page.portlet.BaseAssetDisplayPageFriendlyURLResolver;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Guilherme Camacho
 */
@Component(scope = ServiceScope.PROTOTYPE, service = FriendlyURLResolver.class)
public class ObjectEntryDisplayPageFriendlyURLResolver
	extends BaseAssetDisplayPageFriendlyURLResolver {

	@Override
	public String getDefaultURLSeparator() {
		if (_objectDefinition == null) {
			return FriendlyURLResolverConstants.URL_SEPARATOR_OBJECT_ENTRY;
		}

		return StringUtil.quote(
			_objectDefinition.getFriendlyURLSeparator(), CharPool.SLASH);
	}

	@Override
	public String getKey() {
		if (_objectDefinition == null) {
			return ObjectEntry.class.getName();
		}

		return StringUtil.replace(
			_objectDefinition.getClassName(), CharPool.POUND, CharPool.PERIOD);
	}

	@Override
	public boolean isURLSeparatorConfigurable() {
		return !FeatureFlagManagerUtil.isEnabled(
			portal.getDefaultCompanyId(), "LPD-21926");
	}

	public void setObjectDefinition(ObjectDefinition objectDefinition) {
		_objectDefinition = objectDefinition;
	}

	private ObjectDefinition _objectDefinition;

}