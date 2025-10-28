/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import {sub} from 'frontend-js-web';
import React, {Ref, useEffect, useMemo, useRef, useState} from 'react';

import useId from '../hooks/useId';
import {Locale, Translations} from './TranslationAdminContent';
import TranslationAdminItem from './TranslationAdminItem';
import TranslationAdminModal from './TranslationAdminModal';

const DISPLAY_TYPE = {
	DEFAULT: 'DEFAULT',
	HORIZONTAL: 'HORIZONTAL',
} as const;

type DisplayType = (typeof DISPLAY_TYPE)[keyof typeof DISPLAY_TYPE];

interface IProps extends Translations {
	adminMode?: boolean;
	displayType?: DisplayType;
	onActiveLanguageIdsChange?: (
		languageIds: Liferay.Language.Locale[]
	) => void;
	onSelectedLanguageIdChange?: (languageId: Liferay.Language.Locale) => void;
	onSelectorActiveChange?: () => void;
	selectedLanguageId: Liferay.Language.Locale;
	showOnlyFlags?: boolean;
	small?: boolean;
	translationProgress?: TranslationProgress | null;
}

export interface TranslationProgress {
	totalItems: number;
	translatedItems: Record<string, number>;
}

interface TriggerButtonProps {
	displayType: DisplayType;
	selectedItem?: Locale;
	small: boolean;
}

// These variables are defined here, out of the component, to avoid
// unexpected re-renders

const noop = () => {};

const TriggerButton = React.forwardRef(
	(
		{displayType, selectedItem, small, ...props}: TriggerButtonProps,
		ref: Ref<HTMLButtonElement>
	) => {
		const ariaLabelButton = selectedItem?.displayName
			? sub(
					Liferay.Language.get(
						'select-a-language.-current-language-x'
					),
					selectedItem?.displayName
				)
			: Liferay.Language.get('select-a-language');

		return displayType === DISPLAY_TYPE.HORIZONTAL ? (
			<ClayButton
				{...props}
				aria-label={ariaLabelButton}
				className="btn-block form-control-select"
				displayType="secondary"
				ref={ref}
				size="sm"
			>
				<span className="inline-item-before">
					<ClayIcon symbol={selectedItem?.symbol ?? ''} />
				</span>

				<span aria-hidden="true">{selectedItem?.label}</span>
			</ClayButton>
		) : (
			<ClayButton
				{...props}
				aria-label={ariaLabelButton}
				className="btn-block"
				displayType="secondary"
				monospaced
				ref={ref}
				size={small ? 'sm' : undefined}
				title={Liferay.Language.get('select-a-language')}
			>
				<span className="inline-item">
					<ClayIcon symbol={selectedItem?.symbol ?? ''} />
				</span>

				<span aria-hidden="true" className="btn-section">
					{selectedItem?.label}
				</span>
			</ClayButton>
		);
	}
);

