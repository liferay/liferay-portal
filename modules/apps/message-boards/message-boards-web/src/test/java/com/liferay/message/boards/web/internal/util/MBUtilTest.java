/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.util;

import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

/**
 * @author Adolfo Pérez
 */
public class MBUtilTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			MBUtilTest::_getSecondArgument
		);

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(language);

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getUserName(Mockito.any(MBMessage.class))
		).thenReturn(
			"USER[]"
		);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(portal);
	}

	@Test
	public void testGetBBCodeQuoteBodyReturnsQuotedContent() {
		MBMessage mbMessage = Mockito.mock(MBMessage.class);

		Mockito.when(
			mbMessage.getBody(Mockito.anyBoolean())
		).thenReturn(
			"CONTENT"
		);

		Assert.assertEquals(
			"[quote=USER&#91;&#93;]\nCONTENT[/quote]\n\n\n",
			MBUtil.getBBCodeQuoteBody(
				Mockito.mock(HttpServletRequest.class), mbMessage));
	}

	@Test
	public void testGetCategoryIdReturnsCategoryId() {
		long categoryId = RandomTestUtil.randomInt();

		MBCategory category = Mockito.mock(MBCategory.class);

		Mockito.when(
			category.getCategoryId()
		).thenReturn(
			categoryId
		);

		Assert.assertEquals(
			categoryId,
			MBUtil.getCategoryId(
				Mockito.mock(HttpServletRequest.class), category));
	}

	@Test
	public void testGetCategoryIdReturnsRequestValue() {
		long categoryId = RandomTestUtil.randomInt();

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getParameter("mbCategoryId")
		).thenReturn(
			String.valueOf(categoryId)
		);

		Assert.assertEquals(
			categoryId,
			MBUtil.getCategoryId(httpServletRequest, (MBCategory)null));
	}

	@Test
	public void testGetMBMessageCategoryIdReturnsMessageCategoryId() {
		long categoryId = RandomTestUtil.randomInt();

		MBMessage message = Mockito.mock(MBMessage.class);

		Mockito.when(
			message.getCategoryId()
		).thenReturn(
			categoryId
		);

		Assert.assertEquals(
			categoryId,
			MBUtil.getCategoryId(
				Mockito.mock(HttpServletRequest.class), message));
	}

	@Test
	public void testGetMBMessageCategoryIdReturnsRequestValue() {
		long categoryId = RandomTestUtil.randomInt();

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getParameter("mbCategoryId")
		).thenReturn(
			String.valueOf(categoryId)
		);

		Assert.assertEquals(
			categoryId,
			MBUtil.getCategoryId(httpServletRequest, (MBMessage)null));
	}

	@Test
	public void testGetMBMessageURLIncludesMessageAnchor() {
		RenderResponse renderResponse = Mockito.mock(RenderResponse.class);

		Mockito.when(
			renderResponse.createRenderURL()
		).thenReturn(
			Mockito.mock(PortletURL.class)
		);

		String namespace = StringUtil.randomString();

		Mockito.when(
			renderResponse.getNamespace()
		).thenReturn(
			namespace
		);

		long messageId = RandomTestUtil.randomInt();

		Assert.assertTrue(
			StringUtil.endsWith(
				MBUtil.getMBMessageURL(messageId, renderResponse),
				StringBundler.concat(
					StringPool.POUND, namespace, "message_", messageId)));

		Assert.assertTrue(
			StringUtil.endsWith(
				MBUtil.getMBMessageURL(
					messageId, StringUtil.randomString(), renderResponse),
				StringBundler.concat(
					StringPool.POUND, namespace, "message_", messageId)));
	}

	private static <T> T _getSecondArgument(InvocationOnMock invocationOnMock) {
		Object[] arguments = invocationOnMock.getArguments();

		return (T)arguments[1];
	}

}