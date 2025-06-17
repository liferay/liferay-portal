/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal;

import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.upload.UploadServletRequestImpl;
import com.liferay.portal.upload.test.util.UploadTestUtil;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

/**
 * @author Guilherme Sá
 */
public abstract class BaseExportImportTestCase {

	@Before
	public void setUp() throws Exception {
		user = TestPropsValues.getUser();

		ObjectDefinitionResource.Builder builder =
			_objectDefinitionResourceFactory.create();

		objectDefinitionResource = builder.user(
			user
		).build();

		defaultObjectDefinitionJSON = read(
			"test-object-definition.default.json");
		_defaultObjectRelationshipJSON = read(
			"test-object-relationship.default.json");
	}

	protected JSONObject createOneToManyObjectRelationship(
			String objectDefinitionExternalReferenceCode1,
			String objectDefinitionExternalReferenceCode2,
			String objectDefinitionName2, String objectDefinitionScope2,
			String objectRelationshipName)
		throws Exception {

		return jsonFactory.createJSONObject(
			_defaultObjectRelationshipJSON
		).put(
			"edge", true
		).put(
			"name", objectRelationshipName
		).put(
			"objectDefinitionExternalReferenceCode1",
			objectDefinitionExternalReferenceCode1
		).put(
			"objectDefinitionExternalReferenceCode2",
			objectDefinitionExternalReferenceCode2
		).put(
			"objectDefinitionName2", objectDefinitionName2
		).put(
			"objectDefinitionScope2", objectDefinitionScope2
		).put(
			"type", "oneToMany"
		);
	}

	protected abstract ClassLoader getClassLoader();

	protected abstract Class<?> getClazz();

	protected abstract long getId(String name) throws Exception;

	protected abstract String getIdentifierName();

	protected abstract String getJSONName();

	protected abstract MVCActionCommand getMVCActionCommand();

	protected abstract MVCResourceCommand getMVCResourceCommand();

	protected String read(String fileName) throws Exception {
		Class<?> clazz = getClazz();

		return StringUtil.read(
			clazz.getResourceAsStream("dependencies/" + fileName));
	}

	protected void testExportImport(
			String actualFileName, String expectedFileName,
			String externalReferenceCode, String name)
		throws Exception {

		testExportImportJSON(
			read(actualFileName), read(expectedFileName), externalReferenceCode,
			name);
	}

	protected void testExportImportJSON(
			String actualJSON, String expectedJSON,
			String externalReferenceCode, String name)
		throws Exception {

		// MVCActionCommand

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			_importJSON(externalReferenceCode, actualJSON, name);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		Assert.assertTrue(
			Validator.isNull(mockHttpServletResponse.getContentAsString()));

		// MVCResourceCommand

		MVCResourceCommand mvcResourceCommand = getMVCResourceCommand();

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.addParameter(
			getIdentifierName(), String.valueOf(getId(name)));
		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		JSONAssert.assertEquals(
			expectedJSON,
			String.valueOf(
				mockLiferayResourceResponse.getPortletOutputStream()),
			JSONCompareMode.LENIENT);
	}

	protected void testFailedImportJSON(
			String actualJSON, String expectedJSON,
			String externalReferenceCode, String name)
		throws Exception {

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			_importJSON(externalReferenceCode, actualJSON, name);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		JSONAssert.assertEquals(
			expectedJSON, mockHttpServletResponse.getContentAsString(),
			JSONCompareMode.LENIENT);
	}

	protected String defaultObjectDefinitionJSON;

	@Inject
	protected JSONFactory jsonFactory;

	protected ObjectDefinitionResource objectDefinitionResource;
	protected User user;

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Layout layout = new LayoutImpl();

		layout.setType(LayoutConstants.TYPE_CONTROL_PANEL);

		themeDisplay.setLayout(layout);

		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteDefaultLocale(LocaleUtil.getSiteDefault());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private MockLiferayPortletActionResponse _importJSON(
			String externalReferenceCode, String json, String name)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				UploadServletRequestImpl.class.getName(), LoggerTestUtil.OFF)) {

			MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
				new MockLiferayPortletActionResponse();

			MVCActionCommand mvcActionCommand = getMVCActionCommand();

			MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
				new MockMultipartHttpServletRequest();

			MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
				new MockLiferayPortletActionRequest(
					mockMultipartHttpServletRequest);

			if (JSONUtil.isJSONArray(json)) {
				mockMultipartHttpServletRequest.addParameter(
					"objectDefinitions", json);
			}
			else {
				byte[] bytes = json.getBytes();

				mockMultipartHttpServletRequest.addFile(
					new MockMultipartFile(
						RandomTestUtil.randomString(), bytes));

				mockMultipartHttpServletRequest.setCharacterEncoding(
					StringPool.UTF8);

				String boundary =
					"WebKitFormBoundary" + StringUtil.randomString();

				String start = StringBundler.concat(
					StringPool.DOUBLE_DASH, boundary,
					"\r\nContent-Disposition:form-data;name=\"", getJSONName(),
					"\";filename=\"", RandomTestUtil.randomString(),
					"\";\r\nContent-type:application/json\r\n\r\n");
				String end = StringBundler.concat(
					"\r\n--", boundary, StringPool.DOUBLE_DASH);

				mockMultipartHttpServletRequest.setContent(
					ArrayUtil.append(start.getBytes(), bytes, end.getBytes()));

				mockMultipartHttpServletRequest.setContentType(
					MediaType.MULTIPART_FORM_DATA_VALUE + "; boundary=" +
						boundary);

				if (Validator.isNotNull(externalReferenceCode)) {
					mockLiferayPortletActionRequest.addParameter(
						"externalReferenceCode", externalReferenceCode);
				}

				mockLiferayPortletActionRequest.addParameter("name", name);
			}

			mockLiferayPortletActionRequest.addParameter(
				"redirect", RandomTestUtil.randomString());
			mockLiferayPortletActionRequest.setAttribute(
				JavaConstants.JAKARTA_PORTLET_CONFIG, null);
			mockLiferayPortletActionRequest.setAttribute(
				WebKeys.THEME_DISPLAY, _getThemeDisplay());

			ReflectionTestUtil.setFieldValue(
				mvcActionCommand, "_portal",
				ProxyUtil.newProxyInstance(
					getClassLoader(), new Class<?>[] {Portal.class},
					(proxy, method, args) -> {
						if (Objects.equals(
								method.getName(), "getUploadPortletRequest")) {

							LiferayPortletRequest liferayPortletRequest =
								_portal.getLiferayPortletRequest(
									mockLiferayPortletActionRequest);

							return UploadTestUtil.createUploadPortletRequest(
								_portal.getUploadServletRequest(
									liferayPortletRequest.
										getHttpServletRequest()),
								liferayPortletRequest,
								_portal.getPortletNamespace(
									liferayPortletRequest.getPortletName()));
						}

						return method.invoke(_portal, args);
					}));

			mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				mockLiferayPortletActionResponse);

			return mockLiferayPortletActionResponse;
		}
	}

	private String _defaultObjectRelationshipJSON;

	@Inject
	private ObjectDefinitionResource.Factory _objectDefinitionResourceFactory;

	@Inject
	private Portal _portal;

}