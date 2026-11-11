/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.frontend.taglib.form.navigator.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class JournalDisplayPageFormNavigatorEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPS-147960")
	public void testIsVisible() throws Exception {
		Assert.assertTrue(
			_isVisible(
				DDMStructureTestUtil.addStructure(
					_group.getGroupId(), JournalArticle.class.getName())));

		Assert.assertFalse(
			_isVisible(
				_ddmStructureLocalService.fetchStructure(
					_group.getGroupId(),
					_portal.getClassNameId(JournalArticle.class),
					"BASIC-WEB-CONTENT", true)));
	}

	private boolean _isVisible(DDMStructure ddmStructure) throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.addParameter(
			"classNameId",
			String.valueOf(_portal.getClassNameId(DDMStructure.class)));
		mockLiferayPortletRenderRequest.addParameter(
			"classPK", String.valueOf(ddmStructure.getStructureId()));
		mockLiferayPortletRenderRequest.addParameter(
			"groupId", String.valueOf(ddmStructure.getGroupId()));
		mockLiferayPortletRenderRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			mockLiferayPortletRenderRequest);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setScopeGroupId(_group.getGroupId());

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setRequest(
			mockLiferayPortletRenderRequest.getHttpServletRequest());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			return _formNavigatorEntry.isVisible(
				TestPropsValues.getUser(), null);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.frontend.taglib.form.navigator.JournalDisplayPageFormNavigatorEntry"
	)
	private FormNavigatorEntry<JournalArticle> _formNavigatorEntry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

}