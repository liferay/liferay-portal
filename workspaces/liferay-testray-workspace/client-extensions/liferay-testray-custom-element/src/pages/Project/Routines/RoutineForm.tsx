/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {useEffect, useMemo, useState} from 'react';
import {useForm} from 'react-hook-form';
import {useOutletContext} from 'react-router-dom';
import {KeyedMutator} from 'swr';
import {withPagePermission} from '~/hoc/withPagePermission';
import {useFetch} from '~/hooks/useFetch';

import Form from '../../../components/Form';
import Container from '../../../components/Layout/Container';
import {useHeader} from '../../../hooks';
import useFormActions from '../../../hooks/useFormActions';
import i18n from '../../../i18n';
import yupSchema, {yupResolver} from '../../../schema/yup';
import {
	APIResponse,
	TestrayProject,
	TestrayRoutine,
	TestrayTeam,
	testrayRoutineImpl,
} from '../../../services/rest';
import ParentRoutinesForm from './ParentRoutinesForm';

type RoutineForm = typeof yupSchema.routine.__outputType;

type OutletContext = {
	mutateTestrayRoutine: KeyedMutator<TestrayRoutine>;
	testrayProject: TestrayProject;
	testrayRoutine?: TestrayRoutine;
};

const RoutineForm = () => {
	useHeader({headerActions: {actions: []}, tabs: [], timeout: 150});

	const [routineIds, setRoutineIds] = useState<number[]>([]);

	const {mutateTestrayRoutine, testrayProject, testrayRoutine} =
		useOutletContext<OutletContext>();

	const {data: parentTestrayRoutines} = useFetch<TestrayRoutine>(
		`/routines/${testrayRoutine?.id}`,
		{
			params: {
				fields: 'parentRoutines.id',
				nestedFields: 'parentRoutines',
			},
		}
	);

	const parentTestrayRoutineIds = useMemo(() => {
		return (
			parentTestrayRoutines?.parentRoutines.map((parent) => parent.id) ||
			[]
		);
	}, [parentTestrayRoutines]);

	useEffect(() => {
		setRoutineIds(parentTestrayRoutineIds);
	}, [parentTestrayRoutineIds]);

	const {
		formState: {errors, isSubmitting},
		handleSubmit,
		register,
		setValue,
		watch,
	} = useForm<RoutineForm>({
		defaultValues: {
			autoanalyze: false,
			r_teamToRoutines_c_teamId: 0,
			...testrayRoutine,
		},
		resolver: yupResolver(yupSchema.routine),
	});

	const {
		form: {onClose, onError, onSave, onSubmit},
	} = useFormActions();

	const _onSubmit = async (form: RoutineForm) => {
		const parentRoutinesToRemove = parentTestrayRoutineIds
			?.filter((id: number) => !routineIds.includes(id))
			.map((id: number) => id);

		if (parentRoutinesToRemove?.length && testrayRoutine) {
			await testrayRoutineImpl.removeRelatedEntriesBatch(
				testrayRoutine?.id,
				parentRoutinesToRemove,
				'parentRoutines'
			);
		}

		if (testrayRoutine) {
			await testrayRoutineImpl.updateRelatedEntriesBatch(
				testrayRoutine?.id,
				routineIds,
				'parentRoutines'
			);
		}

		onSubmit(
			{
				...form,
				projectId: testrayProject.id,
			},
			{
				create: (...params) => testrayRoutineImpl.create(...params),
				update: (...params) => testrayRoutineImpl.update(...params),
			}
		)
			.then(mutateTestrayRoutine)
			.then(onSave)
			.catch(onError);
	};

	const autoanalyze = watch('autoanalyze');
	const teamId = watch('r_teamToRoutines_c_teamId');

	const {data: testrayTeamsData, loading} = useFetch<
		APIResponse<TestrayTeam>
	>('/teams', {
		params: {
			fields: 'id,name',
			filter: `projectId eq '${testrayProject.id}'`,
			pageSize: -1,
		},
	});

	const testrayTeams = useMemo(
		() => testrayTeamsData?.items ?? [],
		[testrayTeamsData?.items]
	);

	return (
		<Container className="container">
			<Form.Input
				errors={errors}
				label={i18n.translate('name')}
				name="name"
				register={register}
				required
			/>

			<ClayCheckbox
				checked={autoanalyze}
				className="d-flex"
				label={i18n.translate('autoanalyze')}
				onChange={() => setValue('autoanalyze', !autoanalyze)}
			>
				<ClayTooltipProvider>
					<span
						data-tooltip-floating="true"
						title={i18n.translate(
							'allow-testray-to-automatically-populate-assignee-comments-and-issues-when-an-error-matches-the-previous-result'
						)}
					>
						<ClayIcon
							className="align-bottom ml-2"
							data-tooltip-align="left"
							symbol="question-circle-full"
						/>
					</span>
				</ClayTooltipProvider>
			</ClayCheckbox>

			<Form.Select
				errors={errors}
				isLoading={loading}
				label={
					<>
						{i18n.translate('main-team')}
						<ClayTooltipProvider>
							<span
								data-tooltip-floating="false"
								title={i18n.translate(
									'allow-testray-to-automatically-filter-results-based-on-the-selected-team'
								)}
							>
								<ClayIcon
									className="align-center"
									data-tooltip-align="left"
									symbol="question-circle-full"
								/>
							</span>
						</ClayTooltipProvider>
					</>
				}
				name="r_teamToRoutines_c_teamId"
				options={testrayTeams.map(({id: value, name: label}) => ({
					label,
					value,
				}))}
				register={register}
				value={teamId as number}
			/>

			<ParentRoutinesForm
				currentRoutineId={testrayRoutine?.id || 0}
				routineIds={routineIds}
				setRoutineIds={setRoutineIds}
			/>

			<Form.Footer
				onClose={onClose}
				onSubmit={handleSubmit(_onSubmit)}
				primaryButtonProps={{loading: isSubmitting}}
			/>
		</Container>
	);
};

export default withPagePermission(RoutineForm, {
	createPath: 'project/:projectId/routines/create',
	restImpl: testrayRoutineImpl,
});
