/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayList from '@clayui/list';
import getCN from 'classnames';
import {dateUtils} from 'frontend-js-web';
import React, {useContext, useState} from 'react';

import {PreviewModalWithCopyDownload} from '../../shared/PreviewModal';
import ThemeContext from '../../shared/ThemeContext';
import {TEST_IDS} from '../../utils/testIds';

const getResultDefaultKeys = (locale) => [
	'entryClassName',
	`content_${locale}`,
	'createDate',
	'modified',
	'userName',
];

const SXP_BLUEPRINT_FIELD_PREFIX = '_';
const RESULTS_SHOW_KEYS = ['assetEntryId'];
const DATE_KEYS = ['birthDate', 'createDate', 'modified'];
const TRUNCATE_LENGTH = 700;

const sxpBlueprintFieldPrefixRegex = new RegExp(
	`^(${SXP_BLUEPRINT_FIELD_PREFIX})`
);

function localizeDate(property, value) {
	if (DATE_KEYS.includes(property)) {
		return dateUtils.format(
			dateUtils.parse(value, 'yyyyMMddhhmmss'),
			'PP p'
		);
	}

	return value;
}

function removeSXPBlueprintFieldPrefix(value) {
	return value.replace(sxpBlueprintFieldPrefixRegex, '');
}

function truncateString(value) {
	return value.length > TRUNCATE_LENGTH
		? value.substring(0, TRUNCATE_LENGTH).concat('...')
		: value;
}

function ResultListItem({explanation = '', fields, id, score = 0}) {
	const {locale} = useContext(ThemeContext);

	const [collapse, setCollapse] = useState(true);

	const _renderListRow = (property, value) =>
		value && (
			<ClayLayout.Row justify="start" key={property}>
				<ClayLayout.Col size={4}>
					{removeSXPBlueprintFieldPrefix(property)}
				</ClayLayout.Col>

				<ClayLayout.Col
					className={getCN({'text-truncate': collapse})}
					size={8}
				>
					{truncateString(localizeDate(property, value))}
				</ClayLayout.Col>
			</ClayLayout.Row>
		);

	return (
		<ClayList.Item
			className="result-list-item"
			data-qa-id={TEST_IDS.PREVIEW_SIDEBAR_RESULT_LIST_ITEM}
			flex
			key={id}
		>
			<ClayList.ItemField>
				<PreviewModalWithCopyDownload
					fileName="score_explanation.json"
					size="lg"
					text={explanation}
					title={Liferay.Language.get('score-explanation')}
				>
					<ClayButton
						className="score"
						displayType="unstyled"
						size="sm"
					>
						{score.toFixed(2)}
					</ClayButton>
				</PreviewModalWithCopyDownload>
			</ClayList.ItemField>

			<ClayList.ItemField expand>
				<ClayList.ItemTitle>
					{fields.assetTitle || id}
				</ClayList.ItemTitle>

				{getResultDefaultKeys(locale).map((property) =>
					_renderListRow(property, fields[property])
				)}

				{!collapse && (
					<>
						{RESULTS_SHOW_KEYS.map((property) =>
							_renderListRow(property, fields[property])
						)}

						<div className="list-group-header">
							<span className="list-group-header-title">
								{Liferay.Language.get('document-fields')}
							</span>
						</div>

						{Object.keys(fields)
							.sort()
							.map((property) =>
								_renderListRow(property, fields[property])
							)}
					</>
				)}
			</ClayList.ItemField>

			<ClayList.ItemField>
				<ClayButton
					aria-label={
						collapse
							? Liferay.Language.get('expand')
							: Liferay.Language.get('collapse')
					}
					displayType="unstyled"
					onClick={() => setCollapse(!collapse)}
				>
					<ClayIcon
						symbol={collapse ? 'angle-right' : 'angle-down'}
					/>
				</ClayButton>
			</ClayList.ItemField>
		</ClayList.Item>
	);
}

export default ResultListItem;
