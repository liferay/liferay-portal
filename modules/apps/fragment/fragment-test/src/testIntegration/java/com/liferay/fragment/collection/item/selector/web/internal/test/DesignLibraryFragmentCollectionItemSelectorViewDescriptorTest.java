/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.item.selector.web.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.collection.item.selector.FragmentCollectionItemSelectorCriterion;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.ResultRowSplitter;
import com.liferay.portal.kernel.dao.search.ResultRowSplitterEntry;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
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
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
 * @author Gabriel Lima
 * @author Thiago Buarque
 */
@FeatureFlag("LPD-57283")
@RunWith(Arquillian.class)
public class DesignLibraryFragmentCollectionItemSelectorViewDescriptorTest {

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

		_group = GroupTestUtil.addGroup();

		_themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		_themeDisplay.setSiteGroupId(_group.getGroupId());
	}

	@Test
	@TestInfo("LPD-101911")
	public void testGetItemDescriptor() throws Exception {
		DepotEntry depotEntry = _addConnectedDesignLibraryDepotEntry();

		FragmentCollection designLibraryFragmentCollection =
			FragmentTestUtil.addFragmentCollection(depotEntry.getGroupId());

		ItemSelectorViewDescriptor<FragmentCollection>
			designLibraryItemSelectorViewDescriptor =
				_getItemSelectorViewDescriptor();

		ItemSelectorViewDescriptor.ItemDescriptor itemDescriptor =
			designLibraryItemSelectorViewDescriptor.getItemDescriptor(
				designLibraryFragmentCollection);

		Assert.assertEquals(
			designLibraryFragmentCollection.getName(),
			itemDescriptor.getTitle(LocaleUtil.getDefault()));

		Group group = _groupLocalService.getGroup(depotEntry.getGroupId());

		Assert.assertEquals(
			StringBundler.concat(
				"[", group.getDescriptiveName(LocaleUtil.getDefault()), "] ",
				designLibraryFragmentCollection.getName()),
			JSONFactoryUtil.createJSONObject(
				itemDescriptor.getPayload()
			).getString(
				"name"
			));

		FragmentCollection siteFragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		ItemSelectorViewDescriptor<FragmentCollection>
			siteItemSelectorViewDescriptor = _getItemSelectorViewDescriptor(
				_fragmentCollectionSiteItemSelectorView,
				Collections.emptyMap());

		ItemSelectorViewDescriptor.ItemDescriptor siteItemDescriptor =
			siteItemSelectorViewDescriptor.getItemDescriptor(
				siteFragmentCollection);

		Assert.assertEquals(
			siteFragmentCollection.getName(),
			JSONFactoryUtil.createJSONObject(
				siteItemDescriptor.getPayload()
			).getString(
				"name"
			));
	}

	@Test
	@TestInfo("LPD-101911")
	public void testGetResultRowSplitter() throws Exception {
		DepotEntry depotEntry1 = _addConnectedDesignLibraryDepotEntry(
			"<b>" + RandomTestUtil.randomString() + "</b>");

		FragmentCollection fragmentCollection1 =
			FragmentTestUtil.addFragmentCollection(depotEntry1.getGroupId());

		DepotEntry depotEntry2 = _addConnectedDesignLibraryDepotEntry();

		FragmentCollection fragmentCollection2 =
			FragmentTestUtil.addFragmentCollection(depotEntry2.getGroupId());

		ItemSelectorViewDescriptor<FragmentCollection>
			itemSelectorViewDescriptor = _getItemSelectorViewDescriptor();

		ResultRowSplitter resultRowSplitter =
			itemSelectorViewDescriptor.getResultRowSplitter();

		SearchContainer<FragmentCollection> searchContainer =
			itemSelectorViewDescriptor.getSearchContainer();

		List<ResultRowSplitterEntry> resultRowSplitterEntries =
			resultRowSplitter.split(
				TransformUtil.transform(
					searchContainer.getResults(),
					fragmentCollection ->
						new com.liferay.taglib.search.ResultRow(
							fragmentCollection, RandomTestUtil.randomLong(),
							RandomTestUtil.nextInt())));

		Assert.assertEquals(
			resultRowSplitterEntries.toString(), 2,
			resultRowSplitterEntries.size());

		List<Long> fragmentCollectionIds = new ArrayList<>();

		for (ResultRowSplitterEntry resultRowSplitterEntry :
				resultRowSplitterEntries) {

			for (ResultRow resultRow : resultRowSplitterEntry.getResultRows()) {
				FragmentCollection fragmentCollection =
					(FragmentCollection)resultRow.getObject();

				Group group = _groupLocalService.getGroup(
					fragmentCollection.getGroupId());

				Assert.assertEquals(
					HtmlUtil.escape(
						group.getDescriptiveName(LocaleUtil.getDefault())),
					resultRowSplitterEntry.getTitle());

				fragmentCollectionIds.add(
					fragmentCollection.getFragmentCollectionId());
			}
		}

		Assert.assertEquals(
			fragmentCollectionIds.toString(), 2, fragmentCollectionIds.size());
		Assert.assertTrue(
			fragmentCollectionIds.toString(),
			fragmentCollectionIds.contains(
				fragmentCollection1.getFragmentCollectionId()));
		Assert.assertTrue(
			fragmentCollectionIds.toString(),
			fragmentCollectionIds.contains(
				fragmentCollection2.getFragmentCollectionId()));
	}

	@Test
	@TestInfo("LPD-101911")
	public void testGetSearchContainer() throws Exception {
		DepotEntry depotEntry1 = _addConnectedDesignLibraryDepotEntry();

		FragmentTestUtil.addFragmentCollection(
			depotEntry1.getGroupId(), "Alpha");
		FragmentTestUtil.addFragmentCollection(
			depotEntry1.getGroupId(), "Charlie");

		DepotEntry depotEntry2 = _addConnectedDesignLibraryDepotEntry();

		FragmentTestUtil.addFragmentCollection(
			depotEntry2.getGroupId(), "Bravo");
		FragmentTestUtil.addFragmentCollection(
			depotEntry2.getGroupId(), "Delta");

		_assertSearchContainer(
			Arrays.asList(
				Arrays.asList("Alpha", "Charlie"),
				Arrays.asList("Bravo", "Delta")),
			Collections.emptyMap());
		_assertSearchContainer(
			Arrays.asList(Arrays.asList("Charlie")),
			Collections.singletonMap("keywords", "ar"));
		_assertSearchContainer(
			Arrays.asList(
				Arrays.asList("Charlie", "Alpha"),
				Arrays.asList("Delta", "Bravo")),
			Collections.singletonMap("orderByType", "desc"));
	}

	private DepotEntry _addConnectedDesignLibraryDepotEntry() throws Exception {
		return _addConnectedDesignLibraryDepotEntry(
			RandomTestUtil.randomString());
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

	private void _assertSearchContainer(
			List<List<String>> expectedPages, Map<String, String> parameters)
		throws Exception {

		for (int i = 0; i < expectedPages.size(); i++) {
			Assert.assertEquals(
				expectedPages.get(i),
				_getNames(parameters, String.valueOf(i + 1)));
		}
	}

	private ItemSelectorViewDescriptor<FragmentCollection>
			_getItemSelectorViewDescriptor()
		throws Exception {

		return _getItemSelectorViewDescriptor(Collections.emptyMap());
	}

	private ItemSelectorViewDescriptor<FragmentCollection>
			_getItemSelectorViewDescriptor(
				ItemSelectorView<FragmentCollectionItemSelectorCriterion>
					itemSelectorView,
				Map<String, String> parameters)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			"null-" + WebKeys.CURRENT_PORTLET_URL, new MockLiferayPortletURL());

		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			String key = entry.getKey();

			if (key.equals("cur") || key.equals("delta")) {
				mockLiferayPortletRenderRequest.setParameter(
					key, entry.getValue());
			}
			else {
				mockHttpServletRequest.setParameter(key, entry.getValue());
			}
		}

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			mockLiferayPortletRenderRequest);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());

		_themeDisplay.setLocale(LocaleUtil.getDefault());
		_themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		itemSelectorView.renderHTML(
			mockHttpServletRequest, new MockHttpServletResponse(),
			_fragmentCollectionItemSelectorCriterion,
			new MockLiferayPortletURL(), RandomTestUtil.randomString(), true);

		Object itemSelectorViewDescriptorRendererDisplayContext =
			mockHttpServletRequest.getAttribute(
				"com.liferay.item.selector.web.internal.display.context." +
					"ItemSelectorViewDescriptorRendererDisplayContext");

		return ReflectionTestUtil.invoke(
			itemSelectorViewDescriptorRendererDisplayContext,
			"getItemSelectorViewDescriptor", new Class<?>[0], null);
	}

	private ItemSelectorViewDescriptor<FragmentCollection>
			_getItemSelectorViewDescriptor(Map<String, String> parameters)
		throws Exception {

		return _getItemSelectorViewDescriptor(
			_fragmentCollectionDesignLibraryItemSelectorView, parameters);
	}

	private List<String> _getNames(Map<String, String> parameters, String cur)
		throws Exception {

		ItemSelectorViewDescriptor<FragmentCollection>
			itemSelectorViewDescriptor = _getItemSelectorViewDescriptor(
				HashMapBuilder.putAll(
					parameters
				).put(
					"cur", cur
				).put(
					"delta", "2"
				).build());

		SearchContainer<FragmentCollection> searchContainer =
			itemSelectorViewDescriptor.getSearchContainer();

		return TransformUtil.transform(
			searchContainer.getResults(), FragmentCollection::getName);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.fragment.collection.item.selector.web.internal.DesignLibraryFragmentCollectionItemSelectorView"
	)
	private ItemSelectorView<FragmentCollectionItemSelectorCriterion>
		_fragmentCollectionDesignLibraryItemSelectorView;

	private final FragmentCollectionItemSelectorCriterion
		_fragmentCollectionItemSelectorCriterion =
			new FragmentCollectionItemSelectorCriterion();

	@Inject(
		filter = "component.name=com.liferay.fragment.collection.item.selector.web.internal.SiteFragmentCollectionItemSelectorView"
	)
	private ItemSelectorView<FragmentCollectionItemSelectorCriterion>
		_fragmentCollectionSiteItemSelectorView;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private final ThemeDisplay _themeDisplay = new ThemeDisplay();

}