/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import {useModal} from '@clayui/modal';
import {
	Panel,
	PanelBody,
	PanelHeader,
	stringUtils,
} from '@liferay/object-js-components-web';
import React, {useState} from 'react';

import {defaultLanguageId} from '../../../utils/constants';
import {TYPES, useLayoutContext} from '../objectLayoutContext';
import {HeaderDropdown} from './HeaderDropdown';
import {ModalAddObjectLayoutBox} from './ModalAddObjectLayoutBox';
import {ObjectLayoutBox} from './ObjectLayoutBox';
import {ObjectLayoutRelationship} from './ObjectLayoutRelationship';

const ObjectLayoutTabs: React.FC<React.HTMLAttributes<HTMLElement>> = () => {
	const [{creationLanguageId, isViewOnly, objectLayout}, dispatch] =
		useLayoutContext();
	const [visibleModal, setVisibleModal] = useState(false);
	const [selectedTabIndex, setSelectedTabIndex] = useState(0);
	const {observer, onClose} = useModal({
		onClose: () => setVisibleModal(false),
	});

	const handleAddLayoutBox = (
		box: {name: string; type: 'categorization' | 'seo'},
		tabIndex: number
	) => {
		dispatch({
			payload: {
				name: {
					[defaultLanguageId]: box.name,
				},
				tabIndex,
				type: box.type,
			},
			type: TYPES.ADD_OBJECT_LAYOUT_BOX,
		});
	};

	return (
		<>
			{objectLayout?.objectLayoutTabs?.map(
				({name, objectLayoutBoxes, objectRelationshipId}, tabIndex) => {
					const isRelationshipType =
						objectRelationshipId !== 0 && objectRelationshipId;
					const labelDisplayType = isRelationshipType
						? 'warning'
						: 'info';

					return (
						<Panel
							className="layout-tab__tab"
							key={`layout_${tabIndex}`}
						>
							<PanelHeader
								contentLeft={
									<ClayLabel displayType={labelDisplayType}>
										{isRelationshipType
											? Liferay.Language.get(
													'relationships'
												)
											: Liferay.Language.get('fields')}
									</ClayLabel>
								}
								contentRight={
									<>
										{!isRelationshipType && (
											<ClayButton
												disabled={isViewOnly}
												displayType="secondary"
												onClick={() => {
													setVisibleModal(true);
													setSelectedTabIndex(
														tabIndex
													);
												}}
												small
											>
												<ClayIcon symbol="plus" />

												<span className="ml-2">
													{Liferay.Language.get(
														'add-block'
													)}
												</span>
											</ClayButton>
										)}

										<HeaderDropdown
											addCategorization={() =>
												handleAddLayoutBox(
													{
														name: Liferay.Language.get(
															'categorization'
														),
														type: 'categorization',
													},
													tabIndex
												)
											}
											addSeo={() =>
												handleAddLayoutBox(
													{
														name: Liferay.Language.get(
															'seo'
														),
														type: 'seo',
													},
													tabIndex
												)
											}
											deleteElement={() => {
												dispatch({
													payload: {
														tabIndex,
													},
													type: TYPES.DELETE_OBJECT_LAYOUT_TAB,
												});
											}}
										/>
									</>
								}
								title={stringUtils.getLocalizableLabel({
									fallbackLanguageId: creationLanguageId,
									labels: name,
								})}
								type="regular"
							/>

							{!!objectLayoutBoxes?.length &&
								!isRelationshipType && (
									<PanelBody>
										{objectLayoutBoxes.map(
											(
												{
													collapsable,
													name,
													objectLayoutRows,
													type,
												},
												boxIndex
											) => (
												<ObjectLayoutBox
													boxIndex={boxIndex}
													collapsable={collapsable}
													key={`box_${boxIndex}`}
													label={stringUtils.getLocalizableLabel(
														{
															fallbackLanguageId:
																creationLanguageId,
															labels: name,
														}
													)}
													objectLayoutRows={
														objectLayoutRows
													}
													tabIndex={tabIndex}
													type={type}
												/>
											)
										)}
									</PanelBody>
								)}

							{isRelationshipType && (
								<PanelBody>
									<ObjectLayoutRelationship
										objectRelationshipId={
											objectRelationshipId
										}
									/>
								</PanelBody>
							)}
						</Panel>
					);
				}
			)}

			{visibleModal && (
				<ModalAddObjectLayoutBox
					observer={observer}
					onClose={onClose}
					tabIndex={selectedTabIndex}
				/>
			)}
		</>
	);
};

export default ObjectLayoutTabs;
