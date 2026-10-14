/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.fragment.renderer.test;

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
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Brian I. Kim
 */
@FeatureFlag("LPD-69290")
@RunWith(Arquillian.class)
public class OrderAttachmentsDataSetFragmentRendererTest {

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
			OrderAttachmentsDataSetFragmentRendererTest.class,
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
	public void testGetDSRequestAdditionalProps() throws Exception {
		_testGetAdditionalProps();
		_testGetAdditionalPropsWithRestrictedCommerceOrderAttachment();
		_testGetAdditionalPropsWithDigitalSignatureDisabled();
	}

	@Test
	public void testGetDSRequestFDSActionDropdownItems() throws Exception {
		User user = TestPropsValues.getUser();

		Assert.assertEquals(
			Collections.emptyList(), _getFDSActionDropdownItemIds(user));

		_addDSRequest(_commerceOrderAttachment, user);

		Assert.assertEquals(
			List.of("sign", "view-signature-details"),
			_getFDSActionDropdownItemIds(user));

		Assert.assertEquals(
			List.of("view-signature-details"),
			_getFDSActionDropdownItemIds(_addOrderManagerUser()));
		Assert.assertEquals(
			Collections.emptyList(),
			_getFDSActionDropdownItemIds(UserTestUtil.addUser()));

		_saveDigitalSignatureConfiguration(false);

		Assert.assertEquals(
			Collections.emptyList(), _getFDSActionDropdownItemIds(user));
	}

	private void _addDSRequest(
			CommerceOrderAttachment commerceOrderAttachment, User user)
		throws Exception {

		DSEnvelope dsEnvelope = new DSEnvelope();

		dsEnvelope.setDSEnvelopeId(RandomTestUtil.randomString());

		DSRecipient dsRecipient = new DSRecipient();

		dsRecipient.setDSRecipientId(RandomTestUtil.randomString());
		dsRecipient.setEmailAddress(user.getEmailAddress());
		dsRecipient.setName(user.getFullName());
		dsRecipient.setRoutingOrder(1);
		dsRecipient.setStatus(DSRequestConstants.STATUS_SENT);

		dsEnvelope.setDSRecipients(ListUtil.fromArray(dsRecipient));

		dsEnvelope.setEmailSubject(RandomTestUtil.randomString());
		dsEnvelope.setStatus(DSRequestConstants.STATUS_SENT);

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

	private User _addOrderManagerUser() throws Exception {
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
			new String[] {ActionKeys.VIEW});

		AccountEntry accountEntry = _commerceOrder.getAccountEntry();

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(accountEntry.getAccountEntryGroupId()),
			role.getRoleId(),
			new String[] {CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS});

		User user = UserTestUtil.addUser();

		_roleLocalService.addUserRole(user.getUserId(), role);

		return user;
	}

	private Map<String, Object> _getAdditionalProps(User user)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_orderAttachmentsDataSetFragmentRenderer,
			"_getDSRequestAdditionalProps",
			new Class<?>[] {CommerceOrder.class, HttpServletRequest.class},
			_commerceOrder, _getMockHttpServletRequest(user));
	}

	private List<String> _getFDSActionDropdownItemIds(User user)
		throws Exception {

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			ReflectionTestUtil.invoke(
				_orderAttachmentsDataSetFragmentRenderer,
				"_getDSRequestFDSActionDropdownItems",
				new Class<?>[] {CommerceOrder.class, HttpServletRequest.class},
				_commerceOrder, _getMockHttpServletRequest(user));

		return TransformUtil.transform(
			fdsActionDropdownItems,
			fdsActionDropdownItem -> {
				Map<String, Object> data =
					(Map<String, Object>)fdsActionDropdownItem.get("data");

				return MapUtil.getString(data, "id");
			});
	}

	private MockHttpServletRequest _getMockHttpServletRequest(User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setPortalURL("http://" + RandomTestUtil.randomString());
		themeDisplay.setURLCurrent("/" + RandomTestUtil.randomString());
		themeDisplay.setUser(user);

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

	private void _testGetAdditionalProps() throws Exception {
		User user = TestPropsValues.getUser();

		Assert.assertEquals(Collections.emptyMap(), _getAdditionalProps(user));

		_addDSRequest(_commerceOrderAttachment, user);

		String commerceOrderAttachmentId = String.valueOf(
			_commerceOrderAttachment.getCommerceOrderAttachmentId());

		Map<String, Object> additionalProps = _getAdditionalProps(user);

		Assert.assertEquals(
			HashMapBuilder.put(
				commerceOrderAttachmentId, DSRequestConstants.STATUS_SENT
			).build(),
			additionalProps.get("dsRequestStatuses"));

		Map<String, String> signDSRequestURLs =
			(Map<String, String>)additionalProps.get("signDSRequestURLs");

		Assert.assertEquals(
			Collections.singleton(commerceOrderAttachmentId),
			signDSRequestURLs.keySet());

		additionalProps = _getAdditionalProps(_addOrderManagerUser());

		Assert.assertEquals(
			Collections.emptyMap(), additionalProps.get("signDSRequestURLs"));

		Assert.assertEquals(
			Collections.emptyMap(),
			_getAdditionalProps(UserTestUtil.addUser()));
	}

	private void _testGetAdditionalPropsWithDigitalSignatureDisabled()
		throws Exception {

		_saveDigitalSignatureConfiguration(false);

		Assert.assertEquals(
			Collections.emptyMap(),
			_getAdditionalProps(TestPropsValues.getUser()));
	}

	private void _testGetAdditionalPropsWithRestrictedCommerceOrderAttachment()
		throws Exception {

		CommerceOrderAttachment commerceOrderAttachment =
			_commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_commerceOrder.getCommerceOrderId(),
				RandomTestUtil.nextDouble(), true,
				RandomTestUtil.randomString(), "invoice",
				RandomTestUtil.randomString() + ".pdf",
				new ByteArrayInputStream(RandomTestUtil.randomBytes()));

		User user = _addOrderManagerUser();

		_addDSRequest(commerceOrderAttachment, user);

		String commerceOrderAttachmentId = String.valueOf(
			_commerceOrderAttachment.getCommerceOrderAttachmentId());

		Map<String, Object> additionalProps = _getAdditionalProps(user);

		Assert.assertEquals(
			HashMapBuilder.put(
				commerceOrderAttachmentId, DSRequestConstants.STATUS_SENT
			).build(),
			additionalProps.get("dsRequestStatuses"));

		additionalProps = _getAdditionalProps(TestPropsValues.getUser());

		Assert.assertEquals(
			HashMapBuilder.put(
				commerceOrderAttachmentId, DSRequestConstants.STATUS_SENT
			).put(
				String.valueOf(
					commerceOrderAttachment.getCommerceOrderAttachmentId()),
				DSRequestConstants.STATUS_SENT
			).build(),
			additionalProps.get("dsRequestStatuses"));
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

	@Inject(
		filter = "component.name=com.liferay.commerce.order.content.web.internal.fragment.renderer.OrderAttachmentsDataSetFragmentRenderer"
	)
	private FragmentRenderer _orderAttachmentsDataSetFragmentRenderer;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}