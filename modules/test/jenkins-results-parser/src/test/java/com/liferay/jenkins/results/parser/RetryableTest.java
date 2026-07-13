/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class RetryableTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testBreakLoop() {
		AtomicInteger executeCount = new AtomicInteger();

		Retryable<Void> retryable = new Retryable<Void>(false, 5, 0, false) {

			@Override
			public Void execute() {
				executeCount.incrementAndGet();

				breakLoop();

				throw new RuntimeException(RandomTestUtil.randomString());
			}

		};

		retryable.executeWithRetries();

		testEquals("1", String.valueOf(executeCount.get()));
	}

}