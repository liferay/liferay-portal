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
import com.liferay.digital.signature.exception.DSRequestException;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.digital.signature.url.DSRequestURLProvider;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.service.NotificationQueueEntryLocalService;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.notification.util.NotificationRecipientSettingUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

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
					"relationship",
				".com.liferay.digital.signature.internal.batch.03." +
					"notification.template"
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
					"embeddedViewEnabled", true
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
		User user1 = TestPropsValues.getUser();

		DSRecipient dsRecipient = _createDSRecipient(user1.getEmailAddress());

		dsRecipient.setRoutingOrder(2);

		LocalDateTime dsEnvelopeStatusLocalDateTime =
			LocalDateTime.ofEpochSecond(
				System.currentTimeMillis() / 1000, 0, ZoneOffset.UTC);

		LocalDateTime dsRecipientStatusLocalDateTime =
			dsEnvelopeStatusLocalDateTime.minusHours(1);

		dsRecipient.setStatusLocalDateTime(dsRecipientStatusLocalDateTime);

		User user2 = UserTestUtil.addUser();

		DSEnvelope dsEnvelope = _createDSEnvelope(
			dsRecipient, _createDSRecipient(user2.getEmailAddress()));

		dsEnvelope.setStatusLocalDateTime(dsEnvelopeStatusLocalDateTime);

		FileEntry fileEntry = _addFileEntry();

		DSRequest dsRequest = _addDSRequest(dsEnvelope, fileEntry);

		Assert.assertEquals(
			dsEnvelope.getDSEnvelopeId(), dsRequest.getExternalReferenceCode());
		Assert.assertEquals(
			Collections.singletonList(fileEntry.getFileEntryId()),
			dsRequest.getFileEntryIds());
		Assert.assertEquals(
			TestPropsValues.getGroupId(), dsRequest.getSiteGroupId());
		Assert.assertEquals(
			DSRequestConstants.STATUS_SENT, dsRequest.getStatus());

		Assert.assertEquals(
			String.valueOf(user1.getUserId()), dsRecipient.getDSClientUserId());
		Assert.assertEquals(user1.getFullName(), dsRecipient.getName());

		List<DSRecipient> dsRecipients = dsEnvelope.getDSRecipients();

		for (int i = 0; i < dsRecipients.size(); i++) {
			DSRecipient curDSRecipient = dsRecipients.get(i);

			JSONObject tabsJSONObject = curDSRecipient.getTabsJSONObject();

			JSONArray signHereTabsJSONArray = tabsJSONObject.getJSONArray(
				"signHereTabs");

			JSONObject signHereTabJSONObject =
				signHereTabsJSONArray.getJSONObject(0);

			Assert.assertEquals(
				"true",
				signHereTabJSONObject.getString("anchorIgnoreIfNotPresent"));
			Assert.assertEquals(
				"/sig" + (i + 1) + "/",
				signHereTabJSONObject.getString("anchorString"));
			Assert.assertEquals(
				"pixels", signHereTabJSONObject.getString("anchorUnits"));
			Assert.assertEquals(
				"0", signHereTabJSONObject.getString("anchorXOffset"));
			Assert.assertEquals(
				"0", signHereTabJSONObject.getString("anchorYOffset"));
		}

		ObjectEntry dsRequestObjectEntry = _getDSRequestObjectEntry(dsEnvelope);

		Map<String, Serializable> dsRequestValues =
			dsRequestObjectEntry.getValues();

		Assert.assertEquals(
			DSRequestConstants.STATUS_SENT,
			MapUtil.getString(dsRequestValues, "requestStatus"));
		Assert.assertEquals(
			_toDate(dsEnvelopeStatusLocalDateTime),
			dsRequestValues.get("requestStatusDate"));
		Assert.assertEquals(
			TestPropsValues.getGroupId(),
			MapUtil.getLong(dsRequestValues, "siteGroupId"));

		List<Map<String, Serializable>> dsRequestDocumentValuesList =
			_getValuesList(
				"(fileEntryId eq " + fileEntry.getFileEntryId() + ")",
				_dsRequestDocumentObjectDefinition);

		Assert.assertEquals(
			dsRequestDocumentValuesList.toString(), 1,
			dsRequestDocumentValuesList.size());

		List<Map<String, Serializable>> dsRequestRecipientValuesList =
			_getDSRequestRecipientValuesList(
				dsRequestObjectEntry.getObjectEntryId());

		Assert.assertEquals(
			new HashSet<>(
				TransformUtil.transform(
					dsEnvelope.getDSRecipients(),
					DSRecipient::getEmailAddress)),
			new HashSet<>(
				TransformUtil.transform(
					dsRequestRecipientValuesList,
					values -> MapUtil.getString(values, "emailAddress"))));

		dsRequestRecipientValuesList = ListUtil.filter(
			dsRequestRecipientValuesList,
			values -> Objects.equals(
				dsRecipient.getEmailAddress(),
				MapUtil.getString(values, "emailAddress")));

		Assert.assertEquals(
			dsRequestRecipientValuesList.toString(), 1,
			dsRequestRecipientValuesList.size());

		Map<String, Serializable> dsRequestRecipientValues =
			dsRequestRecipientValuesList.get(0);

		Assert.assertEquals(
			user1.getFullName(),
			MapUtil.getString(dsRequestRecipientValues, "name"));
		Assert.assertEquals(
			user1.getUserId(),
			MapUtil.getLong(
				dsRequestRecipientValues,
				"r_userToDSRequestRecipients_userId"));
		Assert.assertEquals(
			_toDate(dsRecipientStatusLocalDateTime),
			dsRequestRecipientValues.get("requestRecipientStatusDate"));
		Assert.assertEquals(
			2, MapUtil.getInteger(dsRequestRecipientValues, "signingOrder"));

		_testAddDSRequestWhenDSEnvelopeIdIsNull();
		_testAddDSRequestWhenDSEnvelopeVoidFails();
		_testAddDSRequestWhenDSRecipientIsInvalid();
		_testAddDSRequestWhenDSRecipientNameExceedsMaxLength();
		_testAddDSRequestWhenDSRequestRecipientUserIsDeleted();
		_testAddDSRequestWhenDigitalSignatureIsDisabled();
		_testAddDSRequestWhenEmbeddedViewIsDisabled();
		_testAddDSRequestWhenExpireAfterIsNegative();
		_testAddDSRequestWhenExpiredDSEnvelopeIsCompleted();
		_testAddDSRequestWhenFeatureFlagIsDisabled();
		_testAddDSRequestWhenFileEntryIsInvalid();
		_testAddDSRequestWhenGroupIsStagingGroup();
	}

	@Test
	public void testFetchDSRequest() throws Exception {
		_testFetchDSRequest();
		_testFetchDSRequestWhenExpirationDateIsPast();
		_testFetchDSRequestWhenSiteOverridesInstanceSettings();
	}

	@Test
	public void testHasPermission() throws Exception {
		User recipientUser = UserTestUtil.addUser();

		DSEnvelope dsEnvelope = _createDSEnvelope(recipientUser);

		User user = UserTestUtil.addUser();

		DSRequest dsRequest = _addDSRequestWithoutValidation(
			dsEnvelope, user.getUserId(), RandomTestUtil.randomInt());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		Assert.assertTrue(
			_dsRequestManager.hasPermission(
				permissionChecker, dsRequest, ActionKeys.UPDATE));
		Assert.assertTrue(
			_dsRequestManager.hasPermission(
				permissionChecker, dsRequest, ActionKeys.VIEW));

		permissionChecker = PermissionCheckerFactoryUtil.create(recipientUser);

		Assert.assertFalse(
			_dsRequestManager.hasPermission(
				permissionChecker, dsRequest, ActionKeys.UPDATE));
		Assert.assertTrue(
			_dsRequestManager.hasPermission(
				permissionChecker, dsRequest, ActionKeys.VIEW));

		permissionChecker = PermissionCheckerFactoryUtil.create(
			UserTestUtil.addUser());

		Assert.assertFalse(
			_dsRequestManager.hasPermission(
				permissionChecker, dsRequest, ActionKeys.UPDATE));
		Assert.assertFalse(
			_dsRequestManager.hasPermission(
				permissionChecker, dsRequest, ActionKeys.VIEW));
	}

	@Test
	public void testSendDSRequestNotifications() throws Exception {
		User user = UserTestUtil.addUser();

		DSRecipient dsRecipient1 = _createDSRecipient(user);

		dsRecipient1.setStatus(DSRequestConstants.STATUS_SENT);

		DSRecipient dsRecipient2 = _createDSRecipient(UserTestUtil.addUser());

		dsRecipient2.setStatus(DSRequestConstants.STATUS_CREATED);

		DSEnvelope dsEnvelope = _createDSEnvelope(dsRecipient1, dsRecipient2);

		String emailMessage = RandomTestUtil.randomString();

		dsEnvelope.setEmailBlurb("<b>" + emailMessage + "</b>");

		DSRequest dsRequest = _addDSRequestWithoutValidation(
			dsEnvelope, TestPropsValues.getUserId(),
			RandomTestUtil.randomInt());

		_dsRequestManager.sendDSRequestNotifications(
			dsRequest.getDSRequestId());

		List<NotificationQueueEntry> notificationQueueEntries =
			_getNotificationQueueEntries(dsRequest);

		Assert.assertEquals(
			notificationQueueEntries.toString(), 1,
			notificationQueueEntries.size());

		NotificationQueueEntry notificationQueueEntry =
			notificationQueueEntries.get(0);

		Assert.assertEquals(
			dsRequest.getEmailSubject(), notificationQueueEntry.getSubject());

		String body = notificationQueueEntry.getBody();

		Assert.assertTrue(
			body, body.contains("&lt;b&gt;" + emailMessage + "&lt;/b&gt;"));
		Assert.assertTrue(
			body,
			body.contains(
				_dsRequestURLProvider.getSignURL(
					TestPropsValues.getCompanyId(), dsRequest.getDSRequestId(),
					TestPropsValues.getGroupId())));

		Map<String, Object> notificationRecipientSettingsMap =
			NotificationRecipientSettingUtil.
				getNotificationRecipientSettingsMap(notificationQueueEntry);

		Assert.assertEquals(
			user.getEmailAddress(),
			notificationRecipientSettingsMap.get(
				NotificationRecipientSettingConstants.NAME_TO));

		_testSendDSRequestNotificationsWhenDSRequestIsVoided();
		_testSendDSRequestNotificationsWhenTemplateIsMissing();
	}

	@Test
	public void testUpdateDSRequest() throws Exception {
		LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(
			System.currentTimeMillis() / 1000, 0, ZoneOffset.UTC);

		_testUpdateDSRequest(
			DSRequestConstants.STATUS_COMPLETED,
			DSRequestConstants.STATUS_COMPLETED,
			DSRequestConstants.STATUS_COMPLETED,
			DSRequestConstants.STATUS_COMPLETED, localDateTime.plusDays(1),
			localDateTime);
		_testUpdateDSRequest(
			RandomTestUtil.randomString(), "autoresponded",
			DSRequestConstants.STATUS_CREATED,
			DSRequestConstants.STATUS_CREATED, localDateTime.plusDays(1),
			localDateTime);
		_testUpdateDSRequest(
			DSRequestConstants.STATUS_DECLINED,
			DSRequestConstants.STATUS_DECLINED,
			DSRequestConstants.STATUS_DECLINED,
			DSRequestConstants.STATUS_DECLINED, localDateTime.plusDays(1),
			localDateTime);
		_testUpdateDSRequest(
			DSRequestConstants.STATUS_VOIDED, DSRequestConstants.STATUS_SENT,
			DSRequestConstants.STATUS_EXPIRED,
			DSRequestConstants.STATUS_EXPIRED, localDateTime, localDateTime);
		_testUpdateDSRequest(
			"delivered", "delivered", DSRequestConstants.STATUS_SENT,
			DSRequestConstants.STATUS_SENT, localDateTime.plusDays(1),
			localDateTime);
		_testUpdateDSRequest(
			DSRequestConstants.STATUS_VOIDED, DSRequestConstants.STATUS_SIGNED,
			DSRequestConstants.STATUS_SIGNED, DSRequestConstants.STATUS_EXPIRED,
			localDateTime, localDateTime);
		_testUpdateDSRequest(
			DSRequestConstants.STATUS_SIGNED, DSRequestConstants.STATUS_SIGNED,
			DSRequestConstants.STATUS_SIGNED, DSRequestConstants.STATUS_SENT,
			localDateTime.plusDays(1), localDateTime);
		_testUpdateDSRequest(
			DSRequestConstants.STATUS_VOIDED, DSRequestConstants.STATUS_SIGNED,
			DSRequestConstants.STATUS_SIGNED, DSRequestConstants.STATUS_VOIDED,
			localDateTime.plusDays(1), localDateTime);
		_testUpdateDSRequest(
			DSRequestConstants.STATUS_VOIDED, DSRequestConstants.STATUS_SENT,
			DSRequestConstants.STATUS_VOIDED, DSRequestConstants.STATUS_VOIDED,
			localDateTime.plusDays(1), localDateTime);
		_testUpdateDSRequest(
			DSRequestConstants.STATUS_VOIDED, DSRequestConstants.STATUS_SENT,
			DSRequestConstants.STATUS_VOIDED, DSRequestConstants.STATUS_VOIDED,
			null, localDateTime);

		_testUpdateDSRequestWhenDSRecipientStatusesDiffer();
		_testUpdateDSRequestWhenObjectEntryIsNotDSRequest();
	}

	private DSRequest _addDSRequest(DSEnvelope dsEnvelope, FileEntry fileEntry)
		throws Exception {

		DSEnvelopeManager dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						if (Objects.equals(method.getName(), "addDSEnvelope")) {
							return arguments[2];
						}

						if (Objects.equals(method.getName(), "getDSEnvelope")) {
							return dsEnvelope;
						}

						return null;
					}));

		try {
			return _dsRequestManager.addDSRequest(
				TestPropsValues.getCompanyId(), dsEnvelope,
				new long[] {fileEntry.getFileEntryId()},
				TestPropsValues.getGroupId(), TestPropsValues.getUserId());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager", dsEnvelopeManager);
		}
	}

	private DSRequest _addDSRequestWithoutValidation(
			DSEnvelope dsEnvelope, long userId, long... fileEntryIds)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_dsRequestManager, "_addDSRequest",
			new Class<?>[] {
				long.class, DSEnvelope.class, long[].class, long.class,
				long.class
			},
			TestPropsValues.getCompanyId(), dsEnvelope, fileEntryIds,
			TestPropsValues.getGroupId(), userId);
	}

	private DSRequest _addDSRequestWithoutValidation(
			String emailAddress, String status)
		throws Exception {

		DSEnvelope dsEnvelope = _createDSEnvelope();

		List<DSRecipient> dsRecipients = dsEnvelope.getDSRecipients();

		DSRecipient dsRecipient = dsRecipients.get(0);

		dsRecipient.setEmailAddress(emailAddress);

		dsEnvelope.setStatus(status);

		return _addDSRequestWithoutValidation(
			dsEnvelope, TestPropsValues.getUserId(),
			RandomTestUtil.randomInt());
	}

	private FileEntry _addFileEntry() throws Exception {
		return _addFileEntry(RandomTestUtil.randomString() + ".txt");
	}

	private FileEntry _addFileEntry(String fileName) throws Exception {
		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			MimeTypesUtil.getContentType(fileName),
			FileUtil.getBytes(getClass(), "dependencies/Document.pdf"), null,
			null, null,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));
	}

	private DSEnvelope _createDSEnvelope() {
		DSEnvelope dsEnvelope = new DSEnvelope();

		dsEnvelope.setDSEnvelopeId(RandomTestUtil.randomString());
		dsEnvelope.setDSRecipients(
			ListUtil.fromArray(_createDSRecipient(), _createDSRecipient()));
		dsEnvelope.setEmailSubject(RandomTestUtil.randomString());
		dsEnvelope.setStatus(DSRequestConstants.STATUS_SENT);

		return dsEnvelope;
	}

	private DSEnvelope _createDSEnvelope(DSRecipient... dsRecipients) {
		DSEnvelope dsEnvelope = _createDSEnvelope();

		dsEnvelope.setDSRecipients(ListUtil.fromArray(dsRecipients));

		return dsEnvelope;
	}

	private DSEnvelope _createDSEnvelope(User user) {
		return _createDSEnvelope(_createDSRecipient(user));
	}

	private DSRecipient _createDSRecipient() {
		return _createDSRecipient(
			RandomTestUtil.randomString() + "@liferay.com");
	}

	private DSRecipient _createDSRecipient(String emailAddress) {
		DSRecipient dsRecipient = new DSRecipient();

		dsRecipient.setEmailAddress(emailAddress);
		dsRecipient.setName(RandomTestUtil.randomString());

		return dsRecipient;
	}

	private DSRecipient _createDSRecipient(User user) {
		DSRecipient dsRecipient = _createDSRecipient(user.getEmailAddress());

		dsRecipient.setDSClientUserId(String.valueOf(user.getUserId()));

		return dsRecipient;
	}

	private long _getDSRequestDocumentId(long fileEntryId) throws Exception {
		List<Map<String, Serializable>> dsRequestDocumentValuesList =
			_getValuesList(
				"(fileEntryId eq " + fileEntryId + ")",
				_dsRequestDocumentObjectDefinition);

		return MapUtil.getLong(
			dsRequestDocumentValuesList.get(0),
			_dsRequestDocumentObjectDefinition.getPKObjectFieldName());
	}

	private ObjectEntry _getDSRequestObjectEntry(DSEnvelope dsEnvelope)
		throws Exception {

		return _objectEntryLocalService.getObjectEntry(
			dsEnvelope.getDSEnvelopeId(), 0,
			_dsRequestObjectDefinition.getObjectDefinitionId());
	}

	private List<Map<String, Serializable>> _getDSRequestRecipientValuesList(
			long dsRequestId)
		throws Exception {

		return _getValuesList(
			"(r_dsRequestToDSRequestRecipients_l_dsRequestId eq '" +
				dsRequestId + "')",
			_dsRequestRecipientObjectDefinition);
	}

	private List<NotificationQueueEntry> _getNotificationQueueEntries(
		DSRequest dsRequest) {

		return ListUtil.filter(
			_notificationQueueEntryLocalService.getNotificationQueueEntries(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			notificationQueueEntry ->
				Objects.equals(
					notificationQueueEntry.getClassName(),
					DSRequest.class.getName()) &&
				(notificationQueueEntry.getClassPK() ==
					dsRequest.getDSRequestId()));
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
		DSRecipient dsRecipient = _createDSRecipient();

		DSEnvelope dsEnvelope = _createDSEnvelope(dsRecipient);

		dsEnvelope.setDSEnvelopeId(null);

		AssertUtils.assertFailure(
			PortalException.class,
			"A digital signature request requires a digital signature " +
				"envelope ID",
			() -> _addDSRequestWithoutValidation(
				dsEnvelope, TestPropsValues.getUserId(),
				RandomTestUtil.randomInt()));

		Assert.assertEquals(
			Collections.emptyList(),
			_getValuesList(
				"(emailAddress eq '" + dsRecipient.getEmailAddress() + "')",
				_dsRequestRecipientObjectDefinition));
	}

	private void _testAddDSRequestWhenDSEnvelopeVoidFails() throws Exception {
		DSEnvelope dsEnvelope = _createDSEnvelope(
			_createDSRecipient(UserTestUtil.addUser()));
		FileEntry fileEntry = _addFileEntry();

		DSEnvelopeManager dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						if (Objects.equals(
								method.getName(), "voidDSEnvelope")) {

							throw new PortalException();
						}

						DSEnvelope addedDSEnvelope = (DSEnvelope)arguments[2];

						addedDSEnvelope.setDSEnvelopeId(null);

						return addedDSEnvelope;
					}));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.digital.signature.internal.manager." +
					"DSRequestManagerImpl",
				LoggerTestUtil.ERROR)) {

			PortalException portalException = Assert.assertThrows(
				PortalException.class,
				() -> _dsRequestManager.addDSRequest(
					TestPropsValues.getCompanyId(), dsEnvelope,
					new long[] {fileEntry.getFileEntryId()},
					TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

			Assert.assertEquals(
				"A digital signature request requires a digital signature " +
					"envelope ID",
				portalException.getMessage());

			Throwable[] suppressedThrowables = portalException.getSuppressed();

			Assert.assertEquals(
				Arrays.toString(suppressedThrowables), 1,
				suppressedThrowables.length);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager", dsEnvelopeManager);
		}
	}

	private void _testAddDSRequestWhenDSRecipientIsInvalid() throws Exception {
		DSRecipient dsRecipient = _createDSRecipient();

		DSEnvelope dsEnvelope = _createDSEnvelope(dsRecipient);

		FileEntry fileEntry = _addFileEntry();

		AssertUtils.assertFailure(
			DSRequestException.MustHaveExistingRecipient.class,
			"No user exists with recipient email address " +
				dsRecipient.getEmailAddress(),
			() -> _addDSRequest(dsEnvelope, fileEntry));

		User guestUser = _userLocalService.getGuestUser(
			TestPropsValues.getCompanyId());

		dsEnvelope.setDSRecipients(
			ListUtil.fromArray(_createDSRecipient(guestUser)));

		AssertUtils.assertFailure(
			DSRequestException.MustHaveValidRecipient.class,
			"User " + guestUser.getUserId() +
				" cannot be a digital signature request recipient",
			() -> _addDSRequest(dsEnvelope, fileEntry));

		User inactiveUser = UserTestUtil.addUser();

		inactiveUser = _userLocalService.updateStatus(
			inactiveUser.getUserId(), WorkflowConstants.STATUS_INACTIVE,
			ServiceContextTestUtil.getServiceContext());

		dsEnvelope.setDSRecipients(
			ListUtil.fromArray(_createDSRecipient(inactiveUser)));

		AssertUtils.assertFailure(
			DSRequestException.MustHaveValidRecipient.class,
			"User " + inactiveUser.getUserId() +
				" cannot be a digital signature request recipient",
			() -> _addDSRequest(dsEnvelope, fileEntry));

		User user = UserTestUtil.addUser();

		dsEnvelope.setDSRecipients(
			ListUtil.fromArray(
				_createDSRecipient(user), _createDSRecipient(user)));

		AssertUtils.assertFailure(
			DSRequestException.MustNotDuplicateRecipient.class,
			"User " + user.getUserId() +
				" is already a digital signature request recipient",
			() -> _addDSRequest(dsEnvelope, fileEntry));

		dsEnvelope.setDSRecipients(
			ListUtil.fromArray(_createDSRecipient(user)));

		DSRequest dsRequest = _addDSRequest(dsEnvelope, fileEntry);

		Assert.assertEquals(
			dsEnvelope.getDSEnvelopeId(), dsRequest.getExternalReferenceCode());
	}

	private void _testAddDSRequestWhenDSRecipientNameExceedsMaxLength()
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());

		User user = TestPropsValues.getUser();

		themeDisplay.setUser(user);

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		FileEntry fileEntry = _addFileEntry();

		mockLiferayResourceRequest.setParameter(
			"fileEntryIds", String.valueOf(fileEntry.getFileEntryId()));

		mockLiferayResourceRequest.setParameter(
			"recipients",
			JSONUtil.putAll(
				JSONUtil.put(
					"email", user.getEmailAddress()
				).put(
					"fullName", user.getFullName()
				)
			).toString());

		DSRecipient dsRecipient = _createDSRecipient();

		dsRecipient.setName(RandomTestUtil.randomString(281));

		DSEnvelope dsEnvelope = _createDSEnvelope(dsRecipient);

		Queue<Object[]> argumentsList = new LinkedList<>();

		DSEnvelopeManager dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						if (Objects.equals(
								method.getName(), "voidDSEnvelope")) {

							argumentsList.add(arguments);

							return null;
						}

						return dsEnvelope;
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
				_dsRequestManager, "_dsEnvelopeManager", dsEnvelopeManager);
		}

		Assert.assertEquals(argumentsList.toString(), 1, argumentsList.size());

		Object[] arguments = argumentsList.poll();

		Assert.assertEquals(dsEnvelope.getDSEnvelopeId(), arguments[1]);

		Assert.assertEquals(
			Collections.emptyList(),
			_getValuesList(
				"(externalReferenceCode eq '" + dsEnvelope.getDSEnvelopeId() +
					"')",
				_dsRequestObjectDefinition));
		Assert.assertEquals(
			Collections.emptyList(),
			_getValuesList(
				"(fileEntryId eq " + fileEntry.getFileEntryId() + ")",
				_dsRequestDocumentObjectDefinition));
		Assert.assertEquals(
			Collections.emptyList(),
			_getValuesList(
				"(emailAddress eq '" + dsRecipient.getEmailAddress() + "')",
				_dsRequestRecipientObjectDefinition));
	}

	private void _testAddDSRequestWhenDSRequestRecipientUserIsDeleted()
		throws Exception {

		User user = UserTestUtil.addUser();

		DSRecipient dsRecipient = _createDSRecipient(user);

		_addDSRequestWithoutValidation(
			_createDSEnvelope(dsRecipient), TestPropsValues.getUserId(),
			RandomTestUtil.randomInt());

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

	private void _testAddDSRequestWhenDigitalSignatureIsDisabled()
		throws Exception {

		DSEnvelope dsEnvelope = _createDSEnvelope(TestPropsValues.getUser());
		FileEntry fileEntry = _addFileEntry();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						DigitalSignatureConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"embeddedViewEnabled", true
						).put(
							"enabled", false
						).put(
							"siteSettingsStrategy", "always-inherit"
						).build())) {

			AssertUtils.assertFailure(
				DSRequestException.MustBeEnabled.class,
				"Digital signature embedded view is not enabled for group " +
					TestPropsValues.getGroupId(),
				() -> _addDSRequest(dsEnvelope, fileEntry));
		}

		DSRequest dsRequest = _addDSRequest(dsEnvelope, fileEntry);

		Assert.assertEquals(
			dsEnvelope.getDSEnvelopeId(), dsRequest.getExternalReferenceCode());
	}

	private void _testAddDSRequestWhenEmbeddedViewIsDisabled()
		throws Exception {

		DSEnvelope dsEnvelope = _createDSEnvelope(TestPropsValues.getUser());
		FileEntry fileEntry = _addFileEntry();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						DigitalSignatureConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"embeddedViewEnabled", false
						).put(
							"enabled", true
						).put(
							"siteSettingsStrategy", "always-inherit"
						).build())) {

			AssertUtils.assertFailure(
				DSRequestException.MustBeEnabled.class,
				"Digital signature embedded view is not enabled for group " +
					TestPropsValues.getGroupId(),
				() -> _addDSRequest(dsEnvelope, fileEntry));
		}

		DSRequest dsRequest = _addDSRequest(dsEnvelope, fileEntry);

		Assert.assertEquals(
			dsEnvelope.getDSEnvelopeId(), dsRequest.getExternalReferenceCode());
	}

	private void _testAddDSRequestWhenExpireAfterIsNegative() throws Exception {
		DSEnvelope dsEnvelope = _createDSEnvelope(TestPropsValues.getUser());

		dsEnvelope.setExpireAfter(-1);

		FileEntry fileEntry = _addFileEntry();

		AssertUtils.assertFailure(
			DSRequestException.MustNotHaveNegativeExpireAfter.class,
			"Expire after must not be negative",
			() -> _addDSRequest(dsEnvelope, fileEntry));

		dsEnvelope.setExpireAfter(5);

		DSRequest dsRequest = _addDSRequest(dsEnvelope, fileEntry);

		Assert.assertEquals(
			dsEnvelope.getDSEnvelopeId(), dsRequest.getExternalReferenceCode());
	}

	private void _testAddDSRequestWhenExpiredDSEnvelopeIsCompleted()
		throws Exception {

		DSEnvelope dsEnvelope1 = _createDSEnvelope();

		LocalDateTime localDateTime = LocalDateTime.now();

		dsEnvelope1.setExpirationLocalDateTime(localDateTime.minusDays(1));

		FileEntry fileEntry = _addFileEntry(
			RandomTestUtil.randomString() + ".pdf");

		DSRequest dsRequest = _addDSRequestWithoutValidation(
			dsEnvelope1, TestPropsValues.getUserId(),
			fileEntry.getFileEntryId());

		Assert.assertTrue(dsRequest.isReplaceable());

		dsEnvelope1.setStatus(DSRequestConstants.STATUS_COMPLETED);

		DSEnvelopeManager dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> dsEnvelope1));

		try {
			DSEnvelope dsEnvelope2 = _createDSEnvelope(
				TestPropsValues.getUser());

			AssertUtils.assertFailure(
				DSRequestException.MustHaveReplaceableFileEntry.class,
				StringBundler.concat(
					"File entry ", fileEntry.getFileEntryId(),
					" already has a digital signature request with status \"",
					DSRequestConstants.STATUS_COMPLETED, "\""),
				() -> _dsRequestManager.addDSRequest(
					TestPropsValues.getCompanyId(), dsEnvelope2,
					new long[] {fileEntry.getFileEntryId()},
					TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager", dsEnvelopeManager);
		}

		dsRequest = _dsRequestManager.fetchDSRequest(
			TestPropsValues.getCompanyId(), dsRequest.getDSRequestId());

		Assert.assertEquals(
			DSRequestConstants.STATUS_COMPLETED, dsRequest.getStatus());
	}

	private void _testAddDSRequestWhenFeatureFlagIsDisabled() throws Exception {
		DSEnvelope dsEnvelope = _createDSEnvelope(TestPropsValues.getUser());

		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					false, "LPD-69290")) {

			AssertUtils.assertFailure(
				DSRequestException.MustBeEnabled.class,
				"Digital signature embedded view is not enabled for group " +
					TestPropsValues.getGroupId(),
				() -> _dsRequestManager.addDSRequest(
					TestPropsValues.getCompanyId(), dsEnvelope, new long[0],
					TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
		}

		FileEntry fileEntry = _addFileEntry();

		DSRequest dsRequest = _addDSRequest(dsEnvelope, fileEntry);

		Assert.assertEquals(
			dsEnvelope.getDSEnvelopeId(), dsRequest.getExternalReferenceCode());
	}

	private void _testAddDSRequestWhenFileEntryIsInvalid() throws Exception {
		AssertUtils.assertFailure(
			DSRequestException.MustHaveFileEntry.class,
			"A digital signature request must have at least one file entry",
			() -> _dsRequestManager.addDSRequest(
				TestPropsValues.getCompanyId(), new DSEnvelope(), new long[0],
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		DSEnvelope dsEnvelope1 = _createDSEnvelope();
		FileEntry fileEntry = _addFileEntry();

		_addDSRequestWithoutValidation(
			dsEnvelope1, TestPropsValues.getUserId(),
			fileEntry.getFileEntryId());

		DSEnvelope dsEnvelope2 = _createDSEnvelope(TestPropsValues.getUser());

		AssertUtils.assertFailure(
			DSRequestException.MustHaveReplaceableFileEntry.class,
			StringBundler.concat(
				"File entry ", fileEntry.getFileEntryId(),
				" already has a digital signature request with status \"",
				DSRequestConstants.STATUS_SENT, "\""),
			() -> _addDSRequest(dsEnvelope2, fileEntry));

		dsEnvelope1.setStatus(DSRequestConstants.STATUS_VOIDED);

		DSEnvelopeManager dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						if (Objects.equals(method.getName(), "addDSEnvelope")) {
							return arguments[2];
						}

						return dsEnvelope1;
					}));

		try {
			DSRequest dsRequest = _dsRequestManager.addDSRequest(
				TestPropsValues.getCompanyId(), dsEnvelope2,
				new long[] {fileEntry.getFileEntryId()},
				TestPropsValues.getGroupId(), TestPropsValues.getUserId());

			Assert.assertEquals(
				dsEnvelope2.getDSEnvelopeId(),
				dsRequest.getExternalReferenceCode());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager", dsEnvelopeManager);
		}
	}

	private void _testAddDSRequestWhenGroupIsStagingGroup() throws Exception {
		Group liveGroup = GroupTestUtil.addGroup();

		Group group = GroupTestUtil.addGroup();

		group.setLiveGroupId(liveGroup.getGroupId());

		Group stagingGroup = _groupLocalService.updateGroup(group);

		AssertUtils.assertFailure(
			DSRequestException.MustNotBeStagingGroup.class,
			"Digital signature requests are not supported in staging group " +
				stagingGroup.getGroupId(),
			() -> _dsRequestManager.addDSRequest(
				TestPropsValues.getCompanyId(),
				_createDSEnvelope(_createDSRecipient(UserTestUtil.addUser())),
				new long[] {RandomTestUtil.randomLong()},
				stagingGroup.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _testFetchDSRequest() throws Exception {
		long fileEntryId = RandomTestUtil.randomInt();

		DSRequest dsRequest1 = _addDSRequestWithoutValidation(
			_createDSEnvelope(), TestPropsValues.getUserId(), fileEntryId);

		Assert.assertNull(
			_dsRequestManager.fetchDSRequest(
				RandomTestUtil.randomLong(), dsRequest1.getDSRequestId()));

		Assert.assertNull(
			_dsRequestManager.fetchDSRequest(
				TestPropsValues.getCompanyId(),
				_getDSRequestDocumentId(fileEntryId)));

		DSRequest dsRequest2 = _dsRequestManager.fetchDSRequest(
			TestPropsValues.getCompanyId(), dsRequest1.getDSRequestId());

		Assert.assertEquals(
			dsRequest1.getDSRequestId(), dsRequest2.getDSRequestId());
		Assert.assertEquals(
			DSRequestConstants.STATUS_SENT, dsRequest2.getStatus());
	}

	private void _testFetchDSRequestWhenExpirationDateIsPast()
		throws Exception {

		DSEnvelope dsEnvelope = _createDSEnvelope();

		LocalDateTime localDateTime = LocalDateTime.now();

		dsEnvelope.setExpirationLocalDateTime(localDateTime.minusDays(1));

		DSRequest dsRequest = _addDSRequestWithoutValidation(
			dsEnvelope, TestPropsValues.getUserId(),
			RandomTestUtil.randomInt());

		dsRequest = _dsRequestManager.fetchDSRequest(
			TestPropsValues.getCompanyId(), dsRequest.getDSRequestId());

		Assert.assertEquals(
			DSRequestConstants.STATUS_EXPIRED, dsRequest.getStatus());
		Assert.assertTrue(dsRequest.isReplaceable());
		Assert.assertTrue(dsRequest.isTerminal());
	}

	private void _testFetchDSRequestWhenSiteOverridesInstanceSettings()
		throws Exception {

		DSRequest dsRequest1 = _addDSRequestWithoutValidation(
			_createDSEnvelope(), TestPropsValues.getUserId(),
			RandomTestUtil.randomInt());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						DigitalSignatureConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"embeddedViewEnabled", false
						).put(
							"enabled", true
						).put(
							"siteSettingsStrategy", "always-override"
						).build())) {

			Assert.assertNull(
				_dsRequestManager.fetchDSRequest(
					TestPropsValues.getCompanyId(),
					dsRequest1.getDSRequestId()));

			try (GroupConfigurationTemporarySwapper
					groupConfigurationTemporarySwapper =
						new GroupConfigurationTemporarySwapper(
							TestPropsValues.getGroupId(),
							DigitalSignatureConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"embeddedViewEnabled", true
							).put(
								"enabled", true
							).build())) {

				DSRequest dsRequest2 = _dsRequestManager.fetchDSRequest(
					TestPropsValues.getCompanyId(),
					dsRequest1.getDSRequestId());

				Assert.assertEquals(
					dsRequest1.getDSRequestId(), dsRequest2.getDSRequestId());
			}
		}
	}

	private void _testSendDSRequestNotificationsWhenDSRequestIsVoided()
		throws Exception {

		User user = UserTestUtil.addUser();

		DSRequest dsRequest = _addDSRequestWithoutValidation(
			user.getEmailAddress(), DSRequestConstants.STATUS_VOIDED);

		_dsRequestManager.sendDSRequestNotifications(
			dsRequest.getDSRequestId());

		List<NotificationQueueEntry> notificationQueueEntries =
			_getNotificationQueueEntries(dsRequest);

		Assert.assertEquals(
			notificationQueueEntries.toString(), 0,
			notificationQueueEntries.size());
	}

	private void _testSendDSRequestNotificationsWhenTemplateIsMissing()
		throws Exception {

		User user = UserTestUtil.addUser();

		DSRequest dsRequest = _addDSRequestWithoutValidation(
			user.getEmailAddress(), DSRequestConstants.STATUS_SENT);

		NotificationTemplateLocalService notificationTemplateLocalService =
			(NotificationTemplateLocalService)
				ReflectionTestUtil.getAndSetFieldValue(
					_dsRequestManager, "_notificationTemplateLocalService",
					ProxyUtil.newProxyInstance(
						NotificationTemplateLocalService.class.getClassLoader(),
						new Class<?>[] {NotificationTemplateLocalService.class},
						(proxy, method, arguments) -> null));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.digital.signature.internal.manager." +
					"DSRequestManagerImpl",
				LoggerTestUtil.WARN)) {

			_dsRequestManager.sendDSRequestNotifications(
				dsRequest.getDSRequestId());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_notificationTemplateLocalService",
				notificationTemplateLocalService);
		}

		List<NotificationQueueEntry> notificationQueueEntries =
			_getNotificationQueueEntries(dsRequest);

		Assert.assertEquals(
			notificationQueueEntries.toString(), 0,
			notificationQueueEntries.size());
	}

	private void _testUpdateDSRequest(
			String dsEnvelopeStatus, String dsRecipientStatus,
			String expectedRequestRecipientStatus, String expectedRequestStatus,
			LocalDateTime expirationLocalDateTime,
			LocalDateTime statusLocalDateTime)
		throws Exception {

		DSEnvelope dsEnvelope = _createDSEnvelope(
			_createDSRecipient(UserTestUtil.addUser()),
			_createDSRecipient(UserTestUtil.addUser()));

		dsEnvelope.setStatus(DSRequestConstants.STATUS_CREATED);

		for (DSRecipient dsRecipient : dsEnvelope.getDSRecipients()) {
			dsRecipient.setStatus(DSRequestConstants.STATUS_CREATED);
		}

		DSRequest dsRequest = _addDSRequestWithoutValidation(
			dsEnvelope, TestPropsValues.getUserId(),
			RandomTestUtil.randomInt());

		dsEnvelope.setExpirationLocalDateTime(expirationLocalDateTime);
		dsEnvelope.setStatus(dsEnvelopeStatus);
		dsEnvelope.setStatusLocalDateTime(statusLocalDateTime);

		for (DSRecipient dsRecipient : dsEnvelope.getDSRecipients()) {
			dsRecipient.setStatus(dsRecipientStatus);
			dsRecipient.setStatusLocalDateTime(statusLocalDateTime);
		}

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

		User user = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_dsRequestManager.updateDSRequest(dsRequest.getDSRequestId());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager", dsEnvelopeManager);
		}

		ObjectEntry dsRequestObjectEntry = _getDSRequestObjectEntry(dsEnvelope);

		Map<String, Serializable> dsRequestValues =
			dsRequestObjectEntry.getValues();

		Assert.assertEquals(
			_toDate(expirationLocalDateTime),
			dsRequestValues.get("requestExpirationDate"));
		Assert.assertEquals(
			expectedRequestStatus,
			MapUtil.getString(dsRequestValues, "requestStatus"));
		Assert.assertEquals(
			_toDate(statusLocalDateTime),
			dsRequestValues.get("requestStatusDate"));

		List<Map<String, Serializable>> dsRequestRecipientValuesList =
			_getDSRequestRecipientValuesList(
				dsRequestObjectEntry.getObjectEntryId());

		Assert.assertEquals(
			dsRequestRecipientValuesList.toString(), 2,
			dsRequestRecipientValuesList.size());

		for (Map<String, Serializable> values : dsRequestRecipientValuesList) {
			Assert.assertEquals(
				expectedRequestRecipientStatus,
				MapUtil.getString(values, "requestRecipientStatus"));
			Assert.assertEquals(
				_toDate(statusLocalDateTime),
				values.get("requestRecipientStatusDate"));
		}
	}

	private void _testUpdateDSRequestWhenDSRecipientStatusesDiffer()
		throws Exception {

		User completedUser = UserTestUtil.addUser();

		DSRecipient completedDSRecipient = _createDSRecipient(completedUser);

		User deliveredUser = UserTestUtil.addUser();

		DSRecipient deliveredDSRecipient = _createDSRecipient(deliveredUser);

		DSEnvelope dsEnvelope = _createDSEnvelope(
			completedDSRecipient, deliveredDSRecipient);

		DSRequest dsRequest = _addDSRequestWithoutValidation(
			dsEnvelope, TestPropsValues.getUserId(),
			RandomTestUtil.randomInt());

		LocalDateTime completedLocalDateTime = LocalDateTime.ofEpochSecond(
			System.currentTimeMillis() / 1000, 0, ZoneOffset.UTC);

		completedDSRecipient.setStatus(DSRequestConstants.STATUS_COMPLETED);
		completedDSRecipient.setStatusLocalDateTime(completedLocalDateTime);

		LocalDateTime deliveredLocalDateTime =
			completedLocalDateTime.minusHours(1);

		deliveredDSRecipient.setStatus("delivered");
		deliveredDSRecipient.setStatusLocalDateTime(deliveredLocalDateTime);

		dsEnvelope.setDSRecipients(
			ListUtil.fromArray(deliveredDSRecipient, completedDSRecipient));

		DSEnvelopeManager dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> dsEnvelope));

		try {
			_dsRequestManager.updateDSRequest(dsRequest.getDSRequestId());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager", dsEnvelopeManager);
		}

		Map<Long, Map<String, Serializable>> dsRequestRecipientValuesMap =
			new HashMap<>();

		for (Map<String, Serializable> values :
				_getDSRequestRecipientValuesList(dsRequest.getDSRequestId())) {

			dsRequestRecipientValuesMap.put(
				MapUtil.getLong(values, "r_userToDSRequestRecipients_userId"),
				values);
		}

		Map<String, Serializable> dsRequestRecipientValues =
			dsRequestRecipientValuesMap.get(completedUser.getUserId());

		Assert.assertEquals(
			DSRequestConstants.STATUS_COMPLETED,
			MapUtil.getString(
				dsRequestRecipientValues, "requestRecipientStatus"));
		Assert.assertEquals(
			_toDate(completedLocalDateTime),
			dsRequestRecipientValues.get("requestRecipientStatusDate"));

		dsRequestRecipientValues = dsRequestRecipientValuesMap.get(
			deliveredUser.getUserId());

		Assert.assertEquals(
			DSRequestConstants.STATUS_SENT,
			MapUtil.getString(
				dsRequestRecipientValues, "requestRecipientStatus"));
		Assert.assertEquals(
			_toDate(deliveredLocalDateTime),
			dsRequestRecipientValues.get("requestRecipientStatusDate"));
	}

	private void _testUpdateDSRequestWhenObjectEntryIsNotDSRequest()
		throws Exception {

		long fileEntryId = RandomTestUtil.randomInt();

		_addDSRequestWithoutValidation(
			_createDSEnvelope(), TestPropsValues.getUserId(), fileEntryId);

		long dsRequestDocumentId = _getDSRequestDocumentId(fileEntryId);

		AssertUtils.assertFailure(
			NoSuchObjectEntryException.class,
			StringBundler.concat(
				"No ObjectEntry exists with the key {objectDefinitionId=",
				_dsRequestObjectDefinition.getObjectDefinitionId(),
				", objectEntryId=", dsRequestDocumentId, "}"),
			() -> _dsRequestManager.updateDSRequest(dsRequestDocumentId));
	}

	private Date _toDate(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}

		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

	private CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private ObjectDefinition _dsRequestDocumentObjectDefinition;

	@Inject
	private DSRequestManager _dsRequestManager;

	private ObjectDefinition _dsRequestObjectDefinition;
	private ObjectDefinition _dsRequestRecipientObjectDefinition;

	@Inject
	private DSRequestURLProvider _dsRequestURLProvider;

	@Inject(
		filter = "filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private FilterFactory<Predicate> _filterFactory;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(filter = "mvc.command.name=/digital_signature/add_ds_envelope")
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private NotificationQueueEntryLocalService
		_notificationQueueEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private UserLocalService _userLocalService;

}