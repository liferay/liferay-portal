/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.model.listener.test;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Carolina Barbosa
 */
public abstract class BaseModelListenerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		company = CompanyTestUtil.addCompany();

		modifiableSystemObjectDefinition1 =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_FUNCTIONAL_COOKIE_ENTRY",
					TestPropsValues.getCompanyId());

		modifiableSystemObjectDefinition1ClassNameId =
			_classNameLocalService.getClassNameId(
				modifiableSystemObjectDefinition1.getClassName());

		_safeCloseable = CompanyThreadLocal.setCompanyIdWithSafeCloseable(
			company.getCompanyId());

		modifiableSystemObjectDefinition2 =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_FUNCTIONAL_COOKIE_ENTRY", company.getCompanyId());

		modifiableSystemObjectDefinition2ClassNameId =
			_classNameLocalService.getClassNameId(
				modifiableSystemObjectDefinition2.getClassName());

		objectDefinition1 = ObjectDefinitionTestUtil.publishObjectDefinition();

		objectDefinition1ClassNameId = _classNameLocalService.getClassNameId(
			objectDefinition1.getClassName());

		companyAdminUser = UserTestUtil.getAdminUser(company.getCompanyId());

		group = GroupTestUtil.addGroup(
			company.getCompanyId(), companyAdminUser.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		objectDefinition2 =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, companyAdminUser.getUserId(), 0,
				objectDefinition1.getClassName(), null, true, false, true,
				false, true, false, true, true, true, null,
				RandomTestUtil.randomLocaleStringMap(),
				objectDefinition1.getShortName(), null, null,
				RandomTestUtil.randomLocaleStringMap(), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						StringUtil.randomId()
					).build()),
				Collections.emptyList(),
				ServiceContextTestUtil.getServiceContext());

		objectDefinition2 =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				companyAdminUser.getUserId(),
				objectDefinition2.getObjectDefinitionId());

		objectDefinition2ClassNameId = _classNameLocalService.getClassNameId(
			objectDefinition2.getClassName());

		serviceContext = ServiceContextTestUtil.getServiceContext(
			group.getGroupId(), companyAdminUser.getUserId());

		ExportImportThreadLocal.setPortletImportInProcess(true);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_safeCloseable.close();

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition1);
		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition2);

		_companyLocalService.deleteCompany(company);

		ExportImportThreadLocal.setPortletImportInProcess(false);
	}

	@Test
	public void testOnBeforeCreate() throws Exception {
		_assertObjectDefinitionSettings();
	}

	@Test
	public void testOnBeforeUpdate() throws Exception {
		_assertObjectDefinitionSettings();
	}

	protected static Company company;
	protected static User companyAdminUser;
	protected static Group group;
	protected static ObjectDefinition modifiableSystemObjectDefinition1;
	protected static long modifiableSystemObjectDefinition1ClassNameId;
	protected static ObjectDefinition modifiableSystemObjectDefinition2;
	protected static long modifiableSystemObjectDefinition2ClassNameId;
	protected static ObjectDefinition objectDefinition1;
	protected static long objectDefinition1ClassNameId;
	protected static ObjectDefinition objectDefinition2;
	protected static long objectDefinition2ClassNameId;
	protected static ServiceContext serviceContext;

	private void _assertObjectDefinitionSettings() {
		ObjectDefinitionSetting objectDefinitionSetting =
			_objectDefinitionSettingLocalService.fetchObjectDefinitionSetting(
				objectDefinition2.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.NAME_OLD_CLASS_NAME);

		Assert.assertEquals(
			objectDefinition1.getClassName(),
			objectDefinitionSetting.getValue());

		objectDefinitionSetting =
			_objectDefinitionSettingLocalService.fetchObjectDefinitionSetting(
				objectDefinition2.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.NAME_OLD_CLASS_NAME_ID);

		Assert.assertEquals(
			String.valueOf(objectDefinition1ClassNameId),
			objectDefinitionSetting.getValue());
	}

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	@Inject
	private static CompanyLocalService _companyLocalService;

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	private static SafeCloseable _safeCloseable;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

}