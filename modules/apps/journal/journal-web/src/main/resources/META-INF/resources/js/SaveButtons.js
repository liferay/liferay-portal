/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import React, {useEffect, useState} from 'react';

import initializeLock from './initializeLock';
import PublishModal from './modals/PublishModal';
import removeAlert from './removeAlert';

const ACTION_PUBLISH = 'publish';
const ACTION_DRAFT = 'draft';
const ACTION_SCHEDULE = 'schedule';

export default function SaveButtons({
	articleId: initialArticleId,
	defaultLanguageId,
	displayDate,
	editingDefaultValues,
	permissionsURL,
	portletNamespace,
	publishButtonLabel,
	saveButtonLabel,
	selectedLanguageId,
	showPublishModal,
	timeZone,
	workflowEnabled,
}) {
	const formId = `${portletNamespace}fm1`;

	const [articleId, setArticleId] = useState(initialArticleId);

	const [{publishModalAction, publishModalVisible}, setPublishModalState] =
		useState({publishModalAction: '', publishModalVisible: false});

	const [saveButtonDisabled, setSaveButtonDisabled] = useState(false);

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
	}, [portletNamespace]);

	const onClick = (action) => {
		const titleInputComponent = Liferay.component(
			`${portletNamespace}titleMapAsXML`
		);

		if (titleInputComponent?.getValue(defaultLanguageId)) {
			if (articleId && !showPublishModal) {
				handleButtonClick(action);
			}
			else {
				setPublishModalState({
					publishModalAction: action,
					publishModalVisible: true,
				});
			}
		}
		else {
			validateRequiredFields(formId);
		}
	};

	const handleButtonClick = (action) => {
		removeAlert();

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
	};

	const validateRequiredFields = (formId) => {
		Liferay.Form.get(formId).formValidator.validate();
		Liferay.componentReady(
			`${portletNamespace}dataEngineLayoutRenderer`
		).then((dataEngineLayoutRenderer) => {
			const dataEngineLayoutRendererRef =
				dataEngineLayoutRenderer?.reactComponentRef;

			return dataEngineLayoutRendererRef.current.validate();
		});
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
					type={articleId ? 'submit' : 'button'}
				>
					{saveButtonLabel}
				</ClayButton>
			) : null}

			<ClayDropDown
				hasLeftSymbols
				trigger={
					<ClayButton
						aria-label={
							workflowEnabled
								? Liferay.Language.get(
										'select-and-confirm-submit-for-workflow-settings'
									)
								: Liferay.Language.get(
										'select-and-confirm-publish-settings'
									)
						}
						disabled={saveButtonDisabled}
						title={
							workflowEnabled
								? Liferay.Language.get(
										'select-and-confirm-submit-for-workflow-settings'
									)
								: Liferay.Language.get(
										'select-and-confirm-publish-settings'
									)
						}
					>
						{publishButtonLabel}

						<span className="inline-item inline-item-after">
							<ClayIcon symbol="caret-bottom" />
						</span>
					</ClayButton>
				}
			>
				<ClayDropDown.ItemList>
					<ClayDropDown.Item
						form={formId}
						onClick={() => onClick(ACTION_PUBLISH)}
						symbolLeft="arrow-right-full"
						type={showPublishModal ? 'button' : 'submit'}
					>
						{articleId
							? workflowEnabled
								? showPublishModal
									? Liferay.Language.get(
											'submit-for-workflow-with-permissions'
										)
									: Liferay.Language.get(
											'submit-for-workflow'
										)
								: showPublishModal
									? Liferay.Language.get(
											'publish-with-permissions'
										)
									: Liferay.Language.get('publish')
							: workflowEnabled
								? Liferay.Language.get(
										'submit-for-workflow-with-permissions'
									)
								: Liferay.Language.get(
										'publish-with-permissions'
									)}
					</ClayDropDown.Item>

					<ClayDropDown.Item
						onClick={() => {
							const titleInputComponent = Liferay.component(
								`${portletNamespace}titleMapAsXML`
							);
							if (
								titleInputComponent?.getValue(defaultLanguageId)
							) {
								setPublishModalState({
									publishModalAction: ACTION_SCHEDULE,
									publishModalVisible: true,
								});
							}
							else {
								validateRequiredFields(formId);
							}
						}}
						symbolLeft="date-time"
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
					workflowEnabled={workflowEnabled}
				/>
			) : null}
		</div>
	);
}
