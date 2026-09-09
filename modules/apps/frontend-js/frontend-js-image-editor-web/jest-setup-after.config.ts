/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const unexpected: string[] = [];

const failOn =
	(original: (...args: unknown[]) => void) =>
	(...args: unknown[]) => {
		unexpected.push(args.map(String).join(' '));

		original(...args);
	};

// eslint-disable-next-line no-console
console.error = failOn(console.error.bind(console));

// eslint-disable-next-line no-console
console.warn = failOn(console.warn.bind(console));

afterEach(() => {
	if (unexpected.length) {
		const messages = unexpected.splice(0);

		throw new Error(`Unexpected console output:\n${messages.join('\n')}`);
	}
});
