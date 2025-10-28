/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import Form, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import ClayMultiSelect from '@clayui/multi-select';
import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import {useFormik} from 'formik';
import {openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import SpaceSticker from '../../../common/components/SpaceSticker';
import ApiHelper from '../../../common/services/ApiHelper';
import {LogoColor} from '../../../common/types/Space';
import {executeAsyncItemAction} from '../../props_transformer/utils/executeAsyncItemAction';

type Tag = {
	label: string;
	value: any;
};

export default function MergeTagsModalContent({
	closeModal,
	cmsGroupId,
	loadData,
	selectIntoTags,
}: {
	closeModal: () => void;
	cmsGroupId: number;
	loadData: () => {};
	selectIntoTags: Tag[];
}) {
	const [tags, setTags] = useState<Tag[]>([]);
	const [currentTag, setCurrentTag] = useState(selectIntoTags[0]);
	const [selectedTags, setSelectedTags] = useState<Tag[]>([]);

	useEffect(() => {
		const getTags = async () => {
			const {data} = await ApiHelper.get<{items: any[]}>(
				`/o/headless-admin-taxonomy/v1.0/sites/${cmsGroupId}/keywords`
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
					(tag: Tag) =>
						tag.value === currentTag.value &&
						tag.label === currentTag.label
				);

				if (selectedTag) {
					setSelectedTags([selectedTag]);
				}
			}
		};

		getTags();
	}, [cmsGroupId, currentTag]);

    const _getConfirmationMessage = () => {
        const tagNames = '"' + selectedTags.map((item) => item.label).join(', ') + '"';
        const intoTagName = '"' + Liferay.Util.escapeHTML(currentTag.label) + '"';

        return sub(
            Liferay.Language.get(
                'are-you-sure-you-want-to-merge-x-into-x.-x-will-be-available-in-x'
            ),
            `<strong>${tagNames}</strong>`,
            `<strong>${intoTagName}</strong>`,
            `<strong>"${Liferay.Language.get('all-spaces')}"</strong>`
        );
    };

	const _handleTagChange = (items: Tag[]) => {
		setSelectedTags(tags.filter((item) => items.includes(item)));
	};

	const mergeTags = (tag: Tag) => {
		const params = new URLSearchParams();

		for (const item of selectedTags) {
			if (Number(item.value) === Number(tag.value)) {
				continue;
			}
			params.append('fromKeywordIds', item.value);
		}

		const url = `/o/headless-admin-taxonomy/v1.0/keywords/${tag.value}/merge?${params}`;

		executeAsyncItemAction({
			method: 'PUT',
			refreshData: loadData,
			successMessage: sub(
				Liferay.Language.get('x-and-x-have-been-successfully-merged'),
				selectedTags
					.filter((item) => item.label !== currentTag.label)
					.map((item) => item.label)
					.join(', '),
				`${Liferay.Util.escapeHTML(currentTag.label)}`
			),
			url,
		});

		closeModal();
	};

	const {handleSubmit} = useFormik({
		initialValues: {
			currentTag,
		},
		onSubmit: (values) => {
			const mergeModel = document.querySelector(
				'#mergeModal .modal-dialog'
			);
			mergeModel?.setAttribute('hidden', 'true');

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
					onClose: () => {
						mergeModel?.removeAttribute('hidden');
					},
					status: 'warning',
					title: Liferay.Language.get('merge-tags'),
				});

				return;
			}

			openModal({
				bodyHTML: _getConfirmationMessage(),
				buttons: [
					{
						autoFocus: true,
						displayType: 'secondary',
						label: Liferay.Language.get('cancel'),
						type: 'cancel',
					},
					{
						displayType: 'warning',
						label: Liferay.Language.get('save'),
						onClick: ({processClose}: {processClose: Function}) => {
							processClose();

							mergeTags(values.currentTag);
						},
					},
				],
				onClose: () => {
					mergeModel?.removeAttribute('hidden');
				},
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
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{Liferay.Language.get('merge-tags')}
					</ClayModal.Header>

					<ClayModal.Body className="merge-tags">
						<FrontendDataSet
							apiURL={`/o/headless-admin-taxonomy/v1.0/sites/${cmsGroupId}/keywords`}
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
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
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
							aria-label="multiSelect"
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
					<label>
						{Liferay.Language.get('into-this-tag')}

						<span className="ml-1 reference-mark">
							<ClayIcon symbol="asterisk" />
						</span>
					</label>

					<ClaySelectWithOption
						onChange={(event) =>
							setCurrentTag(event.target.dataset as Tag)
						}
						options={selectIntoTags}
						value={currentTag.label}
					/>
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
