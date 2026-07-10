/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {fetch, navigate, sub} from 'frontend-js-web';
import React, {FormEvent, useMemo, useState} from 'react';

import FormField from './FormField';

type FragmentSet = {fragmentCollectionId: number; name: string};

type Errors = {
	error?: string;
	fragmentName?: string | null;
	fragmentSets?: string | null;
	name?: string | null;
};

export default function FragmentSetModal({
	addFragmentCollectionURL,
	allowCustomName = false,
	contributedEntryKeys = [],
	copyFragmentEntriesURL,
	fragmentCollections = [],
	fragmentEntryIds = [],
	onSubmitFragmentCollection,
	portletNamespace,
}: {
	addFragmentCollectionURL?: string;
	allowCustomName?: boolean;
	contributedEntryKeys?: string[];
	copyFragmentEntriesURL?: string;
	fragmentCollections: FragmentSet[];
	fragmentEntryIds?: string[];
	onSubmitFragmentCollection?: (
		fragmentCollectionId: number,
		fragmentName?: string
	) => Promise<void> | void;
	portletNamespace: string;
}) {
	const {observer, onOpenChange, open} = useModal({defaultOpen: true});

	const [errors, setErrors] = useState<Errors>({});
	const [fragmentName, setFragmentName] = useState('');
	const [showFragmentSetForm, setShowFragmentSetForm] = useState(
		!fragmentCollections.length
	);

	const formId = `${portletNamespace}form`;

	const submitFragmentCollection = (fragmentCollectionId: number) => {
		if (onSubmitFragmentCollection) {
			onOpenChange(false);

			onSubmitFragmentCollection(fragmentCollectionId, fragmentName);

			return;
		}

		if (!copyFragmentEntriesURL) {
			openToast({
				message: Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});

			return;
		}

		const formData = new FormData();

		if (fragmentEntryIds) {
			formData.append(
				`${portletNamespace}fragmentEntryIds`,
				fragmentEntryIds.join(',')
			);
		}

		if (contributedEntryKeys) {
			formData.append(
				`${portletNamespace}contributedEntryKeys`,
				contributedEntryKeys.join(',')
			);
		}

		formData.append(
			`${portletNamespace}fragmentCollectionId`,
			fragmentCollectionId.toString()
		);

		fetch(copyFragmentEntriesURL, {
			body: formData,
			method: 'POST',
		})
			.then((response) => {
				onOpenChange(false);

				if (response.redirected) {
					navigate(response.url);
				}

				openToast({
					message: Liferay.Language.get(
						'the-fragment-was-copied-successfully'
					),
					type: 'success',
				});
			})
			.catch(() => {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				});
			});
	};

	let title = Liferay.Language.get('select-fragment-set');

	if (allowCustomName) {
		title = Liferay.Language.get('add-fragment');
	}
	else if (showFragmentSetForm) {
		title = Liferay.Language.get('add-fragment-set');
	}

	if (!open) {
		return null;
	}

	return (
		<ClayModal className="modal-dialog-centered" observer={observer}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ClayModal.Body>
				{errors.error && (
					<ClayAlert
						displayType="danger"
						title={Liferay.Language.get('error')}
					>
						{errors.error}
					</ClayAlert>
				)}

				{showFragmentSetForm ? (
					<FragmentSetForm
						addFragmentCollectionURL={addFragmentCollectionURL}
						allowCustomName={allowCustomName}
						errors={errors}
						formId={formId}
						fragmentCollections={fragmentCollections}
						fragmentName={fragmentName}
						portletNamespace={portletNamespace}
						setErrors={setErrors}
						setFragmentName={setFragmentName}
						submitFragmentCollection={submitFragmentCollection}
					/>
				) : (
					<FragmentSetSelector
						allowCustomName={allowCustomName}
						errors={errors}
						formId={formId}
						fragmentCollections={fragmentCollections}
						fragmentName={fragmentName}
						portletNamespace={portletNamespace}
						setErrors={setErrors}
						setFragmentName={setFragmentName}
						submitFragmentCollection={submitFragmentCollection}
					/>
				)}
			</ClayModal.Body>

			<ClayModal.Footer
				first={
					!showFragmentSetForm ? (
						<ClayButton
							displayType="secondary"
							onClick={() => setShowFragmentSetForm(true)}
						>
							{Liferay.Language.get('save-in-new-set')}
						</ClayButton>
					) : (
						<></>
					)
				}
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={() => onOpenChange(false)}
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
	);
}

