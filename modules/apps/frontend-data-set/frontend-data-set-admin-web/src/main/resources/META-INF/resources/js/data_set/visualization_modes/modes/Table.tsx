/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayForm, {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayLabel from '@clayui/label';
import ClayLayout from '@clayui/layout';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import {
	FDS_INTERNAL_CELL_RENDERERS,
	IClientExtensionRenderer,
	IInternalRenderer,
} from '@liferay/frontend-data-set-web';
import {InputLocalized, openModal} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import fuzzy from 'fuzzy';
import React, {useEffect, useState} from 'react';

import FieldSelectModalContent, {
	visit,
} from '../../../components/AddDataSourceFieldsModalContent';
import OrderableTable from '../../../components/OrderableTable';
import {
	DEFAULT_FETCH_HEADERS,
	FUZZY_OPTIONS,
	OBJECT_RELATIONSHIP,
	PAGE_SIZE,
} from '../../../utils/constants';
import openDefaultFailureToast from '../../../utils/openDefaultFailureToast';
import openDefaultSuccessToast from '../../../utils/openDefaultSuccessToast';
import {IDataSetSectionProps} from '../../DataSet';
import AddCustomFieldModalContent from '../components/AddCustomFieldModalContent';

import '../../../../css/TableVisualizationMode.scss';

import ClayAlert from '@clayui/alert';
import ClayIcon from '@clayui/icon';

import getDataSetResourceURL from '../../../utils/getDataSetResourceURL';
import sortItems from '../../../utils/sortItems';
import {
	EFieldType,
	IDataSet,
	IDataSetTableSection,
	IField,
	IFieldTreeItem,
} from '../../../utils/types';

const defaultLanguageId = Liferay.ThemeDisplay.getDefaultLanguageId();

const getRendererLabel = ({
	cetRenderers = [],
	rendererName,
}: {
	cetRenderers?: IClientExtensionRenderer[];
	rendererName: string;
}): string => {
	let clientExtensionRenderer;

	const internalRenderer = FDS_INTERNAL_CELL_RENDERERS.find(
		(renderer: IInternalRenderer) => {
			return renderer.name === rendererName;
		}
	);

	if (internalRenderer?.label) {
		return internalRenderer.label;
	}
	else {
		clientExtensionRenderer = cetRenderers.find(
			(renderer: IClientExtensionRenderer) => {
				return renderer.externalReferenceCode === rendererName;
			}
		);

		if (clientExtensionRenderer?.name) {
			return clientExtensionRenderer.name;
		}

		return rendererName;
	}
};

interface IRendererLabelCellRendererComponentProps {
	cetRenderers?: IClientExtensionRenderer[];
	item: IDataSetTableSection;
	query: string;
}

const RendererLabelCellRendererComponent = ({
	cetRenderers = [],
	item,
	query,
}: IRendererLabelCellRendererComponentProps) => {
	const itemFieldValue = getRendererLabel({
		cetRenderers,
		rendererName: item.renderer,
	});

	const fuzzyMatch = fuzzy.match(query, itemFieldValue, FUZZY_OPTIONS);

	return (
		<span>
			{fuzzyMatch ? (
				<span
					dangerouslySetInnerHTML={{
						__html: fuzzyMatch.rendered,
					}}
				/>
			) : (
				<span>{itemFieldValue}</span>
			)}
		</span>
	);
};

const EditTableSectionModalContent = ({
	cellClientExtensionRenderers,
	closeModal,
	dataSet,
	namespace,
	onSaveButtonClick,
	sortable,
	tableSection,
}: {
	cellClientExtensionRenderers: IClientExtensionRenderer[];
	closeModal: Function;
	dataSet: IDataSet;
	namespace: string;
	onSaveButtonClick: Function;
	sortable: boolean;
	tableSection: IDataSetTableSection;
}) => {
	const [selectedCellRenderer, setSelectedCellRenderer] = useState(
		tableSection.renderer ?? 'default'
	);
	const [tableSectionSortable, setTableSectionSortable] = useState<boolean>(
		tableSection.sortable
	);

	const fdsInternalCellRendererNames = FDS_INTERNAL_CELL_RENDERERS.map(
		(cellRenderer: IInternalRenderer) => cellRenderer.name
	);

	const tableSectionTranslations = tableSection.label_i18n;

	const [i18nFieldLabels, setI18nFieldLabels] = useState(
		tableSectionTranslations
	);

	const editTableSection = async () => {
		const body = {
			label_i18n: i18nFieldLabels,
			renderer: selectedCellRenderer,
			rendererType: !fdsInternalCellRendererNames.includes(
				selectedCellRenderer
			)
				? 'clientExtension'
				: 'internal',
			sortable: tableSectionSortable,
		};

		const url = getDataSetResourceURL({
			dataSetERC: dataSet.externalReferenceCode,
			relatedResourceERC: tableSection.externalReferenceCode,
			relationship: OBJECT_RELATIONSHIP.DATA_SET_TABLE_SECTIONS,
		});

		const response = await fetch(url, {
			body: JSON.stringify(body),
			headers: DEFAULT_FETCH_HEADERS,
			method: 'PATCH',
		});

		if (!response.ok) {
			openDefaultFailureToast();

			return;
		}

		const editedTableSection = await response.json();

		closeModal();

		onSaveButtonClick({editedTableSection});

		openDefaultSuccessToast();
	};

	const tableSectionNameInputId = `${namespace}tableSectionNameInput`;
	const tableSectionLabelInputId = `${namespace}tableSectionLabelInput`;
	const tableSectionRendererSelectId = `${namespace}tableSectionRendererSelectId`;

	const options = FDS_INTERNAL_CELL_RENDERERS.map(
		(renderer: IInternalRenderer) => ({
			label: renderer.label!,
			value: renderer.name!,
		})
	);

	options.push(
		...cellClientExtensionRenderers.map((item) => ({
			label: item.name!,
			value: item.externalReferenceCode!,
		}))
	);

	const CellRendererDropdown = ({
		cellRenderers,
		namespace,
		onItemClick,
	}: {
		cellRenderers: {
			label: string;
			value: string;
		}[];
		namespace: string;
		onItemClick: Function;
	}) => {
		const cellClientExtensionRenderersERCs =
			cellClientExtensionRenderers.map(
				(cellRendererCET) => cellRendererCET.externalReferenceCode
			);

		return (
			<ClayDropDown
				menuElementAttrs={{
					className: 'fds-cell-renderers-dropdown-menu',
				}}
				trigger={
					<ClayButton
						aria-labelledby={`${namespace}cellRenderersLabel`}
						className="form-control form-control-select form-control-select-secondary"
						displayType="secondary"
						id={tableSectionRendererSelectId}
					>
						{selectedCellRenderer
							? getRendererLabel({
									cetRenderers: cellClientExtensionRenderers,
									rendererName: selectedCellRenderer,
								})
							: Liferay.Language.get('choose-an-option')}
					</ClayButton>
				}
			>
				<ClayDropDown.ItemList items={cellRenderers} role="listbox">
					{cellRenderers.map((cellRenderer) => (
						<ClayDropDown.Item
							className="align-items-center d-flex justify-content-between"
							key={cellRenderer.value}
							onClick={() => onItemClick(cellRenderer.value)}
							roleItem="option"
						>
							{cellRenderer.label}

							{cellClientExtensionRenderersERCs.includes(
								cellRenderer.value
							) && (
								<ClayLabel displayType="info">
									{Liferay.Language.get('client-extension')}
								</ClayLabel>
							)}
						</ClayDropDown.Item>
					))}
				</ClayDropDown.ItemList>
			</ClayDropDown>
		);
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Util.sub(
					Liferay.Language.get('edit-x'),
					tableSection.label_i18n[defaultLanguageId] ??
						tableSection.fieldName
				)}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm.Group>
					<label htmlFor={tableSectionNameInputId}>
						{Liferay.Language.get('name')}
					</label>

					<ClayInput
						disabled
						id={tableSectionNameInputId}
						type="text"
						value={tableSection.fieldName}
					/>
				</ClayForm.Group>

				<ClayForm.Group>
					<InputLocalized
						id={tableSectionLabelInputId}
						label={Liferay.Language.get('label')}
						name="label"
						onChange={setI18nFieldLabels}
						translations={i18nFieldLabels}
					/>
				</ClayForm.Group>

				<ClayForm.Group>
					<label htmlFor={tableSectionRendererSelectId}>
						{Liferay.Language.get('renderer')}
					</label>

					<CellRendererDropdown
						cellRenderers={options}
						namespace={namespace}
						onItemClick={(item: string) =>
							setSelectedCellRenderer(item)
						}
					/>
				</ClayForm.Group>

				<ClayForm.Group>
					<ClayCheckbox
						checked={tableSectionSortable}
						disabled={!sortable}
						inline
						label={Liferay.Language.get('sortable')}
						onChange={({target: {checked}}) =>
							setTableSectionSortable(checked)
						}
					/>

					{tableSection.type !== EFieldType.OBJECT && (
						<span
							className="label-icon lfr-portal-tooltip ml-2"
							title={Liferay.Language.get(
								'if-checked,-data-set-items-can-be-sorted-by-this-field'
							)}
						>
							<ClayIcon symbol="question-circle-full" />
						</span>
					)}
				</ClayForm.Group>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton onClick={() => editTableSection()}>
							{Liferay.Language.get('save')}
						</ClayButton>

						<ClayButton
							displayType="secondary"
							onClick={() => closeModal()}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
};

function Table(props: IDataSetSectionProps & {title?: string}) {
	const {
		cellClientExtensionRenderers,
		dataSet,
		fieldTreeItems,
		namespace,
		saveDataSetTableSectionsURL,
		title,
	} = props;

	const [tableSections, setTableSections] =
		useState<Array<IDataSetTableSection> | null>(null);
	const [saveButtonDisabled, setSaveButtonDisabled] = useState(false);

	const getFDSFields = async () => {
		const url = getDataSetResourceURL({
			dataSetERC: dataSet.externalReferenceCode,
			params: {
				nestedFields: OBJECT_RELATIONSHIP.DATA_SET_TABLE_SECTIONS,
				pageSize: PAGE_SIZE,
				sort: 'dateCreated:asc',
			},
			relationship: OBJECT_RELATIONSHIP.DATA_SET_TABLE_SECTIONS,
		});

		const response = await fetch(url, {
			headers: DEFAULT_FETCH_HEADERS,
		});

		if (!response.ok) {
			openDefaultFailureToast();

			return null;
		}

		const responseJSON = await response.json();

		const storedFDSFields: IDataSetTableSection[] = responseJSON?.items;

		if (!storedFDSFields) {
			openDefaultFailureToast();

			return null;
		}

		const tableSectionsOrder =

			// @ts-ignore

			storedFDSFields?.[0]?.[OBJECT_RELATIONSHIP.DATA_SET_TABLE_SECTIONS]
				?.tableSectionsOrder;

		setTableSections(
			sortItems(
				storedFDSFields,
				tableSectionsOrder
			) as IDataSetTableSection[]
		);
	};

	const onDeleteButtonClick = ({item}: {item: IDataSetTableSection}) => {
		openModal({
			bodyHTML: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-this-field?-fragments-using-it-will-be-affected'
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
					onClick: async ({
						processClose,
					}: {
						processClose: Function;
					}) => {
						processClose();

						const url = getDataSetResourceURL({
							dataSetERC: dataSet.externalReferenceCode,
							relatedResourceERC: String(
								item.externalReferenceCode
							),
							relationship:
								OBJECT_RELATIONSHIP.DATA_SET_TABLE_SECTIONS,
						});

						const response = await fetch(url, {method: 'DELETE'});

						if (!response.ok) {
							openDefaultFailureToast();

							return;
						}

						openDefaultSuccessToast();

						setTableSections(
							tableSections?.filter(
								(tableSection: IDataSetTableSection) =>
									tableSection.id !== item.id
							) || []
						);
					},
				},
			],
			status: 'danger',
			title: Liferay.Language.get('delete-field'),
		});
	};

	const saveDataSetTableColumns = async ({
		closeModal,
		fields,
	}: {
		closeModal: Function;
		fields: Array<IField>;
	}) => {
		setSaveButtonDisabled(true);

		const creationData: Array<{
			name: string;
			sortable: boolean;
			type: string;
		}> = [];

		fields.forEach((field) => {
			if (!field.id) {
				creationData.push({
					name: field.name,
					sortable: field.sortable || false,
					type: field.type || 'string',
				});
			}
		});

		const formData = new FormData();

		formData.append(
			`${namespace}creationData`,
			JSON.stringify(creationData)
		);

		formData.append(`${namespace}dataSetId`, dataSet.id);

		const response = await fetch(saveDataSetTableSectionsURL, {
			body: formData,
			method: 'POST',
		});

		setSaveButtonDisabled(false);

		if (!response.ok) {
			openDefaultFailureToast();

			return;
		}

		getFDSFields();

		closeModal();

		openDefaultSuccessToast();
	};

	const updateFDSFieldsOrder = async ({
		tableSectionsOrder,
	}: {
		tableSectionsOrder: string;
	}) => {
		const body = {
			tableSectionsOrder,
		};

		const url = getDataSetResourceURL({
			dataSetERC: dataSet.externalReferenceCode,
		});

		const response = await fetch(url, {
			body: JSON.stringify(body),
			headers: DEFAULT_FETCH_HEADERS,
			method: 'PATCH',
		});

		if (!response.ok) {
			openDefaultFailureToast();

			return null;
		}

		const responseJSON = await response.json();

		const storedFDSFieldsOrder = responseJSON?.tableSectionsOrder;

		if (
			tableSections &&
			storedFDSFieldsOrder &&
			storedFDSFieldsOrder === tableSectionsOrder
		) {
			setTableSections(
				sortItems(
					tableSections,
					storedFDSFieldsOrder
				) as IDataSetTableSection[]
			);

			openDefaultSuccessToast();
		}
		else {
			openDefaultFailureToast();
		}
	};

	useEffect(() => {
		getFDSFields();

		// eslint-disable-next-line react-compiler/react-compiler
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	const onCreationFromDataSourceButtonClick = () => {
		openModal({
			className: 'modal-height-full',
			contentComponent: ({closeModal}: {closeModal: Function}) => (
				<FieldSelectModalContent
					{...props}
					closeModal={closeModal}
					fieldTreeItems={fieldTreeItems}
					onSaveButtonClick={({
						selectedFields,
					}: {
						selectedFields: Array<IField>;
					}) => {
						saveDataSetTableColumns({
							closeModal,
							fields: selectedFields,
						});
					}}
					saveButtonDisabled={saveButtonDisabled}
					selectedFields={
						tableSections
							? tableSections.map((tableSection) => ({
									id: String(tableSection.id),
									name: tableSection.fieldName,
								}))
							: []
					}
					selectionMode="multiple"
				/>
			),
			size: 'lg',
		});
	};

	const onCreationFieldButtonClick = () => {
		openModal({
			contentComponent: ({closeModal}: {closeModal: Function}) => (
				<AddCustomFieldModalContent
					{...props}
					closeModal={closeModal}
					onSaveButtonClick={({name}: {name: string}) => {
						saveDataSetTableColumns({
							closeModal,
							fields: [{name, sortable: true}] as IField[],
						});
					}}
				/>
			),
			title: Liferay.Language.get('delete-data-set'),
		});
	};

	const onEditButtonClick = ({item}: {item: IDataSetTableSection}) => {
		openModal({
			className: 'overflow-auto',
			contentComponent: ({closeModal}: {closeModal: Function}) => (
				<EditTableSectionModalContent
					cellClientExtensionRenderers={cellClientExtensionRenderers}
					closeModal={closeModal}
					dataSet={dataSet}
					namespace={namespace}
					onSaveButtonClick={({
						editedTableSection,
					}: {
						editedTableSection: IDataSetTableSection;
					}) => {
						setTableSections(
							tableSections?.map((tableSection) => {
								if (
									tableSection.fieldName ===
									editedTableSection.fieldName
								) {
									return editedTableSection;
								}

								return tableSection;
							}) || null
						);
					}}
					sortable={isSortable(fieldTreeItems, item)}
					tableSection={item}
				/>
			),
		});
	};

	return tableSections ? (
		<ClayLayout.ContentCol className="c-gap-4 table-visualization-mode">
			<ClayAlert
				displayType="info"
				title={`${Liferay.Language.get('info')}:`}
				variant="stripe"
			>
				{Liferay.Language.get(
					'this-visualization-mode-will-not-be-shown-until-you-assign-at-least-one-field'
				)}
			</ClayAlert>

			<OrderableTable
				actions={[
					{
						icon: 'pencil',
						label: Liferay.Language.get('edit'),
						onClick: onEditButtonClick,
					},
					{
						icon: 'trash',
						label: Liferay.Language.get('delete'),
						onClick: onDeleteButtonClick,
					},
				]}
				creationMenuItems={[
					{
						label: Liferay.Language.get('assign-from-data-source'),
						onClick: onCreationFromDataSourceButtonClick,
						symbolLeft: 'fieldset',
					},
					{
						label: Liferay.Language.get('assign-field-manually'),
						onClick: onCreationFieldButtonClick,
						symbolLeft: 'custom-field',
					},
				]}
				fields={[
					{
						label: Liferay.Language.get('name'),
						name: 'fieldName',
					},
					{
						label: Liferay.Language.get('label'),
						name: 'label',
					},
					{
						label: Liferay.Language.get('type'),
						name: 'type',
					},
					{
						contentRenderer: {
							component: ({item, query}) => (
								<RendererLabelCellRendererComponent
									cetRenderers={cellClientExtensionRenderers}
									item={item}
									query={query}
								/>
							),
							textMatch: (item: IDataSetTableSection) =>
								getRendererLabel({
									cetRenderers: cellClientExtensionRenderers,
									rendererName: item.renderer,
								}),
						},
						label: Liferay.Language.get('renderer'),
						name: 'renderer',
					},
					{
						label: Liferay.Language.get('sortable'),
						name: 'sortable',
					},
				]}
				items={tableSections}
				noItemsButtonLabel={Liferay.Language.get('add-fields')}
				noItemsDescription={Liferay.Language.get(
					'add-fields-to-show-in-your-view'
				)}
				noItemsTitle={Liferay.Language.get('no-fields-added-yet')}
				onOrderChange={({order}: {order: string}) => {
					updateFDSFieldsOrder({
						tableSectionsOrder: order,
					});
				}}
				title={title}
			/>
		</ClayLayout.ContentCol>
	) : (
		<ClayLoadingIndicator />
	);
}

export function Fields(props: IDataSetSectionProps) {
	return (
		<ClayLayout.ContainerFluid>
			<Table {...props} title={Liferay.Language.get('fields')} />
		</ClayLayout.ContainerFluid>
	);
}

function isSortable(
	fieldTreeItems: Array<IFieldTreeItem>,
	selectedItem: IDataSetTableSection
): boolean {
	let isSortable = false;

	visit(fieldTreeItems, (fieldTreeItem: IFieldTreeItem) => {
		if (fieldTreeItem.name === selectedItem.fieldName) {
			isSortable = fieldTreeItem.sortable || false;

			return;
		}
	});

	return isSortable;
}

export default Table;
