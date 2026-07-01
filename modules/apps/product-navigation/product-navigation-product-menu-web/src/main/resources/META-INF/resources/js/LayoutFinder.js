/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {openToast} from 'frontend-js-components-web';
import {
	fetch,
	objectToFormData,
	setCSPNonce,
	setSessionValue,
	sub,
} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useState} from 'react';

import {useDebounceCallback} from './useDebounceCallback';

const MAX_ITEMS_TO_SHOW = 10;

function LayoutFinder({
	administrationPortletNamespace,
	administrationPortletURL,
	findLayoutsURL,
	keywords,
	namespace,
	productMenuPortletURL,
	setKeywords,
}) {
	const [layouts, setLayouts] = useState([]);
	const [loading, setLoading] = useState(false);
	const [totalCount, setTotalCount] = useState(0);

	const handleFormSubmit = useCallback((event) => {
		event.preventDefault();
		event.stopPropagation();
	}, []);

	const [updatePageResults, cancelUpdatePageResults] = useDebounceCallback(
		(newKeywords) => {
			fetch(findLayoutsURL, {
				body: objectToFormData({
					[`${namespace}keywords`]: newKeywords,
				}),
				method: 'post',
			})
				.then((response) => {
					return response.ok
						? response.json()
						: {
								layouts: [],
								totalCount: 0,
							};
				})
				.then((response) => {
					setLoading(false);
					setLayouts(response.layouts);
					setTotalCount(response.totalCount);
				});
		},
		1000
	);

	const handleOnChange = useCallback(
		(event) => {
			const newKeywords = event.target.value;

			setKeywords(newKeywords);

			if (!newKeywords.length) {
				setLoading(false);
				setLayouts([]);
				setTotalCount(0);

				cancelUpdatePageResults();
			}
			else {
				setLoading(true);
				updatePageResults(newKeywords);
			}
		},
		[cancelUpdatePageResults, setKeywords, updatePageResults]
	);

	const handleOnClick = useCallback(() => {
		Liferay.Portlet.destroy(`#p_p_id${namespace}`);

		setSessionValue(
			'com.liferay.product.navigation.product.menu.web_pagesTreeState',
			'closed'
		).then(() => {
			fetch(productMenuPortletURL)
				.then((response) => {
					if (!response.ok) {
						throw new Error();
					}

					return response.text();
				})
				.then((productMenuContent) => {
					const sidebar = document.querySelector(
						'.lfr-product-menu-sidebar .sidebar-body'
					);

					sidebar.innerHTML = '';

					productMenuContent = setCSPNonce(productMenuContent);

					const range = document.createRange();
					range.selectNode(sidebar);

					sidebar.appendChild(
						range.createContextualFragment(productMenuContent)
					);
				})
				.catch(() => {
					openToast({
						message: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
						title: Liferay.Language.get('error'),
						type: 'danger',
					});
				});
		});
	}, [namespace, productMenuPortletURL]);

	return (
		<div className="layout-finder">
			<button
				className="back-to-menu btn btn-sm btn-unstyled mb-3 pr-3"
				onClick={handleOnClick}
			>
				<ClayIcon className="icon-monospaced" symbol="angle-left" />

				{`${Liferay.Language.get('back-to-menu')} `}
			</button>

			<form onSubmit={handleFormSubmit} role="search">
				<label
					className="sr-only"
					htmlFor={`${namespace}-layout-finder-page-input`}
				>
					{`${Liferay.Language.get('page-name')}: `}
				</label>

				<input
					autoComplete="off"
					className="form-control form-control-sm"
					id={`${namespace}-layout-finder-page-input`}
					onChange={handleOnChange}
					placeholder={Liferay.Language.get(
						'start-typing-to-find-a-page'
					)}
					type="text"
					value={keywords}
				/>
			</form>

			{totalCount > 0 && (
				<>
					<nav className="mt-2">
						{layouts.map(
							(layout, layoutIndex) =>
								layoutIndex < MAX_ITEMS_TO_SHOW && (
									<>
										<ol className="breadcrumb">
											{layout.path &&
												!!layout.path.length && (
													<>
														{layout.path.map(
															(layoutPath) => (
																<li
																	className="breadcrumb-item text-secondary"
																	key={
																		layoutPath
																	}
																>
																	<span className="breadcrumb-text-truncate">
																		{
																			layoutPath
																		}
																	</span>
																</li>
															)
														)}
													</>
												)}

											<li
												className="breadcrumb-item"
												key={layout.name}
											>
												<a
													className="d-block font-weight-bold mb-2 text-break"
													href={layout.url}
													key={layout.url}
													target={layout.target}
												>
													{layout.name}
												</a>
											</li>
										</ol>
									</>
								)
						)}
					</nav>

					{totalCount > MAX_ITEMS_TO_SHOW && (
						<div>
							<div className="mb-3 mt-3 text-center">
								{sub(
									Liferay.Language.get(
										'there-are-x-more-results-narrow-your-searc-to-get-more-precise-results'
									),
									totalCount - MAX_ITEMS_TO_SHOW
								)}
							</div>

							<div className="text-center">
								<a
									href={`${administrationPortletURL}&${administrationPortletNamespace}keywords=${keywords}`}
								>
									{Liferay.Language.get(
										'view-in-page-administration'
									)}
								</a>
							</div>
						</div>
					)}
				</>
			)}

			{loading && (
				<ClayLoadingIndicator className="mb-0 mt-3" light small />
			)}

			{totalCount === 0 && !loading && keywords.length > 1 && (
				<div className="mt-3 text-center">
					{Liferay.Language.get('page-not-found')}
				</div>
			)}
		</div>
	);
}

LayoutFinder.propTypes = {
	administrationPortletNamespace: PropTypes.string,
	administrationPortletURL: PropTypes.string,
	findLayoutsURL: PropTypes.string,
	namespace: PropTypes.string,
	productMenuPortletURL: PropTypes.string,
	viewInPageAdministrationURL: PropTypes.string,
};

export default LayoutFinder;
