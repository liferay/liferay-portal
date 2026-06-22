/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	API,
	Card,
	SidePanelForm,
	openToast,
	saveAndReload,
} from '@liferay/object-js-components-web';
import {ILearnResourceContext} from 'frontend-js-components-web';
import React, {FormEvent, useState} from 'react';

import {EditObjectRelationshipContent} from './EditObjectRelationshipContent';
import {checkDisableInheritanceBlocked} from './checkDisableInheritanceBlocked';
import {useObjectRelationshipForm} from './useObjectRelationshipForm';

interface EditObjectRelationshipProps {
	baseResourceURL: string;
	hasUpdateObjectDefinitionPermission: boolean;
	learnResources: ILearnResourceContext;
	objectDefinitionExternalReferenceCode: string;
	objectRelationship: ObjectRelationship;
	objectRelationshipDeletionTypes: LabelValueObject[];
	parameterRequired: boolean;
	restContextPath: string;
}

export default function EditObjectRelationship({
	baseResourceURL,
	hasUpdateObjectDefinitionPermission,
	learnResources,
	objectDefinitionExternalReferenceCode,
	objectRelationship: initialValues,
	objectRelationshipDeletionTypes,
	parameterRequired,
	restContextPath,
}: EditObjectRelationshipProps) {
	const [isCheckingInheritance, setIsCheckingInheritance] = useState(false);
	const [submitError, setSubmitError] = useState<SubmitError>(null);

	const {errors, handleChange, handleValidate, setValues, values} =
		useObjectRelationshipForm({
			initialValues,
			onSubmit: () => {},
			parameterRequired,
		});

	const onSubmit = async (
		objectRelationship: Partial<ObjectRelationship> = values
	) => {
		try {
			await API.putObjectRelationship(objectRelationship);
			saveAndReload();

			openToast({
				message: Liferay.Language.get(
					'the-object-relationship-was-updated-successfully'
				),
			});
		}
		catch (error: unknown) {
			const {message} = error as Error;

			setSubmitError(message);
		}
	};

	const handleSubmit = (event: FormEvent) => {
		event.preventDefault();

		const validationErrors = handleValidate();

		if (!Object.keys(validationErrors).length) {
			onSubmit(values);
		}
	};

	const readOnly =
		!hasUpdateObjectDefinitionPermission ||
		values.reverse ||
		initialValues.system;

	const handleInheritanceCheckboxChange = async ({
		target,
	}: React.ChangeEvent<HTMLInputElement>) => {
		if (target.checked) {
			setValues({
				...values,
				edge: true,
			});

			return;
		}

		setIsCheckingInheritance(true);

		let isBlocked = false;

		try {
			isBlocked = await checkDisableInheritanceBlocked(values);
		}
		catch (error) {
			openToast({
				message: Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});

			return;
		}
		finally {
			setIsCheckingInheritance(false);
		}

		const parentWindow = Liferay.Util.getOpener();

		if (isBlocked) {
			parentWindow.Liferay.fire('openModalDisableInheritance', {
				isBlocked: true,
			});

			return;
		}

		parentWindow.Liferay.fire('openModalDisableInheritance', {
			handleDisable: async () => {
				setValues({
					...values,
					edge: false,
				});
			},
		});
	};

	return (
		<SidePanelForm
			customLabel={{
				displayType: values.reverse ? 'info' : 'success',
				message: values.reverse
					? Liferay.Language.get('child')
					: Liferay.Language.get('parent'),
			}}
			onSubmit={handleSubmit}
			readOnly={readOnly}
			title={Liferay.Language.get('relationship')}
		>
			<EditObjectRelationshipContent
				baseResourceURL={baseResourceURL}
				containerWrapper={Card}
				errors={errors}
				handleChange={handleChange}
				inheritanceCheckboxDisabled={isCheckingInheritance}
				learnResources={learnResources}
				objectDefinitionExternalReferenceCode={
					objectDefinitionExternalReferenceCode
				}
				objectRelationshipDeletionTypes={
					objectRelationshipDeletionTypes
				}
				onChangeInheritanceCheckbox={handleInheritanceCheckboxChange}
				onSubmit={onSubmit}
				parameterRequired={parameterRequired}
				readOnly={readOnly}
				restContextPath={restContextPath}
				setValues={setValues}
				submitError={submitError}
				values={values}
			/>
		</SidePanelForm>
	);
}
