/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action.test;

import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upload.test.util.UploadTestUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;
import java.util.Objects;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

/**
 * @author Mariano Álvaro Sáiz
 */
public abstract class BaseDataDefinitionMVCActionCommandTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		group = GroupTestUtil.addGroup();
	}

	protected MockLiferayPortletActionRequest
			createMockLiferayPortletActionRequest(String fileName, String name)
		throws Exception {

		return createMockLiferayPortletActionRequest(fileName, name, null);
	}

	protected MockLiferayPortletActionRequest
			createMockLiferayPortletActionRequest(
				String fileName, String name, Long dataDefinitionId)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest(
				_createMockMultipartHttpServletRequest(fileName));

		if (dataDefinitionId != null) {
			mockLiferayPortletActionRequest.addParameter(
				"dataDefinitionId", String.valueOf(dataDefinitionId));
		}

		mockLiferayPortletActionRequest.addParameter("name", name);
		mockLiferayPortletActionRequest.addParameter("redirect", "fakeURL");
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockLiferayPortletActionRequest;
	}

	protected DataDefinition getImportedDataDefinition() throws Exception {
		DataDefinitionResource.Builder builder =
			dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource = builder.user(
			TestPropsValues.getUser()
		).build();

		Page<DataDefinition> page =
			dataDefinitionResource.
				getSiteDataDefinitionByContentTypeContentTypePage(
					group.getGroupId(), "journal", "Imported Structure",
					Pagination.of(1, 1), null);

		List<DataDefinition> items = (List<DataDefinition>)page.getItems();

		return items.get(0);
	}

	protected abstract MVCActionCommand getMVCActionCommand();

	protected void setUpUploadPortletRequest(
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest) {

		ReflectionTestUtil.setFieldValue(
			getMVCActionCommand(), "_portal",
			ProxyUtil.newProxyInstance(
				BaseDataDefinitionMVCActionCommandTestCase.class.
					getClassLoader(),
				new Class<?>[] {Portal.class},
				(proxy, method, args) -> {
					if (Objects.equals(
							method.getName(), "getUploadPortletRequest")) {

						LiferayPortletRequest liferayPortletRequest =
							portal.getLiferayPortletRequest(
								mockLiferayPortletActionRequest);

						return UploadTestUtil.createUploadPortletRequest(
							portal.getUploadServletRequest(
								liferayPortletRequest.getHttpServletRequest()),
							liferayPortletRequest,
							portal.getPortletNamespace(
								liferayPortletRequest.getPortletName()));
					}

					return method.invoke(portal, args);
				}));
	}

	@Inject
	protected DataDefinitionResource.Factory dataDefinitionResourceFactory;

	@DeleteAfterTestRun
	protected Group group;

	@Inject
	protected Portal portal;

	private MockMultipartHttpServletRequest
			_createMockMultipartHttpServletRequest(String fileName)
		throws Exception {

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		if (fileName == null) {
			return mockMultipartHttpServletRequest;
		}

		Class<?> clazz = getClass();

		byte[] bytes = _file.getBytes(
			clazz.getResourceAsStream("dependencies/" + fileName));

		mockMultipartHttpServletRequest.addFile(
			new MockMultipartFile(fileName, bytes));

		mockMultipartHttpServletRequest.setCharacterEncoding(StringPool.UTF8);

		String boundary = "WebKitFormBoundary" + StringUtil.randomString();

		mockMultipartHttpServletRequest.setContent(
			_getContent(boundary, bytes, fileName));
		mockMultipartHttpServletRequest.setContentType(
			MediaType.MULTIPART_FORM_DATA_VALUE + "; boundary=" + boundary);

		return mockMultipartHttpServletRequest;
	}

	private byte[] _getContent(String boundary, byte[] bytes, String fileName) {
		String start = StringBundler.concat(
			StringPool.DOUBLE_DASH, boundary,
			"\r\nContent-Disposition:form-data;name=\"jsonFile\";filename=\"",
			fileName, "\";\r\nContent-type:application/json\r\n\r\n");

		String end = StringBundler.concat(
			"\r\n--", boundary, StringPool.DOUBLE_DASH);

		return ArrayUtil.append(start.getBytes(), bytes, end.getBytes());
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Layout layout = new LayoutImpl();

		layout.setType(LayoutConstants.TYPE_CONTROL_PANEL);

		themeDisplay.setLayout(layout);

		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteDefaultLocale(LocaleUtil.US);
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private File _file;

}