/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// Provided by the shared Jest configuration in @liferay/node-scripts.

// eslint-disable-next-line @liferay/no-extraneous-dependencies
const JSDOMEnvironment = require('jest-environment-jsdom');

const TEST_URL = 'https://learn.liferay.com/';

/**
 * Runs a test file on a secure subdomain instead of the default
 * `http://localhost`. Cookie behavior is only observable there: jsdom drops a
 * `Secure` cookie written from an insecure origin, and rejects a `Domain`
 * attribute that is not a suffix of the current host, which is what the user id
 * cookie shared across subdomains relies on.
 *
 * A test file opts in through the `@jest-environment` docblock. Both keys are
 * set because Jest 27 reads the URL from `testURL` while later versions read it
 * from `testEnvironmentOptions`, and the shared portal configuration sets the
 * latter to `http://localhost`.
 */
class SubdomainEnvironment extends JSDOMEnvironment {
	constructor(config, context) {
		super(
			{
				...config,
				testEnvironmentOptions: {
					...config.testEnvironmentOptions,
					url: TEST_URL,
				},
				testURL: TEST_URL,
			},
			context
		);
	}
}

module.exports = SubdomainEnvironment;
