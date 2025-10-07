/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBreadcrumb from '@clayui/breadcrumb';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {Text} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayLabel from '@clayui/label';
import ClayList from '@clayui/list';
import ClayModal from '@clayui/modal';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import {AssetIcon, MimeTypes} from '../AssetIcon';
import {BulkActionItem, BulkActionItemResponse} from './types';
import {fetchUsageAssetData, openDetailedAssetUsageModal} from './utils';

import '../../../../css/components/AssetUsageModals.scss';

type CachedDataItem = {
	active: boolean;
	data: BulkActionItemResponse;
	folderProps: Omit<BulkActionItem, 'attributes'>;
};

interface IAssetUsageListModalProps {
	apiParams: {
		apiURL?: string;
		selectAll: boolean;
	};
	closeModal: () => void;

	/**
	 * Armazena os dados ao navegar por pastas
	 */

	initialCachedData?: CachedDataItem[];

	initialData: BulkActionItemResponse;
	onDelete: () => void;
}

function getModalProps({
	cachedData,
	selectAll,
}: {
	cachedData: CachedDataItem[];
	selectAll: boolean;
}) {
	const root = cachedData[0];
	const rootHasSingleItem = root?.data.totalCount === 1;
	const rootHasSingleFolder =
		typeof root?.data.items[0].attributes.itemsCount === 'number';
	const itemName = root?.data.items[0].name;

	if (rootHasSingleFolder) {
		return {
			deleteButtonLabel: Liferay.Language.get('delete-folder'),
			description: Liferay.Language.get(
				'the-contents-of-this-folder-may-be-used-in-other-assets-or-pages.-deleting-it-will-permanently-remove-all-files-and-subfolders.-do-you-want-to-continue?'
			),
			title: sub(Liferay.Language.get('delete-x'), `"${itemName}"`),
		};
	}
	else if (rootHasSingleItem) {
		return {
			deleteButtonLabel: Liferay.Language.get('delete-entry'),
			description: Liferay.Language.get(
				'this-item-is-being-used-in-other-assets-or-pages-deleting-it-will-break-those-references-and-cause-broken-links-or-missing-content-this-action-cannot-be-undone-are-you-sure-you-want-to-continue'
			),
			title: sub(Liferay.Language.get('delete-x'), `"${itemName}"`),
		};
	}
	else if (selectAll) {
		return {
			deleteButtonLabel: Liferay.Language.get('delete'),
			description: Liferay.Language.get(
				'some-items-are-being-used-in-other-assets-or-pages.-deleting-them-will-break-those-references-and-cause-broken-links-or-missing-content.-this-action-cannot-be-undone.-are-you-sure-you-want-to-continue'
			),
			title: Liferay.Language.get('delete-all-entries'),
		};
	}

	return {
		deleteButtonLabel: Liferay.Language.get('delete'),
		description: Liferay.Language.get(
			'some-items-are-being-used-in-other-assets-or-pages.-deleting-them-will-break-those-references-and-cause-broken-links-or-missing-content.-this-action-cannot-be-undone.-are-you-sure-you-want-to-continue'
		),
		title: Liferay.Language.get('delete-entries'),
	};
}

