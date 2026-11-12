/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.model.listener.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Tavares
 */
@RunWith(Arquillian.class)
public class AccountEntryOrganizationRelModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {

		// Add account entry with object entries to organization

		ObjectDefinition objectDefinition1 = _enableAccountEntryRestricted(
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				"A" + RandomTestUtil.randomString()));

		objectDefinition1 =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition1.getObjectDefinitionId());

		_objectDefinitions.add(objectDefinition1);

		ObjectDefinition objectDefinition2 = _enableAccountEntryRestricted(
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.emptyList()));

		_objectDefinitions.add(objectDefinition2);

		ObjectDefinition objectDefinition3 = _enableAccountEntryRestricted(
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())),
				ObjectDefinitionConstants.SCOPE_SITE));

		_objectDefinitions.add(objectDefinition3);

		AccountEntry accountEntry1 = _addAccountEntry();

		ObjectEntry objectEntry1 = _addObjectEntry(
			accountEntry1, 0, objectDefinition1);
		ObjectEntry objectEntry2 = _addObjectEntry(
			accountEntry1, 0, objectDefinition2);
		ObjectEntry objectEntry3 = _addObjectEntry(
			accountEntry1, TestPropsValues.getGroupId(), objectDefinition3);

		AccountEntry accountEntry2 = _addAccountEntry();

		_addObjectEntry(accountEntry2, 0, objectDefinition1);
		_addObjectEntry(accountEntry2, 0, objectDefinition2);
		_addObjectEntry(
			accountEntry2, TestPropsValues.getGroupId(), objectDefinition3);

		Set<Long> objectEntryIds = new ConcurrentSkipListSet<>();
		ServiceTrackerMap<String, IndexerDocumentBuilder>
			originalServiceTrackerMap = ReflectionTestUtil.getFieldValue(
				_accountEntryOrganizationRelModelListener,
				"_serviceTrackerMap");

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					_accountEntryOrganizationRelModelListener,
					"_serviceTrackerMap",
					ProxyUtil.newProxyInstance(
						ServiceTrackerMap.class.getClassLoader(),
						new Class<?>[] {ServiceTrackerMap.class},
						(proxy, method, args) -> {
							Object service = method.invoke(
								originalServiceTrackerMap, args);

							if (!Objects.equals(
									method.getName(), "getService") ||
								(service == null)) {

								return service;
							}

							return ProxyUtil.newProxyInstance(
								IndexerDocumentBuilder.class.getClassLoader(),
								new Class<?>[] {IndexerDocumentBuilder.class},
								(documentBuilderProxy, documentBuilderMethod,
								 documentBuilderArgs) -> {

									if (Objects.equals(
											documentBuilderMethod.getName(),
											"getDocument") &&
										(documentBuilderArgs[0] instanceof
											ObjectEntry)) {

										ObjectEntry objectEntry =
											(ObjectEntry)documentBuilderArgs[0];

										objectEntryIds.add(
											objectEntry.getObjectEntryId());
									}

									return documentBuilderMethod.invoke(
										service, documentBuilderArgs);
								});
						}))) {

			Organization organization = OrganizationTestUtil.addOrganization();

			AccountEntryOrganizationRel accountEntryOrganizationRel1 =
				_accountEntryOrganizationRelLocalService.
					addAccountEntryOrganizationRel(
						accountEntry1.getAccountEntryId(),
						organization.getOrganizationId());

			Assert.assertEquals(
				SetUtil.fromArray(
					objectEntry1.getObjectEntryId(),
					objectEntry2.getObjectEntryId(),
					objectEntry3.getObjectEntryId()),
				objectEntryIds);

			// Add account entry without object entries to organization

			objectEntryIds.clear();

			AccountEntry accountEntry3 = _addAccountEntry();

			AccountEntryOrganizationRel accountEntryOrganizationRel3 =
				_accountEntryOrganizationRelLocalService.
					addAccountEntryOrganizationRel(
						accountEntry3.getAccountEntryId(),
						organization.getOrganizationId());

			Assert.assertEquals(Collections.emptySet(), objectEntryIds);

			// Remove account entry with object entries from organization

			objectEntryIds.clear();

			_accountEntryOrganizationRelLocalService.
				deleteAccountEntryOrganizationRel(
					accountEntryOrganizationRel1.
						getAccountEntryOrganizationRelId());

			Assert.assertEquals(
				SetUtil.fromArray(
					objectEntry1.getObjectEntryId(),
					objectEntry2.getObjectEntryId(),
					objectEntry3.getObjectEntryId()),
				objectEntryIds);

			// Remove account entry without object entries from organization

			objectEntryIds.clear();

			_accountEntryOrganizationRelLocalService.
				deleteAccountEntryOrganizationRel(
					accountEntryOrganizationRel3.
						getAccountEntryOrganizationRelId());

			Assert.assertEquals(Collections.emptySet(), objectEntryIds);
		}
	}

	private AccountEntry _addAccountEntry() throws Exception {
		return _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _addObjectEntry(
			AccountEntry accountEntry, long groupId,
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinition.getAccountEntryRestrictedObjectFieldId());

		return ObjectEntryTestUtil.addObjectEntry(
			groupId, objectDefinition.getObjectDefinitionId(),
			Collections.singletonMap(
				objectField.getName(), accountEntry.getAccountEntryId()));
	}

	private ObjectDefinition _enableAccountEntryRestricted(
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectDefinition accountEntryObjectDefinition =
			_objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(), "AccountEntry");

		return _objectDefinitionLocalService.enableAccountEntryRestricted(
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				accountEntryObjectDefinition.getObjectDefinitionId(),
				objectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, null, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.model.listener.AccountEntryOrganizationRelModelListener"
	)
	private ModelListener<AccountEntryOrganizationRel>
		_accountEntryOrganizationRelModelListener;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private final List<ObjectDefinition> _objectDefinitions = new ArrayList<>();

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}