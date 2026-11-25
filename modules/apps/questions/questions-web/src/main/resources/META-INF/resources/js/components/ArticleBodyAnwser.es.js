/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React, {useEffect, useState} from 'react';

import {toCSSString} from '../utils/utils.es';
import {MESSAGE_TYPES} from './ActivityUI.es';
import Highlight from './Highlight.es';

export default function ArticleBodyAnwser({
	articleBody,
	compactMode = false,
	companyName,
	encodingFormat,
	hasCompanyMx,
	id,
	signature,
	type,
}) {
	const [articleBodyContainsParagraph, setArticleBodyContainsParagraph] =
		useState(true);

	useEffect(() => {
		setArticleBodyContainsParagraph(articleBody.includes('<p>'));
	}, [articleBody]);

	const _companyName = hasCompanyMx && companyName ? `(${companyName})` : '';

	return (
		<div
			className={classNames(
				'h-100 p-3 position-relative rectangle-comment w-100',
				{
					'questions-answer questions-answer-success':
						type === MESSAGE_TYPES.bestAnswer.type,
				}
			)}
		>
			<div className="icon-quote-left position-absolute">
				<ClayIcon aria-label="quote left" symbol="quote-left" />
			</div>

			{encodingFormat !== 'bbcode' && compactMode && (
				<div
					className={`questions-article-body-${id} questions-labels-limit`}
					dangerouslySetInnerHTML={{__html: articleBody}}
				/>
			)}

			{encodingFormat !== 'bbcode' && !compactMode && (
				<div className={`cke_readonly questions-article-body-${id}`}>
					<Highlight innerHTML={true}>{articleBody}</Highlight>
				</div>
			)}

			{signature && (
				<style
					dangerouslySetInnerHTML={{
						__html: `.questions-article-body-${id} ${
							articleBodyContainsParagraph ? 'p' : 'div'
						}:last-child:after {content: ${toCSSString(
							` - ${signature} ${_companyName}`
						)}; font-weight: bold;}`,
					}}
				/>
			)}

			<div className="icon-quote-right position-absolute">
				<ClayIcon aria-label="quote right" symbol="quote-right" />
			</div>
		</div>
	);
}