export default function TranslationAdminSelector({
	activeLanguageIds: initialActiveLanguageIds = [],
	adminMode,
	ariaLabels = {
		manageTranslations: Liferay.Language.get('manage-translations'),
	},
	availableLocales = [],
	defaultLanguageId,
	displayType = DISPLAY_TYPE.DEFAULT,
	onActiveLanguageIdsChange = noop,
	onSelectedLanguageIdChange = noop,
	onSelectorActiveChange = noop,
	selectedLanguageId: initialSelectedLanguageId,
	showOnlyFlags,
	small = false,
	translationProgress = null,
	translations = null,
}: IProps) {
	const [activeLanguageIds, setActiveLanguageIds] = useState<
		Liferay.Language.Locale[]
	>(initialActiveLanguageIds);
	const [selectedLanguageId, setSelectedLanguageId] =
		useState<Liferay.Language.Locale>(initialSelectedLanguageId);
	const [selectorDropdownActive, setSelectorDropdownActive] = useState(false);
	const selectorId = useId();
	const [translationModalVisible, setTranslationModalVisible] =
		useState(false);
	const triggerRef = useRef<HTMLButtonElement | null>(null);

	const handleCloseTranslationModal = (
		activeLanguageIds: Liferay.Language.Locale[]
	) => {
		setActiveLanguageIds(activeLanguageIds);

		if (!activeLanguageIds.includes(selectedLanguageId)) {
			setSelectedLanguageId(defaultLanguageId);
		}

		setTranslationModalVisible(false);
	};

	const activeLocales = useMemo(
		() =>
			availableLocales.filter((availableLocale) =>
				activeLanguageIds.includes(availableLocale.id)
			),
		[availableLocales, activeLanguageIds]
	);

	const selectedLocale = useMemo(() => {
		const id = selectedLanguageId ?? defaultLanguageId;

		return availableLocales.find(
			(availableLocale) => availableLocale.id === id
		);
	}, [availableLocales, defaultLanguageId, selectedLanguageId]);

	useEffect(() => {
		onActiveLanguageIdsChange(activeLanguageIds);
	}, [activeLanguageIds, onActiveLanguageIdsChange]);

	useEffect(() => {
		onSelectedLanguageIdChange(selectedLanguageId);
	}, [selectedLanguageId, onSelectedLanguageIdChange]);

	useEffect(() => {
		setActiveLanguageIds(initialActiveLanguageIds);
	}, [initialActiveLanguageIds]);

	useEffect(() => {
		setSelectedLanguageId(initialSelectedLanguageId);
	}, [initialSelectedLanguageId]);

	useEffect(() => {
		const handleUpdateSelectedLanguage = (event: any) => {
			const selectedLanguageId = event.item.getAttribute('data-value');
			setSelectedLanguageId(selectedLanguageId);
		};
		Liferay.on(
			'journal:updateSelectedLanguage',
			handleUpdateSelectedLanguage
		);

		return () => {
			Liferay.detach(
				'journal:updateSelectedLanguage',
				handleUpdateSelectedLanguage as () => void
			);
		};
	}, [initialSelectedLanguageId]);

	if (!adminMode) {
		return (
			<Picker
				active={selectorDropdownActive}
				as={TriggerButton}
				displayType={displayType}
				id={selectorId}
				items={activeLocales}
				messages={{
					itemDescribedby: Liferay.Language.get(
						'you-are-currently-on-a-text-element,-inside-of-a-list-box'
					),
					itemSelected: Liferay.Language.get('x-selected'),
					scrollToBottomAriaLabel:
						Liferay.Language.get('scroll-to-bottom'),
					scrollToTopAriaLabel: Liferay.Language.get('scroll-to-top'),
				}}
				onActiveChange={(active: any) => {
					if (active) {
						onSelectorActiveChange();
					}

					setSelectorDropdownActive(active);
				}}
				onSelectionChange={(id: React.Key) => {
					setSelectedLanguageId(id as Liferay.Language.Locale);

					Liferay.fire('journal:localeChanged', {
						item: document.querySelector(
							`[data-languageid="${id}"][data-value="${id}"]`
						),
					});
				}}
				selectedItem={activeLocales.find(
					(locale) => locale.id === selectedLanguageId
				)}
				selectedKey={selectedLanguageId}
			>
				{(item) => (
					<Option key={item.id} textValue={item.label}>
						<TranslationAdminItem
							defaultLanguageId={defaultLanguageId}
							item={item}
							labels={ariaLabels}
							localeValue={
								translations ? translations[item.id] : null
							}
							showOnlyFlags={showOnlyFlags}
							translationProgress={translationProgress}
						/>
					</Option>
				)}
			</Picker>
		);
	}

	return (
		<>
			<TranslationAdminModal
				activeLanguageIds={activeLanguageIds}
				ariaLabels={ariaLabels}
				availableLocales={availableLocales}
				defaultLanguageId={defaultLanguageId}
				onClose={handleCloseTranslationModal}
				translations={translations}
				visible={translationModalVisible}
			/>

			<ClayDropDown
				active={selectorDropdownActive}
				hasLeftSymbols
				onActiveChange={(active: boolean) => {
					if (active) {
						onSelectorActiveChange();
					}

					setSelectorDropdownActive(active);
				}}
				trigger={
					<TriggerButton
						displayType={displayType}
						ref={(node) => {
							triggerRef.current = node;
						}}
						selectedItem={selectedLocale}
						small={small}
					/>
				}
			>
				<ClayDropDown.ItemList>
					{activeLocales.map((activeLocale) => {
						const active = activeLocale.id === selectedLanguageId;

						return (
							<ClayDropDown.Item
								active={active}
								key={activeLocale.id}
								onClick={() => {
									setSelectedLanguageId(activeLocale.id);
									setSelectorDropdownActive(false);

									triggerRef.current?.focus();
								}}
								symbolLeft={active ? 'check-small' : undefined}
							>
								<TranslationAdminItem
									defaultLanguageId={defaultLanguageId}
									item={activeLocale}
									labels={ariaLabels}
									localeValue={
										translations
											? translations[activeLocale.id]
											: null
									}
									showOnlyFlags={showOnlyFlags}
									translationProgress={translationProgress}
								/>
							</ClayDropDown.Item>
						);
					})}

					{adminMode && (
						<>
							<ClayDropDown.Divider />
							<ClayDropDown.Item
								data-testid="translation-modal-trigger"
								onClick={() => setTranslationModalVisible(true)}
							>
								<ClayLayout.ContentRow containerElement="span">
									<ClayLayout.ContentCol
										containerElement="span"
										expand
									>
										<ClayLayout.ContentSection>
											<ClayIcon
												className="inline-item inline-item-before"
												symbol="automatic-translate"
											/>

											{ariaLabels.manageTranslations}
										</ClayLayout.ContentSection>
									</ClayLayout.ContentCol>
								</ClayLayout.ContentRow>
							</ClayDropDown.Item>
						</>
					)}
				</ClayDropDown.ItemList>
			</ClayDropDown>
		</>
	);
}
