/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.client.extension.util.CETUtil;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.info.field.InfoField;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
public class FragmentEntryLinkManagerTest {

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
	public void testActionsForNonexistentPortlet() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(mockHttpServletRequest, draftLayout));

		ClientExtensionEntry clientExtensionEntry =
			_clientExtensionEntryLocalService.addClientExtensionEntry(
				null, TestPropsValues.getUserId(), StringPool.BLANK,
				Collections.singletonMap(
					LocaleUtil.fromLanguageId(
						UpgradeProcessUtil.getDefaultLanguageId(
							_group.getCompanyId())),
					RandomTestUtil.randomString()),
				StringPool.BLANK, StringPool.BLANK,
				ClientExtensionEntryConstants.TYPE_CUSTOM_ELEMENT,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"htmlElementName", "valid-html-element-name"
				).put(
					"instanceable", false
				).put(
					"urls", "http://" + RandomTestUtil.randomString() + ".com"
				).buildString());

		String portletId = StringBundler.concat(
			"com_liferay_client_extension_web_internal_portlet_",
			"ClientExtensionEntryPortlet_", TestPropsValues.getCompanyId(), "_",
			CETUtil.normalizeExternalReferenceCodeForPortletId(
				clientExtensionEntry.getExternalReferenceCode()));

		JSONObject processAddPortletJSONObject =
			ContentLayoutTestUtil.addPortletToLayout(draftLayout, portletId);

		JSONObject fragmentEntryLinkJSONObject =
			processAddPortletJSONObject.getJSONObject("fragmentEntryLink");

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLinkJSONObject.getLong("fragmentEntryLinkId"));

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					draftLayout.getGroupId(), draftLayout.getPlid());

		Assert.assertNotNull(layoutPageTemplateStructure);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		fragmentEntryLinkJSONObject = ReflectionTestUtil.invoke(
			_fragmentEntryLinkManager, "getFragmentEntryLinkJSONObject",
			new Class<?>[] {
				DefaultFragmentRendererContext.class, FragmentEntryLink.class,
				HttpServletRequest.class, HttpServletResponse.class,
				LayoutStructure.class
			},
			new DefaultFragmentRendererContext(fragmentEntryLink),
			fragmentEntryLink, mockHttpServletRequest,
			new MockHttpServletResponse(), layoutStructure);

		JSONObject actionsJSONObject =
			fragmentEntryLinkJSONObject.getJSONObject("actions");

		Assert.assertFalse(SetUtil.isEmpty(actionsJSONObject.keySet()));

		_clientExtensionEntryLocalService.deleteClientExtensionEntry(
			clientExtensionEntry);

		fragmentEntryLinkJSONObject = ReflectionTestUtil.invoke(
			_fragmentEntryLinkManager, "getFragmentEntryLinkJSONObject",
			new Class<?>[] {
				DefaultFragmentRendererContext.class, FragmentEntryLink.class,
				HttpServletRequest.class, HttpServletResponse.class,
				LayoutStructure.class
			},
			new DefaultFragmentRendererContext(fragmentEntryLink),
			fragmentEntryLink, mockHttpServletRequest,
			new MockHttpServletResponse(), layoutStructure);

		actionsJSONObject = fragmentEntryLinkJSONObject.getJSONObject(
			"actions");

		Assert.assertTrue(SetUtil.isEmpty(actionsJSONObject.keySet()));

		Assert.assertEquals(
			_portal.getPortletTitle(
				portletId, LocaleThreadLocal.getSiteDefaultLocale()),
			fragmentEntryLinkJSONObject.getString("name"));
		Assert.assertEquals(
			portletId, fragmentEntryLinkJSONObject.getString("portletId"));
	}

	@Test
	public void testInputLabelWithSiteDefaultLocaleDifferentFromUserLocale()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "First Name",
						"firstName")));

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, objectDefinition.getClassName());

		InfoForm infoForm = infoItemFormProvider.getInfoForm(
			StringPool.BLANK, _group.getGroupId());

		List<InfoField<?>> allInfoFields = ListUtil.filter(
			infoForm.getAllInfoFields(), InfoField::isEditable);

		JSONObject jsonObject = ContentLayoutTestUtil.addFormToLayout(
			false,
			String.valueOf(
				_portal.getClassNameId(objectDefinition.getClassName())),
			"0", layout.fetchDraftLayout(), _layoutStructureProvider,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()),
			allInfoFields.toArray(new InfoField<?>[0]));

		LayoutStructure layoutStructure = (LayoutStructure)jsonObject.get(
			"layoutData");

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		Set<Long> keySet = fragmentLayoutStructureItems.keySet();

		Iterator<Long> iterator = keySet.iterator();

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				iterator.next());

		JSONObject editableValuesJSONObject = _jsonFactory.createJSONObject(
			fragmentEntryLink.getEditableValues());

		JSONObject freeMarkerEntryProcessorJSONObject =
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		String inputLabel = RandomTestUtil.randomString();

		freeMarkerEntryProcessorJSONObject.put(
			"inputLabel",
			JSONUtil.put(
				LocaleUtil.toLanguageId(LocaleUtil.SPAIN), inputLabel));

		fragmentEntryLink =
			_fragmentEntryLinkLocalService.updateFragmentEntryLink(
				TestPropsValues.getUserId(),
				fragmentEntryLink.getFragmentEntryLinkId(),
				editableValuesJSONObject.toString());

		Locale originalLocale = LocaleThreadLocal.getSiteDefaultLocale();

		try {
			LocaleThreadLocal.setSiteDefaultLocale(LocaleUtil.SPAIN);

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY,
				_getThemeDisplay(mockHttpServletRequest, layout));

			JSONObject fragmentEntryLinkJSONObject = ReflectionTestUtil.invoke(
				_fragmentEntryLinkManager, "getFragmentEntryLinkJSONObject",
				new Class<?>[] {
					DefaultFragmentRendererContext.class,
					FragmentEntryLink.class, HttpServletRequest.class,
					HttpServletResponse.class, LayoutStructure.class
				},
				new DefaultFragmentRendererContext(fragmentEntryLink),
				fragmentEntryLink, mockHttpServletRequest,
				new MockHttpServletResponse(), layoutStructure);

			String content = fragmentEntryLinkJSONObject.getString("content");

			Assert.assertTrue(content.contains(inputLabel));
		}
		finally {
			LocaleThreadLocal.setDefaultLocale(originalLocale);
		}
	}

	private ThemeDisplay _getThemeDisplay(
			HttpServletRequest httpServletRequest, Layout layout)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(layout.getCompanyId()));

		User user = TestPropsValues.getUser();

		themeDisplay.setLanguageId(LocaleUtil.toLanguageId(user.getLocale()));

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());
		themeDisplay.setLocale(user.getLocale());

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());

		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPlid(layout.getPlid());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteDefaultLocale(
			LocaleThreadLocal.getSiteDefaultLocale());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private ClientExtensionEntryLocalService _clientExtensionEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject(
		filter = "component.name=com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager",
		type = Inject.NoType.class
	)
	private Object _fragmentEntryLinkManager;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}