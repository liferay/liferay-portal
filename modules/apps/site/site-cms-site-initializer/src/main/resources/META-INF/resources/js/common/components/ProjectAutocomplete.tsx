/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Autocomplete from '@clayui/autocomplete';
import React, {useMemo, useRef, useState} from 'react';

import {CMPProject} from '../services/ProjectLinkService';

type ProjectAutocompleteProps = {
	ariaLabel: string;
	onSelect: (project: CMPProject) => void;
	projects: CMPProject[];
};

/**
 * Selects a CMP project from a list that narrows as the user types. The whole
 * list is already in memory, so the filtering is done here instead of with a
 * request per keystroke. Shared by the Projects panel and the bulk add assets
 * to project modal.
 */
export default function ProjectAutocomplete({
	ariaLabel,
	onSelect,
	projects,
}: ProjectAutocompleteProps) {
	const [active, setActive] = useState(false);
	const [value, setValue] = useState('');

	// Differentiates selecting a project versus typing in onChange, so the
	// input can be cleared after a selection. Clay writes the selected
	// title into the input by default.

	const selectingRef = useRef(false);

	const filteredProjects = useMemo(() => {
		const query = value.trim().toLowerCase();

		if (!query) {
			return projects;
		}

		return projects.filter(({title}) =>
			title.toLowerCase().includes(query)
		);
	}, [projects, value]);

	return (
		<Autocomplete
			active={active}
			aria-label={ariaLabel}
			filterKey="title"
			items={filteredProjects}
			menuTrigger="focus"
			messages={{
				loading: Liferay.Language.get('loading'),
				notFound: Liferay.Language.get('no-results-found'),
			}}
			onActiveChange={setActive}
			onChange={(nextValue) => {
				if (selectingRef.current) {
					selectingRef.current = false;

					setValue('');

					return;
				}

				setValue(nextValue);
			}}
			onClick={() => setActive(true)}
			onItemsChange={() => {}}
			placeholder={Liferay.Language.get('search-or-select-a-project')}
			value={value}
		>
			{(project) => (
				<Autocomplete.Item
					aria-label={project.title}
					key={project.id}
					onClick={() => {
						selectingRef.current = true;

						onSelect(project);
					}}
					textValue={project.title}
				>
					{project.title}
				</Autocomplete.Item>
			)}
		</Autocomplete>
	);
}
