/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.object.system;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryTable;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryService;
import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.resource.v1_0.AccountResource;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.system.BaseSystemObjectDefinitionManager;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 */
@Component(service = SystemObjectDefinitionManager.class)
public class AccountEntrySystemObjectDefinitionManager
	extends BaseSystemObjectDefinitionManager {

	@Override
	public long addBaseModel(User user, Map<String, Object> values)
		throws Exception {

		AccountResource accountResource = _buildAccountResource(false, user);

		Account account = accountResource.postAccount(_toAccount(values));

		setExtendedProperties(Account.class.getName(), account, user, values);

		return account.getId();
	}

	@Override
	public BaseModel<?> deleteBaseModel(BaseModel<?> baseModel)
		throws PortalException {

		return _accountEntryLocalService.deleteAccountEntry(
			(AccountEntry)baseModel);
	}

	@Override
	public BaseModel<?> fetchBaseModelByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _accountEntryLocalService.
			fetchAccountEntryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public BaseModel<?> getBaseModelByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return _accountEntryLocalService.getAccountEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public String getBaseModelExternalReferenceCode(long primaryKey)
		throws PortalException {

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			primaryKey);

		return accountEntry.getExternalReferenceCode();
	}

	@Override
	public String getExternalReferenceCode() {
		return "L_ACCOUNT";
	}

	@Override
	public JaxRsApplicationDescriptor getJaxRsApplicationDescriptor() {
		return new JaxRsApplicationDescriptor(
			"Liferay.Headless.Admin.User", "headless-admin-user", "accounts",
			"v1.0");
	}

	@Override
	public Map<String, String> getLabelKeys() {
		return HashMapBuilder.put(
			"label", "account"
		).put(
			"pluralLabel", "accounts"
		).build();
	}

	@Override
	public Class<?> getModelClass() {
		return AccountEntry.class;
	}

	@Override
	public List<ObjectField> getObjectFields() {
		return Arrays.asList(
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("description")
			).name(
				"description"
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("name")
			).name(
				"name"
			).required(
				true
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("type")
			).name(
				"type"
			).required(
				true
			).system(
				true
			).build());
	}

	@Override
	public BaseModel<?> getOrAddEmptyBaseModel(
			String externalReferenceCode, User user)
		throws PortalException {

		return _accountEntryService.getOrAddEmptyAccountEntry(
			externalReferenceCode, externalReferenceCode,
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS);
	}

	@Override
	public Page<?> getPage(
			User user, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		AccountResource accountResource = _buildAccountResource(true, user);

		return accountResource.getAccountsPage(
			search, filter, pagination, sorts);
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return AccountEntryTable.INSTANCE.accountEntryId;
	}

	@Override
	public String getScope() {
		return ObjectDefinitionConstants.SCOPE_COMPANY;
	}

	@Override
	public Table getTable() {
		return AccountEntryTable.INSTANCE;
	}

	@Override
	public String getTitleObjectFieldName() {
		return "name";
	}

	@Override
	public int getVersion() {
		return 2;
	}

	@Override
	public void updateBaseModel(
			long primaryKey, User user, Map<String, Object> values)
		throws Exception {

		AccountResource accountResource = _buildAccountResource(false, user);

		Account account = accountResource.patchAccount(
			primaryKey, _toAccount(values));

		setExtendedProperties(Account.class.getName(), account, user, values);
	}

	private AccountResource _buildAccountResource(
		boolean checkPermissions, User user) {

		AccountResource.Builder builder = _accountResourceFactory.create();

		return builder.checkPermissions(
			checkPermissions
		).preferredLocale(
			user.getLocale()
		).user(
			user
		).build();
	}

	private Account _toAccount(Map<String, Object> values) {
		return new Account() {
			{
				setDescription(
					() -> GetterUtil.getString(values.get("description")));
				setExternalReferenceCode(
					() -> GetterUtil.getString(
						values.get("externalReferenceCode")));
				setName(() -> GetterUtil.getString(values.get("name")));
				setType(
					() -> Account.Type.create(
						StringUtil.toLowerCase(
							GetterUtil.getString(values.get("type")))));
			}
		};
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private AccountResource.Factory _accountResourceFactory;

}