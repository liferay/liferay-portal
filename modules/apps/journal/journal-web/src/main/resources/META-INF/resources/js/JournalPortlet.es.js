/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	debounce,
	fetch,
	navigate,
	openConfirmModal,
	sub,
} from 'frontend-js-web';

import {LocaleChangedHandler} from './LocaleChangedHandler.es';
import removeAlert from './removeAlert';
import showAlert from './showAlert';

const AUTO_SAVE_DELAY = 1500;

export default function _JournalPortlet({
	articleId: initialArticleId,
	autoSaveDraftEnabled,
	autoSaveDraftURL,
	availableLocales: initialAvailableLocales,
	classNameId,
	contentTitle,
	defaultLanguageId: initialDefaultLanguageId,
	displayDate,
	hasSavePermission,
	namespace,
}) {
	const formId = `${namespace}fm1`;

	const actionInput = document.getElementById(
		`${namespace}javax-portlet-action`
	);
	const availableLocalesInput = document.getElementById(
		`${namespace}availableLocales`
	);
	const contextualSidebarButton = document.getElementById(
		`${namespace}contextualSidebarButton`
	);
	const contextualSidebarContainer = document.getElementById(
		`${namespace}contextualSidebarContainer`
	);
	const form = document.getElementById(formId);
	const formDateInput = document.getElementById(`${namespace}formDate`);
	const publishButton = document.getElementById(`${namespace}publishButton`);
	const resetValuesButton = document.getElementById(
		`${namespace}resetValuesButton`
	);
	const saveButton = document.getElementById(`${namespace}saveButton`);

	const availableLocales = [
		...initialAvailableLocales,
		initialDefaultLanguageId,
	];

	availableLocalesInput.value = availableLocales;

	let articleId = initialArticleId;
	let defaultLanguageId = initialDefaultLanguageId;
	let selectedLanguageId = initialDefaultLanguageId;

	const lockHolder = {};

	Liferay.componentReady(`${namespace}publishing`).then((lock) => {
		lockHolder.lock = lock;
	});

	const editingDefaultValues = classNameId && classNameId !== '0';

	if (editingDefaultValues) {
		const getInput = (inputName) =>
			document.getElementById(`${namespace}${inputName}`);

		const resetInput = (inputName) => {
			const input = getInput(inputName);

			if (input && !displayDate) {
				input.value = '';
			}
		};

		resetInput('displayDate');
		resetInput('displayDateAmPm');
		resetInput('displayDateDay');
		resetInput('displayDateHour');
		resetInput('displayDateMinute');
		resetInput('displayDateMonth');
		resetInput('displayDateTime');
		resetInput('displayDateYear');

		const displayDateInput = getInput('displayDate');

		if (displayDateInput) {
			displayDateInput.addEventListener('change', (event) => {
				if (!event.target.value) {
					getInput('displayDateDay').value = '';
					getInput('displayDateMonth').value = '';
					getInput('displayDateYear').value = '';
				}
			});
		}
	}

	const handleContextualSidebarButton = () => {
		contextualSidebarContainer?.classList.toggle(
			'contextual-sidebar-visible'
		);
	};

	const isContextualSidebarOpen = () =>
		contextualSidebarContainer.classList.contains(
			'contextual-sidebar-visible'
		);

	const updateContextualSidebarAriaAttributes = () => {
		const isOpen = isContextualSidebarOpen();

		const title = isOpen
			? Liferay.Language.get('close-configuration-panel')
			: Liferay.Language.get('open-configuration-panel');

		contextualSidebarButton.setAttribute('aria-label', title);
		contextualSidebarButton.setAttribute('aria-selected', isOpen);
		contextualSidebarButton.setAttribute('title', title);
	};

	const handleContextualSidebarButtonClick = () => {
		handleContextualSidebarButton();

		updateContextualSidebarAriaAttributes();

		if (isContextualSidebarOpen()) {
			contextualSidebarContainer.focus({preventScroll: true});
		}
	};

	const handleDDMFormError = (event) => {
		if (event.error?.statusCode) {
			showAlert(event.error.message);
		}

		const workflowActionInput = document.getElementById(
			`${namespace}workflowAction`
		);

		workflowActionInput.value = Liferay.Workflow.ACTION_SAVE_DRAFT;

		const titleInputComponent = Liferay.component(
			`${namespace}titleMapAsXML`
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

			validateRequiredDDMFields();
		}
	};

	const handleDDMFormValid = (
		{redirectOnSave, showErrors} = {
			redirectOnSave: false,
			showErrors: false,
		}
	) => {
		const titleInputComponent = Liferay.component(
			`${namespace}titleMapAsXML`
		);

		if (
			titleInputComponent?.getValue(defaultLanguageId) ||
			editingDefaultValues
		) {
			if (!articleId) {
				const newArticleIdInput = document.getElementById(
					`${namespace}newArticleId`
				);

				articleId = newArticleIdInput.value || '';
			}

			const articleIdInput = document.getElementById(
				`${namespace}articleId`
			);

			articleIdInput.value = articleId;

			availableLocalesInput.value = availableLocales;

			if (autoSaveDraftEnabled && !redirectOnSave) {
				if (showErrors) {
					Liferay.componentReady(
						`${namespace}dataEngineLayoutRenderer`
					)
						.then((dataEngineLayoutRenderer) => {
							const dataEngineLayoutRendererRef =
								dataEngineLayoutRenderer?.reactComponentRef;

							return dataEngineLayoutRendererRef.current.validate();
						})
						.then((validForm) => {
							if (validForm) {
								removeAlert();
								submitAsyncForm(form, {redirectOnSave});
							}
						});
				}
				else {
					submitAsyncForm(form, {redirectOnSave});
				}
			}
			else {
				form.submit();
			}
		}
		else if (showErrors) {
			showAlert(
				sub(
					Liferay.Language.get(
						'please-enter-a-valid-title-for-the-default-language-x'
					),
					defaultLanguageId.replaceAll('_', '-')
				)
			);

			validateRequiredDDMFields();

			lockHolder.lock?.unlock(true);
		}
		else {
			showAlert(
				Liferay.Language.get(
					'please-complete-all-mandatory-fields-to-enable-autosave'
				),
				Liferay.Language.get('info'),
				'info'
			);
			lockHolder.lock?.unlock(true);
		}
	};

	const handlePublishButtonClick = (event) => {
		lockHolder.lock?.lock();

		if (
			Liferay.FeatureFlags['LPD-11228']
		) {
			return;
		}

		document
			.querySelectorAll('.journal-alert-container')
			.forEach((alertElement) => {
				alertElement.parentElement.removeChild(alertElement);
			});

		const workflowActionInput = document.getElementById(
			`${namespace}workflowAction`
		);

		if (event.currentTarget.dataset.actionname === 'publish') {
			workflowActionInput.value = Liferay.Workflow.ACTION_PUBLISH;
		}

		if (editingDefaultValues) {
			Liferay.component(`${namespace}dataEngineLayoutRenderer`)
				.reactComponentRef.current.getFields()
				.forEach((field) => {
					field.required = false;
				});

			actionInput.value = articleId
				? '/journal/update_data_engine_default_values'
				: '/journal/add_data_engine_default_values';
		}
		else {
			articleId = document.getElementById(`${namespace}articleId`).value;

			actionInput.value = articleId
				? '/journal/update_article'
				: '/journal/add_article';
		}

		const descriptionInputComponent = Liferay.component(
			`${namespace}descriptionMapAsXML`
		);
		const titleInputComponent = Liferay.component(
			`${namespace}titleMapAsXML`
		);

		[titleInputComponent, descriptionInputComponent].forEach(
			(inputComponent) => {
				const translatedLanguages = inputComponent.get(
					'translatedLanguages'
				);

				if (
					!translatedLanguages.has(selectedLanguageId) &&
					selectedLanguageId !== defaultLanguageId
				) {
					inputComponent.updateInput('');

					Liferay.Form.get(formId).removeRule(
						`${namespace}${inputComponent.get('id')}`,
						'required'
					);
				}
			}
		);

		lockHolder.lock?.unlock();
	};

	const handleResetValuesButtonClick = (event) => {
		lockHolder.lock?.lock();

		openConfirmModal({
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-reset-the-default-values'
			),
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					if (editingDefaultValues) {
						actionInput.value = articleId
							? '/journal/update_data_engine_default_values'
							: '/journal/add_data_engine_default_values';
					}

					submitForm(
						document.hrefFm,
						event.currentTarget.dataset.url
					);
				}
				else {
					lockHolder.lock?.unlock();
				}
			},
		});
	};

	const submitAsyncForm = (
		formElement,
		{redirectOnSave} = {redirectOnSave: false}
	) => {
		return fetch(autoSaveDraftURL, {
			body: new FormData(formElement),
			method: formElement.method,
		})
			.then((response) => {
				if (redirectOnSave) {
					navigate(
						response.redirected && response.url
							? response.url
							: window.location.href
					);
				}

				return response.json();
			})
			.then((data) => {
				if (data.success) {
					if (!articleId) {
						articleId = data.articleId;
						document.getElementById(`${namespace}articleId`).value =
							articleId;

						Liferay.fire('asyncFormSubmission', {articleId});

						const friendlyUrlInputComponent = Liferay.component(
							`${namespace}friendlyURL`
						);

						if (!friendlyUrlInputComponent.getValue()) {
							const friendlyURL = data.friendlyURL;
							friendlyUrlInputComponent.updateInputLanguage(
								friendlyURL,
								defaultLanguageId
							);
							friendlyUrlInputComponent.updateInput(friendlyURL);

							Liferay.fire('journal:update-friendly-url', {
								friendlyURL,
							});
						}
					}

					const articleIdWrapper = document.getElementById(
						`${namespace}articleIdWrapper`
					);
					const articleVersionInput = document.getElementById(
						`${namespace}version`
					);
					const articleVersionStatusWrapper = document.getElementById(
						`${namespace}articleVersionStatusWrapper`
					);
					const displayedArticleId = document.getElementById(
						`${namespace}displayedArticleId`
					);
					const displayedVersion = document.getElementById(
						`${namespace}displayedVersion`
					);
					const statusDraftLabel = document.getElementById(
						`${namespace}statusDraftLabel`
					);
					const statusLabel = document.getElementById(
						`${namespace}statusLabel`
					);

					if (statusLabel) {
						statusLabel.classList.add('hide');
					}

					articleVersionStatusWrapper.classList.remove('hide');
					statusDraftLabel.classList.remove('hide');

					articleVersionInput.value = data.version;
					displayedVersion.innerHTML = data.version;

					articleIdWrapper.classList.remove('hide');
					displayedArticleId.innerHTML = articleId;

					formDateInput.value = data.modifiedDate;
					lockHolder.lock?.unlock();
					removeAlert();
				}
				else {
					formDateInput.value = data.modifiedDate;
					lockHolder.lock?.unlock(true);
					showAlert(
						Liferay.Language.get(
							'please-complete-all-mandatory-fields-to-enable-autosave'
						),
						Liferay.Language.get('info'),
						'info'
					);
				}
			})
			.catch((error) => {
				console.error(error);
				lockHolder.lock?.unlock(true);
			});
	};

	const eventHandlers = [
		attachListener(
			contextualSidebarButton,
			'click',
			handleContextualSidebarButtonClick
		),
		attachListener(publishButton, 'click', handlePublishButtonClick),
		attachListener(saveButton, 'click', handlePublishButtonClick),
		attachListener(
			resetValuesButton,
			'click',
			handleResetValuesButtonClick
		),

		new LocaleChangedHandler({
			contentTitle,
			defaultLanguageId,
			namespace,
			onDefaultLocaleChangedCallback: (languageId) => {
				defaultLanguageId = languageId;
			},
			onLocaleChangedCallback: (_context, languageId) => {
				if (!availableLocales.includes(languageId)) {
					availableLocales.push(languageId);
					availableLocalesInput.value = availableLocales;
				}

				selectedLanguageId = languageId;
			},
		}),

		Liferay.on('ddmFormError', handleDDMFormError),
		Liferay.on('ddmFormValid', () =>
			handleDDMFormValid({
				redirectOnSave: true,
				showErrors: true,
			})
		),
	];

	const validateRequiredDDMFields = () => {
		Liferay.componentReady(`${namespace}dataEngineLayoutRenderer`).then(
			(dataEngineLayoutRenderer) => {
				const dataEngineLayoutRendererRef =
					dataEngineLayoutRenderer?.reactComponentRef;

				return dataEngineLayoutRendererRef.current.validate();
			}
		);
	};

	if (
		autoSaveDraftEnabled &&
		hasSavePermission &&
		(!classNameId || classNameId === '0')
	) {
		eventHandlers.push(
			attachFormChangeListener(
				form,
				() => {
					return !lockHolder.lock?.isLocked();
				},
				(mutationRecord) => {
					if (lockHolder.lock?.isLocked()) {
						return false;
					}

					return [
						mutationRecord.target,
						...mutationRecord.addedNodes,
						...mutationRecord.removedNodes,
					].some(
						(node) =>
							node.name &&
							node.name.startsWith(namespace) &&
							node.name !== `${namespace}languageId`
					);
				},
				() => {
					if (lockHolder.lock?.isLocked()) {
						return;
					}

					lockHolder.lock?.lock();

					actionInput.value = articleId
						? '/journal/update_article'
						: '/journal/add_article';

					handleDDMFormValid({
						redirectOnSave: false,
						showErrors: false,
					});
				},
				namespace
			)
		);
	}

	if (window.innerWidth > Liferay.BREAKPOINTS.PHONE) {
		handleContextualSidebarButton();
	}

	updateContextualSidebarAriaAttributes();

	return {
		dispose() {
			eventHandlers.forEach((eventHandler) => {
				eventHandler.detach();
			});
		},
	};
}

