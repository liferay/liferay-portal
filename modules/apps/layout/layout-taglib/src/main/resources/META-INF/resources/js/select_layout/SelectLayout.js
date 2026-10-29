/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import PropTypes from 'prop-types';
import React, {useState} from 'react';

import SelectLayoutTree from './SelectLayoutTree';

/**
 * SelectLayout
 *
 * This component shows a list of available layouts to select in expanded tree
 * and allows to filter them by searching.
 *
 * @review
 */

const SelectLayout = ({
	checkDisplayPage,
	config,
	groupId,
	itemSelectorReturnType,
	itemSelectorSaveEvent,
	multiSelection,
	namespace,
	nodes,
	privateLayout,
	selectedLayoutIds,
}) => {
	const [filter, setFilter] = useState();
	const [selectedItemsCount, setSelectedItemsCount] = useState(0);

	const empty = !nodes.length;

	return (
		<ClayLayout.ContainerFluid className="p-0 select-layout">
			<ClayForm.Group className="m-0 p-3 select-layout-filter">
				<ClayInput.Group>
					<ClayInput.GroupItem prepend>
						<ClayInput
							aria-label={Liferay.Language.get('search')}
							className="input-group-inset input-group-inset-after"
							disabled={empty}
							onChange={(event) => setFilter(event.target.value)}
							placeholder={`${Liferay.Language.get('search')}`}
							type="text"
						/>

						<ClayInput.GroupInsetItem after>
							<div className="link-monospaced">
								<ClayIcon symbol="search" />
							</div>
						</ClayInput.GroupInsetItem>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</ClayForm.Group>

			{empty ? (
				<EmptyState />
			) : (
				<>
					{Boolean(selectedItemsCount) && multiSelection && (
						<ClayLayout.Container
							className="align-items-center d-flex layout-tree-count-feedback px-3"
							containerElement="section"
							fluid
						>
							<div className="container p-0">
								<p className="m-0 text-2">
									{selectedItemsCount > 1
										? `${selectedItemsCount} ${Liferay.Language.get(
												'items-selected'
											)}`
										: `${selectedItemsCount} ${Liferay.Language.get(
												'item-selected'
											)}`}
								</p>
							</div>
						</ClayLayout.Container>
					)}

					<SelectLayoutTree
						checkDisplayPage={checkDisplayPage}
						config={{...config, namespace}}
						filter={filter}
						groupId={groupId}
						itemSelectorReturnType={itemSelectorReturnType}
						itemSelectorSaveEvent={itemSelectorSaveEvent}
						items={nodes}
						multiSelection={multiSelection}
						onItemsCountChange={setSelectedItemsCount}
						privateLayout={privateLayout}
						selectedLayoutIds={selectedLayoutIds}
					/>
				</>
			)}
		</ClayLayout.ContainerFluid>
	);
};

const EmptyState = () => {
	return (
		<ClayLayout.Sheet>
			<ClayEmptyState
				className="mt-0"
				description={Liferay.Language.get('there-are-no-pages')}
				image={
					<ClayIcon
						className="c-empty-state-icon c-empty-state-icon-empty-state"
						illustration
						symbol="empty-state"
					/>
				}
				title={Liferay.Language.get('no-results-found')}
			/>
		</ClayLayout.Sheet>
	);
};

SelectLayout.propTypes = {
	itemSelectorSaveEvent: PropTypes.string,
	multiSelection: PropTypes.bool,
	namespace: PropTypes.string,
	nodes: PropTypes.array.isRequired,
};

export default SelectLayout;
