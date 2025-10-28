/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sanitizeHTML} from '../../src/main/resources/META-INF/resources/js/util/sanitize';

describe('sanitizeHTML function', () => {
	test('handles multiple instances of potentially harmful patterns', () => {
		const html = `
			<script>alert("Hello");</script>
			<div innerHTML="some content"></div>
			<?php echo "World"; ?>
			<asp:Label runat="server">Hello</asp:Label>
			<button onclick="alert('Hello')">Click</button>
      `;

		const sanitized = sanitizeHTML(html);

		expect(sanitized).not.toContain('alert(');
		expect(sanitized).not.toContain('<asp:Label');
		expect(sanitized).not.toMatch(/<?php/i);
		expect(sanitized).not.toMatch(/innerHTML\s*=/i);
		expect(sanitized).not.toMatch(/on\w+\s*=/i);
	});

	test('returns an empty string when the input is an empty string', () => {
		const html = '';
		const sanitized = sanitizeHTML(html);

		expect(sanitized).toBe('');
	});

	test('returns undefined when the input is undefined', () => {
		const html = undefined;
		const sanitized = sanitizeHTML(html);

		expect(sanitized).toBe(undefined);
	});
});
