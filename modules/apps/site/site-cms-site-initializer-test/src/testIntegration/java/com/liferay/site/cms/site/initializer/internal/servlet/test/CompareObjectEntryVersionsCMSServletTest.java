/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.time.Instant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jürgen Kappler
 */
@FeatureFlag("LPD-56634")
@RunWith(Arquillian.class)
public class CompareObjectEntryVersionsCMSServletTest
	extends BaseCMSServletTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_basicWebContentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", group.getCompanyId());
		_contentsObjectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					"L_CONTENTS", depotEntry.getGroupId(),
					depotEntry.getCompanyId());
	}

	@Test
	public void testCompareObjectEntryVersions() throws Exception {
		_testCompareObjectEntryVersionsWithDateObjectField();
		_testCompareObjectEntryVersionsWithInvalidContent();
		_testCompareObjectEntryVersionsWithRichTextObjectField();
		_testCompareObjectEntryVersionsWithSameVersions();
		_testCompareObjectEntryVersionsWithTextObjectField();
		_testCompareObjectEntryVersionsWithoutUpdatePermission();
	}

	private void _addModelResourcePermissions(
			String[] actionIds, long objectEntryId, User user)
		throws Exception {

		String className = _basicWebContentObjectDefinition.getClassName();

		_resourcePermissionLocalService.addModelResourcePermissions(
			depotEntry.getCompanyId(), depotEntry.getGroupId(),
			user.getUserId(), className, String.valueOf(objectEntryId),
			ModelPermissionsFactory.create(
				HashMapBuilder.put(
					RoleConstants.USER, actionIds
				).build(),
				className));
	}

	private ObjectEntry _addObjectEntry(String content, String title)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);
		serviceContext.setAttribute(
			"friendlyUrlMap", new HashMap<String, String>());

		return _objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), depotEntry.getUserId(),
			_basicWebContentObjectDefinition.getObjectDefinitionId(),
			_contentsObjectEntryFolder.getObjectEntryFolderId(), "en_US",
			_toValues(content, title), serviceContext);
	}

	private void _assertDiffHtml(
		String addedValue, String diffHtml, String removedValue) {

		Assert.assertEquals(
			addedValue, _getDiffHtmlValue(_addedPattern, diffHtml));
		Assert.assertEquals(
			removedValue, _getDiffHtmlValue(_removedPattern, diffHtml));
	}

	private String _getDiffHtmlValue(Pattern pattern, String diffHtml) {
		Matcher matcher = pattern.matcher(diffHtml);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return diffHtml;
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			byte[] content, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL,
			"http://localhost:" + PortalUtil.getPortalServerPort(false) + "/");
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(mockHttpServletRequest));
		mockHttpServletRequest.setAttribute(WebKeys.USER, user);
		mockHttpServletRequest.setContent(content);
		mockHttpServletRequest.setContextPath("/o");
		mockHttpServletRequest.setMethod(HttpMethods.POST);
		mockHttpServletRequest.setServletPath("/cms/compare-versions");

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setRequest(mockHttpServletRequest);

		return themeDisplay;
	}

	private MockHttpServletResponse _service(byte[] content, User user)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(
			_getMockHttpServletRequest(content, user), mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private MockHttpServletResponse _service(
			long objectEntryId, int sourceVersion, int targetVersion, User user)
		throws Exception {

		String content = JSONUtil.put(
			"languageId", "en_US"
		).put(
			"objectEntryId", objectEntryId
		).put(
			"sourceVersion", sourceVersion
		).put(
			"targetVersion", targetVersion
		).toString();

		return _service(content.getBytes(), user);
	}

	private void _testCompareObjectEntryVersionsWithDateObjectField()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true, false, true, ObjectDefinitionTestUtil.getRandomName(),
				ListUtil.fromArray(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_DATE,
						ObjectFieldConstants.DB_TYPE_DATE, false, false, null,
						"Alpha", "alpha", false)),
				0, ObjectDefinitionConstants.SCOPE_COMPANY,
				TestPropsValues.getUserId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, "en_US",
			HashMapBuilder.<String, Serializable>put(
				"alpha", Date.from(Instant.parse("2026-01-01T00:00:00Z"))
			).build(),
			serviceContext);

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(), 0,
			HashMapBuilder.<String, Serializable>put(
				"alpha", Date.from(Instant.parse("2026-02-02T00:00:00Z"))
			).build(),
			serviceContext);

		Assert.assertEquals(2, objectEntry.getVersion());

		JSONObject diffsJSONObject = _toDiffsJSONObject(
			_service(
				objectEntry.getObjectEntryId(), 1, 2,
				TestPropsValues.getUser()));

		JSONObject sourceJSONObject = diffsJSONObject.getJSONObject("source");
		JSONObject targetJSONObject = diffsJSONObject.getJSONObject("target");

		Assert.assertEquals(
			"<span class=\"diff-html-removed\">02/02/2026</span><span " +
				"class=\"diff-html-added\">01/01/2026</span>",
			sourceJSONObject.getString("alpha"));
		Assert.assertEquals(
			"<span class=\"diff-html-removed\">01/01/2026</span><span " +
				"class=\"diff-html-added\">02/02/2026</span>",
			targetJSONObject.getString("alpha"));

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());
	}

	private void _testCompareObjectEntryVersionsWithInvalidContent()
		throws Exception {

		String content = RandomTestUtil.randomString();

		MockHttpServletResponse mockHttpServletResponse = _service(
			content.getBytes(), TestPropsValues.getUser());

		Assert.assertEquals(
			HttpServletResponse.SC_BAD_REQUEST,
			mockHttpServletResponse.getStatus());
	}

	private void _testCompareObjectEntryVersionsWithoutUpdatePermission()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		objectEntry = _updateObjectEntry(
			objectEntry, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		User user = UserTestUtil.addUser();

		_addModelResourcePermissions(
			new String[] {ActionKeys.VIEW}, objectEntry.getObjectEntryId(),
			user);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String name = PrincipalThreadLocal.getName();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		PrincipalThreadLocal.setName(user.getUserId());

		ObjectEntry viewableObjectEntry = _objectEntryService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(
			objectEntry.getObjectEntryId(),
			viewableObjectEntry.getObjectEntryId());

		MockHttpServletResponse mockHttpServletResponse = _service(
			objectEntry.getObjectEntryId(), 1, 2, user);

		Assert.assertEquals(
			HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
			mockHttpServletResponse.getStatus());
		Assert.assertEquals(
			StringPool.BLANK, mockHttpServletResponse.getContentAsString());

		_addModelResourcePermissions(
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
			objectEntry.getObjectEntryId(), user);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		mockHttpServletResponse = _service(
			objectEntry.getObjectEntryId(), 1, 2, user);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		JSONObject diffsJSONObject = _toDiffsJSONObject(
			mockHttpServletResponse);

		JSONObject targetJSONObject = diffsJSONObject.getJSONObject("target");

		Assert.assertTrue(
			targetJSONObject.toString(), targetJSONObject.has("title"));

		PermissionThreadLocal.setPermissionChecker(permissionChecker);
		PrincipalThreadLocal.setName(name);

		_userLocalService.deleteUser(user);
	}

	private void _testCompareObjectEntryVersionsWithRichTextObjectField()
		throws Exception {

		String sourceContent = RandomTestUtil.randomString();
		String title = RandomTestUtil.randomString();

		ObjectEntry objectEntry = _addObjectEntry(
			"<p>" + sourceContent + "</p>", title);

		String targetContent = RandomTestUtil.randomString();

		objectEntry = _updateObjectEntry(
			objectEntry, "<p>" + targetContent + "</p>", title);

		Assert.assertEquals(2, objectEntry.getVersion());

		JSONObject diffsJSONObject = _toDiffsJSONObject(
			_service(
				objectEntry.getObjectEntryId(), 1, 2,
				TestPropsValues.getUser()));

		JSONObject sourceJSONObject = diffsJSONObject.getJSONObject("source");
		JSONObject targetJSONObject = diffsJSONObject.getJSONObject("target");

		Assert.assertFalse(
			sourceJSONObject.toString(), sourceJSONObject.has("title"));
		Assert.assertFalse(
			targetJSONObject.toString(), targetJSONObject.has("title"));

		Assert.assertFalse(
			sourceJSONObject.toString(),
			sourceJSONObject.has("contentRawText"));
		Assert.assertFalse(
			targetJSONObject.toString(),
			targetJSONObject.has("contentRawText"));

		String sourceDiff = sourceJSONObject.getString("content");
		String targetDiff = targetJSONObject.getString("content");

		Assert.assertTrue(sourceDiff, sourceDiff.contains(sourceContent));
		Assert.assertTrue(targetDiff, targetDiff.contains(targetContent));
	}

	private void _testCompareObjectEntryVersionsWithSameVersions()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		MockHttpServletResponse mockHttpServletResponse = _service(
			objectEntry.getObjectEntryId(), 1, 1, TestPropsValues.getUser());

		Assert.assertEquals(
			ContentTypes.APPLICATION_JSON,
			mockHttpServletResponse.getContentType());
		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		JSONObject diffsJSONObject = _toDiffsJSONObject(
			mockHttpServletResponse);

		JSONObject sourceJSONObject = diffsJSONObject.getJSONObject("source");
		JSONObject targetJSONObject = diffsJSONObject.getJSONObject("target");

		Assert.assertEquals(
			sourceJSONObject.toString(), 0, sourceJSONObject.length());
		Assert.assertEquals(
			targetJSONObject.toString(), 0, targetJSONObject.length());
	}

	private void _testCompareObjectEntryVersionsWithTextObjectField()
		throws Exception {

		String content = RandomTestUtil.randomString();
		String sourceTitle = RandomTestUtil.randomString();

		ObjectEntry objectEntry = _addObjectEntry(content, sourceTitle);

		String targetTitle = RandomTestUtil.randomString();

		objectEntry = _updateObjectEntry(objectEntry, content, targetTitle);

		Assert.assertEquals(2, objectEntry.getVersion());

		MockHttpServletResponse mockHttpServletResponse = _service(
			objectEntry.getObjectEntryId(), 1, 2, TestPropsValues.getUser());

		Assert.assertEquals(
			ContentTypes.APPLICATION_JSON,
			mockHttpServletResponse.getContentType());
		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		JSONObject diffsJSONObject = _toDiffsJSONObject(
			mockHttpServletResponse);

		JSONObject sourceJSONObject = diffsJSONObject.getJSONObject("source");
		JSONObject targetJSONObject = diffsJSONObject.getJSONObject("target");

		Assert.assertFalse(
			sourceJSONObject.toString(), sourceJSONObject.has("content"));
		Assert.assertFalse(
			targetJSONObject.toString(), targetJSONObject.has("content"));

		String sourceDiff = sourceJSONObject.getString("title");
		String targetDiff = targetJSONObject.getString("title");

		_assertDiffHtml(sourceTitle, sourceDiff, targetTitle);
		_assertDiffHtml(targetTitle, targetDiff, sourceTitle);
	}

	private JSONObject _toDiffsJSONObject(
			MockHttpServletResponse mockHttpServletResponse)
		throws Exception {

		JSONObject responseJSONObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		return responseJSONObject.getJSONObject("diffs");
	}

	private Map<String, Serializable> _toValues(String content, String title) {
		return HashMapBuilder.<String, Serializable>put(
			"content_i18n",
			HashMapBuilder.put(
				"en_US", content
			).build()
		).put(
			"title_i18n",
			HashMapBuilder.put(
				"en_US", title
			).build()
		).build();
	}

	private ObjectEntry _updateObjectEntry(
			ObjectEntry objectEntry, String content, String title)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAttribute(
			"friendlyUrlMap", new HashMap<String, String>());

		return _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(), _toValues(content, title),
			serviceContext);
	}

	private static final Pattern _addedPattern = Pattern.compile(
		"<span class=\"diff-html-added\"[^>]*>([^<]*)</span>");
	private static final Pattern _removedPattern = Pattern.compile(
		"<span class=\"diff-html-removed\"[^>]*>([^<]*)</span>");

	private ObjectDefinition _basicWebContentObjectDefinition;

	@Inject
	private CompanyLocalService _companyLocalService;

	private ObjectEntryFolder _contentsObjectEntryFolder;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectEntryService _objectEntryService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.site.cms.site.initializer.internal.servlet.CompareObjectEntryVersionsCMSServlet"
	)
	private Servlet _servlet;

	@Inject
	private UserLocalService _userLocalService;

}