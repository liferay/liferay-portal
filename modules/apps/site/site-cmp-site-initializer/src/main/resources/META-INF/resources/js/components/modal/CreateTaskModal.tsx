/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal from '@clayui/modal';
import {AssigneeValue} from '@liferay/object-dynamic-data-mapping-form-field-type';
import {DatePicker} from '@liferay/object-js-components-web';
import {
	FieldPicker,
	FieldText,
	FieldWrapper,
	displayCreateSuccessToast,
	displayErrorToast,
	required,
	validate,
} from '@liferay/site-cms-site-initializer';
import {useFormik} from 'formik';
import React, {useEffect, useState} from 'react';

import {getAllProjects, getAllStates, postTaskByScope} from '../../utils/api';
import {IProjectObjectEntry} from '../../utils/types';
import CustomAssignee from '../CustomAssignee';
import StateSelector from '../StateSelector';

import './../AssigneeTrigger.scss';

type CreateTaskModalProps = {
	closeModal: () => void;
	dueDate?: string;
	loadData: Function;
	onItemsChange?: Function;
	projectId?: string;
	projectObjectDefinitionId: number;
	state: string;
};

export default function CreateTaskModal({
	closeModal,
	dueDate = '',
	loadData,
	onItemsChange,
	projectId,
	projectObjectDefinitionId,
	state,
}: CreateTaskModalProps) {
	const [states, setStates] = useState([]);
	const [projects, setProjects] = useState([
		{
			label: '',
			scopeKey: '',
			value: 0,
		},
	]);
	const [scopeKey, setScopeKey] = useState('');

	const {
		errors,
		handleChange,
		handleSubmit,
		isSubmitting,
		setFieldValue,
		touched,
		values,
	} = useFormik({
		initialValues: {
			assignTo: {},
			dueDate,
			r_cmpProjectToCMPTasks_c_cmpProjectId: Number(projectId) ?? 0,
			state,
			title: '',
		},
		onSubmit: async (values) => {
			const {data, error} = await postTaskByScope({
				body: {
					...values,
					keywords: [
						'L_CMP_TASK_' +
							Math.floor(Math.random() * 100000000).toString(),
					],
				},
				scopeKey,
			});

			if (!error) {
				closeModal();

				if (onItemsChange && data) {
					onItemsChange({
						itemKey: 'embedded.id',
						items: [{embedded: data}],
					});
				}
				else {
					loadData();
				}

				displayCreateSuccessToast(values.title);
			}
			else {
				displayErrorToast(error);
			}
		},
		validate: (values) =>
			validate(
				{
					r_cmpProjectToCMPTasks_c_cmpProjectId: [required],
					title: [required],
				},
				values
			),
	});

	useEffect(() => {
		const makeFetch = async () => {
			const states = (await getAllStates()) as {
				data: {
					items: [];
				};
			};

			setStates(states.data.items);

			const {
				data: {items},
			} = (await getAllProjects(projectObjectDefinitionId)) as {
				data: {
					items: {
						embedded: IProjectObjectEntry;
					}[];
				};
			};

			setProjects(
				items.map(({embedded: {id, scopeKey, title}}) => {
					return {
						label: title,
						scopeKey,
						value: id,
					};
				})
			);

			if (projectId) {
				const scopeKey = items.find(
					({embedded: {id}}) => String(id) === projectId
				)?.embedded.scopeKey;

				if (scopeKey) {
					setScopeKey(scopeKey);
				}
			}
		};

		makeFetch();
	}, [projectId, projectObjectDefinitionId]);

	return (
		<ClayForm

			// @ts-ignore

			noValidate
			onSubmit={handleSubmit}
		>
			<ClayModal.Header>
				{Liferay.Language.get('new-task')}
			</ClayModal.Header>

			<ClayModal.Body>
				<FieldText
					errorMessage={touched.title ? errors.title : undefined}
					id="title"
					label={Liferay.Language.get('title')}
					name="title"
					onChange={handleChange}
					required
					value={values.title}
				/>

				<FieldPicker
					disabled={!!projectId}
					errorMessage={
						touched.r_cmpProjectToCMPTasks_c_cmpProjectId
							? errors.r_cmpProjectToCMPTasks_c_cmpProjectId
							: undefined
					}
					id="r_cmpProjectToCMPTasks_c_cmpProjectId"
					items={projects}
					label={Liferay.Language.get('project')}
					name="r_cmpProjectToCMPTasks_c_cmpProjectId"
					onSelectionChange={(key: string) => {
						setFieldValue(
							'r_cmpProjectToCMPTasks_c_cmpProjectId',
							Number(key)
						);

						const scopeKey = projects.find(
							(project) => String(project.value) === key
						)?.scopeKey;

						if (scopeKey) {
							setScopeKey(scopeKey);
						}
					}}
					required
					selectedKey={String(
						values.r_cmpProjectToCMPTasks_c_cmpProjectId
					)}
				/>

				<FieldWrapper
					fieldId="state"
					label={Liferay.Language.get('state')}
				>
					<StateSelector
						id="state"
						initialSelectedKey={state}
						name="state"
						onChange={async (key: string) => {
							setFieldValue('state', key);
						}}
						states={states ?? []}
					/>
				</FieldWrapper>

				<CustomAssignee
					name="assignTo"
					onChange={(assigneeValue: AssigneeValue | {}) => {
						setFieldValue('assignTo', assigneeValue);
					}}
					triggerClassName="form-control"
					value={values.assignTo}
				/>

				<DatePicker
					id="dueDate"
					label={Liferay.Language.get('due-date')}
					onChange={(value) => {
						setFieldValue('dueDate', value);
					}}
					type="Date"
					value={values.dueDate}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={isSubmitting}
							displayType="primary"
							type="submit"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayForm>
	);
}
