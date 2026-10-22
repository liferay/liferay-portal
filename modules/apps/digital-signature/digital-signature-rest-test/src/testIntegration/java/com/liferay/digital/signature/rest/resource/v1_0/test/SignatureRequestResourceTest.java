/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.constants.DSObjectDefinitionConstants;
import com.liferay.digital.signature.constants.DSRequestConstants;
import com.liferay.digital.signature.exception.DSEnvelopeException;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.digital.signature.model.DSRequestRecipient;
import com.liferay.digital.signature.rest.client.dto.v1_0.Creator;
import com.liferay.digital.signature.rest.client.dto.v1_0.FileEntryBrief;
import com.liferay.digital.signature.rest.client.dto.v1_0.SignatureRequest;
import com.liferay.digital.signature.rest.client.dto.v1_0.SignatureRequestRecipient;
import com.liferay.digital.signature.rest.client.pagination.Page;
import com.liferay.digital.signature.rest.client.pagination.Pagination;
import com.liferay.digital.signature.rest.client.problem.Problem;
import com.liferay.digital.signature.rest.client.resource.v1_0.SignatureRequestResource;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.service.NotificationQueueEntryLocalService;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Danny Situ
 */
@FeatureFlag("LPD-69290")
@RunWith(Arquillian.class)
public class SignatureRequestResourceTest
	extends BaseSignatureRequestResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.digital.signature.impl",
			SignatureRequestResourceTest.class,
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
				testCompany.getCompanyId(),
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
		_dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						if (Objects.equals(method.getName(), "getDSEnvelope")) {
							return _dsEnvelopeAtomicReference.get();
						}

						if (!Objects.equals(
								method.getName(), "addDSEnvelope")) {

							return null;
						}

						DSEnvelope dsEnvelope = (DSEnvelope)arguments[2];

						dsEnvelope.setDSEnvelopeId(
							RandomTestUtil.randomString());

						for (DSRecipient dsRecipient :
								dsEnvelope.getDSRecipients()) {

							dsRecipient.setStatus(
								DSRequestConstants.STATUS_SENT);
						}

						_dsEnvelopeAtomicReference.set(dsEnvelope);

						return dsEnvelope;
					}));
		_dsRequestDocumentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_REQUEST_DOCUMENT,
					testCompany.getCompanyId());
		_dsRequestObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_REQUEST,
					testCompany.getCompanyId());
		_dsRequestRecipientObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_REQUEST_RECIPIENT,
					testCompany.getCompanyId());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_dsRequestManager, "_dsEnvelopeManager", _dsEnvelopeManager);

		_companyConfigurationTemporarySwapper.close();

		super.tearDown();
	}

	@Override
	@Test
	public void testGetSignatureRequest() throws Exception {
		super.testGetSignatureRequest();

		User recipientUser = UserTestUtil.addUser();

		ObjectEntry dsRequestObjectEntry = _addDSRequestObjectEntry(
			recipientUser.getEmailAddress(), DSRequestConstants.STATUS_SENT);
		SignatureRequestResource recipientUserSignatureRequestResource =
			_getSignatureRequestResource(recipientUser);

		SignatureRequest signatureRequest =
			recipientUserSignatureRequestResource.getSignatureRequest(
				dsRequestObjectEntry.getObjectEntryId());

		Creator creator = signatureRequest.getCreator();

		Assert.assertEquals(
			Long.valueOf(TestPropsValues.getUserId()), creator.getId());

		DSRequest dsRequest = _dsRequestManager.fetchDSRequest(
			testCompany.getCompanyId(),
			dsRequestObjectEntry.getObjectEntryId());

		Assert.assertEquals(
			MapUtil.getString(dsRequestObjectEntry.getValues(), "emailMessage"),
			signatureRequest.getEmailMessage());
		Assert.assertEquals(
			MapUtil.getString(dsRequestObjectEntry.getValues(), "emailSubject"),
			signatureRequest.getEmailSubject());
		Assert.assertEquals(
			dsRequest.getExpirationDate(),
			signatureRequest.getExpirationDate());
		Assert.assertEquals(
			Long.valueOf(dsRequestObjectEntry.getObjectEntryId()),
			signatureRequest.getId());
		Assert.assertEquals(
			DSRequestConstants.STATUS_SENT, signatureRequest.getStatus());
		Assert.assertEquals(
			dsRequest.getStatusDate(), signatureRequest.getStatusDate());

		Map<String, Map<String, String>> actions =
			signatureRequest.getActions();

		Map<String, String> signAction = actions.get("sign");

		Assert.assertEquals(
			String.valueOf(signatureRequest.getId()),
			HttpComponentsUtil.getParameter(
				signAction.get("href"), "dsRequestId", false));
		Assert.assertTrue(
			StringUtil.endsWith(
				HttpComponentsUtil.getPath(signAction.get("href")),
				"/digital_signature/open_ds_request"));
		Assert.assertEquals("GET", signAction.get("method"));

		SignatureRequestRecipient[] signatureRequestRecipients =
			signatureRequest.getSignatureRequestRecipients();

		Assert.assertEquals(
			Arrays.toString(signatureRequestRecipients), 1,
			signatureRequestRecipients.length);

		List<DSRequestRecipient> dsRequestRecipients =
			dsRequest.getDSRequestRecipients();

		DSRequestRecipient dsRequestRecipient = dsRequestRecipients.get(0);

		SignatureRequestRecipient signatureRequestRecipient =
			signatureRequestRecipients[0];

		Assert.assertEquals(
			recipientUser.getEmailAddress(),
			signatureRequestRecipient.getEmailAddress());
		Assert.assertEquals(
			dsRequestRecipient.getName(), signatureRequestRecipient.getName());
		Assert.assertEquals(
			dsRequestRecipient.getSentDate(),
			signatureRequestRecipient.getSentDate());
		Assert.assertEquals(
			DSRequestConstants.STATUS_SENT,
			signatureRequestRecipient.getStatus());
		Assert.assertEquals(
			dsRequestRecipient.getStatusDate(),
			signatureRequestRecipient.getStatusDate());
		Assert.assertEquals(
			Long.valueOf(recipientUser.getUserId()),
			signatureRequestRecipient.getUserId());

		signatureRequest = signatureRequestResource.getSignatureRequest(
			dsRequestObjectEntry.getObjectEntryId());

		Assert.assertTrue(MapUtil.isEmpty(signatureRequest.getActions()));

		User user = UserTestUtil.addUser();

		SignatureRequestResource userSignatureRequestResource =
			_getSignatureRequestResource(user);

		_assertProblemException(
			"NOT_FOUND",
			() -> userSignatureRequestResource.getSignatureRequest(
				dsRequestObjectEntry.getObjectEntryId()));
	}

	@Override
	@Test
	public void testGetSiteSignatureRequestsPage() throws Exception {
		super.testGetSiteSignatureRequestsPage();

		_testGetSiteSignatureRequestsPageWithSearch();
		_testGetSiteSignatureRequestsPageWithoutViewPermission();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSignatureRequestsPageWithPagination()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSignatureRequest() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSiteSignatureRequestsPage() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostSignatureRequestNotification() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostSignatureRequestVoid() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostSiteSignatureRequest() throws Exception {
	}

	@Override
	@Test
	public void testPostSignatureRequestNotification() throws Exception {
		super.testPostSignatureRequestNotification();

		_testPostSignatureRequestNotification();
		_testPostSignatureRequestNotificationWithTerminalSignatureRequest();
		_testPostSignatureRequestNotificationWithoutUpdatePermission();
		_testPostSignatureRequestNotificationWithoutViewPermission();
	}

	@Override
	@Test
	public void testPostSignatureRequestVoid() throws Exception {
		super.testPostSignatureRequestVoid();

		_testPostSignatureRequestVoid();
		_testPostSignatureRequestVoidWithBlankVoidedReason();
		_testPostSignatureRequestVoidWithNonvoidableDSEnvelope();
		_testPostSignatureRequestVoidWithTerminalSignatureRequest();
		_testPostSignatureRequestVoidWithoutUpdatePermission();
		_testPostSignatureRequestVoidWithoutViewPermission();
	}

	@Override
	@Test
	public void testPostSiteSignatureRequest() throws Exception {
		super.testPostSiteSignatureRequest();

		_testPostSiteSignatureRequest();
		_testPostSiteSignatureRequestWithDuplicateRecipient();
		_testPostSiteSignatureRequestWithFileEntryDSRequest();
		_testPostSiteSignatureRequestWithNegativeExpireAfter();
		_testPostSiteSignatureRequestWithNullFileEntryId();
		_testPostSiteSignatureRequestWithSendNotificationsFalse();
		_testPostSiteSignatureRequestWithoutAddPermission();
		_testPostSiteSignatureRequestWithoutFileEntryIds();
		_testPostSiteSignatureRequestWithoutFileEntryUpdatePermission();
		_testPostSiteSignatureRequestWithoutRecipientUserId();
		_testPostSiteSignatureRequestWithoutRecipientUserViewPermission();
		_testPostSiteSignatureRequestWithoutRecipients();
		_testPostSiteSignatureRequestWithoutSendNotifications();
		_testPostSiteSignatureRequestWithoutSiteViewPermission();
	}

	@Ignore
	@Override
	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
	}

	@Override
	protected SignatureRequest testGetSignatureRequest_addSignatureRequest()
		throws Exception {

		return signatureRequestResource.postSiteSignatureRequest(
			testGroup.getGroupId(), _createSignatureRequest());
	}

	@Override
	protected SignatureRequest
			testGetSiteSignatureRequestsPage_addSignatureRequest(
				Long siteId, SignatureRequest signatureRequest)
		throws Exception {

		return signatureRequestResource.postSiteSignatureRequest(
			siteId, _createSignatureRequest());
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetSiteSignatureRequestsPage_getExpectedActions(Long siteId)
		throws Exception {

		return Collections.emptyMap();
	}

	@Override
	protected SignatureRequest
			testPostSignatureRequestNotification_addSignatureRequest(
				SignatureRequest signatureRequest)
		throws Exception {

		SignatureRequest postSignatureRequest =
			testGetSignatureRequest_addSignatureRequest();

		return signatureRequestResource.postSignatureRequestNotification(
			postSignatureRequest.getId());
	}

	@Override
	protected SignatureRequest testPostSignatureRequestVoid_addSignatureRequest(
			SignatureRequest signatureRequest)
		throws Exception {

		SignatureRequest postSignatureRequest =
			testGetSignatureRequest_addSignatureRequest();

		return signatureRequestResource.postSignatureRequestVoid(
			postSignatureRequest.getId(), RandomTestUtil.randomString());
	}

	@Override
	protected SignatureRequest testPostSiteSignatureRequest_addSignatureRequest(
			SignatureRequest signatureRequest)
		throws Exception {

		return testGetSignatureRequest_addSignatureRequest();
	}

	private ObjectEntry _addDSRequestObjectEntry(
			String emailAddress, String requestStatus)
		throws Exception {

		String languageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		long time = System.currentTimeMillis() / Time.SECOND * Time.SECOND;

		ObjectEntry dsRequestObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				_dsRequestObjectDefinition.getObjectDefinitionId(), 0,
				languageId,
				HashMapBuilder.<String, Serializable>put(
					"emailMessage", RandomTestUtil.randomString()
				).put(
					"emailSubject", RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", RandomTestUtil.randomString()
				).put(
					"requestExpirationDate", new Date(time + Time.DAY)
				).put(
					"requestStatus", requestStatus
				).put(
					"requestStatusDate", new Date(time - Time.HOUR)
				).put(
					"siteGroupId", testGroup.getGroupId()
				).build(),
				serviceContext);

		_objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_dsRequestDocumentObjectDefinition.getObjectDefinitionId(), 0,
			languageId,
			HashMapBuilder.<String, Serializable>put(
				"fileEntryId", RandomTestUtil.randomInt()
			).put(
				"r_dsRequestToDSRequestDocuments_l_dsRequestId",
				dsRequestObjectEntry.getObjectEntryId()
			).build(),
			serviceContext);

		_objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_dsRequestRecipientObjectDefinition.getObjectDefinitionId(), 0,
			languageId,
			HashMapBuilder.<String, Serializable>put(
				"emailAddress", emailAddress
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_dsRequestToDSRequestRecipients_l_dsRequestId",
				dsRequestObjectEntry.getObjectEntryId()
			).put(
				"r_userToDSRequestRecipients_userId",
				() -> {
					User user = _userLocalService.fetchUserByEmailAddress(
						testCompany.getCompanyId(), emailAddress);

					if (user == null) {
						return null;
					}

					return user.getUserId();
				}
			).put(
				"requestRecipientStatus", DSRequestConstants.STATUS_SENT
			).put(
				"requestRecipientStatusDate", new Date(time - Time.MINUTE)
			).put(
				"sentDate", new Date(time - Time.HOUR)
			).put(
				"signingOrder", 1
			).build(),
			serviceContext);

		return dsRequestObjectEntry;
	}

	private void _assertProblemException(
			String expectedStatus, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			Problem.ProblemException problemException = Assert.assertThrows(
				Problem.ProblemException.class, unsafeRunnable::run);

			Problem problem = problemException.getProblem();

			Assert.assertEquals(expectedStatus, problem.getStatus());
		}
	}

	private SignatureRequest _createSignatureRequest() throws Exception {
		SignatureRequest signatureRequest = randomSignatureRequest();

		signatureRequest.setExpireAfter(0);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".pdf",
			ContentTypes.APPLICATION_PDF,
			FileUtil.getBytes(getClass(), "dependencies/Document.pdf"), null,
			null, null,
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		signatureRequest.setFileEntryIds(
			new Long[] {fileEntry.getFileEntryId()});

		signatureRequest.setSendNotifications(false);

		User user = UserTestUtil.addUser();

		signatureRequest.setSignatureRequestRecipients(
			new SignatureRequestRecipient[] {
				new SignatureRequestRecipient() {
					{
						setUserId(user.getUserId());
					}
				}
			});

		return signatureRequest;
	}

	private List<NotificationQueueEntry> _getNotificationQueueEntries(
		long dsRequestId) {

		return ListUtil.filter(
			_notificationQueueEntryLocalService.getNotificationQueueEntries(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			notificationQueueEntry ->
				Objects.equals(
					notificationQueueEntry.getClassName(),
					DSRequest.class.getName()) &&
				(notificationQueueEntry.getClassPK() == dsRequestId));
	}

	private SignatureRequestResource _getSignatureRequestResource(User user)
		throws Exception {

		String password = RandomTestUtil.randomString();

		_userLocalService.updatePassword(
			user.getUserId(), password, password, false, true);

		return SignatureRequestResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private void _testGetSiteSignatureRequestsPageWithSearch()
		throws Exception {

		String search = RandomTestUtil.randomString();

		SignatureRequest signatureRequest = _createSignatureRequest();

		signatureRequest.setEmailSubject(
			search + " " + RandomTestUtil.randomString());

		SignatureRequest postSignatureRequest =
			signatureRequestResource.postSiteSignatureRequest(
				testGroup.getGroupId(), signatureRequest);

		signatureRequestResource.postSiteSignatureRequest(
			testGroup.getGroupId(), _createSignatureRequest());

		Page<SignatureRequest> signatureRequestsPage =
			signatureRequestResource.getSiteSignatureRequestsPage(
				testGroup.getGroupId(), search, Pagination.of(1, 10));

		Assert.assertEquals(1, signatureRequestsPage.getTotalCount());

		List<SignatureRequest> signatureRequests =
			(List<SignatureRequest>)signatureRequestsPage.getItems();

		SignatureRequest pageSignatureRequest = signatureRequests.get(0);

		Assert.assertEquals(
			postSignatureRequest.getId(), pageSignatureRequest.getId());

		signatureRequestsPage =
			signatureRequestResource.getSiteSignatureRequestsPage(
				irrelevantGroup.getGroupId(), search, Pagination.of(1, 10));

		Assert.assertEquals(0, signatureRequestsPage.getTotalCount());
	}

	private void _testGetSiteSignatureRequestsPageWithoutViewPermission()
		throws Exception {

		User user = UserTestUtil.addUser();

		SignatureRequestResource userSignatureRequestResource =
			_getSignatureRequestResource(user);

		Page<SignatureRequest> signatureRequestsPage =
			userSignatureRequestResource.getSiteSignatureRequestsPage(
				testGroup.getGroupId(), null, Pagination.of(1, 10));

		Assert.assertEquals(0, signatureRequestsPage.getTotalCount());
	}

	private void _testPostSignatureRequestNotification() throws Exception {
		User user = UserTestUtil.addUser();

		ObjectEntry dsRequestObjectEntry = _addDSRequestObjectEntry(
			user.getEmailAddress(), DSRequestConstants.STATUS_SENT);

		signatureRequestResource.postSignatureRequestNotification(
			dsRequestObjectEntry.getObjectEntryId());

		List<NotificationQueueEntry> notificationQueueEntries =
			_getNotificationQueueEntries(
				dsRequestObjectEntry.getObjectEntryId());

		Assert.assertEquals(
			notificationQueueEntries.toString(), 1,
			notificationQueueEntries.size());
	}

	private void _testPostSignatureRequestNotificationWithTerminalSignatureRequest()
		throws Exception {

		ObjectEntry dsRequestObjectEntry = _addDSRequestObjectEntry(
			RandomTestUtil.randomString() + "@liferay.com",
			DSRequestConstants.STATUS_COMPLETED);

		_assertProblemException(
			"BAD_REQUEST",
			() -> signatureRequestResource.postSignatureRequestNotification(
				dsRequestObjectEntry.getObjectEntryId()));
	}

	private void _testPostSignatureRequestNotificationWithoutUpdatePermission()
		throws Exception {

		User user = UserTestUtil.addUser();

		ObjectEntry dsRequestObjectEntry = _addDSRequestObjectEntry(
			user.getEmailAddress(), DSRequestConstants.STATUS_SENT);
		SignatureRequestResource userSignatureRequestResource =
			_getSignatureRequestResource(user);

		_assertProblemException(
			"FORBIDDEN",
			() -> userSignatureRequestResource.postSignatureRequestNotification(
				dsRequestObjectEntry.getObjectEntryId()));
	}

	private void _testPostSignatureRequestNotificationWithoutViewPermission()
		throws Exception {

		ObjectEntry dsRequestObjectEntry = _addDSRequestObjectEntry(
			RandomTestUtil.randomString() + "@liferay.com",
			DSRequestConstants.STATUS_SENT);

		User user = UserTestUtil.addUser();

		SignatureRequestResource userSignatureRequestResource =
			_getSignatureRequestResource(user);

		_assertProblemException(
			"NOT_FOUND",
			() -> userSignatureRequestResource.postSignatureRequestNotification(
				dsRequestObjectEntry.getObjectEntryId()));
	}

	private void _testPostSignatureRequestVoid() throws Exception {
		DSEnvelope dsEnvelope = new DSEnvelope();

		dsEnvelope.setDSRecipients(new ArrayList<>());
		dsEnvelope.setStatus(DSRequestConstants.STATUS_VOIDED);

		List<Object[]> argumentsList = new ArrayList<>();

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
						}

						return dsEnvelope;
					}));

		try {
			ObjectEntry dsRequestObjectEntry = _addDSRequestObjectEntry(
				RandomTestUtil.randomString() + "@liferay.com",
				DSRequestConstants.STATUS_SENT);
			String voidedReason = RandomTestUtil.randomString();

			SignatureRequest signatureRequest =
				signatureRequestResource.postSignatureRequestVoid(
					dsRequestObjectEntry.getObjectEntryId(), voidedReason);

			Assert.assertEquals(
				DSRequestConstants.STATUS_VOIDED, signatureRequest.getStatus());

			Assert.assertEquals(
				argumentsList.toString(), 1, argumentsList.size());

			Object[] arguments = argumentsList.get(0);

			Assert.assertEquals(
				dsRequestObjectEntry.getExternalReferenceCode(), arguments[1]);
			Assert.assertEquals(voidedReason, arguments[3]);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager", dsEnvelopeManager);
		}
	}

	private void _testPostSignatureRequestVoidWithBlankVoidedReason()
		throws Exception {

		ObjectEntry dsRequestObjectEntry = _addDSRequestObjectEntry(
			RandomTestUtil.randomString() + "@liferay.com",
			DSRequestConstants.STATUS_SENT);

		_assertProblemException(
			"BAD_REQUEST",
			() -> signatureRequestResource.postSignatureRequestVoid(
				dsRequestObjectEntry.getObjectEntryId(), StringPool.BLANK));
	}

	private void _testPostSignatureRequestVoidWithNonvoidableDSEnvelope()
		throws Exception {

		DSEnvelope dsEnvelope = new DSEnvelope();

		dsEnvelope.setDSRecipients(new ArrayList<>());
		dsEnvelope.setStatus(DSRequestConstants.STATUS_SENT);

		DSEnvelopeManager dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						if (Objects.equals(
								method.getName(), "voidDSEnvelope")) {

							throw new DSEnvelopeException.MustBeVoidable(
								RandomTestUtil.randomString());
						}

						return dsEnvelope;
					}));

		try {
			ObjectEntry dsRequestObjectEntry = _addDSRequestObjectEntry(
				RandomTestUtil.randomString() + "@liferay.com",
				DSRequestConstants.STATUS_SENT);

			_assertProblemException(
				"BAD_REQUEST",
				() -> signatureRequestResource.postSignatureRequestVoid(
					dsRequestObjectEntry.getObjectEntryId(),
					RandomTestUtil.randomString()));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager", dsEnvelopeManager);
		}
	}

	private void _testPostSignatureRequestVoidWithTerminalSignatureRequest()
		throws Exception {

		ObjectEntry dsRequestObjectEntry = _addDSRequestObjectEntry(
			RandomTestUtil.randomString() + "@liferay.com",
			DSRequestConstants.STATUS_COMPLETED);

		_assertProblemException(
			"BAD_REQUEST",
			() -> signatureRequestResource.postSignatureRequestVoid(
				dsRequestObjectEntry.getObjectEntryId(),
				RandomTestUtil.randomString()));
	}

	private void _testPostSignatureRequestVoidWithoutUpdatePermission()
		throws Exception {

		User user = UserTestUtil.addUser();

		ObjectEntry dsRequestObjectEntry = _addDSRequestObjectEntry(
			user.getEmailAddress(), DSRequestConstants.STATUS_SENT);
		SignatureRequestResource userSignatureRequestResource =
			_getSignatureRequestResource(user);

		_assertProblemException(
			"FORBIDDEN",
			() -> userSignatureRequestResource.postSignatureRequestVoid(
				dsRequestObjectEntry.getObjectEntryId(),
				RandomTestUtil.randomString()));
	}

	private void _testPostSignatureRequestVoidWithoutViewPermission()
		throws Exception {

		ObjectEntry dsRequestObjectEntry = _addDSRequestObjectEntry(
			RandomTestUtil.randomString() + "@liferay.com",
			DSRequestConstants.STATUS_SENT);

		User user = UserTestUtil.addUser();

		SignatureRequestResource userSignatureRequestResource =
			_getSignatureRequestResource(user);

		_assertProblemException(
			"NOT_FOUND",
			() -> userSignatureRequestResource.postSignatureRequestVoid(
				dsRequestObjectEntry.getObjectEntryId(),
				RandomTestUtil.randomString()));
	}

	private void _testPostSiteSignatureRequest() throws Exception {
		SignatureRequest signatureRequest = _createSignatureRequest();

		SignatureRequest postSignatureRequest =
			signatureRequestResource.postSiteSignatureRequest(
				testGroup.getGroupId(), signatureRequest);

		Long[] fileEntryIds = signatureRequest.getFileEntryIds();

		Map<Long, DSRequest> dsRequests =
			_dsRequestManager.getLatestFileEntryDSRequests(
				testCompany.getCompanyId(), new long[] {fileEntryIds[0]});

		DSRequest dsRequest = dsRequests.get(fileEntryIds[0]);

		Assert.assertEquals(
			Long.valueOf(dsRequest.getDSRequestId()),
			postSignatureRequest.getId());

		Assert.assertEquals(
			signatureRequest.getEmailMessage(),
			postSignatureRequest.getEmailMessage());
		Assert.assertEquals(
			signatureRequest.getEmailSubject(),
			postSignatureRequest.getEmailSubject());
		Assert.assertEquals(
			DSRequestConstants.STATUS_SENT, postSignatureRequest.getStatus());

		FileEntryBrief[] fileEntryBriefs =
			postSignatureRequest.getFileEntryBriefs();

		Assert.assertEquals(
			Arrays.toString(fileEntryBriefs), 1, fileEntryBriefs.length);

		FileEntryBrief fileEntryBrief = fileEntryBriefs[0];

		Assert.assertEquals(fileEntryIds[0], fileEntryBrief.getId());

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryIds[0]);

		Assert.assertEquals(fileEntry.getTitle(), fileEntryBrief.getTitle());

		DSEnvelope dsEnvelope = _dsEnvelopeAtomicReference.get();

		Assert.assertEquals(
			signatureRequest.getEmailMessage(), dsEnvelope.getEmailBlurb());
		Assert.assertEquals(
			signatureRequest.getEmailSubject(), dsEnvelope.getEmailSubject());
		Assert.assertEquals(signatureRequest.getName(), dsEnvelope.getName());

		List<DSRecipient> dsRecipients = dsEnvelope.getDSRecipients();

		DSRecipient dsRecipient = dsRecipients.get(0);

		JSONObject tabsJSONObject = dsRecipient.getTabsJSONObject();

		JSONArray signHereTabsJSONArray = tabsJSONObject.getJSONArray(
			"signHereTabs");

		JSONObject signHereTabJSONObject = signHereTabsJSONArray.getJSONObject(
			0);

		Assert.assertEquals(
			"/sig1/", signHereTabJSONObject.getString("anchorString"));

		SignatureRequestRecipient[] postSignatureRequestRecipients =
			postSignatureRequest.getSignatureRequestRecipients();

		Assert.assertEquals(
			Arrays.toString(postSignatureRequestRecipients), 1,
			postSignatureRequestRecipients.length);

		SignatureRequestRecipient[] signatureRequestRecipients =
			signatureRequest.getSignatureRequestRecipients();

		SignatureRequestRecipient signatureRequestRecipient =
			signatureRequestRecipients[0];

		User user = _userLocalService.getUser(
			signatureRequestRecipient.getUserId());

		SignatureRequestRecipient postSignatureRequestRecipient =
			postSignatureRequestRecipients[0];

		Assert.assertEquals(
			user.getEmailAddress(),
			postSignatureRequestRecipient.getEmailAddress());
		Assert.assertEquals(
			user.getFullName(), postSignatureRequestRecipient.getName());
	}

	private void _testPostSiteSignatureRequestWithDuplicateRecipient()
		throws Exception {

		SignatureRequest signatureRequest = _createSignatureRequest();

		SignatureRequestRecipient[] signatureRequestRecipients =
			signatureRequest.getSignatureRequestRecipients();

		signatureRequest.setSignatureRequestRecipients(
			new SignatureRequestRecipient[] {
				signatureRequestRecipients[0], signatureRequestRecipients[0]
			});

		_assertProblemException(
			"BAD_REQUEST",
			() -> signatureRequestResource.postSiteSignatureRequest(
				testGroup.getGroupId(), signatureRequest));
	}

	private void _testPostSiteSignatureRequestWithFileEntryDSRequest()
		throws Exception {

		SignatureRequest signatureRequest = _createSignatureRequest();

		signatureRequestResource.postSiteSignatureRequest(
			testGroup.getGroupId(), signatureRequest);

		_assertProblemException(
			"BAD_REQUEST",
			() -> signatureRequestResource.postSiteSignatureRequest(
				testGroup.getGroupId(), signatureRequest));
	}

	private void _testPostSiteSignatureRequestWithNegativeExpireAfter()
		throws Exception {

		SignatureRequest signatureRequest = _createSignatureRequest();

		signatureRequest.setExpireAfter(-1);

		_assertProblemException(
			"BAD_REQUEST",
			() -> signatureRequestResource.postSiteSignatureRequest(
				testGroup.getGroupId(), signatureRequest));
	}

	private void _testPostSiteSignatureRequestWithNullFileEntryId()
		throws Exception {

		SignatureRequest signatureRequest = _createSignatureRequest();

		signatureRequest.setFileEntryIds(new Long[] {null});

		_assertProblemException(
			"BAD_REQUEST",
			() -> signatureRequestResource.postSiteSignatureRequest(
				testGroup.getGroupId(), signatureRequest));
	}

	private void _testPostSiteSignatureRequestWithSendNotificationsFalse()
		throws Exception {

		SignatureRequest signatureRequest = _createSignatureRequest();

		signatureRequest.setSendNotifications(false);

		SignatureRequest postSignatureRequest =
			signatureRequestResource.postSiteSignatureRequest(
				testGroup.getGroupId(), signatureRequest);

		List<NotificationQueueEntry> notificationQueueEntries =
			_getNotificationQueueEntries(postSignatureRequest.getId());

		Assert.assertEquals(
			notificationQueueEntries.toString(), 0,
			notificationQueueEntries.size());
	}

	private void _testPostSiteSignatureRequestWithoutAddPermission()
		throws Exception {

		User user = UserTestUtil.addUser(testGroup.getGroupId());

		SignatureRequestResource userSignatureRequestResource =
			_getSignatureRequestResource(user);

		_assertProblemException(
			"FORBIDDEN",
			() -> userSignatureRequestResource.postSiteSignatureRequest(
				testGroup.getGroupId(), randomSignatureRequest()));
	}

	private void _testPostSiteSignatureRequestWithoutFileEntryIds()
		throws Exception {

		SignatureRequest signatureRequest = _createSignatureRequest();

		signatureRequest.setFileEntryIds((Long[])null);

		_assertProblemException(
			"BAD_REQUEST",
			() -> signatureRequestResource.postSiteSignatureRequest(
				testGroup.getGroupId(), signatureRequest));
	}

	private void _testPostSiteSignatureRequestWithoutFileEntryUpdatePermission()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			role, _dsRequestObjectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(testCompany.getCompanyId()),
			ObjectActionKeys.ADD_OBJECT_ENTRY);

		User user = UserTestUtil.addUser(testGroup.getGroupId());

		_userLocalService.addRoleUser(role.getRoleId(), user);

		SignatureRequest signatureRequest = randomSignatureRequest();

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".pdf",
			ContentTypes.APPLICATION_PDF,
			FileUtil.getBytes(getClass(), "dependencies/Document.pdf"), null,
			null, null,
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		signatureRequest.setFileEntryIds(
			new Long[] {fileEntry.getFileEntryId()});

		SignatureRequestResource userSignatureRequestResource =
			_getSignatureRequestResource(user);

		_assertProblemException(
			"FORBIDDEN",
			() -> userSignatureRequestResource.postSiteSignatureRequest(
				testGroup.getGroupId(), signatureRequest));
	}

	private void _testPostSiteSignatureRequestWithoutRecipientUserId()
		throws Exception {

		SignatureRequest signatureRequest = _createSignatureRequest();

		signatureRequest.setSignatureRequestRecipients(
			new SignatureRequestRecipient[] {new SignatureRequestRecipient()});

		_assertProblemException(
			"BAD_REQUEST",
			() -> signatureRequestResource.postSiteSignatureRequest(
				testGroup.getGroupId(), signatureRequest));
	}

	private void _testPostSiteSignatureRequestWithoutRecipientUserViewPermission()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			role, _dsRequestObjectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(testCompany.getCompanyId()),
			ObjectActionKeys.ADD_OBJECT_ENTRY);
		RoleTestUtil.addResourcePermission(
			role, DLFileEntry.class.getName(), ResourceConstants.SCOPE_GROUP,
			String.valueOf(testGroup.getGroupId()), ActionKeys.UPDATE);

		User user = UserTestUtil.addUser(testGroup.getGroupId());

		_userLocalService.addRoleUser(role.getRoleId(), user);

		SignatureRequest signatureRequest = _createSignatureRequest();

		SignatureRequestResource userSignatureRequestResource =
			_getSignatureRequestResource(user);

		_assertProblemException(
			"FORBIDDEN",
			() -> userSignatureRequestResource.postSiteSignatureRequest(
				testGroup.getGroupId(), signatureRequest));
	}

	private void _testPostSiteSignatureRequestWithoutRecipients()
		throws Exception {

		SignatureRequest signatureRequest = _createSignatureRequest();

		signatureRequest.setSignatureRequestRecipients(
			new SignatureRequestRecipient[0]);

		_assertProblemException(
			"BAD_REQUEST",
			() -> signatureRequestResource.postSiteSignatureRequest(
				testGroup.getGroupId(), signatureRequest));
	}

	private void _testPostSiteSignatureRequestWithoutSendNotifications()
		throws Exception {

		SignatureRequest signatureRequest = _createSignatureRequest();

		signatureRequest.setSendNotifications((Boolean)null);

		SignatureRequest postSignatureRequest =
			signatureRequestResource.postSiteSignatureRequest(
				testGroup.getGroupId(), signatureRequest);

		List<NotificationQueueEntry> notificationQueueEntries =
			_getNotificationQueueEntries(postSignatureRequest.getId());

		Assert.assertEquals(
			notificationQueueEntries.toString(), 1,
			notificationQueueEntries.size());
	}

	private void _testPostSiteSignatureRequestWithoutSiteViewPermission()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			role, _dsRequestObjectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(testCompany.getCompanyId()),
			ObjectActionKeys.ADD_OBJECT_ENTRY);
		RoleTestUtil.addResourcePermission(
			role, DLFileEntry.class.getName(), ResourceConstants.SCOPE_GROUP,
			String.valueOf(testGroup.getGroupId()), ActionKeys.UPDATE);
		RoleTestUtil.addResourcePermission(
			role, User.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(testCompany.getCompanyId()), ActionKeys.VIEW);

		User user = UserTestUtil.addUser(testGroup.getGroupId());

		_userLocalService.addRoleUser(role.getRoleId(), user);

		Group group = GroupTestUtil.addGroupWithType(
			GroupConstants.TYPE_SITE_PRIVATE);
		SignatureRequest signatureRequest = _createSignatureRequest();
		SignatureRequestResource userSignatureRequestResource =
			_getSignatureRequestResource(user);

		_assertProblemException(
			"FORBIDDEN",
			() -> userSignatureRequestResource.postSiteSignatureRequest(
				group.getGroupId(), signatureRequest));
	}

	private CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private final AtomicReference<DSEnvelope> _dsEnvelopeAtomicReference =
		new AtomicReference<>();
	private DSEnvelopeManager _dsEnvelopeManager;
	private ObjectDefinition _dsRequestDocumentObjectDefinition;

	@Inject
	private DSRequestManager _dsRequestManager;

	private ObjectDefinition _dsRequestObjectDefinition;
	private ObjectDefinition _dsRequestRecipientObjectDefinition;

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