/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.internal.instance.lifecycle;

import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.internal.constants.CompanyGroupConstants;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 * @author Petteri Karttunen
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class AddCompanyGroupPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) {
		if (FeatureFlagManagerUtil.isEnabled(
				company.getCompanyId(), "LPD-35914")) {

			_addCompanyGroup(company.getCompanyId());
		}
	}

	@Override
	public void portalInstanceUnregistered(Company company) {
		_deleteCompanyGroup(company.getCompanyId());
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceRegistration = bundleContext.registerService(
			FeatureFlagListener.class,
			(companyId, featureFlagKey, enabled) -> {
				if (enabled) {
					_addCompanyGroup(companyId);
				}
			},
			MapUtil.singletonDictionary("feature.flag.key", "LPD-35914"));
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	private void _addCompanyGroup(long companyId) {
		try {
			Group group = _groupLocalService.fetchFriendlyURLGroup(
				companyId, CompanyGroupConstants.FRIENDLY_URL);

			if (group != null) {
				return;
			}

			_groupLocalService.addGroup(
				_userLocalService.getGuestUserId(companyId),
				GroupConstants.DEFAULT_PARENT_GROUP_ID,
				StagingGroupHelper.class.getName(), CompanyConstants.SYSTEM,
				GroupConstants.DEFAULT_LIVE_GROUP_ID, null, null,
				GroupConstants.TYPE_SITE_RESTRICTED, true,
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
				CompanyGroupConstants.FRIENDLY_URL, false, true, null);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private void _deleteCompanyGroup(long companyId) {
		try {
			Group group = _groupLocalService.fetchFriendlyURLGroup(
				companyId, CompanyGroupConstants.FRIENDLY_URL);

			if (group != null) {
				_groupLocalService.deleteGroup(group);
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddCompanyGroupPortalInstanceLifecycleListener.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	private volatile ServiceRegistration<?> _serviceRegistration;

	@Reference
	private UserLocalService _userLocalService;

}