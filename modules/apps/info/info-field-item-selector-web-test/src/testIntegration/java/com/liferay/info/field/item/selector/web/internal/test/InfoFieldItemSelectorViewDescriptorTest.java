/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.field.item.selector.web.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.item.selector.InfoFieldItemSelectorCriterion;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.ResultRowSplitter;
import com.liferay.portal.kernel.dao.search.ResultRowSplitterEntry;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.taglib.search.ResultRow;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
public class InfoFieldItemSelectorViewDescriptorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_objectDefinition1 = ObjectDefinitionTestUtil.publishObjectDefinition(
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE,
					ObjectFieldConstants.DB_TYPE_DATE, "myDate", "myDate",
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
					ObjectFieldConstants.DB_TYPE_BOOLEAN, "myBoolean",
					"myBoolean", false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, "myRichText",
					"myRichText", false)));
	}

	@Test
	public void testGetResultRowSplitter() throws Exception {
		_objectDefinition2 = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, "myText", "myText",
					false)));

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, _objectDefinition2,
				_objectDefinition1);

		ItemSelectorViewDescriptor<Object> itemSelectorViewDescriptor =
			_getItemSelectorViewDescriptor(new MockHttpServletRequest());

		ResultRowSplitter resultRowSplitter =
			itemSelectorViewDescriptor.getResultRowSplitter();

		SearchContainer<Object> searchContainer =
			itemSelectorViewDescriptor.getSearchContainer();

		List<ResultRowSplitterEntry> resultRowSplitterEntries =
			resultRowSplitter.split(
				TransformUtil.transform(
					searchContainer.getResults(),
					object -> new ResultRow(
						object, RandomTestUtil.randomLong(),
						RandomTestUtil.nextInt())));

		Assert.assertEquals(
			resultRowSplitterEntries.toString(), 3,
			resultRowSplitterEntries.size());

		ResultRowSplitterEntry resultRowSplitterEntry1 =
			resultRowSplitterEntries.get(0);

		Assert.assertEquals(
			_language.get(LocaleUtil.getSiteDefault(), "basic-information"),
			resultRowSplitterEntry1.getTitle());

		ResultRowSplitterEntry resultRowSplitterEntry2 =
			resultRowSplitterEntries.get(1);

		Assert.assertEquals(
			_objectDefinition1.getLabel(LocaleUtil.getSiteDefault()),
			resultRowSplitterEntry2.getTitle());

		ResultRowSplitterEntry resultRowSplitterEntry3 =
			resultRowSplitterEntries.get(2);

		Assert.assertEquals(
			StringBundler.concat(
				objectRelationship.getLabel(LocaleUtil.getSiteDefault()), " (",
				_objectDefinition2.getLabel(LocaleUtil.getSiteDefault()), ")"),
			resultRowSplitterEntry3.getTitle());
	}

	@Test
	public void testGetSearchContainer() throws Exception {
		ItemSelectorViewDescriptor<Object> itemSelectorViewDescriptor =
			_getItemSelectorViewDescriptor(new MockHttpServletRequest());

		SearchContainer<Object> searchContainer =
			itemSelectorViewDescriptor.getSearchContainer();

		Assert.assertEquals(4, searchContainer.getTotal());
	}

	@Test
	public void testGetSearchContainerWithFormTypeMultistep() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Layout draftLayout = _layout.fetchDraftLayout();

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem formStyledLayoutStructureItem =
			layoutStructure.addFormStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem formStepContainerStyledLayoutStructureItem =
			layoutStructure.addFormStepContainerStyledLayoutStructureItem(
				formStyledLayoutStructureItem.getItemId(), 0);

		layoutStructure.addFormStepLayoutStructureItem(
			formStepContainerStyledLayoutStructureItem.getItemId(), 0);
		layoutStructure.addFormStepLayoutStructureItem(
			formStepContainerStyledLayoutStructureItem.getItemId(), 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _group.getGroupId(),
				draftLayout.getPlid(), defaultSegmentsExperienceId,
				layoutStructure.toString());

		mockHttpServletRequest.setParameter(
			"formItemId", formStyledLayoutStructureItem.getItemId());

		ItemSelectorViewDescriptor<Object> itemSelectorViewDescriptor =
			_getItemSelectorViewDescriptor(mockHttpServletRequest);

		SearchContainer<Object> searchContainer =
			itemSelectorViewDescriptor.getSearchContainer();

		Assert.assertEquals(4, searchContainer.getTotal());
	}

	@Test
	public void testGetSearchContainerWithKeywords() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("keywords", "myRichText");

		ItemSelectorViewDescriptor<Object> itemSelectorViewDescriptor =
			_getItemSelectorViewDescriptor(mockHttpServletRequest);

		SearchContainer<Object> searchContainer =
			itemSelectorViewDescriptor.getSearchContainer();

		Assert.assertEquals(1, searchContainer.getTotal());
	}

	@Test
	public void testGetSearchContainerWithRelationship() throws Exception {
		_objectDefinition2 = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, "myText", "myText",
					false)));

		ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, _objectDefinition2,
			_objectDefinition1);

		ItemSelectorViewDescriptor<Object> itemSelectorViewDescriptor =
			_getItemSelectorViewDescriptor(new MockHttpServletRequest());

		SearchContainer<Object> searchContainer =
			itemSelectorViewDescriptor.getSearchContainer();

		Assert.assertEquals(6, searchContainer.getTotal());
	}

	@Test
	public void testRowCheckerWithCheckedInfoFields() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, _objectDefinition1.getClassName());

		InfoForm infoForm = infoItemFormProvider.getInfoForm(
			StringPool.BLANK, _group.getGroupId());

		List<InfoField<?>> allInfoFields = ListUtil.filter(
			infoForm.getAllInfoFields(), InfoField::isEditable);

		Layout draftLayout = _layout.fetchDraftLayout();

		JSONObject jsonObject = ContentLayoutTestUtil.addFormToLayout(
			false,
			String.valueOf(
				_portal.getClassNameId(_objectDefinition1.getClassName())),
			"0", draftLayout, _layoutStructureProvider,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()),
			allInfoFields.toArray(new InfoField<?>[0]));

		mockHttpServletRequest.setParameter(
			"formItemId", jsonObject.getString("addedItemId"));

		ItemSelectorViewDescriptor<Object> itemSelectorViewDescriptor =
			_getItemSelectorViewDescriptor(mockHttpServletRequest);

		SearchContainer<Object> searchContainer =
			itemSelectorViewDescriptor.getSearchContainer();

		RowChecker rowChecker = searchContainer.getRowChecker();

		for (Object infoField : searchContainer.getResults()) {
			Assert.assertTrue(rowChecker.isChecked(infoField));
		}
	}

	@Test
	public void testRowCheckerWithoutCheckedInfoFields() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		JSONObject jsonObject = ContentLayoutTestUtil.addFormToLayout(
			false,
			String.valueOf(
				_portal.getClassNameId(_objectDefinition1.getClassName())),
			"0", _layout, _layoutStructureProvider,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid()),
			new InfoField<?>[0]);

		mockHttpServletRequest.setParameter(
			"formItemId", jsonObject.getString("addedItemId"));

		ItemSelectorViewDescriptor<Object> itemSelectorViewDescriptor =
			_getItemSelectorViewDescriptor(mockHttpServletRequest);

		SearchContainer<Object> searchContainer =
			itemSelectorViewDescriptor.getSearchContainer();

		RowChecker rowChecker = searchContainer.getRowChecker();

		for (Object infoField : searchContainer.getResults()) {
			Assert.assertFalse(rowChecker.isChecked(infoField));
		}
	}

	private ItemSelectorViewDescriptor<Object> _getItemSelectorViewDescriptor(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

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
			WebKeys.THEME_DISPLAY, _getThemeDisplay(mockHttpServletRequest));

		mockHttpServletRequest.setParameter(
			"itemType", _objectDefinition1.getClassName());

		Layout draftLayout = _layout.fetchDraftLayout();

		mockHttpServletRequest.setParameter(
			"segmentsExperienceId",
			String.valueOf(
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(draftLayout.getPlid())));

		_infoFieldProviderItemSelectorView.renderHTML(
			mockHttpServletRequest, new MockHttpServletResponse(),
			new InfoFieldItemSelectorCriterion(), new MockLiferayPortletURL(),
			RandomTestUtil.randomString(), true);

		Object itemSelectorViewDescriptorRendererDisplayContext =
			mockHttpServletRequest.getAttribute(
				"com.liferay.item.selector.web.internal.display.context." +
					"ItemSelectorViewDescriptorRendererDisplayContext");

		return ReflectionTestUtil.invoke(
			itemSelectorViewDescriptorRendererDisplayContext,
			"getItemSelectorViewDescriptor", new Class<?>[0], null);
	}

	private ThemeDisplay _getThemeDisplay(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(_group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.info.field.item.selector.web.internal.InfoFieldProviderItemSelectorView",
		type = ItemSelectorView.class
	)
	private ItemSelectorView<ItemSelectorCriterion>
		_infoFieldProviderItemSelectorView;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private Language _language;

	private Layout _layout;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition1;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition2;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}