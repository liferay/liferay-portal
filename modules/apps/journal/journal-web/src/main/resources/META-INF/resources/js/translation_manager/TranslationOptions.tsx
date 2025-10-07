/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {MetadataFieldsManager} from './MetadataFieldsManager';
import {TranslationManagerProps} from './Types';
import useTranslationProgress from './useTranslationProgress';

export default function TranslationOptions({
	defaultLanguageId: initialDefaultLanguageId,
	fields: initialFields,
	locales,
	namespace,
	selectedLanguageId: initialSelectedLanguageId,
}: TranslationManagerProps) {
	const {
		observer: resetTranslationObserver,
		onOpenChange: onOpenChangeResetTranslation,
		open: openResetTranslation,
	} = useModal();

	const {
		observer: markTranslatedObserver,
		onOpenChange: onOpenChangeMarkAsTranslated,
		open: openMarkTranslated,
	} = useModal();

	const [dropdownActive, setDropdownActive] = useState(false);

	const {
		defaultLanguageId,
		selectedLanguageId,
		translationProgress,
		updateTranslations,
	} = useTranslationProgress({
		defaultLanguageId: initialDefaultLanguageId,
		fields: initialFields,
		locales,
		namespace,
		selectedLanguageId: initialSelectedLanguageId,
	});

	const markAsTranslatedHandler = () => {
		const metaDataValues = MetadataFieldsManager.getAllMetadataValues(
			initialFields,
			selectedLanguageId
		);

		Liferay.fire('inputLocalized:markAsTranslated', {selectedLanguageId});

		MetadataFieldsManager.restoreAllMetadataValues(
			selectedLanguageId,
			metaDataValues,
			defaultLanguageId
		);

		Liferay.fire('inputLocalized:updateTranslationStatus');

		Liferay.fire('journal:storeState', {
			fieldName: Liferay.Language.get('mark-as-translated'),
		});
	};

	const resetButtonHandler = () => {
		MetadataFieldsManager.resetAllMetadataValues(
			initialFields,
			selectedLanguageId
		);

		Liferay.fire('inputLocalized:resetTranslations', {
			defaultLanguageId,
		});

		Liferay.fire('inputLocalized:localeChanged', {
			item: document.querySelector<HTMLInputElement>(
				`[data-languageid="${selectedLanguageId}"][data-value="${selectedLanguageId}"]`
			),
		});

		Liferay.fire('inputLocalized:updateTranslationStatus');

		Liferay.fire('journal:storeState', {
			fieldName: Liferay.Language.get('reset-translation'),
		});
	};

	const disabledResetButton =
		!translationProgress?.translatedItems[selectedLanguageId] ||
		translationProgress?.translatedItems[selectedLanguageId] === 0;

	const disabledMarkTranslatedButton =
		translationProgress?.translatedItems[selectedLanguageId] ===
		translationProgress?.totalItems;

	return (
		<>
			{selectedLanguageId !== defaultLanguageId && (
				<ClayDropDown
					active={dropdownActive}
					onActiveChange={(active: boolean) => {
						if (active) {
							updateTranslations();
						}

						setDropdownActive(active);
					}}
					trigger={
						<ClayButton
							aria-label={Liferay.Language.get(
								'translation-options'
							)}
							className="px-2"
							displayType="secondary"
							size="sm"
							title={Liferay.Language.get('translation-options')}
							type="button"
						>
							<ClayIcon symbol="ellipsis-v" />
						</ClayButton>
					}
				>
					<ClayDropDown.ItemList>
						<ClayDropDown.Item>
							<ClayButton
								className="font-weight-normal text-secondary"
								disabled={!!disabledMarkTranslatedButton}
								displayType="unstyled"
								onClick={() =>
									onOpenChangeMarkAsTranslated(true)
								}
								size="sm"
							>
								<ClayIcon symbol="check-circle" />

								<span className="c-ml-3">
									{Liferay.Language.get('mark-as-translated')}
								</span>
							</ClayButton>
						</ClayDropDown.Item>

						<ClayDropDown.Item>
							<ClayButton
								className="font-weight-normal text-secondary"
								disabled={!!disabledResetButton}
								displayType="unstyled"
								onClick={() =>
									onOpenChangeResetTranslation(true)
								}
								size="sm"
							>
								<ClayIcon symbol="trash" />

								<span className="c-ml-3">
									{Liferay.Language.get('reset-translation')}
								</span>
							</ClayButton>
						</ClayDropDown.Item>
					</ClayDropDown.ItemList>
				</ClayDropDown>
			)}

			{openResetTranslation && (
				<ClayModal observer={resetTranslationObserver} status="danger">
					<ClayModal.Header>
						{sub(
							Liferay.Language.get('delete-x-translation'),
							selectedLanguageId
						)}
					</ClayModal.Header>

					<ClayModal.Body>
						<p
							dangerouslySetInnerHTML={{
								__html: sub(
									Liferay.Language.get(
										'x-translation-will-be-deleted-and-content-fields-will-be-set-to-default-language'
									),
									`<strong>${selectedLanguageId}</strong>`
								),
							}}
						/>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									displayType="secondary"
									onClick={() =>
										onOpenChangeResetTranslation(false)
									}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>

								<ClayButton
									displayType="danger"
									onClick={() => {
										onOpenChangeResetTranslation(false);
										resetButtonHandler();
									}}
								>
									{Liferay.Language.get('delete')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</ClayModal>
			)}
			{openMarkTranslated && (
				<ClayModal observer={markTranslatedObserver} status="info">
					<ClayModal.Header>
						{sub(
							Liferay.Language.get('mark-x-as-translated'),
							selectedLanguageId
						)}
					</ClayModal.Header>

					<ClayModal.Body>
						<p
							dangerouslySetInnerHTML={{
								__html: sub(
									Liferay.Language.get(
										'mark-as-translated-for-x-language'
									),
									`<strong>${selectedLanguageId}</strong>`
								),
							}}
						/>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									displayType="secondary"
									onClick={() =>
										onOpenChangeMarkAsTranslated(false)
									}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>

								<ClayButton
									displayType="info"
									onClick={() => {
										onOpenChangeMarkAsTranslated(false);
										markAsTranslatedHandler();
									}}
								>
									{Liferay.Language.get('mark-as-translated')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</ClayModal>
			)}
		</>
	);
}
