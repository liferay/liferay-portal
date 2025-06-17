/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.item.selector.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.ServletRequest;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
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
 * @author Selton Guedes
 */
@RunWith(Arquillian.class)
public class SystemObjectEntryItemSelectorViewTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		GroupTestUtil.deleteGroup(_group);
	}

	@Test
	public void testGetTitle() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				_group.getCompanyId(), User.class.getName());

		ItemSelectorView<InfoItemItemSelectorCriterion> itemSelectorView =
			_getItemSelectorView(objectDefinition);

		Queue<ItemSelectorViewDescriptor<?>> queue = new LinkedList<>();

		ItemSelectorViewDescriptorRenderer<?>
			itemSelectorViewDescriptorRenderer =
				(ItemSelectorViewDescriptorRenderer<?>)
					ReflectionTestUtil.getAndSetFieldValue(
						itemSelectorView, "_itemSelectorViewDescriptorRenderer",
						ProxyUtil.newProxyInstance(
							ItemSelectorViewDescriptorRenderer.class.
								getClassLoader(),
							new Class<?>[] {
								ItemSelectorViewDescriptorRenderer.class
							},
							(proxy, method, arguments) -> {
								if (StringUtil.equals(
										method.getName(), "renderHTML")) {

									queue.add(
										(ItemSelectorViewDescriptor
											<BaseModel<?>>)arguments[6]);
								}

								return null;
							}));

		itemSelectorView.renderHTML(
			_mockHttpServletRequest(), new MockHttpServletResponse(),
			new InfoItemItemSelectorCriterion(), new MockLiferayPortletURL(),
			RandomTestUtil.randomString(), true);

		ItemSelectorViewDescriptor<BaseModel<?>> itemSelectorViewDescriptor =
			(ItemSelectorViewDescriptor<BaseModel<?>>)queue.poll();

		User user = UserTestUtil.addUser();

		ItemSelectorViewDescriptor.ItemDescriptor itemDescriptor =
			itemSelectorViewDescriptor.getItemDescriptor(user);

		Assert.assertEquals(
			user.getFirstName(),
			itemDescriptor.getTitle(LocaleUtil.getDefault()));

		long originalTitleObjectFieldId =
			objectDefinition.getTitleObjectFieldId();

		try {
			ObjectField objectField = _objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(), "emailAddress");

			_objectDefinitionLocalService.updateTitleObjectFieldId(
				objectDefinition.getObjectDefinitionId(),
				objectField.getObjectFieldId());

			Assert.assertEquals(
				user.getEmailAddress(),
				itemDescriptor.getTitle(LocaleUtil.getDefault()));
		}
		finally {
			_objectDefinitionLocalService.updateTitleObjectFieldId(
				objectDefinition.getObjectDefinitionId(),
				originalTitleObjectFieldId);

			ReflectionTestUtil.setFieldValue(
				itemSelectorView, "_itemSelectorViewDescriptorRenderer",
				itemSelectorViewDescriptorRenderer);
		}
	}

	private ItemSelectorView<InfoItemItemSelectorCriterion>
			_getItemSelectorView(ObjectDefinition objectDefinition)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			SystemObjectEntryItemSelectorViewTest.class);

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
			CompanyLocalServiceUtil.fetchCompany(_group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private static Group _group;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

}