/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayMultiSelect from '@clayui/multi-select';
import {filesize} from 'filesize';
import {useState} from 'react';

import {UploadedFile} from '../../../../../../components/FileList/FileList';
import Form from '../../../../../../components/MarketplaceForm';
import UploadLogo from '../../../../../../components/UploadLogo/UploadLogo';
import {
	SolutionTypes,
	useSolutionContext,
} from '../../../../../../context/SolutionContext';
import {ProductVocabulary} from '../../../../../../enums/Product';
import i18n from '../../../../../../i18n';
import {getIconSpriteMap} from '../../../../../../liferay/constants';
import {getRandomID} from '../../../../../../utils/string';

const tooltipInfo = {
	categories: i18n.translate(
		'choose-the-marketplace-category-that-most-accurately-describes-what-your-solution-does-users-looking-for-specific-types-of-solutions-will-often-browse-categories-by-searching-on-a-specific-category-name-in-the-main-marketplace-home-page-having-your-solution-listed-under-the-appropriate-category-will-help-them-find-your-solution'
	),
	description: i18n.translate(
		'you-can-put-anything-you-want-here-but-a-good-guideline-is-no-more-than-4-5-paragraphs-this-field-does-not-allow-any-markup-tags-its-just-text-please-do-not-use-misleading-names-information-or-icons-descriptions-should-be-as-concise-as-possible-ensure-your-icons-images-descriptions-and-tags-are-free-of-profanity-or-other-offensive-material'
	),
	name: i18n.translate(
		'customers-of-the-marketplace-will-see-this-as-the-name-of-the-solution-please-use-a-title-of-no-longer-than-50-characters-titles-longer-than-18-characters-may-be-truncated-the-solution-title-may-contain-the-word-liferay-to-describe-its-use-or-intent-as-long-as-the-name-does-not-imply-official-certification-or-validation-from-liferay-inc-an-example-of-permissible-names-would-be-exchange-connector-for-liferay-or-integration-connector-kit-for-liferay-while-liferay-mail-solution-or-liferay-management-console-would-not-be-permitted-without-explicit-approval-please-refer-to-our-trademark-policy'
	),
	tags: i18n.translate(
		'tags-help-to-describe-your-solution-in-the-marketplace-select-the-tags-most-relevant-to-your-solution-they-can-be-changed-if-needed'
	),
};

const Profile = () => {
	const [
		{
			profile: {categories, description, file, name, tags},
			references: {vocabulariesAndCategories},
		},
		dispatch,
	] = useSolutionContext();

	const defaultSourceItems = {
		categories:
			vocabulariesAndCategories[ProductVocabulary.SOLUTION_CATEGORY]
				?.categories ?? [],
		tags:
			vocabulariesAndCategories[ProductVocabulary.SOLUTION_TAGS]
				?.categories ?? [],
	};

	const [multiSelectText, setMultiSelectText] = useState({
		categories: '',
		tags: '',
	});

	const onChange = (event: any) => {
		dispatch({
			payload: {[event.target.name]: event.target.value},
			type: SolutionTypes.SET_PROFILE,
		});
	};

	const onChangeMultiSelect = (event: any) => {
		setMultiSelectText((prevState) => ({
			...prevState,
			[event.target.name]: event.target.value,
		}));
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
			uploaded: true,
		};

		if (file?.id) {
			dispatch({
				payload: file.id,
				type: SolutionTypes.SET_DELETE_IMAGE,
			});
		}

		dispatch({
			payload: {
				file: newUploadedFile,
			},
			type: SolutionTypes.SET_PROFILE,
		});
	};

	const handleDelete = async (id: string) => {
		dispatch({
			payload: id,
			type: SolutionTypes.SET_DELETE_IMAGE,
		});

		dispatch({
			payload: {
				file: undefined,
			},
			type: SolutionTypes.SET_PROFILE,
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
		<div className="mb-4 solutions-form-profile">
			<h3>{i18n.translate('solutions-info')}</h3>
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
					placeholder="Enter solution name"
					type="text"
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
					maxLength={150}
					name="description"
					onChange={onChange}
					placeholder="Enter solution description"
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
						{i18n.translate('categories')}
					</Form.Label>

					<ClayMultiSelect
						{...{placeholder: 'Select categories'}}
						inputName="description-selector"
						items={categories}
						key={`cat-${categories.length}`}
						onChange={(value: string) =>
							onChangeMultiSelect({
								target: {
									name: 'categories',
									value,
								},
							})
						}
						onItemsChange={(value: {[key: string]: string}[]) =>
							onChange({
								target: {name: 'categories', value},
							})
						}
						sourceItems={getFilteredItems(
							categories,
							defaultSourceItems?.categories
						)}
						spritemap={getIconSpriteMap()}
						value={multiSelectText?.categories}
					/>
				</Form.FormControl>

				<Form.FormControl>
					<Form.Label
						className="mt-5"
						htmlFor="tags"
						info={tooltipInfo.tags}
						required
					>
						{i18n.translate('tags')}
					</Form.Label>

					<ClayMultiSelect
						{...{placeholder: 'Select tags'}}
						inputName="tags-selector"
						items={tags}
						key={`tags-${tags.length}`}
						onChange={(value: string) =>
							onChangeMultiSelect({
								target: {
									name: 'tags',
									value,
								},
							})
						}
						onItemsChange={(value: {[key: string]: string}[]) =>
							onChange({
								target: {name: 'tags', value},
							})
						}
						sourceItems={getFilteredItems(
							tags,
							defaultSourceItems?.tags
						)}
						spritemap={getIconSpriteMap()}
						value={multiSelectText?.tags}
					/>
				</Form.FormControl>
			</div>
		</div>
	);
};

export default Profile;
