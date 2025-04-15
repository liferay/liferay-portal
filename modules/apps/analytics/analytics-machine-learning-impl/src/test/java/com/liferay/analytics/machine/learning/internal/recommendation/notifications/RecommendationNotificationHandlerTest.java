/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.machine.learning.internal.recommendation.notifications;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.language.LanguageImpl;
import com.liferay.portal.model.impl.UserNotificationEventImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Marcos Martins
 */
public class RecommendationNotificationHandlerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(new LanguageImpl());
	}

	@Before
	public void setUp() {
		_serviceContext = new ServiceContext();

		_serviceContext.setLanguageId("en_US");
		_serviceContext.setRequest(new MockHttpServletRequest());

		ReflectionTestUtil.setFieldValue(
			_recommendationNotificationHandler, "_jsonFactory",
			new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			_recommendationNotificationHandler, "_language",
			LanguageUtil.getLanguage());

		_portal = Mockito.mock(Portal.class);

		Mockito.when(
			_portal.getControlPanelPortletURL(
				Mockito.any(HttpServletRequest.class), Mockito.any(),
				Mockito.any())
		).thenReturn(
			new MockLiferayPortletURL()
		);

		ReflectionTestUtil.setFieldValue(
			_recommendationNotificationHandler, "_portal", _portal);
	}

	@Test
	public void testGetBody() throws Exception {
		UserNotificationEvent userNotificationEvent =
			new UserNotificationEventImpl();

		userNotificationEvent.setPayload(
			JSONUtil.put(
				"notificationTypeCode",
				RecommendationNotificationType.
					CONTENT_RECOMMENDER_MOST_POPULAR_ITEMS_ENABLED.
						getNotificationTypeCode()
			).toString());

		Assert.assertEquals(
			_getBody(
				RecommendationNotificationType.
					CONTENT_RECOMMENDER_MOST_POPULAR_ITEMS_ENABLED.getKey()),
			_recommendationNotificationHandler.getBody(
				userNotificationEvent, _serviceContext));

		userNotificationEvent.setPayload(
			JSONUtil.put(
				"notificationTypeCode",
				RecommendationNotificationType.
					CONTENT_RECOMMENDER_MOST_POPULAR_ITEMS_FAILED.
						getNotificationTypeCode()
			).toString());

		Assert.assertEquals(
			_getBody(
				RecommendationNotificationType.
					CONTENT_RECOMMENDER_MOST_POPULAR_ITEMS_FAILED.getKey()),
			_recommendationNotificationHandler.getBody(
				userNotificationEvent, _serviceContext));

		userNotificationEvent.setPayload(
			JSONUtil.put(
				"notificationTypeCode",
				RecommendationNotificationType.
					CONTENT_RECOMMENDER_USER_PERSONALIZATION_ENABLED.
						getNotificationTypeCode()
			).toString());

		Assert.assertEquals(
			_getBody(
				RecommendationNotificationType.
					CONTENT_RECOMMENDER_USER_PERSONALIZATION_ENABLED.getKey()),
			_recommendationNotificationHandler.getBody(
				userNotificationEvent, _serviceContext));

		userNotificationEvent.setPayload(
			JSONUtil.put(
				"notificationTypeCode",
				RecommendationNotificationType.
					CONTENT_RECOMMENDER_USER_PERSONALIZATION_FAILED.
						getNotificationTypeCode()
			).toString());

		Assert.assertEquals(
			_getBody(
				RecommendationNotificationType.
					CONTENT_RECOMMENDER_USER_PERSONALIZATION_FAILED.getKey()),
			_recommendationNotificationHandler.getBody(
				userNotificationEvent, _serviceContext));
	}

	@Test
	public void testGetLink() throws Exception {
		UserNotificationEvent userNotificationEvent =
			new UserNotificationEventImpl();

		userNotificationEvent.setPayload(
			JSONUtil.put(
				"notificationTypeCode",
				RecommendationNotificationType.
					CONTENT_RECOMMENDER_MOST_POPULAR_ITEMS_ENABLED.
						getNotificationTypeCode()
			).toString());

		StringBundler sb = new StringBundler(4);

		sb.append("http//localhost/test?param_mvcRenderCommandName=");
		sb.append("%2Fconfiguration_admin%2Fview_configuration_screen&");
		sb.append("param_configurationScreenKey=analytics-cloud-connection&");
		sb.append("currentPage=RECOMMENDATIONS");

		Assert.assertEquals(
			sb.toString(),
			_recommendationNotificationHandler.getLink(
				userNotificationEvent, _serviceContext));
	}

	@Test
	public void testGetTitle() throws Exception {
		UserNotificationEvent userNotificationEvent =
			new UserNotificationEventImpl();

		userNotificationEvent.setPayload(
			JSONUtil.put(
				"notificationTypeCode",
				RecommendationNotificationType.
					CONTENT_RECOMMENDER_MOST_POPULAR_ITEMS_ENABLED.
						getNotificationTypeCode()
			).toString());

		Assert.assertEquals(
			RecommendationNotificationType.
				CONTENT_RECOMMENDER_MOST_POPULAR_ITEMS_ENABLED.getKey(),
			_recommendationNotificationHandler.getTitle(
				userNotificationEvent, _serviceContext));

		userNotificationEvent.setPayload(
			JSONUtil.put(
				"notificationTypeCode",
				RecommendationNotificationType.
					CONTENT_RECOMMENDER_MOST_POPULAR_ITEMS_FAILED.
						getNotificationTypeCode()
			).toString());

		Assert.assertEquals(
			RecommendationNotificationType.
				CONTENT_RECOMMENDER_MOST_POPULAR_ITEMS_FAILED.getKey(),
			_recommendationNotificationHandler.getTitle(
				userNotificationEvent, _serviceContext));

		userNotificationEvent.setPayload(
			JSONUtil.put(
				"notificationTypeCode",
				RecommendationNotificationType.
					CONTENT_RECOMMENDER_USER_PERSONALIZATION_ENABLED.
						getNotificationTypeCode()
			).toString());

		Assert.assertEquals(
			RecommendationNotificationType.
				CONTENT_RECOMMENDER_USER_PERSONALIZATION_ENABLED.getKey(),
			_recommendationNotificationHandler.getTitle(
				userNotificationEvent, _serviceContext));

		userNotificationEvent.setPayload(
			JSONUtil.put(
				"notificationTypeCode",
				RecommendationNotificationType.
					CONTENT_RECOMMENDER_USER_PERSONALIZATION_FAILED.
						getNotificationTypeCode()
			).toString());

		Assert.assertEquals(
			RecommendationNotificationType.
				CONTENT_RECOMMENDER_USER_PERSONALIZATION_FAILED.getKey(),
			_recommendationNotificationHandler.getTitle(
				userNotificationEvent, _serviceContext));
	}

	private String _getBody(String title) {
		return String.format(
			"<div class=\"title\">%s</div><div class=\"body\"></div>", title);
	}

	private static final RecommendationNotificationHandler
		_recommendationNotificationHandler =
			new RecommendationNotificationHandler();

	private Portal _portal;
	private ServiceContext _serviceContext;

}