function attachFormChangeListener(
	form,
	accentChangeEvent,
	acceptMutationRecord,
	callback,
	namespace
) {
	const handleChange = debounce(() => {
		callback();
	}, AUTO_SAVE_DELAY);

	const mutationObserver = new MutationObserver((mutationRecords) => {
		const observedMutationRecords = mutationRecords
			.filter((mutationRecord) => {
				if (mutationRecord.target.id === `${namespace}formDate`) {
					return;
				}
				else if (mutationRecord.type === 'attributes') {
					return (
						mutationRecord.oldValue !== null &&
						mutationRecord.target.value.trim() !==
							mutationRecord.oldValue.trim()
					);
				}
				else if (mutationRecord.type === 'childList') {
					return [
						...mutationRecord.addedNodes,
						...mutationRecord.removedNodes,
					].some((node) => node.name);
				}
			})
			.filter((mutationRecord) => acceptMutationRecord(mutationRecord));

		if (observedMutationRecords.length) {
			handleChange();
		}
	});

	mutationObserver.observe(form, {
		attributeFilter: ['value'],
		attributeOldValue: true,
		attributes: true,
		childList: true,
		subtree: true,
	});

	const handleFormChange = (event) => {
		if (accentChangeEvent(event)) {
			handleChange();
		}
	};

	form.addEventListener('change', handleFormChange);

	return {
		detach() {
			mutationObserver.disconnect();
			form.removeEventListener('change', handleFormChange);
		},
	};
}

function attachListener(element, eventType, callback) {
	element?.addEventListener(eventType, callback);

	return {
		detach() {
			element?.removeEventListener(eventType, callback);
		},
	};
}
