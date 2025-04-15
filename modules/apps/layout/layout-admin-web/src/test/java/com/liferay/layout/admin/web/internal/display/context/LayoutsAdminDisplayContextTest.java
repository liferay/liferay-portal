/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.helper.LayoutActionsHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutsAdminDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroup(Mockito.anyLong())
		).thenReturn(
			_group
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_groupLocalServiceUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		_setUpLanguageUtil();
		_setUpPortalUtil();

		_layoutActionsHelper = Mockito.mock(LayoutActionsHelper.class);

		_layoutsAdminDisplayContext = new LayoutsAdminDisplayContext(
			null, _layoutActionsHelper, null, null, _liferayPortletRequest,
			null);
	}

	@Test
	public void testGetEditOrViewLayoutURLEditURL() throws Exception {
		Layout layout = _getContentLayout(true);

		Mockito.when(
			_layoutActionsHelper.isShowConfigureAction(layout)
		).thenReturn(
			true
		);

		_assertGetEditOrViewLayoutURL(layout, Constants.EDIT);
	}

	@Test
	public void testGetEditOrViewLayoutURLViewURL() throws Exception {
		Layout layout = _getContentLayout(true);

		Mockito.when(
			_layoutActionsHelper.isShowViewLayoutAction(layout)
		).thenReturn(
			true
		);

		_assertGetEditOrViewLayoutURL(layout, StringPool.BLANK);
	}

	@Test
	public void testGetEditOrViewLayoutURLViewURLWithLayoutUpdateableFalse()
		throws Exception {

		Layout layout = _getContentLayout(false);

		Mockito.when(
			_layoutActionsHelper.isShowConfigureAction(layout)
		).thenReturn(
			true
		);

		Mockito.when(
			_layoutActionsHelper.isShowViewLayoutAction(layout)
		).thenReturn(
			true
		);

		_assertGetEditOrViewLayoutURL(layout, StringPool.BLANK);
	}

	private void _assertGetEditOrViewLayoutURL(Layout layout, String layoutMode)
		throws Exception {

		String friendlyURL = RandomTestUtil.randomString();

		if (Validator.isNull(layoutMode)) {
			Mockito.when(
				_portal.getLayoutFullURL(layout, null)
			).thenReturn(
				friendlyURL
			);
		}
		else {
			Mockito.when(
				_portal.getLayoutFullURL(layout.fetchDraftLayout(), null)
			).thenReturn(
				friendlyURL
			);
		}

		String url = _layoutsAdminDisplayContext.getEditOrViewLayoutURL(layout);

		Assert.assertTrue(url, StringUtil.startsWith(url, friendlyURL));
		Assert.assertEquals(
			url, layoutMode,
			HttpComponentsUtil.getParameter(url, "p_l_mode", false));

		String backURL = HttpComponentsUtil.decodeURL(
			HttpComponentsUtil.getParameter(url, "p_l_back_url", false));

		Assert.assertEquals(
			backURL, layout.getPlid(),
			GetterUtil.getLong(
				HttpComponentsUtil.getParameter(
					backURL, LayoutAdminPortletKeys.GROUP_PAGES + "_selPlid",
					false),
				-1));

		Assert.assertEquals(
			"pages",
			HttpComponentsUtil.getParameter(url, "p_l_back_url_title", false));
	}

	private Layout _getContentLayout(boolean layoutUpdateable) {
		Layout layout = Mockito.mock(Layout.class);

		Layout draftLayout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.fetchDraftLayout()
		).thenReturn(
			draftLayout
		);

		Mockito.when(
			layout.getPlid()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			layout.isLayoutUpdateable()
		).thenReturn(
			layoutUpdateable
		);

		Mockito.when(
			layout.isTypeContent()
		).thenReturn(
			true
		);

		return layout;
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			(Answer<String>)invocationOnMock -> invocationOnMock.getArgument(
				1, String.class)
		);

		languageUtil.setLanguage(language);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		_portal = Mockito.mock(Portal.class);

		_liferayPortletRequest = new MockLiferayPortletActionRequest();

		MockLiferayPortletURL liferayPortletURL = new MockLiferayPortletURL();

		liferayPortletURL.setPortletId(LayoutAdminPortletKeys.GROUP_PAGES);

		Mockito.when(
			_portal.getControlPanelPortletURL(
				_liferayPortletRequest, _group,
				LayoutAdminPortletKeys.GROUP_PAGES, 0, 0,
				PortletRequest.RENDER_PHASE)
		).thenReturn(
			liferayPortletURL
		);

		Mockito.when(
			_portal.getHttpServletRequest(_liferayPortletRequest)
		).thenReturn(
			new MockHttpServletRequest()
		);

		Mockito.when(
			_portal.stripURLAnchor(Mockito.anyString(), Mockito.anyString())
		).thenAnswer(
			(Answer<String[]>)invocationOnMock -> new String[] {
				invocationOnMock.getArgument(0, String.class), StringPool.BLANK
			}
		);

		portalUtil.setPortal(_portal);
	}

	private static final Group _group = Mockito.mock(Group.class);
	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);

	private LayoutActionsHelper _layoutActionsHelper;
	private LayoutsAdminDisplayContext _layoutsAdminDisplayContext;
	private LiferayPortletRequest _liferayPortletRequest;
	private Portal _portal;

}