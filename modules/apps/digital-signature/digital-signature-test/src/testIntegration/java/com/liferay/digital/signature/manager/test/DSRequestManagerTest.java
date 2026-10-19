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
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

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
		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.digital.signature.impl", DSRequestManagerTest.class,
			new String[] {
				".com.liferay.digital.signature.internal.batch.01.object." +
					"definition",
				".com.liferay.digital.signature.internal.batch.02.object." +
					"relationship"
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
		DSEnvelope dsEnvelope = _createDSEnvelope();
		DSRecipient dsRecipient = _createDSRecipient();

		User user = TestPropsValues.getUser();

		dsRecipient.setEmailAddress(user.getEmailAddress());

		dsEnvelope.setDSRecipients(
			ListUtil.fromArray(dsRecipient, _createDSRecipient()));

		long fileEntryId = RandomTestUtil.randomInt();

		_dsRequestManager.addDSRequest(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), dsEnvelope, new long[] {fileEntryId});

		Map<String, Serializable> dsRequestValues = _getDSRequestValues(
			dsEnvelope);

		Assert.assertEquals(
			DSRequestConstants.STATUS_SENT,
			MapUtil.getString(dsRequestValues, "requestStatus"));
		Assert.assertEquals(
			TestPropsValues.getGroupId(),
			MapUtil.getLong(dsRequestValues, "siteGroupId"));
		Assert.assertEquals(
			new HashSet<>(
				TransformUtil.transform(
					dsEnvelope.getDSRecipients(),
					DSRecipient::getEmailAddress)),
			new HashSet<>(
				TransformUtil.transform(
					_getDSRequestRecipientValuesList(dsRequestValues),
					values -> MapUtil.getString(values, "emailAddress"))));

		List<Map<String, Serializable>> dsRequestDocumentValuesList =
			_getValuesList(
				"(fileEntryId eq " + fileEntryId + ")",
				_dsRequestDocumentObjectDefinition);

		Assert.assertEquals(
			dsRequestDocumentValuesList.toString(), 1,
			dsRequestDocumentValuesList.size());

		List<Map<String, Serializable>> dsRequestRecipientValuesList =
			ListUtil.filter(
				_getDSRequestRecipientValuesList(dsRequestValues),
				values -> Objects.equals(
					dsRecipient.getEmailAddress(),
					MapUtil.getString(values, "emailAddress")));

		Assert.assertEquals(
			dsRequestRecipientValuesList.toString(), 1,
			dsRequestRecipientValuesList.size());
		Assert.assertEquals(
			user.getUserId(),
			MapUtil.getLong(
				dsRequestRecipientValuesList.get(0),
				"r_userToDSRequestRecipients_userId"));

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(user);

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		String emailAddress = RandomTestUtil.randomString() + "@liferay.com";

		mockLiferayResourceRequest.setParameter(
			"recipients",
			JSONUtil.putAll(
				JSONUtil.put(
					"email", emailAddress
				).put(
					"fullName", RandomTestUtil.randomString(281)
				)
			).toString());

		String dsEnvelopeId = RandomTestUtil.randomString();

		DSEnvelopeManager dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_mvcResourceCommand, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						DSEnvelope sentDSEnvelope = (DSEnvelope)arguments[2];

						sentDSEnvelope.setDSEnvelopeId(dsEnvelopeId);

						return sentDSEnvelope;
					}));

		try {
			PortletException portletException = Assert.assertThrows(
				PortletException.class,
				() -> _mvcResourceCommand.serveResource(
					mockLiferayResourceRequest,
					new MockLiferayResourceResponse()));

			Throwable throwable = portletException.getCause();

			Assert.assertSame(
				ObjectEntryValuesException.ExceedsTextMaxLength.class,
				throwable.getClass());
			Assert.assertEquals(
				"Object entry value exceeds the maximum length of 280 " +
					"characters for object field \"name\"",
				throwable.getMessage());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_mvcResourceCommand, "_dsEnvelopeManager", dsEnvelopeManager);
		}

		Assert.assertEquals(
			Collections.emptyList(),
			_getValuesList(
				"(externalReferenceCode eq '" + dsEnvelopeId + "')",
				_dsRequestObjectDefinition));
		Assert.assertEquals(
			Collections.emptyList(),
			_getValuesList(
				"(emailAddress eq '" + emailAddress + "')",
				_dsRequestRecipientObjectDefinition));

		_testAddDSRequestWhenDSEnvelopeIdIsNull();
		_testAddDSRequestWhenDSRequestRecipientUserIsDeleted();
	}

	@Test
	public void testUpdateDSRequest() throws Exception {
		LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(
			System.currentTimeMillis() / 1000, 0, ZoneOffset.UTC);

		_testUpdateDSRequest(
			"completed", DSRequestConstants.STATUS_COMPLETED,
			localDateTime.plusDays(1), localDateTime);
		_testUpdateDSRequest(
			RandomTestUtil.randomString(), DSRequestConstants.STATUS_CREATED,
			localDateTime.plusDays(1), localDateTime);
		_testUpdateDSRequest(
			"declined", DSRequestConstants.STATUS_DECLINED,
			localDateTime.plusDays(1), localDateTime);
		_testUpdateDSRequest(
			"voided", DSRequestConstants.STATUS_EXPIRED, localDateTime,
			localDateTime);
		_testUpdateDSRequest(
			"delivered", DSRequestConstants.STATUS_SENT,
			localDateTime.plusDays(1), localDateTime);
		_testUpdateDSRequest(
			"signed", DSRequestConstants.STATUS_SENT, localDateTime.plusDays(1),
			localDateTime);
		_testUpdateDSRequest(
			"voided", DSRequestConstants.STATUS_VOIDED,
			localDateTime.plusDays(1), localDateTime);

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
			"A digital signature request requires a digital signature " +
				"envelope ID",
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

	private void _testAddDSRequestWhenDSRequestRecipientUserIsDeleted()
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
			String dsEnvelopeStatus, String expectedRequestStatus,
			LocalDateTime expirationLocalDateTime,
			LocalDateTime statusLocalDateTime)
		throws Exception {

		DSEnvelope dsEnvelope = _createDSEnvelope();

		dsEnvelope.setStatus(DSRequestConstants.STATUS_CREATED);

		_dsRequestManager.addDSRequest(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), dsEnvelope,
			new long[] {RandomTestUtil.randomInt()});

		dsEnvelope.setExpirationLocalDateTime(expirationLocalDateTime);
		dsEnvelope.setStatus(dsEnvelopeStatus);
		dsEnvelope.setStatusLocalDateTime(statusLocalDateTime);

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
			Date.from(expirationLocalDateTime.toInstant(ZoneOffset.UTC)),
			dsRequestValues.get("requestExpirationDate"));
		Assert.assertEquals(
			expectedRequestStatus,
			MapUtil.getString(dsRequestValues, "requestStatus"));
		Assert.assertEquals(
			Date.from(statusLocalDateTime.toInstant(ZoneOffset.UTC)),
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
			StringBundler.concat(
				"No ObjectEntry exists with the key {objectDefinitionId=",
				_dsRequestObjectDefinition.getObjectDefinitionId(),
				", objectEntryId=", dsRequestDocumentId, "}"),
			() -> _dsRequestManager.updateDSRequest(dsRequestDocumentId));
	}

	private CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;

	@Inject
	private CompanyLocalService _companyLocalService;

	private ObjectDefinition _dsRequestDocumentObjectDefinition;

	@Inject
	private DSRequestManager _dsRequestManager;

	private ObjectDefinition _dsRequestObjectDefinition;
	private ObjectDefinition _dsRequestRecipientObjectDefinition;

	@Inject(
		filter = "filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private FilterFactory<Predicate> _filterFactory;

	@Inject(filter = "mvc.command.name=/digital_signature/add_ds_envelope")
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private UserLocalService _userLocalService;

}