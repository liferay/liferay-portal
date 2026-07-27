/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.Serializable;

import java.security.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.KeyGenerator;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;

/**
 * @author Jonathan McCann
 */
public abstract class BaseTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		_objectEncryptionAlgorithmSafeCloseable =
			PropsValuesTestUtil.swapWithSafeCloseable(
				"OBJECT_ENCRYPTION_ALGORITHM", "AES");
		_objectEncryptionEnabledSafeCloseable =
			PropsValuesTestUtil.swapWithSafeCloseable(
				"OBJECT_ENCRYPTION_ENABLED", true);

		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(128);

		Key key = keyGenerator.generateKey();

		_objectEncryptionKeySafeCloseable =
			PropsValuesTestUtil.swapWithSafeCloseable(
				"OBJECT_ENCRYPTION_KEY", Base64.encode(key.getEncoded()));

		_group = GroupTestUtil.addGroup();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.seo.studio.site.initializer");

		siteInitializer.initialize(_group.getGroupId());

		seoStudioDomainObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_DOMAIN", TestPropsValues.getCompanyId());
		_seoStudioInstanceObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_INSTANCE", TestPropsValues.getCompanyId());

		_updateSEOStudioScanObjectActions(false);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_updateSEOStudioScanObjectActions(true);

		ServiceContextThreadLocal.popServiceContext();

		_objectEncryptionAlgorithmSafeCloseable.close();
		_objectEncryptionEnabledSafeCloseable.close();
		_objectEncryptionKeySafeCloseable.close();

		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		PrincipalThreadLocal.setName(_originalName);
	}

	@Before
	public void setUp() throws Exception {
		accountEntry = _addAccountEntry();

		_seoStudioInstanceObjectEntry = _addSEOStudioInstanceObjectEntry();
	}

	protected ObjectEntry addObjectEntry(
			ObjectDefinition objectDefinition, Map<String, Serializable> values)
		throws Exception {

		ObjectEntry objectEntry = objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_objectEntries.add(objectEntry);

		return objectEntry;
	}

	protected ObjectEntry addSEOStudioDomainObjectEntry(
			boolean autoScanEnabled, String hostname, String scanConfigJSON)
		throws Exception {

		return addObjectEntry(
			seoStudioDomainObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"autoScanEnabled", autoScanEnabled
			).put(
				"hostname", hostname
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_accountToSEOStudioDomains_accountEntryId",
				accountEntry.getAccountEntryId()
			).put(
				"r_seoStudioInstanceToSEOStudioDomains_seoStudioInstanceId",
				_seoStudioInstanceObjectEntry.getObjectEntryId()
			).put(
				"scanConfig", scanConfigJSON
			).put(
				"scanFrequency", "daily"
			).put(
				"scanTime", "09:00"
			).build());
	}

	protected ObjectEntry fetchSEOStudioScanRunObjectEntry(
			ObjectEntry seoStudioDomainObjectEntry)
		throws Exception {

		List<ObjectEntry> seoStudioScanRunObjectEntries =
			getRelatedObjectEntries(
				seoStudioDomainObjectEntry,
				"seoStudioDomainToSEOStudioScanRuns");

		if (ListUtil.isEmpty(seoStudioScanRunObjectEntries)) {
			return null;
		}

		return seoStudioScanRunObjectEntries.get(0);
	}

	protected List<ObjectEntry> getRelatedObjectEntries(
			ObjectEntry objectEntry, String objectRelationshipName)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.fetchObjectRelationship(
				objectEntry.getObjectDefinitionId(), objectRelationshipName);

		return objectEntryLocalService.getOneToManyObjectEntries(
			objectEntry.getGroupId(),
			objectRelationship.getObjectRelationshipId(), null, true,
			objectEntry.getObjectEntryId(), true, null, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	protected List<ObjectEntry> getSEOStudioScanObjectEntries(
			ObjectEntry seoStudioScanRunObjectEntry)
		throws Exception {

		return getRelatedObjectEntries(
			seoStudioScanRunObjectEntry, "seoStudioScanRunToSEOStudioScans");
	}

	protected void partialUpdateObjectEntry(
			ObjectEntry objectEntry, Map<String, Serializable> values)
		throws Exception {

		objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			values,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	protected static ObjectDefinition seoStudioDomainObjectDefinition;

	protected AccountEntry accountEntry;

	@Inject
	protected ObjectEntryLocalService objectEntryLocalService;

	protected ObjectEntry seoStudioDomainObjectEntry;

	private static void _updateSEOStudioScanObjectActions(boolean active)
		throws Exception {

		ObjectDefinition seoStudioScanObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_SCAN", TestPropsValues.getCompanyId());

		for (ObjectAction objectAction :
				_objectActionLocalService.getObjectActions(
					seoStudioScanObjectDefinition.getObjectDefinitionId())) {

			objectAction.setActive(active);

			_objectActionLocalService.updateObjectAction(objectAction);
		}
	}

	private AccountEntry _addAccountEntry() throws Exception {
		return _accountEntryLocalService.addAccountEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null, null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _addSEOStudioInstanceObjectEntry() throws Exception {
		return addObjectEntry(
			_seoStudioInstanceObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"hostname", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_accountToSEOStudioInstances_accountEntryId",
				accountEntry.getAccountEntryId()
			).build());
	}

	private static Group _group;

	@Inject
	private static ObjectActionLocalService _objectActionLocalService;

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	private static SafeCloseable _objectEncryptionAlgorithmSafeCloseable;
	private static SafeCloseable _objectEncryptionEnabledSafeCloseable;
	private static SafeCloseable _objectEncryptionKeySafeCloseable;
	private static String _originalName;
	private static PermissionChecker _originalPermissionChecker;
	private static ObjectDefinition _seoStudioInstanceObjectDefinition;

	@Inject
	private static SiteInitializerRegistry _siteInitializerRegistry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@DeleteAfterTestRun
	private List<ObjectEntry> _objectEntries = new ArrayList<>();

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private ObjectEntry _seoStudioInstanceObjectEntry;

}