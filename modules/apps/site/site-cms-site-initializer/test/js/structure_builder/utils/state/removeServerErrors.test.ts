/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Uuid} from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Uuid';
import removeServerErrors from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/state/removeServerErrors';
import {ErrorMap} from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/validation';

const UUID = 'structure-uuid' as Uuid;

describe('removeServerErrors', () => {
	it('removes the global errors reported by the server', () => {
		const invalids = new Map<Uuid, ErrorMap>([
			[UUID, new Map([['global', 'permission']])],
		]);

		expect(removeServerErrors({invalids, uuid: UUID}).has(UUID)).toBe(
			false
		);
	});

	it('keeps the field errors, which are cleared when the field is edited', () => {
		const invalids = new Map<Uuid, ErrorMap>([
			[
				UUID,
				new Map([
					['erc', 'in-use'],
					['global', 'unexpected'],
					['name', 'in-use'],
					['slug', 'in-use'],
				]),
			],
		]);

		expect(removeServerErrors({invalids, uuid: UUID}).get(UUID)).toEqual(
			new Map([
				['erc', 'in-use'],
				['name', 'in-use'],
				['slug', 'in-use'],
			])
		);
	});

	it('keeps the global errors computed by the client', () => {
		const invalids = new Map<Uuid, ErrorMap>([
			[UUID, new Map([['global', 'default-language-label']])],
		]);

		expect(removeServerErrors({invalids, uuid: UUID}).get(UUID)).toEqual(
			new Map([['global', 'default-language-label']])
		);
	});

	it('does not mutate the given invalids', () => {
		const invalids = new Map<Uuid, ErrorMap>([
			[UUID, new Map([['global', 'unexpected']])],
		]);

		removeServerErrors({invalids, uuid: UUID});

		expect(invalids.get(UUID)).toEqual(new Map([['global', 'unexpected']]));
	});
});
