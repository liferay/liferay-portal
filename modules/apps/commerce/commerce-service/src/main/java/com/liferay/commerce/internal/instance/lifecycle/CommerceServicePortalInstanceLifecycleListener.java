/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.instance.lifecycle;

import com.liferay.commerce.helper.CommerceRoleHelper;
import com.liferay.commerce.helper.CommerceSAPHelper;
import com.liferay.commerce.payment.configuration.CommercePaymentEntryRefundTypeConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.IOException;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class CommerceServicePortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		User user = _userLocalService.getGuestUser(company.getCompanyId());

		_commerceSAPHelper.addCommerceDefaultSAPEntries(
			company.getCompanyId(), user.getUserId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(company.getCompanyId());
		serviceContext.setUserId(_getAdminUserId(company.getCompanyId()));
		serviceContext.setUuid(PortalUUIDUtil.generate());

		_commerceRoleHelper.checkCommerceAccountRoles(serviceContext);

		try {
			Configuration[] configurations =
				_configurationAdmin.listConfigurations(
					StringBundler.concat(
						"(&(companyId=", company.getCompanyId(),
						")(service.pid=",
						CommercePaymentEntryRefundTypeConfiguration.class.
							getName(),
						"*))"));

			if (ArrayUtil.isNotEmpty(configurations)) {
				return;
			}

			_createFactoryConfiguration(
				_configurationAdmin.createFactoryConfiguration(
					CommercePaymentEntryRefundTypeConfiguration.class.getName(),
					StringPool.QUESTION),
				CommercePaymentEntryRefundTypeConfiguration.class.getName(),
				"damaged-in-transit", "Damaged in Transit", company);
			_createFactoryConfiguration(
				_configurationAdmin.createFactoryConfiguration(
					CommercePaymentEntryRefundTypeConfiguration.class.getName(),
					StringPool.QUESTION),
				CommercePaymentEntryRefundTypeConfiguration.class.getName(),
				"product-defect", "Product Defect", company);
			_createFactoryConfiguration(
				_configurationAdmin.createFactoryConfiguration(
					CommercePaymentEntryRefundTypeConfiguration.class.getName(),
					StringPool.QUESTION),
				CommercePaymentEntryRefundTypeConfiguration.class.getName(),
				"return", "Return", company);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _createFactoryConfiguration(
			Configuration configuration, String factoryPid, String key,
			String name, Company company)
		throws IOException {

		configuration.update(
			HashMapDictionaryBuilder.<String, Object>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, factoryPid
			).put(
				"enabled", true
			).put(
				"key", key
			).put(
				"name", name
			).put(
				"priority", "0"
			).put(
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
				company.getCompanyId()
			).put(
				"configuration.cleaner.ignore", "true"
			).build());
	}

	private long _getAdminUserId(long companyId) throws Exception {
		Role role = _roleLocalService.getRole(
			companyId, RoleConstants.ADMINISTRATOR);

		long[] userIds = _userLocalService.getRoleUserIds(role.getRoleId());

		if (userIds.length == 0) {
			throw new NoSuchUserException(
				StringBundler.concat(
					"No user exists in company ", companyId, " with role ",
					role.getName()));
		}

		return userIds[0];
	}

	@Reference
	private CommerceRoleHelper _commerceRoleHelper;

	@Reference
	private CommerceSAPHelper _commerceSAPHelper;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}