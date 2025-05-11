/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext} from 'react';

import {ViewDashboardContext} from '../ViewDashboardContext';
import {FilterDropdown} from './FilterDropdown';

const languages = [
	{
		label: Liferay.Language.get('all-languages'),
		value: 'all',
	},
	{
		label: Liferay.Language.get('language-02'),
		value: 'language02',
	},
];

const LanguagesDropdown: React.FC<React.HTMLAttributes<HTMLElement>> = ({
	className,
}) => {
	const {
		changeLanguageDropdown,
		filters: {languageId},
	} = useContext(ViewDashboardContext);

	return (
		<FilterDropdown
			active={languageId}
			borderless={false}
			className={className}
			filterByValue="languages"
			icon="automatic-translate"
			items={languages}
			onSelectItem={(language) => changeLanguageDropdown(language.value)}
			triggerLabel={
				languages.find(({value}) => value === languageId)?.label ?? ''
			}
		/>
	);
};

export {LanguagesDropdown};
