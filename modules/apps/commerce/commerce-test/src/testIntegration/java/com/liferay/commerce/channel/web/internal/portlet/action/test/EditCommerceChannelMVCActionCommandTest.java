/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.portlet.action.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletSession;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class EditCommerceChannelMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(_group.getCompanyId());

		Group siteGroup = GroupTestUtil.addGroup();

		_commerceChannel = _commerceChannelLocalService.addCommerceChannel(
			null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			siteGroup.getGroupId(), RandomTestUtil.randomString(),
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			commerceCurrency.getCode(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			role.getCompanyId(), CommerceChannel.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_commerceChannel.getCommerceChannelId()),
			role.getRoleId(),
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

		_user = UserTestUtil.addUser();

		_roleLocalService.addUserRole(_user.getUserId(), role.getRoleId());
	}

	@Test
	@TestInfo("LPD-116071")
	public void testProcessAction() throws Exception {
		FileEntry fileEntry = _addFileEntry(
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString() + ".txt");

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_mvcActionCommand.processAction(
				_getMockLiferayPortletActionRequest(fileEntry.getFileEntryId()),
				new MockLiferayPortletActionResponse());
		}

		Assert.assertNull(
			_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
				_commerceChannel.getGroupId(), "ORDER_PRINT_TEMPLATE"));
		Assert.assertNotNull(
			_dlAppLocalService.fetchFileEntry(fileEntry.getFileEntryId()));

		_userLocalService.addGroupUser(_group.getGroupId(), _user.getUserId());

		fileEntry = _addFileEntry(
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString() + ".txt");

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_mvcActionCommand.processAction(
				_getMockLiferayPortletActionRequest(fileEntry.getFileEntryId()),
				new MockLiferayPortletActionResponse());
		}

		Assert.assertNull(
			_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
				_commerceChannel.getGroupId(), "ORDER_PRINT_TEMPLATE"));
		Assert.assertNotNull(
			_dlAppLocalService.fetchFileEntry(fileEntry.getFileEntryId()));

		fileEntry = _addFileEntry(
			"<jasperReport bottomMargin=\"20\" columnWidth=\"555\" " +
				"leftMargin=\"20\" name=\"Test\" pageHeight=\"842\" " +
					"pageWidth=\"595\" rightMargin=\"20\" topMargin=\"20\" />",
			ContentTypes.TEXT_XML, RandomTestUtil.randomString() + ".jrxml");

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_mvcActionCommand.processAction(
				_getMockLiferayPortletActionRequest(fileEntry.getFileEntryId()),
				new MockLiferayPortletActionResponse());
		}

		Assert.assertNotNull(
			_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
				_commerceChannel.getGroupId(), "ORDER_PRINT_TEMPLATE"));
		Assert.assertNotNull(
			_dlAppLocalService.fetchFileEntry(fileEntry.getFileEntryId()));
	}

	private FileEntry _addFileEntry(
			String content, String mimeType, String sourceFileName)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(false);

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, sourceFileName,
			mimeType, sourceFileName, null, null, null,
			content.getBytes(StandardCharsets.UTF_8), null, null, null,
			serviceContext);
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			long fileEntryId)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_user.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setUser(_user);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletActionRequest.setParameter(
			Constants.CMD, Constants.UPDATE);
		mockLiferayPortletActionRequest.setParameter(
			"commerceChannelId",
			String.valueOf(_commerceChannel.getCommerceChannelId()));
		mockLiferayPortletActionRequest.setParameter(
			"commerceCurrencyCode", _commerceChannel.getCommerceCurrencyCode());
		mockLiferayPortletActionRequest.setParameter(
			"fileEntryId", String.valueOf(fileEntryId));
		mockLiferayPortletActionRequest.setParameter(
			"name", _commerceChannel.getName());
		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		return mockLiferayPortletActionRequest;
	}

	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private Group _group;

	@Inject(
		filter = "mvc.command.name=/commerce_channels/edit_commerce_channel",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}