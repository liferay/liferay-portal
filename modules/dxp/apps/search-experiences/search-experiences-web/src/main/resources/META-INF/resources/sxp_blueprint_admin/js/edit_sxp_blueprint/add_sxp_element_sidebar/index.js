/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClaySticker from '@clayui/sticker';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import getCN from 'classnames';
import PropTypes from 'prop-types';
import React, {
	useCallback,
	useContext,
	useEffect,
	useMemo,
	useState,
} from 'react';

import LearnMessage from '../../shared/LearnMessage';
import SearchInput from '../../shared/SearchInput';
import Sidebar from '../../shared/Sidebar';
import ThemeContext from '../../shared/ThemeContext';
import {
	CUSTOM_JSON_SXP_ELEMENT,
	DEFAULT_SXP_ELEMENT_ICON,
} from '../../utils/data';
import addParams from '../../utils/fetch/add_params';
import fetchData from '../../utils/fetch/fetch_data';
import {
	SIDEBAR_STATE,
	setStorageAddSXPElementSidebar,
} from '../../utils/sessionStorage';
import getSXPElementTitleAndDescription from '../../utils/sxp_element/get_sxp_element_title_and_description';
import isElementInactiveFromNonCompanyIndex from '../../utils/sxp_element/is_element_inactive_from_noncompany_index';

const DEFAULT_CATEGORY = 'other';
const DEFAULT_EXPANDED_LIST = ['match'];

const LAST_CATEGORIES = [DEFAULT_CATEGORY, 'custom'];

const SXPElementList = ({
	category,
	expand,
	isIndexCompany,
	onAddSXPElement,
	sxpElements,
}) => {
	const {locale} = useContext(ThemeContext);

	const [showList, setShowList] = useState(expand);

	useEffect(() => {
		setShowList(expand);
	}, [expand]);

	const _handleAddSXPElement = (sxpElement) => () => {
		onAddSXPElement(sxpElement);
	};

	return (
		<>
			{!!category && (
				<ClayButton
					aria-label={
						showList
							? Liferay.Language.get('collapse')
							: Liferay.Language.get('expand')
					}
					className="panel-header sidebar-dt"
					displayType="unstyled"
					onClick={() => setShowList(!showList)}
				>
					<span>{category}</span>

					<span className="sidebar-arrow">
						<ClayIcon
							symbol={showList ? 'angle-down' : 'angle-right'}
						/>
					</span>
				</ClayButton>
			)}

			{showList && (
				<ClayList>
					{sxpElements.map((sxpElement, index) => {
						const [title, description] =
							getSXPElementTitleAndDescription(
								sxpElement,
								locale
							);

						return (
							<ClayList.Item
								className={getCN('sxp-element-item', {
									inactive:
										isElementInactiveFromNonCompanyIndex(
											isIndexCompany,
											sxpElement
										),
								})}
								flex
								key={index}
							>
								<ClayList.ItemField>
									<ClaySticker
										className="bg-light"
										displayType="secondary"
										size="md"
									>
										<ClayIcon
											symbol={
												sxpElement.elementDefinition
													?.icon ||
												DEFAULT_SXP_ELEMENT_ICON
											}
										/>
									</ClaySticker>
								</ClayList.ItemField>

								<ClayList.ItemField expand>
									{title && (
										<ClayList.ItemTitle>
											{title}
										</ClayList.ItemTitle>
									)}

									{description && (
										<ClayList.ItemText subtext={true}>
											{description}
										</ClayList.ItemText>
									)}
								</ClayList.ItemField>

								<ClayList.ItemField>
									<div className="add-sxp-element-button-background bg-primary-l3" />

									{isElementInactiveFromNonCompanyIndex(
										isIndexCompany,
										sxpElement
									) ? (
										<ClayTooltipProvider>
											<ClayButton
												aria-disabled="true"
												className="add-sxp-element-button disabled"
												data-tooltip-align="left"
												displayType="secondary"
												small
												title={Liferay.Language.get(
													'query-element-inactive-from-index-help'
												)}
											>
												{Liferay.Language.get('add')}
											</ClayButton>
										</ClayTooltipProvider>
									) : (
										<ClayButton
											aria-label={Liferay.Language.get(
												'add'
											)}
											className="add-sxp-element-button"
											displayType="secondary"
											onClick={_handleAddSXPElement(
												sxpElement
											)}
											small
										>
											{Liferay.Language.get('add')}
										</ClayButton>
									)}
								</ClayList.ItemField>
							</ClayList.Item>
						);
					})}
				</ClayList>
			)}
		</>
	);
};

