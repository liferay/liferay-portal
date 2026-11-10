/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.internal.tracker;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.redirect.internal.configuration.RedirectConfiguration;
import com.liferay.redirect.service.RedirectNotFoundEntryLocalService;
import com.liferay.redirect.tracker.RedirectNotFoundTracker;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	configurationPid = "com.liferay.redirect.internal.configuration.RedirectConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = RedirectNotFoundTracker.class
)
public class RedirectNotFoundTrackerImpl implements RedirectNotFoundTracker {

	@Override
	public void trackURL(Group group, String url) {
		if (_redirectConfiguration.enabled()) {
			_redirectNotFoundEntryLocalService.addOrUpdateRedirectNotFoundEntry(
				group, url);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_redirectConfiguration = ConfigurableUtil.createConfigurable(
			RedirectConfiguration.class, properties);
	}

	private volatile RedirectConfiguration _redirectConfiguration;

	@Reference
	private RedirectNotFoundEntryLocalService
		_redirectNotFoundEntryLocalService;

}