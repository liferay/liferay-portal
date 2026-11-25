/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import {toCSSString} from '../utils/utils.es';
import Highlight from './Highlight.es';

export default function ArticleBodyRenderer({
	articleBody,
	compactMode = false,
	companyName,
	elapsedTime,
	encodingFormat,
	hasCompanyMx,
	id,
	showSignature = true,
	signature,
}) {
	const [articleBodyContainsParagraph, setArticleBodyContainsParagraph] =
		useState(true);

	useEffect(() => {
		setArticleBodyContainsParagraph(articleBody.includes('<p>'));
	}, [articleBody]);

	const _companyName = hasCompanyMx && companyName ? `(${companyName})` : '';

	return (
		<>
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

			{showSignature && signature && (
				<style
					dangerouslySetInnerHTML={{
						__html: `.questions-article-body-${id} ${
							articleBodyContainsParagraph ? 'p' : 'div'
						}:last-child:after {content: ${toCSSString(
							` - ${signature} ${_companyName} - ${elapsedTime}`
						)}; font-weight: bold;}`,
					}}
				/>
			)}
		</>
	);
}
