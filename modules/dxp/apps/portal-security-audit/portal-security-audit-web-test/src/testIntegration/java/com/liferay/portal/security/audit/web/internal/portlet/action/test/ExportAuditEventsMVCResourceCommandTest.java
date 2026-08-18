/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ProgressTracker;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.audit.AuditEvent;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christian Moura
 */
@FeatureFlag("LPD-6417")
@RunWith(Arquillian.class)
public class ExportAuditEventsMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testBuildCSV() throws Exception {
		AuditEvent auditEvent = (AuditEvent)ProxyUtil.newProxyInstance(
			AuditEvent.class.getClassLoader(),
			new Class<?>[] {AuditEvent.class},
			(proxy, method, arguments) -> {
				Class<?> returnType = method.getReturnType();

				if (returnType == int.class) {
					return 0;
				}

				if (returnType == long.class) {
					return 0L;
				}

				return null;
			});

		String csv = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_buildCSV",
			new Class<?>[] {List.class, String[].class, ProgressTracker.class},
			ListUtil.fromArray(auditEvent),
			_getColumns(TestPropsValues.getCompanyId()), null);

		String[] lines = StringUtil.split(csv, CharPool.NEW_LINE);

		Assert.assertEquals(Arrays.toString(lines), 2, lines.length);
		Assert.assertEquals(
			StringUtil.merge(_COLUMNS_DEFAULT, StringPool.COMMA), lines[0]);
	}

	@Test
	public void testGetColumns() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_CONFIGURATION_PID,
					HashMapDictionaryBuilder.<String, Object>put(
						"columns", new String[] {RandomTestUtil.randomString()}
					).build())) {

			Assert.assertArrayEquals(
				_COLUMNS_DEFAULT, _getColumns(TestPropsValues.getCompanyId()));
		}
	}

	@FeatureFlag(enable = false, value = "LPD-6417")
	@Test
	public void testGetColumnsWhenFeatureFlagIsDisabled() throws Exception {
		String[] columns = {
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		};

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_CONFIGURATION_PID,
					HashMapDictionaryBuilder.<String, Object>put(
						"columns", columns
					).build())) {

			Assert.assertArrayEquals(
				columns, _getColumns(TestPropsValues.getCompanyId()));
		}
	}

	private String[] _getColumns(long companyId) {
		return ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getColumns", new Class<?>[] {long.class},
			companyId);
	}

	private static final String[] _COLUMNS_DEFAULT = {
		"additionalInfo", "className", "classPK", "clientHost", "clientIP",
		"companyId", "eventType", "message", "serverName", "serverPort",
		"sessionID", "timestamp", "userEmailAddress", "userId", "userLogin",
		"userName"
	};

	private static final String _CONFIGURATION_PID =
		"com.liferay.portal.security.audit.router.configuration." +
			"CSVLogMessageFormatterConfiguration";

	@Inject(filter = "mvc.command.name=/audit/export_audit_events")
	private MVCResourceCommand _mvcResourceCommand;

}