/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {escapeHTML, fetch, sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';
import {flushSync} from 'react-dom';

import FormField from './FormField';

type ConvertProps = {
	addPageTemplateSetURL?: never;
	allowCustomName?: never;
	createTemplateURL: string;
	getCollectionsURL: string;
	hasMultipleSegmentsExperienceIds?: boolean;
	layoutId?: string;
	namespace?: string;
	onSubmitPageTemplateSet?: never;
	pageTemplateSets?: never;
	segmentsExperienceId?: string;
};

type DesignLibraryProps = {
	addPageTemplateSetURL: string;
	allowCustomName?: boolean;
	createTemplateURL?: never;
	getCollectionsURL?: never;
	hasMultipleSegmentsExperienceIds?: never;
	layoutId?: never;
	namespace?: string;
	onSubmitPageTemplateSet: (
		pageTemplateSetId: number,
		pageTemplateName?: string
	) => void;
	pageTemplateSets: PageTemplateSet[];
	segmentsExperienceId?: never;
};

type Props = (ConvertProps | DesignLibraryProps) & {onClose: () => {}};

export type PageTemplateSet = {
	id: number;
	name: string;
};

type Errors = {
	pageTemplateName?: string;
	templateSetId?: string;
	templateSetName?: string;
};

export default function PageTemplateModal({onClose, ...otherProps}: Props) {
	const {observer} = useModal({
		onClose,
	});

	return (
		<ClayModal containerProps={{className: 'cadmin'}} observer={observer}>
			<PageTemplateModalContent {...otherProps} closeModal={onClose} />
		</ClayModal>
	);
}

export function PageTemplateModalContent({
	addPageTemplateSetURL,
	allowCustomName = false,
	closeModal,
	createTemplateURL,
	getCollectionsURL,
	hasMultipleSegmentsExperienceIds,
	layoutId,
	namespace,
	onSubmitPageTemplateSet,
	pageTemplateSets = [],
	segmentsExperienceId,
}: (ConvertProps | DesignLibraryProps) & {closeModal: () => void}) {
	const [availableSets, setAvailableSets] =
		useState<PageTemplateSet[]>(pageTemplateSets);
	const [formErrors, setFormErrors] = useState<Errors>({});
	const [loading, setLoading] = useState(false);
	const [openAddTemplateSetModal, setOpenAddTemplateSetModal] = useState(
		!getCollectionsURL && !pageTemplateSets.length
	);
	const [pageTemplateName, setPageTemplateName] = useState('');
	const [templateSetDescription, setTemplateSetDescription] = useState('');
	const [templateSetId, setTemplateSetId] = useState('');
	const [templateSetName, setTemplateSetName] = useState(() =>
		getUniqueName(pageTemplateSets, Liferay.Language.get('untitled-set'))
	);

	const nameInputRef = useRef<HTMLInputElement>(null);

	const nameLabel = allowCustomName
		? Liferay.Language.get('page-template-set-name')
		: Liferay.Language.get('name');

	const templateSetSelectOptions = useMemo(
		() => [
			{label: `-- ${Liferay.Language.get('not-selected')} --`, value: ''},
			...availableSets.map((set: {id: any; name: any}) => ({
				label: set.name,
				value: set.id,
			})),
		],
		[availableSets]
	);

	useEffect(() => {
		if (nameInputRef.current) {
			nameInputRef.current.focus();
		}
	}, []);

	useEffect(() => {
		if (!getCollectionsURL) {
			return;
		}

		const getCollections = async () => {
			try {
				const response = await fetch(getCollectionsURL);

				const sets = await response.json();

				if (Array.isArray(sets)) {
					setAvailableSets(sets);
					setOpenAddTemplateSetModal(!sets.length);
					setTemplateSetName(
						getUniqueName(
							sets,
							Liferay.Language.get('untitled-set')
						)
					);
				}
			}
			catch (error) {
				console.error(error);
			}
		};

		getCollections();
	}, [getCollectionsURL]);

	const validateForm = useCallback(() => {
		const errors: Errors = {};

		if (allowCustomName && !pageTemplateName) {
			errors.pageTemplateName = sub(
				Liferay.Language.get('x-field-is-required'),
				Liferay.Language.get('page-template-name')
			);
		}

		if (openAddTemplateSetModal) {
			if (!templateSetName) {
				errors.templateSetName = sub(
					Liferay.Language.get('x-field-is-required'),
					nameLabel
				);
			}
		}
		else {
			if (!templateSetId) {
				errors.templateSetId = sub(
					Liferay.Language.get('x-field-is-required'),
					Liferay.Language.get('page-template-set')
				);
			}
		}

		return errors;
	}, [
		allowCustomName,
		nameLabel,
		openAddTemplateSetModal,
		pageTemplateName,
		templateSetId,
		templateSetName,
	]);

	// We are using flush here because this way we can clear errors inmediately
	// in handleSubmit. Otherwise, React will batch setStates and will do only
	// one update.

	const resetErrors = useCallback(
		() =>
			flushSync(() => {
				setFormErrors({});
			}),
		[]
	);

	const getFormData = useCallback(
		(body: Record<string, string>): FormData => {
			const formData = new FormData();

			Object.entries(body).forEach(([key, value]) => {
				if (!value && process.env.NODE_ENV === 'development') {
					console.warn(
						`${key} does not have any value, sending it this way could cause some wrong behavior`
					);
				}

				formData.append(`${namespace}${key}`, value);
			});

			return formData;
		},
		[namespace]
	);

	const submitPageTemplateSet = useCallback(async () => {
		if (!onSubmitPageTemplateSet) {
			return;
		}

		if (!openAddTemplateSetModal) {
			closeModal();

			onSubmitPageTemplateSet(Number(templateSetId), pageTemplateName);

			return;
		}

		try {
			const response = await fetch(addPageTemplateSetURL, {
				body: getFormData({
					description: templateSetDescription,
					name: templateSetName,
				}),
				method: 'POST',
			});

			const {
				error,
				layoutPageTemplateCollectionId,
			}: {
				error?: string;
				layoutPageTemplateCollectionId?: number;
			} = await response.json();

			if (!layoutPageTemplateCollectionId) {
				throw new Error(error);
			}

			closeModal();

			onSubmitPageTemplateSet(
				layoutPageTemplateCollectionId,
				pageTemplateName
			);
		}
		catch (error) {
			setLoading(false);

			openToast({
				message:
					escapeHTML((error as Error).message) ||
					Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});
		}
	}, [
		addPageTemplateSetURL,
		closeModal,
		getFormData,
		onSubmitPageTemplateSet,
		openAddTemplateSetModal,
		pageTemplateName,
		templateSetDescription,
		templateSetId,
		templateSetName,
	]);

	const handleSubmit = useCallback(
		async (event: any) => {
			event.preventDefault();

			const errors = validateForm();

			resetErrors();

			if (Object.keys(errors).length) {
				setFormErrors(errors);

				return;
			}

			setLoading(true);

			if (onSubmitPageTemplateSet) {
				submitPageTemplateSet();

				return;
			}

			try {
				const response = await fetch(createTemplateURL, {
					body: getFormData({
						layoutPageTemplateCollectionDescription:
							templateSetDescription,
						layoutPageTemplateCollectionId: templateSetId,
						layoutPageTemplateCollectionName: templateSetName,
						plid: layoutId ?? '',
						segmentsExperienceId: segmentsExperienceId ?? '',
					}),
					method: 'POST',
				});

				const json = await response.json();

				openToast({
					message: sub(
						Liferay.Language.get(
							'the-page-template-was-created-successfully.-you-can-view-it-here-x'
						),
						`<a href="${escapeHTML(json.url)}">${Liferay.Language.get(
							'see-in-page-templates'
						)}</a>`
					),
					type: 'success',
				});

				closeModal();
			}
			catch {
				setLoading(false);

				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				});
			}
		},
		[
			closeModal,
			createTemplateURL,
			getFormData,
			layoutId,
			onSubmitPageTemplateSet,
			resetErrors,
			segmentsExperienceId,
			submitPageTemplateSet,
			templateSetDescription,
			templateSetId,
			templateSetName,
			validateForm,
		]
	);

	let title = Liferay.Language.get('select-page-template-set');

	if (allowCustomName) {
		title = Liferay.Language.get('add-page-template');
	}
	else if (openAddTemplateSetModal) {
		title = Liferay.Language.get('add-page-template-set');
	}

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ClayModal.Body>
				{hasMultipleSegmentsExperienceIds && (
					<div className="form-feedback-group mb-3">
						<div className="form-feedback-item text-info">
							<ClayIcon className="mr-2" symbol="info-circle" />

							<span>
								{Liferay.Language.get(
									'the-page-template-is-based-on-the-current-experience'
								)}
							</span>
						</div>
					</div>
				)}

				<ClayForm onSubmit={handleSubmit}>
					{allowCustomName && (
						<FormField
							error={formErrors.pageTemplateName}
							id={`${namespace}pageTemplateName`}
							name={Liferay.Language.get('page-template-name')}
							required
						>
							<ClayInput
								id={`${namespace}pageTemplateName`}
								onChange={(event) => {
									setPageTemplateName(event.target.value);

									setFormErrors({
										...formErrors,
										pageTemplateName: '',
									});
								}}
								required
								value={pageTemplateName}
							/>
						</FormField>
					)}

					{openAddTemplateSetModal ? (
						<>
							{!availableSets.length ? (
								<div className="mb-3 text-secondary">
									{Liferay.Language.get(
										'a-page-template-set-must-first-be-created-before-you-can-create-your-page-template'
									)}
								</div>
							) : null}
							<FormField
								error={formErrors.templateSetName}
								id={`${namespace}templateSetName`}
								name={nameLabel}
								required
							>
								<ClayInput
									id={`${namespace}templateSetName`}
									name={`${namespace}name`}
									onChange={(event) => {
										setTemplateSetName(event.target.value);

										setFormErrors({
											...formErrors,
											templateSetName: '',
										});
									}}
									ref={nameInputRef}
									required
									value={templateSetName}
								/>
							</FormField>
							<FormField
								id={`${namespace}templateSetDescription`}
								name={Liferay.Language.get('description')}
							>
								<ClayInput
									component="textarea"
									id={`${namespace}templateSetDescription`}
									name={`${namespace}description`}
									onChange={(event) => {
										setTemplateSetDescription(
											event.target.value
										);
									}}
									ref={nameInputRef}
									value={templateSetDescription}
								/>
							</FormField>
						</>
					) : (
						<>
							<div className="mb-3 text-secondary">
								{Liferay.Language.get(
									'select-an-existing-set-or-create-a-new-one-to-save-your-page-template'
								)}
							</div>

							<FormField
								error={formErrors.templateSetId}
								id={`${namespace}templateSet`}
								name={Liferay.Language.get('page-template-set')}
								required
							>
								<ClaySelectWithOption
									id={`${namespace}templateSet`}
									onChange={(event) => {
										setTemplateSetId(event.target.value);
										setFormErrors({
											...formErrors,
											templateSetId: '',
										});
									}}
									options={templateSetSelectOptions}
									required
									value={templateSetId}
								/>
							</FormField>
						</>
					)}
				</ClayForm>
			</ClayModal.Body>

			<ClayModal.Footer
				first={
					!openAddTemplateSetModal ? (
						<ClayButton
							displayType="secondary"
							onClick={() => setOpenAddTemplateSetModal(true)}
						>
							{Liferay.Language.get('save-in-new-set')}
						</ClayButton>
					) : undefined
				}
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							onClick={handleSubmit}
						>
							{loading && (
								<span className="inline-item inline-item-before">
									<span
										aria-hidden="true"
										className="loading-animation"
									></span>
								</span>
							)}

							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}

function getUniqueName(items: PageTemplateSet[], languageKey: string) {
	let name = languageKey;

	const names = new Set(items.map((item) => item.name));

	items.forEach((_, index) => {
		if (names.has(name)) {
			name = `${languageKey} ${index + 2}`;
		}
	});

	return name;
}
