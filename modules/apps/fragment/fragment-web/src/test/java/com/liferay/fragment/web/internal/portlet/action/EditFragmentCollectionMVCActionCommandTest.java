/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class EditFragmentCollectionMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_editFragmentCollectionMVCActionCommand, "_portal", _portal);
	}

	@Test
	public void testGetRedirectURL() {
		_testGetRedirectURLWithoutRedirect();
		_testGetRedirectURLWithRedirect();
	}

	private void _testGetRedirectURLWithoutRedirect() {
		long fragmentCollectionId = RandomTestUtil.randomLong();

		Mockito.when(
			_fragmentCollection.getFragmentCollectionId()
		).thenReturn(
			fragmentCollectionId
		);

		LiferayPortletResponse liferayPortletResponse = Mockito.mock(
			LiferayPortletResponse.class);

		Mockito.when(
			_portal.getLiferayPortletResponse(_actionResponse)
		).thenReturn(
			liferayPortletResponse
		);

		Mockito.when(
			liferayPortletResponse.createRenderURL()
		).thenReturn(
			new MockLiferayPortletURL()
		);

		String redirectURL =
			_editFragmentCollectionMVCActionCommand.getRedirectURL(
				_actionRequest, _actionResponse, _fragmentCollection);

		Assert.assertTrue(
			redirectURL,
			redirectURL.contains(
				"fragmentCollectionId=" + fragmentCollectionId));
	}

	private void _testGetRedirectURLWithRedirect() {
		String redirect = RandomTestUtil.randomString();

		Mockito.when(
			_actionRequest.getParameter("redirect")
		).thenReturn(
			redirect
		);

		Assert.assertEquals(
			redirect,
			_editFragmentCollectionMVCActionCommand.getRedirectURL(
				_actionRequest, _actionResponse, _fragmentCollection));
	}

	private final ActionRequest _actionRequest = Mockito.mock(
		ActionRequest.class);
	private final ActionResponse _actionResponse = Mockito.mock(
		ActionResponse.class);
	private final EditFragmentCollectionMVCActionCommand
		_editFragmentCollectionMVCActionCommand =
			new EditFragmentCollectionMVCActionCommand();
	private final FragmentCollection _fragmentCollection = Mockito.mock(
		FragmentCollection.class);
	private final Portal _portal = Mockito.mock(Portal.class);

}