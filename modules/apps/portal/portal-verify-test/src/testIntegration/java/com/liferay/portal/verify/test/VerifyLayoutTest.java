/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogWrapper;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.verify.VerifyLayout;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.test.util.BaseVerifyProcessTestCase;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Chaparro
 */
@RunWith(Arquillian.class)
public class VerifyLayoutTest extends BaseVerifyProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_verifyLayoutLog = ReflectionTestUtil.getFieldValue(
			VerifyLayout.class, "_log");

		ReflectionTestUtil.setFieldValue(
			VerifyLayout.class, "_log",
			new VerifyLayoutLogWrapper(_verifyLayoutLog));

		_connection = DataAccess.getConnection();

		_errorMessages = new ArrayList<>();

		for (String keyword : PropsValues.LAYOUT_FRIENDLY_URL_KEYWORDS) {
			if (!keyword.contains(StringPool.STAR) &&
				!keyword.contains(StringPool.UNDERLINE)) {

				_keyword1 = keyword;

				_keyword2 = keyword;

				break;
			}
		}

		_layout1 = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			false, 0, "name", "title", "description",
			LayoutConstants.TYPE_PORTLET, false, _FRIENDLY_URL_1,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));

		_layout2 = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			false, 0, "name", "title", "description",
			LayoutConstants.TYPE_PORTLET, false, _FRIENDLY_URL_2,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ReflectionTestUtil.setFieldValue(
			VerifyLayout.class, "_log", _verifyLayoutLog);

		_layoutLocalService.deleteLayout(_layout1);

		_layoutLocalService.deleteLayout(_layout2);

		DataAccess.cleanUp(_connection);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		_errorMessages = new ArrayList<>();

		_updateFriendlyURL(_layout1.getPlid(), _FRIENDLY_URL_1);
		_updateFriendlyURL(_layout2.getPlid(), _FRIENDLY_URL_2);
	}

	@Test
	public void testVerifyLayoutsWithoutReservedLayoutFriendlyURL()
		throws Exception {

		super.testVerify();

		Assert.assertEquals(
			_errorMessages.toString(), 0, _errorMessages.size());
	}

	@Ignore
	@Test
	public void testVerifyLayoutsWithReservedLayoutFriendlyURLs()
		throws Exception {

		_updateFriendlyURL(
			_layout1.getPlid(), StringPool.FORWARD_SLASH + _keyword1);
		_updateFriendlyURL(
			_layout2.getPlid(), StringPool.FORWARD_SLASH + _keyword2);

		super.testVerify();

		Assert.assertEquals(
			_errorMessages.toString(), 2, _errorMessages.size());

		String errorMessage1 = _errorMessages.get(0);

		String errorMessage2 = _errorMessages.get(1);

		Assert.assertTrue(errorMessage1.contains(_keyword1));

		Assert.assertTrue(errorMessage2.contains(_keyword2));
	}

	@Test
	public void testVerifyLayoutsWithUnderscoreReservedLayoutFriendlyURL()
		throws Exception {

		for (String keyword : PropsValues.LAYOUT_FRIENDLY_URL_KEYWORDS) {
			if (keyword.contains(StringPool.UNDERLINE)) {
				if (keyword.contains(StringPool.STAR)) {
					_keyword1 = StringUtil.replace(keyword, '*', "12345");
				}
				else {
					_keyword1 = keyword;
				}

				break;
			}
		}

		_updateFriendlyURL(
			_layout1.getPlid(), StringPool.FORWARD_SLASH + _keyword1);
		_updateFriendlyURL(
			_layout2.getPlid(),
			StringPool.FORWARD_SLASH + StringUtil.replace(_keyword1, '_', "a"));

		super.testVerify();

		Assert.assertEquals(
			_errorMessages.toString(), 1, _errorMessages.size());

		String errorMessage = _errorMessages.get(0);

		Assert.assertTrue(errorMessage.contains(_keyword1));
	}

	@Test
	public void testVerifyLayoutWithAsteriskReservedLayoutFriendlyURL()
		throws Exception {

		for (String keyword : PropsValues.LAYOUT_FRIENDLY_URL_KEYWORDS) {
			if (keyword.contains(StringPool.STAR) &&
				!keyword.contains(StringPool.UNDERLINE)) {

				_keyword1 = StringUtil.replace(keyword, '*', "12345");

				break;
			}
		}

		_updateFriendlyURL(
			_layout1.getPlid(), StringPool.FORWARD_SLASH + _keyword1);

		super.testVerify();

		Assert.assertEquals(
			_errorMessages.toString(), 1, _errorMessages.size());

		String errorMessage = _errorMessages.get(0);

		Assert.assertTrue(errorMessage.contains(_keyword1));
	}

	@Test
	public void testVerifyLayoutWithReservedLayoutFriendlyURL()
		throws Exception {

		_updateFriendlyURL(
			_layout1.getPlid(), StringPool.FORWARD_SLASH + _keyword1);

		super.testVerify();

		Assert.assertEquals(
			_errorMessages.toString(), 1, _errorMessages.size());

		String errorMessage = _errorMessages.get(0);

		Assert.assertTrue(errorMessage.contains(_keyword1));
	}

	@Override
	protected VerifyProcess getVerifyProcess() {
		return new VerifyLayout();
	}

	private void _updateFriendlyURL(long plid, String friendlyURL)
		throws Exception {

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"update LayoutFriendlyURL set friendlyURL = ? where plid = " +
					"?")) {

			preparedStatement.setString(1, friendlyURL);
			preparedStatement.setLong(2, plid);

			preparedStatement.executeUpdate();
		}
	}

	private static final String _FRIENDLY_URL_1 = "/friendlyURL1";

	private static final String _FRIENDLY_URL_2 = "/friendlyURL2";

	private static Connection _connection;
	private static List<String> _errorMessages;
	private static String _keyword1;
	private static String _keyword2;
	private static Layout _layout1;
	private static Layout _layout2;

	@Inject
	private static LayoutLocalService _layoutLocalService;

	private static Log _verifyLayoutLog;

	private static class VerifyLayoutLogWrapper extends LogWrapper {

		public VerifyLayoutLogWrapper(Log log) {
			super(log);
		}

		@Override
		public void error(Object msg) {
			_errorMessages.add((String)msg);
		}

	}

}