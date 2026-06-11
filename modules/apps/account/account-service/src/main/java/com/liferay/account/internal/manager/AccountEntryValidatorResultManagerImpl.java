/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.manager;

import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.account.manager.AccountEntryValidatorResultManager;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Time;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
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
			AccountEntry accountEntry, String className,
			AccountEntryValidatorResult accountEntryValidatorResult)
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
			AccountEntry accountEntry, String className, String classPK,
			int checkInterval)
		throws PortalException {

		ObjectEntry objectEntry = _fetchLatestObjectEntry(
			accountEntry, className, classPK);

		if (objectEntry == null) {
			return null;
		}

		Map<String, Serializable> values = objectEntry.getValues();

		String resultStatus = GetterUtil.getString(values.get("resultStatus"));

		if (!Objects.equals(
				AccountEntryValidatorConstants.RESULT_SUCCESS, resultStatus) &&
			!Objects.equals(
				AccountEntryValidatorConstants.RESULT_MANUAL, resultStatus)) {

			return null;
		}

		Date createDate = objectEntry.getCreateDate();

		long age = System.currentTimeMillis() - createDate.getTime();

		if (age >= (checkInterval * Time.DAY)) {
			return null;
		}

		return AccountEntryValidatorResult.builder(
			classPK
		).additionalProps(
			_jsonFactory.safeCreateJSONObject(
				GetterUtil.getString(values.get("data")))
		).resultMessage(
			GetterUtil.getString(values.get("resultMessage"))
		).resultStatus(
			resultStatus
		).build();
	}

	private ObjectEntry _fetchLatestObjectEntry(
			AccountEntry accountEntry, String className, String classPK)
		throws PortalException {

		long companyId = accountEntry.getCompanyId();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					_OBJECT_DEFINITION_ERC_ACCOUNT_VALIDATOR_RESULT, companyId);

		if (objectDefinition == null) {
			return null;
		}

		long objectDefinitionId = objectDefinition.getObjectDefinitionId();

		Column<?, String> classNameColumn =
			(Column<?, String>)_objectFieldLocalService.getColumn(
				objectDefinitionId, "className");
		Column<?, String> classPKColumn =
			(Column<?, String>)_objectFieldLocalService.getColumn(
				objectDefinitionId, "classPK");
		Column<?, Long> accountEntryIdColumn =
			(Column<?, Long>)_objectFieldLocalService.getColumn(
				objectDefinitionId,
				"r_accountToAccountValidatorResults_accountEntryId");

		List<Long> primaryKeys = null;

		boolean skipObjectEntryResourcePermission =
			ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission();

		ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

		try {
			primaryKeys = _objectEntryLocalService.getPrimaryKeys(
				new Long[] {0L}, companyId, accountEntry.getUserId(),
				objectDefinitionId,
				classNameColumn.eq(
					className
				).and(
					classPKColumn.eq(classPK)
				).and(
					accountEntryIdColumn.eq(accountEntry.getAccountEntryId())
				),
				false, null, 0, 1,
				new Sort[] {new Sort("id", Sort.LONG_TYPE, true)});
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(
				skipObjectEntryResourcePermission);
		}

		if (primaryKeys.isEmpty()) {
			return null;
		}

		return _objectEntryLocalService.fetchObjectEntry(primaryKeys.get(0));
	}

	private static final String
		_OBJECT_DEFINITION_ERC_ACCOUNT_VALIDATOR_RESULT =
			"L_ACCOUNT_VALIDATOR_RESULT";

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

}