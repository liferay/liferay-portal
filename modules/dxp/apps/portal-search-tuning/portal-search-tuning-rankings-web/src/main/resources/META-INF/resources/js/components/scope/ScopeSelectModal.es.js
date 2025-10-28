/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal, {useModal} from '@clayui/modal';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import ClayTable from '@clayui/table';
import React, {useEffect, useState} from 'react';

import {fetchResponse} from '../../utils/api.es';
import {DELTAS} from '../../utils/constants.es';

/**
 * Modal that opens when user clicks on "View More" in ScopeSelect dropdown.
 * Displays a table of sites or blueprints and fetches more results as the user
 * clicks through the pagination bar.
 */

const ScopeSelectModal = ({
	fetchItemsUrl,
	locator,
	observer,
	onSubmit,
	selected = '',
	title,
}) => {
	const [activePage, setActivePage] = useState(1);
	const [delta, setDelta] = useState(10);
	const [resource, setResource] = useState({});
	const [loading, setLoading] = useState(false);
	const [error, setError] = useState('');

	useEffect(() => {
		setLoading(true);

		fetchResponse(fetchItemsUrl, {page: activePage, pageSize: delta})
			.then((response) => {
				setResource(response);
			})
			.catch((error) => {
				setError(error);
			})
			.finally(() => {
				setLoading(false);
			});
	}, [activePage, delta, fetchItemsUrl]);

	/**
	 * Handles what is displayed depending on loading/error/results/no results.
	 * @return The JSX to be rendered.
	 */
	const _renderModalBody = () => {

		// Loading

		if (loading) {
			return <ClayLoadingIndicator className="my-7" />;
		}

		// Error

		if (error) {
			return (
				<ClayEmptyState
					description={Liferay.Language.get(
						'an-error-has-occurred-and-we-were-unable-to-load-the-results'
					)}
					imgProps={{
						alt: Liferay.Language.get('unable-to-load-content'),
						title: Liferay.Language.get('unable-to-load-content'),
					}}
					imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/empty_state.svg`}
					title={Liferay.Language.get('unable-to-load-content')}
				/>
			);
		}

		// Has Results

		if (resource.items?.length) {
			return (
				<>
					<ClayTable>
						<ClayTable.Head>
							<ClayTable.Row>
								<ClayTable.Cell expanded headingCell>
									{Liferay.Language.get('name')}
								</ClayTable.Cell>

								<ClayTable.Cell expanded headingCell>
									{Liferay.Language.get(
										'external-reference-code'
									)}
								</ClayTable.Cell>
							</ClayTable.Row>
						</ClayTable.Head>

						<ClayTable.Body>
							{resource.items?.map((item) => (
								<ClayTable.Row key={item[locator.id]}>
									<ClayTable.Cell headingTitle>
										{item[locator.label]}
									</ClayTable.Cell>

									<ClayTable.Cell>
										{item.externalReferenceCode}
									</ClayTable.Cell>

									<ClayTable.Cell align="right">
										<ClayButton
											disabled={
												item[locator.id]?.toString() ===
												selected
											}
											displayType="secondary"
											onClick={() =>
												onSubmit(
													item[locator.id],
													item[locator.label]
												)
											}
										>
											{item[locator.id]?.toString() ===
											selected
												? Liferay.Language.get(
														'selected'
													)
												: Liferay.Language.get(
														'select'
													)}
										</ClayButton>
									</ClayTable.Cell>
								</ClayTable.Row>
							))}
						</ClayTable.Body>
					</ClayTable>

					<ClayPaginationBarWithBasicItems
						activeDelta={delta}
						activePage={activePage}
						deltas={DELTAS}
						ellipsisBuffer={3}
						onDeltaChange={setDelta}
						onPageChange={setActivePage}
						totalItems={resource.totalCount || 0}
					/>
				</>
			);
		}

		// No Results

		return (
			<ClayEmptyState
				description={Liferay.Language.get(
					'sorry,-no-results-were-found'
				)}
				imgProps={{
					alt: Liferay.Language.get('no-results-found'),
					title: Liferay.Language.get('no-results-found'),
				}}
				imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/empty_state.svg`}
				title={Liferay.Language.get('no-results-found')}
			/>
		);
	};

	return (
		<ClayModal observer={observer} size="full-screen">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ClayModal.Body>{_renderModalBody()}</ClayModal.Body>
		</ClayModal>
	);
};

export default function ({
	children,
	fetchItemsUrl,
	locator,
	onSubmit,
	selected,
	title,
}) {
	const {observer, onOpenChange, open} = useModal();

	const _handleSubmit = (key, name) => {
		onSubmit(key, name);

		onOpenChange(false);
	};

	return (
		<>
			{open && (
				<ScopeSelectModal
					fetchItemsUrl={fetchItemsUrl}
					locator={locator}
					observer={observer}
					onSubmit={_handleSubmit}
					selected={selected}
					title={title}
				/>
			)}

			<span onClick={() => onOpenChange(true)}>{children}</span>
		</>
	);
}
