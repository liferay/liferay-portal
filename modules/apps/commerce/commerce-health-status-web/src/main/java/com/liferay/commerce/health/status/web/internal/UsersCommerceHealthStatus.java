/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.health.status.web.internal;

import com.liferay.commerce.constants.CommerceHealthStatusConstants;
import com.liferay.commerce.health.status.CommerceHealthStatus;
import com.liferay.commerce.helper.CommerceRoleHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"commerce.health.status.display.order:Integer=210",
		"commerce.health.status.key=" + CommerceHealthStatusConstants.USERS_COMMERCE_HEALTH_STATUS_KEY
	},
	service = CommerceHealthStatus.class
)
public class UsersCommerceHealthStatus implements CommerceHealthStatus {

	@Override
	public void fixIssue(HttpServletRequest httpServletRequest)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				_portal.getCompanyId(httpServletRequest), "LPD-10562")) {

			throw new UnsupportedOperationException();
		}

		try {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				httpServletRequest);

			serviceContext.setAttribute("forceReloadPermissions", Boolean.TRUE);

			UserRoleCallable userRoleCallable = new UserRoleCallable(
				serviceContext);

			TransactionInvokerUtil.invoke(_transactionConfig, userRoleCallable);
		}
		catch (Throwable throwable) {
			_log.error(throwable, throwable);
		}
	}

	@Override
	public String getDescription(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(
			resourceBundle,
			CommerceHealthStatusConstants.
				USERS_COMMERCE_HEALTH_STATUS_DESCRIPTION);
	}

	@Override
	public String getKey() {
		return CommerceHealthStatusConstants.USERS_COMMERCE_HEALTH_STATUS_KEY;
	}

	@Override
	public String getName(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(
			resourceBundle,
			CommerceHealthStatusConstants.USERS_COMMERCE_HEALTH_STATUS_KEY);
	}

	@Override
	public int getType() {
		return CommerceHealthStatusConstants.
			COMMERCE_HEALTH_STATUS_TYPE_VIRTUAL_INSTANCE;
	}

	@Override
	public boolean isActive() {
		return FeatureFlagManagerUtil.isEnabled("LPD-10562");
	}

	@Override
	public boolean isFixed(long companyId, long commerceChannelId)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-10562")) {
			return true;
		}

		return _commerceRoleHelper.hasCommerceUserPermissions(companyId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UsersCommerceHealthStatus.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private CommerceRoleHelper _commerceRoleHelper;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private class UserRoleCallable implements Callable<Object> {

		@Override
		public Object call() throws Exception {
			_commerceRoleHelper.checkCommerceUserRoles(_serviceContext);

			return null;
		}

		private UserRoleCallable(ServiceContext serviceContext) {
			_serviceContext = serviceContext;
		}

		private final ServiceContext _serviceContext;

	}

}