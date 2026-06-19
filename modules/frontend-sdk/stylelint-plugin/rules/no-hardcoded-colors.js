/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const stylelint = require('stylelint');

const ruleName = 'liferay/no-hardcoded-colors';
const messages = stylelint.utils.ruleMessages(ruleName, {
	rejected: (color, property) =>
		`Replace the hardcoded color "${color}" in "${property}" with a Clay design token (for example "var(--cadmin-body-color)") so it adapts when the theme switches between light and dark mode. A hardcoded value stays frozen at its light-mode color`,
});

// Match a hardcoded hex color, or an "rgb()"/"hsl()" function whose first
// argument is numeric. Requiring a numeric start leaves relative-color syntax
// derived from a token (for example "rgb(from var(--x) r g b)") untouched.

const HARDCODED_COLOR_REGEXP =
	/#(?:[\da-f]{8}|[\da-f]{6}|[\da-f]{4}|[\da-f]{3})\b|\b(?:rgb|rgba|hsl|hsla)\(\s*[\d.][^)]*\)/i;

// The lookbehind keeps custom functions whose name merely ends in one of these
// keywords (for example the Clay "clay-svg-url(...)" helper) from being treated
// as a token function, since a hyphen on its own is a word boundary.

const TOKEN_FUNCTION_REGEXP = /(?<![\w-])(?:var|light-dark|url)\([^()]*\)/gi;

const rule = (actual) => {
	return function (root, result) {
		const validOptions = stylelint.utils.validateOptions(result, ruleName, {
			actual,
			possible: [true, false],
		});
		if (!validOptions || !actual) {
			return;
		}
		root.walkDecls((decl) => {

			// The "content" property holds string literals (for example in
			// style guides) that may contain "#" sequences or color names,
			// which are not rendered colors.

			if (decl.prop.toLowerCase() === 'content') {
				return;
			}

			// Blank out "var(...)", "light-dark(...)", and "url(...)" calls,
			// replacing each with whitespace of the same length so the colors
			// they legitimately carry (token fallbacks, the theming primitive,
			// SVG data URIs) are ignored while the indices of any remaining
			// hardcoded color stay accurate. Repeat to unwrap nested calls such
			// as "var(--x, var(--y))". A hardcoded color mixed with a token in
			// the same value (for example "var(--foo) #fff") then still
			// surfaces.

			let maskedValue = decl.value;
			let previousValue;

			do {
				previousValue = maskedValue;
				maskedValue = maskedValue.replace(
					TOKEN_FUNCTION_REGEXP,
					(match) => ' '.repeat(match.length)
				);
			} while (maskedValue !== previousValue);

			const match = maskedValue.match(HARDCODED_COLOR_REGEXP);

			if (!match) {
				return;
			}

			// Point the report at the color value itself rather than the start
			// of the declaration, so editors underline the offending color.

			const index =
				decl.prop.length +
				(decl.raws.between || '').length +
				match.index;

			stylelint.utils.report({
				endIndex: index + match[0].length,
				index,
				message: messages.rejected(match[0], decl.prop),
				node: decl,
				result,
				ruleName,
			});
		});
	};
};

rule.ruleName = ruleName;
rule.messages = messages;

module.exports = rule;
