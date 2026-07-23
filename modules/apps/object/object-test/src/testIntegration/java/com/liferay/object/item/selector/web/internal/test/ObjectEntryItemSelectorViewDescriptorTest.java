/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.item.selector.web.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.ServletRequest;

import java.io.Serializable;

import java.sql.Date;

import java.util.Collections;
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
import org.osgi.framework.ServiceReference;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jhosseph Gonzalez
 */
@RunWith(Arquillian.class)
public class ObjectEntryItemSelectorViewDescriptorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, "text", "text",
					false)),
			ObjectDefinitionConstants.SCOPE_SITE);
	}

	@Test
	public void testGetSearchContainer() throws Exception {
		ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition, "text", RandomTestUtil.randomString());
		ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"displayDate",
				new Date(System.currentTimeMillis() + TimeUnit.DAY.toMillis(1))
			).put(
				"text", RandomTestUtil.randomString()
			).build());

		ItemSelectorViewDescriptor<Object> itemSelectorViewDescriptor =
			_getItemSelectorViewDescriptor();

		SearchContainer<Object> searchContainer =
			itemSelectorViewDescriptor.getSearchContainer();

		List<Object> objectEntries = searchContainer.getResults();

		Assert.assertEquals(objectEntries.toString(), 1, objectEntries.size());

		ObjectEntry objectEntry = (ObjectEntry)objectEntries.get(0);

		Assert.assertEquals(
			TestPropsValues.getGroupId(), objectEntry.getGroupId());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, objectEntry.getStatus());
	}

	private ItemSelectorView<InfoItemItemSelectorCriterion>
			_getItemSelectorView(ObjectDefinition objectDefinition)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectEntryItemSelectorViewDescriptorTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		for (ServiceReference<ItemSelectorView<InfoItemItemSelectorCriterion>>
				serviceReference :
					bundleContext.getServiceReferences(
						(Class<ItemSelectorView<InfoItemItemSelectorCriterion>>)
							(Class<?>)ItemSelectorView.class,
						"(item.selector.view.order=500)")) {

			ItemSelectorView<InfoItemItemSelectorCriterion> itemSelectorView =
				bundleContext.getService(serviceReference);

			if (StringUtil.equals(
					objectDefinition.getPluralLabel(LocaleUtil.getDefault()),
					itemSelectorView.getTitle(LocaleUtil.getDefault()))) {

				return itemSelectorView;
			}
		}

		return null;
	}

	private ItemSelectorViewDescriptor<Object> _getItemSelectorViewDescriptor()
		throws Exception {

		ItemSelectorView<InfoItemItemSelectorCriterion> itemSelectorView =
			_getItemSelectorView(_objectDefinition);

		ServletRequest servletRequest = _mockHttpServletRequest();

		itemSelectorView.renderHTML(
			servletRequest, new MockHttpServletResponse(),
			new InfoItemItemSelectorCriterion(), new MockLiferayPortletURL(),
			RandomTestUtil.randomString(), true);

		Object itemSelectorViewDescriptorRendererDisplayContext =
			servletRequest.getAttribute(
				"com.liferay.item.selector.web.internal.display.context." +
					"ItemSelectorViewDescriptorRendererDisplayContext");

		return ReflectionTestUtil.invoke(
			itemSelectorViewDescriptorRendererDisplayContext,
			"getItemSelectorViewDescriptor", new Class<?>[0], null);
	}

	private ServletRequest _mockHttpServletRequest() throws Exception {
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

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.fetchCompany(
				TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

}