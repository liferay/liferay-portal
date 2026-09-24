/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import {ClayRadio, ClayRadioGroup} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import React, {useEffect, useState} from 'react';

import TagSelector from '../../../components/TagSelector.es';

export const filterByOptions = [
	{
		filterValue: '',
		label: Liferay.Language.get('none'),
		value: 'none',
	},
	{
		filterValue: 'childMessagesCount eq 0',
		label: Liferay.Language.get('no-answer'),
		value: 'no-answer',
	},
	{
		filterValue: `answered eq false`,
		label: Liferay.Language.get('no-accepted-answer'),
		value: 'no-accepted-answer',
	},

	{
		filterValue: `answered eq true`,
		label: Liferay.Language.get('accepted-answer'),
		value: 'accepted-answer',
	},
];

export const sortedByOptions = [
	{
		label: Liferay.Language.get('newest'),
		sortValue: 'dateCreated:desc',
		value: 'newest',
	},
	{
		label: Liferay.Language.get('oldest'),
		sortValue: 'dateCreated:asc',
		value: 'oldest',
	},
	{
		label: Liferay.Language.get('highest-score'),
		sortValue: 'ratingsStatTotalScore:desc',
		value: 'highest-score',
	},
	{
		label: Liferay.Language.get('most-frequent'),
		sortValue: 'viewCount:desc',
		value: 'most-frequent',
	},
];

export const taggedWithOptions = [
	{
		label: Liferay.Language.get('none'),
		value: 'none',
	},
	{
		label: Liferay.Language.get('my-watched-tags'),
		value: 'my-watched-tags',
	},
	{
		label: Liferay.Language.get('some-specific-tag'),
		value: 'some-specific-tag',
	},
];

const getFilterValues = (form, tags) => {
	const query = {
		creatorId: '',
		filterBy: '',
		resultBar: [],
		sortBy: '',
	};

	const sortOption = sortedByOptions.find(({value}) => value === form.sortBy);

	const filterOption = filterByOptions.find(
		({value}) => value === form.filterBy
	);

	query.filterBy = filterOption?.filterValue;

	if (filterOption?.label) {
		query.resultBar.push({
			label: Liferay.Language.get('filter'),
			value: filterOption?.label,
		});
	}

	if (sortOption?.label) {
		query.resultBar.push({
			label: Liferay.Language.get('sort'),
			value: sortOption?.label,
		});
	}

	query.sortBy = sortOption?.sortValue;

	query.creatorId = form.creatorId;

	if (query.creatorId) {
		const creatorIdFilter = `creatorId eq ${query.creatorId}`;

		query.filterBy =
			query.filterBy === undefined
				? creatorIdFilter
				: `${query.filterBy} and ${creatorIdFilter}`;
	}

	if (form.taggedWith === 'none') {
		return query;
	}

	const _tags = tags.map(({value}) => value);

	if (tags.length) {
		query.resultBar.push({
			label:
				form.taggedWith === 'some-specific-tag'
					? Liferay.Language.get('some-specific-tag')
					: Liferay.Language.get('my-watched-tags'),
			value: _tags.join(', '),
		});

		const filterKeyword = _tags
			.map((value) => `(x eq '${value}')`)
			.join(' or ');

		if (query.filterBy) {
			query.filterBy += ' and ';
		}

		query.filterBy = `${
			query.filterBy ?? ''
		} (keywords/any(x:${filterKeyword}))`;
	}

	query.creatorId = form.creatorId;

	if (query.creatorId) {
		const creatorIdFilter = `creatorId eq ${query.creatorId}`;

		query.filterBy =
			query.filterBy === undefined
				? creatorIdFilter
				: `${query.filterBy} and ${creatorIdFilter}`;
	}

	return query;
};

const initialState = {
	creatorId: '',
	filterBy: 'none',
	selectedTags: [],
	sortBy: 'newest',
	taggedWith: 'none',
};

const TAGS_LIMIT = 3;

