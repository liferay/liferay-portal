/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import * as audiences from '../src/main/resources/META-INF/resources/main/implementation';
import {store} from '../src/main/resources/META-INF/resources/main/store';

import type {AudiencesDefinition} from '../src/main/resources/META-INF/resources/main/index';

const DEFINITION_URL = 'https://example.com/audiences.json';

function mockAudiencesDefinition(audienceIds: string[]) {
	const audiencesDefinition: AudiencesDefinition = {
		audiences: audienceIds.map((audienceId) => ({
			conjunction: 'AND',
			id: audienceId,
			rules: [],
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
		store.clear();
	});

	describe('runHandlers', () => {
		it('runs the handlers in the order they were registered and only once', async () => {
			const executionOrder: string[] = [];

			const audienceIds = ['a', 'b', 'c', 'd', 'e'];

			// Store audiences in reverse order so that we really test the registration is honored

			store.setAudienceIds(new Set(audienceIds.reverse()));

			for (const audienceId of audienceIds) {
				audiences.on(audienceId, () => {
					executionOrder.push(audienceId);
				});
			}

			await audiences.runHandlers();

			expect(executionOrder).toEqual(audienceIds);
			expect(executionOrder).toHaveLength(audienceIds.length);
		});

		it('keeps the registered handlers so they run again on the next navigation', async () => {
			let runCount = 0;

			store.setAudienceIds(new Set(['persistent']));

			audiences.on('persistent', () => {
				runCount += 1;
			});

			await audiences.runHandlers();

			expect(runCount).toBe(1);

			// The handler stays registered so a later navigation runs it again

			await audiences.runHandlers();

			expect(runCount).toBe(2);
		});

		it('does not run handlers after they are cleared', async () => {
			let runCount = 0;

			store.setAudienceIds(new Set(['cleared']));

			audiences.on('cleared', () => {
				runCount += 1;
			});

			await audiences.runHandlers();

			expect(runCount).toBe(1);

			// Navigating to another page clears the previous page's handlers

			audiences.clearHandlers();

			await audiences.runHandlers();

			expect(runCount).toBe(1);
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
