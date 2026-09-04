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
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectActionKeys;
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
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporter;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporterRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Marco Galluzzi
 */
public abstract class BaseSectionDisplayContextTestCase
	extends BaseDisplayContextTestCase {

	public Map<String, Object> getAdditionalProps() throws Exception {
		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(mockHttpServletRequest),
			"getAdditionalProps", new Class<?>[0]);
	}

	public Map<String, Object> getBaseAdditionalProps() throws Exception {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ObjectDefinition cmpProjectObjectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", themeDisplay.getCompanyId());

		return HashMapBuilder.<String, Object>put(
			"additionalAPIURLParameters",
			() -> {
				if (isFolderSearchEnabled()) {
					return getAdditionalAPIURLParameters();
				}

				return null;
			}
		).put(
			"assetLibraries", _getDepotEntriesJSONArray()
		).put(
			"autocompleteURL",
			() -> StringBundler.concat(
				"/o/search/v1.0/search?emptySearch=",
				"true&entryClassNames=com.liferay.portal.kernel.model.",
				"User,com.liferay.portal.kernel.model.",
				"UserGroup&nestedFields=embedded")
		).put(
			"availableExportFileFormats",
			() -> TransformUtil.transform(
				_translationInfoItemFieldValuesExporterRegistry.
					getTranslationInfoItemFieldValuesExporters(),
				this::_getExportFileFormatJSONObject)
		).put(
			"availableLocales",
			_getLocalesJSONArray(
				themeDisplay.getLocale(),
				LanguageUtil.getAvailableLocales(themeDisplay.getSiteGroupId()))
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
			"brokenLinksCheckerEnabled",
			GetterUtil.getBoolean(
				PropsUtil.get(PropsKeys.CMS_BROKEN_LINKS_CHECKER_ENABLED))
		).put(
			"candidateAssetLibraries", _getDepotEntriesJSONArray()
		).put(
			"cmpEnabled", LicenseManagerUtil.isAppEnabled(App.CMP)
		).put(
			"cmpProjectLinkObjectDefinitionId",
			() -> {
				ObjectDefinition cmpProjectLinkObjectDefinition =
					ObjectDefinitionLocalServiceUtil.
						fetchObjectDefinitionByExternalReferenceCode(
							"L_CMP_PROJECT_LINK", themeDisplay.getCompanyId());

				if (cmpProjectLinkObjectDefinition == null) {
					return null;
				}

				return cmpProjectLinkObjectDefinition.getObjectDefinitionId();
			}
		).put(
			"cmpProjectObjectDefinitionId",
			() -> {
				if (cmpProjectObjectDefinition == null) {
					return null;
				}

				return cmpProjectObjectDefinition.getObjectDefinitionId();
			}
		).put(
			"cmpProjectViewURL",
			() -> {
				if (cmpProjectObjectDefinition == null) {
					return null;
				}

				return StringBundler.concat(
					themeDisplay.getPortalURL(),
					_portal.getPathFriendlyURLPublic(), "/cms/e/project/",
					_portal.getClassNameId(
						cmpProjectObjectDefinition.getClassName()));
			}
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
								mockHttpServletRequest));

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
				"/edit_content_item?p_l_mode=read&p_p_state=",
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
				"L_CMS_BASIC_WEB_CONTENT", "content-icon-basic-content"
			).put(
				"L_CMS_BLOG", "content-icon-blog"
			).put(
				"L_CMS_EXTERNAL_VIDEO", "file-icon-color-3"
			).build()
		).put(
			"objectDefinitionIcons",
			HashMapBuilder.put(
				"default", "web-content"
			).put(
				"L_CMS_BASIC_WEB_CONTENT", "forms"
			).put(
				"L_CMS_BLOG", "blogs"
			).put(
				"L_CMS_EXTERNAL_VIDEO", "document-multimedia"
			).build()
		).put(
			"parentObjectEntryFolderExternalReferenceCode",
			getRootObjectEntryFolderExternalReferenceCode()
		).put(
			"redirect",
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/currentURL"
		).build();
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		adminUser = TestPropsValues.getUser();
	}

	@After
	public void tearDown() throws Exception {
		_mockHttpServletRequest = null;
		_objectEntryFolder = null;

		setUser(adminUser);
	}

	@Test
	public void testGetAdditionalAPIURLParameters() throws Exception {
		String additionalAPIURLParameters = ReflectionTestUtil.invoke(
			getSectionDisplayContext(getMockHttpServletRequest()),
			"getAdditionalAPIURLParameters", new Class<?>[0]);

		Assert.assertTrue(
			additionalAPIURLParameters,
			additionalAPIURLParameters.contains("sort=dateModified:desc"));
	}

	@Test
	public void testGetAdditionalProps() throws Exception {
		DepotEntry depotEntry = addDepotEntry(
			StringUtil.randomString(), TestPropsValues.getUserId());

		_assertEquals(getBaseAdditionalProps(), getAdditionalProps());

		_depotEntryLocalService.deleteDepotEntry(depotEntry);
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
	public void testGetCMSSectionFilterString() throws Exception {
		DepotEntry depotEntry1 = addDepotEntry(
			StringUtil.randomString(), TestPropsValues.getUserId());

		DepotEntry depotEntry2 = addDepotEntry(
			StringUtil.randomString(), TestPropsValues.getUserId());

		User regularUser = UserTestUtil.addUser();

		groupLocalService.addUserGroup(
			regularUser.getUserId(), depotEntry1.getGroupId());

		setUser(regularUser);

		Object displayContext = getSectionDisplayContext(
			getMockHttpServletRequest(regularUser));

		String filterString = getCMSSectionFilterString(displayContext);

		Assert.assertTrue(
			filterString,
			filterString.contains(
				"groupIds/any(g:g in (" + depotEntry1.getGroupId() + "))"));

		User cmsAdministratorUser = UserTestUtil.addCompanyUser(
			companyLocalService.getCompany(TestPropsValues.getCompanyId()),
			RoleConstants.CMS_ADMINISTRATOR);

		setUser(cmsAdministratorUser);

		displayContext = getSectionDisplayContext(
			getMockHttpServletRequest(cmsAdministratorUser));

		filterString = getCMSSectionFilterString(displayContext);

		Assert.assertFalse(filterString, filterString.contains("groupIds/any"));

		_depotEntryLocalService.deleteDepotEntry(depotEntry1);
		_depotEntryLocalService.deleteDepotEntry(depotEntry2);

		_userLocalService.deleteUser(regularUser);
		_userLocalService.deleteUser(cmsAdministratorUser);
	}

	@Test
	public void testGetCreationMenu() throws Exception {

		// Create menu in root folder

		Map<String, String> expectedCreationMenuItems =
			getExpectedCreationMenuItems();

		expectedCreationMenuItems = _getLocalizedKeysMap(
			expectedCreationMenuItems);

		Map<String, String> objectEntryExpectedCreationMenuItems =
			new HashMap<>(expectedCreationMenuItems);

		Map<String, String> objectEntryFolderExpectedCreationMenuItems =
			new HashMap<>();

		if (expectedCreationMenuItems.containsKey("Folder")) {
			objectEntryExpectedCreationMenuItems.remove("Folder");
			objectEntryFolderExpectedCreationMenuItems.put(
				"Folder", StringPool.BLANK);
		}

		User user1 = UserTestUtil.addUser();

		setUser(user1);

		_assertCreationMenu(getCreationMenu(user1), Collections.emptyMap());

		setUser(adminUser);

		DepotEntry depotEntry1 = addDepotEntry(
			StringUtil.randomString(), TestPropsValues.getUserId());

		User user2 = UserTestUtil.addUser();

		groupLocalService.addUserGroup(
			user2.getUserId(), depotEntry1.getGroup());

		User user3 = _addUser(new String[] {ActionKeys.ADD_ENTRY}, depotEntry1);
		User user4 = _addUser(
			new String[] {ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER},
			depotEntry1);
		User user5 = _addUser(
			new String[] {
				ActionKeys.ADD_ENTRY, ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER
			},
			depotEntry1);

		_assertCreationMenu(
			getCreationMenu(adminUser), expectedCreationMenuItems);

		DepotEntry depotEntry2 = addDepotEntry(
			StringUtil.randomString(), TestPropsValues.getUserId());

		_assertCreationMenu(
			getCreationMenu(adminUser), expectedCreationMenuItems);

		setUser(user1);

		_assertCreationMenu(getCreationMenu(user1), Collections.emptyMap());

		setUser(user2);

		_assertCreationMenu(getCreationMenu(user2), Collections.emptyMap());

		setUser(user3);

		_assertCreationMenu(
			getCreationMenu(user3), objectEntryExpectedCreationMenuItems);

		setUser(user4);

		_assertCreationMenu(
			getCreationMenu(user4), objectEntryFolderExpectedCreationMenuItems);

		setUser(user5);

		_assertCreationMenu(getCreationMenu(user5), expectedCreationMenuItems);

		// Create menu with custom object definitions

		setUser(adminUser);

		List<ObjectDefinition> objectDefinitions = new ArrayList<>();

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

			objectDefinitions.add(objectDefinition);
		}

		addCustomObjectDefinition(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			false, true, ObjectDefinitionConstants.SCOPE_DEPOT,
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

		_assertCreationMenu(
			getCreationMenu(adminUser),
			HashMapBuilder.putAll(
				expectedCreationMenuItems
			).putAll(
				expectedCustomCreationMenuItems
			).build());

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}

		// Create menu within an object entry folder

		if (getRootObjectEntryFolderExternalReferenceCode() != null) {
			setUser(adminUser);

			ObjectEntryFolder objectEntryFolder = _addObjectFolderEntry(
				depotEntry1.getGroup());

			_assertCreationMenu(
				getCreationMenu(objectEntryFolder, adminUser),
				expectedCreationMenuItems);

			groupLocalService.addUserGroup(
				user1.getUserId(), depotEntry1.getGroup());

			Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

			_userGroupRoleLocalService.addUserGroupRoles(
				user1.getUserId(), depotEntry1.getGroupId(),
				new long[] {role.getRoleId()});

			setUser(user1);

			_assertCreationMenu(
				getCreationMenu(objectEntryFolder, user1),
				Collections.emptyMap());

			_setResourcePermissions(
				new String[] {ActionKeys.ADD_ENTRY}, objectEntryFolder, role);

			_assertCreationMenu(
				getCreationMenu(objectEntryFolder, user1),
				objectEntryExpectedCreationMenuItems);

			_setResourcePermissions(
				new String[] {ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER},
				objectEntryFolder, role);

			_assertCreationMenu(
				getCreationMenu(objectEntryFolder, user1),
				objectEntryFolderExpectedCreationMenuItems);

			_setResourcePermissions(
				new String[] {
					ActionKeys.ADD_ENTRY,
					ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER
				},
				objectEntryFolder, role);

			_assertCreationMenu(
				getCreationMenu(objectEntryFolder, user1),
				expectedCreationMenuItems);
		}

		_depotEntryLocalService.deleteDepotEntry(depotEntry1);
		_depotEntryLocalService.deleteDepotEntry(depotEntry2);

		_userLocalService.deleteUser(user1);
		_userLocalService.deleteUser(user2);
		_userLocalService.deleteUser(user3);
		_userLocalService.deleteUser(user4);
		_userLocalService.deleteUser(user5);
	}

	@Test
	@TestInfo("LPD-57827")
	public void testGetDepotEntriesJSONArray() throws Exception {
		String name = StringUtil.toLowerCase(RandomTestUtil.randomString());

		List<DepotEntry> depotEntries = _depotEntryLocalService.getDepotEntries(
			group.getCompanyId(), DepotConstants.TYPE_SPACE);

		int originalDepotEntriesSize = depotEntries.size();

		addDepotEntry(name, DepotConstants.TYPE_SPACE);

		depotEntries = _depotEntryLocalService.getDepotEntries(
			group.getCompanyId(), DepotConstants.TYPE_SPACE);

		Assert.assertEquals(
			depotEntries.toString(), originalDepotEntriesSize + 1,
			depotEntries.size());

		DepotEntry depotEntry = depotEntries.get(depotEntries.size() - 1);

		Group depotGroup = groupLocalService.fetchGroup(
			depotEntry.getGroupId());

		Assert.assertEquals(name, depotGroup.getGroupKey());

		_testGetDepotEntriesJSONArray(
			List.of(depotEntry), null, String.valueOf(depotGroup.getGroupId()));

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

	protected DepotEntry addDepotEntry(String name, long userId)
		throws Exception {

		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), name
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), userId));
	}

	protected String appendGroupIds(String filterString) throws Exception {
		return StringBundler.concat(
			filterString, " and groupIds/any(g:g in (",
			com.liferay.petra.string.StringUtil.merge(
				_depotEntryLocalService.getDepotEntryGroupIds(
					group.getCompanyId(), TestPropsValues.getUserId(),
					DepotConstants.TYPE_SPACE),
				StringPool.COMMA),
			"))");
	}

	protected String appendStatus(String filterString) {
		return filterString + " and status in (0, 2, 3, 1, 7)";
	}

	protected String getAdditionalAPIURLParameters() throws Exception {
		return StringBundler.concat(
			"emptySearch=true&filter=",
			appendStatus(appendGroupIds(getFilterString())),
			"&nestedFields=embedded,embeddedTaxonomyCategory,",
			"file.metadata,file.previewURL,file.thumbnailURL,",
			"modifiedBy,numberOfObjectEntries,numberOfObjectEntryFolders,",
			"systemProperties.collaboratorBrief,",
			"systemProperties.objectDefinitionBrief&sort=dateModified:desc");
	}

	protected List<FDSActionDropdownItem> getBulkActionDropdownItems()
		throws Exception {

		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(getMockHttpServletRequest()),
			"getBulkActionDropdownItems", new Class<?>[0]);
	}

	protected String getCMSSectionFilterString(Object displayContext) {
		return ReflectionTestUtil.invoke(
			displayContext, "getCMSSectionFilterString", new Class<?>[0],
			new Object[0]);
	}

	protected CreationMenu getCreationMenu(ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return getCreationMenu(objectEntryFolder, TestPropsValues.getUser());
	}

	protected CreationMenu getCreationMenu(
			ObjectEntryFolder objectEntryFolder, User user)
		throws Exception {

		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(
				getMockHttpServletRequest(objectEntryFolder, user)),
			"getCreationMenu", new Class<?>[0]);
	}

	protected CreationMenu getCreationMenu(User user) throws Exception {
		return getCreationMenu(null, user);
	}

	protected abstract Map<String, String> getExpectedCreationMenuItems()
		throws PortalException;

	protected List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(getMockHttpServletRequest()),
			"getFDSActionDropdownItems", new Class<?>[0]);
	}

	protected String getFilterString() {
		return StringPool.BLANK;
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

		StringBundler sb = new StringBundler(10);

		sb.append("http://localhost:");
		sb.append(PortalUtil.getPortalServerPort(false));
		sb.append(portal.getPathMain());
		sb.append("/cms/add_structured_content_item?objectDefinitionId=");
		sb.append(objectDefinition.getObjectDefinitionId());
		sb.append("&objectEntryFolderExternalReferenceCode=");
		sb.append(objectEntryFolderExternalReferenceCode);
		sb.append("&plid=0&redirect=http://localhost:");
		sb.append(PortalUtil.getPortalServerPort(false));
		sb.append("/currentURL");

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

	protected boolean isFolderSearchEnabled() {
		return false;
	}

	protected void setUser(User user) {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());
	}

	protected User adminUser;

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

	private User _addUser(String[] actionIds, DepotEntry depotEntry)
		throws Exception {

		User user = UserTestUtil.addUser();

		groupLocalService.addUserGroup(user.getUserId(), depotEntry.getGroup());

		Role role = _getRoleWithPermissions(actionIds, depotEntry);

		_userGroupRoleLocalService.addUserGroupRoles(
			user.getUserId(), depotEntry.getGroupId(),
			new long[] {role.getRoleId()});

		return user;
	}

	private void _assertCreationMenu(
		CreationMenu creationMenu,
		Map<String, String> expectedCreationMenuItems) {

		List<DropdownItem> dropdownItems = (List<DropdownItem>)creationMenu.get(
			"primaryItems");

		Assert.assertEquals(
			dropdownItems.toString(), expectedCreationMenuItems.size(),
			dropdownItems.size());

		for (DropdownItem dropdownItem : dropdownItems) {
			String key = (String)dropdownItem.get("label");

			Assert.assertTrue(expectedCreationMenuItems.containsKey(key));

			String value = expectedCreationMenuItems.get(key);

			if (Validator.isNull(value)) {
				Assert.assertNull(_getRedirect(dropdownItem));
			}
			else {
				Assert.assertEquals(value, _getRedirect(dropdownItem));
			}
		}
	}

	private void _assertCreationMenuContainsDropdownItem(
			CreationMenu creationMenu,
			JSONArray expectedAssetLibrariesJSONArray, String expectedLabel)
		throws Exception {

		List<DropdownItem> dropdownItems = (List<DropdownItem>)creationMenu.get(
			"primaryItems");

		Assert.assertFalse(dropdownItems.toString(), dropdownItems.isEmpty());

		Map<String, Object> dropdownItemData = null;

		for (DropdownItem dropdownItem : dropdownItems) {
			if (Objects.equals(dropdownItem.get("label"), expectedLabel)) {
				dropdownItemData = (Map<String, Object>)dropdownItem.get(
					"data");

				break;
			}
		}

		Assert.assertNotNull(dropdownItemData);

		JSONArray assetLibrariesJSONArray = (JSONArray)dropdownItemData.get(
			"assetLibraries");

		Assert.assertEquals(
			assetLibrariesJSONArray.toString() + " does not equal " +
				expectedAssetLibrariesJSONArray.toString(),
			SetUtil.fromList(
				JSONUtil.toList(
					expectedAssetLibrariesJSONArray, JSONObject::toString)),
			SetUtil.fromList(
				JSONUtil.toList(
					assetLibrariesJSONArray, JSONObject::toString)));
	}

	private void _assertCreationMenuNotContainsDropdownItem(
		CreationMenu creationMenu, String unexpectedLabel) {

		List<DropdownItem> dropdownItems = (List<DropdownItem>)creationMenu.get(
			"primaryItems");

		Assert.assertFalse(dropdownItems.toString(), dropdownItems.isEmpty());

		Map<String, Object> dropdownItemData = null;

		for (DropdownItem dropdownItem : dropdownItems) {
			if (Objects.equals(dropdownItem.get("label"), unexpectedLabel)) {
				dropdownItemData = (Map<String, Object>)dropdownItem.get(
					"data");

				break;
			}
		}

		Assert.assertNull(dropdownItemData);
	}

	private void _assertEquals(
			Map<String, ?> expectedMap, Map<String, ?> actualMap)
		throws Exception {

		Assert.assertEquals(
			actualMap.toString(), expectedMap.size(), actualMap.size());

		JSONObject expectedJSONObject = _jsonFactory.createJSONObject(
			expectedMap);
		JSONObject actualJSONObject = _jsonFactory.createJSONObject(actualMap);

		JSONAssert.assertEquals(
			expectedJSONObject.toString(), actualJSONObject.toString(),
			JSONCompareMode.STRICT);
	}

	private Map<String, Object> _getBreadcrumbProps(
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
								"L_CMS_BASIC_WEB_CONTENT",
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
								"L_CMS_BASIC_DOCUMENT",
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
			"allowPropagate", false
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
					DepotRolesConstants.SUBTYPE_SPACE, 0, 0, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS),
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

	private JSONArray _getDepotEntriesJSONArray() throws Exception {
		return _getDepotEntriesJSONArray(
			_depotEntryLocalService.getDepotEntryGroupIds(
				group.getCompanyId(), TestPropsValues.getUserId(),
				DepotConstants.TYPE_SPACE));
	}

	private JSONArray _getDepotEntriesJSONArray(List<Long> groupIds) {
		if (ListUtil.isEmpty(groupIds)) {
			return null;
		}

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (Long groupId : groupIds) {
			JSONObject jsonObject = _getJSONObject(groupId);

			if (jsonObject != null) {
				jsonArray.put(jsonObject);
			}
		}

		return jsonArray;
	}

	private JSONObject _getExportFileFormatJSONObject(
		TranslationInfoItemFieldValuesExporter
			translationInfoItemFieldValuesExporter) {

		return JSONUtil.put(
			"displayName",
			() -> {
				InfoLocalizedValue<String> labelInfoLocalizedValue =
					translationInfoItemFieldValuesExporter.
						getLabelInfoLocalizedValue();

				return labelInfoLocalizedValue.getValue(
					themeDisplay.getLocale());
			}
		).put(
			"mimeType", translationInfoItemFieldValuesExporter.getMimeType()
		);
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

	private JSONArray _getLocalesJSONArray(
		Locale locale, Collection<Locale> locales) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		locales.forEach(
			currentLocale -> {
				String w3cLanguageId = LocaleUtil.toW3cLanguageId(
					currentLocale);

				jsonArray.put(
					JSONUtil.put(
						"displayName",
						LocaleUtil.getLocaleDisplayName(currentLocale, locale)
					).put(
						"id", LocaleUtil.toLanguageId(currentLocale)
					).put(
						"label", w3cLanguageId
					).put(
						"languageId", LocaleUtil.toLanguageId(currentLocale)
					).put(
						"name", currentLocale.getDisplayName()
					).put(
						"symbol", StringUtil.toLowerCase(w3cLanguageId)
					));
			});

		return jsonArray;
	}

	private Map<String, String> _getLocalizedKeysMap(Map<String, String> map) {
		Map<String, String> localizedKeysMap = new HashMap<>();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			localizedKeysMap.put(
				language.get(LocaleUtil.getDefault(), entry.getKey()),
				entry.getValue());
		}

		return localizedKeysMap;
	}

	private String _getRedirect(DropdownItem dropdownItem) {
		Map<String, Object> map = (Map<String, Object>)dropdownItem.get("data");

		if (map == null) {
			return null;
		}

		return (String)map.get("redirect");
	}

	private Role _getRoleWithPermissions(
			String[] actionIds, DepotEntry depotEntry)
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		ModelPermissions modelPermissions = ModelPermissionsFactory.create(
			HashMapBuilder.put(
				role.getName(), actionIds
			).build(),
			ObjectEntryFolder.class.getName());

		for (String objectEntryFolderExternalReferenceCode :
				_getRootObjectEntryFolderExternalReferenceCodes(
					getRootObjectEntryFolderExternalReferenceCode())) {

			ObjectEntryFolder objectEntryFolder =
				_objectEntryFolderLocalService.
					getObjectEntryFolderByExternalReferenceCode(
						objectEntryFolderExternalReferenceCode,
						depotEntry.getGroupId(), depotEntry.getCompanyId());

			ResourcePermissionLocalServiceUtil.addModelResourcePermissions(
				depotEntry.getCompanyId(), depotEntry.getGroupId(),
				TestPropsValues.getUserId(), ObjectEntryFolder.class.getName(),
				String.valueOf(objectEntryFolder.getObjectEntryFolderId()),
				modelPermissions);
		}

		return role;
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

	private String[] _getRootObjectEntryFolderExternalReferenceCodes(
		String rootObjectEntryFolderExternalReferenceCode) {

		if (rootObjectEntryFolderExternalReferenceCode == null) {
			return new String[] {
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES
			};
		}

		return new String[] {rootObjectEntryFolderExternalReferenceCode};
	}

	private void _setResourcePermissions(
			String[] actionIds, ObjectEntryFolder objectEntryFolder, Role role)
		throws Exception {

		ResourcePermissionLocalServiceUtil.setResourcePermissions(
			objectEntryFolder.getCompanyId(),
			objectEntryFolder.getModelClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntryFolder.getObjectEntryFolderId()),
			role.getRoleId(), actionIds);
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

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
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

	@Inject
	private TranslationInfoItemFieldValuesExporterRegistry
		_translationInfoItemFieldValuesExporterRegistry;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}