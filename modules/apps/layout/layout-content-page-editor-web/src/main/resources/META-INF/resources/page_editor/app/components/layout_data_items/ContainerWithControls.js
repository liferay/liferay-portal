/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {useEffect, useState} from 'react';

import useSetRef from '../../../common/hooks/useSetRef';
import {getLayoutDataItemPropTypes} from '../../../prop_types/index';
import {CONTAINER_WIDTH_TYPES} from '../../config/constants/containerWidthTypes';
import {
	useHoveredItemId,
	useHoveredItemType,
} from '../../contexts/ControlsContext';
import {useSelector} from '../../contexts/StoreContext';
import selectCanUpdateItemConfiguration from '../../selectors/selectCanUpdateItemConfiguration';
import getLayoutDataItemTopperUniqueClassName from '../../utils/getLayoutDataItemTopperUniqueClassName';
import {getResponsiveConfig} from '../../utils/getResponsiveConfig';
import Topper from '../topper/Topper';
import Container from './Container';
import isHovered from './isHovered';

const ContainerWithControls = React.forwardRef(
	({children, className, item}, ref) => {
		const canUpdateItemConfiguration = useSelector(
			selectCanUpdateItemConfiguration
		);
		const [hovered, setHovered] = useState(false);
		const selectedViewportSize = useSelector(
			(state) => state.selectedViewportSize
		);

		const [setRef, itemElement] = useSetRef(ref);

		const itemConfig = getResponsiveConfig(
			item.config,
			selectedViewportSize
		);

		const {widthType} = itemConfig;

		return (
			<>
				<HoverHandler
					hovered={hovered}
					item={item}
					setHovered={setHovered}
				/>
				<Topper
					className={classNames(
						getLayoutDataItemTopperUniqueClassName(item.itemId),
						{
							[`container-fluid`]:
								widthType === CONTAINER_WIDTH_TYPES.fixed,
							[`container-fluid-max-xl`]:
								widthType === CONTAINER_WIDTH_TYPES.fixed,
							'p-0': widthType === CONTAINER_WIDTH_TYPES.fixed,
							'page-editor__topper--hovered': hovered,
						}
					)}
					item={item}
					itemElement={itemElement}
				>
					<Container
						className={classNames(className, {
							'page-editor__container':
								canUpdateItemConfiguration,
						})}
						item={item}
						ref={setRef}
					>
						{children}
					</Container>
				</Topper>
			</>
		);
	}
);

ContainerWithControls.propTypes = {
	item: getLayoutDataItemPropTypes().isRequired,
};

export default ContainerWithControls;

const HoverHandler = ({hovered, item, setHovered}) => {
	const hoveredItemType = useHoveredItemType();
	const hoveredItemId = useHoveredItemId();

	useEffect(() => {
		const backgroundImage = item.config?.styles?.backgroundImage;

		if (backgroundImage?.classNameId && backgroundImage?.classPK) {
			const nextHovered = isHovered({
				editableValue: backgroundImage,
				hoveredItemId,
				hoveredItemType,
			});

			if (hovered !== nextHovered) {
				setHovered(nextHovered);
			}
		}
	}, [hovered, hoveredItemId, hoveredItemType, item, setHovered]);

	return null;
};
