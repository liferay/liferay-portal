/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v10_5_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Tavares
 */
@RunWith(Arquillian.class)
public class ObjectEntryDefaultLanguageIdUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = CompanyTestUtil.addCompany();
		_group = GroupTestUtil.addGroup();
		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testUpgrade() throws Exception {
		User companyAdminUser = UserTestUtil.addCompanyAdminUser(_company);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(companyAdminUser));
		PrincipalThreadLocal.setName(companyAdminUser.getUserId());

		CompanyTestUtil.resetCompanyLocales(
			_company.getCompanyId(),
			Set.of(LocaleUtil.BRAZIL, LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.BRAZIL);

		ObjectDefinition companyObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"textObjectField"
					).build()),
				ObjectDefinitionConstants.SCOPE_COMPANY,
				companyAdminUser.getUserId());

		ObjectEntry companyObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0, companyAdminUser.getUserId(),
				companyObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				Collections.singletonMap(
					"textObjectField", RandomTestUtil.randomString()),
				ServiceContextTestUtil.getServiceContext());

		companyObjectEntry.setDefaultLanguageId(null);

		companyObjectEntry = _objectEntryLocalService.updateObjectEntry(
			companyObjectEntry);

		Assert.assertEquals(
			_company.getCompanyId(), companyObjectEntry.getCompanyId());
		Assert.assertTrue(
			Validator.isNull(companyObjectEntry.getDefaultLanguageId()));

		_group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), Arrays.asList(LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.SPAIN);

		ObjectDefinition siteObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"textObjectField"
					).build()),
				ObjectDefinitionConstants.SCOPE_SITE,
				companyAdminUser.getUserId());

		ObjectEntry siteObjectEntry = _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), companyAdminUser.getUserId(),
			siteObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			Collections.singletonMap(
				"textObjectField", RandomTestUtil.randomString()),
			ServiceContextTestUtil.getServiceContext());

		siteObjectEntry.setDefaultLanguageId(null);

		siteObjectEntry = _objectEntryLocalService.updateObjectEntry(
			siteObjectEntry);

		Assert.assertEquals(_group.getGroupId(), siteObjectEntry.getGroupId());
		Assert.assertTrue(
			Validator.isNull(siteObjectEntry.getDefaultLanguageId()));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}

		companyObjectEntry = _objectEntryLocalService.getObjectEntry(
			companyObjectEntry.getObjectEntryId());

		Assert.assertEquals(
			LocaleUtil.BRAZIL,
			LocaleUtil.fromLanguageId(
				companyObjectEntry.getDefaultLanguageId(), false, false));

		siteObjectEntry = _objectEntryLocalService.getObjectEntry(
			siteObjectEntry.getObjectEntryId());

		Assert.assertEquals(
			LocaleUtil.SPAIN,
			LocaleUtil.fromLanguageId(
				siteObjectEntry.getDefaultLanguageId(), false, false));
	}

	private static final String _CLASS_NAME =
		"com.liferay.object.internal.upgrade.v10_5_0." +
			"ObjectEntryDefaultLanguageIdUpgradeProcess";

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@DeleteAfterTestRun
	private Company _company;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;

}