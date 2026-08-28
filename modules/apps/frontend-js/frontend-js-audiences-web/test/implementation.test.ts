/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import * as audiences from '../src/main/resources/META-INF/resources/main/implementation';

import type {AudiencesDefinition} from '../src/main/resources/META-INF/resources/main/index';

const DEFINITION_URL = 'https://example.com/audiences.json';

function mockAudiencesDefinition(audienceIds: string[]) {
	const audiencesDefinition: AudiencesDefinition = {
		audiences: audienceIds.map((audienceId) => ({
			conjunction: 'AND',
			id: audienceId,
			rules: [
				{attribute: 'hostname', operator: 'eq', value: 'localhost'},
			],
		})),
	};

	(global as any).fetch = jest.fn(() =>
		Promise.resolve({
			json: () => Promise.resolve(audiencesDefinition),
			ok: true,
			status: 200,
			statusText: 'OK',
		})
	);
}

describe('implementation', () => {
	afterEach(async () => {
		jest.restoreAllMocks();

		audiences.setLogEnabled(false);
		audiences.clear();
	});

	describe('runHandlers', () => {
		it('runs every registered handler exactly once', async () => {
			mockAudiencesDefinition(['a', 'b', 'c']);

			await audiences.runDetection(DEFINITION_URL);

			const first = jest.fn();
			const second = jest.fn();
			const third = jest.fn();

			audiences.on('a', first);
			audiences.on('a', second);
			audiences.on('b', third);

			await audiences.runHandlers();

			// The handlers are run in parallel and no order is guaranteed, so
			// the only thing to check is that each one ran, and ran once

			expect(first).toHaveBeenCalledTimes(1);
			expect(second).toHaveBeenCalledTimes(1);
			expect(third).toHaveBeenCalledTimes(1);
		});

		it('does not run the handlers again because a run consumes them', async () => {
			mockAudiencesDefinition(['persistent']);

			await audiences.runDetection(DEFINITION_URL);

			const handler = jest.fn();

			audiences.on('persistent', handler);

			await audiences.runHandlers();

			expect(handler).toHaveBeenCalledTimes(1);

			// Running the handlers clears them, so the next navigation has to
			// register them again

			await audiences.runHandlers();

			expect(handler).toHaveBeenCalledTimes(1);
		});

		it('does not run the handlers cleared before the run', async () => {
			mockAudiencesDefinition(['cleared']);

			await audiences.runDetection(DEFINITION_URL);

			const handler = jest.fn();

			audiences.on('cleared', handler);

			audiences.clearHandlers();

			await audiences.runHandlers();

			expect(handler).not.toHaveBeenCalled();
		});

		it('runs the remaining handlers when one handler throws', async () => {
			mockAudiencesDefinition(['first', 'broken', 'last']);

			await audiences.runDetection(DEFINITION_URL);

			audiences.setLogEnabled(true);

			const consoleLog = jest
				.spyOn(console, 'log')
				.mockImplementation(() => {});

			const firstHandler = jest.fn();
			const lastHandler = jest.fn();

			const brokenHandler = () => {
				throw new Error('The handler is broken');
			};

			audiences.on('first', firstHandler);

			audiences.on('broken', brokenHandler);

			audiences.on('last', lastHandler);

			await audiences.runHandlers();

			expect(firstHandler).toHaveBeenCalledTimes(1);
			expect(lastHandler).toHaveBeenCalledTimes(1);

			expect(consoleLog).toHaveBeenCalledWith(
				expect.anything(),
				expect.anything(),
				expect.stringContaining(
					"Handler 'brokenHandler' of audience 'broken' failed with error"
				)
			);
		});
	});

	describe('getPriority', () => {
		it('reflects the definition order', async () => {
			mockAudiencesDefinition(['a', 'b', 'c']);

			await audiences.runDetection(DEFINITION_URL);

			expect(audiences.getPriority('a')).toBe(0);
			expect(audiences.getPriority('b')).toBe(1);
			expect(audiences.getPriority('c')).toBe(2);
		});

		it('returns Infinity for an audience absent from the definition', async () => {
			mockAudiencesDefinition(['a']);

			await audiences.runDetection(DEFINITION_URL);

			expect(audiences.getPriority('missing')).toBe(Infinity);
		});

		it('refreshes the priorities on a second runDetection', async () => {
			mockAudiencesDefinition(['a', 'b']);

			await audiences.runDetection(DEFINITION_URL);

			expect(audiences.getPriority('a')).toBe(0);
			expect(audiences.getPriority('b')).toBe(1);

			mockAudiencesDefinition(['b', 'a']);

			await audiences.runDetection(DEFINITION_URL);

			expect(audiences.getPriority('b')).toBe(0);
			expect(audiences.getPriority('a')).toBe(1);
		});
	});
});
