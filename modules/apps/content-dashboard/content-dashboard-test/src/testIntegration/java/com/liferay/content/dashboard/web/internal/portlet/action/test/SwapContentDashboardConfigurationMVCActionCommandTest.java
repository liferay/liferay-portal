/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Arques
 */
@RunWith(Arquillian.class)
public class SwapContentDashboardConfigurationMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testProcessAction() throws PortletException {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		PortletPreferences portletPreferences =
			mockLiferayPortletActionRequest.getPreferences();

		portletPreferences.setValues(
			"assetVocabularyIds", "vocabularyId1", "vocabularyId2");

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		portletPreferences = mockLiferayPortletActionRequest.getPreferences();

		Assert.assertArrayEquals(
			new String[] {"vocabularyId2", "vocabularyId1"},
			portletPreferences.getValues("assetVocabularyIds", new String[0]));
	}

	@Test
	public void testProcessActionWithOneAssetVocabularyName()
		throws PortletException {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		PortletPreferences portletPreferences =
			mockLiferayPortletActionRequest.getPreferences();

		portletPreferences.setValues("assetVocabularyIds", "vocabulary1");

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		portletPreferences = mockLiferayPortletActionRequest.getPreferences();

		Assert.assertArrayEquals(
			new String[] {"vocabulary1"},
			portletPreferences.getValues("assetVocabularyIds", new String[0]));
	}

	@Inject(
		filter = "mvc.command.name=/content_dashboard/swap_content_dashboard_configuration",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _mvcActionCommand;

}