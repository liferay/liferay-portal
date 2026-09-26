/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Align} from '@clayui/drop-down';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {ClayPaginationWithBasicItems} from '@clayui/pagination';
import ClayPaginationBar from '@clayui/pagination-bar';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import getCN from 'classnames';
import {ManagementToolbar} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React, {useState} from 'react';

import useDidUpdateEffect from '../../hooks/useDidUpdateEffect';
import ErrorListItem from '../../shared/ErrorListItem';
import {PreviewModalWithCopyDownload} from '../../shared/PreviewModal';
import SearchInput from '../../shared/SearchInput';
import isDefined from '../../utils/functions/is_defined';
import parseAndPrettifyJSON from '../../utils/functions/parse_and_prettify_json';
import sub from '../../utils/language/sub';
import {TEST_IDS} from '../../utils/testIds';
import PreviewAttributesModal from './PreviewAttributesModal';
import ResultListItem from './ResultListItem';

const DELTAS = [10, 20, 30, 50];

function PreviewSidebar({
	errors = [],
	hits = [],
	loading,
	onClose,
	onFetchCancel,
	onFetchResults,
	onFocusSXPElement,
	requestString = '',
	responseString = '',
	totalHits,
	visible,
}) {
	const [activeDelta, setActiveDelta] = useState(10);
	const [activePage, setActivePage] = useState(1);
	const [attributes, setAttributes] = useState([]);
	const [showCancel, setShowCancel] = useState(false);
	const [value, setValue] = useState('');

	const isMounted = useIsMounted();

	const _handleAttributesSubmit = (attributes) => {
		setAttributes(attributes);
	};

	const _handleFetch = () => {
		setShowCancel(false);
		setTimeout(() => {
			if (isMounted()) {
				setShowCancel(true);
			}
		}, 10000);
		onFetchResults(value, activeDelta, activePage, attributes);
	};

	useDidUpdateEffect(() => {
		_handleFetch();
	}, [activeDelta, activePage]);

	const _handleDeltaChange = (delta) => () => {
		setActiveDelta(delta);
		setActivePage(1);
	};

	const _renderErrors = () => (
		<ClayList className="preview-error-list">
			{errors.map((error, index) => (
				<ErrorListItem
					error={error}
					key={index}
					onFocusSXPElement={onFocusSXPElement}
				/>
			))}
		</ClayList>
	);

	const _renderHits = () => (
		<div className="preview-results-list">
			<ClayList>
				{hits.map((result) => (
					<ResultListItem
						explanation={result.explanation}
						fields={result.documentFields}
						id={result.id}
						key={result.id}
						score={result.score}
					/>
				))}
			</ClayList>

			<ClayPaginationBar>
				<ClayPaginationBar.DropDown
					alignmentPosition={Align.TopLeft}
					items={DELTAS.map((delta) => ({
						label: delta,
						onClick: _handleDeltaChange(delta),
					}))}
					trigger={
						<ClayButton displayType="unstyled">
							{sub(Liferay.Language.get('x-entries'), [
								activeDelta,
							])}

							<ClayIcon symbol="caret-double-l" />
						</ClayButton>
					}
				/>

				<ClayPaginationBar.Results>
					{sub(Liferay.Language.get('showing-x-to-x-of-x-entries'), [
						(activePage - 1) * activeDelta + 1,
						activePage * activeDelta < totalHits
							? activePage * activeDelta
							: totalHits,
						totalHits,
					])}
				</ClayPaginationBar.Results>

				<ClayPaginationWithBasicItems
					active={activePage}
					alignmentPosition={Align.TopCenter}
					ellipsisBuffer={1}
					onActiveChange={setActivePage}
					totalPages={Math.ceil(totalHits / activeDelta)}
				/>
			</ClayPaginationBar>
		</div>
	);

	const _renderResultsManagementBar = () => (
		<ManagementToolbar.Container>
			<ManagementToolbar.ItemList>
				<ManagementToolbar.Item>
					<span className="text-truncate-inline total-hits-label">
						<span className="text-truncate">
							{sub(Liferay.Language.get('x-results'), [
								isDefined(totalHits)
									? totalHits.toLocaleString()
									: 0,
							])}
						</span>
					</span>
				</ManagementToolbar.Item>

				<ManagementToolbar.Item>
					<ClayButton
						aria-label={Liferay.Language.get('refresh')}
						disabled={loading}
						displayType="secondary"
						onClick={_handleFetch}
						small
					>
						{Liferay.Language.get('refresh')}
					</ClayButton>
				</ManagementToolbar.Item>
			</ManagementToolbar.ItemList>

			<ManagementToolbar.ItemList>
				<ManagementToolbar.Item>
					<PreviewModalWithCopyDownload
						fileName="raw_request.json"
						lineWrapping={false}
						size="lg"
						text={parseAndPrettifyJSON(requestString)}
						title={Liferay.Language.get('raw-request')}
					>
						<ClayButton
							borderless
							className="raw-request"
							disabled={loading}
							displayType="secondary"
							small
						>
							{Liferay.Language.get('view-raw-request')}
						</ClayButton>
					</PreviewModalWithCopyDownload>
				</ManagementToolbar.Item>

				<ManagementToolbar.Item>
					<PreviewModalWithCopyDownload
						fileName="raw_response.json"
						foldInitializationDelay={200}
						folded
						lineWrapping={false}
						size="lg"
						text={parseAndPrettifyJSON(responseString)}
						title={Liferay.Language.get('raw-response')}
					>
						<ClayButton
							borderless
							className="raw-response"
							disabled={loading}
							displayType="secondary"
							small
						>
							{Liferay.Language.get('view-raw-response')}
						</ClayButton>
					</PreviewModalWithCopyDownload>
				</ManagementToolbar.Item>
			</ManagementToolbar.ItemList>
		</ManagementToolbar.Container>
	);

	return (
		<div
			className={getCN('preview-sidebar', 'sidebar', 'sidebar-light', {
				open: visible,
			})}
			data-qa-id={TEST_IDS.PREVIEW_SIDEBAR}
		>
			<div className="sidebar-header">
				<div className="component-title">
					<span className="text-truncate-inline">
						<span className="text-truncate">
							{Liferay.Language.get('preview')}
						</span>
					</span>
				</div>

				<span>
					<PreviewAttributesModal
						description={Liferay.Language.get(
							'search-context-attributes-description'
						)}
						onSubmit={_handleAttributesSubmit}
						title={Liferay.Language.get(
							'search-context-attributes'
						)}
					>
						<ClayTooltipProvider>
							<ClayButton
								aria-label={Liferay.Language.get(
									'search-context-attributes'
								)}
								borderless
								displayType="secondary"
								monospaced
								small
								title={Liferay.Language.get(
									'search-context-attributes'
								)}
							>
								<ClayIcon symbol="cog" />
							</ClayButton>
						</ClayTooltipProvider>
					</PreviewAttributesModal>

					<ClayButton
						aria-label={Liferay.Language.get('close')}
						borderless
						displayType="secondary"
						monospaced
						onClick={onClose}
						small
					>
						<ClayIcon symbol="times" />
					</ClayButton>
				</span>
			</div>

			<nav
				aria-label={Liferay.Language.get('search')}
				className="component-tbar sidebar-search tbar"
			>
				<div className="container-fluid">
					<SearchInput
						disabled={loading}
						onChange={setValue}
						onEnter={_handleFetch}
					/>
				</div>
			</nav>

			<div className="sidebar-body">
				{!!errors.length && _renderErrors()}

				{isDefined(totalHits) && _renderResultsManagementBar()}

				{!loading ? (
					totalHits > 0 ? (
						_renderHits()
					) : totalHits === 0 ? (
						<div className="empty-list-message">
							<ClayEmptyState
								description=""
								title={Liferay.Language.get('no-results-found')}
							/>
						</div>
					) : (
						!errors.length && (
							<div className="search-message text-secondary">
								{Liferay.Language.get(
									'perform-a-search-to-preview-your-blueprints-search-results'
								)}
							</div>
						)
					)
				) : (
					<>
						<ClayLoadingIndicator />

						{showCancel && (
							<div className="search-message text-secondary">
								{Liferay.Language.get(
									'it-looks-like-this-is-taking-longer-than-expected'
								)}

								<ClayButton
									className="cancel"
									displayType="secondary"
									onClick={onFetchCancel}
									small
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>
							</div>
						)}
					</>
				)}
			</div>
		</div>
	);
}

PreviewSidebar.propTypes = {
	errors: PropTypes.arrayOf(PropTypes.object),
	hits: PropTypes.arrayOf(PropTypes.object),
	loading: PropTypes.bool,
	onClose: PropTypes.func,
	onFetchCancel: PropTypes.func,
	onFetchResults: PropTypes.func,
	onFocusSXPElement: PropTypes.func,
	requestString: PropTypes.string,
	responseString: PropTypes.string,
	totalHits: PropTypes.number,
	visible: PropTypes.bool,
};

export default React.memo(PreviewSidebar);
