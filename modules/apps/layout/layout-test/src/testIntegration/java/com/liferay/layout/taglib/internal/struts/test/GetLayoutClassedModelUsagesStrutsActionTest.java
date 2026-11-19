/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletLayoutListener;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class GetLayoutClassedModelUsagesStrutsActionTest {

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
	@TestInfo("LPS-88500")
	public void testExecute() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			journalArticle.getGroupId(), StringPool.BLANK,
			_portal.getClassNameId(JournalArticle.class),
			journalArticle.getResourcePrimKey(), RandomTestUtil.randomString(),
			0, 0, new ServiceContext());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_getLayoutClassedModelUsages.execute(
			_getMockHttpServletRequest(
				JournalArticle.class.getName(),
				journalArticle.getResourcePrimKey()),
			mockHttpServletResponse);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		JSONArray jsonArray = jsonObject.getJSONArray("usages");

		Assert.assertEquals(1, jsonArray.length());

		_user = UserTestUtil.addUser();

		_testExecuteWithoutModelViewPermission();
		_testExecuteWithoutLayoutViewPermission();
		_testExecuteWithoutInfoItemPermissionProvider();
		_testExecuteWithWidgets();
	}

	private LayoutClassedModelUsage _addLayoutClassedModelUsage(
		JournalArticle journalArticle, long plid) {

		return _layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			journalArticle.getGroupId(), StringPool.BLANK,
			_portal.getClassNameId(JournalArticle.class),
			journalArticle.getResourcePrimKey(), RandomTestUtil.randomString(),
			_portal.getClassNameId(FragmentEntryLink.class), plid,
			new ServiceContext());
	}

	private String _addPortletToLayout(
			Layout layout, String portletId,
			Map<String, String[]> preferenceMap)
		throws Exception {

		String newPortletId = LayoutTestUtil.addPortletToLayout(
			layout, portletId, preferenceMap);

		Portlet portlet = _portletLocalService.getPortletById(
			layout.getCompanyId(), newPortletId);

		PortletLayoutListener portletLayoutListener =
			portlet.getPortletLayoutListenerInstance();

		portletLayoutListener.onSetup(newPortletId, layout.getPlid());

		return newPortletId;
	}

	private JSONObject _execute(JournalArticle journalArticle)
		throws Exception {

		return _execute(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());
	}

	private JSONObject _execute(String className, long classPK)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_getLayoutClassedModelUsages.execute(
			_getMockHttpServletRequest(className, classPK),
			mockHttpServletResponse);

		return _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());
	}

	private String _getAssetEntryXML(String assetEntryUuid) throws Exception {
		Document document = SAXReaderUtil.createDocument(StringPool.UTF8);

		Element assetEntryElement = document.addElement("asset-entry");

		assetEntryElement.addElement("asset-entry-type");

		Element assetEntryUuidElement = assetEntryElement.addElement(
			"asset-entry-uuid");

		assetEntryUuidElement.addText(assetEntryUuid);

		return document.formattedString(StringPool.BLANK);
	}

	private JSONObject _getJSONObject(
		JSONArray jsonArray, LayoutClassedModelUsage layoutClassedModelUsage) {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (jsonObject.getLong("id") ==
					layoutClassedModelUsage.getLayoutClassedModelUsageId()) {

				return jsonObject;
			}
		}

		return null;
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			String className, long classPK)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter("className", className);
		mockHttpServletRequest.addParameter("classPK", String.valueOf(classPK));
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			ContentLayoutTestUtil.getThemeDisplay(
				_companyLocalService.getCompany(_group.getCompanyId()), _group,
				_layoutLocalService.getLayout(
					_portal.getControlPanelPlid(_group.getCompanyId()))));

		return mockHttpServletRequest;
	}

	private String _getUsageKey(String containerKey, long plid) {
		return plid + StringPool.POUND + containerKey;
	}

	private List<String> _getUsageKeys(JournalArticle journalArticle) {
		return ListUtil.sort(
			TransformUtil.transform(
				_layoutClassedModelUsageLocalService.
					getLayoutClassedModelUsages(
						_portal.getClassNameId(JournalArticle.class),
						journalArticle.getResourcePrimKey()),
				layoutClassedModelUsage -> _getUsageKey(
					layoutClassedModelUsage.getContainerKey(),
					layoutClassedModelUsage.getPlid())));
	}

	private void _testExecuteWithoutInfoItemPermissionProvider()
		throws Exception {

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(),
			ContentTypes.APPLICATION_OCTET_STREAM, RandomTestUtil.randomBytes(),
			null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			_group.getGroupId(), StringPool.BLANK,
			_portal.getClassNameId(DLFileEntry.class),
			fileEntry.getFileEntryId(), RandomTestUtil.randomString(),
			_portal.getClassNameId(FragmentEntryLink.class), layout.getPlid(),
			new ServiceContext());

		JSONObject jsonObject = _execute(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId());

		JSONArray jsonArray = jsonObject.getJSONArray("usages");

		Assert.assertEquals(1, jsonArray.length());

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(fileEntry.getFileEntryId()), ActionKeys.VIEW);
		RoleTestUtil.removeResourcePermission(
			RoleConstants.SITE_MEMBER, DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(fileEntry.getFileEntryId()), ActionKeys.VIEW);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		jsonObject = _execute(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId());

		jsonArray = jsonObject.getJSONArray("usages");

		Assert.assertEquals(0, jsonArray.length());

		PermissionThreadLocal.setPermissionChecker(permissionChecker);
	}

	private void _testExecuteWithoutLayoutViewPermission() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group, true);

		LayoutClassedModelUsage layoutLayoutClassedModelUsage =
			_addLayoutClassedModelUsage(journalArticle, layout.getPlid());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(), LayoutPageTemplateEntryTypeConstants.BASIC,
				WorkflowConstants.STATUS_APPROVED);

		LayoutClassedModelUsage layoutPageTemplateLayoutClassedModelUsage =
			_addLayoutClassedModelUsage(
				journalArticle, layoutPageTemplateEntry.getPlid());

		JSONObject jsonObject = _execute(journalArticle);

		JSONArray jsonArray = jsonObject.getJSONArray("usages");

		JSONObject layoutJSONObject = _getJSONObject(
			jsonArray, layoutLayoutClassedModelUsage);

		Assert.assertEquals(
			layout.getName(LocaleUtil.getSiteDefault()),
			layoutJSONObject.getString("name"));
		Assert.assertTrue(layoutJSONObject.has("url"));

		JSONObject layoutPageTemplateJSONObject = _getJSONObject(
			jsonArray, layoutPageTemplateLayoutClassedModelUsage);

		Assert.assertEquals(
			layoutPageTemplateEntry.getName(),
			layoutPageTemplateJSONObject.getString("name"));
		Assert.assertTrue(layoutPageTemplateJSONObject.has("url"));

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		jsonObject = _execute(journalArticle);

		jsonArray = jsonObject.getJSONArray("usages");

		Assert.assertEquals(2, jsonArray.length());

		String restrictedPageLabel = StringBundler.concat(
			StringPool.OPEN_BRACKET,
			_language.get(LocaleUtil.getSiteDefault(), "restricted-page"),
			StringPool.CLOSE_BRACKET);

		layoutJSONObject = _getJSONObject(
			jsonArray, layoutLayoutClassedModelUsage);

		Assert.assertEquals(
			restrictedPageLabel, layoutJSONObject.getString("name"));
		Assert.assertFalse(layoutJSONObject.has("url"));

		layoutPageTemplateJSONObject = _getJSONObject(
			jsonArray, layoutPageTemplateLayoutClassedModelUsage);

		Assert.assertEquals(
			restrictedPageLabel,
			layoutPageTemplateJSONObject.getString("name"));
		Assert.assertFalse(layoutPageTemplateJSONObject.has("url"));

		_userLocalService.addGroupUser(_group.getGroupId(), _user.getUserId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		jsonObject = _execute(journalArticle);

		jsonArray = jsonObject.getJSONArray("usages");

		layoutPageTemplateJSONObject = _getJSONObject(
			jsonArray, layoutPageTemplateLayoutClassedModelUsage);

		Assert.assertEquals(
			layoutPageTemplateEntry.getName(),
			layoutPageTemplateJSONObject.getString("name"));
		Assert.assertTrue(layoutPageTemplateJSONObject.has("url"));

		PermissionThreadLocal.setPermissionChecker(permissionChecker);
	}

	private void _testExecuteWithoutModelViewPermission() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_addLayoutClassedModelUsage(journalArticle, layout.getPlid());

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, JournalArticle.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(journalArticle.getResourcePrimKey()),
			ActionKeys.VIEW);

		JSONObject jsonObject = _execute(journalArticle);

		JSONArray jsonArray = jsonObject.getJSONArray("usages");

		Assert.assertEquals(1, jsonArray.length());

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		jsonObject = _execute(journalArticle);

		Assert.assertEquals(0, jsonObject.getInt("totalNumberOfPages"));

		jsonArray = jsonObject.getJSONArray("usages");

		Assert.assertEquals(0, jsonArray.length());

		PermissionThreadLocal.setPermissionChecker(permissionChecker);
	}

	private void _testExecuteWithWidgets() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Layout journalContentLayout = LayoutTestUtil.addTypePortletLayout(
			_group);
		Map<String, String[]> journalContentPreferenceMap = HashMapBuilder.put(
			"articleExternalReferenceCode",
			new String[] {journalArticle.getExternalReferenceCode()}
		).build();

		String journalContentPortletId = _addPortletToLayout(
			journalContentLayout, JournalContentPortletKeys.JOURNAL_CONTENT,
			journalContentPreferenceMap);

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		Map<String, String[]> assetPublisherPreferenceMap = HashMapBuilder.put(
			"assetEntryXml",
			new String[] {_getAssetEntryXML(assetEntry.getClassUuid())}
		).put(
			"selectionStyle", new String[] {"manual"}
		).build();

		Layout assetPublisherLayout = LayoutTestUtil.addTypePortletLayout(
			_group);

		String assetPublisherPortletId = _addPortletToLayout(
			assetPublisherLayout, AssetPublisherPortletKeys.ASSET_PUBLISHER,
			assetPublisherPreferenceMap);

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		String layoutAssetPublisherPortletId = _addPortletToLayout(
			layout, AssetPublisherPortletKeys.ASSET_PUBLISHER,
			assetPublisherPreferenceMap);
		String layoutJournalContentPortletId = _addPortletToLayout(
			layout, JournalContentPortletKeys.JOURNAL_CONTENT,
			journalContentPreferenceMap);

		Layout unusedLayout = LayoutTestUtil.addTypePortletLayout(_group);

		_addPortletToLayout(
			unusedLayout, AssetPublisherPortletKeys.ASSET_PUBLISHER, null);
		_addPortletToLayout(
			unusedLayout, JournalContentPortletKeys.JOURNAL_CONTENT, null);

		Assert.assertEquals(
			ListUtil.sort(
				Arrays.asList(
					_getUsageKey(
						assetPublisherPortletId,
						assetPublisherLayout.getPlid()),
					_getUsageKey(
						journalContentPortletId,
						journalContentLayout.getPlid()),
					_getUsageKey(
						layoutAssetPublisherPortletId, layout.getPlid()),
					_getUsageKey(
						layoutJournalContentPortletId, layout.getPlid()))),
			_getUsageKeys(journalArticle));

		JSONObject jsonObject = _execute(journalArticle);

		JSONArray jsonArray = jsonObject.getJSONArray("usages");

		Assert.assertEquals(
			ListUtil.sort(
				Arrays.asList(
					assetPublisherLayout.getName(LocaleUtil.getDefault()),
					journalContentLayout.getName(LocaleUtil.getDefault()),
					layout.getName(LocaleUtil.getDefault()),
					layout.getName(LocaleUtil.getDefault()))),
			ListUtil.sort(JSONUtil.toStringList(jsonArray, "name")));
		Assert.assertEquals(
			Arrays.asList("Page", "Page", "Page", "Page"),
			JSONUtil.toStringList(jsonArray, "type"));

		layout = _layoutLocalService.getLayout(layout.getPlid());

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		layoutTypePortlet.removePortletId(
			TestPropsValues.getUserId(), layoutAssetPublisherPortletId);
		layoutTypePortlet.removePortletId(
			TestPropsValues.getUserId(), layoutJournalContentPortletId);

		_layoutLocalService.updateTypeSettings(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());

		Assert.assertEquals(
			ListUtil.sort(
				Arrays.asList(
					_getUsageKey(
						assetPublisherPortletId,
						assetPublisherLayout.getPlid()),
					_getUsageKey(
						journalContentPortletId,
						journalContentLayout.getPlid()))),
			_getUsageKeys(journalArticle));

		ServiceContextThreadLocal.popServiceContext();
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject(filter = "path=/portal/get_layout_classed_model_usages")
	private StrutsAction _getLayoutClassedModelUsages;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Language _language;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private PortletLocalService _portletLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}