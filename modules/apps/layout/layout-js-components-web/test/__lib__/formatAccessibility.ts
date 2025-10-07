/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {Result as AxeViolation} from 'axe-core';

function indentMultiline(text: string, spaces = 4): string {
	return text
		.split('\n')
		.map((line) => ' '.repeat(spaces) + line)
		.join('\n');
}

export default function formatAccessibility(
	violations: AxeViolation[]
): string {
	if (!violations.length) {
		return 'No Accessibility issues found.';
	}

	const RED = '\x1b[31m';
	const YELLOW = '\x1b[33m';
	const CYAN = '\x1b[36m';
	const WHITE = '\x1b[37m';
	const BOLD = '\x1b[1m';
	const RESET = '\x1b[0m';

	const output = [];

	output.push('\n');
	output.push(
		`${RED}${BOLD}Accessibility issue(s) found: ${RESET}${WHITE}${
			violations.length
		} rule(s) affecting ${violations.reduce(
			(acc, {nodes}) => acc + nodes.length,
			0
		)} node(s)${RESET}`
	);
	output.push(`${CYAN}${'='.repeat(80)}${RESET}`);

	for (const violation of violations) {
		output.push('');
		output.push(
			`${YELLOW}${BOLD}• RULE: ${RESET}${YELLOW}${violation.description}${RESET}`
		);
		output.push(`  Help   : ${violation.help}`);
		output.push(`  Docs   : ${violation.helpUrl}`);
		output.push(`  Impact : ${violation.impact}`);
		output.push('');

		violation.nodes.forEach((node, index) => {
			if (index > 0) {
				output.push(`${CYAN}    ${'-'.repeat(40)}${RESET}\n`);
			}

			output.push(`${CYAN}  - Affected element:${RESET}`);
			output.push(`    ${node.html}`);
			output.push(`${CYAN}  - Target selector(s):${RESET}`);
			output.push(`    ${node.target.join(', ')}`);

			if (node.failureSummary) {
				output.push(`${CYAN}  - Summary:${RESET}`);
				output.push(indentMultiline(node.failureSummary.trim()));
			}
			output.push('');
		});

		output.push(`${CYAN}${'-'.repeat(80)}${RESET}`);
	}

	return output.join('\n');
}
