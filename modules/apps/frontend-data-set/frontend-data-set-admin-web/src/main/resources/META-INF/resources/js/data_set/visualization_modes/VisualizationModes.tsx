/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBreadcrumb from '@clayui/breadcrumb';
import {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLayout from '@clayui/layout';
import {escapeHTML, fetch} from 'frontend-js-web';
import fuzzy from 'fuzzy';
import React, {ComponentType, useEffect, useState} from 'react';

import OrderableTable from '../../components/OrderableTable';
import Toggle from '../../components/Toggle';
import {
	DEFAULT_FETCH_HEADERS,
	DEFAULT_VISUALIZATION_MODES,
	FUZZY_OPTIONS,
} from '../../utils/constants';
import getDataSetResourceURL from '../../utils/getDataSetResourceURL';
import {
	getClientExtensionSections,
	getSectionFields,
	hasMissingRequiredSections,
} from '../../utils/getVisualizationModeSections';
import getVisualizationModes, {
	getMappedVisualizationModeNames,
	serializeVisualizationModes,
} from '../../utils/getVisualizationModes';
import openDefaultFailureToast from '../../utils/openDefaultFailureToast';
import openDefaultSuccessToast from '../../utils/openDefaultSuccessToast';
import {IDataSetVisualizationMode} from '../../utils/types';
import {IDataSetSectionProps} from '../DataSet';
import Cards from './modes/Cards';
import ClientExtensionVisualizationMode from './modes/ClientExtensionVisualizationMode';
import List from './modes/List';
import Table from './modes/Table';

interface IVisualizationModeItem extends IDataSetVisualizationMode {
	active: boolean;
	externalReferenceCode: string;
	lastEnabled: boolean;
	mapped: boolean | null;
	onEdit: () => void;
	onToggle: () => Promise<void>;
	toggleDisabled: boolean;
	type: string;
}

const VISUALIZATION_MODE_COMPONENT_MAP: {
	[name: string]: ComponentType<IDataSetSectionProps>;
} = {
	cards: Cards,
	list: List,
	table: Table,
};

function EditCell({item}: {item: IVisualizationModeItem}) {
	const label = Liferay.Util.sub(Liferay.Language.get('edit-x'), item.label);

	return (
		<ClayButtonWithIcon
			aria-label={label}
			borderless
			displayType="secondary"
			onClick={item.onEdit}
			size="sm"
			symbol="pencil"
			title={label}
		/>
	);
}

function NameCell({
	item,
	query,
}: {
	item: IVisualizationModeItem;
	query: string;
}) {
	const fuzzyMatch = fuzzy.match(
		escapeHTML(query),
		escapeHTML(item.label),
		FUZZY_OPTIONS
	);

	return (
		<>
			<ClayIcon className="mr-3" symbol={item.thumbnail} />

			{fuzzyMatch ? (
				<span
					dangerouslySetInnerHTML={{
						__html: fuzzyMatch.rendered,
					}}
				/>
			) : (
				<span>{item.label}</span>
			)}
		</>
	);
}

function FieldMappingCell({item}: {item: IVisualizationModeItem}) {
	if (item.mapped === null) {
		return null;
	}

	return item.mapped ? (
		<ClayLabel displayType="success">
			{Liferay.Language.get('mapped')}
		</ClayLabel>
	) : (
		<ClayLabel displayType="warning">
			{Liferay.Language.get('not-mapped')}
		</ClayLabel>
	);
}

function StatusCell({item}: {item: IVisualizationModeItem}) {
	return (
		<span
			title={
				item.lastEnabled
					? Liferay.Language.get(
							'at-least-one-visualization-mode-must-be-enabled'
						)
					: undefined
			}
		>
			<Toggle
				disabled={item.toggleDisabled}
				item={item}
				toggleChange={item.onToggle}
			/>
		</span>
	);
}

const FIELDS = [
	{
		contentRenderer: {
			component: NameCell,
			textMatch: (item: IVisualizationModeItem) => item.label,
		},
		label: Liferay.Language.get('name'),
		name: 'label',
	},
	{
		contentRenderer: {
			component: FieldMappingCell,
			textMatch: (item: IVisualizationModeItem) =>
				item.mapped === null
					? ''
					: item.mapped
						? Liferay.Language.get('mapped')
						: Liferay.Language.get('not-mapped'),
		},
		label: Liferay.Language.get('field-mapping'),
		name: 'mapped',
	},
	{
		label: Liferay.Language.get('type'),
		name: 'type',
	},
	{
		contentRenderer: {
			component: StatusCell,
			textMatch: (item: IVisualizationModeItem) =>
				item.enabled
					? Liferay.Language.get('active')
					: Liferay.Language.get('inactive'),
		},
		label: Liferay.Language.get('status'),
		name: 'active',
	},
	{
		contentRenderer: {
			component: EditCell,
			textMatch: () => '',
		},
		label: '',
		name: 'edit',
	},
];

export default function VisualizationModes(props: IDataSetSectionProps) {
	const {dataSet, onDataSetUpdate, visualizationModeClientExtensions} = props;

	const [activeVisualizationModeName, setActiveVisualizationModeName] =
		useState<string | null>(null);
	const [mappedVisualizationModeNames, setMappedVisualizationModeNames] =
		useState<string[] | null>(null);
	const [saving, setSaving] = useState(false);
	const [visualizationModes, setVisualizationModes] = useState(() =>
		getVisualizationModes({dataSet, visualizationModeClientExtensions})
	);

	useEffect(() => {
		if (activeVisualizationModeName) {
			return;
		}

		const fields = DEFAULT_VISUALIZATION_MODES.map(
			({relationship}) => relationship
		).join(',');

		fetch(
			getDataSetResourceURL({
				dataSetERC: dataSet.externalReferenceCode,
				params: {fields, nestedFields: fields},
			}),
			{headers: DEFAULT_FETCH_HEADERS}
		)
			.then((response: Response) => {
				if (!response.ok) {
					throw new Error();
				}

				return response.json();
			})
			.then((responseJSON: {[key: string]: any}) =>
				setMappedVisualizationModeNames(
					getMappedVisualizationModeNames(responseJSON)
				)
			)
			.catch(openDefaultFailureToast);
	}, [activeVisualizationModeName, dataSet.externalReferenceCode]);

	const updateVisualizationModes = async (
		updatedVisualizationModes: IDataSetVisualizationMode[]
	) => {
		const previousVisualizationModes = visualizationModes;

		setSaving(true);
		setVisualizationModes(updatedVisualizationModes);

		const response = await fetch(
			getDataSetResourceURL({
				dataSetERC: dataSet.externalReferenceCode,
			}),
			{
				body: JSON.stringify({
					visualizationModes: serializeVisualizationModes({
						dataSet,
						visualizationModes: updatedVisualizationModes,
					}),
				}),
				headers: DEFAULT_FETCH_HEADERS,
				method: 'PATCH',
			}
		);

		const responseJSON = response.ok ? await response.json() : null;

		setSaving(false);

		if (!responseJSON?.id) {
			openDefaultFailureToast();

			setVisualizationModes(previousVisualizationModes);

			return;
		}

		openDefaultSuccessToast();

		onDataSetUpdate(responseJSON);
	};

	const updateVisualizationModesOrder = (order: string) => {
		const names = order.split(',');

		// The order only lists the visible rows while searching, so they take
		// the sections the visible rows held and the hidden ones stay in place

		const reorderedVisualizationModes = names
			.map((name) =>
				visualizationModes.find(
					(visualizationMode) => visualizationMode.name === name
				)
			)
			.filter(Boolean) as IDataSetVisualizationMode[];

		let index = 0;

		updateVisualizationModes(
			visualizationModes.map((visualizationMode) =>
				names.includes(visualizationMode.name)
					? reorderedVisualizationModes[index++]
					: visualizationMode
			)
		);
	};

	const toggleVisualizationMode = (name: string) =>
		updateVisualizationModes(
			visualizationModes.map((visualizationMode) =>
				visualizationMode.name === name
					? {
							...visualizationMode,
							enabled: !visualizationMode.enabled,
						}
					: visualizationMode
			)
		);

	const enabledVisualizationModesCount = visualizationModes.filter(
		({enabled}) => enabled
	).length;

	const items: IVisualizationModeItem[] = visualizationModes.map(
		(visualizationMode) => {
			const lastEnabled =
				visualizationMode.enabled &&
				enabledVisualizationModesCount === 1;

			const sections = visualizationMode.clientExtension
				? getClientExtensionSections(
						visualizationModeClientExtensions,
						visualizationMode.name
					)
				: [];

			let mapped: boolean | null = null;

			if (visualizationMode.clientExtension) {
				if (sections.some(({required}) => required)) {
					mapped = !hasMissingRequiredSections(
						getSectionFields(dataSet, visualizationMode.name),
						sections
					);
				}
			}
			else if (mappedVisualizationModeNames) {
				mapped = mappedVisualizationModeNames.includes(
					visualizationMode.name
				);
			}

			return {
				...visualizationMode,
				active: visualizationMode.enabled,
				externalReferenceCode: visualizationMode.name,
				lastEnabled,
				mapped,
				onEdit: () =>
					setActiveVisualizationModeName(visualizationMode.name),
				onToggle: () => toggleVisualizationMode(visualizationMode.name),
				toggleDisabled: saving || lastEnabled,
				type: visualizationMode.clientExtension
					? Liferay.Language.get('client-extension')
					: Liferay.Language.get('default'),
			};
		}
	);

	const activeVisualizationMode = visualizationModes.find(
		(visualizationMode) =>
			visualizationMode.name === activeVisualizationModeName
	);

	const breadcrumbItems: React.ComponentProps<
		typeof ClayBreadcrumb
	>['items'] = [
		{
			active: !activeVisualizationMode,
			label: Liferay.Language.get('visualization-modes'),
			onClick: () => setActiveVisualizationModeName(null),
		},
	];

	if (activeVisualizationMode) {
		breadcrumbItems.push({
			active: true,
			label: activeVisualizationMode.label,
		});
	}

	const ActiveVisualizationModeComponent = activeVisualizationMode
		? VISUALIZATION_MODE_COMPONENT_MAP[activeVisualizationMode.name]
		: null;

	return (
		<ClayLayout.ContainerFluid className="mt-3 visualization-modes">
			<ClayBreadcrumb className="my-2" items={breadcrumbItems} />

			{activeVisualizationMode ? (
				<ClayLayout.Sheet className="visualization-mode-detail">
					<ClayLayout.SheetHeader className="mb-4">
						<h2 className="mb-0">
							{activeVisualizationMode.label}
						</h2>
					</ClayLayout.SheetHeader>

					{ActiveVisualizationModeComponent ? (
						<ActiveVisualizationModeComponent {...props} />
					) : (
						<ClientExtensionVisualizationMode
							{...props}
							visualizationModeName={activeVisualizationMode.name}
						/>
					)}
				</ClayLayout.Sheet>
			) : (
				<OrderableTable
					className="mt-0"
					fields={FIELDS}
					items={items}
					noItemsButtonLabel=""
					noItemsDescription={Liferay.Language.get(
						'sorry,-no-results-were-found'
					)}
					noItemsTitle={Liferay.Language.get('no-results-found')}
					onOrderChange={({order}: {order: string}) =>
						updateVisualizationModesOrder(order)
					}
					onRowClick={({item}) => item.onEdit()}
					title={Liferay.Language.get('visualization-modes')}
				/>
			)}
		</ClayLayout.ContainerFluid>
	);
}
