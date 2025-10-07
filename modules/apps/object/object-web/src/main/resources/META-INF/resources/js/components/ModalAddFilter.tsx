/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {Observer} from '@clayui/modal/lib/types';
import {
	API,
	DatePicker,
	Input,
	MultiSelectItem,
	MultiSelectItemChild,
	MultipleSelect,
	SingleSelect,
	stringUtils,
} from '@liferay/object-js-components-web';
import React, {
	FormEvent,
	useCallback,
	useEffect,
	useMemo,
	useState,
} from 'react';

import {
	getCheckedListTypeEntries,
	getCheckedObjectRelationshipItems,
	getCheckedWorkflowStatusItems,
	getSystemObjectFieldLabelFromObjectEntry,
} from '../utils/filter';

import './ModalAddFilter.scss';

interface ModalAddFilterProps {
	aggregationFilter?: boolean;
	creationLanguageId?: Liferay.Language.Locale;
	currentFilters: CurrentFilter[];
	disableAutoClose?: boolean;
	disableDateValues?: boolean;
	editingFilter: boolean;
	editingObjectFieldName: string;
	filterOperators: TFilterOperators;
	filterTypeRequired?: boolean;
	header: string;
	objectFields: ObjectField[];
	observer: Observer;
	onClose: () => void;
	onSave: (props: OnSaveProps) => void;
	validate: ({
		checkedItems,
		disableDateValues,
		items,
		selectedFilterBy,
		selectedFilterTypeValue,
		setErrors,
		value,
	}: FilterValidation) => FilterErrors;
	workflowStatuses: LabelValueObject[];
}

export interface OnSaveProps {
	fieldLabel?: LocalizedValue<string>;
	filterBy?: string;
	filterType?: string;
	objectFieldBusinessType?: string;
	objectFieldName: string;
	value?: string;
	valueList?: MultiSelectItemChild[];
}

export type FilterErrors = {
	endDate?: string;
	items?: string;
	selectedFilterBy?: string;
	selectedFilterType?: string;
	startDate?: string;
	value?: string;
};

export type FilterValidation = {
	checkedItems: MultiSelectItemChild[];
	disableDateValues?: boolean;
	items: MultiSelectItem[] | LabelValueObject[];
	selectedFilterBy?: ObjectField;
	selectedFilterTypeValue?: string;
	setErrors: (value: FilterErrors) => void;
	value?: string;
};

type CurrentFilter = {
	definition: {
		[key: string]: string[] | number[];
	} | null;
	fieldLabel?: string;
	filterBy?: string;
	filterType: string | null;
	label: LocalizedValue<string>;
	objectFieldBusinessType?: string;
	objectFieldName?: string;
	value?: string;
	valueList?: LabelValueObject[];
};

type AttachmentEntry = {
	id: number;
	link: {
		href: string;
		label: string;
	};
	name: string;
};

