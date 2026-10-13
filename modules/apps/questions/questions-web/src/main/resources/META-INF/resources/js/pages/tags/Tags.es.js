/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import {ClayInput, ClaySelect} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React from 'react';

import Alert from '../../components/Alert.es';
import PaginatedList from '../../components/PaginatedList.es';
import TagsLayout from '../../components/TagsLayout.es';
import useTags from '../../hooks/useTags.es';
import {withRouter} from '../../hooks/withRouter.es';

export default withRouter(({history, location}) => {
	const {
		changePage,
		context,
		debounceCallback,
		error,
		loading,
		orderBy,
		orderByOptions,
		page,
		pageSize,
		search,
		searchBoxValue,
		setLoading,
		setOrderBy,
		setSearchBoxValue,
		tags,
	} = useTags({history, location});

	const linkTagPage = location.pathname.split('/')[1];

	return (
		<>
			<div className="container">
				<div className="d-flex flex-row">
					<div className="d-flex flex-column flex-grow-1">
						<ClayInput.Group className="c-mt-3 justify-content-end">
							<ClayInput.GroupItem shrink>
								<label
									className="align-items-center d-inline-flex m-0 text-secondary"
									htmlFor="tagsOrderBy"
								>
									{Liferay.Language.get('order-by')}
								</label>
							</ClayInput.GroupItem>

							<ClayInput.GroupItem shrink>
								<ClaySelect
									className="bg-transparent border-0"
									disabled={loading}
									id="tagsOrderBy"
									onChange={(event) => {
										setLoading(true);
										setOrderBy(event.target.value);
									}}
									value={orderBy}
								>
									{orderByOptions.map((option) => (
										<ClaySelect.Option
											key={option.value}
											label={option.label}
											value={option.value}
										/>
									))}
								</ClaySelect>
							</ClayInput.GroupItem>
						</ClayInput.Group>
					</div>

					<div className="d-flex flex-column flex-grow-0">
						<ClayInput.Group className="c-mt-3">
							<ClayInput.GroupItem>
								<ClayInput
									className="bg-transparent form-control input-group-inset input-group-inset-after"
									disabled={
										!search &&
										tags &&
										tags.items &&
										!tags.items.length
									}
									onChange={(event) => {
										setSearchBoxValue(event.target.value);
										debounceCallback(event.target.value);
									}}
									placeholder={Liferay.Language.get('search')}
									type="text"
									value={searchBoxValue}
								/>

								<ClayInput.GroupInsetItem
									after
									className="bg-transparent"
									tag="span"
								>
									{loading && <ClayLoadingIndicator small />}

									{!loading &&
										((!!search && (
											<ClayButtonWithIcon
												aria-label={Liferay.Language.get(
													'clear'
												)}
												displayType="unstyled"
												onClick={() => {
													debounceCallback('');
												}}
												symbol="times-circle"
												type="submit"
											/>
										)) || (
											<ClayButtonWithIcon
												aria-label={Liferay.Language.get(
													'search'
												)}
												displayType="unstyled"
												symbol="search"
												type="search"
											/>
										))}
								</ClayInput.GroupInsetItem>
							</ClayInput.GroupItem>
						</ClayInput.Group>
					</div>
				</div>

				<div className="c-mt-3 row">
					<PaginatedList
						activeDelta={pageSize}
						activePage={page}
						changeDelta={(pageSize) =>
							changePage(search, page, pageSize)
						}
						changePage={(page) =>
							changePage(search, page, pageSize)
						}
						data={tags}
						emptyState={
							<ClayEmptyState
								aria-label={Liferay.Language.get(
									'there-are-no-results'
								)}
								description={Liferay.Language.get(
									'sorry,-no-results-were-found'
								)}
								image={
									<ClayIcon
										className="c-empty-state-icon c-empty-state-icon-empty-state"
										illustration
										symbol="empty-state"
									/>
								}
								title={Liferay.Language.get(
									'there-are-no-results'
								)}
							/>
						}
						loading={loading}
					>
						{(tag) => (
							<div className="col-md-4" key={tag.id}>
								<TagsLayout
									context={context.siteKey}
									linkPage={linkTagPage}
									orderBy={orderBy}
									page={page}
									pageSize={pageSize}
									search={search}
									tag={tag}
								/>
							</div>
						)}
					</PaginatedList>

					<Alert info={error} />
				</div>
			</div>
		</>
	);
});
