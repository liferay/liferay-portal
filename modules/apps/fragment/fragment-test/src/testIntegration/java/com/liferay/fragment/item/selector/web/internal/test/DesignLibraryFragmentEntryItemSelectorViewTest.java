/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.item.selector.web.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.item.selector.FragmentEntryItemSelectorCriterion;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.props.test.util.PropsTemporarySwapper;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Lourdes Fernández Besada
 */
@FeatureFlag("LPD-57283")
@RunWith(Arquillian.class)
public class DesignLibraryFragmentEntryItemSelectorViewTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		_fragmentEntryItemSelectorCriterion.setInputTypes(
			SetUtil.fromArray("text"));

		_group = GroupTestUtil.addGroup();

		_themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		_themeDisplay.setLocale(LocaleUtil.getDefault());
		_themeDisplay.setScopeGroupId(_group.getGroupId());
	}

	@Test
	@TestInfo("LPD-112439")
	public void testIsVisible() throws Exception {
		Assert.assertFalse(
			_designLibraryFragmentEntryItemSelectorView.isVisible(
				_fragmentEntryItemSelectorCriterion, _themeDisplay));

		_addConnectedDesignLibraryDepotEntry(RandomTestUtil.randomString());

		Assert.assertTrue(
			_designLibraryFragmentEntryItemSelectorView.isVisible(
				_fragmentEntryItemSelectorCriterion, _themeDisplay));

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					FeatureFlagConstants.getKey("LPD-57283"),
					Boolean.FALSE.toString())) {

			Assert.assertFalse(
				_designLibraryFragmentEntryItemSelectorView.isVisible(
					_fragmentEntryItemSelectorCriterion, _themeDisplay));
		}
	}

	@Test
	@TestInfo("LPD-112439")
	public void testRenderHTML() throws Exception {
		DepotEntry depotEntry1 = _addConnectedDesignLibraryDepotEntry(
			"Charlie");

		_addFragmentEntry("text", depotEntry1.getGroupId());

		DepotEntry depotEntry2 = _addConnectedDesignLibraryDepotEntry("Bravo");

		_addFragmentEntry("checkbox", depotEntry2.getGroupId());

		DepotEntry depotEntry3 = _addConnectedDesignLibraryDepotEntry("Alpha");

		_addFragmentEntry("text", depotEntry3.getGroupId());

		Assert.assertEquals(
			Long.valueOf(0),
			ReflectionTestUtil.invoke(
				_getFragmentEntriesDisplayContext(0), "getGroupId",
				new Class<?>[0]));
		Assert.assertEquals(
			Long.valueOf(0),
			ReflectionTestUtil.invoke(
				_getFragmentEntriesDisplayContext(_group.getGroupId()),
				"getGroupId", new Class<?>[0]));
		Assert.assertEquals(
			Long.valueOf(depotEntry3.getGroupId()),
			ReflectionTestUtil.invoke(
				_getFragmentEntriesDisplayContext(depotEntry3.getGroupId()),
				"getGroupId", new Class<?>[0]));

		SearchContainer<Group> groupsSearchContainer =
			ReflectionTestUtil.invoke(
				_getFragmentEntriesDisplayContext(0),
				"getGroupsSearchContainer", new Class<?>[0]);

		Assert.assertEquals(
			Arrays.asList("Alpha", "Charlie"),
			TransformUtil.transform(
				groupsSearchContainer.getResults(),
				group -> group.getName(LocaleUtil.getDefault())));
	}

	private DepotEntry _addConnectedDesignLibraryDepotEntry(String name)
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(LocaleUtil.getDefault(), name),
			Collections.emptyMap(), DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());

		return depotEntry;
	}

	private void _addFragmentEntry(String fieldType, long groupId)
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(groupId);

		_fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), groupId,
			fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_INPUT,
			JSONUtil.put(
				"fieldTypes", JSONUtil.put(fieldType)
			).toString(),
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	private Object _getFragmentEntriesDisplayContext(long selectedGroupId)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			"null-" + WebKeys.CURRENT_PORTLET_URL, new MockLiferayPortletURL());

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			mockLiferayPortletRenderRequest);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);
		mockHttpServletRequest.setParameter(
			"selectedGroupId", String.valueOf(selectedGroupId));

		_themeDisplay.setRequest(mockHttpServletRequest);

		_designLibraryFragmentEntryItemSelectorView.renderHTML(
			mockHttpServletRequest, new MockHttpServletResponse(),
			_fragmentEntryItemSelectorCriterion, new MockLiferayPortletURL(),
			RandomTestUtil.randomString(), false);

		return mockHttpServletRequest.getAttribute(
			"com.liferay.fragment.item.selector.web.internal.display.context." +
				"FragmentEntriesDisplayContext");
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.fragment.item.selector.web.internal.DesignLibraryFragmentEntryItemSelectorView"
	)
	private ItemSelectorView<FragmentEntryItemSelectorCriterion>
		_designLibraryFragmentEntryItemSelectorView;

	private final FragmentEntryItemSelectorCriterion
		_fragmentEntryItemSelectorCriterion =
			new FragmentEntryItemSelectorCriterion();

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	private Group _group;
	private final ThemeDisplay _themeDisplay = new ThemeDisplay();

}