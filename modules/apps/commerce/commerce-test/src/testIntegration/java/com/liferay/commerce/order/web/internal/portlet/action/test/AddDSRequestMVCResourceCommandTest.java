/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action.test;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.commerce.test.util.DSCommerceOrderAttachmentTestUtil;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.constants.DSRequestConstants;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.service.NotificationQueueEntryLocalService;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

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
public class AddDSRequestMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CommerceOrderAttachmentTestUtil.initialize(getClass());
		DSCommerceOrderAttachmentTestUtil.initialize(getClass());

		_saveDigitalSignatureConfiguration(true);

		_group = GroupTestUtil.addGroup();

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addPersonAccountEntry(
				TestPropsValues.getUserId(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(_group.getCompanyId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), commerceCurrency.getCode());

		_commerceOrder = _commerceOrderLocalService.addCommerceOrder(
			TestPropsValues.getUserId(), commerceChannel.getGroupId(),
			accountEntry.getAccountEntryId(), commerceCurrency.getCode(), 0);

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
	public void testServeResource() throws Exception {
		PortletException portletException = Assert.assertThrows(
			PortletException.class,
			() -> _serveResource(
				_getMockLiferayResourceRequest(
					_commerceOrderAttachment.getCommerceOrderAttachmentId(), 0,
					UserTestUtil.addUser())));

		Assert.assertTrue(
			portletException.getCause() instanceof
				PrincipalException.MustHavePermission);

		_saveDigitalSignatureConfiguration(false);

		_testServeResourceWithErrorMessage(
			"Digital signature is not enabled for this order.");

		_saveDigitalSignatureConfiguration(true);

		portletException = Assert.assertThrows(
			PortletException.class,
			() -> _serveResource(
				_getMockLiferayResourceRequest(
					RandomTestUtil.randomLong(), 0, TestPropsValues.getUser(),
					TestPropsValues.getUserId())));

		Assert.assertTrue(
			portletException.getCause() instanceof PrincipalException);

		User user1 = UserTestUtil.addUser();

		_userLocalService.updateStatus(
			user1.getUserId(), WorkflowConstants.STATUS_INACTIVE,
			ServiceContextTestUtil.getServiceContext());

		_testServeResourceWithErrorMessage(
			"One or more recipients cannot sign this order.",
			user1.getUserId());

		User user2 = UserTestUtil.addUser();
		User user3 = UserTestUtil.addUser();

		_testServeResourceWithErrorMessage(
			"Only one internal countersigner is allowed.", user2.getUserId(),
			user3.getUserId());

		_testServeResourceWithErrorMessage(
			"At least one recipient is required.");

		Role role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_REGULAR, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), CommerceOrderAttachment.class.getName(),
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(_commerceOrder.getGroupId()), role.getRoleId(),
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

		AccountEntry accountEntry = _commerceOrder.getAccountEntry();

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(accountEntry.getAccountEntryGroupId()),
			role.getRoleId(),
			new String[] {CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS});
		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), AccountEntry.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_group.getCompanyId()), role.getRoleId(),
			new String[] {AccountActionKeys.VIEW_USERS});

		User orderManagerUser = UserTestUtil.addUser();

		_roleLocalService.addUserRole(orderManagerUser.getUserId(), role);

		User countersignerUser = UserTestUtil.addUser();

		portletException = Assert.assertThrows(
			PortletException.class,
			() -> _serveResource(
				_getMockLiferayResourceRequest(
					_commerceOrderAttachment.getCommerceOrderAttachmentId(), 0,
					orderManagerUser, countersignerUser.getUserId())));

		PrincipalException.MustHavePermission mustHavePermission =
			(PrincipalException.MustHavePermission)portletException.getCause();

		Assert.assertEquals(
			countersignerUser.getUserId(), mustHavePermission.resourceId);
		Assert.assertEquals(
			User.class.getName(), mustHavePermission.resourceName);

		_accountEntryUserRelLocalService.
			deleteAccountEntryUserRelsByAccountEntryId(
				accountEntry.getAccountEntryId());

		User accountUser = UserTestUtil.addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), accountUser.getUserId());

		int expireAfter = RandomTestUtil.randomInt(2, 120);

		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayResourceRequest(
				_commerceOrderAttachment.getCommerceOrderAttachmentId(),
				expireAfter, orderManagerUser, accountUser.getUserId(),
				TestPropsValues.getUserId());

		String emailMessage = RandomTestUtil.randomString();

		mockLiferayResourceRequest.addParameter("emailMessage", emailMessage);

		String emailSubject = RandomTestUtil.randomString();

		mockLiferayResourceRequest.addParameter("emailSubject", emailSubject);

		DSEnvelope dsEnvelope = _getDSEnvelope(mockLiferayResourceRequest);

		Assert.assertEquals(emailMessage, dsEnvelope.getEmailBlurb());
		Assert.assertEquals(emailSubject, dsEnvelope.getEmailSubject());
		Assert.assertEquals(expireAfter, dsEnvelope.getExpireAfter());
		Assert.assertEquals(
			_commerceOrderAttachment.getTitle(), dsEnvelope.getName());

		List<DSRecipient> dsRecipients = dsEnvelope.getDSRecipients();

		Assert.assertEquals(dsRecipients.toString(), 2, dsRecipients.size());

		_assertDSRecipient(dsRecipients.get(0), 1, accountUser);
		_assertDSRecipient(dsRecipients.get(1), 2, TestPropsValues.getUser());

		DSRequest dsRequest = _dsRequestManager.fetchLatestFileEntryDSRequest(
			_group.getCompanyId(), _commerceOrderAttachment.getFileEntryId());

		Assert.assertEquals(
			dsEnvelope.getDSEnvelopeId(), dsRequest.getExternalReferenceCode());
		Assert.assertEquals(
			DSRequestConstants.STATUS_SENT, dsRequest.getStatus());

		List<NotificationQueueEntry> notificationQueueEntries =
			_getNotificationQueueEntries(dsRequest);

		Assert.assertEquals(
			notificationQueueEntries.toString(), 2,
			notificationQueueEntries.size());

		dsEnvelope.setStatus(DSRequestConstants.STATUS_SENT);

		DSEnvelopeManager dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						if (!Objects.equals(
								method.getName(), "addDSEnvelope")) {

							return dsEnvelope;
						}

						DSEnvelope addedDSEnvelope = (DSEnvelope)arguments[2];

						addedDSEnvelope.setDSEnvelopeId(
							RandomTestUtil.randomString());

						for (DSRecipient addedDSRecipient :
								addedDSEnvelope.getDSRecipients()) {

							addedDSRecipient.setStatus(
								DSRequestConstants.STATUS_SENT);
						}

						return addedDSEnvelope;
					}));

		try {
			_testServeResourceWithErrorMessage(
				"This document already has an active or completed signature " +
					"request.",
				TestPropsValues.getUserId());

			dsEnvelope.setStatus(DSRequestConstants.STATUS_VOIDED);

			JSONObject jsonObject = _serveResource(
				_getMockLiferayResourceRequest(
					_commerceOrderAttachment.getCommerceOrderAttachmentId(), 0,
					TestPropsValues.getUser(), TestPropsValues.getUserId()));

			Assert.assertEquals("{}", jsonObject.toString());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager", dsEnvelopeManager);
		}

		dsRequest = _dsRequestManager.fetchLatestFileEntryDSRequest(
			_group.getCompanyId(), _commerceOrderAttachment.getFileEntryId());

		Assert.assertNotEquals(
			dsEnvelope.getDSEnvelopeId(), dsRequest.getExternalReferenceCode());

		_commerceOrderAttachment =
			_commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_commerceOrder.getCommerceOrderId(),
				RandomTestUtil.nextDouble(), false,
				RandomTestUtil.randomString(), "invoice",
				RandomTestUtil.randomString() + ".zip",
				new ByteArrayInputStream(RandomTestUtil.randomBytes()));

		_testServeResourceWithErrorMessage(
			"This file extension is not supported by DocuSign.",
			TestPropsValues.getUserId());

		_testServeResourceWithFailedResponseWrite();
		_testServeResourceWithSequentialFalse();
	}

	private void _assertDSRecipient(
		DSRecipient dsRecipient, int routingOrder, User user) {

		Assert.assertEquals(
			user.getEmailAddress(), dsRecipient.getEmailAddress());
		Assert.assertEquals(user.getFullName(), dsRecipient.getName());
		Assert.assertEquals(routingOrder, dsRecipient.getRoutingOrder());
	}

	private DSEnvelope _getDSEnvelope(
			MockLiferayResourceRequest mockLiferayResourceRequest)
		throws Exception {

		AtomicReference<DSEnvelope> dsEnvelopeAtomicReference =
			new AtomicReference<>();

		DSEnvelopeManager dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
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

						dsEnvelopeAtomicReference.set(dsEnvelope);

						return dsEnvelope;
					}));

		try {
			JSONObject jsonObject = _serveResource(mockLiferayResourceRequest);

			Assert.assertEquals("{}", jsonObject.toString());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager", dsEnvelopeManager);
		}

		return dsEnvelopeAtomicReference.get();
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest(
			long commerceOrderAttachmentId, int expireAfter, User user,
			long... recipientUserIds)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setUser(user);

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayResourceRequest.addParameter(
			"commerceOrderAttachmentId",
			String.valueOf(commerceOrderAttachmentId));
		mockLiferayResourceRequest.addParameter(
			"expireAfter", String.valueOf(expireAfter));

		for (long recipientUserId : recipientUserIds) {
			mockLiferayResourceRequest.addParameter(
				"recipientUserIds", String.valueOf(recipientUserId));
		}

		return mockLiferayResourceRequest;
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

	private void _saveDigitalSignatureConfiguration(boolean enabled)
		throws Exception {

		if (_companyConfigurationTemporarySwapper != null) {
			_companyConfigurationTemporarySwapper.close();
		}

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
					"enabled", enabled
				).put(
					"environment", "sandbox"
				).put(
					"integrationKey", RandomTestUtil.randomString()
				).put(
					"rsaPrivateKey", RandomTestUtil.randomString()
				).put(
					"siteSettingsStrategy", "always-inherit"
				).build());
	}

	private JSONObject _serveResource(
			MockLiferayResourceRequest mockLiferayResourceRequest)
		throws Exception {

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		return _jsonFactory.createJSONObject(byteArrayOutputStream.toString());
	}

	private void _testServeResourceWithErrorMessage(
			String expectedMessage, long... recipientUserIds)
		throws Exception {

		JSONObject jsonObject = _serveResource(
			_getMockLiferayResourceRequest(
				_commerceOrderAttachment.getCommerceOrderAttachmentId(), 0,
				TestPropsValues.getUser(), recipientUserIds));

		Assert.assertEquals(
			expectedMessage, jsonObject.getString("errorMessage"));
	}

	private void _testServeResourceWithFailedResponseWrite() throws Exception {
		CommerceOrderAttachment commerceOrderAttachment =
			_commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_commerceOrder.getCommerceOrderId(),
				RandomTestUtil.nextDouble(), false,
				RandomTestUtil.randomString(), "invoice",
				RandomTestUtil.randomString() + ".pdf",
				new ByteArrayInputStream(RandomTestUtil.randomBytes()));

		AtomicReference<String> dsEnvelopeIdAtomicReference =
			new AtomicReference<>();
		AtomicReference<String> voidedDSEnvelopeIdAtomicReference =
			new AtomicReference<>();

		DSEnvelopeManager proxyDSEnvelopeManager =
			(DSEnvelopeManager)ProxyUtil.newProxyInstance(
				DSEnvelopeManager.class.getClassLoader(),
				new Class<?>[] {DSEnvelopeManager.class},
				(proxy, method, arguments) -> {
					if (Objects.equals(method.getName(), "addDSEnvelope")) {
						DSEnvelope dsEnvelope = (DSEnvelope)arguments[2];

						dsEnvelope.setDSEnvelopeId(
							RandomTestUtil.randomString());

						for (DSRecipient dsRecipient :
								dsEnvelope.getDSRecipients()) {

							dsRecipient.setStatus(
								DSRequestConstants.STATUS_SENT);
						}

						dsEnvelopeIdAtomicReference.set(
							dsEnvelope.getDSEnvelopeId());

						return dsEnvelope;
					}

					if (Objects.equals(method.getName(), "voidDSEnvelope")) {
						voidedDSEnvelopeIdAtomicReference.set(
							(String)arguments[1]);
					}

					return null;
				});

		DSEnvelopeManager dsEnvelopeManager1 =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				proxyDSEnvelopeManager);
		DSEnvelopeManager dsEnvelopeManager2 =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_mvcResourceCommand, "_dsEnvelopeManager",
				proxyDSEnvelopeManager);

		try {
			Assert.assertThrows(
				PortletException.class,
				() -> _mvcResourceCommand.serveResource(
					_getMockLiferayResourceRequest(
						commerceOrderAttachment.getCommerceOrderAttachmentId(),
						0, TestPropsValues.getUser(),
						TestPropsValues.getUserId()),
					new MockLiferayResourceResponse() {

						@Override
						public OutputStream getPortletOutputStream()
							throws IOException {

							throw new IOException();
						}

					}));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager", dsEnvelopeManager1);
			ReflectionTestUtil.setFieldValue(
				_mvcResourceCommand, "_dsEnvelopeManager", dsEnvelopeManager2);
		}

		Assert.assertNull(
			_dsRequestManager.fetchLatestFileEntryDSRequest(
				_group.getCompanyId(),
				commerceOrderAttachment.getFileEntryId()));
		Assert.assertNotNull(dsEnvelopeIdAtomicReference.get());
		Assert.assertEquals(
			dsEnvelopeIdAtomicReference.get(),
			voidedDSEnvelopeIdAtomicReference.get());
	}

	private void _testServeResourceWithSequentialFalse() throws Exception {
		CommerceOrderAttachment commerceOrderAttachment =
			_commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_commerceOrder.getCommerceOrderId(),
				RandomTestUtil.nextDouble(), false,
				RandomTestUtil.randomString(), "invoice",
				RandomTestUtil.randomString() + ".pdf",
				new ByteArrayInputStream(RandomTestUtil.randomBytes()));

		User user = UserTestUtil.addUser();

		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayResourceRequest(
				commerceOrderAttachment.getCommerceOrderAttachmentId(), 0,
				TestPropsValues.getUser(), TestPropsValues.getUserId(),
				user.getUserId());

		mockLiferayResourceRequest.addParameter("sequential", "false");

		DSEnvelope dsEnvelope = _getDSEnvelope(mockLiferayResourceRequest);

		List<DSRecipient> dsRecipients = dsEnvelope.getDSRecipients();

		Assert.assertEquals(dsRecipients.toString(), 2, dsRecipients.size());

		_assertDSRecipient(dsRecipients.get(0), 1, TestPropsValues.getUser());
		_assertDSRecipient(dsRecipients.get(1), 1, user);
	}

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

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
	private CompanyLocalService _companyLocalService;

	@Inject
	private DSRequestManager _dsRequestManager;

	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject(filter = "mvc.command.name=/commerce_order/add_ds_request")
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private NotificationQueueEntryLocalService
		_notificationQueueEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}