/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {parseVisualizationModes} from './getVisualizationModes';
import {IDataSet, IVisualizationModeClientExtension} from './types';

export interface ISection {
	label: string;
	name: string;
	required: boolean;
}

export interface ISectionFields {
	[sectionName: string]: string;
}

function parseJSON(json?: string) {
	if (!json) {
		return null;
	}

	try {
		return JSON.parse(json);
	}
	catch (error) {
		return null;
	}
}

export function getSections(json?: string): ISection[] {
	const sections = parseJSON(json);

	if (!Array.isArray(sections)) {
		return [];
	}

	return sections
		.filter(
			(section) =>
				section && typeof section.name === 'string' && section.name
		)
		.map((section) => ({
			label: section.label || section.name,
			name: section.name,
			required: !!section.required,
		}));
}

export function getClientExtensionSections(
	visualizationModeClientExtensions: IVisualizationModeClientExtension[],
	visualizationModeName: string
): ISection[] {
	return getSections(
		visualizationModeClientExtensions.find(
			({externalReferenceCode}) =>
				externalReferenceCode === visualizationModeName
		)?.sections
	);
}

export function getSectionFields(
	dataSet: IDataSet,
	visualizationModeName: string
): ISectionFields {
	const visualizationMode = parseVisualizationModes(
		dataSet.visualizationModes
	).find(({name}) => name === visualizationModeName);

	const sectionFields = visualizationMode?.configuration?.sectionFields;

	return sectionFields && typeof sectionFields === 'object'
		? (sectionFields as ISectionFields)
		: {};
}

/**
 * Returns the assigned sections the client extension no longer declares, which
 * happens when it changes its sections after fields were assigned.
 */
export function getStaleSections(
	sectionFields: ISectionFields,
	sections: ISection[]
): string[] {
	return Object.keys(sectionFields).filter(
		(name) => !sections.some((section) => section.name === name)
	);
}

export function hasMissingRequiredSections(
	sectionFields: ISectionFields,
	sections: ISection[]
) {
	return sections.some(
		({name, required}) => required && !sectionFields[name]
	);
}

export function serializeSectionFields({
	dataSet,
	sectionFields,
	visualizationModeName,
}: {
	dataSet: IDataSet;
	sectionFields: ISectionFields;
	visualizationModeName: string;
}) {
	const visualizationModes = parseVisualizationModes(
		dataSet.visualizationModes
	);

	const index = visualizationModes.findIndex(
		({name}) => name === visualizationModeName
	);

	const visualizationMode =
		index === -1
			? {enabled: false, name: visualizationModeName}
			: visualizationModes[index];

	const updatedVisualizationMode = {
		...visualizationMode,
		configuration: {...visualizationMode.configuration, sectionFields},
	};

	if (index === -1) {
		visualizationModes.push(updatedVisualizationMode);
	}
	else {
		visualizationModes[index] = updatedVisualizationMode;
	}

	return JSON.stringify(visualizationModes);
}
