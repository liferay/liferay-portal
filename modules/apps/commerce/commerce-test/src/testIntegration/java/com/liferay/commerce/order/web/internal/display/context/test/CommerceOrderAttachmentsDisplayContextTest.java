/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.display.context.test;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
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
import com.liferay.commerce.test.util.DSCommerceOrderAttachmentTestUtil;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.constants.DSRequestConstants;
import com.liferay.digital.signature.constants.DigitalSignatureConstants;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

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
		CommerceOrderAttachmentTestUtil.initialize(getClass());
		DSCommerceOrderAttachmentTestUtil.initialize(getClass());

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

		Map<String, Object> addDSRequestModalProps =
			(Map<String, Object>)additionalProps.get("addDSRequestModalProps");

		Assert.assertArrayEquals(
			DigitalSignatureConstants.ALLOWED_FILE_EXTENSIONS,
			(String[])addDSRequestModalProps.get("allowedFileExtensions"));

		JSONObject buyerJSONObject = (JSONObject)addDSRequestModalProps.get(
			"buyer");

		Assert.assertEquals(
			user.getUserId(), buyerJSONObject.getLong("userId"));

		Assert.assertEquals(
			_commerceOrder.getCommerceOrderId(),
			addDSRequestModalProps.get("commerceOrderId"));
		Assert.assertEquals(
			Collections.emptyMap(), additionalProps.get("dsRequestStatuses"));
		Assert.assertEquals(
			Collections.emptyList(),
			additionalProps.get("replaceableCommerceOrderAttachmentIds"));

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

		Assert.assertEquals(
			Collections.emptyList(),
			additionalProps.get("replaceableCommerceOrderAttachmentIds"));

		CommerceOrderAttachment commerceOrderAttachment =
			_commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_commerceOrder.getCommerceOrderId(),
				RandomTestUtil.nextDouble(), false,
				RandomTestUtil.randomString(), "invoice",
				RandomTestUtil.randomString() + ".pdf",
				new ByteArrayInputStream(RandomTestUtil.randomBytes()));

		_addDSRequest(
			commerceOrderAttachment, DSRequestConstants.STATUS_VOIDED, user);

		additionalProps = _getAdditionalProps(user);

		Assert.assertEquals(
			List.of(
				String.valueOf(
					commerceOrderAttachment.getCommerceOrderAttachmentId())),
			additionalProps.get("replaceableCommerceOrderAttachmentIds"));

		_commerceOrderAttachmentLocalService.deleteCommerceOrderAttachment(
			commerceOrderAttachment);

		_commerceOrder.setUserId(
			_userLocalService.getGuestUserId(_group.getCompanyId()));

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		additionalProps = _getAdditionalProps(user);

		addDSRequestModalProps = (Map<String, Object>)additionalProps.get(
			"addDSRequestModalProps");

		Assert.assertNull(addDSRequestModalProps.get("buyer"));

		additionalProps = _getAdditionalProps(UserTestUtil.addUser());

		Assert.assertNull(additionalProps.get("addDSRequestModalProps"));

		_saveDigitalSignatureConfiguration(false);

		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"commerceOrderId", _commerceOrder.getCommerceOrderId()
			).build(),
			_getAdditionalProps(user));

		_saveDigitalSignatureConfiguration(true);

		_testGetAdditionalPropsWithRestrictedCommerceOrderAttachment();
		_testGetAdditionalPropsWithoutViewUsersPermission();
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		User user = TestPropsValues.getUser();

		Assert.assertEquals(
			List.of(
				"edit", "download", "delete", "request-signature", "resend",
				"void"),
			_getFDSActionDropdownItemIds(user));

		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					false, "LPD-69290")) {

			Assert.assertEquals(
				List.of("edit", "download", "delete"),
				_getFDSActionDropdownItemIds(user));
		}

		_saveDigitalSignatureConfiguration(false);

		Assert.assertEquals(
			List.of("edit", "download", "delete"),
			_getFDSActionDropdownItemIds(user));
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

	private List<String> _getFDSActionDropdownItemIds(User user)
		throws Exception {

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			ReflectionTestUtil.invoke(
				_getCommerceOrderAttachmentsDisplayContext(user),
				"getFDSActionDropdownItems", new Class<?>[0]);

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

	private void _testGetAdditionalPropsWithRestrictedCommerceOrderAttachment()
		throws Exception {

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

		CommerceOrderAttachment commerceOrderAttachment =
			_commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_commerceOrder.getCommerceOrderId(),
				RandomTestUtil.nextDouble(), true,
				RandomTestUtil.randomString(), "invoice",
				RandomTestUtil.randomString() + ".pdf",
				new ByteArrayInputStream(RandomTestUtil.randomBytes()));

		_addDSRequest(
			commerceOrderAttachment, DSRequestConstants.STATUS_SENT,
			TestPropsValues.getUser());

		String commerceOrderAttachmentId = String.valueOf(
			_commerceOrderAttachment.getCommerceOrderAttachmentId());

		Map<String, Object> additionalProps = _getAdditionalProps(user);

		Assert.assertEquals(
			HashMapBuilder.put(
				commerceOrderAttachmentId, DSRequestConstants.STATUS_SENT
			).build(),
			additionalProps.get("dsRequestStatuses"));

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(accountEntry.getAccountEntryGroupId()),
			role.getRoleId(),
			new String[] {
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS,
				CommerceOrderActionKeys.
					VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS
			});

		additionalProps = _getAdditionalProps(user);

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

	private void _testGetAdditionalPropsWithoutViewUsersPermission()
		throws Exception {

		AccountEntry accountEntry = _commerceOrder.getAccountEntry();

		_accountEntryUserRelLocalService.
			deleteAccountEntryUserRelsByAccountEntryId(
				accountEntry.getAccountEntryId());

		User user1 = UserTestUtil.addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), user1.getUserId());

		Role role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_REGULAR, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(accountEntry.getAccountEntryGroupId()),
			role.getRoleId(),
			new String[] {CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS});

		User user2 = UserTestUtil.addUser();

		_roleLocalService.addUserRole(user2.getUserId(), role);

		Map<String, Object> additionalProps = _getAdditionalProps(user2);

		Map<String, Object> addDSRequestModalProps =
			(Map<String, Object>)additionalProps.get("addDSRequestModalProps");

		JSONArray jsonArray = (JSONArray)addDSRequestModalProps.get(
			"accountEntryUsers");

		Assert.assertEquals(jsonArray.toString(), 0, jsonArray.length());

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), AccountEntry.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_group.getCompanyId()), role.getRoleId(),
			new String[] {AccountActionKeys.VIEW_USERS});

		additionalProps = _getAdditionalProps(user2);

		addDSRequestModalProps = (Map<String, Object>)additionalProps.get(
			"addDSRequestModalProps");

		jsonArray = (JSONArray)addDSRequestModalProps.get("accountEntryUsers");

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(user1.getUserId(), jsonObject.getLong("userId"));
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
	private Layout _layout;

	@Inject(
		filter = "mvc.command.name=/commerce_order/edit_commerce_order",
		type = MVCRenderCommand.class
	)
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject(
		filter = "component.name=com.liferay.commerce.order.web.internal.frontend.taglib.servlet.taglib.CommerceOrderAttachmentsScreenNavigationEntry"
	)
	private ScreenNavigationEntry<?> _screenNavigationEntry;

	@Inject
	private UserLocalService _userLocalService;

}