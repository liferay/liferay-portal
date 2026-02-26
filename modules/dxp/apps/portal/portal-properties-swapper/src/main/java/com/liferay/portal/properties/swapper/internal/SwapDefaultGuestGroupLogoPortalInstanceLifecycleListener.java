/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.properties.swapper.internal;

import com.liferay.osgi.util.configuration.ConfigurationPersistenceUtil;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.InputStream;

import java.net.URL;

import java.util.Collections;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(enabled = false, service = PortalInstanceLifecycleListener.class)
public class SwapDefaultGuestGroupLogoPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public long getLastModifiedTime() {
		return _lastModifiedTime;
	}

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (company.getCompanyId() != _defaultCompanyId) {
			return;
		}

		Group group = _groupLocalService.getGroup(
			company.getCompanyId(), GroupConstants.GUEST);

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			group.getGroupId(), false);

		if (layoutSet.getLogoId() != 0) {
			return;
		}

		Bundle bundle = FrameworkUtil.getBundle(
			SwapDefaultGuestGroupLogoPortalInstanceLifecycleListener.class);

		URL url = bundle.getResource(
			"com/liferay/portal/properties/swapper/internal" +
				"/default_guest_group_logo.png");

		try (InputStream inputStream = url.openStream()) {
			_layoutSetLocalService.updateLogo(
				group.getGroupId(), false, true, inputStream);
		}
	}

	@Activate
	protected void activate() throws Exception {
		_lastModifiedTime = ConfigurationPersistenceUtil.update(
			this,
			Collections.singletonMap(
				PropsKeys.COMPANY_DEFAULT_WEB_ID,
				PropsValues.COMPANY_DEFAULT_WEB_ID));

		_defaultCompanyId = _portal.getDefaultCompanyId();
	}

	private long _defaultCompanyId;

	@Reference
	private GroupLocalService _groupLocalService;

	private long _lastModifiedTime;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private Portal _portal;

}