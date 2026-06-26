/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import {SearchForm} from '@liferay/layout-js-components-web';
import React, {useState} from 'react';

import {AudiencesCriteriaType} from '../types';

interface IProps {
	audiencesCriteriaTypes: AudiencesCriteriaType[];
}

export default function AttributesSidebar({audiencesCriteriaTypes}: IProps) {
	const [selectedIndex, setSelectedIndex] = useState(0);
	const [query, setQuery] = useState('');

	const normalizedQuery = query.trim().toLowerCase();

	const audiencesCriterias =
		audiencesCriteriaTypes[selectedIndex]?.audiencesCriterias?.filter(
			(audiencesCriteria) =>
				audiencesCriteria.label.toLowerCase().includes(normalizedQuery)
		) ?? [];

	return (
		<div className="d-flex flex-column flex-grow-0 h-100">
			<p className="h4 my-3">
				{Liferay.Language.get('attributes-types')}
			</p>

			<ClayForm.Group>
				<ClaySelectWithOption
					aria-label={Liferay.Language.get('attributes-types')}
					className="bg-white font-weight-semi-bold text-4"
					onChange={(event) => {
						setSelectedIndex(Number(event.target.value));
						setQuery('');
					}}
					options={audiencesCriteriaTypes.map(
						(audiencesCriteriaType, index) => ({
							label: audiencesCriteriaType.label,
							value: index,
						})
					)}
					value={selectedIndex}
				/>
			</ClayForm.Group>

			<SearchForm
				className="mb-3"
				label={Liferay.Language.get('search-attributes')}
				onChange={setQuery}
			/>

			{audiencesCriterias.length ? (
				<div className="overflow-auto">
					<ClayList>
						{audiencesCriterias.map((audiencesCriteria) => (
							<ClayList.Item
								className="align-items-center border-0"
								flex
								key={audiencesCriteria.key}
							>
								<ClayList.ItemField>
									<ClayIcon symbol={audiencesCriteria.icon} />
								</ClayList.ItemField>

								<ClayList.ItemField className="text-3" expand>
									{audiencesCriteria.label}
								</ClayList.ItemField>
							</ClayList.Item>
						))}
					</ClayList>
				</div>
			) : (
				<ClayEmptyState
					description={Liferay.Language.get(
						'no-attributes-were-found'
					)}
					small
				/>
			)}
		</div>
	);
}
