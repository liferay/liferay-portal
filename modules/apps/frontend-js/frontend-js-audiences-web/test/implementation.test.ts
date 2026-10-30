/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import * as audiences from '../src/main/resources/META-INF/resources/main/implementation';

import type {AudiencesDefinition} from '../src/main/resources/META-INF/resources/main/index';

const COOKIE_NAME = 'com.liferay.frontend.js.audiences';

const DEFINITION_URL = 'https://example.com/audiences.json';

function getCookieValue() {
	return document.cookie
		.split('; ')
		.find((item) => item.startsWith(`${COOKIE_NAME}=`))
		?.slice(COOKIE_NAME.length + 1);
}

function getCookieAudienceIds() {
	const value = getCookieValue();

	if (value === undefined) {
		return undefined;
	}

	return JSON.parse(decodeURIComponent(value));
}

function mockAudiencesDefinition(audienceIds: string[]) {
	const audiencesDefinition: AudiencesDefinition = {
		audiences: audienceIds.map((audienceId) => ({
			conjunction: 'AND',
			id: audienceId,
			rules: [
				{attribute: 'hostname', operator: 'eq', value: 'localhost'},
			],
			scope: [],
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

	describe('runDetection', () => {
		it('detects every audience when filterAudiences is absent', async () => {
			mockAudiencesDefinition(['a', 'b', 'c']);

			await audiences.runDetection(DEFINITION_URL);

			expect([...audiences.get()].sort()).toEqual(['a', 'b', 'c']);
		});

		it('detects only the audiences accepted by filterAudiences', async () => {
			mockAudiencesDefinition(['a', 'b', 'c']);

			await audiences.runDetection(DEFINITION_URL, {
				filterAudiences: (audience) => audience.id !== 'b',
			});

			expect([...audiences.get()].sort()).toEqual(['a', 'c']);
		});
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

	describe('set', () => {
		it('replaces the detected audiences', async () => {
			mockAudiencesDefinition(['a', 'b']);

			await audiences.runDetection(DEFINITION_URL);

			audiences.set(['c']);

			expect([...audiences.get()]).toEqual(['c']);
		});

		it('sets the priorities in the given order', () => {
			audiences.set(['b', 'a']);

			expect(audiences.getAudienceIndex('a')).toBe(1);
			expect(audiences.getAudienceIndex('b')).toBe(0);
		});

		it('runs the handlers of the audiences it sets', async () => {
			const handler = jest.fn();
			const otherHandler = jest.fn();

			audiences.on('a', handler);
			audiences.on('b', otherHandler);

			audiences.set(['a']);

			await audiences.runHandlers();

			expect(handler).toHaveBeenCalledTimes(1);
			expect(otherHandler).not.toHaveBeenCalled();
		});
	});

	describe('cookie', () => {
		afterEach(() => {
			document.cookie = `${COOKIE_NAME}=; max-age=0; path=/`;
			document.cookie = 'CONSENT_TYPE_FUNCTIONAL=; max-age=0; path=/';
		});

		it('is kept by clear', () => {
			audiences.set(['a']);

			audiences.clear();

			expect(getCookieAudienceIds()).toEqual(['a']);
		});

		it('is not written without functional consent', () => {
			document.cookie = 'CONSENT_TYPE_FUNCTIONAL=false; path=/';

			audiences.set(['a']);

			expect(getCookieAudienceIds()).toBeUndefined();
		});

		it('stores an empty list when no audience is detected', async () => {
			audiences.set(['a']);

			mockAudiencesDefinition([]);

			await audiences.runDetection(DEFINITION_URL);

			expect(getCookieAudienceIds()).toEqual([]);
		});

		it('stores the audiences detected by runDetection', async () => {
			mockAudiencesDefinition(['a', 'b']);

			await audiences.runDetection(DEFINITION_URL);

			expect(getCookieAudienceIds()).toEqual(['a', 'b']);
		});

		it('stores the audiences given to set', () => {
			audiences.set(['c', 'd']);

			expect(getCookieAudienceIds()).toEqual(['c', 'd']);
		});

		it('stores audiences with any character in their ERCs', () => {
			audiences.set(['a\nb', 'c;d', 'e,"f"', 'g\\h', 'ñ 😀']);

			expect(getCookieAudienceIds()).toEqual([
				'a\nb',
				'c;d',
				'e,"f"',
				'g\\h',
				'ñ 😀',
			]);
		});

		it('stores audiences with ERCs escaped by the sanitizer', () => {
			audiences.set(['%41', '&lt;b&gt;']);

			expect(getCookieAudienceIds()).toEqual(['%41', '&lt;b&gt;']);
		});

		it('stores audiences with numeric ERCs as strings', () => {
			audiences.set(['12345']);

			expect(getCookieAudienceIds()).toEqual(['12345']);
		});

		it('stores only characters allowed in a cookie value', () => {
			audiences.set(['a\nb', 'c;d', 'e,"f"', 'g\\h', 'ñ 😀']);

			expect(getCookieValue()).toMatch(
				/^[\x21\x23-\x2B\x2D-\x3A\x3C-\x5B\x5D-\x7E]*$/
			);
		});
	});

	describe('getAudienceIndex', () => {
		it('reflects the definition order', async () => {
			mockAudiencesDefinition(['a', 'b', 'c']);

			await audiences.runDetection(DEFINITION_URL);

			expect(audiences.getAudienceIndex('a')).toBe(0);
			expect(audiences.getAudienceIndex('b')).toBe(1);
			expect(audiences.getAudienceIndex('c')).toBe(2);
		});

		it('returns undefined for an audience absent from the definition', async () => {
			mockAudiencesDefinition(['a']);

			await audiences.runDetection(DEFINITION_URL);

			expect(audiences.getAudienceIndex('missing')).toBeUndefined();
		});

		it('refreshes the priorities on a second runDetection', async () => {
			mockAudiencesDefinition(['a', 'b']);

			await audiences.runDetection(DEFINITION_URL);

			expect(audiences.getAudienceIndex('a')).toBe(0);
			expect(audiences.getAudienceIndex('b')).toBe(1);

			mockAudiencesDefinition(['b', 'a']);

			await audiences.runDetection(DEFINITION_URL);

			expect(audiences.getAudienceIndex('b')).toBe(0);
			expect(audiences.getAudienceIndex('a')).toBe(1);
		});
	});
});
