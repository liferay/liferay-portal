/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import Form, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import ClayMultiSelect from '@clayui/multi-select';
import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import {useFormik} from 'formik';
import {openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import ApiHelper from '../../../services/ApiHelper';
import {executeAsyncItemAction} from '../../FDSPropsTransformer/utils/executeAsyncItemAction';
import SpaceSticker, {LogoColor} from '../../components/SpaceSticker';

type Tag = {
	label: string;
	value: any;
};

export default function MergeTagsModalContent({
	closeModal,
	loadData,
	tagId,
	tagName,
}: {
	closeModal: () => void;
	loadData: () => {};
	tagId: number;
	tagName: string;
}) {
	const [tags, setTags] = useState<Tag[]>([]);
	const [selectedTags, setSelectedTags] = useState<Tag[]>([]);

	useEffect(() => {
		const getTags = async () => {
			const {data} = await ApiHelper.get<{items: any[]}>(
				'/o/headless-admin-taxonomy/v1.0/keywords'
			);

			if (data) {
				const allTags = data.items.map(
					({id, name}: {id: any; name: string}) => ({
						label: name,
						value: id,
					})
				);

				setTags(allTags);

				const selectedTag = allTags.find(
					(tag: Tag) => tag.value === tagId && tag.label === tagName
				);

				if (selectedTag) {
					setSelectedTags([selectedTag]);
				}
			}
		};

		getTags();
	}, [tagId, tagName]);

	const _handleTagChange = (items: Tag[]) => {
		setSelectedTags(tags.filter((item) => items.includes(item)));
	};

	const mergeTags = (values: any) => {
		const params = new URLSearchParams();

		for (const item of selectedTags) {
			if (Number(item.value) === Number(values.tagId)) {
				continue;
			}
			params.append('fromKeywordIds', item.value);
		}

		const url = `/o/headless-admin-taxonomy/v1.0/keywords/${values.tagId}/merge?${params}`;

		executeAsyncItemAction({
			method: 'PUT',
			refreshData: loadData,
			successMessage: sub(
				Liferay.Language.get('x-and-x-have-been-successfully-merged'),
				selectedTags
					.filter((item) => item.label !== tagName)
					.map((item) => item.label)
					.join(', '),
				`${Liferay.Util.escapeHTML(tagName)}`
			),
			url,
		});

		closeModal();
	};

	const {handleSubmit} = useFormik({
		initialValues: {
			tagId,
			tagName,
		},
		onSubmit: (values) => {
			if (selectedTags.length < 2) {
				openModal({
					bodyHTML: sub(
						Liferay.Language.get('please-choose-at-least-x-tags'),
						2
					),
					buttons: [
						{
							autoFocus: true,
							displayType: 'warning',
							label: Liferay.Language.get('ok'),
							type: 'cancel',
						},
					],
					height: '30vh',
					status: 'warning',
					title: Liferay.Language.get('merge-tags'),
				});

				return;
			}

			openModal({
				bodyHTML: sub(
					Liferay.Language.get(
						'are-you-sure-you-want-to-merge-x-into-x-all-spaces'
					),
					selectedTags.map((item) => item.label).join(', '),
					`${Liferay.Util.escapeHTML(tagName)}`
				),
				buttons: [
					{
						autoFocus: true,
						displayType: 'warning',
						label: Liferay.Language.get('cancel'),
						type: 'cancel',
					},
					{
						displayType: 'warning',
						label: Liferay.Language.get('ok'),
						onClick: ({processClose}: {processClose: Function}) => {
							processClose();

							mergeTags(values);
						},
					},
				],
				height: '30vh',
				status: 'warning',
				title: Liferay.Language.get('confirm-merge-tags'),
			});
		},
	});

	const SelectTagsDataSetModalContent = ({
		closeModal,
	}: {
		closeModal: () => void;
	}) => {
		const VIEWS_SPACE_TABLE_CELL_RENDERER_NAME =
			'ViewsSpaceTableCellRenderer';

		const ViewsSpaceTableCell = ({itemData}: {itemData: any}) => {
			const assetLibraryIds = itemData.assetLibraries.map(
				(assetLibrary: any) => assetLibrary.id
			);

			if (assetLibraryIds.includes(-1)) {
				return (
					<span className="align-items-center d-flex space-renderer-sticker">
						<SpaceSticker name="All Spaces" size="sm" />
					</span>
				);
			}

			return (
				<>
					{itemData.assetLibraries.map(
						(
							assetLibrary: {
								name: string;
								settings?: {logoColor: string};
							},
							index: number
						) => (
							<span
								className="align-items-center d-flex space-renderer-sticker"
								key={index}
							>
								<SpaceSticker
									displayType={
										assetLibrary.settings
											?.logoColor as LogoColor
									}
									name={assetLibrary.name}
									size="sm"
								/>
							</span>
						)
					)}
				</>
			);
		};

		return (
			<>
				<div className="categorization-section">
					<ClayModal.Header>
						{Liferay.Language.get('merge-tags')}
					</ClayModal.Header>

					<ClayModal.Body className="merge-tags">
						<FrontendDataSet
							apiURL="/o/headless-admin-taxonomy/v1.0/keywords"
							bulkActions={[{}]}
							customRenderers={{
								tableCell: [
									{
										component: ViewsSpaceTableCell,
										name: VIEWS_SPACE_TABLE_CELL_RENDERER_NAME,
										type: 'internal',
									},
								],
							}}
							id="merge"

							// @ts-ignore

							onSelectedItemsChange={(
								selectedItems: React.SetStateAction<{
									selectedItems: any;
								}>
							) => {
								const items = JSON.parse(
									JSON.stringify(selectedItems)
								);

								const setTag: Tag[] = items.map(
									(item: {id: any; name: any}) => ({
										label: item.name,
										value: item.id,
									})
								);

								setSelectedTags(setTag);
							}}
							selectedItemsKey="id"
							selectionType="multiple"
							views={[
								{
									contentRenderer: 'table',
									label: Liferay.Language.get('table'),
									name: 'table',
									schema: {
										fields: [
											{
												fieldName: 'name',
												label: Liferay.Language.get(
													'name'
												),
												sortable: false,
											},
											{
												contentRenderer:
													VIEWS_SPACE_TABLE_CELL_RENDERER_NAME,
												fieldName: 'assetLibraries',
												label: Liferay.Language.get(
													'space'
												),
												sortable: false,
											},
										],
									},
									thumbnail: 'table',
								},
							]}
						/>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									className="btn-cancel"
									displayType="secondary"
									onClick={() => closeModal()}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>

								<ClayButton onClick={() => closeModal()}>
									{Liferay.Language.get('done')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</div>
			</>
		);
	};

	const handleSelectButtonClick = () => {
		openModal({
			contentComponent: ({closeModal}: {closeModal: () => void}) => (
				<SelectTagsDataSetModalContent closeModal={closeModal} />
			),
			height: '70vh',
			size: 'lg',
		});
	};

	return (
		<form onSubmit={handleSubmit}>
			<ClayModal.Header>
				{Liferay.Language.get('merge-tags')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayInput.Group>
					<ClayInput.GroupItem className="categorization-spaces">
						<label htmlFor="multiSelect">
							{Liferay.Language.get('tags')}

							<span className="ml-1 reference-mark">
								<ClayIcon symbol="asterisk" />
							</span>
						</label>

						<ClayMultiSelect
							inputName="multiSelect"
							items={selectedTags}
							loadingState={3}
							onItemsChange={(items: Tag[]) => {
								_handleTagChange(items);
							}}
							sourceItems={tags}
						/>
					</ClayInput.GroupItem>

					<ClayInput.GroupItem className="c-mt-4" shrink>
						<ClayButton
							aria-haspopup="dialog"
							aria-label={Liferay.Language.get('select')}
							displayType="secondary"
							onClick={handleSelectButtonClick}
						>
							{Liferay.Language.get('select')}
						</ClayButton>
					</ClayInput.GroupItem>
				</ClayInput.Group>

				<Form.Group className="c-mt-3">
					<label htmlFor="picker">
						{Liferay.Language.get('into-this-tag')}

						<span className="ml-1 reference-mark">
							<ClayIcon symbol="asterisk" />
						</span>
					</label>

					<ClayInput disabled value={tagName} />
				</Form.Group>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton displayType="primary" type="submit">
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</form>
	);
}