export function ModalAddFilter({
	aggregationFilter,
	creationLanguageId,
	currentFilters,
	disableAutoClose = false,
	disableDateValues,
	editingFilter,
	editingObjectFieldName,
	filterOperators,
	filterTypeRequired,
	header,
	objectFields,
	observer,
	onClose,
	onSave,
	validate,
	workflowStatuses,
}: ModalAddFilterProps) {
	const [items, setItems] = useState<MultiSelectItem[] | LabelValueObject[]>(
		[]
	);

	const [selectedFilterBy, setSelectedFilterBy] = useState<ObjectField>();

	const [selectedFilterTypeValue, setSelectedFilterTypeValue] =
		useState<string>();
	const [value, setValue] = useState<string>();

	const [errors, setErrors] = useState<FilterErrors>({});

	const [filterStartDate, setFilterStartDate] = useState('');
	const [filterEndDate, setFilterEndDate] = useState('');

	const aggregationRelationshipOrDateFieldBusinessType =
		selectedFilterBy?.businessType === 'Date' ||
		(aggregationFilter &&
			selectedFilterBy?.businessType === 'Relationship');

	const filterByItems = useMemo(() => {
		return objectFields.map(({id, label, name}) => ({
			label: stringUtils.getLocalizableLabel({
				fallbackLabel: name,
				fallbackLanguageId:
					creationLanguageId as Liferay.Language.Locale,
				labels: label,
			}),
			value: id,
		})) as LabelValueObject<number>[];
	}, [creationLanguageId, objectFields]);

	const setEditingFilterType = () => {
		const currentFilterColumn = currentFilters.find((filterColumn) => {
			if (filterColumn.objectFieldName === editingObjectFieldName) {
				return filterColumn;
			}
		});

		const definition = currentFilterColumn?.definition;
		const filterType = currentFilterColumn?.filterType;

		const valuesArray =
			definition && filterType ? definition[filterType] : null;

		const editingFilterType = filterOperators.picklistOperators.find(
			(filterType) => filterType.value === currentFilterColumn?.filterType
		);

		if (editingFilterType) {
			setSelectedFilterTypeValue(editingFilterType.value as string);
		}

		return valuesArray;
	};

	const setFieldValues = useCallback(
		(objectField: ObjectField) => {
			if (
				objectField.businessType === 'MultiselectPicklist' ||
				objectField?.businessType === 'Picklist'
			) {
				const makeFetch = async () => {
					if (objectField.listTypeDefinitionId) {
						const items =
							await API.getListTypeDefinitionListTypeEntries(
								objectField.listTypeDefinitionId
							);

						if (editingFilter) {
							setItems(
								getCheckedListTypeEntries(
									items,
									setEditingFilterType
								)
							);
						}
						else {
							setItems([
								{
									children: items.map((item) => {
										return {
											label: item.name,
											value: item.key,
										};
									}),
									label: '',
									value: 'listTypeEntries',
								},
							]);
						}
					}
				};

				makeFetch();
			}
			else if (objectField.name === 'status') {
				let newItems: MultiSelectItem[] = [];

				if (editingFilter) {
					newItems = getCheckedWorkflowStatusItems(
						workflowStatuses,
						setEditingFilterType
					);
				}
				else {
					newItems = [
						{
							children: workflowStatuses.map((workflowStatus) => {
								return {
									label: workflowStatus.label,
									value: workflowStatus.value,
								};
							}),
							label: '',
							value: 'workflowStatuses',
						},
					];
				}

				setItems(newItems);
			}
			else if (objectField.businessType === 'Relationship') {
				const makeFetch = async () => {
					const {objectFieldSettings} = objectField;

					const [{value}] = objectFieldSettings as NameValueObject[];

					const [
						{
							objectFields,
							restContextPath,
							system,
							titleObjectFieldName,
						},
					] = await API.getObjectDefinitions({
						filter: `name eq '${value}'`,
					});

					const titleObjectField = objectFields.find(
						(objectField) =>
							objectField.name === titleObjectFieldName
					) as ObjectField;

					const relatedObjectEntries = await API.getList<ObjectEntry>(
						`${restContextPath}`
					);

					if (!relatedObjectEntries) {
						setItems([]);

						return;
					}

					if (editingFilter) {
						setItems(
							getCheckedObjectRelationshipItems(
								relatedObjectEntries,
								titleObjectField.name,
								titleObjectField.system as boolean,
								system,
								setEditingFilterType
							)
						);
					}
					else {
						const newChildren = relatedObjectEntries.map(
							(objectEntry) => {
								const newItemsObject = {
									value: system
										? String(objectEntry.id)
										: objectEntry.externalReferenceCode,
								};

								if (titleObjectField.system) {
									return getSystemObjectFieldLabelFromObjectEntry(
										titleObjectField.name,
										objectEntry,
										newItemsObject
									);
								}

								let label = String(
									objectEntry[titleObjectField?.name]
								);

								if (
									titleObjectField.businessType ===
									'Attachment'
								) {
									label = (
										objectEntry as {
											[key: string]: AttachmentEntry;
										}
									)[titleObjectField.name].name;
								}

								return {
									...newItemsObject,
									label,
								};
							}
						);

						const newItems = [
							{
								children: newChildren,
								label: '',
								value: 'objectEntries',
							},
						];

						setItems(newItems);
					}
				};

				makeFetch();
			}
		},

		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);

	const isMultiSelectValue = () => {
		if (
			aggregationFilter &&
			selectedFilterBy?.businessType === 'Relationship'
		) {
			return false;
		}

		if (
			selectedFilterTypeValue &&
			(selectedFilterBy?.name === 'status' ||
				selectedFilterBy?.businessType === 'MultiselectPicklist' ||
				selectedFilterBy?.businessType === 'Picklist' ||
				selectedFilterBy?.businessType === 'Relationship')
		) {
			return true;
		}
	};

	const handleSaveFilter = (event: FormEvent) => {
		event.preventDefault();

		const checkedItems: MultiSelectItemChild[] = [];

		if (!!items.length && selectedFilterBy?.businessType !== 'Date') {
			const [itemGroup] = items as MultiSelectItem[];
			itemGroup.children.forEach((child) => {
				if (child.checked) {
					checkedItems.push(child);
				}
			});
		}

		const currentErrors = validate({
			checkedItems,
			disableDateValues,
			items,
			selectedFilterBy,
			selectedFilterTypeValue,
			setErrors,
			value,
		});

		if (Object.keys(currentErrors).length) {
			return;
		}

		if (editingFilter) {
			onSave({
				fieldLabel: selectedFilterBy?.label,
				filterBy: selectedFilterBy?.name,
				filterType: selectedFilterTypeValue,
				objectFieldBusinessType: selectedFilterBy?.businessType,
				objectFieldName: editingObjectFieldName,
				value: value ?? undefined,
				valueList: isMultiSelectValue() ? checkedItems : undefined,
			});
		}
		else {
			onSave({
				fieldLabel: selectedFilterBy?.label,
				filterBy: selectedFilterBy?.name,
				filterType: selectedFilterTypeValue,
				objectFieldBusinessType: selectedFilterBy?.businessType,
				objectFieldName: selectedFilterBy?.name!,
				value: value ?? undefined,
				valueList: isMultiSelectValue()
					? checkedItems
					: selectedFilterBy?.businessType === 'Date'
						? items
						: undefined,
			});
		}

		onClose();
	};

	useEffect(() => {
		if (!selectedFilterBy && !editingObjectFieldName) {
			setItems([]);
		}
		else {
			if (selectedFilterBy) {
				setFieldValues(selectedFilterBy as unknown as ObjectFieldView);
			}
			else {
				const objectField = objectFields.find(
					({name}) => name === editingObjectFieldName
				);

				objectField && setFieldValues(objectField);
			}
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [editingFilter, setFieldValues, selectedFilterBy, workflowStatuses]);

	useEffect(() => {
		if (editingFilter) {
			const editingObjectFieldFilter = objectFields.find(
				(objectField) => objectField.name === editingObjectFieldName
			);

			setSelectedFilterBy(editingObjectFieldFilter);
		}
	}, [editingFilter, editingObjectFieldName, objectFields]);

	useEffect(() => {
		if (!selectedFilterBy && !editingObjectFieldName) {
			setItems([]);
		}
		else {
			if (selectedFilterBy) {
				setFieldValues(selectedFilterBy as unknown as ObjectFieldView);
			}
			else {
				const objectField = objectFields.find(
					({name}) => name === editingObjectFieldName
				);

				objectField && setFieldValues(objectField);
			}
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [editingFilter, setFieldValues, selectedFilterBy, workflowStatuses]);

	useEffect(() => {
		if (editingFilter) {
			const editingObjectFieldFilter = objectFields.find(
				(objectField) => objectField.name === editingObjectFieldName
			);

			setSelectedFilterBy(editingObjectFieldFilter);
		}
	}, [editingFilter, editingObjectFieldName, objectFields]);

	return (
		<ClayModal
			center
			disableAutoClose={disableAutoClose}
			observer={observer}
		>
			<ClayModal.Header>{header}</ClayModal.Header>

			<ClayModal.Body>
				{!editingFilter && (
					<SingleSelect
						error={errors.selectedFilterBy}
						id="modalAddFilterBy"
						items={filterByItems}
						label={Liferay.Language.get('filter-by')}
						onSelectionChange={(value) => {
							const selectedField = objectFields.find(
								({id}) => id.toString() === value
							);

							const userRelationship =
								!!selectedField?.objectFieldSettings?.find(
									({name, value}) =>
										name === 'objectDefinition1ShortName' &&
										value === 'User'
								);

							setSelectedFilterBy(selectedField);
							setValue('');

							if (
								selectedField?.businessType ===
									'Relationship' &&
								userRelationship &&
								aggregationFilter
							) {
								return setSelectedFilterTypeValue(
									'currentUser'
								);
							}

							setSelectedFilterTypeValue(undefined);
						}}
						required
						selectedKey={selectedFilterBy?.id.toString()}
					/>
				)}

				{selectedFilterBy &&
					!aggregationRelationshipOrDateFieldBusinessType && (
						<SingleSelect
							error={errors.selectedFilterType}
							items={
								selectedFilterBy?.businessType === 'Integer' ||
								selectedFilterBy?.businessType === 'LongInteger'
									? filterOperators.numericOperators
									: filterOperators.picklistOperators
							}
							label={Liferay.Language.get('filter-type')}
							onSelectionChange={(value) =>
								setSelectedFilterTypeValue(value as string)
							}
							required={filterTypeRequired}
							selectedKey={selectedFilterTypeValue}
						/>
					)}

				{selectedFilterBy &&
					selectedFilterBy?.businessType === 'Date' &&
					!disableDateValues && (
						<SingleSelect
							error={errors.selectedFilterType}
							items={filterOperators.dateOperators}
							label={Liferay.Language.get('filter-type')}
							onSelectionChange={(value) =>
								setSelectedFilterTypeValue(value as string)
							}
							required={filterTypeRequired}
							selectedKey={selectedFilterTypeValue}
						/>
					)}

				{selectedFilterTypeValue &&
					(selectedFilterBy?.businessType === 'Integer' ||
						selectedFilterBy?.businessType === 'LongInteger') && (
						<Input
							error={errors.value}
							label={Liferay.Language.get('value')}
							onChange={({target: {value}}) => {
								const newValue = value.replace(/[\D]/g, '');
								setValue(newValue);
							}}
							required
							type="number"
							value={value}
						/>
					)}

				{isMultiSelectValue() && (
					<MultipleSelect
						error={errors.items}
						label={Liferay.Language.get('value')}
						options={items as MultiSelectItem[]}
						required
						setOptions={setItems}
					/>
				)}

				{selectedFilterTypeValue &&
					selectedFilterBy?.businessType === 'Date' &&
					!disableDateValues && (
						<div className="row">
							<div className="col-lg-6">
								<DatePicker
									error={errors.startDate}
									label={Liferay.Language.get('start')}
									onChange={(value) => {
										setItems([
											...items.filter(
												(item) => item.value !== 'ge'
											),
											{
												label: value,
												value: 'ge',
											},
										]);

										setFilterStartDate(value);
									}}
									required
									type="Date"
									value={filterStartDate}
								/>
							</div>

							<div className="col-lg-6">
								<DatePicker
									error={errors.endDate}
									label={Liferay.Language.get('end')}
									onChange={(value) => {
										setItems([
											...items.filter(
												(item) => item.value !== 'le'
											),
											{
												label: value,
												value: 'le',
											},
										]);

										setFilterEndDate(value);
									}}
									required
									type="Date"
									value={filterEndDate}
								/>
							</div>
						</div>
					)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={() => onClose()}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							onClick={handleSaveFilter}
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
