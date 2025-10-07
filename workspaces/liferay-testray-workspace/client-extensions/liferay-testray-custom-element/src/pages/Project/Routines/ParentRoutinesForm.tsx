/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {Dispatch, SetStateAction, useCallback} from 'react';

import Form from '../../../components/Form';
import useFormModal from '../../../hooks/useFormModal';
import i18n from '../../../i18n';
import {TestrayRoutine} from '../../../services/rest';
import RoutineFormSelectModal from './RoutineFormSelectModal';
import RoutineListView from './RoutineListView';

type ChildRoutinesFormProps = {
	currentRoutineId: number;
	routineIds: number[];
	setRoutineIds: Dispatch<SetStateAction<number[]>>;
};

const ParentRoutinesForm: React.FC<ChildRoutinesFormProps> = ({
	currentRoutineId,
	routineIds,
	setRoutineIds,
}) => {
	const {modal} = useFormModal<number[]>({
		onSave: useCallback(
			(newRoutineIds: number[], updateRoutines = true) => {
				if (newRoutineIds.length) {
					if (updateRoutines) {
						return setRoutineIds(newRoutineIds);
					}
				}

				setRoutineIds([]);
			},
			[setRoutineIds]
		),
	});

	return (
		<>
			<>
				<h4>{i18n.translate('parent-routines')}</h4>

				<Form.Divider />
			</>

			<ClayButton
				className="mb-4"
				displayType="secondary"
				onClick={() => {
					modal.open(routineIds);
				}}
			>
				{i18n.translate('add-parent-routine')}
			</ClayButton>

			{routineIds.length ? (
				<RoutineListView
					listViewProps={{
						managementToolbarProps: {visible: false},
						tableProps: {
							actions: [
								{
									action: ({id}: TestrayRoutine) =>
										setRoutineIds((prevRoutines) =>
											prevRoutines.filter(
												(prevRoutines: number) =>
													prevRoutines !== id
											)
										),
									icon: 'trash',
									name: i18n.translate('delete'),
								},
							] as any,
							columns: [
								{
									key: 'name',
									size: 'md',
									value: i18n.translate('name'),
								},
							],
						},
						variables: {
							filter: `id in ('${routineIds.join(`','`)}')`,
						},
					}}
					resource="/routines"
				/>
			) : (
				<ClayAlert>
					{i18n.translate('there-are-no-parent-routines-selected')}
				</ClayAlert>
			)}

			<RoutineFormSelectModal
				currentRoutineId={currentRoutineId}
				modal={modal}
				selectedRoutineIds={routineIds}
			/>
		</>
	);
};

export default ParentRoutinesForm;
