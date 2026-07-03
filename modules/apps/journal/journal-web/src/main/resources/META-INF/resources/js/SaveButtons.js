/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import initializeLock from './initializeLock';
import PublishModal from './modals/PublishModal';
import removeAlert from './removeAlert';
import showAlert from './showAlert';

const ACTION_PUBLISH = 'publish';
const ACTION_DRAFT = 'draft';
const ACTION_SCHEDULE = 'schedule';

export default function SaveButtons({
	articleId: initialArticleId,
	defaultLanguageId: initialDefaultLanguageId,
	displayDate,
	editingDefaultValues,
	isPending,
	permissionsURL,
	portletNamespace,
	publishButtonLabel,
	saveButtonLabel,
	selectedLanguageId,
	showPublishModal,
	timeZone,
	use12Hours,
	workflowEnabled,
}) {
	const formId = `${portletNamespace}fm1`;

	const [articleId, setArticleId] = useState(initialArticleId);

	const [defaultLanguageId, setDefaultLanguageId] = useState(
		initialDefaultLanguageId
	);

	const [{publishModalAction, publishModalVisible}, setPublishModalState] =
		useState({publishModalAction: '', publishModalVisible: false});

	const [saveButtonDisabled, setSaveButtonDisabled] = useState(false);

	const lockRef = useRef(null);

	useEffect(() => {
		const localeChangeHandler = (event) => {
			const defaultLanguageId = event.item.getAttribute('data-value');

			setDefaultLanguageId(defaultLanguageId);
		};

		Liferay.on('inputLocalized:defaultLocaleChanged', localeChangeHandler);

		return () =>
			Liferay.detach(
				'inputLocalized:defaultLocaleChanged',
				localeChangeHandler
			);
	}, []);

	useEffect(() => {
		initializeLock('publishing', {
			errorIndicator: document.getElementById(
				`${portletNamespace}lockErrorIndicator`
			),
			lockedIndicator: document.getElementById(
				`${portletNamespace}savingChangesIndicator`
			),
			namespace: portletNamespace,
			onLockChange: ({isLocked}) => {
				setSaveButtonDisabled(isLocked);

				const resetValuesButton = document.getElementById(
					`${portletNamespace}resetValuesButton`
				);

				if (resetValuesButton) {
					resetValuesButton.disabled = isLocked;
				}
			},
			unlockedIndicator: document.getElementById(
				`${portletNamespace}changesSavedIndicator`
			),
		});

		Liferay.componentReady(`${portletNamespace}publishing`).then(
			(publishLock) => {
				lockRef.current = publishLock;
			}
		);
	}, [portletNamespace]);

	const validateDefaultLanguageTitle = () => {
		const titleInputComponent = Liferay.component(
			`${portletNamespace}titleMapAsXML`
		);

		if (!titleInputComponent?.getValue(defaultLanguageId)) {
			showAlert(
				sub(
					Liferay.Language.get(
						'please-enter-a-valid-title-for-the-default-language-x'
					),
					defaultLanguageId.replaceAll('_', '-')
				)
			);

			return false;
		}

		return true;
	};

	const onClick = async (action, directSubmit = false) => {
		removeAlert();

		if (!(await validateRequiredFields(formId))) {
			return;
		}

		if (!validateDefaultLanguageTitle()) {
			return;
		}

		if (directSubmit || (articleId && !showPublishModal)) {
			handleButtonClick(action);

			return;
		}

		setPublishModalState({
			publishModalAction: action,
			publishModalVisible: true,
		});
	};

	const onScheduleButtonClick = async () => {
		if (await validateRequiredFields(formId)) {
			if (!validateDefaultLanguageTitle()) {
				return;
			}

			setPublishModalState({
				publishModalAction: ACTION_SCHEDULE,
				publishModalVisible: true,
			});
		}
	};

	const handleButtonClick = (action) => {
		if (lockRef.current?.isLocked()) {
			return;
		}

		lockRef.current?.lock();

		const workflowActionInput = document.getElementById(
			`${portletNamespace}workflowAction`
		);

		if (
			action === ACTION_PUBLISH ||
			publishModalAction === ACTION_PUBLISH ||
			publishModalAction === ACTION_SCHEDULE
		) {
			workflowActionInput.value = Liferay.Workflow.ACTION_PUBLISH;
		}

		const actionInput = document.getElementById(
			`${portletNamespace}jakarta-portlet-action`
		);

		if (editingDefaultValues) {
			Liferay.component(`${portletNamespace}dataEngineLayoutRenderer`)
				.reactComponentRef.current.getFields()
				.forEach((field) => {
					field.required = false;
				});

			actionInput.value = articleId
				? '/journal/update_data_engine_default_values'
				: '/journal/add_data_engine_default_values';
		}
		else {
			actionInput.value = articleId
				? '/journal/update_article'
				: '/journal/add_article';
		}

		const titleInputComponent = Liferay.component(
			`${portletNamespace}titleMapAsXML`
		);
		const descriptionInputComponent = Liferay.component(
			`${portletNamespace}descriptionMapAsXML`
		);

		[titleInputComponent, descriptionInputComponent].forEach(
			(inputComponent) => {
				if (!inputComponent) {
					return;
				}

				const translatedLanguages = inputComponent.get(
					'translatedLanguages'
				);

				if (
					!translatedLanguages.has(selectedLanguageId) &&
					selectedLanguageId !== defaultLanguageId
				) {
					inputComponent.updateInput('');

					Liferay.Form.get(formId).removeRule(
						`${portletNamespace}${inputComponent.get('id')}`,
						'required'
					);
				}
			}
		);

		const form = document.getElementById(formId);

		if (form) {
			form.requestSubmit();
		}
	};

	const validateRequiredFields = async (formId) => {
		const formValidator = Liferay.Form?.get(formId)?.formValidator;

		formValidator.validate();

		if (formValidator.hasErrors()) {
			return false;
		}

		const renderer = await Liferay.componentReady(
			`${portletNamespace}dataEngineLayoutRenderer`
		);

		try {
			const [, isValid] =
				await renderer.reactComponentRef.current.validate();

			return isValid;
		}
		catch (error) {
			if (error?.message && error.name !== 'AbortError') {
				showAlert(error.message);
			}

			return false;
		}
	};

	useEffect(() => {
		if (Liferay.FeatureFlags['LPD-11228']) {
			const updateArticleId = ({articleId}) => {
				setArticleId(articleId);
			};
			Liferay.on('asyncFormSubmission', updateArticleId);

			return () => {
				Liferay.detach('asyncFormSubmission', updateArticleId);
			};
		}
	}, []);

	return (
		<div className="d-flex">
			{!Liferay.FeatureFlags['LPD-11228'] && !editingDefaultValues ? (
				<ClayButton
					className="mr-3"
					displayType="secondary"
					form={formId}
					onClick={() => onClick(ACTION_DRAFT)}
					title={
						articleId
							? null
							: Liferay.Language.get(
									'save-as-draft-with-permissions'
								)
					}
					type="button"
				>
					{saveButtonLabel}
				</ClayButton>
			) : null}

			<ClayButton
				className="rounded-0-right"
				disabled={saveButtonDisabled || isPending}
				displayType="primary"
				onClick={() => onClick(ACTION_PUBLISH, true)}
				title={sub(
					workflowEnabled
						? Liferay.Language.get('submit-x-for-workflow')
						: Liferay.Language.get('publish-x'),
					Liferay.Language.get('article')
				)}
				type="button"
			>
				{publishButtonLabel}
			</ClayButton>

			<ClayDropDown
				hasLeftSymbols
				trigger={
					<ClayButton
						aria-label={Liferay.Language.get('publish-options')}
						className="border-left px-2 rounded-0-left"
						disabled={saveButtonDisabled || isPending}
						title={Liferay.Language.get('publish-options')}
					>
						<span className="inline-item">
							<ClayIcon symbol="caret-bottom" />
						</span>
					</ClayButton>
				}
			>
				<ClayDropDown.ItemList>
					{(!articleId || showPublishModal) && (
						<ClayDropDown.Item
							form={formId}
							onClick={() => onClick(ACTION_PUBLISH)}
							symbolLeft="arrow-right-full"
							type="button"
						>
							{workflowEnabled
								? Liferay.Language.get(
										'submit-for-workflow-with-permissions'
									)
								: Liferay.Language.get(
										'publish-with-permissions'
									)}
						</ClayDropDown.Item>
					)}

					<ClayDropDown.Item
						onClick={onScheduleButtonClick}
						symbolLeft="date-time"
						type="button"
					>
						{workflowEnabled
							? Liferay.Language.get(
									'schedule-publication-and-submit-for-workflow'
								)
							: Liferay.Language.get('schedule-publication')}
					</ClayDropDown.Item>
				</ClayDropDown.ItemList>
			</ClayDropDown>

			{publishModalVisible ? (
				<PublishModal
					actionButton={publishModalAction}
					articleId={articleId}
					buttonDisabled={saveButtonDisabled}
					displayDate={displayDate}
					onCloseModal={() =>
						setPublishModalState({
							publishModalAction: '',
							publishModalVisible: false,
						})
					}
					onPublishButtonClick={handleButtonClick}
					permissionsURL={permissionsURL}
					portletNamespace={portletNamespace}
					showPermissionsOptions={showPublishModal}
					timeZone={timeZone}
					use12Hours={use12Hours}
					workflowEnabled={workflowEnabled}
				/>
			) : null}
		</div>
	);
}
