/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Uuid} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Uuid';
import buildStructureErrorAction from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/buildStructureErrorAction';

const UUID = 'structure-uuid' as Uuid;

describe('buildStructureErrorAction', () => {
	it('routes a friendly URL collision to the friendly URL field', () => {
		const action = buildStructureErrorAction({
			error: 'slug-in-use',
			uuid: UUID,
		});

		expect(action).toEqual({
			error: 'in-use',
			property: 'slug',
			type: 'add-error',
			uuid: UUID,
		});
	});

	it('routes a name collision to the name field', () => {
		const action = buildStructureErrorAction({
			error: 'in-use',
			uuid: UUID,
		});

		expect(action).toEqual({
			error: 'in-use',
			property: 'name',
			type: 'add-error',
			uuid: UUID,
		});
	});

	it('routes an unexpected error to the global scope', () => {
		const action = buildStructureErrorAction({
			error: 'unexpected',
			uuid: UUID,
		});

		expect(action).toEqual({
			error: 'unexpected',
			property: 'global',
			type: 'add-error',
			uuid: UUID,
		});
	});
});
