/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {
	AssetTagsSelector,
	AssetVocabularyCategoriesSelector,
} from 'asset-taglib';
import PropTypes from 'prop-types';
import React, {useCallback, useState} from 'react';

const DEFAULT_RULE = {
	queryContains: true,
	type: 'assetTags',
};

const QUERY_AND_OPERATOR_OPTIONS = [
	{
		label: Liferay.Language.get('all'),
		value: true,
	},
	{
		label: Liferay.Language.get('any'),
		value: false,
	},
];

const QUERY_CONTAINS_OPTIONS = [
	{
		label: Liferay.Language.get('contains'),
		value: true,
	},
	{
		label: Liferay.Language.get('does-not-contain'),
		value: false,
	},
];

const RULE_TYPE_OPTIONS = [
	{
		label: Liferay.Language.get('categories'),
		value: 'assetCategories',
	},
	{
		label: Liferay.Language.get('keywords'),
		value: 'keywords',
	},
	{
		label: Liferay.Language.get('tags'),
		value: 'assetTags',
	},
];

function AssetCategories({
	categorySelectorURL,
	groupIds,
	index,
	namespace,
	rule,
	vocabularyIds,
}) {
	const [selectedItems, setSelectedItems] = useState(
		rule.selectedItems || []
	);

	return (
		<div className="d-inline-block">
			<label htmlFor={`${namespace}queryCategoryIds${index}`}>
				{Liferay.Language.get('categories')}
			</label>

			<AssetVocabularyCategoriesSelector
				categoryIds={rule.queryValues ? rule.queryValues : ''}
				eventName={`${namespace}selectCategory`}
				formGroupClassName="mb-0"
				groupIds={groupIds}
				inputName={`${namespace}queryCategoryIds${index}`}
				onSelectedItemsChange={setSelectedItems}
				portletURL={categorySelectorURL}
				selectedItems={selectedItems}
				sourceItemsVocabularyIds={vocabularyIds}
			/>
		</div>
	);
}

function AssetTags({groupIds, index, namespace, rule, tagSelectorURL}) {
	const [inputValue, setInputValue] = useState('');
	const [selectedItems, setSelectedItems] = useState(rule.selectedItems);

	return (
		<AssetTagsSelector
			eventName={`${namespace}selectTag`}
			formGroupClassName="mb-0"
			groupIds={groupIds}
			inputName={`${namespace}queryTagNames${index}`}
			inputValue={inputValue}
			onInputValueChange={setInputValue}
			onSelectedItemsChange={setSelectedItems}
			portletURL={tagSelectorURL}
			selectedItems={selectedItems}
			showSelectButton={true}
			showSubtitle={false}
			tagNames={rule.queryValues ? rule.queryValues : ''}
		/>
	);
}

function Keywords({index, namespace, onChange, rule}) {
	return (
		<div className="d-inline-block">
			<label htmlFor={`${namespace}keywords${index}`}>
				{Liferay.Language.get('keywords')}
			</label>

			<ClayInput
				className="asset-query-keywords"
				data-index={index}
				data-item-index={index}
				data-property="queryValues"
				id={`${namespace}keywords${index}`}
				name={`${namespace}keywords${index}`}
				onChange={onChange}
				type="text"
				value={rule.queryValues}
			/>
		</div>
	);
}

function Rule({
	categorySelectorURL,
	groupIds,
	index,
	namespace,
	onDeleteRule,
	onRuleChange,
	rule,
	tagSelectorURL,
	vocabularyIds,
}) {
	return (
		<div className="align-items-baseline c-gap-3 d-flex">
			<div className="panel panel-default">
				<div className="align-items-baseline c-gap-3 d-flex mb-0 panel-body">
					<ClayForm.Group>
						<ClaySelectWithOption
							aria-label={Liferay.Language.get('query-contains')}
							data-index={index}
							data-property="queryContains"
							id={`${namespace}queryContains${index}`}
							name={`${namespace}queryContains${index}`}
							onChange={onRuleChange}
							options={QUERY_CONTAINS_OPTIONS}
							title={Liferay.Language.get('query-contains')}
							value={rule.queryContains}
						/>
					</ClayForm.Group>

					<ClayForm.Group>
						<ClaySelectWithOption
							aria-label={Liferay.Language.get('and-operator')}
							data-index={index}
							data-property="queryAndOperator"
							id={`${namespace}queryAndOperator${index}`}
							name={`${namespace}queryAndOperator${index}`}
							onChange={onRuleChange}
							options={QUERY_AND_OPERATOR_OPTIONS}
							title={Liferay.Language.get('and-operator')}
							value={rule.queryAndOperator}
						/>
					</ClayForm.Group>

					<ClayForm.Group>
						<label
							className="control-label"
							htmlFor={`${namespace}queryName${index}`}
						>
							{Liferay.Language.get('of-the-following')}
						</label>
					</ClayForm.Group>

					<ClayForm.Group>
						<ClaySelectWithOption
							data-index={index}
							data-property="type"
							id={`${namespace}queryName${index}`}
							name={`${namespace}queryName${index}`}
							onChange={onRuleChange}
							options={RULE_TYPE_OPTIONS}
							value={rule.type}
						/>
					</ClayForm.Group>

					{rule.type === 'assetCategories' && (
						<ClayForm.Group>
							<AssetCategories
								categorySelectorURL={categorySelectorURL}
								groupIds={groupIds}
								index={index}
								namespace={namespace}
								rule={rule}
								vocabularyIds={vocabularyIds}
							/>
						</ClayForm.Group>
					)}

					{rule.type === 'assetTags' && (
						<ClayForm.Group>
							<AssetTags
								groupIds={groupIds}
								index={index}
								namespace={namespace}
								rule={rule}
								tagSelectorURL={tagSelectorURL}
							/>
						</ClayForm.Group>
					)}

					{rule.type === 'keywords' && (
						<ClayForm.Group>
							<Keywords
								index={index}
								namespace={namespace}
								onChange={onRuleChange}
								rule={rule}
							/>
						</ClayForm.Group>
					)}

					<div className="ml-0 timeline-increment">
						<span className="timeline-icon"></span>
					</div>
				</div>
			</div>

			{index > 0 && (
				<ClayButton
					aria-label={Liferay.Language.get('delete-condition')}
					borderless
					className="asset-filter-builder-remove container-trash"
					data-index={index}
					displayType="secondary"
					monospaced
					onClick={onDeleteRule}
					title={Liferay.Language.get('delete-condition')}
				>
					<ClayIcon symbol="trash" />
				</ClayButton>
			)}
		</div>
	);
}

