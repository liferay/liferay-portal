/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-env jest */

const stylelint = require('stylelint');

const config = {
	plugins: [require.resolve('../index.js')],
	rules: {
		'liferay/no-hardcoded-colors': true,
	},
};

function lint(code) {
	return stylelint
		.lint({code, config})
		.then((result) => result.results[0].warnings);
}

describe('liferay/no-hardcoded-colors', () => {
	it('accepts a color that references a design token', async () => {
		expect(await lint('a { color: var(--cadmin-body-color); }')).toEqual(
			[]
		);
	});

	it('accepts a token reference with a hardcoded fallback', async () => {
		expect(
			await lint('a { color: var(--cadmin-body-color, #fff); }')
		).toEqual([]);
	});

	it('accepts a light-dark() value', async () => {
		expect(await lint('a { color: light-dark(#fff, #111116); }')).toEqual(
			[]
		);
	});

	it('accepts color keywords', async () => {
		expect(
			await lint('a { color: inherit; border-color: transparent; }')
		).toEqual([]);
	});

	it('accepts a hex inside a url() data URI', async () => {
		expect(
			await lint(
				'a { background-image: url("data:image/svg+xml,<svg fill=\'%23fff\'/>"); }'
			)
		).toEqual([]);
	});

	it('accepts a multi-value property of only tokens', async () => {
		expect(await lint('a { background: var(--foo), var(--bar); }')).toEqual(
			[]
		);
	});

	it('accepts a relative color derived from a token', async () => {
		expect(
			await lint('a { background: rgb(from var(--shimmer) r g b / 0); }')
		).toEqual([]);
	});

	it('accepts a hex inside a content string', async () => {
		expect(await lint('a { content: "#fff"; }')).toEqual([]);
	});

	it('rejects a hardcoded color mixed with a token in a shorthand', async () => {
		const reports = await lint('a { background: var(--foo), #fff; }');

		expect(reports).toHaveLength(1);
		expect(reports[0].rule).toBe('liferay/no-hardcoded-colors');
	});

	it('rejects a hardcoded color in a gradient beside a token', async () => {
		const reports = await lint(
			'a { background: linear-gradient(0deg, #fff, #000), var(--y); }'
		);

		expect(reports).toHaveLength(1);
		expect(reports[0].rule).toBe('liferay/no-hardcoded-colors');
	});

	it('rejects a hardcoded color mixed with a token in a space-separated shorthand', async () => {
		const reports = await lint('a { background: var(--foo) #fff; }');

		expect(reports).toHaveLength(1);
		expect(reports[0].rule).toBe('liferay/no-hardcoded-colors');
	});

	it('rejects a hardcoded color in a gradient that also contains a token', async () => {
		const reports = await lint(
			'a { background: linear-gradient(0deg, var(--x), #000); }'
		);

		expect(reports).toHaveLength(1);
		expect(reports[0].rule).toBe('liferay/no-hardcoded-colors');
	});

	it('rejects a hardcoded color in a custom function whose name ends in a token keyword', async () => {
		const reports = await lint('a { color: custom-url(#abc); }');

		expect(reports).toHaveLength(1);
		expect(reports[0].rule).toBe('liferay/no-hardcoded-colors');
	});

	it('rejects a hardcoded hex color', async () => {
		const reports = await lint('a { color: #fff; }');

		expect(reports).toHaveLength(1);
		expect(reports[0].rule).toBe('liferay/no-hardcoded-colors');
	});

	it('names the offending color in the message', async () => {
		const reports = await lint('a { color: #868896; }');

		expect(reports[0].text).toContain('"#868896"');
	});

	it('rejects a hardcoded rgb() color', async () => {
		const reports = await lint('a { background-color: rgb(0, 0, 0); }');

		expect(reports).toHaveLength(1);
		expect(reports[0].rule).toBe('liferay/no-hardcoded-colors');
	});

	it('rejects a hardcoded hsl() color in a border shorthand', async () => {
		const reports = await lint('a { border: 1px solid hsl(0, 0%, 0%); }');

		expect(reports).toHaveLength(1);
		expect(reports[0].rule).toBe('liferay/no-hardcoded-colors');
	});
});
