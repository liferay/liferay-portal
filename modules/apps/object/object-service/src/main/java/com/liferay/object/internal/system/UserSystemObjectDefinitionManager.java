/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.system;

import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.resource.v1_0.UserAccountResource;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.DateObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.system.BaseSystemObjectDefinitionManager;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserTable;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
@Component(service = SystemObjectDefinitionManager.class)
public class UserSystemObjectDefinitionManager
	extends BaseSystemObjectDefinitionManager {

	@Override
	public long addBaseModel(User user, Map<String, Object> values)
		throws Exception {

		UserAccountResource userAccountResource = _buildUserAccountResource(
			false, user);

		UserAccount userAccount = userAccountResource.postUserAccount(
			null, null, _toUserAccount(values));

		setExtendedProperties(
			UserAccount.class.getName(), userAccount, user, values);

		return userAccount.getId();
	}

	@Override
	public BaseModel<?> deleteBaseModel(BaseModel<?> baseModel)
		throws PortalException {

		return _userLocalService.deleteUser((User)baseModel);
	}

	@Override
	public BaseModel<?> fetchBaseModelByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _userLocalService.fetchUserByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public BaseModel<?> getBaseModelByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return _userLocalService.getUserByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public String getBaseModelExternalReferenceCode(long primaryKey)
		throws PortalException {

		User user = _userLocalService.getUser(primaryKey);

		return user.getExternalReferenceCode();
	}

	@Override
	public String getExternalReferenceCode() {
		return "L_USER";
	}

	@Override
	public JaxRsApplicationDescriptor getJaxRsApplicationDescriptor() {
		return new JaxRsApplicationDescriptor(
			"Liferay.Headless.Admin.User", "headless-admin-user",
			"user-accounts", "v1.0");
	}

	@Override
	public Map<String, String> getLabelKeys() {
		return HashMapBuilder.put(
			"label", "user"
		).put(
			"pluralLabel", "users"
		).build();
	}

	@Override
	public Class<?> getModelClass() {
		return User.class;
	}

	@Override
	public List<ObjectField> getObjectFields() {
		return Arrays.asList(
			new TextObjectFieldBuilder(
			).dbColumnName(
				"middleName"
			).labelMap(
				createLabelMap("middle-name")
			).name(
				"additionalName"
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).dbColumnName(
				"screenName"
			).labelMap(
				createLabelMap("screen-name")
			).name(
				"alternateName"
			).required(
				true
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).labelMap(
				createLabelMap("email-address")
			).name(
				"emailAddress"
			).required(
				true
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).dbColumnName(
				"lastName"
			).labelMap(
				createLabelMap("last-name")
			).name(
				"familyName"
			).required(
				true
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).dbColumnName(
				"firstName"
			).labelMap(
				createLabelMap("first-name")
			).name(
				"givenName"
			).required(
				true
			).system(
				true
			).build(),
			new DateObjectFieldBuilder(
			).dbColumnName(
				"lastLoginDate"
			).labelMap(
				createLabelMap("last-login-date")
			).name(
				"lastLoginDate"
			).system(
				true
			).build(),
			new TextObjectFieldBuilder(
			).dbColumnName(
				"uuid_"
			).labelMap(
				createLabelMap("uuid")
			).name(
				"uuid"
			).system(
				true
			).build());
	}

	@Override
	public Page<?> getPage(
			User user, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		UserAccountResource userAccountResource = _buildUserAccountResource(
			true, user);

		return userAccountResource.getUserAccountsPage(
			search, filter, pagination, sorts);
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return UserTable.INSTANCE.userId;
	}

	@Override
	public String getScope() {
		return ObjectDefinitionConstants.SCOPE_COMPANY;
	}

	@Override
	public Table getTable() {
		return UserTable.INSTANCE;
	}

	@Override
	public String getTitleObjectFieldName() {
		return "givenName";
	}

	@Override
	public Map<String, Object> getVariables(
		String contentType, ObjectDefinition objectDefinition,
		boolean oldValues, JSONObject payloadJSONObject) {

		Map<String, Object> variables = super.getVariables(
			contentType, objectDefinition, oldValues, payloadJSONObject);

		if (variables.containsKey("firstName")) {
			variables.put("givenName", variables.get("firstName"));
		}

		if (variables.containsKey("lastName")) {
			variables.put("familyName", variables.get("lastName"));
		}

		if (variables.containsKey("middleName")) {
			variables.put("additionalName", variables.get("middleName"));
		}

		if (variables.containsKey("screenName")) {
			variables.put("alternateName", variables.get("screenName"));
		}

		return variables;
	}

	@Override
	public int getVersion() {
		return 4;
	}

	@Override
	public void updateBaseModel(
			long primaryKey, User user, Map<String, Object> values)
		throws Exception {

		UserAccountResource userAccountResource = _buildUserAccountResource(
			false, user);

		UserAccount userAccount = userAccountResource.patchUserAccount(
			primaryKey, _toUserAccount(values));

		setExtendedProperties(
			UserAccount.class.getName(), userAccount, user, values);
	}

	private UserAccountResource _buildUserAccountResource(
		boolean checkPermissions, User user) {

		UserAccountResource.Builder builder =
			_userAccountResourceFactory.create();

		return builder.checkPermissions(
			checkPermissions
		).preferredLocale(
			user.getLocale()
		).user(
			user
		).build();
	}

	private UserAccount _toUserAccount(Map<String, Object> values) {
		return new UserAccount() {
			{
				setAdditionalName(
					() -> GetterUtil.getString(values.get("additionalName")));
				setAlternateName(
					() -> GetterUtil.getString(values.get("alternateName")));
				setEmailAddress(
					() -> GetterUtil.getString(values.get("emailAddress")));
				setExternalReferenceCode(
					() -> GetterUtil.getString(
						values.get("externalReferenceCode")));
				setFamilyName(
					() -> GetterUtil.getString(values.get("familyName")));
				setGivenName(
					() -> GetterUtil.getString(values.get("givenName")));
			}
		};
	}

	@Reference
	private UserAccountResource.Factory _userAccountResourceFactory;

	@Reference
	private UserLocalService _userLocalService;

}