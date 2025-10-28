/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal, {ClayModalProvider, useModal} from '@clayui/modal';
import {API, Input} from '@liferay/object-js-components-web';
import React, {useState} from 'react';

import {defaultLanguageId} from '../../utils/constants';
import {toCamelCase} from '../../utils/string';
import {ObjectRelationshipFormBase} from './ObjectRelationshipFormBase';
import {SelectObjectRelationship} from './SelectObjectRelationship';
import {useObjectRelationshipForm} from './useObjectRelationshipForm';

import './ModalAddObjectRelationship.scss';

import {ILearnResourceContext} from 'frontend-js-components-web';

interface ModalAddObjectRelationshipProps {
	baseResourceURL: string;
	handleOnClose: () => void;
	hasDefinedObjectDefinitionTarget?: boolean;
	learnResources: ILearnResourceContext;
	objectDefinitionExternalReferenceCode1: string;
	objectDefinitionExternalReferenceCode2?: string;
	objectRelationshipParameterRequired: boolean;
	onAfterAddObjectRelationship?: (
		objectRelationship: ObjectRelationship
	) => void;
	reload?: boolean;
}

export function ModalAddObjectRelationship({
	baseResourceURL,
	handleOnClose,
	hasDefinedObjectDefinitionTarget,
	learnResources,
	objectDefinitionExternalReferenceCode1,
	objectDefinitionExternalReferenceCode2,
	objectRelationshipParameterRequired,
	onAfterAddObjectRelationship,
	reload = true,
}: ModalAddObjectRelationshipProps) {
	const formId = 'modalAddObjectRelationshipForm';

	const {observer, onClose} = useModal({
		onClose: () => {
			handleOnClose();
		},
	});

	const [submitError, setSubmitError] = useState<SubmitError>(null);

	const initialValues: Partial<ObjectRelationship> = {
		objectDefinitionExternalReferenceCode1,
		objectDefinitionExternalReferenceCode2,
	};

	const onSubmit = async ({
		label = {[defaultLanguageId]: ''},
		name,
		objectDefinitionExternalReferenceCode1,
		...others
	}: Partial<ObjectRelationship>) => {
		try {
			const objectRelationship = await API.save<ObjectRelationship>({
				item: {
					objectDefinitionExternalReferenceCode1,
					...others,
					label,
					name: name ?? toCamelCase(label[defaultLanguageId]!, true),
				},
				method: 'POST',
				returnValue: true,
				url: `/o/object-admin/v1.0/object-definitions/by-external-reference-code/${objectDefinitionExternalReferenceCode1}/object-relationships`,
			});

			onClose();

			if (reload) {
				setTimeout(() => window.location.reload(), 1500);
			}

			if (onAfterAddObjectRelationship && objectRelationship) {
				setTimeout(
					() => onAfterAddObjectRelationship(objectRelationship),
					200
				);
			}
		}
		catch (error: unknown) {
			const {message} = error as Error;

			setSubmitError(message);

			const modalBodyElement = document.querySelector(
				'.lfr-objects__modal-add-object-relationship-body'
			);

			if (modalBodyElement) {
				modalBodyElement.scrollTop = modalBodyElement.scrollHeight;
			}
		}
	};

	const {errors, handleChange, handleSubmit, setValues, values} =
		useObjectRelationshipForm({
			initialValues,
			onSubmit,
			parameterRequired: objectRelationshipParameterRequired,
		});

	const handleInheritanceCheckboxChange = ({
		target,
	}: React.ChangeEvent<HTMLInputElement>) => {
		setValues({
			...values,
			edge: target.checked,
		});
	};

	return (
		<ClayModalProvider>
			<ClayModal center observer={observer}>
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{Liferay.Language.get('new-relationship')}
				</ClayModal.Header>

				<ClayModal.Body
					className="lfr-objects__modal-add-object-relationship-body"
					scrollable
				>
					<ClayForm id={formId} onSubmit={handleSubmit}>
						<Input
							error={errors.label}
							id={formId + 'LabelField'}
							label={Liferay.Language.get('label')}
							onChange={({target: {value}}) =>
								setValues({label: {[defaultLanguageId]: value}})
							}
							required
							value={values.label?.[defaultLanguageId]}
						/>

						<ObjectRelationshipFormBase
							baseResourceURL={baseResourceURL}
							className="lfr-objects__modal-add-object-relationship-form-base"
							errors={errors}
							handleChange={handleChange}
							hasDefinedObjectDefinitionTarget={
								hasDefinedObjectDefinitionTarget
							}
							learnResources={learnResources}
							objectDefinitionExternalReferenceCode1={
								objectDefinitionExternalReferenceCode1
							}
							objectDefinitionExternalReferenceCode2={
								objectDefinitionExternalReferenceCode2
							}
							onChangeInheritanceCheckbox={
								handleInheritanceCheckboxChange
							}
							setValues={setValues}
							submitError={submitError}
							values={{
								...values,
								name:
									values.name ??
									toCamelCase(
										values.label?.[defaultLanguageId] ?? '',
										true
									),
							}}
						>
							{objectRelationshipParameterRequired &&
							values.type === 'oneToMany' ? (
								<SelectObjectRelationship
									error={errors.parameterObjectFieldName}
									objectDefinitionExternalReferenceCode1={
										values.objectDefinitionExternalReferenceCode2 as string
									}
									onChange={(parameterObjectFieldName) => {
										setValues({parameterObjectFieldName});
									}}
									value={values.parameterObjectFieldName}
								/>
							) : undefined}
						</ObjectRelationshipFormBase>
					</ClayForm>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={() => onClose()}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								displayType="primary"
								form={formId}
								type="submit"
							>
								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayModal>
		</ClayModalProvider>
	);
}
