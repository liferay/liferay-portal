/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.log4j;

import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ObjectMessage;
import org.apache.logging.log4j.message.SimpleMessage;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jorge García Jiménez
 */
public class FIPSAuditFilterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Test
	public void testFilterAcceptsAFIPSAuditLogEntry() {
		FIPSAuditFilter fipsAuditFilter = _createFIPSAuditFilter();

		Assert.assertEquals(
			Filter.Result.ACCEPT,
			fipsAuditFilter.filter(
				_createLogEvent(
					FIPSLog4jUtil.getMarker(),
					new ObjectMessage(_fipsAuditLogEntry))));
	}

	@Test
	public void testFilterDeniesAnEventWithoutAFIPSAuditLogEntry() {
		_testFilterDenies(
			FIPSLog4jUtil.getMarker(),
			new ObjectMessage(RandomTestUtil.randomString()));
		_testFilterDenies(
			FIPSLog4jUtil.getMarker(),
			new SimpleMessage(RandomTestUtil.randomString()));
	}

	@Test
	public void testFilterDeniesAnEventWithoutTheMarker() {
		_testFilterDenies(
			MarkerManager.getMarker(RandomTestUtil.randomString()),
			new ObjectMessage(_fipsAuditLogEntry));
		_testFilterDenies(null, new ObjectMessage(_fipsAuditLogEntry));
	}

	private FIPSAuditFilter _createFIPSAuditFilter() {
		FIPSAuditFilter.Builder builder = FIPSAuditFilter.newBuilder();

		return builder.build();
	}

	private LogEvent _createLogEvent(Marker marker, Message message) {
		Log4jLogEvent.Builder builder = Log4jLogEvent.newBuilder();

		builder.setLevel(Level.INFO);
		builder.setLoggerName(RandomTestUtil.randomString());
		builder.setMarker(marker);
		builder.setMessage(message);

		return builder.build();
	}

	private void _testFilterDenies(Marker marker, Message message) {
		FIPSAuditFilter fipsAuditFilter = _createFIPSAuditFilter();

		Assert.assertEquals(
			Filter.Result.DENY,
			fipsAuditFilter.filter(_createLogEvent(marker, message)));
	}

	private final Map<String, Object> _fipsAuditLogEntry =
		Collections.singletonMap(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

}