function AssetFilterBuilder({
	categorySelectorURL,
	groupIds,
	namespace,
	rules,
	tagSelectorURL,
	vocabularyIds,
}) {
	const [currentRules, setCurrentRules] = useState(rules);

	const handleAddRule = useCallback(() => {
		setCurrentRules([...currentRules, DEFAULT_RULE]);
	}, [currentRules]);

	const handleDeleteRule = useCallback(
		(event) => {
			const index = parseInt(event.currentTarget.dataset.index, 10);

			setCurrentRules([
				...currentRules.slice(0, index),
				...currentRules.slice(index + 1, currentRules.length),
			]);
		},

		[currentRules]
	);

	const handleRuleChange = useCallback(
		(event) => {
			const index = parseInt(event.currentTarget.dataset.index, 10);
			const property = event.currentTarget.dataset.property;

			const rule =
				property === 'type'
					? {queryContains: true}
					: currentRules[index];

			setCurrentRules([
				...currentRules.slice(0, index),
				{
					...rule,
					[property]: event.currentTarget.value,
				},
				...currentRules.slice(index + 1, currentRules.length),
			]);
		},
		[currentRules]
	);

	return (
		<>
			<ClayInput
				name={`${namespace}queryLogicIndexes`}
				type="hidden"
				value={Object.keys(currentRules).toString()}
			/>

			<ul className="timeline">
				{currentRules.map((rule, index) => (
					<li className="timeline-item" key={index}>
						<Rule
							categorySelectorURL={categorySelectorURL}
							groupIds={groupIds}
							index={index}
							namespace={namespace}
							onDeleteRule={handleDeleteRule}
							onRuleChange={handleRuleChange}
							rule={rule}
							tagSelectorURL={tagSelectorURL}
							vocabularyIds={vocabularyIds}
						/>
					</li>
				))}

				<li className="timeline-item">
					<ClayButton
						aria-label={Liferay.Language.get('add-option')}
						borderless
						className="asset-filter-builder-add"
						displayType="secondary"
						onClick={handleAddRule}
						type="button"
					>
						<span className="inline-item inline-item-before">
							<ClayIcon symbol="plus" />
						</span>

						{Liferay.Language.get('add-option')}
					</ClayButton>
				</li>
			</ul>
		</>
	);
}

AssetFilterBuilder.propTypes = {
	categorySelectorURL: PropTypes.string,
	groupIds: PropTypes.arrayOf(PropTypes.string),
	namespace: PropTypes.string,
	rules: PropTypes.arrayOf(
		PropTypes.shape({
			queryAndOperator: PropTypes.bool,
			queryContains: PropTypes.bool,
			queryValues: PropTypes.string,
			selectedItems: PropTypes.oneOfType([
				PropTypes.string,
				PropTypes.arrayOf(
					PropTypes.shape({
						label: PropTypes.string,
						value: PropTypes.oneOfType([
							PropTypes.number,
							PropTypes.string,
						]),
					})
				),
			]),
			type: PropTypes.string,
		})
	),
	tagSelectorURL: PropTypes.string,
	vocabularyIds: PropTypes.arrayOf(PropTypes.string),
};

export default function (props) {
	return (
		<AssetFilterBuilder
			{...props}
			rules={props.rules.length ? props.rules : [DEFAULT_RULE]}
		/>
	);
}