function FragmentNameField({
	errors,
	fragmentName,
	portletNamespace,
	setErrors,
	setFragmentName,
}: {
	errors: Errors;
	fragmentName: string;
	portletNamespace: string;
	setErrors: (errors: Errors) => void;
	setFragmentName: (fragmentName: string) => void;
}) {
	return (
		<FormField
			error={errors.fragmentName}
			id={`${portletNamespace}fragmentName`}
			name={Liferay.Language.get('name')}
			required
		>
			<ClayInput
				id={`${portletNamespace}fragmentName`}
				onChange={(event) => {
					setErrors({...errors, fragmentName: null});
					setFragmentName(event.target.value);
				}}
				required
				type="text"
				value={fragmentName}
			/>
		</FormField>
	);
}

function FragmentSetSelector({
	allowCustomName,
	errors,
	formId,
	fragmentCollections,
	fragmentName,
	portletNamespace,
	setErrors,
	setFragmentName,
	submitFragmentCollection,
}: {
	allowCustomName: boolean;
	errors: Errors;
	formId: string;
	fragmentCollections: FragmentSet[];
	fragmentName: string;
	portletNamespace: string;
	setErrors: (errors: Errors) => void;
	setFragmentName: (fragmentName: string) => void;
	submitFragmentCollection: (fragmentCollectionId: number) => void;
}) {
	const [selectedFragmentCollection, setSelectedFragmentCollection] =
		useState('');

	const items = useMemo(
		() => [
			{
				label: `-- ${Liferay.Language.get('not-selected')} --`,
				value: '',
			},
			...fragmentCollections.map((fragmentSet: FragmentSet) => ({
				label: fragmentSet.name,
				value: fragmentSet.fragmentCollectionId,
			})),
		],
		[fragmentCollections]
	);

	const handleSubmit = (event: FormEvent) => {
		event.preventDefault();

		const nextErrors: Errors = {};

		if (allowCustomName && !fragmentName) {
			nextErrors.fragmentName = sub(
				Liferay.Language.get('x-field-is-required'),
				Liferay.Language.get('name')
			);
		}

		if (!selectedFragmentCollection) {
			nextErrors.fragmentSets = sub(
				Liferay.Language.get('x-field-is-required'),
				Liferay.Language.get('fragment-set')
			);
		}

		if (Object.keys(nextErrors).length) {
			setErrors(nextErrors);

			return;
		}

		submitFragmentCollection(Number(selectedFragmentCollection));
	};

	return (
		<ClayForm
			id={formId}

			// @ts-ignore

			noValidate
			onSubmit={handleSubmit}
		>
			<p className="text-secondary">
				{Liferay.Language.get(
					'select-an-existing-set-or-create-a-new-one-to-save-your-fragment'
				)}
			</p>

			{allowCustomName && (
				<FragmentNameField
					errors={errors}
					fragmentName={fragmentName}
					portletNamespace={portletNamespace}
					setErrors={setErrors}
					setFragmentName={setFragmentName}
				/>
			)}

			<FormField
				error={errors.fragmentSets}
				id={`${portletNamespace}fragment-sets`}
				name={Liferay.Language.get('fragment-sets')}
				required
			>
				<ClaySelectWithOption
					id={`${portletNamespace}fragment-sets`}
					onChange={(event) => {
						setErrors({...errors, fragmentSets: null});
						setSelectedFragmentCollection(event.target.value);
					}}
					options={items}
					value={selectedFragmentCollection}
				/>
			</FormField>
		</ClayForm>
	);
}

