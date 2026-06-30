/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import React, {useContext, useEffect, useState} from 'react';

import SpaceService from '../../../common/services/SpaceService';
import {InventoryContext, initialLanguage} from '../InventoryContext';
import PickerTrigger from './PickerTrigger';

type AvailableLocales = Exclude<
	Liferay.Language.Locale,
	'zh_Hans_CN' | 'zh_Hant_TW' | 'zh_TW'
>;

export const localizations: Record<AvailableLocales, string> = {
	ar_SA: Liferay.Language.get('language.ar'),
	ca_ES: Liferay.Language.get('language.ca'),
	de_DE: Liferay.Language.get('language.de'),
	en_US: Liferay.Language.get('language.en'),
	es_ES: Liferay.Language.get('language.es'),
	fi_FI: Liferay.Language.get('language.fi'),
	fr_FR: Liferay.Language.get('language.fr'),
	hu_HU: Liferay.Language.get('language.hu'),
	ja_JP: Liferay.Language.get('language.ja'),
	nl_NL: Liferay.Language.get('language.nl'),
	pt_BR: Liferay.Language.get('language.pt_BR'),
	sv_SE: Liferay.Language.get('language.sv'),
	zh_CN: Liferay.Language.get('language.zh_CN'),
};

/**
 * Must update the code below to iterate through the collection
 * of languages used by the assets. Expect to be represented as
 * an array.
 */
const availableLanguages = Object.entries(localizations).map(
	([locale, translation]) => ({
		label: translation,
		value: locale,
	})
);

const initialLanguages = [initialLanguage, ...availableLanguages];

const LanguagesDropdown: React.FC<React.HTMLAttributes<HTMLElement>> = ({
	className,
}) => {
	const {
		changeLanguage,
		filters: {language, space},
	} = useContext(InventoryContext);

	const [languages, setLanguages] = useState(initialLanguages);

	useEffect(() => {
		if (space.value === 'all') {
			setLanguages(initialLanguages);

			return;
		}

		const fetchSpaceLanguages = async () => {
			try {
				const {settings} = await SpaceService.getSpace(
					space.externalReferenceCode as string
				);

				if (settings?.availableLanguageIds) {
					const availableLanguageIds =
						settings.availableLanguageIds.map((languageId) =>
							languageId.replace('-', '_')
						);

					const filteredLanguages = availableLanguages.filter(
						({value}) => availableLanguageIds.includes(value)
					);

					setLanguages([initialLanguage, ...filteredLanguages]);

					if (
						language.value !== 'all' &&
						!availableLanguageIds.includes(language.value)
					) {
						changeLanguage(initialLanguage);
					}
				}
				else {
					setLanguages(initialLanguages);
				}
			}
			catch (error) {
				console.error(error);

				setLanguages(initialLanguages);
			}
		};

		fetchSpaceLanguages();
	}, [
		changeLanguage,
		language.value,
		space.value,
		space.externalReferenceCode,
	]);

	return (
		<Picker
			aria-label={Liferay.Language.get('filter-by-languages')}
			as={PickerTrigger}
			filterKey="label"
			items={languages}
			messages={{
				noResultsFound: Liferay.Language.get('no-results-were-found'),
				searchPlaceholder: Liferay.Language.get('search'),
			}}
			onSelectionChange={(key) => {
				const selectedLanguage = languages.find(
					({value}) => value === String(key)
				);

				if (selectedLanguage) {
					changeLanguage(selectedLanguage);
				}
			}}
			searchable
			selectedKey={language.value}
			triggerClassName={className}
			triggerIcon="automatic-translate"
		>
			{(item: {label: string; value: string}) => (
				<Option key={item.value}>{item.label}</Option>
			)}
		</Picker>
	);
};

export {LanguagesDropdown};
