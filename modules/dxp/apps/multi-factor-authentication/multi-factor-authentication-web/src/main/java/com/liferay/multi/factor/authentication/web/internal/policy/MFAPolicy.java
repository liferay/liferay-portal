/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.web.internal.policy;

import com.liferay.multi.factor.authentication.email.otp.configuration.MFAEmailOTPConfiguration;
import com.liferay.multi.factor.authentication.spi.checker.browser.BrowserMFAChecker;
import com.liferay.multi.factor.authentication.spi.checker.headless.HeadlessMFAChecker;
import com.liferay.multi.factor.authentication.web.internal.system.configuration.MFASystemConfiguration;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marta Medio
 */
@Component(service = MFAPolicy.class)
public class MFAPolicy {

	public List<BrowserMFAChecker> getAvailableBrowserMFACheckers(
		long companyId, long userId) {

		List<BrowserMFAChecker> browserMFACheckers =
			_browserMFACheckerServiceTrackerMap.getService(companyId);

		if (browserMFACheckers == null) {
			return Collections.emptyList();
		}

		return ListUtil.filter(
			browserMFACheckers,
			browserMFAChecker -> browserMFAChecker.isAvailable(userId));
	}

	public List<HeadlessMFAChecker> getAvailableHeadlessMFACheckers(
		long companyId, long userId) {

		List<HeadlessMFAChecker> headlessMFACheckers =
			_headlessMFACheckerServiceTrackerMap.getService(companyId);

		if (headlessMFACheckers == null) {
			return Collections.emptyList();
		}

		return ListUtil.filter(
			headlessMFACheckers,
			headlessMFAChecker -> headlessMFAChecker.isAvailable(userId));
	}

	public boolean isMFAEnabled(long companyId) {
		try {
			MFASystemConfiguration mfaSystemConfiguration =
				_configurationProvider.getSystemConfiguration(
					MFASystemConfiguration.class);

			if (!mfaSystemConfiguration.disableGlobally()) {
				MFAEmailOTPConfiguration mfaEmailOTPConfiguration =
					_configurationProvider.getCompanyConfiguration(
						MFAEmailOTPConfiguration.class, companyId);

				return mfaEmailOTPConfiguration.enabled();
			}

			return false;
		}
		catch (ConfigurationException configurationException) {
			throw new SystemException(configurationException);
		}
	}

	public boolean isSatisfied(
		long companyId, HttpServletRequest httpServletRequest, long userId) {

		for (HeadlessMFAChecker headlessMFAChecker :
				getAvailableHeadlessMFACheckers(companyId, userId)) {

			if (headlessMFAChecker.verifyHeadlessRequest(
					httpServletRequest, userId)) {

				return true;
			}
		}

		for (BrowserMFAChecker browserMFAChecker :
				getAvailableBrowserMFACheckers(companyId, userId)) {

			if (browserMFAChecker.isBrowserVerified(
					httpServletRequest, userId)) {

				return true;
			}
		}

		return false;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_browserMFACheckerServiceTrackerMap =
			ServiceTrackerMapFactory.openMultiValueMap(
				bundleContext, BrowserMFAChecker.class, "(companyId=*)",
				new PropertyServiceReferenceMapper<>("companyId"));
		_headlessMFACheckerServiceTrackerMap =
			ServiceTrackerMapFactory.openMultiValueMap(
				bundleContext, HeadlessMFAChecker.class, "(companyId=*)",
				new PropertyServiceReferenceMapper<>("companyId"));
	}

	@Deactivate
	protected void deactivate() {
		_browserMFACheckerServiceTrackerMap.close();
	}

	private ServiceTrackerMap<Long, List<BrowserMFAChecker>>
		_browserMFACheckerServiceTrackerMap;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private ServiceTrackerMap<Long, List<HeadlessMFAChecker>>
		_headlessMFACheckerServiceTrackerMap;

}