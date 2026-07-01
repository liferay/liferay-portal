/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import {ClayCheckbox, ClayInput, ClaySelect} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import ClayTable from '@clayui/table';
import React, {useEffect, useMemo, useState} from 'react';

import GenerateProgress from '../components/GenerateProgress';
import StepActions from '../components/StepActions';
import SummaryStats from '../components/SummaryStats';
import {getGenerationPages} from '../services/generations';
import {getSiteByExternalReferenceCode} from '../services/sites';
import {buildDetectedConfig, getLanguageLabel} from '../util/contentModel';

import type {SummaryStat} from '../types/ContentModel';
import type {GeneratedPage} from '../types/GeneratedPage';
import type {Generation} from '../types/Generation';
import type {GenerationItem} from '../types/GenerationItem';

interface IProps {
	error?: string;
	generation: Generation;
	items: GenerationItem[];
	onBack: () => void;
	onCancel: () => void;
	onPublish: () => void;
	publishing: boolean;
}

export default function ReviewStep({
	error,
	generation,
	items,
	onBack,
	onCancel,
	onPublish,
	publishing,
}: IProps) {
	const [filterType, setFilterType] = useState('');
	const [loadError, setLoadError] = useState(false);
	const [order, setOrder] = useState('title-asc');
	const [page, setPage] = useState(1);
	const [pages, setPages] = useState<GeneratedPage[]>([]);
	const [pageSize, setPageSize] = useState(20);
	const [search, setSearch] = useState('');
	const [selectedIds, setSelectedIds] = useState<Set<string>>(new Set());
	const [siteFriendlyURL, setSiteFriendlyURL] = useState<string | null>(null);
	const [urlColumnVisible, setUrlColumnVisible] = useState(true);

	const committed = generation.generationStatus.key === 'committed';

	const generating = generation.generationStatus.key === 'generating';

	useEffect(() => {
		let cancelled = false;

		getGenerationPages(items)
			.then((newPages) => {
				if (!cancelled) {
					setPages(newPages);
				}
			})
			.catch(() => {
				if (!cancelled) {
					setLoadError(true);
				}
			});

		return () => {
			cancelled = true;
		};
	}, [items]);

	useEffect(() => {
		if (!committed || !generation.generatedSiteERC) {
			return;
		}

		let cancelled = false;

		getSiteByExternalReferenceCode(generation.generatedSiteERC)
			.then((site) => {
				if (!cancelled && site && site.friendlyUrlPath) {
					setSiteFriendlyURL(site.friendlyUrlPath);
				}
			})
			.catch(() => {});

		return () => {
			cancelled = true;
		};
	}, [committed, generation.generatedSiteERC]);

	const reviewStats = useMemo<SummaryStat[]>(() => {
		const detectedConfig = buildDetectedConfig(generation, items);

		return [
			{
				icon: 'list',
				label: Liferay.Language.get('total-items'),
				value: items.reduce(
					(sum, item) => sum + (item.itemCount ?? 0),
					0
				),
			},
			{
				icon: 'automatic-translate',
				label: Liferay.Language.get('languages'),
				value: detectedConfig.languageLabels.length,
			},
			{
				icon: 'flag-full',
				label: Liferay.Language.get('status'),
				value: committed
					? generation.generationStatus.name ||
						Liferay.Language.get('published')
					: Liferay.Language.get('draft'),
			},
		];
	}, [committed, generation, items]);

	const templateLabels = useMemo(
		() => [
			...new Set(
				pages.map((generatedPage) => generatedPage.templateLabel)
			),
		],
		[pages]
	);

	const visiblePages = useMemo(() => {
		const searchText = search.trim().toLowerCase();

		const filtered = pages.filter((generatedPage) => {
			if (filterType && generatedPage.templateLabel !== filterType) {
				return false;
			}

			if (
				searchText &&
				!generatedPage.title.toLowerCase().includes(searchText) &&
				!(generatedPage.url ?? '').toLowerCase().includes(searchText)
			) {
				return false;
			}

			return true;
		});

		return [...filtered].sort((a, b) => {
			const comparison = a.title.localeCompare(b.title);

			return order === 'title-desc' ? -comparison : comparison;
		});
	}, [pages, search, filterType, order]);

	const pageItems = visiblePages.slice(
		(page - 1) * pageSize,
		page * pageSize
	);

	const hasURL = pages.some((generatedPage) => generatedPage.url);

	const showURL = hasURL && urlColumnVisible;

	const allSelected =
		!!visiblePages.length &&
		visiblePages.every((generatedPage) =>
			selectedIds.has(generatedPage.id)
		);

	const getPageHref = (generatedPage: GeneratedPage) =>
		committed && siteFriendlyURL && generatedPage.url?.startsWith('/')
			? `/web${siteFriendlyURL}${generatedPage.url}`
			: null;

	const toggleAll = () => {
		if (allSelected) {
			setSelectedIds(new Set());
		}
		else {
			setSelectedIds(
				new Set(visiblePages.map((generatedPage) => generatedPage.id))
			);
		}
	};

	const toggleOne = (id: string) => {
		const newSelectedIds = new Set(selectedIds);

		if (newSelectedIds.has(id)) {
			newSelectedIds.delete(id);
		}
		else {
			newSelectedIds.add(id);
		}

		setSelectedIds(newSelectedIds);
	};

	const openSelected = () => {
		for (const generatedPage of visiblePages) {
			if (!selectedIds.has(generatedPage.id)) {
				continue;
			}

			const href = getPageHref(generatedPage);

			if (href) {
				window.open(href, '_blank');
			}
		}
	};

	return (
		<div className="content-site-generator__review">
			{generating ? (
				<GenerateProgress generation={generation} items={items} />
			) : (
				<>
					<div className="content-site-generator__refine-header">
						<h3>{Liferay.Language.get('review-and-publish')}</h3>

						<p className="text-secondary">
							{Liferay.Language.get(
								'review-the-generated-content-before-publishing-it-to-your-site'
							)}
						</p>
					</div>

					{error && (
						<ClayAlert
							displayType="danger"
							title={Liferay.Language.get('error')}
						>
							{error}
						</ClayAlert>
					)}

					{committed && (
						<ClayAlert
							displayType="success"
							title={Liferay.Language.get('success')}
						>
							{Liferay.Language.get(
								'the-generated-content-was-published'
							)}
						</ClayAlert>
					)}

					<SummaryStats stats={reviewStats} />

					<div className="content-site-generator__review-toolbar">
						<ClaySelect
							aria-label={Liferay.Language.get('filter')}
							className="content-site-generator__review-filter"
							onChange={(event) => {
								setPage(1);
								setFilterType(event.target.value);
							}}
							value={filterType}
						>
							<ClaySelect.Option
								label={Liferay.Language.get('all-types')}
								value=""
							/>

							{templateLabels.map((label) => (
								<ClaySelect.Option
									key={label}
									label={label}
									value={label}
								/>
							))}
						</ClaySelect>

						<ClaySelect
							aria-label={Liferay.Language.get('order')}
							className="content-site-generator__review-order"
							onChange={(event) => setOrder(event.target.value)}
							value={order}
						>
							<ClaySelect.Option
								label={Liferay.Language.get('ascending')}
								value="title-asc"
							/>

							<ClaySelect.Option
								label={Liferay.Language.get('descending')}
								value="title-desc"
							/>
						</ClaySelect>

						<ClayInput.Group className="content-site-generator__review-search">
							<ClayInput.GroupItem>
								<ClayInput
									aria-label={Liferay.Language.get('search')}
									insetBefore
									onChange={(event) => {
										setPage(1);
										setSearch(event.target.value);
									}}
									placeholder={Liferay.Language.get('search')}
									type="text"
									value={search}
								/>

								<ClayInput.GroupInsetItem before tag="span">
									<ClayIcon
										spritemap={Liferay.Icons.spritemap}
										symbol="search"
									/>
								</ClayInput.GroupInsetItem>
							</ClayInput.GroupItem>
						</ClayInput.Group>
					</div>

					{!!selectedIds.size && (
						<div className="content-site-generator__review-selection">
							<span>
								{Liferay.Util.sub(
									Liferay.Language.get('x-selected'),
									String(selectedIds.size)
								)}
							</span>

							<ClayButton
								disabled={!committed || !siteFriendlyURL}
								displayType="secondary"
								onClick={openSelected}
								small
							>
								{Liferay.Language.get('open-selected')}
							</ClayButton>

							<ClayButton
								displayType="unstyled"
								onClick={() => setSelectedIds(new Set())}
								small
							>
								{Liferay.Language.get('clear')}
							</ClayButton>
						</div>
					)}

					{!visiblePages.length ? (
						<p className="text-secondary">
							{loadError
								? Liferay.Language.get('unable-to-load-content')
								: search.trim() || filterType
									? Liferay.Language.get(
											'no-results-were-found'
										)
									: Liferay.Language.get('no-results-found')}
						</p>
					) : (
						<>
							<ClayTable className="content-site-generator__review-table">
								<ClayTable.Head>
									<ClayTable.Row>
										<ClayTable.Cell headingCell>
											<ClayCheckbox
												aria-label={Liferay.Language.get(
													'select-all'
												)}
												checked={allSelected}
												onChange={toggleAll}
											/>
										</ClayTable.Cell>

										<ClayTable.Cell expanded headingCell>
											{Liferay.Language.get('title')}
										</ClayTable.Cell>

										<ClayTable.Cell headingCell>
											{Liferay.Language.get('language')}
										</ClayTable.Cell>

										<ClayTable.Cell headingCell>
											{Liferay.Language.get('items')}
										</ClayTable.Cell>

										{showURL && (
											<ClayTable.Cell headingCell>
												{Liferay.Language.get('url')}
											</ClayTable.Cell>
										)}

										<ClayTable.Cell headingCell>
											<ClayDropDown
												trigger={
													<ClayButtonWithIcon
														aria-label={Liferay.Language.get(
															'columns'
														)}
														displayType="unstyled"
														size="sm"
														spritemap={
															Liferay.Icons
																.spritemap
														}
														symbol="caret-bottom"
													/>
												}
											>
												<ClayDropDown.ItemList>
													<ClayDropDown.Item
														active={
															urlColumnVisible
														}
														disabled={!hasURL}
														onClick={() =>
															setUrlColumnVisible(
																(visible) =>
																	!visible
															)
														}
													>
														{Liferay.Language.get(
															'url'
														)}
													</ClayDropDown.Item>
												</ClayDropDown.ItemList>
											</ClayDropDown>
										</ClayTable.Cell>
									</ClayTable.Row>
								</ClayTable.Head>

								<ClayTable.Body>
									{pageItems.map((generatedPage) => {
										const href = getPageHref(generatedPage);

										return (
											<ClayTable.Row
												key={generatedPage.id}
											>
												<ClayTable.Cell>
													<ClayCheckbox
														aria-label={Liferay.Util.sub(
															Liferay.Language.get(
																'select-x'
															),
															generatedPage.title
														)}
														checked={selectedIds.has(
															generatedPage.id
														)}
														onChange={() =>
															toggleOne(
																generatedPage.id
															)
														}
													/>
												</ClayTable.Cell>

												<ClayTable.Cell expanded>
													<span className="content-site-generator__review-title">
														<ClayIcon
															className="mr-2 text-secondary"
															spritemap={
																Liferay.Icons
																	.spritemap
															}
															symbol={
																generatedPage.icon
															}
														/>

														{generatedPage.title}
													</span>
												</ClayTable.Cell>

												<ClayTable.Cell>
													{generatedPage.languages
														.map(getLanguageLabel)
														.join(', ')}
												</ClayTable.Cell>

												<ClayTable.Cell>
													{generatedPage.itemCount}
												</ClayTable.Cell>

												{showURL && (
													<ClayTable.Cell>
														<span className="content-site-generator__review-url">
															{generatedPage.url ??
																''}
														</span>
													</ClayTable.Cell>
												)}

												<ClayTable.Cell>
													<ClayDropDown
														trigger={
															<ClayButtonWithIcon
																aria-label={Liferay.Util.sub(
																	Liferay.Language.get(
																		'actions-for-x'
																	),
																	generatedPage.title
																)}
																displayType="unstyled"
																size="sm"
																spritemap={
																	Liferay
																		.Icons
																		.spritemap
																}
																symbol="ellipsis-v"
															/>
														}
													>
														<ClayDropDown.ItemList>
															<ClayDropDown.Item
																disabled={!href}
																onClick={() => {
																	if (href) {
																		window.open(
																			href,
																			'_blank'
																		);
																	}
																}}
															>
																{Liferay.Language.get(
																	'open-page'
																)}
															</ClayDropDown.Item>
														</ClayDropDown.ItemList>
													</ClayDropDown>
												</ClayTable.Cell>
											</ClayTable.Row>
										);
									})}
								</ClayTable.Body>
							</ClayTable>

							<ClayPaginationBarWithBasicItems
								active={page}
								activeDelta={pageSize}
								ellipsisBuffer={3}
								onActiveChange={setPage}
								onDeltaChange={(delta) => {
									setPage(1);
									setPageSize(delta);
								}}
								spritemap={Liferay.Icons.spritemap}
								totalItems={visiblePages.length}
							/>
						</>
					)}
				</>
			)}

			<StepActions
				backLabel={Liferay.Language.get('back')}
				continueDisabled={committed || generating}
				continueLabel={Liferay.Language.get('publish')}
				continueLoading={publishing}
				onBack={onBack}
				onCancel={onCancel}
				onContinue={onPublish}
			>
				{committed && siteFriendlyURL && (
					<ClayButton
						displayType="secondary"
						onClick={() =>
							window.open(`/web${siteFriendlyURL}`, '_blank')
						}
					>
						{Liferay.Language.get('view-site')}
					</ClayButton>
				)}
			</StepActions>
		</div>
	);
}
