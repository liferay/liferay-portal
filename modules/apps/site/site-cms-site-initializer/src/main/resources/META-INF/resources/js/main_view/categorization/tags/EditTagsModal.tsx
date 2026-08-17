/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {useFormik} from 'formik';
import {openConfirmModal, openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {FieldText} from '../../../common/components/forms';
import {required, validate} from '../../../common/components/forms/validations';
import ApiHelper from '../../../common/services/ApiHelper';
import {
	displayErrorToast,
	displayNameInUseErrorToast,
} from '../../../common/utils/toastUtil';
import CategorizationProjects from '../components/CategorizationProjects';
import CategorizationSpaces from '../components/CategorizationSpaces';

const CONFIRMATION_MESSAGES = {
	BOTH: {
		message: Liferay.Language.get(
			'removing-both-will-make-the-tag-unavailable'
		),
		title: Liferay.Language.get('confirm-changes'),
	},
	PROJECTS: {
		message: Liferay.Language.get(
			'removing-a-project-will-make-the-tag-unavailable'
		),
		title: Liferay.Language.get('confirm-project-change'),
	},
	SPACES: {
		message: Liferay.Language.get(
			'removing-a-space-will-make-the-tag-unavailable'
		),
		title: Liferay.Language.get('confirm-space-change'),
	},
};

export default function EditTagsModalContent({
	assetLibraries,
	closeModal,
	editTagURL,
	loadData,
	projects,
	tagId,
	tagName,
}: {
	assetLibraries: AssetLibraryType[];
	closeModal: () => void;
	editTagURL: string;
	loadData: () => {};
	projects?: AssetLibraryType[];
	tagId: number;
	tagName: string;
}) {
	const [nameInputError, setNameInputError] = useState<string>('');
	const [projectChange, setProjectChange] = useState(false);
	const [projectInputError, setProjectInputError] = useState('');
	const [selectedProjects, setSelectedProjects] = useState<string[]>(
		projects?.map((project) => project.scopeKey) ?? []
	);
	const [selectedSpaces, setSelectedSpaces] = useState<string[]>(
		assetLibraries.map((item: {scopeKey: string}) => item.scopeKey)
	);
	const [spaceChange, setSpaceChange] = useState(false);
	const [spaceInputError, setSpaceInputError] = useState('');

	const updateTag = (values: any) => {
		const body = {
			assetLibraries: selectedSpaces.map((scopeKey) => ({scopeKey})),
			name: values.tagName,
			projects: selectedProjects.map((scopeKey) => ({scopeKey})),
		};

		ApiHelper.put(editTagURL, body).then(({error, status}) => {
			if (error) {
				if (status === 'CONFLICT') {
					setNameInputError(
						Liferay.Language.get(
							'please-enter-a-unique-name.-this-one-is-already-in-use'
						)
					);

					displayNameInUseErrorToast();
				}
				else {
					displayErrorToast();

					closeModal();
				}

				throw new Error(
					`PUT request failed to update tag '${tagName}' using the following data: ${JSON.stringify(body)}`
				);
			}
			else {
				openToast({
					message: sub(
						Liferay.Language.get('x-was-updated-successfully'),
						`<strong>${Liferay.Util.escapeHTML(tagName)}</strong>`
					),
					type: 'success',
				});

				loadData?.();

				closeModal();
			}
		});
	};

	const {errors, handleBlur, handleChange, handleSubmit, touched, values} =
		useFormik({
			initialValues: {
				assetLibraries,
				projects: projects ?? [],
				tagId,
				tagName,
			},
			onSubmit: (values) => {
				if (!projectChange && !spaceChange) {
					updateTag(values);

					return;
				}

				const confirmationMessages = (() => {
					if (projectChange && spaceChange) {
						return CONFIRMATION_MESSAGES.BOTH;
					}
					else if (projectChange) {
						return CONFIRMATION_MESSAGES.PROJECTS;
					}

					return CONFIRMATION_MESSAGES.SPACES;
				})();

				openConfirmModal({
					message: confirmationMessages.message,
					onConfirm: (isConfirm: boolean) => {
						if (isConfirm) {
							updateTag(values);
						}
					},
					status: 'info',
					title: confirmationMessages.title,
				});
			},
			validate: (values) => {
				const errors = validate(
					{
						assetLibraries: [required],
						projects: [required],
						tagName: [required],
					},
					values
				);

				if (projectInputError) {
					errors.projects = projectInputError;
				}

				if (spaceInputError) {
					errors.assetLibraries = spaceInputError;
				}

				return errors;
			},
		});

	const errorMessage = sub(
		Liferay.Language.get('the-x-field-is-required'),
		Liferay.Language.get('name')
	);

	const handleNameInputErrorMessage = () => {
		if (nameInputError) {
			return nameInputError;
		}

		if (values.tagName.length !== 0 || !touched.tagName) {
			return errors.tagName;
		}

		return errorMessage;
	};

	return (
		<form onSubmit={handleSubmit}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{sub(Liferay.Language.get('edit-x'), '"' + tagName + '"')}
			</ClayModal.Header>

			<ClayModal.Body>
				<FieldText
					errorMessage={handleNameInputErrorMessage()}
					label={Liferay.Language.get('name')}
					name="tagName"
					onBlur={handleBlur}
					onChange={(event) => {
						setNameInputError('');
						handleChange(event);
					}}
					required
					value={values.tagName}
				/>

				<div className="c-gap-4 d-flex flex-column">
					<CategorizationSpaces
						assetLibraries={assetLibraries}
						checkboxText="tag"
						setSelectedSpaces={setSelectedSpaces}
						setSpaceChange={setSpaceChange}
						setSpaceInputError={setSpaceInputError}
					/>

					{Liferay.FeatureFlags['LPD-58677'] && (
						<CategorizationProjects
							checkboxText="tag"
							projects={projects}
							setProjectChange={setProjectChange}
							setProjectInputError={setProjectInputError}
							setSelectedProjects={setSelectedProjects}
						/>
					)}
				</div>
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

						<ClayButton displayType="primary" type="submit">
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</form>
	);
}
