/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.display.context.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommercePortletKeys;
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
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayInputStream;

import java.util.Collections;
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
public class CommerceOrderAttachmentsDisplayContextTest {

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
			CommerceOrderAttachmentsDisplayContextTest.class,
			new String[] {
				".com.liferay.digital.signature.internal.batch.01.object." +
					"definition"
			});

		CommerceOrderAttachmentTestUtil.initialize(getClass());

		_saveDigitalSignatureConfiguration(true);

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

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
	public void testGetAdditionalProps() throws Exception {
		User user = TestPropsValues.getUser();

		Map<String, Object> additionalProps = _getAdditionalProps(user);

		Assert.assertEquals(
			Collections.emptyMap(), additionalProps.get("dsRequestStatuses"));

		_addDSRequest(
			_commerceOrderAttachment, DSRequestConstants.STATUS_SENT, user);

		additionalProps = _getAdditionalProps(user);

		Assert.assertEquals(
			HashMapBuilder.put(
				String.valueOf(
					_commerceOrderAttachment.getCommerceOrderAttachmentId()),
				DSRequestConstants.STATUS_SENT
			).build(),
			additionalProps.get("dsRequestStatuses"));

		_saveDigitalSignatureConfiguration(false);

		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"commerceOrderId", _commerceOrder.getCommerceOrderId()
			).build(),
			_getAdditionalProps(user));
	}

	private void _addDSRequest(
			CommerceOrderAttachment commerceOrderAttachment, String status,
			User user)
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
			new long[] {commerceOrderAttachment.getFileEntryId()},
			_group.getGroupId(), user.getUserId());
	}

	private Map<String, Object> _getAdditionalProps(User user)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_getCommerceOrderAttachmentsDisplayContext(user),
			"getAdditionalProps", new Class<?>[0]);
	}

	private Object _getCommerceOrderAttachmentsDisplayContext(User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(user);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			PortletConfigFactoryUtil.create(
				_portletLocalService.getPortletById(
					CommercePortletKeys.COMMERCE_ORDER),
				null));
		mockHttpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			_getCommerceOrderEditDisplayContext());

		_screenNavigationEntry.render(
			mockHttpServletRequest, new MockHttpServletResponse());

		return mockHttpServletRequest.getAttribute(
			"com.liferay.commerce.order.web.internal.display.context." +
				"CommerceOrderAttachmentsDisplayContext");
	}

	private Object _getCommerceOrderEditDisplayContext() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(TestPropsValues.getUser());

		mockHttpServletRequest.setParameter(
			"commerceOrderId",
			String.valueOf(_commerceOrder.getCommerceOrderId()));

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(mockHttpServletRequest);

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return mockLiferayPortletRenderRequest.getAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT);
	}

	private MockHttpServletRequest _getMockHttpServletRequest(User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_layout.getLayoutSet());
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setPlid(_layout.getPlid());
		themeDisplay.setPortalURL("http://" + RandomTestUtil.randomString());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setURLCurrent("/" + RandomTestUtil.randomString());
		themeDisplay.setUser(user);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			new MockLiferayPortletRenderRequest(mockHttpServletRequest));
		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());
		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, _group.getCompanyId());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
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
	private Layout _layout;

	@Inject(
		filter = "mvc.command.name=/commerce_order/edit_commerce_order",
		type = MVCRenderCommand.class
	)
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject(
		filter = "component.name=com.liferay.commerce.order.web.internal.frontend.taglib.servlet.taglib.CommerceOrderAttachmentsScreenNavigationEntry"
	)
	private ScreenNavigationEntry<?> _screenNavigationEntry;

}