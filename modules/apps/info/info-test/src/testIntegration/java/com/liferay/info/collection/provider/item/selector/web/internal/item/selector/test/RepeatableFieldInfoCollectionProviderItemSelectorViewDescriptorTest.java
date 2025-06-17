/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.collection.provider.item.selector.web.internal.item.selector.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.collection.provider.item.selector.RepeatableFieldInfoCollectionProviderItemSelectorCriterion;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.provider.RepeatableFieldsInfoItemFormProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
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
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class
	RepeatableFieldInfoCollectionProviderItemSelectorViewDescriptorTest {

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
	public void testItemSelectorViewDescriptorWithEntries() throws Exception {
		ServiceRegistration<RepeatableFieldsInfoItemFormProvider>
			serviceRegistration = _registerService();

		try {
			ItemSelectorViewDescriptor<Object> itemSelectorViewDescriptor =
				_getItemSelectorViewDescriptor();

			SearchContainer<Object> searchContainer =
				itemSelectorViewDescriptor.getSearchContainer();

			List<Object> results = searchContainer.getResults();

			Assert.assertEquals(1, searchContainer.getTotal());

			InfoFieldSetEntry infoFieldSetEntry =
				(InfoFieldSetEntry)results.get(0);

			Assert.assertEquals("TestItemField1", infoFieldSetEntry.getName());
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testItemSelectorViewDescriptorWithNoEntries() throws Exception {
		ItemSelectorViewDescriptor<Object> itemSelectorViewDescriptor =
			_getItemSelectorViewDescriptor();

		SearchContainer<Object> searchContainer =
			itemSelectorViewDescriptor.getSearchContainer();

		Assert.assertEquals(0, searchContainer.getTotal());
	}

	public static class TestItem {
	}

	private ItemSelectorViewDescriptor<Object> _getItemSelectorViewDescriptor()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			"null-" + WebKeys.CURRENT_PORTLET_URL, new MockLiferayPortletURL());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			mockLiferayPortletRenderRequest);
		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(mockHttpServletRequest));

		RepeatableFieldInfoCollectionProviderItemSelectorCriterion
			repeatableFieldInfoCollectionProviderItemSelectorCriterion =
				new RepeatableFieldInfoCollectionProviderItemSelectorCriterion();

		repeatableFieldInfoCollectionProviderItemSelectorCriterion.setItemType(
			TestItem.class.getName());

		_infoFieldProviderItemSelectorView.renderHTML(
			mockHttpServletRequest, new MockHttpServletResponse(),
			repeatableFieldInfoCollectionProviderItemSelectorCriterion,
			new MockLiferayPortletURL(), RandomTestUtil.randomString(), true);

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

	private ServiceRegistration<RepeatableFieldsInfoItemFormProvider>
		_registerService() {

		Bundle bundle = FrameworkUtil.getBundle(
			RepeatableFieldsInfoItemFormProvider.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			RepeatableFieldsInfoItemFormProvider.class,
			new TestRepeatableFieldsInfoItemFormProvider(), null);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.info.collection.provider.item.selector.web.internal.item.selector.RepeatableFieldInfoCollectionProviderItemSelectorView",
		type = ItemSelectorView.class
	)
	private ItemSelectorView<ItemSelectorCriterion>
		_infoFieldProviderItemSelectorView;

	private static class TestRepeatableFieldsInfoItemFormProvider
		implements RepeatableFieldsInfoItemFormProvider<TestItem> {

		@Override
		public InfoForm getInfoForm() {
			return null;
		}

		@Override
		public InfoForm getRepeatableFieldsInfoForm(String formVariationKey) {
			return InfoForm.builder(
			).infoFieldSetEntry(
				infoFieldSetEntryUnsafeConsumer -> {
					infoFieldSetEntryUnsafeConsumer.accept(
						InfoField.builder(
						).infoFieldType(
							TextInfoFieldType.INSTANCE
						).namespace(
							TestItem.class.getName()
						).name(
							"TestItemField1"
						).labelInfoLocalizedValue(
							(InfoLocalizedValue<String>)
								InfoLocalizedValue.function(
									locale -> "test item 1")
						).repeatable(
							true
						).build());

					infoFieldSetEntryUnsafeConsumer.accept(
						InfoField.builder(
						).infoFieldType(
							TextInfoFieldType.INSTANCE
						).namespace(
							TestItem.class.getName()
						).name(
							"TestItemField2"
						).labelInfoLocalizedValue(
							(InfoLocalizedValue<String>)
								InfoLocalizedValue.function(
									locale -> "test item 2")
						).build());
				}
			).build();
		}

	}

}