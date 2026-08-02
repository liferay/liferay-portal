/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.request.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.request.DSRequestManager;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Kim
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-69290"))
@RunWith(Arquillian.class)
public class DSSignatureReminderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_configurationProvider.saveCompanyConfiguration(
			DigitalSignatureConfiguration.class, TestPropsValues.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", true
			).put(
				"enableEmbeddedView", true
			).put(
				"signatureReminderEnabled", true
			).build());

		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testRemindPendingSignatureRecipients() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		int count = _dsRequestManager.sendSignatureReminders(companyId);

		_addSignatureRequest("sent");
		_addSignatureRequest("voided");

		Assert.assertEquals(
			count + 1, _dsRequestManager.sendSignatureReminders(companyId));
	}

	private void _addSignatureRequest(String requestStatus) throws Exception {
		long companyId = TestPropsValues.getCompanyId();
		long userId = TestPropsValues.getUserId();

		ObjectDefinition recipientObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DS_REQUEST_RECIPIENT", companyId);
		ObjectDefinition requestObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DS_REQUEST", companyId);

		String languageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		ObjectEntry requestObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0, userId, requestObjectDefinition.getObjectDefinitionId(), 0,
				languageId,
				HashMapBuilder.<String, Serializable>put(
					"emailSubject", "Please sign"
				).put(
					"fileEntryId", RandomTestUtil.randomInt()
				).put(
					"providerKey", "docusign"
				).put(
					"providerRequestId",
					"reminder-" + RandomTestUtil.randomInt()
				).put(
					"requestStatus", requestStatus
				).build(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), userId));

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.fetchObjectRelationship(
				requestObjectDefinition.getObjectDefinitionId(),
				"dsRequestToDSRequestRecipients");

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		_objectEntryLocalService.addObjectEntry(
			0, userId, recipientObjectDefinition.getObjectDefinitionId(), 0,
			languageId,
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), requestObjectEntry.getObjectEntryId()
			).put(
				"emailAddress", "ray.chen@liferay.com"
			).put(
				"requestRecipientStatus", "sent"
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), userId));
	}

	@Inject
	private ConfigurationProvider _configurationProvider;

	@Inject
	private DSRequestManager _dsRequestManager;

	private Group _group;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}