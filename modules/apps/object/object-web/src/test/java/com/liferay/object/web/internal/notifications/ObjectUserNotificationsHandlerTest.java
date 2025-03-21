/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.notifications;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.info.item.InfoItemReference;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Thalles Montenegro
 */
public class ObjectUserNotificationsHandlerTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws PortalException {
		_objectDefinition = Mockito.mock(ObjectDefinition.class);

		_objectUserNotificationsHandler = new ObjectUserNotificationsHandler(
			_assetDisplayPageFriendlyURLProvider, _objectDefinition);

		_setUpServiceContext();
	}

	@Test
	public void testGetLinkToAccessSubmittedObjectEntry1() throws Exception {
		Mockito.when(
			_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				Mockito.any(InfoItemReference.class),
				Mockito.any(ThemeDisplay.class))
		).thenReturn(
			"http://localhost:8080/web/l/54321"
		);

		Mockito.when(
			_serviceContext.getThemeDisplay()
		).thenReturn(
			Mockito.mock(ThemeDisplay.class)
		);

		Mockito.when(
			_userNotificationEvent.getPayload()
		).thenReturn(
			StringPool.BLANK
		);

		Assert.assertEquals(
			"http://localhost:8080/web/l/54321",
			_objectUserNotificationsHandler.getLink(
				_userNotificationEvent, _serviceContext));
	}

	@Test
	public void testGetLinkToAccessSubmittedObjectEntry2() throws Exception {
		Mockito.when(
			_userNotificationEvent.getPayload()
		).thenReturn(
			"{\"externalReferenceCode\": \"externalReferenceCode\"}"
		);

		try (MockedStatic<PortalUtil> portalUtilMockedStatic =
				Mockito.mockStatic(PortalUtil.class);
			MockedStatic<PortletURLBuilder> portletURLBuilderMockedStatic =
				Mockito.mockStatic(PortletURLBuilder.class)) {

			MockLiferayPortletURL mockLiferayPortletURL =
				new MockLiferayPortletURL();

			portletURLBuilderMockedStatic.when(
				() -> PortletURLBuilder.create(Mockito.any())
			).thenReturn(
				new PortletURLBuilder.PortletURLStep(mockLiferayPortletURL)
			);

			Mockito.when(
				_objectDefinition.getObjectDefinitionId()
			).thenReturn(
				1L
			);

			_objectUserNotificationsHandler.getLink(
				_userNotificationEvent, _serviceContext);

			Assert.assertEquals(
				"externalReferenceCode",
				mockLiferayPortletURL.getParameter("externalReferenceCode"));
			Assert.assertEquals(
				"/object_entries/edit_object_entry",
				mockLiferayPortletURL.getParameter("mvcRenderCommandName"));
			Assert.assertEquals(
				String.valueOf(_objectDefinition.getObjectDefinitionId()),
				mockLiferayPortletURL.getParameter("objectDefinitionId"));
		}
	}

	@Test
	public void testGetLinkToAccessSystemSettingObjectConfiguration()
		throws Exception {

		Mockito.when(
			_userNotificationEvent.getPayload()
		).thenReturn(
			"{\"exceedsObjectEntryLimit\": true}"
		);

		try (MockedStatic<PortletURLBuilder> portletURLBuilderMockedStatic =
				Mockito.mockStatic(PortletURLBuilder.class);
			MockedStatic<RequestBackedPortletURLFactoryUtil>
				requestBackedPortletURLFactoryUtilMockedStatic =
					Mockito.mockStatic(
						RequestBackedPortletURLFactoryUtil.class)) {

			requestBackedPortletURLFactoryUtilMockedStatic.when(
				() -> RequestBackedPortletURLFactoryUtil.create(
					Mockito.any(HttpServletRequest.class))
			).thenReturn(
				_requestBackedPortletURLFactory
			);

			MockLiferayPortletURL mockLiferayPortletURL =
				new MockLiferayPortletURL();

			portletURLBuilderMockedStatic.when(
				() -> PortletURLBuilder.create(Mockito.any())
			).thenReturn(
				new PortletURLBuilder.PortletURLStep(mockLiferayPortletURL)
			);

			_objectUserNotificationsHandler.getLink(
				_userNotificationEvent, _serviceContext);

			Assert.assertEquals(
				"com.liferay.object.configuration.ObjectConfiguration",
				mockLiferayPortletURL.getParameter("factoryPid"));
			Assert.assertEquals(
				"/configuration_admin/edit_configuration",
				mockLiferayPortletURL.getParameter("mvcRenderCommandName"));

			Mockito.verify(
				_requestBackedPortletURLFactory
			).createActionURL(
				ConfigurationAdminPortletKeys.INSTANCE_SETTINGS
			);
		}
	}

	private void _setUpServiceContext() throws PortalException {
		Mockito.when(
			_serviceContext.getCurrentURL()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_serviceContext.getRequest()
		).thenReturn(
			Mockito.mock(HttpServletRequest.class)
		);

		Mockito.when(
			_serviceContext.getScopeGroup()
		).thenReturn(
			Mockito.mock(Group.class)
		);

		Mockito.when(
			_serviceContext.getThemeDisplay()
		).thenReturn(
			null
		);
	}

	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider = Mockito.mock(
			AssetDisplayPageFriendlyURLProvider.class);
	private ObjectDefinition _objectDefinition;
	private ObjectUserNotificationsHandler _objectUserNotificationsHandler;
	private final RequestBackedPortletURLFactory
		_requestBackedPortletURLFactory = Mockito.mock(
			RequestBackedPortletURLFactory.class);
	private final ServiceContext _serviceContext = Mockito.mock(
		ServiceContext.class);
	private final UserNotificationEvent _userNotificationEvent = Mockito.mock(
		UserNotificationEvent.class);

}