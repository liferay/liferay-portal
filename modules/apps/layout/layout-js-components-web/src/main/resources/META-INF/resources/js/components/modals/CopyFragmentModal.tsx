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
	fragmentSets?: string | null;
	name?: string | null;
};

export default function CopyFragmentModal({
	addFragmentCollectionURL,
	contributedEntryKeys,
	copyFragmentEntriesURL,
	fragmentCollections = [],
	fragmentEntryIds,
	portletNamespace,
}: {
	addFragmentCollectionURL: string;
	contributedEntryKeys: string[];
	copyFragmentEntriesURL: string;
	fragmentCollections: FragmentSet[];
	fragmentEntryIds: string[];
	portletNamespace: string;
}) {
	const noFragmentCollections = !fragmentCollections.length;

	const [visible, setVisible] = useState(true);

	const {observer, onClose} = useModal({
		onClose: () => setVisible(false),
	});

	const [errors, setErrors] = useState<Errors>({});
	const [showFragmentSetForm, setShowFragmentSetForm] = useState(
		noFragmentCollections
	);

	const formId = `${portletNamespace}form`;

	const copyFragments = (fragmentCollectionId: number) => {
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
				onClose();

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

	return (
		visible && (
			<ClayModal observer={observer}>
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{showFragmentSetForm
						? Liferay.Language.get('add-fragment-set')
						: Liferay.Language.get('select-fragment-set')}
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
							copyFragments={copyFragments}
							errors={errors}
							formId={formId}
							fragmentCollections={fragmentCollections}
							portletNamespace={portletNamespace}
							setErrors={setErrors}
							showNoFragmentCollectionMessage={
								noFragmentCollections
							}
						/>
					) : (
						<FragmentSetSelector
							copyFragments={copyFragments}
							errors={errors}
							formId={formId}
							fragmentCollections={fragmentCollections}
							portletNamespace={portletNamespace}
							setErrors={setErrors}
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
								onClick={onClose}
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
		)
	);
}

function FragmentSetSelector({
	copyFragments,
	errors,
	formId,
	fragmentCollections,
	portletNamespace,
	setErrors,
}: {
	copyFragments: (fragmentCollectionId: number) => void;
	errors: Errors;
	formId: string;
	fragmentCollections: FragmentSet[];
	portletNamespace: string;
	setErrors: (errors: Errors) => void;
}) {
	const [selectedFragmentCollection, setSelectedFragmentCollection] =
		useState('');

	const items = useMemo(
		() => [
			{label: `-- ${Liferay.Language.get('not-selected')} --`, value: ''},
			...fragmentCollections.map((fragmentSet: FragmentSet) => ({
				label: fragmentSet.name,
				value: fragmentSet.fragmentCollectionId,
			})),
		],
		[fragmentCollections]
	);

	const handleSubmit = (event: FormEvent) => {
		event.preventDefault();

		if (!selectedFragmentCollection) {
			setErrors({
				fragmentSets: sub(
					Liferay.Language.get('x-field-is-required'),
					Liferay.Language.get('fragment-set')
				),
			});

			return;
		}

		copyFragments(Number(selectedFragmentCollection));
	};

	return (
		<ClayForm id={formId} onSubmit={handleSubmit}>
			<p className="text-secondary">
				{Liferay.Language.get(
					'select-an-existing-set-or-create-a-new-one-to-save-your-fragment'
				)}
			</p>

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
	copyFragments,
	errors,
	formId,
	fragmentCollections,
	portletNamespace,
	setErrors,
	showNoFragmentCollectionMessage,
}: {
	addFragmentCollectionURL: string;
	copyFragments: (fragmentCollectionId: number) => void;
	errors: Errors;
	formId: string;
	fragmentCollections: FragmentSet[];
	portletNamespace: string;
	setErrors: (errors: Errors) => void;
	showNoFragmentCollectionMessage: boolean;
}) {
	const [name, setName] = useState(() =>
		getDefaultFragmentSetName(fragmentCollections)
	);
	const [description, setDescription] = useState('');

	const handleSubmit = (event: FormEvent) => {
		event.preventDefault();

		if (!name) {
			setErrors({
				name: sub(
					Liferay.Language.get('x-field-is-required'),
					Liferay.Language.get('name')
				),
			});

			return;
		}

		const formData = new FormData();

		formData.append(`${portletNamespace}name`, name);

		formData.append(`${portletNamespace}description`, description);

		fetch(addFragmentCollectionURL, {body: formData, method: 'POST'})
			.then((response) => response.json())
			.then((response) => {
				if (response.error) {
					setErrors({error: response.error});
				}
				else if (response.fragmentCollectionId) {
					copyFragments(response.fragmentCollectionId);
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
			{showNoFragmentCollectionMessage ? (
				<p className="text-secondary">
					{Liferay.Language.get(
						'a-fragment-set-must-first-be-created-before-you-can-copy-it'
					)}
				</p>
			) : null}

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
