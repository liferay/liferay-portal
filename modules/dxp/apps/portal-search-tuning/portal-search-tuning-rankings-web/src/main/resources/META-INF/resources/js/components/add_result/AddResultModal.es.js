/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useResource} from '@clayui/data-provider';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import {usePrevious} from '@liferay/frontend-js-react-web';
import getCN from 'classnames';
import PropTypes from 'prop-types';
import React, {useContext, useEffect, useState} from 'react';

import ThemeContext from '../../ThemeContext.es';
import {
	DEFAULT_DELTA,
	DELTAS,
	FETCH_OPTIONS,
	KEY_CODES,
	PORTAL_TOOLTIP_TRIGGER_CLASS,
} from '../../utils/constants.es';
import {getPluralMessage} from '../../utils/language.es';
import {buildUrl, resultsDataToMap, toggleListItem} from '../../utils/util.es';
import Item from '../list/Item.es';
import ClayEmptyState, {DISPLAY_STATES} from '../shared/ClayEmptyState.es';
import AddResultSearchBar from './AddResultSearchBar.es';

/**
 * A button that opens a modal to be able to search, select, and add results.
 */
function AddResultModal({
	fetchDocumentsSearchURL,
	observer,
	onAddResultSubmit,
	onClose,
}) {
	const {companyId, namespace, spritemap} = useContext(ThemeContext);

	/**
	 * State flow: resourceNotAsked -> loading -> error/success
	 * Success is implicit when all are false.
	 */
	const [resourceState, setResourceState] = useState(() => ({
		error: false,
		loading: false,
	}));

	const {error, loading} = resourceState;

	const [delta, setDelta] = useState(DEFAULT_DELTA.label);
	const [resourceNotAsked, setResourceNotAsked] = useState(true);
	const [page, setPage] = useState(1);
	const [searchQuery, setSearchQuery] = useState('');
	const [selectedIds, setSelectedIds] = useState([]);

	const prevDelta = usePrevious(delta);
	const prevPage = usePrevious(page);

	/**
	 * Stores the full object data of selected results. This is used to
	 * transform the `selectedIds` into a the list of objects to send to
	 * `onAddResultSubmit`.
	 */
	const [dataMap, setDataMap] = useState(false);

	const {refetch, resource} = useResource({
		fetchOptions: FETCH_OPTIONS,
		link: buildUrl(fetchDocumentsSearchURL, {
			[`${namespace}companyId`]: companyId,
			[`${namespace}from`]: page * delta - delta,
			[`${namespace}keywords`]: searchQuery,
			[`${namespace}size`]: delta,
		}),
		onNetworkStatusChange: (status) =>
			setResourceState({
				error: status === 5,
				loading: status < 4,
			}),
	});

	/**
	 * Fetches for new data when the delta or page change.
	 */
	useEffect(() => {
		if (prevDelta !== delta || prevPage !== page) {
			refetch();
		}
	}, [delta, page, prevDelta, prevPage, refetch]);

	/**
	 * Deselects all items on the current page.
	 */
	function _deselectAll() {
		setSelectedIds(
			selectedIds.filter(
				(resultId) => !_getCurrentResultIds().includes(resultId)
			)
		);
	}

	/**
	 * Gets the ids of the current results displayed on the page.
	 * @returns {Array} List of ids
	 */
	function _getCurrentResultIds() {
		return resource.documents.map((result) => result.id);
	}

	/**
	 * Gets the selected ids on the current page. Useful for displaying the
	 * proper checkbox and performing the proper action when clicking on the
	 * checkbox.
	 * @returns {Array} List of ids
	 */
	function _getCurrentResultSelectedIds() {
		return selectedIds.filter((resultId) =>
			_getCurrentResultIds().includes(resultId)
		);
	}

	/**
	 * Gets the result data object from ids.
	 * @param {Array} ids The list of ids to get.
	 * @returns {Array} List of result data objects.
	 */
	function _getResultData(ids) {
		return resource.documents.filter(({id}) => ids.includes(id));
	}

	/**
	 * Controls the behavior when clicking on the list header checkbox.
	 * If any results on the current page are selected, the items will be
	 * deselected. Otherwise, select all items.
	 */
	function _handleAllCheckbox() {
		if (_getCurrentResultSelectedIds().length) {
			_deselectAll();
		}
		else {
			_selectAll();
		}
	}

	/**
	 * Removes all selected ids.
	 */
	function _handleClearAllSelected() {
		setSelectedIds([]);
	}

	/**
	 * Sets the new delta, resets to the first page, and fetches for the new
	 * set of results.
	 * @param {number} newDelta The delta selected.
	 */
	function _handleDeltaChange(newDelta) {
		setDelta(newDelta);
		setPage(1);
	}

	/**
	 * Sets the new page selected and fetches for the new results.
	 * @param {number} newPage The page selected.
	 */
	function _handlePageChange(newPage) {
		setPage(newPage);
	}

	/**
	 * Wrapper around the `refetch` function to keep track if it has been
	 * called to be able to show an initial display. This is a workaround
	 * since there isn't currently a way to prevent an initial fetch from
	 * being called when using `useResource`.
	 */
	function _handleRefetch() {
		setResourceNotAsked(false);

		refetch();
	}

	/**
	 * When the 'Enter' key is pressed, the page will reset back to the first
	 * page, selections will be cleared, and a new search will be performed.
	 * If the search query is blank, nothing will happen.
	 * @param {SyntheticEvent} event
	 */
	function _handleSearchKeyDown(event) {
		if (!event.currentTarget.value.trim()) {
			return;
		}

		if (event.key === KEY_CODES.ENTER) {
			setPage(1);

			_handleRefetch();
		}
	}

	/**
	 * Updates the search query input field.
	 * @param {SyntheticEvent} event
	 */
	function _handleSearchQueryChange(event) {
		setSearchQuery(event.target.value);
	}

	/**
	 * Handles when an item's checkbox is clicked on.
	 * @param {String} id The id selected.
	 */
	function _handleSelect(id) {
		const resultDataToSelect = _getResultData([id]);

		setDataMap(resultsDataToMap(resultDataToSelect, dataMap));

		setSelectedIds(toggleListItem(selectedIds, id));
	}

	/**
	 * Primary action of the modal. Pins the selected results to the result
	 * rankings form.
	 * @param {SyntheticEvent} event
	 */
	function _handleSubmit(event) {
		event.preventDefault();

		onAddResultSubmit(selectedIds.map((id) => dataMap[id]));

		onClose();
	}

	/**
	 * Checks if there are any results to display.
	 * @returns {boolean} True if there is data.
	 */
	function _hasResultsToShow() {
		return !!(resource && resource.total && resource.documents);
	}

	/**
	 * Selects all items on the current page.
	 */
	function _selectAll() {
		_deselectAll(); // Deselect all items first to avoid adding duplicates.

		const resultDataToSelect = _getResultData(_getCurrentResultIds());

		setDataMap(resultsDataToMap(resultDataToSelect, dataMap));

		setSelectedIds([...selectedIds, ..._getCurrentResultIds()]);
	}

	/**
	 * Renders the empty state. Conditionally shows 3 different empty states:
	 * 1) Initial message with help text.
	 * 2) Empty result message when a search returns no matches.
	 * 3) Error message when a search request fails to resolve.
	 */
	function _renderEmptyState() {
		let emptyState = <ClayEmptyState />;

		if (resourceNotAsked) {
			emptyState = (
				<ClayEmptyState
					description={Liferay.Language.get(
						'search-the-engine-to-display-results'
					)}
					displayState={DISPLAY_STATES.EMPTY}
					title={Liferay.Language.get('search-the-engine')}
				/>
			);
		}
		else if (error) {
			emptyState = (
				<ClayEmptyState
					actionLabel={Liferay.Language.get('try-again')}
					description={Liferay.Language.get(
						'an-error-has-occurred-and-we-were-unable-to-load-the-results'
					)}
					displayState={DISPLAY_STATES.EMPTY}
					onClickAction={_handleRefetch}
					title={Liferay.Language.get('unable-to-load-content')}
				/>
			);
		}

		return (
			<ClayLayout.Sheet className="add-result-sheet">
				{emptyState}
			</ClayLayout.Sheet>
		);
	}

	/**
	 * Displays the search results with a management and pagination bar.
	 */
	function _renderSearchResults() {
		const classManagementBar = getCN(
			'management-bar',
			selectedIds.length
				? 'management-bar-primary'
				: 'management-bar-light',
			'navbar',
			'navbar-expand-md'
		);

		const selectItemsLabel = selectedIds.length
			? getPluralMessage(
					Liferay.Language.get('x-item-selected'),
					Liferay.Language.get('x-items-selected'),
					selectedIds.length
				)
			: Liferay.Language.get('select-items');

		const checked =
			_getCurrentResultSelectedIds().length === resource.documents.length;

		const indeterminate =
			!!selectedIds.length &&
			_getCurrentResultSelectedIds().length !== resource.documents.length;

		return (
			<>
				<ClayLayout.Sheet className="add-result-sheet">
					<div className={classManagementBar}>
						<ClayLayout.ContainerFluid>
							<ul className="navbar-nav navbar-nav-expand">
								<li className="nav-item">
									<ClayCheckbox
										aria-label={Liferay.Language.get(
											'select-all'
										)}
										checked={checked}
										indeterminate={indeterminate}
										onChange={_handleAllCheckbox}
									/>
								</li>

								<li className="nav-item">
									<span className="navbar-text">
										{selectItemsLabel}
									</span>
								</li>

								{!!selectedIds.length && (
									<li className="nav-item nav-item-shrink">
										<ClayButton
											aria-label={Liferay.Language.get(
												'clear-all-selected'
											)}
											className="btn-outline-borderless"
											displayType="secondary"
											onClick={_handleClearAllSelected}
											small
										>
											{Liferay.Language.get(
												'clear-all-selected'
											)}
										</ClayButton>
									</li>
								)}
							</ul>
						</ClayLayout.ContainerFluid>
					</div>

					<ul className="list-group" data-testid="add-result-items">
						{resource.documents.map((result, index) => (
							<Item.DecoratedComponent
								author={result.author}
								clicks={result.clicks}
								date={result.date}
								description={result.description}
								hidden={result.hidden}
								icon={result.icon}
								id={result.id}
								index={index}
								key={result.id}
								onSelect={_handleSelect}
								selected={selectedIds.includes(result.id)}
								title={result.title}
								type={result.type}
								viewURL={result.viewURL}
							/>
						))}
					</ul>
				</ClayLayout.Sheet>

				<div className="add-result-container">
					<ClayPaginationBarWithBasicItems
						activeDelta={delta}
						activePage={page}
						deltas={DELTAS}
						ellipsisBuffer={1}
						labels={{
							paginationResults: Liferay.Language.get(
								'showing-x-to-x-of-x-entries'
							),
							perPageItems: Liferay.Language.get('x-items'),
							selectPerPageItems: Liferay.Language.get('x-items'),
						}}
						onDeltaChange={_handleDeltaChange}
						onPageChange={_handlePageChange}
						spritemap={spritemap}
						totalItems={resource.total}
					/>
				</div>
			</>
		);
	}

	return (
		<ClayModal
			className="result-ranking-modal-root"
			observer={observer}
			size="lg"
		>
			<div
				className="add-result-modal-root"
				data-testid="add-result-modal"
			>
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{Liferay.Language.get('add-result')}

					<span
						className={getCN(
							'inline-item',
							'inline-item-after',
							'modal-title-help-icon',
							PORTAL_TOOLTIP_TRIGGER_CLASS
						)}
						data-title={Liferay.Language.get('add-results-help')}
					>
						<ClayIcon symbol="question-circle-full" />
					</span>
				</ClayModal.Header>

				<ClayModal.Body>
					<AddResultSearchBar
						onSearchKeyDown={_handleSearchKeyDown}
						onSearchQueryChange={_handleSearchQueryChange}
						onSearchSubmit={_handleRefetch}
						searchQuery={searchQuery}
					/>

					<div className="add-result-scroller">
						{loading && (
							<ClayLayout.Sheet className="add-result-sheet">
								<div className="sheet-title">
									<div className="load-more-container">
										<ClayLoadingIndicator />
									</div>
								</div>
							</ClayLayout.Sheet>
						)}

						{!loading &&
							(_hasResultsToShow()
								? _renderSearchResults()
								: _renderEmptyState())}
					</div>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								className="btn-outline-borderless"
								displayType="secondary"
								onClick={onClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								disabled={!selectedIds.length}
								onClick={_handleSubmit}
							>
								{Liferay.Language.get('add')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</div>
		</ClayModal>
	);
}

AddResultModal.propTypes = {
	fetchDocumentsSearchURL: PropTypes.string.isRequired,
	onAddResultSubmit: PropTypes.func.isRequired,
	onClose: PropTypes.func.isRequired,
};

export default AddResultModal;
