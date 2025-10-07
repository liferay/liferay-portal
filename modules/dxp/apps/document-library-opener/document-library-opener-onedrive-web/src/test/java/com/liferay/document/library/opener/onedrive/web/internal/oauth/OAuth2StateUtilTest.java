/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.onedrive.web.internal.oauth;

import com.liferay.document.library.opener.oauth.OAuth2State;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

/**
 * @author Alicia García García
 * @author Cristina González
 */
public class OAuth2StateUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCleanUp() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		OAuth2StateUtil.save(
			mockHttpServletRequest,
			new OAuth2State(
				RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));

		OAuth2StateUtil.cleanUp(mockHttpServletRequest);

		OAuth2State oAuth2State = OAuth2StateUtil.getOAuth2State(
			mockHttpServletRequest);

		Assert.assertFalse(oAuth2State != null);
	}

	@Test
	public void testGetOAuth2StateOptionalWithNotNullState() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String state = RandomTestUtil.randomString();

		OAuth2State initialOAuth2State = new OAuth2State(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), state);

		OAuth2StateUtil.save(mockHttpServletRequest, initialOAuth2State);

		_assertOAuth2State(
			initialOAuth2State, state,
			OAuth2StateUtil.getOAuth2State(mockHttpServletRequest));
	}

	@Test
	public void testGetOAuth2StateOptionalWithNullState() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setSession(new MockHttpSession());

		Assert.assertNull(
			OAuth2StateUtil.getOAuth2State(mockHttpServletRequest));
	}

	@Test
	public void testIsValidWithDifferentState() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"state", RandomTestUtil.randomString(5));

		OAuth2State oAuth2State = new OAuth2State(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(5));

		Assert.assertFalse(
			OAuth2StateUtil.isValid(oAuth2State, mockHttpServletRequest));
	}

	@Test
	public void testIsValidWithSameState() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String state = RandomTestUtil.randomString();

		mockHttpServletRequest.setParameter("state", state);

		OAuth2State oAuth2State = new OAuth2State(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), state);

		Assert.assertTrue(
			OAuth2StateUtil.isValid(oAuth2State, mockHttpServletRequest));
	}

	@Test
	public void testSave() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String state = RandomTestUtil.randomString();

		OAuth2State initialOAuth2State = new OAuth2State(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), state);

		OAuth2StateUtil.save(mockHttpServletRequest, initialOAuth2State);

		_assertOAuth2State(
			initialOAuth2State, state,
			OAuth2StateUtil.getOAuth2State(mockHttpServletRequest));
	}

	private void _assertOAuth2State(
		OAuth2State expectedOAuth2State, String state,
		OAuth2State actualOAuth2State) {

		Assert.assertNotNull(actualOAuth2State);

		Assert.assertEquals(
			expectedOAuth2State.getFailureURL(),
			actualOAuth2State.getFailureURL());
		Assert.assertEquals(
			expectedOAuth2State.getSuccessURL(),
			actualOAuth2State.getSuccessURL());
		Assert.assertEquals(
			expectedOAuth2State.getUserId(), actualOAuth2State.getUserId());

		Assert.assertTrue(actualOAuth2State.isValid(state));
	}

}