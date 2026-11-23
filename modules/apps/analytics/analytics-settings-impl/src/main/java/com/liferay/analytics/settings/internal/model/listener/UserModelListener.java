/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.internal.model.listener;

import com.liferay.analytics.batch.exportimport.model.listener.BaseAnalyticsDXPEntityModelListener;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.internal.scheduler.AnalyticsDataControlTasksSchedulerJobConfiguration;
import com.liferay.analytics.settings.security.constants.AnalyticsSecurityConstants;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Http;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(service = ModelListener.class)
public class UserModelListener
	extends BaseAnalyticsDXPEntityModelListener<User> {

	@Override
	public void onAfterRemove(User user) throws ModelListenerException {
		if (!analyticsConfigurationRegistry.isActive() || !isTracked(user)) {
			return;
		}

		_emailAddresses.putIfAbsent(
			user.getCompanyId(), Collections.synchronizedSet(new HashSet<>()));

		Set<String> emailAddresses = _emailAddresses.get(user.getCompanyId());

		emailAddresses.add(user.getEmailAddress());
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceRegistration = bundleContext.registerService(
			SchedulerJobConfiguration.class,
			new AnalyticsDataControlTasksSchedulerJobConfiguration(
				analyticsConfigurationRegistry, _emailAddresses, _http),
			null);
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	@Override
	protected boolean isTracked(User user) {
		if ((user == null) ||
			Objects.equals(
				user.getScreenName(),
				AnalyticsSecurityConstants.SCREEN_NAME_ANALYTICS_ADMIN)) {

			return false;
		}

		AnalyticsConfiguration analyticsConfiguration =
			analyticsConfigurationRegistry.getAnalyticsConfiguration(
				user.getCompanyId());

		if (analyticsConfiguration.syncAllContacts()) {
			return true;
		}

		long[] organizationIds = null;

		try {
			organizationIds = user.getOrganizationIds();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}

		for (long organizationId : organizationIds) {
			if (ArrayUtil.contains(
					analyticsConfiguration.syncedOrganizationIds(),
					String.valueOf(organizationId))) {

				return true;
			}
		}

		for (long userGroupId : user.getUserGroupIds()) {
			if (ArrayUtil.contains(
					analyticsConfiguration.syncedUserGroupIds(),
					String.valueOf(userGroupId))) {

				return true;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserModelListener.class);

	private final Map<Long, Set<String>> _emailAddresses =
		new ConcurrentHashMap<>();

	@Reference
	private Http _http;

	private ServiceRegistration<SchedulerJobConfiguration> _serviceRegistration;

}