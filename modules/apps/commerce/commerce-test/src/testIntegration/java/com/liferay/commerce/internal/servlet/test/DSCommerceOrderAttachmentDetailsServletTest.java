/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.servlet.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderAttachmentLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceOrderAttachmentTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.constants.DSRequestConstants;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Brian I. Kim
 */
@FeatureFlag("LPD-69290")
@RunWith(Arquillian.class)
public class DSCommerceOrderAttachmentDetailsServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.digital.signature.impl",
			DSCommerceOrderAttachmentDetailsServletTest.class,
			new String[] {
				".com.liferay.digital.signature.internal.batch.01.object." +
					"definition"
			});

		CommerceOrderAttachmentTestUtil.initialize(getClass());

		_companyConfigurationTemporarySwapper =
			new CompanyConfigurationTemporarySwapper(
				TestPropsValues.getCompanyId(),
				DigitalSignatureConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"accountBaseURI", "https://demo.docusign.net/restapi"
				).put(
					"apiAccountId", RandomTestUtil.randomString()
				).put(
					"apiUsername", RandomTestUtil.randomString()
				).put(
					"embeddedViewEnabled", true
				).put(
					"enabled", true
				).put(
					"environment", "sandbox"
				).put(
					"integrationKey", RandomTestUtil.randomString()
				).put(
					"rsaPrivateKey", RandomTestUtil.randomString()
				).put(
					"siteSettingsStrategy", "always-inherit"
				).build());

		_group = GroupTestUtil.addGroup();

		_accountEntry = CommerceAccountTestUtil.addPersonAccountEntry(
			TestPropsValues.getUserId(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(_group.getCompanyId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), commerceCurrency.getCode());

		_commerceOrder = _commerceOrderLocalService.addCommerceOrder(
			TestPropsValues.getUserId(), commerceChannel.getGroupId(),
			_accountEntry.getAccountEntryId(), commerceCurrency.getCode(), 0);

		_commerceOrderAttachment =
			_commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_commerceOrder.getCommerceOrderId(),
				RandomTestUtil.nextDouble(), false,
				RandomTestUtil.randomString(), "invoice",
				RandomTestUtil.randomString() + ".pdf",
				new ByteArrayInputStream(RandomTestUtil.randomBytes()));
	}

	@After
	public void tearDown() throws Exception {
		_companyConfigurationTemporarySwapper.close();
	}

	@Test
	public void testDoGet() throws Exception {
		_assertNotFound(
			_commerceOrderAttachment.getCommerceOrderAttachmentId(), null);

		User user = TestPropsValues.getUser();

		_assertNotFound(RandomTestUtil.randomLong(), user);

		Assert.assertEquals(
			"{}",
			_getContentAsString(
				_commerceOrderAttachment.getCommerceOrderAttachmentId(), user));

		DSRequest voidedDSRequest = _addDSRequest(
			DSRequestConstants.STATUS_VOIDED,
			_createDSRecipient(DSRequestConstants.STATUS_SENT, user));
		DSRequest sentDSRequest = _addDSRequest(
			DSRequestConstants.STATUS_SENT,
			_createDSRecipient(DSRequestConstants.STATUS_SENT, user));

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			_getContentAsString(
				_commerceOrderAttachment.getCommerceOrderAttachmentId(), user));

		Assert.assertEquals(
			sentDSRequest.getExternalReferenceCode(),
			jsonObject.getString("externalReferenceCode"));
		Assert.assertEquals(
			DSRequestConstants.STATUS_SENT, jsonObject.getString("status"));
		Assert.assertEquals(
			user.getFullName(), jsonObject.getString("userName"));

		JSONArray historyJSONArray = jsonObject.getJSONArray("history");

		Assert.assertEquals(
			historyJSONArray.toString(), 1, historyJSONArray.length());

		JSONObject historyJSONObject = historyJSONArray.getJSONObject(0);

		Assert.assertEquals(
			voidedDSRequest.getExternalReferenceCode(),
			historyJSONObject.getString("externalReferenceCode"));
		Assert.assertEquals(
			DSRequestConstants.STATUS_VOIDED,
			historyJSONObject.getString("status"));

		JSONArray recipientsJSONArray = jsonObject.getJSONArray("recipients");

		Assert.assertEquals(
			recipientsJSONArray.toString(), 1, recipientsJSONArray.length());

		JSONObject recipientJSONObject = recipientsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			user.getEmailAddress(),
			recipientJSONObject.getString("emailAddress"));
		Assert.assertEquals(
			DSRequestConstants.STATUS_SENT,
			recipientJSONObject.getString("status"));

		_assertNotFound(
			_commerceOrderAttachment.getCommerceOrderAttachmentId(),
			UserTestUtil.addUser());

		_testDoGetWithExpiredDSRequest();
		_testDoGetWithOrderManagerUser();
	}

	private CommerceOrderAttachment _addCommerceOrderAttachment(
			boolean restricted)
		throws Exception {

		return _commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_commerceOrder.getCommerceOrderId(), RandomTestUtil.nextDouble(),
			restricted, RandomTestUtil.randomString(), "invoice",
			RandomTestUtil.randomString() + ".pdf",
			new ByteArrayInputStream(RandomTestUtil.randomBytes()));
	}

	private DSRequest _addDSRequest(String status, DSRecipient... dsRecipients)
		throws Exception {

		DSEnvelope dsEnvelope = new DSEnvelope();

		dsEnvelope.setDSEnvelopeId(RandomTestUtil.randomString());
		dsEnvelope.setDSRecipients(ListUtil.fromArray(dsRecipients));

		dsEnvelope.setEmailSubject(RandomTestUtil.randomString());
		dsEnvelope.setStatus(status);

		return ReflectionTestUtil.invoke(
			_dsRequestManager, "_addDSRequest",
			new Class<?>[] {
				long.class, DSEnvelope.class, long[].class, long.class,
				long.class
			},
			_group.getCompanyId(), dsEnvelope,
			new long[] {_commerceOrderAttachment.getFileEntryId()},
			_group.getGroupId(), TestPropsValues.getUserId());
	}

	private void _assertNotFound(long commerceOrderAttachmentId, User user)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.internal.servlet." +
					"DSCommerceOrderAttachmentDetailsServlet",
				LoggerTestUtil.OFF)) {

			MockHttpServletResponse mockHttpServletResponse =
				_getMockHttpServletResponse(commerceOrderAttachmentId, user);

			Assert.assertEquals(
				HttpServletResponse.SC_NOT_FOUND,
				mockHttpServletResponse.getStatus());
		}
	}

	private DSRecipient _createDSRecipient(String status, User user) {
		DSRecipient dsRecipient = new DSRecipient();

		dsRecipient.setDSRecipientId(RandomTestUtil.randomString());
		dsRecipient.setEmailAddress(user.getEmailAddress());
		dsRecipient.setName(user.getFullName());
		dsRecipient.setRoutingOrder(1);
		dsRecipient.setStatus(status);

		return dsRecipient;
	}

	private String _getContentAsString(
			long commerceOrderAttachmentId, User user)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			_getMockHttpServletResponse(commerceOrderAttachmentId, user);

		return mockHttpServletResponse.getContentAsString();
	}

	private MockHttpServletResponse _getMockHttpServletResponse(
			long commerceOrderAttachmentId, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, _group.getCompanyId());

		if (user != null) {
			mockHttpServletRequest.setAttribute(WebKeys.USER, user);
		}

		mockHttpServletRequest.setMethod(HttpMethods.GET);
		mockHttpServletRequest.setParameter(
			"commerceOrderAttachmentId",
			String.valueOf(commerceOrderAttachmentId));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private void _setResourcePermissions(Role role, String... actionIds)
		throws Exception {

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceOrder.getCompanyId(), CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(_accountEntry.getAccountEntryGroupId()),
			role.getRoleId(), actionIds);
	}

	private void _testDoGetWithExpiredDSRequest() throws Exception {
		User user1 = TestPropsValues.getUser();
		User user2 = UserTestUtil.addUser();

		DSRequest dsRequest = _addDSRequest(
			DSRequestConstants.STATUS_SENT,
			_createDSRecipient(DSRequestConstants.STATUS_SENT, user1),
			_createDSRecipient(DSRequestConstants.STATUS_COMPLETED, user2));

		_objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), dsRequest.getDSRequestId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			HashMapBuilder.<String, Serializable>put(
				"requestStatus", DSRequestConstants.STATUS_EXPIRED
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			_getContentAsString(
				_commerceOrderAttachment.getCommerceOrderAttachmentId(),
				user1));

		Assert.assertEquals(
			DSRequestConstants.STATUS_EXPIRED, jsonObject.getString("status"));

		Map<String, String> recipientStatuses = new HashMap<>();

		JSONArray recipientsJSONArray = jsonObject.getJSONArray("recipients");

		for (int i = 0; i < recipientsJSONArray.length(); i++) {
			JSONObject recipientJSONObject = recipientsJSONArray.getJSONObject(
				i);

			recipientStatuses.put(
				recipientJSONObject.getString("emailAddress"),
				recipientJSONObject.getString("status"));
		}

		Assert.assertEquals(
			HashMapBuilder.put(
				user1.getEmailAddress(), DSRequestConstants.STATUS_EXPIRED
			).put(
				user2.getEmailAddress(), DSRequestConstants.STATUS_COMPLETED
			).build(),
			recipientStatuses);
	}

	private void _testDoGetWithOrderManagerUser() throws Exception {
		User user = UserTestUtil.addUser();

		Role role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_REGULAR, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_roleLocalService.addUserRole(user.getUserId(), role);

		_setResourcePermissions(
			role, CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS);

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceOrder.getCompanyId(),
			CommerceOrderAttachment.class.getName(),
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(_commerceOrder.getGroupId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		CommerceOrderAttachment commerceOrderAttachment =
			_addCommerceOrderAttachment(false);

		Assert.assertEquals(
			"{}",
			_getContentAsString(
				commerceOrderAttachment.getCommerceOrderAttachmentId(), user));

		CommerceOrderAttachment restrictedCommerceOrderAttachment =
			_addCommerceOrderAttachment(true);

		_assertNotFound(
			restrictedCommerceOrderAttachment.getCommerceOrderAttachmentId(),
			user);

		_setResourcePermissions(
			role, CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS,
			CommerceOrderActionKeys.VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS);

		Assert.assertEquals(
			"{}",
			_getContentAsString(
				restrictedCommerceOrderAttachment.
					getCommerceOrderAttachmentId(),
				user));
	}

	private AccountEntry _accountEntry;
	private CommerceOrder _commerceOrder;
	private CommerceOrderAttachment _commerceOrderAttachment;

	@Inject
	private CommerceOrderAttachmentLocalService
		_commerceOrderAttachmentLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;

	@Inject
	private DSRequestManager _dsRequestManager;

	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.commerce.internal.servlet.DSCommerceOrderAttachmentDetailsServlet"
	)
	private Servlet _servlet;

}