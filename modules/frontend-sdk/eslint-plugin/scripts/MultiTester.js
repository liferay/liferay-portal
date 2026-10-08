/**
 * SPDX-FileCopyrightText: © 2020 Liferay, Inc. <https://liferay.com>
 * SPDX-License-Identifier: MIT
 */

const {RuleTester} = require('eslint');

const generalConfig = require('../configs/general');

/**
 * Wrapper for ESLint's RuleTester class that runs tests against:
 *
 * -   The default "espree" parser.
 *
 * -   "@babel/eslint-parser", that we use in liferay-portal (see the
 *      `parser` in configs/general.js).
 *
 * -    `@typescript-eslint/parser`, that we use in Clay:
 *
 *      https://github.com/liferay/clay/blob/c6795fd6/.eslintrc.js#L15
 */
class MultiTester extends RuleTester {
	constructor(options) {
		super(options);

		this._liferay = {
			parsers: {
				babel: new RuleTester({
					...options,
					parser: require.resolve('@babel/eslint-parser'),
					parserOptions: {
						...options?.parserOptions,
						babelOptions: generalConfig.parserOptions.babelOptions,
						requireConfigFile: false,
					},
				}),
				espree: new RuleTester(options),
				typescript: new RuleTester({
					...options,
					parser: require.resolve('@typescript-eslint/parser'),
				}),
			},
		};
	}

	run(name, rule, tests) {
		Object.entries(this._liferay.parsers).forEach(([key, parser]) => {

			// Not all tests can run on all parsers, so we filter first.

			const handleSkips = (test) => {
				const {skip, ...config} = test;
				if (skip && skip.includes(key)) {
					return false;
				}

				return config;
			};

			const filteredTests = {
				invalid: tests.invalid.map(handleSkips).filter(Boolean),
				valid: tests.valid.map(handleSkips).filter(Boolean),
			};

			parser.run(`${name} (parser: ${key})`, rule, filteredTests);
		});
	}
}

module.exports = MultiTester;
