/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.constants.DSObjectDefinitionConstants;
import com.liferay.digital.signature.constants.DSRequestConstants;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian I. Kim
 */
@FeatureFlag("LPD-69290")
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
		String prefix = ".com.liferay.digital.signature.internal.batch.";

		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.digital.signature.impl", DSRequestManagerTest.class,
			new String[] {
				prefix + "00.list.type.definition",
				prefix + "01.object.definition",
				prefix + "02.object.relationship"
			});

		_companyConfigurationTemporarySwapper =
			new CompanyConfigurationTemporarySwapper(
				TestPropsValues.getCompanyId(),
				DigitalSignatureConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"accountBaseURI", RandomTestUtil.randomString()
				).put(
					"apiAccountId", RandomTestUtil.randomString()
				).put(
					"apiUsername", RandomTestUtil.randomString()
				).put(
					"enabled", true
				).put(
					"environment", RandomTestUtil.randomString()
				).put(
					"integrationKey", RandomTestUtil.randomString()
				).put(
					"rsaPrivateKey", RandomTestUtil.randomString()
				).put(
					"siteSettingsStrategy", "always-inherit"
				).build());
		_dsRequestDocumentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_REQUEST_DOCUMENT,
					TestPropsValues.getCompanyId());
		_dsRequestObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_REQUEST,
					TestPropsValues.getCompanyId());
		_dsRequestRecipientObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_REQUEST_RECIPIENT,
					TestPropsValues.getCompanyId());
	}

	@After
	public void tearDown() throws Exception {
		_companyConfigurationTemporarySwapper.close();
	}

	@Test
	public void testAddDSRequest() throws Exception {
		DSEnvelope dsEnvelope1 = _createDSEnvelope();

		DSRecipient dsRecipient1 = _createDSRecipient();

		User user = TestPropsValues.getUser();

		dsRecipient1.setEmailAddress(user.getEmailAddress());

		dsEnvelope1.setDSRecipients(
			ListUtil.fromArray(dsRecipient1, _createDSRecipient()));

		long fileEntryId1 = RandomTestUtil.randomInt();

		_dsRequestManager.addDSRequest(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), dsEnvelope1,
			new long[] {fileEntryId1});

		Map<String, Serializable> dsRequestValues = _getDSRequestValues(
			dsEnvelope1);

		Assert.assertEquals(
			DSRequestConstants.STATUS_SENT,
			MapUtil.getString(dsRequestValues, "requestStatus"));
		Assert.assertEquals(
			TestPropsValues.getGroupId(),
			MapUtil.getLong(dsRequestValues, "siteGroupId"));
		Assert.assertEquals(
			new HashSet<>(
				TransformUtil.transform(
					dsEnvelope1.getDSRecipients(),
					DSRecipient::getEmailAddress)),
			new HashSet<>(
				TransformUtil.transform(
					_getDSRequestRecipientValuesList(dsRequestValues),
					values -> MapUtil.getString(values, "emailAddress"))));

		List<Map<String, Serializable>> dsRequestDocumentValuesList = _getValuesList(
			"(fileEntryId eq " + fileEntryId1 + ")",
			_dsRequestDocumentObjectDefinition);

		Assert.assertEquals(
			dsRequestDocumentValuesList.toString(), 1,
			dsRequestDocumentValuesList.size());

		List<Map<String, Serializable>> dsRecipientValuesList = ListUtil.filter(
			_getDSRequestRecipientValuesList(dsRequestValues),
			values -> Objects.equals(
				dsRecipient1.getEmailAddress(),
				MapUtil.getString(values, "emailAddress")));

		Assert.assertEquals(
			dsRecipientValuesList.toString(), 1, dsRecipientValuesList.size());
		Assert.assertEquals(
			user.getUserId(),
			MapUtil.getLong(
				dsRecipientValuesList.get(0),
				"r_userToDSRequestRecipients_userId"));

		DSEnvelope dsEnvelope2 = _createDSEnvelope();

		DSRecipient dsRecipient2 = _createDSRecipient();

		dsRecipient2.setName(RandomTestUtil.randomString(281));

		dsEnvelope2.setDSRecipients(ListUtil.fromArray(dsRecipient2));

		long fileEntryId2 = RandomTestUtil.randomInt();

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsTextMaxLength.class,
			"Object entry value exceeds the maximum length of 280 characters " +
				"for object field \"name\"",
			() -> _dsRequestManager.addDSRequest(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				TestPropsValues.getUserId(), dsEnvelope2,
				new long[] {fileEntryId2}));

		Assert.assertEquals(
			Collections.emptyList(),
			_getValuesList(
				"(externalReferenceCode eq '" + dsEnvelope2.getDSEnvelopeId() +
					"')",
				_dsRequestObjectDefinition));
		Assert.assertEquals(
			Collections.emptyList(),
			_getValuesList(
				"(fileEntryId eq " + fileEntryId2 + ")",
				_dsRequestDocumentObjectDefinition));
		Assert.assertEquals(
			Collections.emptyList(),
			_getValuesList(
				"(emailAddress eq '" + dsRecipient2.getEmailAddress() + "')",
				_dsRequestRecipientObjectDefinition));

		_testAddDSRequestWhenDSEnvelopeIdIsNull();
		_testAddDSRequestWhenDSRecipientUserIsDeleted();
	}

	@Test
	public void testUpdateDSRequest() throws Exception {
		LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(
			System.currentTimeMillis() / 1000, 0, ZoneOffset.UTC);

		_testUpdateDSRequest(
			DSRequestConstants.STATUS_COMPLETED, localDateTime.plusDays(1),
			"completed", localDateTime);
		_testUpdateDSRequest(
			DSRequestConstants.STATUS_EXPIRED, localDateTime, "voided",
			localDateTime);
		_testUpdateDSRequest(
			DSRequestConstants.STATUS_VOIDED, localDateTime.plusDays(1),
			"voided", localDateTime);

		_testUpdateDSRequestWhenObjectEntryIsNotDSRequest();
	}

	private DSEnvelope _createDSEnvelope() {
		DSEnvelope dsEnvelope = new DSEnvelope();

		dsEnvelope.setDSEnvelopeId(RandomTestUtil.randomString());
		dsEnvelope.setDSRecipients(
			ListUtil.fromArray(_createDSRecipient(), _createDSRecipient()));
		dsEnvelope.setEmailSubject(RandomTestUtil.randomString());
		dsEnvelope.setStatus("sent");

		return dsEnvelope;
	}

	private DSRecipient _createDSRecipient() {
		DSRecipient dsRecipient = new DSRecipient();

		dsRecipient.setEmailAddress(
			RandomTestUtil.randomString() + "@liferay.com");
		dsRecipient.setName(RandomTestUtil.randomString());

		return dsRecipient;
	}

	private List<Map<String, Serializable>> _getDSRequestRecipientValuesList(
			Map<String, Serializable> values)
		throws Exception {

		long dsRequestId = MapUtil.getLong(
			values, _dsRequestObjectDefinition.getPKObjectFieldName());

		return _getValuesList(
			"(r_dsRequestToDSRequestRecipients_l_dsRequestId eq '" +
				dsRequestId + "')",
			_dsRequestRecipientObjectDefinition);
	}

	private Map<String, Serializable> _getDSRequestValues(DSEnvelope dsEnvelope)
		throws Exception {

		List<Map<String, Serializable>> valuesList = _getValuesList(
			"(externalReferenceCode eq '" + dsEnvelope.getDSEnvelopeId() + "')",
			_dsRequestObjectDefinition);

		Assert.assertEquals(valuesList.toString(), 1, valuesList.size());

		return valuesList.get(0);
	}

	private List<Map<String, Serializable>> _getValuesList(
			String filterString, ObjectDefinition objectDefinition)
		throws Exception {

		return _objectEntryLocalService.getValuesList(
			0, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			_filterFactory.create(filterString, objectDefinition), null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	private void _testAddDSRequestWhenDSEnvelopeIdIsNull() throws Exception {
		DSEnvelope dsEnvelope = _createDSEnvelope();

		dsEnvelope.setDSEnvelopeId(null);

		DSRecipient dsRecipient = _createDSRecipient();

		dsEnvelope.setDSRecipients(ListUtil.fromArray(dsRecipient));

		AssertUtils.assertFailure(
			PortalException.class,
			"A signature request requires a DocuSign envelope ID",
			() -> _dsRequestManager.addDSRequest(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				TestPropsValues.getUserId(), dsEnvelope,
				new long[] {RandomTestUtil.randomInt()}));

		Assert.assertEquals(
			Collections.emptyList(),
			_getValuesList(
				"(emailAddress eq '" + dsRecipient.getEmailAddress() + "')",
				_dsRequestRecipientObjectDefinition));
	}

	private void _testAddDSRequestWhenDSRecipientUserIsDeleted()
		throws Exception {

		DSEnvelope dsEnvelope = _createDSEnvelope();
		DSRecipient dsRecipient = _createDSRecipient();

		User user = UserTestUtil.addUser();

		dsRecipient.setEmailAddress(user.getEmailAddress());

		dsEnvelope.setDSRecipients(ListUtil.fromArray(dsRecipient));

		_dsRequestManager.addDSRequest(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), dsEnvelope,
			new long[] {RandomTestUtil.randomInt()});

		String filterString =
			"(emailAddress eq '" + dsRecipient.getEmailAddress() + "')";

		List<Map<String, Serializable>> dsRequestRecipientValuesList =
			_getValuesList(filterString, _dsRequestRecipientObjectDefinition);

		Assert.assertEquals(
			dsRequestRecipientValuesList.toString(), 1,
			dsRequestRecipientValuesList.size());
		Assert.assertEquals(
			user.getUserId(),
			MapUtil.getLong(
				dsRequestRecipientValuesList.get(0),
				"r_userToDSRequestRecipients_userId"));

		_userLocalService.deleteUser(user);

		dsRequestRecipientValuesList = _getValuesList(
			filterString, _dsRequestRecipientObjectDefinition);

		Assert.assertEquals(
			dsRequestRecipientValuesList.toString(), 1,
			dsRequestRecipientValuesList.size());

		Map<String, Serializable> dsRequestRecipientValues =
			dsRequestRecipientValuesList.get(0);

		Assert.assertEquals(
			user.getEmailAddress(),
			MapUtil.getString(dsRequestRecipientValues, "emailAddress"));
		Assert.assertEquals(
			0,
			MapUtil.getLong(
				dsRequestRecipientValues,
				"r_userToDSRequestRecipients_userId"));
	}

	private void _testUpdateDSRequest(
			String expectedRequestStatus, LocalDateTime expireLocalDateTime,
			String status, LocalDateTime statusChangedLocalDateTime)
		throws Exception {

		DSEnvelope dsEnvelope = _createDSEnvelope();

		_dsRequestManager.addDSRequest(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), dsEnvelope,
			new long[] {RandomTestUtil.randomInt()});

		dsEnvelope.setExpireLocalDateTime(expireLocalDateTime);
		dsEnvelope.setStatus(status);
		dsEnvelope.setStatusChangedLocalDateTime(statusChangedLocalDateTime);

		DSEnvelopeManager dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						Assert.assertEquals(
							TestPropsValues.getCompanyId(), arguments[0]);
						Assert.assertEquals(
							TestPropsValues.getGroupId(), arguments[1]);
						Assert.assertEquals(
							dsEnvelope.getDSEnvelopeId(), arguments[2]);

						return dsEnvelope;
					}));

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			dsEnvelope.getDSEnvelopeId(), 0,
			_dsRequestObjectDefinition.getObjectDefinitionId());

		User user = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_dsRequestManager.updateDSRequest(objectEntry.getObjectEntryId());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager", dsEnvelopeManager);
		}

		Map<String, Serializable> dsRequestValues = _getDSRequestValues(
			dsEnvelope);

		Assert.assertEquals(
			Date.from(expireLocalDateTime.toInstant(ZoneOffset.UTC)),
			dsRequestValues.get("requestExpirationDate"));
		Assert.assertEquals(
			expectedRequestStatus,
			MapUtil.getString(dsRequestValues, "requestStatus"));
		Assert.assertEquals(
			Date.from(statusChangedLocalDateTime.toInstant(ZoneOffset.UTC)),
			dsRequestValues.get("requestStatusDate"));
	}

	private void _testUpdateDSRequestWhenObjectEntryIsNotDSRequest()
		throws Exception {

		long fileEntryId = RandomTestUtil.randomInt();

		_dsRequestManager.addDSRequest(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), _createDSEnvelope(),
			new long[] {fileEntryId});

		List<Map<String, Serializable>> dsRequestDocumentValuesList =
			_getValuesList(
				"(fileEntryId eq " + fileEntryId + ")",
				_dsRequestDocumentObjectDefinition);

		long dsRequestDocumentId = MapUtil.getLong(
			dsRequestDocumentValuesList.get(0),
			_dsRequestDocumentObjectDefinition.getPKObjectFieldName());

		AssertUtils.assertFailure(
			NoSuchObjectEntryException.class,
			"No signature request exists with the primary key " +
				dsRequestDocumentId,
			() -> _dsRequestManager.updateDSRequest(dsRequestDocumentId));
	}

	private CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;
	private ObjectDefinition _dsRequestDocumentObjectDefinition;

	@Inject
	private DSRequestManager _dsRequestManager;

	private ObjectDefinition _dsRequestObjectDefinition;
	private ObjectDefinition _dsRequestRecipientObjectDefinition;

	@Inject(
		filter = "filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private FilterFactory<Predicate> _filterFactory;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private UserLocalService _userLocalService;

}