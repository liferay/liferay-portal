/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';
import {
	Panel,
	PanelBody,
	PanelHeader,
	Toggle,
} from '@liferay/object-js-components-web';
import React, {useState} from 'react';

import {TYPES, useLayoutContext} from '../objectLayoutContext';
import {BoxType, TObjectLayoutRow} from '../types';
import {HeaderDropdown} from './HeaderDropdown';
import ModalAddObjectLayoutField from './ModalAddObjectLayoutField';
import {ObjectLayoutRows} from './ObjectLayoutRows';

interface ObjectLayoutBoxProps extends React.HTMLAttributes<HTMLElement> {
	boxIndex: number;
	collapsable: boolean;
	label: string;
	objectLayoutRows?: TObjectLayoutRow[];
	tabIndex: number;
	type: BoxType;
}

export function ObjectLayoutBox({
	boxIndex,
	collapsable,
	label,
	objectLayoutRows,
	tabIndex,
	type,
}: ObjectLayoutBoxProps) {
	const [{isViewOnly}, dispatch] = useLayoutContext();
	const [visibleModal, setVisibleModal] = useState(false);
	const {observer, onClose} = useModal({
		onClose: () => setVisibleModal(false),
	});

	return (
		<>
			<Panel>
				<PanelHeader
					contentRight={
						<>
							<Toggle
								aria-label={Liferay.Language.get('collapsible')}
								disabled={isViewOnly}
								label={Liferay.Language.get('collapsible')}
								onToggle={(value) => {
									dispatch({
										payload: {
											attribute: {
												key: 'collapsable',
												value,
											},
											boxIndex,
											tabIndex,
										},
										type: TYPES.CHANGE_OBJECT_LAYOUT_BOX_ATTRIBUTE,
									});
								}}
								toggled={collapsable}
							/>

							{type === 'regular' && (
								<ClayButton
									className="ml-4"
									disabled={isViewOnly}
									displayType="secondary"
									onClick={() => setVisibleModal(true)}
									small
								>
									{Liferay.Language.get('add-field')}
								</ClayButton>
							)}

							<HeaderDropdown
								deleteElement={() => {
									dispatch({
										payload: {
											boxIndex,
											tabIndex,
										},
										type: TYPES.DELETE_OBJECT_LAYOUT_BOX,
									});
								}}
								disabled={isViewOnly}
							/>
						</>
					}
					disabled={isViewOnly}
					title={label}
					type={type}
				/>

				{!!objectLayoutRows?.length && (
					<PanelBody>
						<ObjectLayoutRows
							boxIndex={boxIndex}
							objectLayoutRows={objectLayoutRows}
							tabIndex={tabIndex}
						/>
					</PanelBody>
				)}
			</Panel>

			{visibleModal && (
				<ModalAddObjectLayoutField
					boxIndex={boxIndex}
					observer={observer}
					onClose={onClose}
					tabIndex={tabIndex}
				/>
			)}
		</>
	);
}
