/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import ClayPanel from '@clayui/panel';
import {useId} from 'frontend-js-components-web';
import React from 'react';

import {COMMON_STYLES_ROLES} from '../../../../../../app/config/constants/commonStylesRoles';
import {
	useDispatch,
	useSelector,
} from '../../../../../../app/contexts/StoreContext';
import {CommonStyles, TextField} from '../../../../../../app/js-index';
import selectLanguageId from '../../../../../../app/selectors/selectLanguageId';
import updateItemConfig from '../../../../../../app/thunks/updateItemConfig';
import {getEditableLocalizedValue} from '../../../../../../app/utils/getEditableLocalizedValue';
import {setIn} from '../../../../../../app/utils/setIn';
import CurrentLanguageFlag from '../../../../../../common/components/CurrentLanguageFlag';
import {FormRelationshipLayoutDataItem} from '../../../../../../types/layout_data/FormRelationshipLayoutDataItem';
import FormRelationshipMappingOptions from './FormRelationshipMappingOptions';

export function FormRelationshipGeneralPanel({
	item,
}: {
	item: FormRelationshipLayoutDataItem;
}) {
	return (
		<>
			<FormRelationshipOptions item={item} />

			<FrameOptions item={item} />
		</>
	);
}

function FormRelationshipOptions({
	item,
}: {
	item: FormRelationshipLayoutDataItem;
}) {
	const dispatch = useDispatch();

	const languageId = useSelector(selectLanguageId);

	const helpTextId = useId();

	const {buttonLabel} = item.config;

	const isMapped = Boolean(item.config.contentType);

	return (
		<div className="mb-3 panel-group-sm">
			<ClayPanel
				collapsable
				defaultExpanded
				displayTitle={Liferay.Language.get('form-relationship-options')}
				displayType="unstyled"
				showCollapseIcon
			>
				<ClayPanel.Body>
					<FormRelationshipMappingOptions item={item} />

					{isMapped ? (
						<ClayForm.Group small>
							<ClayInput.Group className="align-items-end" small>
								<ClayInput.GroupItem>
									<TextField
										field={{
											label: Liferay.Language.get(
												'add-button-label'
											),
											typeOptions: {
												additionalProps: {
													'aria-described-by':
														helpTextId,
												},
											},
										}}
										onValueSelect={(
											name: string,
											value: string
										) =>
											dispatch(
												updateItemConfig({
													itemConfig: {
														...item.config,
														buttonLabel: setIn(
															buttonLabel || {},
															[languageId],
															value
														),
													},
													itemIds: [item.itemId],
												})
											)
										}
										value={getEditableLocalizedValue(
											buttonLabel,
											languageId,
											Liferay.Language.get('add-new')
										)}
									/>
								</ClayInput.GroupItem>

								<ClayInput.GroupItem shrink>
									<CurrentLanguageFlag />
								</ClayInput.GroupItem>
							</ClayInput.Group>

							<p
								className="m-0 mt-1 text-2 text-secondary"
								id={helpTextId}
							>
								{Liferay.Language.get(
									'use-a-clear-and-concise-label-to-describe-the-action'
								)}
							</p>
						</ClayForm.Group>
					) : null}
				</ClayPanel.Body>
			</ClayPanel>
		</div>
	);
}

function FrameOptions({item}: {item: FormRelationshipLayoutDataItem}) {
	return (
		<div className="mb-3 panel-group-sm">
			<ClayPanel
				collapsable
				defaultExpanded
				displayTitle={Liferay.Language.get('frame')}
				displayType="unstyled"
				showCollapseIcon
			>
				<ClayPanel.Body>
					<CommonStyles
						commonStylesValues={item.config.styles || {}}
						embedInCollapsableSection={false}
						item={item}
						role={COMMON_STYLES_ROLES.general}
					/>
				</ClayPanel.Body>
			</ClayPanel>
		</div>
	);
}
