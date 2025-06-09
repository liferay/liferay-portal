/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBreadcrumb from '@clayui/breadcrumb';
import ClayLayout from '@clayui/layout';
import {IClientExtensionRenderer} from '@liferay/frontend-data-set-web';
import {openModal} from 'frontend-js-components-web';
import {fetch, sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {visit} from '../../components/AddDataSourceFieldsModalContent';
import {
	API_URL,
	DEFAULT_FETCH_HEADERS,
	OBJECT_RELATIONSHIP,
} from '../../utils/constants';
import openDefaultFailureToast from '../../utils/openDefaultFailureToast';
import openDefaultSuccessToast from '../../utils/openDefaultSuccessToast';
import {
	EFieldFormat,
	EFieldType,
	EFilterType,
	ESelectionFilterSourceType,
	IDataSet,
	IDateFilter,
	IField,
	IFieldTreeItem,
	IFilter,
	IFilterTypeProps,
	ISelectionFilter,
} from '../../utils/types';
import {IDataSetSectionProps} from '../DataSet';
import ClientExtensionFilterFormContent from './components/ClientExtensionFilter';
import DateRangeFilterFormContent from './components/DateRangeFilter';
import FilterList from './components/FilterList';
import SelectionFilterFormContent from './components/selection_filter/SelectionFilter';

import '../../../css/Filters.scss';
import sortItems from '../../utils/sortItems';

const FILTER_MODE = {
	CREATION: 'filter-creation',
	EDITION: 'filter-edition',
	LIST: 'filter-list',
};

const FILTER_TYPES: Record<EFilterType, IFilterTypeProps> = {
	[EFilterType.CLIENT_EXTENSION]: {
		Component: ClientExtensionFilterFormContent,
		availableFieldsFilter: (item: IField) => !!item,
		displayType: () => Liferay.Language.get('client-extension-filter'),
		fdsViewRelationship:
			OBJECT_RELATIONSHIP.DATA_SET_CLIENT_EXTENSION_FILTERS,
		fdsViewRelationshipId:
			OBJECT_RELATIONSHIP.DATA_SET_CLIENT_EXTENSION_FILTERS_ID,
		label: Liferay.Language.get('client-extension'),
		url: API_URL.CLIENT_EXTENSION_FILTERS,
	},
	[EFilterType.DATE_RANGE]: {
		Component: DateRangeFilterFormContent,
		availableFieldsFilter: (item: IField) =>
			(item.format === EFieldFormat.DATE ||
				item.format === EFieldFormat.DATE_TIME) &&
			item.filterable,
		displayType: () => Liferay.Language.get('date-filter'),
		fdsViewRelationship: OBJECT_RELATIONSHIP.DATA_SET_DATE_FILTERS,
		fdsViewRelationshipId: OBJECT_RELATIONSHIP.DATA_SET_DATE_FILTERS_ID,
		label: Liferay.Language.get('date-range'),
		url: API_URL.DATE_FILTERS,
	},
	[EFilterType.SELECTION]: {
		Component: SelectionFilterFormContent,
		availableFieldsFilter: (item: IField) =>
			((item.type === EFieldType.STRING && !item.format) ||
				item.type === EFieldType.INTEGER) &&
			item.filterable,
		displayType: (filter: IFilter | undefined) => {
			if (filter?.sourceType === ESelectionFilterSourceType.ITEM_PROXY) {
				return Liferay.Language.get('system-filter');
			}

			return Liferay.Language.get('selection-filter');
		},
		fdsViewRelationship: OBJECT_RELATIONSHIP.DATA_SET_SELECTION_FILTERS,
		fdsViewRelationshipId:
			OBJECT_RELATIONSHIP.DATA_SET_SELECTION_FILTERS_ID,
		label: Liferay.Language.get('selection'),
		url: API_URL.SELECTION_FILTERS,
	},
};

type FilterCollection = Array<IFilter>;

interface IPropsFilterFormComponent {
	dataSet: IDataSet;
	fieldNames?: string[];
	fields: IField[];
	filter?: IFilter | ISelectionFilter;
	filterClientExtensionRenderers?: IClientExtensionRenderer[];
	filterType?: EFilterType;
	namespace: string;
	onCancel: Function;
	onSave: (newFilter: IFilter) => void;
	resolvedRESTSchemas: string[];
	restApplications: string[];
}

function FilterFormComponent({
	dataSet,
	fieldNames,
	fields,
	filter,
	filterClientExtensionRenderers = [],
	filterType,
	namespace,
	onCancel,
	onSave,
	resolvedRESTSchemas,
	restApplications,
}: IPropsFilterFormComponent) {
	const {Component, displayType, fdsViewRelationshipId} =
		FILTER_TYPES[filterType as EFilterType];

	const saveFDSFilter = async (formData: any) => {
		formData = {
			...formData,
			[fdsViewRelationshipId]: dataSet.id,
		};

		let url = FILTER_TYPES[filterType as EFilterType].url;
		let method = 'POST';

		if (filter) {
			method = 'PUT';
			url = `${url}/${filter.id}`;
		}

		const response = await fetch(url, {
			body: JSON.stringify(formData),
			headers: DEFAULT_FETCH_HEADERS,
			method,
		});

		if (!response.ok) {
			openDefaultFailureToast();

			return null;
		}

		const responseJSON = await response.json();

		openDefaultSuccessToast();

		onSave({...responseJSON, displayType: displayType(filter), filterType});
	};

	return (
		<>
			<ClayLayout.SheetHeader className="mb-4">
				<h2>
					{filter &&
						sub(Liferay.Language.get('edit-x-filter'), [
							filter.label,
						])}

					{!filter && <Component.Header />}
				</h2>
			</ClayLayout.SheetHeader>

			<Component.Body
				fieldNames={fieldNames}
				fields={fields}
				filter={filter}
				filterClientExtensionRenderers={filterClientExtensionRenderers}
				namespace={namespace}
				onCancel={onCancel}
				onSave={(formData: any) => saveFDSFilter(formData)}
				resolvedRESTSchemas={resolvedRESTSchemas}
				restApplications={restApplications}
				selectedField={filter ? {name: filter.fieldName} : undefined}
			/>
		</>
	);
}

function Filters({
	dataSet,
	fieldTreeItems: fields,
	filterClientExtensionRenderers,
	namespace,
	resolvedRESTSchemas,
	restApplications,
}: IDataSetSectionProps) {
	const [activeFilter, setActiveFilter] = useState<IFilter | null>(null);
	const [activeFilterType, setActiveFilterType] =
		useState<EFilterType | null>(null);
	const [activeMode, setActiveMode] = useState(FILTER_MODE.LIST);
	const [availableFields, setAvailableFields] = useState(fields);
	const [fieldNames, setFieldNames] = useState<string[]>([]);
	const [filters, setFilters] = useState<IFilter[]>([]);
	const [toggleActiveDisabled, setToogleActiveDisabled] =
		useState<boolean>(false);

	useEffect(() => {
		const getFilters = async () => {
			const response = await fetch(
				`${API_URL.DATA_SETS}/${
					dataSet.id
				}?nestedFields=${Object.values(FILTER_TYPES)
					.map((filter) => filter.fdsViewRelationship)
					.join(',')}`,
				{
					headers: DEFAULT_FETCH_HEADERS,
				}
			);

			const responseJSON = await response.json();

			let filtersOrdered: FilterCollection = [];

			Object.keys(FILTER_TYPES).forEach((type) => {
				const filterTypeProps: IFilterTypeProps =
					FILTER_TYPES[type as EFilterType];

				const filtersArray =
					responseJSON[filterTypeProps.fdsViewRelationship];

				filtersArray.forEach((filter: any) => {
					filtersOrdered.push({
						...filter,
						displayType: filterTypeProps.displayType(filter),
						filterType: type as EFilterType,
					});
				});
			});

			filtersOrdered = sortItems(
				filtersOrdered,
				responseJSON.filtersOrder,
				true
			) as FilterCollection;

			setFilters(
				filtersOrdered.map((filter) => {
					return {
						...filter,
						label: filter.label || '',
					};
				})
			);

			setFieldNames(filtersOrdered.map((filter) => filter.fieldName));
		};

		getFilters();
	}, [dataSet]);

	const updateFiltersOrder = async ({
		filtersOrder,
	}: {
		filtersOrder: string;
	}) => {
		const response = await fetch(
			`${API_URL.DATA_SETS}/by-external-reference-code/${dataSet.externalReferenceCode}`,
			{
				body: JSON.stringify({
					filtersOrder,
				}),
				headers: DEFAULT_FETCH_HEADERS,
				method: 'PATCH',
			}
		);

		if (!response.ok) {
			openDefaultFailureToast();

			return null;
		}

		const responseJSON = await response.json();

		const storedFiltersOrder = responseJSON?.filtersOrder;

		if (
			filters &&
			storedFiltersOrder &&
			storedFiltersOrder === filtersOrder
		) {
			setFilters(
				sortItems(filters, storedFiltersOrder, true) as FilterCollection
			);

			openDefaultSuccessToast();
		}
		else {
			openDefaultFailureToast();
		}
	};

	const noFilterClientExtensionsAvailableModal = () => {
		openModal({
			bodyHTML: Liferay.Language.get(
				'no-frontend-data-set-filter-client-extensions-are-available.-add-a-client-extension-first-in-order-to-create-a-filter'
			),
			buttons: [
				{
					displayType: 'primary',
					label: Liferay.Language.get('close'),
					onClick: ({processClose}: {processClose: Function}) => {
						processClose();
					},
				},
			],
			size: 'lg',
			status: 'info',
			title: Liferay.Language.get(
				'no-frontend-data-set-filter-client-extensions-available'
			),
		});
	};

	const onCreationButtonClick = (filterType: EFilterType) => {
		let availableFieldsListLength = 0;

		const availableFilterTypeFields = JSON.parse(JSON.stringify(fields));

		visit(availableFilterTypeFields, (field: IFieldTreeItem) => {
			const availableFieldsFilter =
				FILTER_TYPES[filterType as EFilterType].availableFieldsFilter(
					field
				);

			if (!availableFieldsFilter) {
				field.disabled = true;
			}
			else {
				availableFieldsListLength++;
				field.disabled = false;
			}
		});

		setAvailableFields(availableFilterTypeFields);

		if (!availableFieldsListLength) {
			openModal({
				bodyHTML: Liferay.Language.get(
					'there-are-no-fields-compatible-with-this-type-of-filter'
				),
				buttons: [
					{
						displayType: 'primary',
						label: Liferay.Language.get('close'),
						onClick: ({processClose}: {processClose: Function}) => {
							processClose();
						},
					},
				],
				size: 'lg',
				status: 'info',
				title: Liferay.Language.get('no-fields-available'),
			});
		}
		else if (
			filterType === EFilterType.CLIENT_EXTENSION &&
			!filterClientExtensionRenderers.length
		) {
			noFilterClientExtensionsAvailableModal();
		}
		else {
			setActiveFilterType(filterType);
			setActiveMode(FILTER_MODE.CREATION);
		}
	};

	const onDelete = async ({item}: {item: IFilter}) => {
		openModal({
			bodyHTML: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-this-filter'
			),
			buttons: [
				{
					autoFocus: true,
					displayType: 'secondary',
					label: Liferay.Language.get('cancel'),
					type: 'cancel',
				},
				{
					displayType: 'danger',
					label: Liferay.Language.get('delete'),
					onClick: ({processClose}: {processClose: Function}) => {
						processClose();

						const url = `${
							FILTER_TYPES[item.filterType as EFilterType].url
						}/${item.id}`;

						fetch(url, {
							headers: DEFAULT_FETCH_HEADERS,
							method: 'DELETE',
						})
							.then(() => {
								openDefaultSuccessToast();
								const filterList = filters.filter(
									(filter: IFilter) => filter.id !== item.id
								);
								setFilters(filterList);

								setFieldNames(
									filterList.map((filter) => filter.fieldName)
								);
							})
							.catch(openDefaultFailureToast);
					},
				},
			],
			status: 'danger',
			title: Liferay.Language.get('delete-filter'),
		});
	};

	const onEdit = ({item}: {item: IFilter}) => {
		if (
			item.filterType === EFilterType.CLIENT_EXTENSION &&
			!filterClientExtensionRenderers.length
		) {
			noFilterClientExtensionsAvailableModal();
		}
		else {
			setActiveMode(FILTER_MODE.EDITION);
			setActiveFilter(item);
		}
	};

	const updateActive = async (item: IFilter) => {
		setToogleActiveDisabled(true);

		const type: any =
			item.filterType === 'DATE_RANGE'
				? 'DATE_FILTERS'
				: `${item.filterType}_FILTERS`;

		const response = await fetch(
			`${API_URL[type]}/by-external-reference-code/${item.externalReferenceCode}`,
			{
				body: JSON.stringify({active: !item.active}),
				headers: DEFAULT_FETCH_HEADERS,
				method: 'PATCH',
			}
		);

		if (!response.ok) {
			openDefaultFailureToast();

			return;
		}

		const dataSetFilter: IFilter = await response.json();

		if (dataSetFilter?.id) {
			const updatedFilters = filters.map((filter) => {
				if (filter.id === dataSetFilter.id) {
					filter = {...filter, ...dataSetFilter};
				}

				return filter;
			});

			setFilters(updatedFilters);

			openDefaultSuccessToast();
		}
		else {
			openDefaultFailureToast();
		}

		setToogleActiveDisabled(false);
	};

	const getBreadcrumbItems = () => {
		const breadcrumbItems: React.ComponentProps<
			typeof ClayBreadcrumb
		>['items'] = [
			{
				active: activeMode === FILTER_MODE.LIST ? true : false,
				label: Liferay.Language.get('filters'),
				onClick: () => {
					setActiveMode(FILTER_MODE.LIST);
				},
			},
		];

		if (activeMode === FILTER_MODE.CREATION) {
			let label = '';
			if (activeFilterType === EFilterType.CLIENT_EXTENSION) {
				label = Liferay.Language.get('new-client-extension-filter');
			}
			if (activeFilterType === EFilterType.DATE_RANGE) {
				label = Liferay.Language.get('new-date-range-filter');
			}
			if (activeFilterType === EFilterType.SELECTION) {
				label = Liferay.Language.get('new-selection-filter');
			}

			breadcrumbItems.push({
				active: true,
				label,
			});
		}

		if (activeMode === FILTER_MODE.EDITION) {
			breadcrumbItems.push({
				active: true,
				label: activeFilter!.label,
			});
		}

		return breadcrumbItems;
	};

	return (
		<ClayLayout.ContainerFluid className="filter-form-wrapper">
			<ClayBreadcrumb className="my-2" items={getBreadcrumbItems()} />

			{activeMode === FILTER_MODE.CREATION && (
				<ClayLayout.Sheet>
					{activeFilterType && (
						<FilterFormComponent
							dataSet={dataSet}
							fieldNames={fieldNames}
							fields={availableFields}
							filterClientExtensionRenderers={
								filterClientExtensionRenderers
							}
							filterType={activeFilterType}
							namespace={namespace}
							onCancel={() => setActiveMode(FILTER_MODE.LIST)}
							onSave={(newfilter) => {
								if (newfilter.label === undefined) {
									newfilter.label = '';
								}
								setFilters([...filters, newfilter]);
								setFieldNames([
									...fieldNames,
									newfilter.fieldName,
								]);
								setActiveMode(FILTER_MODE.LIST);
							}}
							resolvedRESTSchemas={resolvedRESTSchemas}
							restApplications={restApplications}
						/>
					)}
				</ClayLayout.Sheet>
			)}

			{activeMode === FILTER_MODE.EDITION && (
				<ClayLayout.Sheet>
					{activeFilter && (
						<FilterFormComponent
							dataSet={dataSet}
							fieldNames={fieldNames}
							fields={fields}
							filter={activeFilter}
							filterClientExtensionRenderers={
								filterClientExtensionRenderers
							}
							filterType={activeFilter.filterType}
							namespace={namespace}
							onCancel={() => setActiveMode(FILTER_MODE.LIST)}
							onSave={(newfilter) => {
								const newFilters = filters.map((item) => {
									if (item.id === newfilter.id) {
										if (
											item.filterType ===
											EFilterType.DATE_RANGE
										) {
											(newfilter as IDateFilter).from =
												(newfilter as IDateFilter)
													.from || '';
											(newfilter as IDateFilter).to =
												(newfilter as IDateFilter).to ||
												'';
										}

										return {...item, ...newfilter};
									}

									return item;
								});

								setFilters(newFilters);
								setActiveMode(FILTER_MODE.LIST);
							}}
							resolvedRESTSchemas={resolvedRESTSchemas}
							restApplications={restApplications}
						/>
					)}
				</ClayLayout.Sheet>
			)}

			{activeMode === FILTER_MODE.LIST && (
				<ClayLayout.ContainerFluid className="bg-white mb-4 p-0 rounded-sm">
					<FilterList
						createFilter={onCreationButtonClick}
						deleteFilter={onDelete}
						editFilter={onEdit}
						filterTypes={FILTER_TYPES}
						filters={filters}
						toogleActiveDisabled={toggleActiveDisabled}
						updateActive={updateActive}
						updateFiltersOrder={updateFiltersOrder}
					/>
				</ClayLayout.ContainerFluid>
			)}
		</ClayLayout.ContainerFluid>
	);
}

export default Filters;
