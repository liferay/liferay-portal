/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.validator;

import com.liferay.account.configuration.AccountEntryValidatorConfiguration;
import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidator;
import com.liferay.account.validator.AccountEntryValidatorRegistry;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory.ServiceWrapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

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
	public boolean isLastResultSuccess(
			AccountEntry accountEntry, JSONObject jsonObject)
		throws PortalException {

		if (accountEntry == null) {
			return false;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_ACCOUNT_VALIDATOR_RESULT", accountEntry.getCompanyId());

		if (objectDefinition == null) {
			return false;
		}

		for (AccountEntryValidator accountEntryValidator :
				getAccountEntryValidators()) {

			AccountEntryValidatorConfiguration
				accountEntryValidatorConfiguration =
					accountEntryValidator.getConfiguration(
						accountEntry.getCompanyId());

			if (!accountEntryValidatorConfiguration.enabled()) {
				continue;
			}

			String filterString = StringBundler.concat(
				"(className eq '",
				accountEntryValidator.getClass(
				).getName(),
				"') and (classPK eq '",
				accountEntryValidator.getKey(accountEntry, jsonObject),
				"') and (", _ACCOUNT_ENTRY_RESTRICTED_FIELD_NAME, " eq '",
				accountEntry.getAccountEntryId(), "')");

			List<Map<String, Serializable>> valuesList =
				_objectEntryLocalService.getValuesList(
					0, accountEntry.getCompanyId(), accountEntry.getUserId(),
					objectDefinition.getObjectDefinitionId(),
					_filterFactory.create(filterString, objectDefinition), null,
					0, 1,
					new Sort[] {
						new Sort(Field.CREATE_DATE, Sort.LONG_TYPE, true)
					});

			if (valuesList.isEmpty()) {
				return false;
			}

			String resultStatus = (String)valuesList.get(
				0
			).get(
				"resultStatus"
			);

			if (!Objects.equals(
					AccountEntryValidatorConstants.RESULT_SUCCESS,
					resultStatus) &&
				!Objects.equals(
					AccountEntryValidatorConstants.RESULT_MANUAL,
					resultStatus)) {

				return false;
			}
		}

		return true;
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

	private static final String _ACCOUNT_ENTRY_RESTRICTED_FIELD_NAME =
		"r_accountToAccountValidatorResults_accountEntryId";

	private static final Log _log = LogFactoryUtil.getLog(
		AccountEntryValidatorRegistryImpl.class);

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	private ServiceTrackerMap<String, ServiceWrapper<AccountEntryValidator>>
		_serviceTrackerMap;

}