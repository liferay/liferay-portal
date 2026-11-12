/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DEFAULT_VISUALIZATION_MODES} from './constants';
import {
	IDataSet,
	IDataSetVisualizationMode,
	IVisualizationModeClientExtension,
} from './types';

interface IPersistedVisualizationMode {
	configuration?: {[key: string]: unknown};
	enabled: boolean;
	name: string;
	[key: string]: unknown;
}

function parseVisualizationModes(
	visualizationModes?: string
): IPersistedVisualizationMode[] {
	if (!visualizationModes) {
		return [];
	}

	try {
		const persistedVisualizationModes = JSON.parse(visualizationModes);

		return Array.isArray(persistedVisualizationModes)
			? persistedVisualizationModes
			: [];
	}
	catch (error) {
		return [];
	}
}

/**
 * Returns every visualization mode available to the data set, in the order
 * the data set shows them. Visualization modes the data set does not persist
 * yet go last: the default ones enabled and the client extensions disabled.
 */
export default function getVisualizationModes({
	dataSet,
	visualizationModeClientExtensions,
}: {
	dataSet: IDataSet;
	visualizationModeClientExtensions: IVisualizationModeClientExtension[];
}): IDataSetVisualizationMode[] {
	const availableVisualizationModes = new Map<
		string,
		IDataSetVisualizationMode
	>();

	DEFAULT_VISUALIZATION_MODES.forEach(({label, mode, thumbnail}) =>
		availableVisualizationModes.set(mode, {
			clientExtension: false,
			enabled: true,
			label,
			name: mode,
			thumbnail,
		})
	);

	visualizationModeClientExtensions.forEach(
		({externalReferenceCode, icon, name}) =>
			availableVisualizationModes.set(externalReferenceCode, {
				clientExtension: true,
				enabled: false,
				label: name,
				name: externalReferenceCode,
				thumbnail: icon || 'view',
			})
	);

	const visualizationModes: IDataSetVisualizationMode[] = [];

	parseVisualizationModes(dataSet.visualizationModes).forEach(
		({enabled, name}) => {
			const visualizationMode = availableVisualizationModes.get(name);

			if (visualizationMode) {
				visualizationModes.push({...visualizationMode, enabled});

				availableVisualizationModes.delete(name);
			}
		}
	);

	return [...visualizationModes, ...availableVisualizationModes.values()];
}

/**
 * Returns the names of the default visualization modes with at least one field
 * assigned, given a data set fetched with their relationships as nested fields.
 */
export function getMappedVisualizationModeNames(dataSet: {
	[key: string]: any;
}): string[] {
	return DEFAULT_VISUALIZATION_MODES.filter(
		({relationship}) => !!dataSet[relationship]?.length
	).map(({mode}) => mode);
}

/**
 * Returns whether a data set fetched with its relationships as nested fields
 * renders at least one visualization mode, as the runtime does: persisted
 * modes are enabled unless stated otherwise, default ones need fields, and
 * the default ones with fields stand in when none of them is enabled.
 */
export function hasDisplayedVisualizationMode(dataSet: {
	[key: string]: any;
}): boolean {
	const mappedVisualizationModeNames =
		getMappedVisualizationModeNames(dataSet);

	const defaultVisualizationModeNames = DEFAULT_VISUALIZATION_MODES.map(
		({mode}) => mode
	);

	const persistedVisualizationModes = parseVisualizationModes(
		dataSet.visualizationModes
	).filter(({name}) => !!name);

	const enabledVisualizationModeNames = [
		...persistedVisualizationModes
			.filter(({enabled}) => enabled)
			.map(({name}) => name),
		...defaultVisualizationModeNames.filter(
			(name) =>
				!persistedVisualizationModes.some(
					(visualizationMode) => visualizationMode.name === name
				)
		),
	];

	const defaultVisualizationModeEnabled = enabledVisualizationModeNames.some(
		(name) => defaultVisualizationModeNames.includes(name)
	);

	return (
		enabledVisualizationModeNames.some(
			(name) =>
				!defaultVisualizationModeNames.includes(name) ||
				mappedVisualizationModeNames.includes(name)
		) ||
		(!defaultVisualizationModeEnabled &&
			!!mappedVisualizationModeNames.length)
	);
}

export function isVisualizationModeDisplayed({
	mappedVisualizationModeNames,
	visualizationMode,
}: {
	mappedVisualizationModeNames: string[];
	visualizationMode: IDataSetVisualizationMode;
}) {
	return (
		visualizationMode.enabled &&
		(visualizationMode.clientExtension ||
			mappedVisualizationModeNames.includes(visualizationMode.name))
	);
}

/**
 * Serializes the visualization modes over the ones the data set persists, so
 * their configuration and the visualization modes of client extensions that
 * are not deployed now are kept.
 */
export function serializeVisualizationModes({
	dataSet,
	visualizationModes,
}: {
	dataSet: IDataSet;
	visualizationModes: IDataSetVisualizationMode[];
}) {
	const persistedVisualizationModes = parseVisualizationModes(
		dataSet.visualizationModes
	);

	const names = visualizationModes.map(({name}) => name);

	return JSON.stringify([
		...visualizationModes.map(({enabled, name}) => ({
			...persistedVisualizationModes.find(
				(persistedVisualizationMode) =>
					persistedVisualizationMode.name === name
			),
			enabled,
			name,
		})),
		...persistedVisualizationModes.filter(
			({name}) => !names.includes(name)
		),
	]);
}
