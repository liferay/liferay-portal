/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayPaginationWithBasicItems} from '@clayui/pagination';
import ClayPaginationBar from '@clayui/pagination-bar';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React from 'react';

import {config} from '../../config/index';
import {useIsActive} from '../../contexts/ControlsContext';

export default function CollectionPagination({
	activePage,
	collectionConfig,
	collectionId,
	onPageChange,
	totalNumberOfItems,
	totalPages,
}) {
	const isActive = useIsActive();
	const {
		displayAllPages,
		numberOfItemsPerPage,
		numberOfPages,
		paginationType,
	} = collectionConfig;

	const itemsPerPage = Math.min(
		numberOfItemsPerPage,
		config.searchContainerPageMaxDelta
	);

	const numericPaginationLabelValues = [
		totalNumberOfItems ? (activePage - 1) * itemsPerPage + 1 : 0,
		Math.min(activePage * itemsPerPage, totalNumberOfItems),
		displayAllPages
			? totalNumberOfItems
			: Math.min(numberOfPages * itemsPerPage, totalNumberOfItems),
	];

	return (
		<div
			className={classNames(
				'page-editor__collection__pagination d-flex',
				{
					'page-editor__collection__pagination__overlay':
						totalNumberOfItems < 1 || !isActive(collectionId),
					'pt-3 pb-2': paginationType === 'numeric',
					'py-3': paginationType === 'simple',
				}
			)}
		>
			{paginationType === 'numeric' ? (
				<ClayPaginationBar className="flex-grow-1">
					<ClayPaginationBar.Results>
						{sub(
							Liferay.Language.get('showing-x-to-x-of-x-entries'),
							numericPaginationLabelValues
						)}
					</ClayPaginationBar.Results>

					<ClayPaginationWithBasicItems
						active={activePage}
						ariaLabels={{
							link: Liferay.Language.get('go-to-page-x'),
							next: Liferay.Language.get('go-to-the-next-page-x'),
							previous: Liferay.Language.get(
								'go-to-the-previous-page-x'
							),
						}}
						disableEllipsis
						onActiveChange={onPageChange}
						totalPages={
							(Number.isFinite(totalPages) && totalPages) || 1
						}
					/>
				</ClayPaginationBar>
			) : (
				<div className="d-flex flex-grow-1 h-100 justify-content-center">
					<ClayButton
						className="font-weight-semi-bold mr-3 text-secondary"
						disabled={activePage === 1}
						displayType="unstyled"
						onClick={() => onPageChange(activePage - 1)}
					>
						{Liferay.Language.get('previous')}
					</ClayButton>

					<ClayButton
						className="font-weight-semi-bold ml-3 text-secondary"
						disabled={activePage === totalPages}
						displayType="unstyled"
						onClick={() => onPageChange(activePage + 1)}
					>
						{Liferay.Language.get('next')}
					</ClayButton>
				</div>
			)}
		</div>
	);
}

CollectionPagination.propTypes = {
	activePage: PropTypes.number,
	collectionConfig: PropTypes.object,
	collectionId: PropTypes.string,
	onPageChange: PropTypes.func,
	totalNumberOfItems: PropTypes.number,
};
