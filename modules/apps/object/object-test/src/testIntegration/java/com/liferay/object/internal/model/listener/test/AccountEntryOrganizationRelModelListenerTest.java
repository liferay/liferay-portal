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
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
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
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

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
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				"A" + RandomTestUtil.randomString());

		objectDefinition =
			_objectDefinitionLocalService.enableAccountEntryRestricted(
				_objectRelationshipLocalService.addObjectRelationship(
					null, TestPropsValues.getUserId(),
					_objectDefinitionLocalService.fetchSystemObjectDefinition(
						TestPropsValues.getCompanyId(), "AccountEntry"
					).getObjectDefinitionId(),
					objectDefinition.getObjectDefinitionId(), 0,
					ObjectRelationshipConstants.DELETION_TYPE_PREVENT, null,
					false,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					StringUtil.randomId(), false,
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));

		objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		List<ObjectRelationship> objectRelationships =
			_objectRelationshipLocalService.
				getObjectRelationshipsByObjectDefinitionId2(
					objectDefinition.getObjectDefinitionId());

		ObjectRelationship objectRelationship = objectRelationships.get(0);

		ObjectField relationshipObjectField =
			_objectFieldLocalService.getObjectField(
				objectRelationship.getObjectFieldId2());

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0L,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		ObjectEntryTestUtil.addObjectEntry(
			0, objectDefinition.getObjectDefinitionId(),
			Collections.singletonMap(
				relationshipObjectField.getName(),
				accountEntry.getAccountEntryId()));

		Organization organization = OrganizationTestUtil.addOrganization();

		Object originalServiceTrackerMap = ReflectionTestUtil.getFieldValue(
			_accountEntryOrganizationRelModelListener, "_serviceTrackerMap");

		AtomicBoolean reindexed = new AtomicBoolean(false);

		try {
			ReflectionTestUtil.setFieldValue(
				_accountEntryOrganizationRelModelListener, "_serviceTrackerMap",
				ProxyUtil.newProxyInstance(
					ServiceTrackerMap.class.getClassLoader(),
					new Class<?>[] {ServiceTrackerMap.class},
					(proxy, method, args) -> {
						if (Objects.equals(method.getName(), "getService")) {
							reindexed.set(true);
						}

						return method.invoke(originalServiceTrackerMap, args);
					}));

			_accountEntryOrganizationRelLocalService.
				addAccountEntryOrganizationRel(
					accountEntry.getAccountEntryId(),
					organization.getOrganizationId());

			Assert.assertTrue(reindexed.get());

			reindexed.set(false);

			accountEntry = _accountEntryLocalService.addAccountEntry(
				StringPool.BLANK, TestPropsValues.getUserId(), 0L,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				null, null, null, RandomTestUtil.randomString(),
				AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext());

			AccountEntryOrganizationRel accountEntryOrganizationRel =
				_accountEntryOrganizationRelLocalService.
					addAccountEntryOrganizationRel(
						accountEntry.getAccountEntryId(),
						organization.getOrganizationId());

			Assert.assertFalse(reindexed.get());

			_accountEntryOrganizationRelLocalService.
				deleteAccountEntryOrganizationRel(
					accountEntryOrganizationRel.
						getAccountEntryOrganizationRelId());

			Assert.assertFalse(reindexed.get());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_accountEntryOrganizationRelModelListener, "_serviceTrackerMap",
				originalServiceTrackerMap);
		}
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

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}