function AddSXPElement({
	isIndexCompany,
	emptyMessage = Liferay.Language.get('no-query-elements-found'),
	onAddSXPElement,
	querySXPElements,
}) {
	const {locale} = useContext(ThemeContext);

	const [loading, setLoading] = useState(true);

	const sxpElements = useMemo(
		() => [...querySXPElements, CUSTOM_JSON_SXP_ELEMENT],
		[querySXPElements]
	);

	const [filteredSXPElements, setFilteredSXPElements] = useState(sxpElements);

	const [categories, setCategories] = useState([]);
	const [categorizedSXPElements, setCategorizedSXPElements] = useState({});
	const [expandAll, setExpandAll] = useState(false);

	const _categorizeSXPElements = (sxpElements) => {
		const newCategories = [];
		const newCategorizedSXPElements = {};

		sxpElements.map((sxpElement) => {
			const category =
				sxpElement.elementDefinition?.category || DEFAULT_CATEGORY;

			newCategorizedSXPElements[category] = [
				...(newCategorizedSXPElements[category] || []),
				sxpElement,
			];

			// Don't add last categories since they will be added in the
			// `setCategories` call below

			if (
				!newCategories.includes(category) &&
				!LAST_CATEGORIES.includes(category)
			) {
				newCategories.push(category);
			}
		});

		setCategories([
			...newCategories.sort(),
			...LAST_CATEGORIES.filter(
				(category) =>
					newCategorizedSXPElements[category] &&
					newCategorizedSXPElements[category].length
			), // Add last categories unless there are no elements
		]);

		setCategorizedSXPElements(newCategorizedSXPElements);
	};

	useEffect(() => {
		_categorizeSXPElements(sxpElements);

		setLoading(false);
	}, [sxpElements]);

	const _handleSearchChange = useCallback(
		(value) => {
			const newSXPElements = sxpElements.filter((sxpElement) => {
				if (value) {
					const [sxpElementTitle] = getSXPElementTitleAndDescription(
						sxpElement,
						locale
					);

					return sxpElementTitle
						.toLowerCase()
						.includes(value.toLowerCase());
				}
				else {
					return true;
				}
			});

			_categorizeSXPElements(newSXPElements);
			setFilteredSXPElements(newSXPElements);
			setExpandAll(!!value);
		},
		[sxpElements, locale]
	);

	return (
		<>
			<LearnMessage resourceKey="query-elements" />

			<nav className="component-tbar sidebar-search tbar">
				<div className="container-fluid">
					<SearchInput onChange={_handleSearchChange} />
				</div>
			</nav>

			{!loading ? (
				filteredSXPElements.length ? (
					<div className="sxp-element-list">
						{categories.map((category) => (
							<SXPElementList
								category={category}
								expand={
									expandAll ||
									DEFAULT_EXPANDED_LIST.includes(category)
								}
								isIndexCompany={isIndexCompany}
								key={category}
								onAddSXPElement={onAddSXPElement}
								sxpElements={categorizedSXPElements[category]}
							/>
						))}
					</div>
				) : (
					<div className="empty-list-message">
						<ClayEmptyState description="" title={emptyMessage} />
					</div>
				)
			) : (
				<ClayLoadingIndicator />
			)}
		</>
	);
}

function AddSXPElementSidebar({
	emptyMessage,
	isIndexCompany,
	onAddSXPElement,
	onClose,
	visible,
}) {
	const isMounted = useIsMounted();

	const [querySXPElements, setQuerySXPElements] = useState(null);

	useEffect(() => {
		fetchData(
			addParams('/o/search-experiences-rest/v1.0/sxp-elements', {
				pageSize: 200, // TODO check pagesize
			})
		)
			.then((responseContent) => {
				if (isMounted()) {
					setQuerySXPElements(responseContent.items);
				}
			})
			.catch(() => {
				if (isMounted()) {
					setQuerySXPElements([]);
				}
			});
	}, []); //eslint-disable-line

	if (!querySXPElements) {
		return null;
	}

	const _handleClose = () => {
		setStorageAddSXPElementSidebar(SIDEBAR_STATE.CLOSED);

		onClose();
	};

	return (
		<Sidebar
			className="add-sxp-element-sidebar"
			onClose={_handleClose}
			title={Liferay.Language.get('add-query-elements')}
			visible={visible}
		>
			<AddSXPElement
				emptyMessage={emptyMessage}
				isIndexCompany={isIndexCompany}
				onAddSXPElement={onAddSXPElement}
				querySXPElements={querySXPElements}
			/>
		</Sidebar>
	);
}

AddSXPElementSidebar.propTypes = {
	emptyMessage: PropTypes.string,
	isIndexCompany: PropTypes.bool,
	onAddSXPElement: PropTypes.func,
	onClose: PropTypes.func,
	visible: PropTypes.bool,
};

export default React.memo(AddSXPElementSidebar);
