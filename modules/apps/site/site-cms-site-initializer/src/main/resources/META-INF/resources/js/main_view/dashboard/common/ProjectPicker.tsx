/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import React, {useEffect, useState} from 'react';

import ProjectLinkService from '../../../common/services/ProjectLinkService';
import PickerTrigger from './PickerTrigger';

export type ProjectOption = {
	label: string;
	value: string;
};

export const initialProject: ProjectOption = {
	label: Liferay.Language.get('all-projects'),
	value: 'all',
};

interface IProjectPicker extends React.HTMLAttributes<HTMLElement> {
	className?: string;
	cmpProjectObjectDefinitionId?: number;
	onSelectProject: (project: ProjectOption) => void;
	selectedProject: ProjectOption;
}

const ProjectPicker: React.FC<IProjectPicker> = ({
	className,
	cmpProjectObjectDefinitionId,
	onSelectProject,
	selectedProject,
}) => {
	const [projects, setProjects] = useState<ProjectOption[]>([initialProject]);

	useEffect(() => {
		const controller = new AbortController();

		ProjectLinkService.getProjects({
			cmpProjectObjectDefinitionId,
			signal: controller.signal,
		}).then(({data}) => {
			if (!data) {
				return;
			}

			setProjects([
				initialProject,
				...data.map(({id, title}) => ({
					label: title,
					value: String(id),
				})),
			]);
		});

		return () => controller.abort();
	}, [cmpProjectObjectDefinitionId]);

	return (
		<Picker
			aria-label={Liferay.Language.get('filter-by-projects')}
			as={PickerTrigger}
			filterKey="label"
			items={projects}
			messages={{
				noResultsFound: Liferay.Language.get('no-results-were-found'),
				searchPlaceholder: Liferay.Language.get('search'),
			}}
			onSelectionChange={(key) => {
				const project = projects.find(
					({value}) => value === String(key)
				);

				if (project) {
					onSelectProject(project);
				}
			}}
			searchable
			selectedKey={selectedProject.value}
			triggerClassName={className}
			triggerIcon="archive"
		>
			{(item: ProjectOption) => (
				<Option key={item.value}>{item.label}</Option>
			)}
		</Picker>
	);
};

export {ProjectPicker};
