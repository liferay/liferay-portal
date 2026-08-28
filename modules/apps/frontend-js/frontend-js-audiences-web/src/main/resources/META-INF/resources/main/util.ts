/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function formatError(error: any): string {
	if (error instanceof Error) {
		let message = error.message;

		if (error.cause) {
			message += '\n';
			message += '  caused by: ';
			message += indent(4, false, formatError(error.cause));
		}

		return message;
	}
	else {
		return String(error);
	}
}

export function indent(
	spaces: number,
	firstLineIndent: boolean,
	multiLineMessage: string
): string {
	const padding = ' '.repeat(spaces);

	return multiLineMessage
		.split('\n')
		.map((line: string, index: number) => {
			if (index === 0 && !firstLineIndent) {
				return line;
			}

			return `${padding}${line}`;
		})
		.join('\n');
}

export function waitForAbort(signal: AbortSignal): Promise<void> {
	return new Promise<void>((resolve) => {
		if (signal.aborted) {
			resolve();

			return;
		}

		signal.addEventListener('abort', () => resolve(), {once: true});
	});
}
