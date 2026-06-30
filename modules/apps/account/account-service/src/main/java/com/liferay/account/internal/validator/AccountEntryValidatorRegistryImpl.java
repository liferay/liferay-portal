/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.validator;

import com.liferay.account.configuration.AccountEntryValidatorConfiguration;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidator;
import com.liferay.account.validator.AccountEntryValidatorRegistry;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory.ServiceWrapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Tancredi Covioli
 */
@Component(service = AccountEntryValidatorRegistry.class)
public class AccountEntryValidatorRegistryImpl
	implements AccountEntryValidatorRegistry {

	@Override
	public AccountEntryValidator getAccountEntryValidator(String key) {
		if (Validator.isNull(key)) {
			return null;
		}

		ServiceWrapper<AccountEntryValidator>
			accountEntryValidatorServiceWrapper = _serviceTrackerMap.getService(
				key);

		if (accountEntryValidatorServiceWrapper == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"No account entry validator registered with key " + key);
			}

			return null;
		}

		return accountEntryValidatorServiceWrapper.getService();
	}

	@Override
	public List<AccountEntryValidator> getAccountEntryValidators() {
		return Collections.unmodifiableList(
			TransformUtil.transform(
				_serviceTrackerMap.values(), ServiceWrapper::getService));
	}

	@Override
	public List<AccountEntryValidatorResult> validate(
			AccountEntry accountEntry, JSONObject jsonObject)
		throws PortalException {

		if (accountEntry == null) {
			return Collections.emptyList();
		}

		return TransformUtil.transform(
			getAccountEntryValidators(),
			accountEntryValidator -> {
				AccountEntryValidatorConfiguration
					accountEntryValidatorConfiguration =
						accountEntryValidator.
							getAccountEntryValidatorConfiguration(
								accountEntry.getCompanyId());

				if (!accountEntryValidatorConfiguration.enabled()) {
					return null;
				}

				return accountEntryValidator.validate(accountEntry, jsonObject);
			});
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, AccountEntryValidator.class,
			"account.entry.validator.key",
			ServiceTrackerCustomizerFactory.
				<AccountEntryValidator>serviceWrapper(bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountEntryValidatorRegistryImpl.class);

	private ServiceTrackerMap<String, ServiceWrapper<AccountEntryValidator>>
		_serviceTrackerMap;

}