const AssetUsageListModal: React.FC<IAssetUsageListModalProps> = ({
	apiParams,
	closeModal,
	initialCachedData,
	initialData,
	onDelete,
}) => {
	const [cachedData, setCachedData] = useState<CachedDataItem[]>(
		initialCachedData ?? [
			{
				active: true,
				data: initialData,
				folderProps: {
					classExternalReferenceCode: '',
					className: '',
					classPK: -1,
					name: Liferay.Language.get('selected-asset-list'),
				},
			},
		]
	);

	const [data, setData] = useState<BulkActionItemResponse | null>(
		initialCachedData
			? initialCachedData[initialCachedData.length - 1].data
			: initialData
	);

	const [page, setPage] = useState(1);
	const [pageSize, setPageSize] = useState(20);
	const [search, setSearch] = useState('');

	const inputSearchRef = useRef<HTMLInputElement>(null);
	const dataRef = useRef(data || initialData);
	const cachedDataRef = useRef(cachedData);

	useEffect(() => {
		if (data) {
			dataRef.current = data;
		}
	}, [data]);

	useEffect(() => {
		cachedDataRef.current = cachedData;
	}, [cachedData]);

	async function updateData({
		page,
		pageSize,
		search,
	}: {
		page: number;
		pageSize: number;
		search: string;
	}): Promise<BulkActionItemResponse | null> {
		const currentCachedData = cachedDataRef.current.find(
			({active}) => active
		) as CachedDataItem;
		const root = cachedDataRef.current.length === 1;

		const {data} = await fetchUsageAssetData({
			apiURL: apiParams.apiURL,
			bulkActionItems: root
				? currentCachedData.data.items
				: ([currentCachedData.folderProps] as BulkActionItem[]),
			fetchChildren: !root,
			page,
			pageSize,
			search,
			selectAll: root ? apiParams.selectAll : false,
		});

		return data;
	}

	const handleChangeCachedData = (index: number) => {
		if (index === -1) {
			return;
		}

		const newTrail = cachedData.slice(0, index + 1);

		setCachedData(
			newTrail.map((item, index) => ({
				...item,
				active: index === newTrail.length - 1,
			}))
		);

		const newData = newTrail[newTrail.length - 1].data;

		dataRef.current = newData;

		setData(newData);
		setPage(1);
		setPageSize(20);
		setSearch('');
	};

	const handleClearSearch = async () => {
		const data = await updateData({
			page: 1,
			pageSize: 20,
			search: '',
		});

		setData(data);
		setPage(1);
		setPageSize(20);
		setSearch('');

		inputSearchRef.current!.focus();
	};

	const handleOpenDetailedAssetUsageModal = (item: BulkActionItem) => {
		closeModal();

		openDetailedAssetUsageModal({
			item,
			onClose: () => {
				openModal({
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) => (
						<AssetUsageListModal
							apiParams={apiParams}
							closeModal={closeModal}
							initialCachedData={cachedDataRef.current}
							initialData={dataRef.current}
							onDelete={onDelete}
						/>
					),
					size: 'lg',
					status: 'danger',
				});
			},
		});
	};

	const handleOpenFolder = async (item: BulkActionItem) => {
		const {attributes: _, ...folderProps} = item;

		const {data} = await fetchUsageAssetData({
			bulkActionItems: [item],
			fetchChildren: true,
			page: 1,
			pageSize: 20,
			search: '',
			selectAll: false,
		});

		setData(data);
		setPage(1);
		setPageSize(20);
		setSearch('');

		if (data) {
			dataRef.current = data;

			setCachedData((prev) => [
				...prev.map((data) => ({
					...data,
					active: false,
				})),
				{
					active: true,
					data,
					folderProps,
				},
			]);
		}
	};

	const modalProps = getModalProps({
		cachedData,
		selectAll: apiParams.selectAll,
	});

	const ableToPaginateAndSearch =
		cachedData.length > 1 ||
		(cachedData.length === 1 && cachedData[0].data.totalCount > 1);

	return (
		<ClayTooltipProvider>
			<div className="cms-asset-usage-list-modal">
				<ClayModal.Header>{modalProps.title}</ClayModal.Header>

				<ClayModal.Body>
					{cachedData.length === 1 && (
						<div className="mb-4">
							<Text size={3}>{modalProps.description}</Text>
						</div>
					)}

					{cachedData.length > 1 && (
						<ClayBreadcrumb
							className="mb-4"
							items={cachedData.map((item) => ({
								active: item.active,
								href: `#${item.folderProps.classPK}`,
								label: item.folderProps.name,
							}))}
							onClick={(event) => {
								event.preventDefault();

								const folderId = (
									event.target as HTMLAnchorElement
								).hash.replace('#', '');

								const index = cachedData.findIndex(
									(item) =>
										item.folderProps.classPK ===
										Number(folderId)
								);

								handleChangeCachedData(index);
							}}
						/>
					)}

					{ableToPaginateAndSearch && (
						<ClayForm
							onSubmit={async (event) => {
								event.preventDefault();

								const data = await updateData({
									page: 1,
									pageSize: 20,
									search,
								});

								setData(data);
								setPage(1);
								setPageSize(20);
								setSearch(search);
							}}
						>
							<ClayInput.Group className="mb-4">
								<ClayInput.GroupItem>
									<ClayInput
										aria-label={Liferay.Language.get(
											'search'
										)}
										className="form-control input-group-inset input-group-inset-after"
										name="search"
										onChange={(event) => {
											setSearch(event.target.value);
										}}
										placeholder={Liferay.Language.get(
											'search'
										)}
										ref={inputSearchRef}
										sizing="lg"
										type="text"
										value={search}
									/>

									<ClayInput.GroupInsetItem after tag="span">
										{!!search && (
											<ClayButtonWithIcon
												aria-label={Liferay.Language.get(
													'clear'
												)}
												displayType="unstyled"
												onClick={handleClearSearch}
												symbol="times-small"
											/>
										)}

										<ClayButtonWithIcon
											aria-label={Liferay.Language.get(
												'search'
											)}
											displayType="unstyled"
											symbol="search"
											type="submit"
										/>
									</ClayInput.GroupInsetItem>
								</ClayInput.GroupItem>
							</ClayInput.Group>
						</ClayForm>
					)}

					{!(data?.totalCount ?? 0) && (
						<ClayEmptyState
							className="mb-6 mt-0"
							description={Liferay.Language.get(
								'review-your-search-and-try-again'
							)}
							imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/search_state.svg`}
							imgSrcReducedMotion={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/search_state_reduced_motion.svg`}
							title={Liferay.Language.get('no-results-found')}
						>
							<ClayButton
								displayType="secondary"
								onClick={handleClearSearch}
							>
								{Liferay.Language.get('clear-search')}
							</ClayButton>
						</ClayEmptyState>
					)}

					<ClayList>
						{data?.items.map((item) => (
							<ClayList.Item flex key={item.classPK}>
								<ClayList.ItemField>
									<AssetIcon
										mimeType={
											item.attributes.type === 'FOLDER'
												? MimeTypes.Folder
												: item.attributes.mimeType
										}
									/>
								</ClayList.ItemField>

								<ClayList.ItemField expand>
									<ClayList.ItemTitle>
										{item.name}
									</ClayList.ItemTitle>

									{item.attributes.type === 'ASSET' && (
										<>
											<ClayList.ItemText>
												{sub(
													Liferay.Language.get(
														'x-usages'
													),
													[item.attributes.usages]
												)}
											</ClayList.ItemText>

											<ClayList.ItemText>
												<ClayLabel
													displayType={
														item.attributes
															.deletionType ===
														'PERMANENT_DELETION'
															? 'danger'
															: 'secondary'
													}
												>
													{item.attributes
														.deletionType ===
													'PERMANENT_DELETION'
														? Liferay.Language.get(
																'permanent-deletion'
															)
														: Liferay.Language.get(
																'recycle-bin'
															)}
												</ClayLabel>
											</ClayList.ItemText>
										</>
									)}
								</ClayList.ItemField>

								{item.attributes.type === 'FOLDER' && (
									<ClayList.ItemField>
										<ClayButtonWithIcon
											aria-label={
												item.attributes.itemsCount
													? Liferay.Language.get(
															'view-asset-list'
														)
													: Liferay.Language.get(
															'empty-folder'
														)
											}
											className="border-0"
											disabled={
												!item.attributes.itemsCount
											}
											displayType="secondary"
											onClick={async () => {
												handleOpenFolder(item);
											}}
											symbol="forms"
											title={
												item.attributes.itemsCount
													? Liferay.Language.get(
															'view-asset-list'
														)
													: Liferay.Language.get(
															'empty-folder'
														)
											}
										/>
									</ClayList.ItemField>
								)}

								{item.attributes.type === 'ASSET' && (
									<ClayList.ItemField>
										<ClayButtonWithIcon
											aria-label={
												item.attributes.usages
													? Liferay.Language.get(
															'view-usages'
														)
													: Liferay.Language.get(
															'no-usages'
														)
											}
											className="border-0"
											disabled={!item.attributes.usages}
											displayType="secondary"
											onClick={() =>
												handleOpenDetailedAssetUsageModal(
													item
												)
											}
											symbol="list-ul"
											title={
												item.attributes.usages
													? Liferay.Language.get(
															'view-usages'
														)
													: Liferay.Language.get(
															'no-usages'
														)
											}
										/>
									</ClayList.ItemField>
								)}
							</ClayList.Item>
						))}
					</ClayList>

					{ableToPaginateAndSearch && (
						<ClayPaginationBarWithBasicItems
							active={page}
							activeDelta={pageSize}
							deltas={[20, 40, 60].map((size) => ({label: size}))}
							disableEllipsis={
								(data?.totalCount ?? 0) / pageSize - 5 > 999
							}
							ellipsisBuffer={3}
							onActiveChange={async (newPage: number) => {
								if (
									data &&
									newPage >= 1 &&
									newPage <= data.lastPage
								) {
									const data = await updateData({
										page: newPage,
										pageSize,
										search,
									});

									setData(data);
									setPage(newPage);
									setPageSize(pageSize);
									setSearch(search);
								}
							}}
							onDeltaChange={async (pageSize) => {
								const data = await updateData({
									page: 1,
									pageSize,
									search,
								});

								setData(data);
								setPage(1);
								setPageSize(pageSize);
								setSearch(search);
							}}
							showDeltasDropDown
							totalItems={data?.totalCount ?? 0}
						/>
					)}
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								borderless={cachedData.length > 1}
								displayType="secondary"
								onClick={closeModal}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							{cachedData.length === 1 ? (
								<ClayButton
									displayType="danger"
									onClick={async () => {
										closeModal();

										onDelete();
									}}
								>
									{modalProps.deleteButtonLabel}
								</ClayButton>
							) : (
								<ClayButton
									displayType="secondary"
									onClick={() => {
										handleChangeCachedData(
											cachedData.length - 2
										);
									}}
								>
									{Liferay.Language.get('go-back')}
								</ClayButton>
							)}
						</ClayButton.Group>
					}
				/>
			</div>
		</ClayTooltipProvider>
	);
};

export {AssetUsageListModal};
