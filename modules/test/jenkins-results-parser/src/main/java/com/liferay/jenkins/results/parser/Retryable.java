/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

/**
 * @author Kenji Heigel
 */
public abstract class Retryable<T> {

	public Retryable() {
		this(5, 30, true);
	}

	public Retryable(
		boolean exceptionOnFail, int maxRetries, int retryPeriod,
		boolean verbose) {

		_exceptionOnFail = exceptionOnFail;
		this.maxRetries = maxRetries;
		_retryPeriod = retryPeriod;
		_verbose = verbose;
	}

	public Retryable(int maxRetries, int retryPeriod, boolean verbose) {
		this(true, maxRetries, retryPeriod, verbose);
	}

	public abstract T execute();

	public final T executeWithRetries() {
		int retryCount = 0;

		while (true) {
			try {
				return execute();
			}
			catch (Exception exception) {
				retryCount++;

				if (_verbose) {
					System.out.println("An error has occurred: " + exception);
				}

				if ((maxRetries >= 0) && (retryCount > maxRetries)) {
					if (_exceptionOnFail) {
						throw exception;
					}

					return null;
				}

				sleep(_retryPeriod * 1000);

				String retryMessage = getRetryMessage(retryCount);

				if (!JenkinsResultsParserUtil.isNullOrEmpty(retryMessage)) {
					System.out.println(retryMessage);
				}
			}
		}
	}

	public void sleep(long duration) {
		JenkinsResultsParserUtil.sleep(duration);
	}

	protected final void breakLoop() {
		maxRetries = 0;
	}

	protected String getRetryMessage(int retryCount) {
		return JenkinsResultsParserUtil.combine(
			"Retry attempt ", String.valueOf(retryCount), " of ",
			String.valueOf(maxRetries));
	}

	protected int maxRetries;

	private boolean _exceptionOnFail;
	private int _retryPeriod;
	private boolean _verbose;

}