const QuestionsFilter = ({onApplyFilter, urlParams}) => {
	const [form, setForm] = useState(initialState);

	useEffect(() => {
		setForm((prevState) => ({
			...prevState,
			filterBy: urlParams.filterBy || initialState.filterBy,
			selectedTags: urlParams.selectedTags ?? [],
			sortBy: urlParams.sortBy || initialState.sortBy,
			taggedWith: urlParams.taggedWith || initialState.taggedWith,
		}));
	}, [urlParams]);

	const onApply = () => {
		onApplyFilter({
			...form,
			selectedTags: form.selectedTags?.map(({value}) => value),
		});
	};

	return (
		<ClayDropDown
			menuElementAttrs={{className: 'management-toolbar-filter-dropdown'}}
			menuHeight="auto"
			trigger={
				<ClayButton
					aria-label={Liferay.Language.get('filter-and-order')}
					displayType="secondary"
				>
					{Liferay.Language.get('filter-and-order')}

					<ClayIcon className="ml-2" symbol="caret-bottom" />
				</ClayButton>
			}
		>
			<div className="mx-3 pl-3 pr-3 py-3">
				<ClayDropDown.ItemList>
					<ClayDropDown.Group>
						<label className="align-items-center d-inline-flex">
							{Liferay.Language.get('filter-by')}
						</label>

						<div className="form-check">
							<ClayRadioGroup
								defaultValue={form.filterBy}
								name="filterBy"
								onChange={(value) => {
									setForm({
										...form,
										filterBy: value,
									});
								}}
								onFocus={null}
								value={form.filterBy}
							>
								{filterByOptions.map(
									({label, value}, index) => (
										<ClayRadio
											aria-label={label}
											key={index}
											label={label}
											value={value}
										/>
									)
								)}
							</ClayRadioGroup>
						</div>
					</ClayDropDown.Group>

					<ClayDropDown.Group>
						<label className="align-items-center d-inline-flex form-check">
							{Liferay.Language.get('sort-by')}
						</label>

						<div className="form-check">
							<ClayRadioGroup
								defaultValue={form.sortBy}
								name="sortBy"
								onChange={(value) => {
									setForm({
										...form,
										sortBy: value,
									});
								}}
								value={form.sortBy}
							>
								{sortedByOptions.map(
									({label, value}, index) => (
										<ClayRadio
											aria-label={label}
											key={index}
											label={label}
											value={value}
										/>
									)
								)}
							</ClayRadioGroup>
						</div>
					</ClayDropDown.Group>

					<ClayDropDown.Group>
						<label className="align-items-center d-inline-flex">
							{Liferay.Language.get('tagged-with')}
						</label>

						<div className="form-check">
							<ClayRadioGroup
								defaultValue={form.taggedWith}
								onChange={(value) =>
									setForm({
										...form,
										taggedWith: value,
									})
								}
								value={form.taggedWith}
							>
								{taggedWithOptions.map(
									({label, value}, index) => (
										<ClayRadio
											aria-label={label}
											key={index}
											label={label}
											value={value}
										/>
									)
								)}
							</ClayRadioGroup>

							{form.taggedWith === 'some-specific-tag' && (
								<TagSelector
									className="c-mt-3"
									showSelectButton={false}
									tags={form.selectedTags}
									tagsChange={(tags) =>
										setForm((prevForm) => ({
											...prevForm,
											selectedTags: tags,
										}))
									}
									tagsLimit={TAGS_LIMIT}
								/>
							)}
						</div>
					</ClayDropDown.Group>
				</ClayDropDown.ItemList>

				<ClayButton
					aria-label={Liferay.Language.get('apply')}
					block
					className="btn btn-primary c-mt-4 c-mt-sm-0"
					onClick={onApply}
					type="button"
				>
					{Liferay.Language.get('apply')}
				</ClayButton>
			</div>
		</ClayDropDown>
	);
};

export {getFilterValues};

export default QuestionsFilter;
