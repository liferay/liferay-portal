/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.media.internal.servlet.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.media.constants.CommerceMediaConstants;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;

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
@RunWith(Arquillian.class)
public class CommerceMediaServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());

		_accountEntry = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com", _serviceContext);

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(_group.getCompanyId());

		_commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId(),
			commerceCurrency.getCode());
	}

	@Test
	public void testDoGet() throws Exception {
		byte[] bytes = FileUtil.getBytes(
			CommerceMediaServletTest.class, "dependencies/image.jpg");

		CPAttachmentFileEntry cpAttachmentFileEntry1 =
			_addCPAttachmentFileEntry(bytes, ContentTypes.IMAGE_JPEG, "jpg");

		_cpDefinitionLocalService.updateCPDefinitionAccountGroupFilter(
			cpAttachmentFileEntry1.getClassPK(), true);

		AccountGroup accountGroup =
			CommerceAccountTestUtil.addAccountGroupAndAccountRel(
				_group.getCompanyId(), RandomTestUtil.randomString(),
				AccountConstants.ACCOUNT_GROUP_TYPE_STATIC,
				_accountEntry.getAccountEntryId(), _serviceContext);

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), CPDefinition.class.getName(),
			cpAttachmentFileEntry1.getClassPK());

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), VirtualCPTypeConstants.NAME, true,
			true);

		_cpDefinitionLocalService.updateCPDefinitionAccountGroupFilter(
			cpDefinition.getCPDefinitionId(), true);

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), CPDefinition.class.getName(),
			cpDefinition.getCPDefinitionId());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, _user.getUserId(), _commerceCatalog.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".jpg", ContentTypes.IMAGE_JPEG,
			bytes, null, null, null, _serviceContext);

		_cpDefinitionVirtualSettingLocalService.addCPDefinitionVirtualSetting(
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
			fileEntry.getFileEntryId(), null,
			CommerceOrderConstants.ORDER_STATUS_PENDING, 0, 0, true,
			fileEntry.getFileEntryId(), null, false, null, 0, false,
			_serviceContext);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstance cpInstance = cpInstances.get(0);

		_cpDefinitionVirtualSettingLocalService.addCPDefinitionVirtualSetting(
			CPInstance.class.getName(), cpInstance.getCPInstanceId(),
			fileEntry.getFileEntryId(), null,
			CommerceOrderConstants.ORDER_STATUS_PENDING, 0, 0, true,
			fileEntry.getFileEntryId(), null, false, null, 0, true,
			_serviceContext);

		User user1 = UserTestUtil.addUser();
		User user2 = _userLocalService.getGuestUser(_group.getCompanyId());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.media.internal.servlet." +
					"CommerceMediaServlet",
				LoggerTestUtil.OFF)) {

			MockHttpServletResponse mockHttpServletResponse = _get(
				_accountEntry.getAccountEntryId(), cpAttachmentFileEntry1,
				false, user1);

			Assert.assertEquals(
				HttpServletResponse.SC_NOT_FOUND,
				mockHttpServletResponse.getStatus());

			mockHttpServletResponse = _get(
				_accountEntry.getAccountEntryId(), cpAttachmentFileEntry1,
				false, user2);

			Assert.assertEquals(
				HttpServletResponse.SC_NOT_FOUND,
				mockHttpServletResponse.getStatus());

			mockHttpServletResponse = _get(
				RandomTestUtil.nextLong(), cpAttachmentFileEntry1, false,
				user2);

			Assert.assertEquals(
				HttpServletResponse.SC_NOT_FOUND,
				mockHttpServletResponse.getStatus());

			mockHttpServletResponse = _get(
				AccountConstants.ACCOUNT_ENTRY_ID_GUEST, cpAttachmentFileEntry1,
				false, user2);

			Assert.assertEquals(
				HttpServletResponse.SC_NOT_FOUND,
				mockHttpServletResponse.getStatus());

			CPAttachmentFileEntry cpAttachmentFileEntry2 =
				_addCPAttachmentFileEntry(
					bytes, ContentTypes.IMAGE_JPEG, "jpg");

			Role role = _roleLocalService.getRole(
				_group.getCompanyId(), RoleConstants.GUEST);

			_resourcePermissionLocalService.removeResourcePermission(
				_group.getCompanyId(), DLFileEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(cpAttachmentFileEntry2.getFileEntryId()),
				role.getRoleId(), ActionKeys.VIEW);

			mockHttpServletResponse = _get(
				_accountEntry.getAccountEntryId(), cpAttachmentFileEntry2,
				false, user2);

			Assert.assertFalse(
				Arrays.equals(
					bytes, mockHttpServletResponse.getContentAsByteArray()));

			mockHttpServletResponse = _serviceVirtualSampleRequest(
				_accountEntry.getAccountEntryId(),
				cpDefinition.getCPDefinitionId(), fileEntry,
				CommerceMediaConstants.URL_SEPARATOR_VIRTUAL_PRODUCT_SAMPLE,
				user2);

			Assert.assertEquals(
				HttpServletResponse.SC_UNAUTHORIZED,
				mockHttpServletResponse.getStatus());

			mockHttpServletResponse = _serviceVirtualSampleRequest(
				RandomTestUtil.nextLong(), cpDefinition.getCPDefinitionId(),
				fileEntry,
				CommerceMediaConstants.URL_SEPARATOR_VIRTUAL_PRODUCT_SAMPLE,
				user2);

			Assert.assertEquals(
				HttpServletResponse.SC_UNAUTHORIZED,
				mockHttpServletResponse.getStatus());

			mockHttpServletResponse = _serviceVirtualSampleRequest(
				_accountEntry.getAccountEntryId(), cpInstance.getCPInstanceId(),
				fileEntry,
				CommerceMediaConstants.URL_SEPARATOR_VIRTUAL_SKU_SAMPLE, user2);

			Assert.assertEquals(
				HttpServletResponse.SC_UNAUTHORIZED,
				mockHttpServletResponse.getStatus());

			mockHttpServletResponse = _serviceVirtualSampleRequest(
				RandomTestUtil.nextLong(), cpInstance.getCPInstanceId(),
				fileEntry,
				CommerceMediaConstants.URL_SEPARATOR_VIRTUAL_SKU_SAMPLE, user2);

			Assert.assertEquals(
				HttpServletResponse.SC_UNAUTHORIZED,
				mockHttpServletResponse.getStatus());
		}

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			_accountEntry.getAccountEntryId(), user1.getUserId());

		MockHttpServletResponse mockHttpServletResponse = _get(
			_accountEntry.getAccountEntryId(), cpAttachmentFileEntry1, false,
			user1);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		_assertContentDisposition(
			HttpHeaders.CONTENT_DISPOSITION_INLINE, mockHttpServletResponse);

		mockHttpServletResponse = _get(
			_accountEntry.getAccountEntryId(), cpAttachmentFileEntry1, true,
			user1);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		_assertContentDisposition(
			HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT,
			mockHttpServletResponse);

		mockHttpServletResponse = _serviceVirtualSampleRequest(
			_accountEntry.getAccountEntryId(), cpDefinition.getCPDefinitionId(),
			fileEntry,
			CommerceMediaConstants.URL_SEPARATOR_VIRTUAL_PRODUCT_SAMPLE, user1);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		mockHttpServletResponse = _serviceVirtualSampleRequest(
			_accountEntry.getAccountEntryId(), cpInstance.getCPInstanceId(),
			fileEntry, CommerceMediaConstants.URL_SEPARATOR_VIRTUAL_SKU_SAMPLE,
			user1);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		accountGroup = _accountGroupLocalService.getDefaultAccountGroup(
			_group.getCompanyId());

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), CPDefinition.class.getName(),
			cpAttachmentFileEntry1.getClassPK());

		mockHttpServletResponse = _get(
			AccountConstants.ACCOUNT_ENTRY_ID_GUEST, cpAttachmentFileEntry1,
			false, user2);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		String content = "<html><script>alert(1)</script></html>";

		mockHttpServletResponse = _get(
			_accountEntry.getAccountEntryId(),
			_addCPAttachmentFileEntry(
				content.getBytes(), ContentTypes.TEXT_HTML, "html"),
			false, user2);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		_assertContentDisposition(
			HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT,
			mockHttpServletResponse);

		content = "<svg><script>alert(1)</script></svg>";

		mockHttpServletResponse = _get(
			_accountEntry.getAccountEntryId(),
			_addCPAttachmentFileEntry(
				content.getBytes(), ContentTypes.IMAGE_SVG_XML, "svg"),
			false, user2);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		_assertContentDisposition(
			HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT,
			mockHttpServletResponse);
	}

	private CPAttachmentFileEntry _addCPAttachmentFileEntry(
			byte[] bytes, String contentType, String extension)
		throws Exception {

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId());
		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, _user.getUserId(), _commerceCatalog.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringBundler.concat(
				RandomTestUtil.randomString(), StringPool.PERIOD, extension),
			contentType, bytes, null, null, null, _serviceContext);

		return _cpAttachmentFileEntryLocalService.addCPAttachmentFileEntry(
			null, _user.getUserId(), _commerceCatalog.getGroupId(),
			_portal.getClassNameId(CPDefinition.class.getName()),
			cpDefinition.getCPDefinitionId(), fileEntry.getFileEntryId(), false,
			null, 1, 1, 2020, 1, 1, 2, 2, 2021, 2, 2, true, true,
			RandomTestUtil.randomLocaleStringMap(), null, 0D,
			CPAttachmentFileEntryConstants.TYPE_IMAGE, _serviceContext);
	}

	private void _assertContentDisposition(
		String expectedContentDisposition,
		MockHttpServletResponse mockHttpServletResponse) {

		String contentDisposition = mockHttpServletResponse.getHeader(
			HttpHeaders.CONTENT_DISPOSITION);

		Assert.assertTrue(
			contentDisposition,
			contentDisposition.startsWith(expectedContentDisposition));
	}

	private MockHttpServletResponse _get(
			long accountEntryId, CPAttachmentFileEntry cpAttachmentFileEntry,
			boolean download, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest("GET", StringPool.BLANK);

		mockHttpServletRequest.setAttribute(WebKeys.USER, user);
		mockHttpServletRequest.setParameter(
			"download", String.valueOf(download));
		mockHttpServletRequest.setPathInfo(
			StringBundler.concat(
				"/accounts/", accountEntryId, "/images/",
				cpAttachmentFileEntry.getCPAttachmentFileEntryId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private MockHttpServletResponse _serviceVirtualSampleRequest(
			long accountEntryId, long classPK, FileEntry fileEntry,
			String urlSeparator, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest("GET", StringPool.BLANK);

		mockHttpServletRequest.setAttribute(WebKeys.USER, user);
		mockHttpServletRequest.setPathInfo(
			StringBundler.concat(
				"/accounts/", accountEntryId, urlSeparator, classPK,
				CommerceMediaConstants.URL_SEPARATOR_FILE,
				fileEntry.getFileEntryId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@Inject
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Inject
	private CPDefinitionVirtualSettingLocalService
		_cpDefinitionVirtualSettingLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private Group _group;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.commerce.media.servlet.CommerceMediaServlet"
	)
	private Servlet _servlet;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}