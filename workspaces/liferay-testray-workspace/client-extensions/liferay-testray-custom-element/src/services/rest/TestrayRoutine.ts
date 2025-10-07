/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import TestrayError from '../../TestrayError';
import Rest from '../../core/Rest';
import SearchBuilder from '../../core/SearchBuilder';
import i18n from '../../i18n';
import yupSchema from '../../schema/yup';
import {testrayBuildImpl} from './TestrayBuild';
import {APIResponse, TestrayRoutine} from './types';

type RoutineFormType = typeof yupSchema.routine.__outputType & {
	projectId: number;
};

class TestrayRoutineImpl extends Rest<RoutineFormType, TestrayRoutine> {
	constructor() {
		super({
			adapter: (routine) => ({
				autoanalyze: routine.autoanalyze,
				id: routine.id,
				name: routine.name,
				parentRoutines: routine.parentRoutines,
				r_routineToProjects_c_projectId: routine.projectId,
				r_teamToRoutines_c_teamId: routine.r_teamToRoutines_c_teamId,
			}),
			nestedFields: 'routineToBuilds',
			transformData: (testrayRoutine) => ({
				...testrayRoutine,
				builds:
					testrayBuildImpl.transformData(
						testrayRoutine.routineToBuilds
					) ?? [],
			}),
			uri: 'routines',
		});
	}

	protected async validate(routine: RoutineFormType, id?: number) {
		const searchBuilder = new SearchBuilder({useURIEncode: true});

		if (id) {
			searchBuilder.ne('id', id).and();
		}

		const filter = searchBuilder
			.eq('name', routine.name)
			.and()
			.eq('projectId', routine.projectId)
			.build();

		const response = await this.fetcher<APIResponse<TestrayRoutine>>(
			`/routines?filter=${filter}`
		);

		if (response?.totalCount) {
			throw new TestrayError(
				i18n.sub('the-x-name-already-exists', 'routine')
			);
		}
	}

	protected async beforeCreate(routine: RoutineFormType): Promise<void> {
		await this.validate(routine);
	}

	protected async beforeUpdate(
		id: number,
		routine: RoutineFormType
	): Promise<void> {
		await this.validate(routine, id);
	}
}

const testrayRoutineImpl = new TestrayRoutineImpl();

export {testrayRoutineImpl};
