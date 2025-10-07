/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import React, {useCallback, useEffect, useState} from 'react';

const META_FIELD_NAMES = {
	description: 'descriptionMapAsXML',
	friendlyURL: 'friendlyURL',
	title: 'titleMapAsXML',
};

const METADATA_FIELD_NAME_HISTORY = {
	[`${META_FIELD_NAMES.description}Editor`]:
		Liferay.Language.get('description'),
	[META_FIELD_NAMES.friendlyURL]: Liferay.Language.get('friendly-url'),
	[META_FIELD_NAMES.title]: Liferay.Language.get('title'),
};

export default function UndoRedo({
	initialDefaultLanguageId,
	initialFields,
	languageId,
	portletNamespace,
}) {
	const [active, setActive] = useState(false);
	const [lock, setLock] = useState(false);

	const [

		// eslint-disable-next-line no-unused-vars
		{defaultLanguageId, history, selectedLanguageId, step},
		setState,
	] = useState({
		defaultLanguageId: initialDefaultLanguageId,
		history: [
			{
				defaultLanguageId: initialDefaultLanguageId,
				descriptionInputValue:
					initialFields[`${META_FIELD_NAMES.description}`][
						`${initialDefaultLanguageId}`
					] || '',
				descriptionTranslatedLanguages: Object.keys(
					initialFields[`${META_FIELD_NAMES.description}`]
				),
				friendlyURLInputValue:
					initialFields[`${META_FIELD_NAMES.friendlyURL}`][
						`${initialDefaultLanguageId}`
					] || '',
				friendlyURLTranslatedLanguages: Object.keys(
					initialFields[`${META_FIELD_NAMES.friendlyURL}`]
				),
				name: 'Reset',
				selectedLanguageId: languageId,
				titleInputValue:
					initialFields[`${META_FIELD_NAMES.title}`][
						`${initialDefaultLanguageId}`
					] || '',
				titleTranslatedLanguages: Object.keys(
					initialFields[`${META_FIELD_NAMES.title}`]
				),
			},
		],
		selectedLanguageId: languageId,
		step: 0,
	});

	const [descriptionInputComponent, setDescriptionInputComponent] =
		useState(null);
	const [friendlyURLInputComponent, setFriendlyURLInputComponent] =
		useState(null);
	const [titleInputComponent, setTitleInputComponent] = useState(null);

	useEffect(() => {
		Liferay.componentReady(
			`${portletNamespace}${META_FIELD_NAMES.description}`
		).then(setDescriptionInputComponent);

		Liferay.componentReady(
			`${portletNamespace}${META_FIELD_NAMES.friendlyURL}`
		).then(setFriendlyURLInputComponent);

		Liferay.componentReady(
			`${portletNamespace}${META_FIELD_NAMES.title}`
		).then(setTitleInputComponent);
	}, [portletNamespace]);

	const handleUndo = (newStep = step - 1) => {
		setLock(true);
		const nextStep = history[newStep];

		const selectedLanguageIdInput = document.getElementById(
			`${portletNamespace}languageId`
		);
		if (nextStep.selectedLanguageId !== selectedLanguageIdInput.value) {
			descriptionInputComponent
				.get('translatedLanguages')
				.values()
				.map((lang) => {
					if (
						!nextStep.descriptionTranslatedLanguages.includes(lang)
					) {
						descriptionInputComponent
							.get('translatedLanguages')
							.remove(lang);
						descriptionInputComponent.removeInputLanguage(lang);
						descriptionInputComponent._updateTranslationStatus(
							selectedLanguageIdInput.value
						);
					}
				});
			descriptionInputComponent.selectFlag(nextStep.selectedLanguageId);
			friendlyURLInputComponent
				.get('translatedLanguages')
				.values()
				.map((lang) => {
					if (
						!nextStep.friendlyURLTranslatedLanguages.includes(lang)
					) {
						friendlyURLInputComponent
							.get('translatedLanguages')
							.remove(lang);
						friendlyURLInputComponent.removeInputLanguage(lang);
						friendlyURLInputComponent._updateTranslationStatus(
							selectedLanguageIdInput.value
						);
					}
				});
			friendlyURLInputComponent.selectFlag(nextStep.selectedLanguageId);
			titleInputComponent
				.get('translatedLanguages')
				.values()
				.map((lang) => {
					if (!nextStep.titleTranslatedLanguages.includes(lang)) {
						titleInputComponent
							.get('translatedLanguages')
							.remove(lang);
						titleInputComponent.removeInputLanguage(lang);
						titleInputComponent._updateTranslationStatus(
							selectedLanguageIdInput.value
						);
					}
				});
			titleInputComponent.selectFlag(nextStep.selectedLanguageId);

			selectedLanguageIdInput.value = nextStep.selectedLanguageId;

			Liferay.fire('journal:updateSelectedLanguage', {
				item: document.querySelector(
					`[data-languageid="${nextStep.selectedLanguageId}"][data-value="${nextStep.selectedLanguageId}"]`
				),
			});
		}

		updateMetadataFields(nextStep, newStep);
		Liferay.fire('inputLocalized:updateTranslationStatus');
		setLock(false);
	};

	const handleRedo = (newStep = step + 1) => {
		setLock(true);
		const nextStep = history[newStep];

		const selectedLanguageIdInput = document.getElementById(
			`${portletNamespace}languageId`
		);

		if (nextStep.selectedLanguageId !== selectedLanguageIdInput.value) {
			selectedLanguageIdInput.value = nextStep.selectedLanguageId;

			nextStep.descriptionTranslatedLanguages.map((lang) => {
				if (
					!descriptionInputComponent
						.get('translatedLanguages')
						.has(lang)
				) {
					descriptionInputComponent
						.get('translatedLanguages')
						.add(lang);
					descriptionInputComponent._updateTranslationStatus(
						nextStep.selectedLanguageId
					);
				}
			});
			descriptionInputComponent.selectFlag(nextStep.selectedLanguageId);
			nextStep.friendlyURLTranslatedLanguages.map((lang) => {
				if (
					!friendlyURLInputComponent
						.get('translatedLanguages')
						.has(lang)
				) {
					friendlyURLInputComponent
						.get('translatedLanguages')
						.add(lang);
					friendlyURLInputComponent._updateTranslationStatus(
						nextStep.selectedLanguageId
					);
				}
			});
			friendlyURLInputComponent.selectFlag(nextStep.selectedLanguageId);
			nextStep.titleTranslatedLanguages.map((lang) => {
				if (!titleInputComponent.get('translatedLanguages').has(lang)) {
					titleInputComponent.get('translatedLanguages').add(lang);
					titleInputComponent._updateTranslationStatus(
						nextStep.selectedLanguageId
					);
				}
			});
			titleInputComponent.selectFlag(nextStep.selectedLanguageId);

			Liferay.fire('journal:updateSelectedLanguage', {
				item: document.querySelector(
					`[data-languageid="${nextStep.selectedLanguageId}"][data-value="${nextStep.selectedLanguageId}"]`
				),
			});
		}

		updateMetadataFields(nextStep, newStep);
		Liferay.fire('inputLocalized:updateTranslationStatus');
		setLock(false);
	};

	const handleStoreState = useCallback(
		({fieldName}) => {
			setLock(true);
			const defaultLanguageIdInput = document.getElementById(
				`${portletNamespace}defaultLanguageId`
			);

			const selectedLanguageIdInput = document.getElementById(
				`${portletNamespace}languageId`
			);

			const newHistory = {
				defaultLanguageId: defaultLanguageIdInput.value,
				descriptionInputValue: descriptionInputComponent.getValue(
					selectedLanguageIdInput.value
				),
				descriptionTranslatedLanguages: descriptionInputComponent
					.get('translatedLanguages')
					.values(),
				friendlyURLInputValue: friendlyURLInputComponent.getValue(
					selectedLanguageIdInput.value
				),
				friendlyURLTranslatedLanguages: friendlyURLInputComponent
					.get('translatedLanguages')
					.values(),
				name: fieldName,
				selectedLanguageId: selectedLanguageIdInput.value,
				titleInputValue: titleInputComponent.getValue(
					selectedLanguageIdInput.value
				),
				titleTranslatedLanguages: titleInputComponent
					.get('translatedLanguages')
					.values(),
			};

			setState((previousState) => ({
				defaultLanguageId: defaultLanguageIdInput.value,
				history: [
					...previousState.history.slice(0, previousState.step + 1),
					newHistory,
				],
				selectedLanguageId: selectedLanguageIdInput.value,
				step: previousState.step + 1,
			}));

			setLock(false);
		},
		[
			descriptionInputComponent,
			friendlyURLInputComponent,
			titleInputComponent,
			portletNamespace,
		]
	);

	const updateMetadataFields = (step, newStep) => {
		descriptionInputComponent.updateInputLanguage(
			step.descriptionInputValue,
			step.selectedLanguageId
		);

		friendlyURLInputComponent.updateInputLanguage(
			step.friendlyURLInputValue,
			step.selectedLanguageId
		);

		titleInputComponent.updateInputLanguage(
			step.titleInputValue,
			step.selectedLanguageId
		);

		if (!Liferay.FeatureFlags['LPD-11235']) {
			setTimeout(
				() =>
					descriptionInputComponent.updateInput(
						step.descriptionInputValue
					),
				200
			);
		}

		friendlyURLInputComponent.updateInput(step.friendlyURLInputValue);

		titleInputComponent.updateInput(step.titleInputValue);

		setState((previousState) => ({
			...previousState,
			defaultLanguageId: step.defaultLanguageId,
			selectedLanguageId: step.selectedLanguageId,
			step: newStep,
		}));
	};

	const resetStoreState = useCallback(
		({fieldName}) => {
			const defaultLanguageIdInput = document.getElementById(
				`${portletNamespace}defaultLanguageId`
			);

			const selectedLanguageIdInput = document.getElementById(
				`${portletNamespace}languageId`
			);

			const newHistory = {
				defaultLanguageId: defaultLanguageIdInput.value,
				descriptionInputValue: descriptionInputComponent.getValue(
					selectedLanguageIdInput.value
				),
				descriptionTranslatedLanguages: descriptionInputComponent
					.get('translatedLanguages')
					.values(),
				friendlyURLInputValue: friendlyURLInputComponent.getValue(
					selectedLanguageIdInput.value
				),
				friendlyURLTranslatedLanguages: friendlyURLInputComponent
					.get('translatedLanguages')
					.values(),
				name: fieldName,
				selectedLanguageId: selectedLanguageIdInput.value,
				titleInputValue: titleInputComponent.getValue(
					selectedLanguageIdInput.value
				),
				titleTranslatedLanguages: titleInputComponent
					.get('translatedLanguages')
					.values(),
			};

			setState({
				defaultLanguageId: defaultLanguageIdInput.value,
				history: [newHistory],
				selectedLanguageId: selectedLanguageIdInput.value,
				step: 0,
			});
		},
		[
			descriptionInputComponent,
			friendlyURLInputComponent,
			portletNamespace,
			titleInputComponent,
		]
	);

	const localeChangeHandler = useCallback(
		(event) => {
			const selectedLanguageId = event.item.getAttribute('data-value');
			const selectedLanguageIdInput = document.getElementById(
				`${portletNamespace}languageId`
			);

			selectedLanguageIdInput.value = selectedLanguageId;

			Liferay.fire('journal:storeState', {
				fieldName: Liferay.Language.get('change-language'),
			});
		},
		[portletNamespace]
	);

	const defaultLocaleChangeHandler = useCallback(
		(event) => {
			const defaultLanguageIdInput = document.getElementById(
				`${portletNamespace}defaultLanguageId`
			);
			const fieldName = 'Reset';
			const selectedLanguageId = event.item.getAttribute('data-value');
			const selectedLanguageIdInput = document.getElementById(
				`${portletNamespace}languageId`
			);

			defaultLanguageIdInput.value = selectedLanguageId;

			selectedLanguageIdInput.value = selectedLanguageId;

			resetStoreState({fieldName});
		},
		[portletNamespace, resetStoreState]
	);

	const handleUpdateFriendlyURL = useCallback(
		({friendlyURL}) => {
			const newHistory = history.map((step) => {
				if (step.titleInputValue) {
					step.friendlyURLInputValue = friendlyURL;
				}

				return step;
			});
			setState((prevState) => {
				return {
					...prevState,
					history: newHistory,
				};
			});
		},
		[history]
	);

	useEffect(() => {
		Liferay.after('journal:localeChanged', localeChangeHandler);
		Liferay.after(
			'journal:defaultLocaleChanged',
			defaultLocaleChangeHandler
		);

		return () => {
			Liferay.detach('journal:localeChanged', localeChangeHandler);
			Liferay.detach(
				'journal:defaultLocaleChanged',
				defaultLocaleChangeHandler
			);
		};
	}, [defaultLocaleChangeHandler, localeChangeHandler]);

	useEffect(() => {
		Liferay.on('journal:storeState', handleStoreState);

		return () => {
			Liferay.detach('journal:storeState', handleStoreState);
		};
	}, [handleStoreState]);

	useEffect(() => {
		Liferay.on('journal:update-friendly-url', handleUpdateFriendlyURL);

		return () => {
			Liferay.detach(
				'journal:update-friendly-url',
				handleUpdateFriendlyURL
			);
		};
	}, [handleUpdateFriendlyURL]);

	useEffect(() => {
		const handleLock = () => setLock(true);
		const handleUnlock = () => setLock(false);

		Liferay.on('journal:lock', handleLock);
		Liferay.on('journal:unlock', handleUnlock);

		return () => {
			Liferay.detach('journal:lock', handleLock);
			Liferay.detach('journal:unlock', handleUnlock);
		};
	}, []);

	return (
		<div className="d-flex">
			<ClayButtonWithIcon
				aria-label={Liferay.Language.get('undo')}
				className="btn-monospaced"
				disabled={step <= 0 || lock}
				displayType="secondary"
				name="journal_undo_redo"
				onClick={() => {
					Liferay.fire('journal:undo');
					handleUndo();
				}}
				size="sm"
				symbol="undo"
				title={Liferay.Language.get('undo')}
			/>

			<ClayButtonWithIcon
				aria-label={Liferay.Language.get('redo')}
				className="btn-monospaced"
				disabled={
					!history.length || step === history.length - 1 || lock
				}
				displayType="secondary"
				name="journal_undo_redo"
				onClick={() => {
					Liferay.fire('journal:redo');
					handleRedo();
				}}
				size="sm"
				symbol="redo"
				title={Liferay.Language.get('redo')}
			/>

			<ClayDropDown
				active={active}
				alignmentPosition={Align.BottomLeft}
				className="ml-2"
				onActiveChange={setActive}
				trigger={
					<ClayButton
						aria-label={Liferay.Language.get('history')}
						aria-pressed={active}
						className="px-2"
						disabled={history.length <= 1}
						displayType="secondary"
						size="sm"
						title={Liferay.Language.get('history')}
					>
						<span className="inline-item inline-item-before">
							<ClayIcon symbol="time" />
						</span>

						<span className="inline-item">
							<ClayIcon symbol="caret-bottom" />
						</span>
					</ClayButton>
				}
			>
				<ClayDropDown.ItemList>
					{history
						.map((item, index) => {
							return (
								index > 0 && (
									<ClayDropDown.Item
										active={step === index}
										key={index}
										onClick={() => {
											if (index === step) {
												return;
											}

											Liferay.fire('journal:goto', {
												step: index,
											});

											if (index < step) {
												let i = step;
												while (i > index) {
													i--;
													handleUndo(i);
												}
											}
											else {
												let i = step;
												while (i < index) {
													i++;
													handleRedo(i);
												}
											}

											setActive(false);
										}}
										symbolLeft={
											step === index ? 'check' : ''
										}
									>
										<span className="ml-4 px-1">
											{METADATA_FIELD_NAME_HISTORY[
												item.name
											] || item.name}
										</span>
									</ClayDropDown.Item>
								)
							);
						})
						.reverse()}

					<ClayDropDown.Divider />

					<ClayDropDown.Item
						active={step === 0}
						onClick={() => {
							if (step === 0) {
								return;
							}

							Liferay.fire('journal:goto', {
								step: 0,
							});

							let i = step;
							while (i > 0) {
								i--;
								handleUndo(i);
							}

							setActive(false);
						}}
					>
						<span className="ml-4 px-1">
							{Liferay.Language.get('undo-all')}
						</span>
					</ClayDropDown.Item>
				</ClayDropDown.ItemList>
			</ClayDropDown>
		</div>
	);
}
