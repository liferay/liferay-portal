/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class RetryableTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testBreakLoop() {
		BreakLoopRetryable breakLoopRetryable = new BreakLoopRetryable();

		breakLoopRetryable.executeWithRetries();

		testEquals("1", String.valueOf(breakLoopRetryable.getExecuteCount()));
	}

	private static class BreakLoopRetryable extends Retryable<Void> {

		public BreakLoopRetryable() {
			super(false, 5, 0, false);
		}

		@Override
		public Void execute() {
			_executeCount++;

			breakLoop();

			throw new RuntimeException("Permanent failure");
		}

		public int getExecuteCount() {
			return _executeCount;
		}

		private int _executeCount;

	}

}