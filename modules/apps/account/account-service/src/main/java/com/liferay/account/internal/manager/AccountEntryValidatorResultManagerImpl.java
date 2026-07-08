/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.manager;

import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.account.manager.AccountEntryValidatorResultManager;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tancredi Covioli
 */
@Component(service = AccountEntryValidatorResultManager.class)
public class AccountEntryValidatorResultManagerImpl
	implements AccountEntryValidatorResultManager {

	@Override
	public void addAccountEntryValidatorResult(
			AccountEntry accountEntry,
			AccountEntryValidatorResult accountEntryValidatorResult,
			String className)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					_OBJECT_DEFINITION_ERC_ACCOUNT_VALIDATOR_RESULT,
					accountEntry.getCompanyId());

		if (objectDefinition == null) {
			return;
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(accountEntry.getCompanyId());
		serviceContext.setUserId(accountEntry.getUserId());

		_objectEntryLocalService.addObjectEntry(
			0, accountEntry.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"className", className
			).put(
				"classPK", accountEntryValidatorResult.getClassPK()
			).put(
				"data", accountEntryValidatorResult.getAdditionalProps()
			).put(
				"r_accountToAccountValidatorResults_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"resultMessage", accountEntryValidatorResult.getResultMessage()
			).put(
				"resultStatus", accountEntryValidatorResult.getResultStatus()
			).build(),
			serviceContext);
	}

	@Override
	public AccountEntryValidatorResult getValidAccountEntryValidatorResult(
			AccountEntry accountEntry, int checkInterval, String className,
			String classPK)
		throws PortalException {

		ObjectEntry objectEntry = _fetchLastObjectEntry(
			accountEntry, className, classPK);

		if (objectEntry == null) {
			return null;
		}

		String resultStatus = MapUtil.getString(
			objectEntry.getValues(), "resultStatus");

		if (!Objects.equals(
				AccountEntryValidatorConstants.RESULT_MANUAL, resultStatus) &&
			!Objects.equals(
				AccountEntryValidatorConstants.RESULT_SUCCESS, resultStatus)) {

			return null;
		}

		int daysBetween = DateUtil.getDaysBetween(
			objectEntry.getCreateDate(), new Date());

		if (daysBetween >= checkInterval) {
			return null;
		}

		return AccountEntryValidatorResult.builder(
			classPK
		).additionalProps(
			_jsonFactory.safeCreateJSONObject(
				MapUtil.getString(objectEntry.getValues(), "data"))
		).resultMessage(
			MapUtil.getString(objectEntry.getValues(), "resultMessage")
		).resultStatus(
			resultStatus
		).build();
	}

	private ObjectEntry _fetchLastObjectEntry(
			AccountEntry accountEntry, String className, String classPK)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					_OBJECT_DEFINITION_ERC_ACCOUNT_VALIDATOR_RESULT,
					accountEntry.getCompanyId());

		if (objectDefinition == null) {
			return null;
		}

		boolean skipObjectEntryResourcePermission =
			ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission();

		ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

		try {
			String filterString = StringBundler.concat(
				"(className eq '", className, "') and (classPK eq '", classPK,
				"') and (r_accountToAccountValidatorResults_accountEntryId eq ",
				"'", accountEntry.getAccountEntryId(), "')");

			List<Long> primaryKeys = _objectEntryLocalService.getPrimaryKeys(
				new Long[] {0L}, accountEntry.getCompanyId(),
				accountEntry.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				_filterFactory.create(filterString, objectDefinition), false,
				null, 0, 1,
				new Sort[] {new Sort(Field.CREATE_DATE, Sort.LONG_TYPE, true)});

			if (primaryKeys.isEmpty()) {
				return null;
			}

			return _objectEntryLocalService.fetchObjectEntry(
				primaryKeys.get(0));
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(
				skipObjectEntryResourcePermission);
		}
	}

	private static final String
		_OBJECT_DEFINITION_ERC_ACCOUNT_VALIDATOR_RESULT =
			"L_ACCOUNT_VALIDATOR_RESULT";

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}