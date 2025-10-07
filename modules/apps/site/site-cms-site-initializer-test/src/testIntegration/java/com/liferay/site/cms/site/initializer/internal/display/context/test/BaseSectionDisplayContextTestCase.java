/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.definition.setting.builder.ObjectDefinitionSettingBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Marco Galluzzi
 */
public abstract class BaseSectionDisplayContextTestCase
	extends BaseDisplayContextTestCase {

	public HashMap<String, Object> getAdditionalProps() throws Exception {
		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(mockHttpServletRequest),
			"getAdditionalProps", new Class<?>[0]);
	}

	public HashMap<String, Object> getBaseAdditionalProps()
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return HashMapBuilder.<String, Object>put(
			"assetLibraries", _getDepotEntriesJSONArray()
		).put(
			"autocompleteURL",
			() -> StringBundler.concat(
				"/o/search/v1.0/search?emptySearch=",
				"true&entryClassNames=com.liferay.portal.kernel.model.",
				"User,com.liferay.portal.kernel.model.",
				"UserGroup&nestedFields=embedded")
		).put(
			"baseAssetLibraryViewURL",
			StringBundler.concat(
				GroupConstants.CMS_FRIENDLY_URL, "/e/space/",
				_portal.getClassNameId(DepotEntry.class), StringPool.SLASH)
		).put(
			"baseFolderViewURL",
			StringBundler.concat(
				GroupConstants.CMS_FRIENDLY_URL, "/e/view-folder/",
				_portal.getClassNameId(ObjectEntryFolder.class),
				StringPool.SLASH)
		).put(
			"cmsGroupId",
			() -> {
				try {
					Group group = groupLocalService.getGroup(
						TestPropsValues.getCompanyId(), GroupConstants.CMS);

					return GetterUtil.getLong(group.getGroupId());
				}
				catch (PortalException portalException) {
					return null;
				}
			}
		).put(
			"collaboratorURLs",
			() -> {
				Map<String, String> collaboratorURL = new HashMap<>();

				for (ObjectDefinition objectDefinition :
						objectDefinitionService.getCMSObjectDefinitions(
							group.getCompanyId(),
							getObjectFolderExternalReferenceCodes())) {

					collaboratorURL.put(
						objectDefinition.getClassName(),
						StringBundler.concat(
							"/o", objectDefinition.getRESTContextPath(),
							"/{objectEntryId}/collaborators"));
				}

				collaboratorURL.put(
					ObjectEntryFolder.class.getName(),
					"/o/headless-object/v1.0/object-entry-folders" +
						"/{objectEntryFolderId}/collaborators");

				return collaboratorURL;
			}
		).put(
			"commentsProps",
			() -> HashMapBuilder.<String, Object>put(
				"addCommentURL",
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/add_content_item_comment")
			).put(
				"deleteCommentURL",
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/delete_content_item_comment")
			).put(
				"editCommentURL",
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item_comment")
			).put(
				"editorConfig",
				() -> {
					EditorConfiguration contentItemCommentEditorConfiguration =
						EditorConfigurationFactoryUtil.getEditorConfiguration(
							StringPool.BLANK, "contentItemCommentEditor",
							StringPool.BLANK, Collections.emptyMap(),
							themeDisplay,
							RequestBackedPortletURLFactoryUtil.create(
								getMockHttpServletRequest()));

					Map<String, Object> data =
						contentItemCommentEditorConfiguration.getData();

					return data.get("editorConfig");
				}
			).put(
				"getCommentsURL",
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL, "/get_asset_comments")
			).build()
		).put(
			"contentViewURL",
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/edit_content_item?&p_l_mode=read&p_p_state=",
				LiferayWindowState.POP_UP, "&redirect=",
				themeDisplay.getURLCurrent(), "&objectEntryId={embedded.id}")
		).put(
			"defaultPermissionAdditionalProps",
			_getDefaultPermissionAdditionalProps()
		).put(
			"objectDefinitionCssClasses",
			HashMapBuilder.put(
				"default", "content-icon-custom-structure"
			).put(
				"L_BASIC_WEB_CONTENT", "content-icon-basic-content"
			).put(
				"L_BLOG", "content-icon-blog"
			).build()
		).put(
			"objectDefinitionIcons",
			HashMapBuilder.put(
				"default", "web-content"
			).put(
				"L_BASIC_WEB_CONTENT", "forms"
			).put(
				"L_BLOG", "blogs"
			).build()
		).put(
			"parentObjectEntryFolderExternalReferenceCode",
			getRootObjectEntryFolderExternalReferenceCode()
		).put(
			"redirect", "http://localhost:8080/currentURL"
		).build();
	}

	@After
	public void tearDown() throws Exception {
		_mockHttpServletRequest = null;
		_objectEntryFolder = null;
	}

	@Test
	public void testGetAdditionalProps() throws Exception {
		_assertEquals(getBaseAdditionalProps(), getAdditionalProps());
	}

	@Test
	public void testGetBreadcrumbProps() throws Exception {
		HttpServletRequest httpServletRequest = getMockHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setLayout(
			LayoutTestUtil.addTypeContentLayout(group, "test-name"));

		AssertUtils.assertEquals(
			HashMapBuilder.<String, Object>put(
				"breadcrumbItems",
				JSONUtil.putAll(
					JSONUtil.put(
						"active", false
					).put(
						"href", (String)null
					).put(
						"label", "test-name"
					))
			).put(
				"hideSpace", true
			).build(),
			_getBreadcrumbProps(httpServletRequest));
	}

	@Test
	@TestInfo("LPD-50664")
	public void testGetCreationMenu() throws Exception {
		Map<String, String> expectedCreationMenuItems =
			getExpectedCreationMenuItems();

		if (expectedCreationMenuItems.isEmpty()) {
			return;
		}

		_testGetCreationMenu(getCreationMenu(), expectedCreationMenuItems);

		TreeMap<String, String> expectedCustomCreationMenuItems = new TreeMap<>(
			String.CASE_INSENSITIVE_ORDER);
		ObjectFolder objectFolder = null;

		for (String objectFolderExternalReferenceCode :
				getObjectFolderExternalReferenceCodes()) {

			objectFolder =
				objectFolderLocalService.getObjectFolderByExternalReferenceCode(
					objectFolderExternalReferenceCode,
					TestPropsValues.getCompanyId());

			ObjectDefinition objectDefinition = addCustomObjectDefinition(
				objectFolder.getObjectFolderId(), true, true,
				ObjectDefinitionConstants.SCOPE_DEPOT,
				WorkflowConstants.STATUS_APPROVED);

			expectedCustomCreationMenuItems.put(
				objectDefinition.getLabel(LocaleUtil.US),
				getRedirect(
					objectDefinition,
					_getRootObjectEntryFolderExternalReferenceCode(
						objectFolderExternalReferenceCode)));
		}

		expectedCreationMenuItems.putAll(expectedCustomCreationMenuItems);

		addCustomObjectDefinition(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			false, true, ObjectDefinitionConstants.SCOPE_DEPOT,
			WorkflowConstants.STATUS_APPROVED);

		addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), true, true,
			ObjectDefinitionConstants.SCOPE_COMPANY,
			WorkflowConstants.STATUS_APPROVED);
		addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), false, true,
			ObjectDefinitionConstants.SCOPE_DEPOT,
			WorkflowConstants.STATUS_APPROVED);
		addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), true, false,
			ObjectDefinitionConstants.SCOPE_DEPOT,
			WorkflowConstants.STATUS_APPROVED);
		addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), true, true,
			ObjectDefinitionConstants.SCOPE_DEPOT,
			WorkflowConstants.STATUS_DRAFT);
		addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), true, true,
			ObjectDefinitionConstants.SCOPE_SITE,
			WorkflowConstants.STATUS_APPROVED);

		_testGetCreationMenu(getCreationMenu(), expectedCreationMenuItems);
	}

	@Test
	@TestInfo("LPD-57827")
	public void testGetDepotEntriesJSONArrayWithMultipleDepotEntries()
		throws Exception {

		String name = StringUtil.randomString();

		DepotEntry depotEntry = addDepotEntry(name, DepotConstants.TYPE_SPACE);

		try {
			List<DepotEntry> depotEntries =
				_depotEntryLocalService.getDepotEntries(
					group.getCompanyId(), DepotConstants.TYPE_SPACE);

			Assert.assertEquals(
				depotEntries.toString(), 2, depotEntries.size());

			DepotEntry defaultDepotEntry = depotEntries.get(0);

			Group defaultDepotGroup = groupLocalService.fetchGroup(
				defaultDepotEntry.getGroupId());

			Assert.assertEquals("Default", defaultDepotGroup.getGroupKey());

			Group depotGroup = groupLocalService.fetchGroup(
				depotEntry.getGroupId());

			Assert.assertEquals(name, depotGroup.getGroupKey());

			_testGetDepotEntriesJSONArray(
				List.of(defaultDepotEntry), null,
				String.valueOf(defaultDepotGroup.getGroupId()));
			_testGetDepotEntriesJSONArray(
				List.of(depotEntry), null,
				String.valueOf(depotGroup.getGroupId()));
			_testGetDepotEntriesJSONArray(depotEntries, null, null);

			if (getRootObjectEntryFolderExternalReferenceCode() != null) {
				ObjectEntryFolder objectEntryFolder = _addObjectFolderEntry(
					depotGroup);

				_testGetDepotEntriesJSONArray(
					List.of(depotEntry), objectEntryFolder, null);
				_testGetDepotEntriesJSONArray(
					List.of(depotEntry), objectEntryFolder,
					String.valueOf(depotGroup.getGroupId()));
				_testGetDepotEntriesJSONArray(
					null, objectEntryFolder,
					String.valueOf(defaultDepotGroup.getGroupId()));
			}
		}
		finally {
			_depotEntryLocalService.deleteDepotEntry(depotEntry);
		}
	}

	@Test
	@TestInfo("LPD-57827")
	public void testGetDepotEntriesJSONArrayWithOneDepotEntryOnly()
		throws Exception {

		List<DepotEntry> depotEntries = _depotEntryLocalService.getDepotEntries(
			group.getCompanyId(), DepotConstants.TYPE_SPACE);

		Assert.assertEquals(depotEntries.toString(), 1, depotEntries.size());

		DepotEntry depotEntry = depotEntries.get(0);

		Group depotGroup = groupLocalService.fetchGroup(
			depotEntry.getGroupId());

		Assert.assertEquals("Default", depotGroup.getGroupKey());

		_testGetDepotEntriesJSONArray(
			depotEntries, null, String.valueOf(depotGroup.getGroupId()));

		_testGetDepotEntriesJSONArray(depotEntries, null, null);

		if (getRootObjectEntryFolderExternalReferenceCode() != null) {
			ObjectEntryFolder objectEntryFolder = _addObjectFolderEntry(
				depotGroup);

			_testGetDepotEntriesJSONArray(
				List.of(depotEntry), objectEntryFolder,
				String.valueOf(depotGroup.getGroupId()));
			_testGetDepotEntriesJSONArray(
				List.of(depotEntry), objectEntryFolder, null);
		}
	}

	protected ObjectDefinition addCustomObjectDefinition(
			String objectDefinitionSettingName,
			String objectDefinitionSettingValue)
		throws Exception {

		ObjectFolder objectFolder =
			objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				getObjectFolderExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		return addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), true, true,
			Collections.singletonList(
				new ObjectDefinitionSettingBuilder(
				).name(
					objectDefinitionSettingName
				).value(
					objectDefinitionSettingValue
				).build()),
			ObjectDefinitionConstants.SCOPE_DEPOT,
			WorkflowConstants.STATUS_APPROVED);
	}

	protected DepotEntry addDepotEntry(String name, int type) throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), name
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			type, ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	protected void assertFDSActionDropdownItem(
		FDSActionDropdownItem fdsActionDropdownItem, String icon, String id,
		String label, String method, String type) {

		Assert.assertNotNull(fdsActionDropdownItem);

		Map<String, String> data =
			(Map<String, String>)fdsActionDropdownItem.get("data");

		Assert.assertEquals(id, data.get("id"));
		Assert.assertEquals(method, data.get("method"));

		Assert.assertEquals(icon, fdsActionDropdownItem.get("icon"));
		Assert.assertEquals(label, fdsActionDropdownItem.get("label"));
		Assert.assertEquals(type, fdsActionDropdownItem.get("type"));
	}

	protected CreationMenu getCreationMenu() throws Exception {
		return getCreationMenu(null);
	}

	protected CreationMenu getCreationMenu(ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(
				getMockHttpServletRequest(objectEntryFolder)),
			"getCreationMenu", new Class<?>[0]);
	}

	protected abstract Map<String, String> getExpectedCreationMenuItems()
		throws PortalException;

	protected List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(getMockHttpServletRequest()),
			"getFDSActionDropdownItems", new Class<?>[0]);
	}

	@Override
	protected MockHttpServletRequest getMockHttpServletRequest(
			ObjectEntryFolder objectEntryFolder)
		throws Exception {

		if ((_mockHttpServletRequest == null) ||
			(_objectEntryFolder != objectEntryFolder)) {

			_mockHttpServletRequest = super.getMockHttpServletRequest(
				objectEntryFolder);

			_objectEntryFolder = objectEntryFolder;
		}

		return _mockHttpServletRequest;
	}

	protected abstract String getObjectFolderExternalReferenceCode();

	protected String[] getObjectFolderExternalReferenceCodes() {
		return new String[] {getObjectFolderExternalReferenceCode()};
	}

	protected String getRedirect(
		ObjectDefinition objectDefinition,
		String objectEntryFolderExternalReferenceCode) {

		StringBundler sb = new StringBundler(7);

		sb.append("http://localhost:8080");
		sb.append(portal.getPathMain());
		sb.append("/cms/add_structured_content_item?objectDefinitionId=");
		sb.append(objectDefinition.getObjectDefinitionId());
		sb.append("&objectEntryFolderExternalReferenceCode=");
		sb.append(objectEntryFolderExternalReferenceCode);
		sb.append("&plid=0&redirect=http://localhost:8080/currentURL");

		return sb.toString();
	}

	protected String getRedirect(String objectDefinitionExternalReferenceCode)
		throws PortalException {

		return getRedirect(
			objectDefinitionExternalReferenceCode,
			getRootObjectEntryFolderExternalReferenceCode());
	}

	protected String getRedirect(
			String objectDefinitionExternalReferenceCode,
			String rootObjectEntryFolderExternalReferenceCode)
		throws PortalException {

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode,
					TestPropsValues.getCompanyId());

		return getRedirect(
			objectDefinition, rootObjectEntryFolderExternalReferenceCode);
	}

	protected String getRootObjectEntryFolderExternalReferenceCode() {
		return null;
	}

	protected abstract Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception;

	@Inject
	protected GroupLocalService groupLocalService;

	@Inject
	protected ObjectDefinitionService objectDefinitionService;

	private ObjectEntryFolder _addObjectFolderEntry(Group group)
		throws Exception {

		ObjectEntryFolder rootObjectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					getRootObjectEntryFolderExternalReferenceCode(),
					group.getGroupId(), group.getCompanyId());

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			StringUtil.randomString(), group.getGroupId(),
			TestPropsValues.getUserId(),
			rootObjectEntryFolder.getObjectEntryFolderId(),
			RandomTestUtil.randomString(), null, StringUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private void _assertCreationMenuContainsDropdownItem(
		CreationMenu creationMenu, JSONArray expectedAssetLibrariesJSONArray,
		String expectedLabel) {

		List<DropdownItem> dropdownItems = (List<DropdownItem>)creationMenu.get(
			"primaryItems");

		Assert.assertFalse(dropdownItems.toString(), dropdownItems.isEmpty());

		Map<String, Object> dropdownItemData = null;

		for (DropdownItem dropdownItem : dropdownItems) {
			if (Objects.equals(dropdownItem.get("label"), expectedLabel)) {
				dropdownItemData = (HashMap<String, Object>)dropdownItem.get(
					"data");

				break;
			}
		}

		Assert.assertNotNull(dropdownItemData);

		JSONArray assetLibrariesJSONArray = (JSONArray)dropdownItemData.get(
			"assetLibraries");

		Assert.assertTrue(
			assetLibrariesJSONArray.toString(),
			JSONUtil.equals(
				expectedAssetLibrariesJSONArray, assetLibrariesJSONArray));
	}

	private void _assertCreationMenuNotContainsDropdownItem(
		CreationMenu creationMenu, String unexpectedLabel) {

		List<DropdownItem> dropdownItems = (List<DropdownItem>)creationMenu.get(
			"primaryItems");

		Assert.assertFalse(dropdownItems.toString(), dropdownItems.isEmpty());

		Map<String, Object> dropdownItemData = null;

		for (DropdownItem dropdownItem : dropdownItems) {
			if (Objects.equals(dropdownItem.get("label"), unexpectedLabel)) {
				dropdownItemData = (HashMap<String, Object>)dropdownItem.get(
					"data");

				break;
			}
		}

		Assert.assertNull(dropdownItemData);
	}

	private void _assertEquals(
		Map<String, ?> expectedMap, Map<String, ?> actualMap) {

		Assert.assertEquals(
			actualMap.toString(), expectedMap.size(), actualMap.size());

		JSONObject expectedJSONObject = _jsonFactory.createJSONObject(
			expectedMap);
		JSONObject actualJSONObject = _jsonFactory.createJSONObject(actualMap);

		JSONAssert.assertEquals(
			expectedJSONObject.toString(), actualJSONObject.toString(),
			JSONCompareMode.STRICT);
	}

	private HashMap<String, Object> _getBreadcrumbProps(
			HttpServletRequest httpServletRequest)
		throws Exception {

		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(httpServletRequest), "getBreadcrumbProps",
			new Class<?>[0]);
	}

	private Map<String, Object> _getDefaultPermissionAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"actions",
			() -> HashMapBuilder.put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
				() -> {
					ObjectDefinition objectDefinition =
						ObjectDefinitionLocalServiceUtil.
							getObjectDefinitionByExternalReferenceCode(
								"L_BASIC_WEB_CONTENT",
								TestPropsValues.getCompanyId());

					List<String> guestUnsupportedActions =
						ResourceActionsUtil.getResourceGuestUnsupportedActions(
							null, objectDefinition.getClassName());

					return TransformUtil.transformToArray(
						ResourceActionsUtil.getResourceActions(
							objectDefinition.getClassName()),
						resourceAction -> HashMapBuilder.<String, Object>put(
							"guestUnsupported",
							guestUnsupportedActions.contains(resourceAction)
						).put(
							"key", resourceAction
						).put(
							"label",
							ResourceActionsUtil.getAction(
								LocaleUtil.US, resourceAction)
						).build(),
						Map.class);
				}
			).put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
				() -> {
					ObjectDefinition objectDefinition =
						ObjectDefinitionLocalServiceUtil.
							getObjectDefinitionByExternalReferenceCode(
								"L_BASIC_DOCUMENT",
								TestPropsValues.getCompanyId());

					List<String> guestUnsupportedActions =
						ResourceActionsUtil.getResourceGuestUnsupportedActions(
							null, objectDefinition.getClassName());

					return TransformUtil.transformToArray(
						ResourceActionsUtil.getResourceActions(
							objectDefinition.getClassName()),
						resourceAction -> HashMapBuilder.<String, Object>put(
							"guestUnsupported",
							guestUnsupportedActions.contains(resourceAction)
						).put(
							"key", resourceAction
						).put(
							"label",
							ResourceActionsUtil.getAction(
								LocaleUtil.US, resourceAction)
						).build(),
						Map.class);
				}
			).put(
				"OBJECT_ENTRY_FOLDERS",
				() -> {
					List<String> guestUnsupportedActions =
						ResourceActionsUtil.getResourceGuestUnsupportedActions(
							null, ObjectEntryFolder.class.getName());

					return TransformUtil.transformToArray(
						ResourceActionsUtil.getResourceActions(
							ObjectEntryFolder.class.getName()),
						resourceAction -> HashMapBuilder.<String, Object>put(
							"guestUnsupported",
							guestUnsupportedActions.contains(resourceAction)
						).put(
							"key", resourceAction
						).put(
							"label",
							ResourceActionsUtil.getAction(
								LocaleUtil.US, resourceAction)
						).build(),
						Map.class);
				}
			).build()
		).put(
			"roles",
			() -> TransformUtil.transformToArray(
				RoleLocalServiceUtil.getGroupRolesAndTeamRoles(
					TestPropsValues.getCompanyId(), null,
					Arrays.asList(
						RoleConstants.ADMINISTRATOR,
						DepotRolesConstants.ASSET_LIBRARY_OWNER),
					null, null,
					new int[] {
						RoleConstants.TYPE_REGULAR, RoleConstants.TYPE_DEPOT
					},
					0, 0, QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				role -> HashMapBuilder.put(
					"key", role.getName()
				).put(
					"name", role.getTitle(LocaleUtil.US)
				).put(
					"type", String.valueOf(role.getType())
				).build(),
				Map.class)
		).build();
	}

	private JSONArray _getDepotEntriesJSONArray() throws PortalException {
		return _getDepotEntriesJSONArray(
			TransformUtil.transform(
				_depotEntryLocalService.getDepotEntries(
					group.getCompanyId(), DepotConstants.TYPE_SPACE),
				DepotEntry::getGroupId));
	}

	private JSONArray _getDepotEntriesJSONArray(List<Long> groupIds) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (Long groupId : groupIds) {
			JSONObject jsonObject = _getJSONObject(groupId);

			if (jsonObject != null) {
				jsonArray.put(jsonObject);
			}
		}

		return jsonArray;
	}

	private JSONArray _getJSONArray(List<DepotEntry> depotEntries) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (DepotEntry depotEntry : depotEntries) {
			Group group = groupLocalService.fetchGroup(depotEntry.getGroupId());

			if (group != null) {
				jsonArray.put(
					JSONUtil.put(
						"externalReferenceCode",
						group.getExternalReferenceCode()
					).put(
						"groupId", group.getGroupId()
					).put(
						"name", group.getName(LocaleUtil.getDefault())
					));
			}
		}

		return jsonArray;
	}

	private JSONObject _getJSONObject(long groupId) {
		Group group = groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return null;
		}

		return JSONUtil.put(
			"externalReferenceCode", group.getExternalReferenceCode()
		).put(
			"groupId", group.getGroupId()
		).put(
			"name", group.getName(LocaleUtil.getDefault())
		);
	}

	private String _getRedirect(DropdownItem dropdownItem) {
		Map<String, Object> map = (HashMap<String, Object>)dropdownItem.get(
			"data");

		if (map == null) {
			return null;
		}

		return (String)map.get("redirect");
	}

	private String _getRootObjectEntryFolderExternalReferenceCode(
		String objectFolderExternalReferenceCode) {

		if (Objects.equals(
				objectFolderExternalReferenceCode,
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES)) {

			return ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS;
		}

		return ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES;
	}

	private void _testGetCreationMenu(
		CreationMenu creationMenu,
		Map<String, String> expectedCreationMenuItems) {

		List<DropdownItem> dropdownItems = (List<DropdownItem>)creationMenu.get(
			"primaryItems");

		Assert.assertEquals(
			dropdownItems.toString(), expectedCreationMenuItems.size(),
			dropdownItems.size());

		int index = 0;

		for (Map.Entry<String, String> entry :
				expectedCreationMenuItems.entrySet()) {

			DropdownItem dropdownItem = dropdownItems.get(index);

			Assert.assertEquals(entry.getKey(), dropdownItem.get("label"));

			if (Validator.isNull(entry.getValue())) {
				Assert.assertNull(_getRedirect(dropdownItem));
			}
			else {
				Assert.assertEquals(
					entry.getValue(), _getRedirect(dropdownItem));
			}

			index++;
		}
	}

	private void _testGetDepotEntriesJSONArray(
			List<DepotEntry> depotEntries, ObjectEntryFolder objectEntryFolder,
			String acceptedGroupIds)
		throws Exception {

		ObjectDefinition objectDefinition = null;

		if (acceptedGroupIds != null) {
			objectDefinition = addCustomObjectDefinition(
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
				acceptedGroupIds);
		}
		else {
			objectDefinition = addCustomObjectDefinition(
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
				StringPool.TRUE);
		}

		try {
			CreationMenu creationMenu = getCreationMenu(objectEntryFolder);

			if (creationMenu == null) {
				return;
			}

			if (depotEntries != null) {
				_assertCreationMenuContainsDropdownItem(
					creationMenu, _getJSONArray(depotEntries),
					objectDefinition.getLabel(LocaleUtil.getDefault()));
			}
			else {
				_assertCreationMenuNotContainsDropdownItem(
					creationMenu,
					objectDefinition.getLabel(LocaleUtil.getDefault()));
			}
		}
		finally {
			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	private MockHttpServletRequest _mockHttpServletRequest;
	private ObjectEntryFolder _objectEntryFolder;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private Portal _portal;

}