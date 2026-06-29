/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useMemo, useState} from 'react';

import ApiHelper from '../../../common/services/ApiHelper';
import {Space as Project} from '../../../common/types/Space';
import ScopeMultiSelect, {ScopeItem as ProjectItem} from './ScopeMultiSelect';

export default function CategorizationProjects({
	checkboxText,
	disabled = false,
	projects,
	setProjectChange,
	setProjectInputError,
	setSelectedProjects,
}: {
	checkboxText: string;
	disabled?: boolean;
	projects?: AssetLibraryType[];
	setProjectChange?: (value: boolean) => void;
	setProjectInputError: (value: string) => void;
	setSelectedProjects: (value: string[]) => void;
}) {
	const [sourceItems, setSourceItems] = useState<ProjectItem[]>([]);

	useEffect(() => {
		const loadProjects = async () => {
			const response = await ApiHelper.getAll<Project>({
				filter: "type eq 'Project'",
				url: '/o/headless-asset-library/v1.0/asset-libraries',
			});

			setSourceItems(
				response.map(
					(item): ProjectItem => ({
						displayType: item.settings?.logoColor,
						label: item.name,
						scopeKey: item.assetLibraryKey,
						value: item.id,
					})
				)
			);
		};

		loadProjects();
	}, []);

	const preselectedItems = useMemo(() => {
		if (projects?.some((project) => project.id === -1)) {
			return [];
		}

		const scopeKeys = projects?.map((project) => project.scopeKey);

		return sourceItems.filter((item) => scopeKeys?.includes(item.scopeKey));
	}, [projects, sourceItems]);

	return (
		<ScopeMultiSelect<ProjectItem>
			disabled={disabled}
			labels={{
				allItemsValue: Liferay.Language.get('all-projects'),
				ariaLabel: Liferay.Language.get('project-selector'),
				checkbox:
					checkboxText === 'tag'
						? Liferay.Language.get(
								'make-this-tag-available-in-all-projects'
							)
						: Liferay.Language.get(
								'make-this-vocabulary-available-in-all-projects'
							),
				field: Liferay.Language.get('project'),
			}}
			onChange={setProjectChange}
			onError={setProjectInputError}
			onSelectionChange={setSelectedProjects}
			preselectedItems={preselectedItems}
			sourceItems={sourceItems}
		/>
	);
}
