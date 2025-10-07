/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.item.selector.web.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.object.item.selector.ObjectDefinitionItemSelectorCriterion;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jonathan McCann
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionItemSelectorViewDescriptorTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();
	}

	@Test
	public void testGetPayload() throws Exception {
		ItemSelectorViewDescriptor<Object> itemSelectorViewDescriptor =
			_getItemSelectorViewDescriptor(new MockHttpServletRequest());

		ItemSelectorViewDescriptor.ItemDescriptor itemDescriptor =
			itemSelectorViewDescriptor.getItemDescriptor(_objectDefinition);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			itemDescriptor.getPayload());

		Assert.assertEquals(
			_objectDefinition.getObjectDefinitionId(),
			jsonObject.getLong("objectDefinitionId"));
		Assert.assertEquals(
			_objectDefinition.getLabel(_locale), jsonObject.getString("label"));
	}

	@Test
	public void testGetSearchContainer() throws Exception {
		ItemSelectorViewDescriptor<Object> itemSelectorViewDescriptor =
			_getItemSelectorViewDescriptor(new MockHttpServletRequest());

		SearchContainer<Object> searchContainer =
			itemSelectorViewDescriptor.getSearchContainer();

		Assert.assertEquals(1, searchContainer.getTotal());

		List<Object> objectDefinitions = searchContainer.getResults();

		Assert.assertEquals(_objectDefinition, objectDefinitions.get(0));
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
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		_objectDefinitionItemSelectorView.renderHTML(
			mockHttpServletRequest, new MockHttpServletResponse(),
			new ObjectDefinitionItemSelectorCriterion(),
			new MockLiferayPortletURL(), RandomTestUtil.randomString(), true);

		Object itemSelectorViewDescriptorRendererDisplayContext =
			mockHttpServletRequest.getAttribute(
				"com.liferay.item.selector.web.internal.display.context." +
					"ItemSelectorViewDescriptorRendererDisplayContext");

		return ReflectionTestUtil.invoke(
			itemSelectorViewDescriptorRendererDisplayContext,
			"getItemSelectorViewDescriptor", new Class<?>[0], null);
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(_locale);
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private final Locale _locale = LocaleUtil.US;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject(
		filter = "component.name=com.liferay.object.item.selector.web.internal.ObjectDefinitionItemSelectorView",
		type = ItemSelectorView.class
	)
	private ItemSelectorView<ItemSelectorCriterion>
		_objectDefinitionItemSelectorView;

}