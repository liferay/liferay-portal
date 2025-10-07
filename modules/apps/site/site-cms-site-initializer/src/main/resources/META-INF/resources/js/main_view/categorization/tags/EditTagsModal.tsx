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
import CategorizationSpaces from '../components/CategorizationSpaces';

export default function EditTagsModalContent({
	assetLibraries,
	closeModal,
	editTagURL,
	loadData,
	tagId,
	tagName,
}: {
	assetLibraries: any;
	closeModal: () => void;
	editTagURL: string;
	loadData: () => {};
	tagId: number;
	tagName: string;
}) {
	const [nameInputError, setNameInputError] = useState<string>('');
	const [selectedSpaces, setSelectedSpaces] = useState<number[]>(
		assetLibraries.map((item: {id: number}) => item.id)
	);
	const [spaceChange, setSpaceChange] = useState(false);
	const [spaceInputError, setSpaceInputError] = useState('');

	const assetLibraryIds = selectedSpaces.map((number) => ({
		id: number,
	}));

	const updateTag = (values: any) => {
		const body = {
			assetLibraries: assetLibraryIds,
			name: values.tagName,
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
				tagId,
				tagName,
			},
			onSubmit: (values) => {
				if (spaceChange) {
					openConfirmModal({
						message: Liferay.Language.get(
							'removing-a-space-will-make-the-tag-unavailable'
						),
						onConfirm: (isConfirm: boolean) => {
							if (isConfirm) {
								updateTag(values);
							}
						},
						status: 'info',
						title: Liferay.Language.get('confirm-space-change'),
					});
				}
				else {
					updateTag(values);
				}
			},
			validate: (values) => {
				const errors = validate(
					{
						assetLibraries: [required],
						tagName: [required],
					},
					values
				);
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
			<ClayModal.Header>
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

				<CategorizationSpaces
					assetLibraries={assetLibraries}
					checkboxText="tag"
					selectedSpaces={selectedSpaces}
					setSelectedSpaces={setSelectedSpaces}
					setSpaceChange={setSpaceChange}
					setSpaceInputError={setSpaceInputError}
					spaceInputError={spaceInputError}
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

						<ClayButton displayType="primary" type="submit">
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</form>
	);
}
