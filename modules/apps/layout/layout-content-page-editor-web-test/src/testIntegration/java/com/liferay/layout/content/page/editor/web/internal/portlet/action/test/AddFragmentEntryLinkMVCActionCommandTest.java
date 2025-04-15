/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.exception.NoSuchEntryException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.content.page.editor.web.internal.portlet.constants.LayoutContentPageEditorWebPortletKeys;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
@Sync
public class AddFragmentEntryLinkMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_layout = layout.fetchDraftLayout();
	}

	@Test
	public void testAddFragmentEntryLink() throws Exception {
		_testAddFragmentEntryLink(_getFragmentEntry(_company.getGroupId()));
		_testAddFragmentEntryLink(_getFragmentEntry(_group.getGroupId()));

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(_group.getGroupId());

		mockLiferayPortletActionRequest.addParameter(
			"fragmentEntryKey", RandomTestUtil.randomString());

		try {
			ReflectionTestUtil.invoke(
				_mvcActionCommand, "addFragmentEntryLink",
				new Class<?>[] {ActionRequest.class},
				mockLiferayPortletActionRequest);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				NoSuchEntryException.class, exception.getClass());
		}
	}

	@Test
	public void testAddFragmentEntryLinkTwiceWithEmbeddedNoninstanceablePortlet()
		throws Exception {

		_assertAddFragmentEntryLinkTwiceWithEmbeddedNoninstanceablePortlet(
			_addFragmentEntry(
				StringBundler.concat(
					"<div><lfr-widget-",
					LayoutContentPageEditorWebPortletKeys.
						LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET_ALIAS,
					"></lfr-widget-",
					LayoutContentPageEditorWebPortletKeys.
						LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET_ALIAS,
					"></div>")),
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET,
			true);
	}

	@Test
	public void testAddFragmentEntryLinkTwiceWithEmbeddedNoninstanceablePortletWithItemMarkedForDeletion()
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry(
			StringBundler.concat(
				"<div><lfr-widget-",
				LayoutContentPageEditorWebPortletKeys.
					LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET_ALIAS,
				"></lfr-widget-",
				LayoutContentPageEditorWebPortletKeys.
					LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET_ALIAS,
				"></div>"));

		_assertAddFragmentEntryLinkWithEmbeddedPortlet(
			fragmentEntry, StringPool.BLANK,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET,
			true);

		_markForDeletionFragmentLayoutStructureItems(1);

		_assertAddFragmentEntryLinkWithEmbeddedPortlet(
			fragmentEntry, StringPool.BLANK,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET,
			true);

		_markForDeletionFragmentLayoutStructureItems(2);

		fragmentEntry = _addFragmentEntry(
			"<div>[@liferay_portlet.runtime portletName=\"" +
				LayoutContentPageEditorWebPortletKeys.
					LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET +
						"\"/]/div>");

		_assertAddFragmentEntryLinkWithEmbeddedPortlet(
			fragmentEntry, StringPool.BLANK,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET,
			false);
	}

	@Test
	public void testAddFragmentEntryLinkTwiceWithFreeMarkerEmbeddedNoninstanceablePortlet1()
		throws Exception {

		_assertAddFragmentEntryLinkTwiceWithEmbeddedNoninstanceablePortlet(
			_addFragmentEntry(
				"<div>[@liferay_portlet.runtime portletName=\"" +
					LayoutContentPageEditorWebPortletKeys.
						LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET +
							"\"/]/div>"),
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET,
			false);
	}

	@Test
	public void testAddFragmentEntryLinkTwiceWithFreeMarkerEmbeddedNoninstanceablePortlet2()
		throws Exception {

		_assertAddFragmentEntryLinkTwiceWithEmbeddedNoninstanceablePortlet(
			_addFragmentEntry(
				"<div>[@liferay_portlet[\"runtime\"] portletName=\"" +
					LayoutContentPageEditorWebPortletKeys.
						LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET +
							"\"/]/div>"),
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_NONINSTANCEABLE_TEST_PORTLET,
			false);
	}

	@Test
	public void testAddFragmentEntryLinkWithEmbeddedPortlet() throws Exception {
		String instanceId = RandomTestUtil.randomString();

		_assertAddFragmentEntryLinkWithEmbeddedPortlet(
			_addFragmentEntry(
				StringBundler.concat(
					"<div><lfr-widget-",
					LayoutContentPageEditorWebPortletKeys.
						LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET_ALIAS,
					" id=\"", instanceId, "\"></lfr-widget-",
					LayoutContentPageEditorWebPortletKeys.
						LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET_ALIAS,
					"></div>")),
			instanceId,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET,
			true);
		_assertAddFragmentEntryLinkWithEmbeddedPortlet(
			_addFragmentEntry(
				StringBundler.concat(
					"<div>[@liferay_portlet.runtime portletName=\"",
					LayoutContentPageEditorWebPortletKeys.
						LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET,
					"\" instanceId=\"${fragmentEntryLinkNamespace}", instanceId,
					"\"/]/div>")),
			instanceId,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET,
			true);
		_assertAddFragmentEntryLinkWithEmbeddedPortlet(
			_addFragmentEntry(
				StringBundler.concat(
					"<div>[@liferay_portlet[\"runtime\"] portletName=\"",
					LayoutContentPageEditorWebPortletKeys.
						LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET,
					"\" instanceId=\"${fragmentEntryLinkNamespace}", instanceId,
					"\"/]/div>")),
			instanceId,
			LayoutContentPageEditorWebPortletKeys.
				LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET,
			true);
	}

	private FragmentEntry _addFragmentEntry(String html) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				StringUtil.randomString(), StringPool.BLANK, serviceContext);

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(),
			StringUtil.randomString(), StringUtil.randomString(),
			RandomTestUtil.randomString(), html, RandomTestUtil.randomString(),
			false, "{fieldSets: []}", null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private void
			_assertAddFragmentEntryLinkTwiceWithEmbeddedNoninstanceablePortlet(
				FragmentEntry fragmentEntry, String portletId,
				boolean namespaced)
		throws Exception {

		_assertAddFragmentEntryLinkWithEmbeddedPortlet(
			fragmentEntry, StringPool.BLANK, portletId, namespaced);

		List<PortletPreferences> originalPortletPreferences =
			_portletPreferencesLocalService.getPortletPreferencesByPlid(
				_layout.getPlid());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(fragmentEntry.getGroupId());

		mockLiferayPortletActionRequest.addParameter(
			"fragmentEntryKey", fragmentEntry.getFragmentEntryKey());

		try {
			ReflectionTestUtil.invoke(
				_mvcActionCommand, "_processAddFragmentEntryLink",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			JSONObject jsonObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "processException",
				new Class<?>[] {ActionRequest.class, Exception.class},
				mockLiferayPortletActionRequest, modelListenerException);

			Assert.assertEquals(jsonObject.toString(), 1, jsonObject.length());

			Assert.assertEquals(
				_language.format(
					_portal.getSiteDefaultLocale(_group),
					"the-fragment-could-not-be-added-because-it-contains-a-" +
						"widget-x-that-can-only-appear-once-on-the-page",
					"Noninstanciable Test"),
				jsonObject.getString("error"));
		}

		List<PortletPreferences> portletPreferences =
			_portletPreferencesLocalService.getPortletPreferencesByPlid(
				_layout.getPlid());

		Assert.assertEquals(
			portletPreferences.toString(), originalPortletPreferences.size(),
			portletPreferences.size());
	}

	private void _assertAddFragmentEntryLinkWithEmbeddedPortlet(
			FragmentEntry fragmentEntry, String instanceId, String portletId,
			boolean namespaced)
		throws Exception {

		List<PortletPreferences> originalPortletPreferencesList =
			_portletPreferencesLocalService.getPortletPreferencesByPlid(
				_layout.getPlid());

		FragmentEntryLink fragmentEntryLink = _testAddFragmentEntryLink(
			fragmentEntry);

		List<PortletPreferences> portletPreferencesList = ListUtil.remove(
			_portletPreferencesLocalService.getPortletPreferencesByPlid(
				_layout.getPlid()),
			originalPortletPreferencesList);

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());

		PortletPreferences portletPreferences = portletPreferencesList.get(0);

		Assert.assertEquals(
			_getPortletId(fragmentEntryLink, instanceId, portletId, namespaced),
			portletPreferences.getPortletId());
	}

	private FragmentEntry _getFragmentEntry(long groupId) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), groupId,
				StringUtil.randomString(), StringPool.BLANK, serviceContext);

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), groupId,
			fragmentCollection.getFragmentCollectionId(),
			StringUtil.randomString(), StringUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			long groupId)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_company, _group, _layout);

		mockLiferayPortletActionRequest.addParameter(
			"groupId", String.valueOf(groupId));

		return mockLiferayPortletActionRequest;
	}

	private String _getPortletId(
		FragmentEntryLink fragmentEntryLink, String instanceId,
		String portletId, boolean namespaced) {

		if (namespaced) {
			instanceId = fragmentEntryLink.getNamespace() + instanceId;
		}

		return _portal.getJsSafePortletId(
			PortletIdCodec.encode(portletId, instanceId));
	}

	private void _markForDeletionFragmentLayoutStructureItems(int count)
		throws Exception {

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				_layout.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()));

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		Assert.assertEquals(
			MapUtil.toString(fragmentLayoutStructureItems), count,
			fragmentLayoutStructureItems.size());

		for (Map.Entry<Long, LayoutStructureItem> entry :
				fragmentLayoutStructureItems.entrySet()) {

			LayoutStructureItem layoutStructureItem = entry.getValue();

			if (layoutStructure.isItemMarkedForDeletion(
					layoutStructureItem.getItemId())) {

				continue;
			}

			ContentLayoutTestUtil.markItemForDeletionFromLayout(
				layoutStructureItem.getItemId(), _layout, StringPool.BLANK);
		}
	}

	private FragmentEntryLink _testAddFragmentEntryLink(
			FragmentEntry fragmentEntry)
		throws Exception {

		List<FragmentEntryLink> originalFragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_group.getGroupId(), _layout.getPlid());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(fragmentEntry.getGroupId());

		mockLiferayPortletActionRequest.addParameter(
			"fragmentEntryKey", fragmentEntry.getFragmentEntryKey());

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "_processAddFragmentEntryLink",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		JSONObject fragmentEntryLinkJSONObject = jsonObject.getJSONObject(
			"fragmentEntryLink");

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				GetterUtil.getLong(
					fragmentEntryLinkJSONObject.getString(
						"fragmentEntryLinkId")));

		Assert.assertNotNull(fragmentEntryLink);

		Assert.assertEquals(
			fragmentEntry.getFragmentEntryId(),
			fragmentEntryLink.getFragmentEntryId());
		Assert.assertEquals(_layout.getPlid(), fragmentEntryLink.getPlid());
		Assert.assertEquals(fragmentEntry.getCss(), fragmentEntryLink.getCss());
		Assert.assertEquals(
			fragmentEntry.getHtml(), fragmentEntryLink.getHtml());
		Assert.assertEquals(fragmentEntry.getJs(), fragmentEntryLink.getJs());
		Assert.assertEquals(
			fragmentEntry.getConfiguration(),
			fragmentEntryLink.getConfiguration());
		Assert.assertEquals(
			StringPool.BLANK, fragmentEntryLink.getRendererKey());

		List<FragmentEntryLink> actualFragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_group.getGroupId(), _layout.getPlid());

		Assert.assertEquals(
			actualFragmentEntryLinks.toString(),
			originalFragmentEntryLinks.size() + 1,
			actualFragmentEntryLinks.size());

		return fragmentEntryLink;
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	private Layout _layout;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/add_fragment_entry_link"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}