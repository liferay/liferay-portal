/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext} from 'react';

import {Context} from '../../Context';
import {Individuals} from '../../types/global';
import Filter from '../Filter';

const individualFilterLang = {
	[Individuals.AllIndividuals]: Liferay.Language.get('all-individuals'),
	[Individuals.AnonymousIndividuals]: Liferay.Language.get(
		'anonymous-individuals'
	),
	[Individuals.KnownIndividuals]: Liferay.Language.get('known-individuals'),
};

const AllIndividualsFilter = () => {
	const {changeIndividualFilter, filters} = useContext(Context);

	return (
		<Filter
			active={filters.individual}
			className="mr-3"
			filterByValue="individuals"
			icon="users"
			items={[
				{
					label: Liferay.Language.get('all-individuals'),
					value: Individuals.AllIndividuals,
				},
				{
					label: Liferay.Language.get('anonymous-individuals'),
					value: Individuals.AnonymousIndividuals,
				},
				{
					label: Liferay.Language.get('known-individuals'),
					value: Individuals.KnownIndividuals,
				},
			]}
			onSelectItem={(item) => changeIndividualFilter(item.value)}
			triggerLabel={individualFilterLang[filters.individual]}
		/>
	);
};

export {AllIndividualsFilter};