function FragmentSetForm({
	addFragmentCollectionURL,
	allowCustomName,
	errors,
	formId,
	fragmentCollections,
	fragmentName,
	portletNamespace,
	setErrors,
	setFragmentName,
	submitFragmentCollection,
}: {
	addFragmentCollectionURL?: string;
	allowCustomName: boolean;
	errors: Errors;
	formId: string;
	fragmentCollections: FragmentSet[];
	fragmentName: string;
	portletNamespace: string;
	setErrors: (errors: Errors) => void;
	setFragmentName: (fragmentName: string) => void;
	submitFragmentCollection: (fragmentCollectionId: number) => void;
}) {
	const [name, setName] = useState(() =>
		getDefaultFragmentSetName(fragmentCollections)
	);
	const [description, setDescription] = useState('');

	const handleSubmit = (event: FormEvent) => {
		event.preventDefault();

		const nextErrors: Errors = {};

		if (allowCustomName && !fragmentName) {
			nextErrors.fragmentName = sub(
				Liferay.Language.get('x-field-is-required'),
				Liferay.Language.get('name')
			);
		}

		if (!name) {
			nextErrors.name = sub(
				Liferay.Language.get('x-field-is-required'),
				Liferay.Language.get('name')
			);
		}

		if (Object.keys(nextErrors).length) {
			setErrors(nextErrors);

			return;
		}

		const formData = new FormData();

		if (!addFragmentCollectionURL) {
			setErrors({
				error: Liferay.Language.get('an-unexpected-error-occurred'),
			});

			return;
		}

		formData.append(`${portletNamespace}name`, name);

		formData.append(`${portletNamespace}description`, description);

		fetch(addFragmentCollectionURL, {body: formData, method: 'POST'})
			.then((response) => response.json())
			.then((response) => {
				if (response.error) {
					setErrors({error: response.error});
				}
				else if (response.fragmentCollectionId) {
					submitFragmentCollection(response.fragmentCollectionId);
				}
			});
	};

	return (
		<ClayForm
			id={formId}

			// @ts-ignore

			noValidate
			onSubmit={handleSubmit}
		>
			{!fragmentCollections.length && (
				<p className="text-secondary">
					{Liferay.Language.get(
						'add-a-fragment-set-to-save-your-fragment'
					)}
				</p>
			)}

			{allowCustomName && (
				<FragmentNameField
					errors={errors}
					fragmentName={fragmentName}
					portletNamespace={portletNamespace}
					setErrors={setErrors}
					setFragmentName={setFragmentName}
				/>
			)}

			<FormField
				error={errors.name}
				id={`${portletNamespace}name`}
				name={Liferay.Language.get('name')}
				required
			>
				<ClayInput
					id={`${portletNamespace}name`}
					name={`${portletNamespace}name`}
					onChange={(event) => {
						setErrors({...errors, name: null});
						setName(event.target.value);
					}}
					required
					type="text"
					value={name}
				/>
			</FormField>

			<FormField
				id={`${portletNamespace}description`}
				name={Liferay.Language.get('description')}
			>
				<textarea
					className="form-control"
					id={`${portletNamespace}description`}
					name={`${portletNamespace}description`}
					onChange={(event) => setDescription(event.target.value)}
					value={description}
				/>
			</FormField>
		</ClayForm>
	);
}

function getDefaultFragmentSetName(fragmentCollections: FragmentSet[]) {
	const nameIsUsed = (collections: FragmentSet[], name: string) =>
		collections.some((collection: FragmentSet) => collection.name === name);

	let name = Liferay.Language.get('untitled-set');
	let suffix = 0;

	while (nameIsUsed(fragmentCollections, name)) {
		suffix++;

		name = `${Liferay.Language.get('untitled-set')} ${suffix}`;
	}

	return name;
}
