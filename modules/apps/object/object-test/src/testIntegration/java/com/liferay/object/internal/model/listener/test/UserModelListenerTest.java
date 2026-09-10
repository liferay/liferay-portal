/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.test.system.TestSystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.AuditedModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class UserModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = CompanyTestUtil.addCompany();

		CompanyTestUtil.resetCompanyLocales(
			_company.getCompanyId(),
			ListUtil.fromArray(LocaleUtil.SPAIN, LocaleUtil.US), LocaleUtil.US);

		Bundle bundle = FrameworkUtil.getBundle(UserModelListenerTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			SystemObjectDefinitionManager.class,
			new TestSystemObjectDefinitionManager(
				ObjectEntry.class, _OBJECT_DEFINITION_NAME,
				StringBundler.concat(
					"/o/", RandomTestUtil.randomString(), StringPool.SLASH,
					RandomTestUtil.randomString())),
			new HashMapDictionary<>());
	}

	@After
	public void tearDown() throws Exception {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}

		UserTestUtil.setUser(
			UserTestUtil.getAdminUser(_company.getCompanyId()));

		_objectDefinitionLocalService.deleteCompanyObjectDefinitions(
			_company.getCompanyId());

		UserTestUtil.setUser(TestPropsValues.getUser());

		_companyLocalService.deleteCompany(_company.getCompanyId());
	}

	@Test
	public void testOnAfterRemove() throws Exception {
		User user = UserTestUtil.addUser();

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(), user.getUserId());
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(), user.getUserId());

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinition1,
				objectDefinition2, user.getUserId());

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				user.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				StringUtil.randomId()
			).objectDefinitionId(
				objectDefinition1.getObjectDefinitionId()
			).build());

		_userLocalService.deleteUser(user);

		long userId = _userLocalService.getUserIdByScreenName(
			TestPropsValues.getCompanyId(), "default-service-account");

		_assertUserId(
			_objectDefinitionLocalService.getObjectDefinition(
				objectDefinition1.getObjectDefinitionId()),
			userId);
		_assertUserId(
			_objectDefinitionLocalService.getObjectDefinition(
				objectDefinition2.getObjectDefinitionId()),
			userId);
		_assertUserId(
			_objectFieldLocalService.getObjectField(
				objectField.getObjectFieldId()),
			userId);
		_assertUserId(
			_objectRelationshipLocalService.getObjectRelationship(
				objectRelationship.getObjectRelationshipId()),
			userId);
	}

	@Test
	public void testOnAfterUpdate() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_company.getCompanyId(), _OBJECT_DEFINITION_NAME);

		_assertEquals(LocaleUtil.SPAIN, objectDefinition);
		_assertEquals(LocaleUtil.US, objectDefinition);
		_assertEqualsSorted(new String[] {"es_ES", "en_US"}, objectDefinition);

		CompanyTestUtil.resetCompanyLocales(
			_company.getCompanyId(),
			ListUtil.fromArray(LocaleUtil.BRAZIL, LocaleUtil.SPAIN),
			LocaleUtil.BRAZIL);

		objectDefinition = _objectDefinitionLocalService.fetchObjectDefinition(
			_company.getCompanyId(), _OBJECT_DEFINITION_NAME);

		_assertEquals(LocaleUtil.BRAZIL, objectDefinition);
		_assertEqualsSorted(
			new String[] {"es_ES", "en_US", "pt_BR"}, objectDefinition);
	}

	private void _assertEquals(
		Locale locale, ObjectDefinition objectDefinition) {

		Assert.assertEquals(
			_language.get(locale, "test"), objectDefinition.getLabel(locale));
		Assert.assertEquals(
			_language.get(locale, "tests"),
			objectDefinition.getPluralLabel(locale));
	}

	private void _assertEqualsSorted(
		String[] availableLanguageIds, ObjectDefinition objectDefinition) {

		AssertUtils.assertEqualsSorted(
			availableLanguageIds,
			_localization.getAvailableLanguageIds(objectDefinition.getLabel()));
		AssertUtils.assertEqualsSorted(
			availableLanguageIds,
			_localization.getAvailableLanguageIds(
				objectDefinition.getPluralLabel()));
	}

	private void _assertUserId(AuditedModel auditedModel, long expectedUserId) {
		Assert.assertEquals(expectedUserId, auditedModel.getUserId());
	}

	private static final String _OBJECT_DEFINITION_NAME =
		ObjectDefinitionTestUtil.getRandomName();

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private Language _language;

	@Inject
	private Localization _localization;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private ServiceRegistration<SystemObjectDefinitionManager>
		_serviceRegistration;

	@Inject
	private UserLocalService _userLocalService;

}