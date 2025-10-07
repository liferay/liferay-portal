/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {filesize} from 'filesize';

import {UploadedFile} from '../../../../../components/FileList/FileList';
import Form from '../../../../../components/MarketplaceForm';
import MultiSelect from '../../../../../components/MultiSelect/MultiSelect';
import Select from '../../../../../components/Select/Select';
import UploadLogo from '../../../../../components/UploadLogo/UploadLogo';
import {
	NewAppTypes,
	useNewAppContext,
} from '../../../../../context/NewAppContext';
import {
	ProductTags,
	ProductVocabulary,
	ProductWorkflowStatusCode,
} from '../../../../../enums/Product';
import i18n from '../../../../../i18n';
import {getRandomID} from '../../../../../utils/string';

const tooltipInfo = {
	areas: 'tooltip',
	categories: 'tootip',
	description: 'tootip',
	name: 'tootip',
	tags: 'tootip',
};

const Profile = () => {
	const [
		{
			_product,
			profile: {areas, categories, description, file, name, tags},
			references: {
				flags: {canModifyProductProfileCategory},
				vocabulariesAndCategories,
			},
		},
		dispatch,
	] = useNewAppContext();

	const defaultSourceItems = {
		areas:
			vocabulariesAndCategories[ProductVocabulary.APP_AREA]?.categories ??
			[],
		categories:
			vocabulariesAndCategories[ProductVocabulary.APP_CATEGORY]
				?.categories ?? [],
		tags:
			vocabulariesAndCategories[ProductVocabulary.APP_TAGS]?.categories ??
			[],
	};

	const onChange = (event: any) => {
		dispatch({
			payload: {[event.target.name]: event.target.value},
			type: NewAppTypes.SET_PROFILE,
		});
	};

	const handleLogoUpload = (files: FileList) => {
		const _file = files[0];

		const newUploadedFile: UploadedFile = {
			changed: true,
			error: false,
			file: _file,
			fileName: _file.name,
			id: getRandomID(),
			preview: URL.createObjectURL(_file),
			progress: 0,
			readableSize: filesize(_file.size),
			tags: [ProductTags.APP_ICON],
			uploaded: true,
		};

		if (file?.id) {
			dispatch({
				payload: file.id,
				type: NewAppTypes.SET_DELETE_IMAGE,
			});
		}

		dispatch({
			payload: {
				file: newUploadedFile,
			},
			type: NewAppTypes.SET_PROFILE,
		});
	};

	const handleDelete = async (id: string) => {
		dispatch({
			payload: id,
			type: NewAppTypes.SET_DELETE_IMAGE,
		});

		dispatch({
			payload: {
				file: undefined,
			},
			type: NewAppTypes.SET_PROFILE,
		});
	};

	const getFilteredItems = (
		selectedItems: {[key: string]: string}[],
		defaultItems: {[key: string]: string}[]
	) =>
		defaultItems?.filter(
			(defaultCategory) =>
				!selectedItems?.some(
					(category) => defaultCategory.value === category.value
				)
		);

	return (
		<div className="new-app-form-profile">
			<h5>App Info</h5>
			<hr />
			<div className="align-items-center d-flex mt-5">
				<UploadLogo
					onDeleteFile={handleDelete}
					onUpload={handleLogoUpload}
					uploadedFile={file}
				/>
			</div>
			<Form.FormControl>
				<Form.Label
					className="mt-5"
					htmlFor="name"
					info={tooltipInfo.name}
					required
				>
					{i18n.translate('name')}
				</Form.Label>

				<Form.Input
					maxLength={50}
					name="name"
					onChange={onChange}
					placeholder="Enter app name"
					value={name}
				/>
			</Form.FormControl>
			<Form.FormControl>
				<Form.Label
					className="mt-5"
					htmlFor="description"
					info={tooltipInfo.description}
					required
				>
					{i18n.translate('description')}
				</Form.Label>

				<Form.Input
					component="textarea"
					maxLength={2000}
					name="description"
					onChange={onChange}
					placeholder="Enter app description"
					type="textarea"
					value={description}
				/>
			</Form.FormControl>

			<div className="form-multiselect">
				<Form.FormControl>
					<Form.Label
						className="mt-5"
						htmlFor="categories"
						info={tooltipInfo.categories}
						required
					>
						{i18n.translate('category')}
					</Form.Label>

					<Select
						className={categories?.value || 'select-empty-value'}
						defaultOption
						defaultOptionLabel={i18n.translate('select-category')}
						disabled={
							!canModifyProductProfileCategory &&
							!!_product?.productId &&
							_product.productStatus !==
								ProductWorkflowStatusCode.DRAFT
						}
						name="category"
						onChange={(event) => {
							const category = defaultSourceItems.categories.find(
								(defaultCategory: {
									label: string;
									value: string;
								}) =>
									defaultCategory.value === event.target.value
							);
							onChange({
								target: {
									name: 'categories',
									value: {
										label: category.label,
										value: event.target.value,
									},
								},
							});
						}}
						options={defaultSourceItems.categories.map(
							(category: {label: string; value: string}) => ({
								key: category.value,
								name: category.label,
							})
						)}
						required
						value={categories?.value || ''}
					/>
				</Form.FormControl>

				<Form.FormControl>
					<Form.Label
						htmlFor="areas"
						info={tooltipInfo.areas}
						required
					>
						{i18n.translate('area')}
					</Form.Label>

					<MultiSelect
						inputName="area"
						key={`areas-${areas.length}`}
						multiselectKey={`area-${
							getFilteredItems(areas, defaultSourceItems?.areas)
								.length
						}`}
						onItemsChange={(items: {[key: string]: string}[]) => {
							const filteredValue = items.filter((item) =>
								defaultSourceItems.areas.some(
									(defaultItem: Categories) =>
										defaultItem.value === item.value
								)
							);

							onChange({
								target: {name: 'areas', value: filteredValue},
							});
						}}
						placeholder={i18n.translate('select-areas')}
						required
						selectedItems={areas}
						sourceItems={getFilteredItems(
							areas,
							defaultSourceItems?.areas
						)}
					/>
				</Form.FormControl>

				<Form.FormControl>
					<Form.Label htmlFor="tags" info={tooltipInfo.tags} required>
						{i18n.translate('tags')}
					</Form.Label>

					<MultiSelect
						inputName="tags-selector"
						key={`tags-${tags.length}`}
						multiselectKey={`tag-${
							getFilteredItems(tags, defaultSourceItems?.tags)
								.length
						}`}
						onItemsChange={(items: {[key: string]: string}[]) => {
							const filteredValue = items.filter((item) =>
								defaultSourceItems.tags.some(
									(defaultItem: Categories) =>
										defaultItem.value === item.value
								)
							);
							onChange({
								target: {name: 'tags', value: filteredValue},
							});
						}}
						placeholder={i18n.translate('select-tags')}
						required
						selectedItems={tags}
						sourceItems={getFilteredItems(
							tags,
							defaultSourceItems?.tags
						)}
					/>
				</Form.FormControl>
			</div>
		</div>
	);
};

export default Profile;
