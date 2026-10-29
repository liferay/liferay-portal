/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayPanel from '@clayui/panel';
import PropTypes from 'prop-types';
import React from 'react';

import {CONTENT_TYPE_LABELS} from '../../../app/config/constants/contentTypeLabels';
import PageContent from './PageContent';

export default function ContentList({contents, selectedType}) {
	const filteredContentTypes = Object.keys(contents);
	const hasSelectedType =
		selectedType && selectedType !== CONTENT_TYPE_LABELS.allContent;

	return (
		<div className="overflow-auto page-editor__page-contents__content-list panel-group-sm pt-4 px-3">
			{(filteredContentTypes.includes(selectedType) ||
				!hasSelectedType) &&
			!!filteredContentTypes.length ? (
				hasSelectedType ? (
					<PageContentList
						pageContents={contents}
						type={selectedType}
					/>
				) : (
					Object.keys(contents).map((type) => (
						<ClayPanel
							collapsable
							defaultExpanded
							displayTitle={type}
							displayType="unstyled"
							key={type}
							showCollapseIcon
						>
							<ClayPanel.Body>
								<PageContentList
									pageContents={contents}
									type={type}
								/>
							</ClayPanel.Body>
						</ClayPanel>
					))
				)
			) : (
				<ClayEmptyState
					description={Liferay.Language.get(
						'try-again-with-a-different-search'
					)}
					image={
						<ClayIcon
							className="c-empty-state-icon c-empty-state-icon-search-state"
							illustration
							symbol="search-state"
						/>
					}
					small
					title={Liferay.Language.get('no-results-found')}
				/>
			)}
		</div>
	);
}

ContentList.propTypes = {
	contents: PropTypes.object,
	selectedType: PropTypes.string,
};

const PageContentList = ({pageContents, type}) => (
	<ul className="list-unstyled mb-1">
		{pageContents[type || CONTENT_TYPE_LABELS.allContent]?.map(
			(pageContent, index) => (
				<PageContent
					key={`${
						pageContent.classPK || pageContent.externalReferenceCode
					}${index}`}
					{...pageContent}
				/>
			)
		)}
	</ul>
);

PageContentList.proptypes = {
	pageContents: PropTypes.array,
	type: PropTypes.string,
};
