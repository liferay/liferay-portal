/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Autocomplete from '@clayui/autocomplete';
import React, {useMemo, useState} from 'react';

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
			onChange={setValue}
			onItemsChange={() => {}}
			placeholder={Liferay.Language.get('search-or-select-a-project')}
			value={value}
		>
			{(project) => (
				<Autocomplete.Item
					aria-label={project.title}
					key={project.id}
					onClick={(event) => {

						// Clay would otherwise replace the input with the
						// project title. The field is a picker rather than a
						// text field, so it is cleared for the next selection.

						event.preventDefault();

						setActive(false);
						setValue('');

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
