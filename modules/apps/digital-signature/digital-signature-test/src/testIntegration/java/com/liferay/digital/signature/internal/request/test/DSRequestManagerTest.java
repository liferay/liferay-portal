/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.request.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.model.DSDocument;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.digital.signature.request.DSRequestManager;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class DSRequestManagerTest {

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
			).build());

		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddDSRequests() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		long fileEntryId = RandomTestUtil.randomInt();

		_dsRequestManager.addDSRequest(
			companyId, _group.getGroupId(), TestPropsValues.getUserId(),
			_createDSEnvelope(fileEntryId), new long[] {fileEntryId});

		ObjectDefinition requestObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DS_REQUEST", companyId);

		List<Map<String, Serializable>> requestValuesList = _getValuesList(
			companyId, requestObjectDefinition,
			"(fileEntryId eq " + fileEntryId + ")");

		Assert.assertEquals(
			requestValuesList.toString(), 1, requestValuesList.size());

		Map<String, Serializable> requestValues = requestValuesList.get(0);

		Assert.assertEquals("sent", requestValues.get("requestStatus"));
		Assert.assertEquals(
			"env-" + fileEntryId, requestValues.get("providerRequestId"));

		ObjectDefinition recipientObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DS_REQUEST_RECIPIENT", companyId);

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.fetchObjectRelationship(
				requestObjectDefinition.getObjectDefinitionId(),
				"dsRequestToDSRequestRecipients");

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		long requestId = GetterUtil.getLong(
			requestValues.get(requestObjectDefinition.getPKObjectFieldName()));

		List<Map<String, Serializable>> recipientValuesList = _getValuesList(
			companyId, recipientObjectDefinition,
			StringBundler.concat(
				"(", objectField.getName(), " eq '", requestId, "')"));

		Assert.assertEquals(
			recipientValuesList.toString(), 2, recipientValuesList.size());

		Set<String> emailAddresses = new HashSet<>();

		for (Map<String, Serializable> recipientValues : recipientValuesList) {
			emailAddresses.add(
				GetterUtil.getString(recipientValues.get("emailAddress")));
		}

		Assert.assertTrue(
			emailAddresses.toString(),
			emailAddresses.contains("ray.chen@liferay.com"));
		Assert.assertTrue(
			emailAddresses.toString(),
			emailAddresses.contains("mei.lin@liferay.com"));
	}

	@Test
	public void testGetRecipientStatusesByFileEntryId() throws Exception {
		long companyId = TestPropsValues.getCompanyId();
		long userId = TestPropsValues.getUserId();

		ObjectDefinition requestObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DS_REQUEST", companyId);
		ObjectDefinition recipientObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DS_REQUEST_RECIPIENT", companyId);

		long fileEntryId = RandomTestUtil.randomInt();

		String languageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		ObjectEntry requestObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0, userId, requestObjectDefinition.getObjectDefinitionId(), 0,
				languageId,
				HashMapBuilder.<String, Serializable>put(
					"fileEntryId", fileEntryId
				).put(
					"providerKey", "docusign"
				).put(
					"providerRequestId", "test-" + fileEntryId
				).put(
					"requestStatus", "sent"
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
				"r_userToDSRequestRecipient_userId", userId
			).put(
				"requestRecipientStatus", "sent"
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), userId));

		Map<Long, Map<Long, String>> recipientStatusesByFileEntryId =
			_dsRequestManager.getRecipientStatusesByFileEntryId(
				companyId, Collections.singletonList(fileEntryId));

		Assert.assertEquals(
			"sent",
			recipientStatusesByFileEntryId.get(
				fileEntryId
			).get(
				userId
			));
	}

	@Test
	public void testGetRequestStatuses() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DS_REQUEST", companyId);

		long fileEntryId = RandomTestUtil.randomInt();

		_objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
			HashMapBuilder.<String, Serializable>put(
				"fileEntryId", fileEntryId
			).put(
				"providerKey", "docusign"
			).put(
				"providerRequestId", "test-" + fileEntryId
			).put(
				"requestStatus", "completed"
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		Map<Long, String> requestStatuses =
			_dsRequestManager.getRequestStatusesByFileEntryId(
				companyId, Collections.singletonList(fileEntryId));

		Assert.assertEquals("completed", requestStatuses.get(fileEntryId));
	}

	@Test
	public void testGetRequestStatusesReturnsEmptyForMissingRequest()
		throws Exception {

		Map<Long, String> requestStatuses =
			_dsRequestManager.getRequestStatusesByFileEntryId(
				TestPropsValues.getCompanyId(),
				Collections.singletonList(RandomTestUtil.randomLong()));

		Assert.assertTrue(requestStatuses.isEmpty());
	}

	@Test
	public void testGetSignatureRequiredFileEntryIds() throws Exception {
		long companyId = TestPropsValues.getCompanyId();
		long userId = TestPropsValues.getUserId();

		ObjectDefinition requestObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DS_REQUEST", companyId);
		ObjectDefinition recipientObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DS_REQUEST_RECIPIENT", companyId);

		long fileEntryId = RandomTestUtil.randomInt();

		String languageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		ObjectEntry requestObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0, userId, requestObjectDefinition.getObjectDefinitionId(), 0,
				languageId,
				HashMapBuilder.<String, Serializable>put(
					"fileEntryId", fileEntryId
				).put(
					"providerKey", "docusign"
				).put(
					"providerRequestId", "test-" + fileEntryId
				).put(
					"requestStatus", "sent"
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
				"r_userToDSRequestRecipient_userId", userId
			).put(
				"requestRecipientStatus", "sent"
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), userId));

		Set<Long> signatureRequiredFileEntryIds =
			_dsRequestManager.getSignatureRequiredFileEntryIds(
				companyId, userId, Collections.singletonList(fileEntryId));

		Assert.assertTrue(
			signatureRequiredFileEntryIds.toString(),
			signatureRequiredFileEntryIds.contains(fileEntryId));

		Assert.assertTrue(
			_dsRequestManager.getSignatureRequiredCount(companyId, userId) > 0);
	}

	private DSEnvelope _createDSEnvelope(long fileEntryId) {
		DSEnvelope dsEnvelope = new DSEnvelope();

		DSDocument dsDocument = new DSDocument();

		dsDocument.setDSDocumentId(String.valueOf(fileEntryId));

		dsEnvelope.setDSDocuments(ListUtil.fromArray(dsDocument));

		dsEnvelope.setDSEnvelopeId("env-" + fileEntryId);
		dsEnvelope.setDSRecipients(
			ListUtil.fromArray(
				_createDSRecipient("1", "ray.chen@liferay.com", "Ray Chen"),
				_createDSRecipient("2", "mei.lin@liferay.com", "Mei Lin")));
		dsEnvelope.setEmailSubject("Please sign");
		dsEnvelope.setStatus("sent");

		return dsEnvelope;
	}

	private DSRecipient _createDSRecipient(
		String dsRecipientId, String emailAddress, String name) {

		DSRecipient dsRecipient = new DSRecipient();

		dsRecipient.setDSRecipientId(dsRecipientId);
		dsRecipient.setEmailAddress(emailAddress);
		dsRecipient.setName(name);
		dsRecipient.setStatus("sent");

		return dsRecipient;
	}

	private List<Map<String, Serializable>> _getValuesList(
			long companyId, ObjectDefinition objectDefinition,
			String filterString)
		throws Exception {

		return _objectEntryLocalService.getValuesList(
			0, companyId, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			_filterFactory.create(filterString, objectDefinition), null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	@Inject
	private ConfigurationProvider _configurationProvider;

	@Inject
	private DSRequestManager _dsRequestManager;

	@Inject(
		filter = "filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private FilterFactory<Predicate> _filterFactory;

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