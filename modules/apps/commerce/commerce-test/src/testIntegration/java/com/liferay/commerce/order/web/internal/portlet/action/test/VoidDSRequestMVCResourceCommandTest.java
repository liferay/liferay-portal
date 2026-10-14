/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
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
import com.liferay.digital.signature.exception.DSEnvelopeException;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
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
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.Collections;
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
public class VoidDSRequestMVCResourceCommandTest {

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
			VoidDSRequestMVCResourceCommandTest.class,
			new String[] {
				".com.liferay.digital.signature.internal.batch.01.object." +
					"definition"
			});

		CommerceOrderAttachmentTestUtil.initialize(getClass());

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

		_commerceOrderAttachment = _addCommerceOrderAttachment();
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
				_commerceOrderAttachment.getCommerceOrderAttachmentId(),
				UserTestUtil.addUser()));

		Assert.assertTrue(
			portletException.getCause() instanceof
				PrincipalException.MustHavePermission);

		_saveDigitalSignatureConfiguration(false);

		_assertErrorMessage("Digital signature is not enabled for this order.");

		_saveDigitalSignatureConfiguration(true);

		_assertErrorMessage(
			"No signature request was found for this document.");

		portletException = Assert.assertThrows(
			PortletException.class,
			() -> _serveResource(
				RandomTestUtil.randomLong(), TestPropsValues.getUser()));

		Assert.assertTrue(
			portletException.getCause() instanceof PrincipalException);

		_addDSRequest(
			DSRequestConstants.STATUS_COMPLETED, UserTestUtil.addUser());

		_assertErrorMessage("This signature request is no longer active.");

		_commerceOrderAttachment = _addCommerceOrderAttachment();

		DSEnvelope dsEnvelope = _addDSRequest(
			DSRequestConstants.STATUS_SENT, UserTestUtil.addUser());

		String message = RandomTestUtil.randomString();
		List<String> methodNames = new ArrayList<>();

		DSEnvelopeManager dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						methodNames.add(method.getName());

						if (Objects.equals(method.getName(), "getDSEnvelope")) {
							return dsEnvelope;
						}

						throw new PortalException(message);
					}));

		try {
			_assertErrorMessage(message);

			Assert.assertEquals(
				Collections.singletonList("voidDSEnvelope"), methodNames);

			AtomicReference<Object[]> argumentsAtomicReference =
				new AtomicReference<>();

			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						if (Objects.equals(method.getName(), "getDSEnvelope")) {
							for (DSRecipient dsRecipient :
									dsEnvelope.getDSRecipients()) {

								dsRecipient.setStatus(
									DSRequestConstants.STATUS_VOIDED);
							}

							dsEnvelope.setStatus(
								DSRequestConstants.STATUS_VOIDED);

							return dsEnvelope;
						}

						if (Objects.equals(
								method.getName(), "voidDSEnvelope")) {

							argumentsAtomicReference.set(arguments);
						}

						return null;
					}));

			JSONObject jsonObject = _serveResource(
				_commerceOrderAttachment.getCommerceOrderAttachmentId(),
				TestPropsValues.getUser());

			Assert.assertEquals("{}", jsonObject.toString());

			Object[] voidDSEnvelopeArguments = argumentsAtomicReference.get();

			Assert.assertEquals(
				dsEnvelope.getDSEnvelopeId(), voidDSEnvelopeArguments[1]);
			Assert.assertEquals(
				"The sender voided the signature request.",
				voidDSEnvelopeArguments[3]);

			DSRequest dsRequest =
				_dsRequestManager.fetchLatestFileEntryDSRequest(
					_group.getCompanyId(),
					_commerceOrderAttachment.getFileEntryId());

			Assert.assertEquals(
				DSRequestConstants.STATUS_VOIDED, dsRequest.getStatus());

			_commerceOrderAttachment = _addCommerceOrderAttachment();

			DSEnvelope declinedDSEnvelope = _addDSRequest(
				DSRequestConstants.STATUS_SENT, UserTestUtil.addUser());

			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						if (Objects.equals(method.getName(), "getDSEnvelope")) {
							declinedDSEnvelope.setStatus(
								DSRequestConstants.STATUS_DECLINED);

							return declinedDSEnvelope;
						}

						throw new DSEnvelopeException.MustBeVoidable(message);
					}));

			_assertErrorMessage("This signature request is no longer active.");

			dsRequest = _dsRequestManager.fetchLatestFileEntryDSRequest(
				_group.getCompanyId(),
				_commerceOrderAttachment.getFileEntryId());

			Assert.assertEquals(
				DSRequestConstants.STATUS_DECLINED, dsRequest.getStatus());

			_commerceOrderAttachment = _addCommerceOrderAttachment();

			DSEnvelope voidedDSEnvelope = _addDSRequest(
				DSRequestConstants.STATUS_SENT, UserTestUtil.addUser());

			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						if (Objects.equals(method.getName(), "getDSEnvelope")) {
							voidedDSEnvelope.setStatus(
								DSRequestConstants.STATUS_VOIDED);

							return voidedDSEnvelope;
						}

						throw new DSEnvelopeException.MustBeVoidable(message);
					}));

			jsonObject = _serveResource(
				_commerceOrderAttachment.getCommerceOrderAttachmentId(),
				TestPropsValues.getUser());

			Assert.assertEquals("{}", jsonObject.toString());

			dsRequest = _dsRequestManager.fetchLatestFileEntryDSRequest(
				_group.getCompanyId(),
				_commerceOrderAttachment.getFileEntryId());

			Assert.assertEquals(
				DSRequestConstants.STATUS_VOIDED, dsRequest.getStatus());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager", dsEnvelopeManager);
		}
	}

	private CommerceOrderAttachment _addCommerceOrderAttachment()
		throws Exception {

		return _commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_commerceOrder.getCommerceOrderId(), RandomTestUtil.nextDouble(),
			false, RandomTestUtil.randomString(), "invoice",
			RandomTestUtil.randomString() + ".pdf",
			new ByteArrayInputStream(RandomTestUtil.randomBytes()));
	}

	private DSEnvelope _addDSRequest(String status, User user)
		throws Exception {

		DSEnvelope dsEnvelope = new DSEnvelope();

		dsEnvelope.setDSEnvelopeId(RandomTestUtil.randomString());

		DSRecipient dsRecipient = new DSRecipient();

		dsRecipient.setDSRecipientId(RandomTestUtil.randomString());
		dsRecipient.setEmailAddress(user.getEmailAddress());
		dsRecipient.setName(user.getFullName());
		dsRecipient.setRoutingOrder(1);
		dsRecipient.setStatus(status);

		dsEnvelope.setDSRecipients(ListUtil.fromArray(dsRecipient));

		dsEnvelope.setEmailSubject(RandomTestUtil.randomString());
		dsEnvelope.setStatus(status);

		ReflectionTestUtil.invoke(
			_dsRequestManager, "_addDSRequest",
			new Class<?>[] {
				long.class, DSEnvelope.class, long[].class, long.class,
				long.class
			},
			_group.getCompanyId(), dsEnvelope,
			new long[] {_commerceOrderAttachment.getFileEntryId()},
			_group.getGroupId(), user.getUserId());

		return dsEnvelope;
	}

	private void _assertErrorMessage(String expectedMessage) throws Exception {
		JSONObject jsonObject = _serveResource(
			_commerceOrderAttachment.getCommerceOrderAttachmentId(),
			TestPropsValues.getUser());

		Assert.assertEquals(
			expectedMessage, jsonObject.getString("errorMessage"));
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

	private JSONObject _serveResource(long commerceOrderAttachmentId, User user)
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

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		return _jsonFactory.createJSONObject(byteArrayOutputStream.toString());
	}

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

	@Inject(filter = "mvc.command.name=/commerce_order/void_ds_request")
	private MVCResourceCommand _mvcResourceCommand;

}