/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadRequestSizeException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upload.test.util.UploadTestUtil;

import jakarta.portlet.PortletException;

import java.util.Arrays;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class AddTempAttachmentMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testDoProcessActionWithFileLargerThanMaxSize()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.portal.upload.internal.configuration." +
						"UploadServletRequestConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"maxSize", 102400L
					).build())) {

			Assert.assertEquals(
				102400,
				_dlValidator.getMaxAllowableSize(_group.getGroupId(), null));

			MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
				_getMockLiferayPortletActionRequest(new byte[163328]);

			Portal portal = ReflectionTestUtil.getFieldValue(
				_mvcActionCommand, "_portal");

			ReflectionTestUtil.setFieldValue(
				_mvcActionCommand, "_portal",
				ProxyUtil.newProxyInstance(
					Portal.class.getClassLoader(),
					new Class<?>[] {Portal.class},
					(proxy, method, args) -> {
						if (Objects.equals(
								method.getName(), "getUploadPortletRequest")) {

							LiferayPortletRequest liferayPortletRequest =
								portal.getLiferayPortletRequest(
									mockLiferayPortletActionRequest);

							return UploadTestUtil.createUploadPortletRequest(
								portal.getUploadServletRequest(
									liferayPortletRequest.
										getHttpServletRequest()),
								liferayPortletRequest,
								portal.getPortletNamespace(
									liferayPortletRequest.getPortletName()));
						}

						return method.invoke(portal, args);
					}));

			try {
				_mvcActionCommand.processAction(
					mockLiferayPortletActionRequest,
					new MockLiferayPortletActionResponse());

				Assert.fail();
			}
			catch (PortletException portletException) {
				Throwable throwable = portletException.getCause();

				Assert.assertTrue(
					throwable instanceof UploadRequestSizeException);
			}
			finally {
				ReflectionTestUtil.setFieldValue(
					_mvcActionCommand, "_portal", portal);
			}
		}

		String[] tempAttachmentNames =
			_kbArticleLocalService.getTempAttachmentNames(
				_group.getGroupId(), TestPropsValues.getUserId(),
				"com.liferay.knowledge.base.admin");

		Assert.assertEquals(
			Arrays.toString(tempAttachmentNames), 0,
			tempAttachmentNames.length);
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			byte[] bytes)
		throws Exception {

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.setCharacterEncoding(StringPool.UTF8);

		String boundary = "WebKitFormBoundary" + StringUtil.randomString();

		String footerString = StringBundler.concat(
			"\r\n--", boundary, StringPool.DOUBLE_DASH);
		String headerString = StringBundler.concat(
			StringPool.DOUBLE_DASH, boundary,
			"\r\nContent-Disposition: form-data; name=\"file\"; ",
			"filename=\"", StringUtil.randomString(),
			".txt\"\r\nContent-Type: text/plain\r\n\r\n");

		mockMultipartHttpServletRequest.setContent(
			ArrayUtil.append(
				headerString.getBytes(), bytes, footerString.getBytes()));

		mockMultipartHttpServletRequest.setContentType(
			StringBundler.concat(
				MediaType.MULTIPART_FORM_DATA_VALUE, "; boundary=", boundary));

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest(
				mockMultipartHttpServletRequest);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockLiferayPortletActionRequest.setParameter("resourcePrimKey", "0");

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLValidator _dlValidator;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private KBArticleLocalService _kbArticleLocalService;

	@Inject(filter = "mvc.command.name=/knowledge_base/add_temp_attachment")
	private MVCActionCommand _mvcActionCommand;

}