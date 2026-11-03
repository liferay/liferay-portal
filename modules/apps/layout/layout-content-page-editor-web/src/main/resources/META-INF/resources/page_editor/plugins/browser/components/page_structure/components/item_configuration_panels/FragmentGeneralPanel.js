/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import PropTypes from 'prop-types';
import React, {useCallback} from 'react';

import {COMMON_STYLES_ROLES} from '../../../../../../app/config/constants/commonStylesRoles';
import {FRAGMENT_ENTRY_TYPES} from '../../../../../../app/config/constants/fragmentEntryTypes';
import {VIEWPORT_SIZES} from '../../../../../../app/config/constants/viewportSizes';
import {
	useDispatch,
	useSelector,
	useSelectorCallback,
	useSelectorRef,
} from '../../../../../../app/contexts/StoreContext';
import selectLanguageId from '../../../../../../app/selectors/selectLanguageId';
import getFragmentConfigurationValues from '../../../../../../app/utils/getFragmentConfigurationValues';
import {getResponsiveConfig} from '../../../../../../app/utils/getResponsiveConfig';
import updateConfigurationValue from '../../../../../../app/utils/updateConfigurationValue';
import {getLayoutDataItemPropTypes} from '../../../../../../prop_types/index';
import {CommonStyles} from './CommonStyles';
import {FieldSet} from './FieldSet';

export function FragmentGeneralPanel({item}) {
	const dispatch = useDispatch();

	const restrictedItemIds = useSelector((state) => state.restrictedItemIds);

	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const fragmentEntryLink = useSelectorCallback(
		(state) => state.fragmentEntryLinks[item.config.fragmentEntryLinkId],
		[item.config.fragmentEntryLinkId]
	);

	const languageId = useSelector(selectLanguageId);

	const fragmentEntryLinksRef = useSelectorRef(
		(state) => state.fragmentEntryLinks
	);

	const fieldSets =
		fragmentEntryLink.configuration?.fieldSets?.filter(
			({configurationRole, label}) =>
				!configurationRole &&
				!(
					fragmentEntryLink.fragmentEntryType ===
						FRAGMENT_ENTRY_TYPES.input && !label
				)
		) ?? [];

	const itemConfig = getResponsiveConfig(item.config, selectedViewportSize);

	const onValueSelect = useCallback(
		(name, value) => {
			updateConfigurationValue({
				configuration: fragmentEntryLink.configuration,
				dispatch,
				fragmentEntryLink,
				languageId,
				name,
				value,
			});
		},
		[dispatch, fragmentEntryLink, languageId]
	);

	if (restrictedItemIds.has(item.itemId)) {
		return (
			<ClayAlert displayType="secondary" role={null}>
				{Liferay.Language.get(
					'this-content-cannot-be-displayed-due-to-permission-restrictions'
				)}
			</ClayAlert>
		);
	}

	return (
		<>
			{selectedViewportSize === VIEWPORT_SIZES.desktop &&
				fieldSets.map((fieldSet, index) => {
					const fields = fieldSet.fields;

					return (
						<div className="mb-1 panel-group-sm" key={index}>
							<FieldSet
								collapsed={fieldSet.collapsed}
								customComponentModule={
									fieldSet.customComponentModule
								}
								description={fieldSet.description}
								fields={fields}
								fragmentEntryLinks={
									fragmentEntryLinksRef.current
								}
								isCustomStylesFieldSet
								label={fieldSet.label}
								languageId={languageId}
								onValueSelect={onValueSelect}
								selectedViewportSize={selectedViewportSize}
								values={getFragmentConfigurationValues(
									fragmentEntryLink
								)}
							/>
						</div>
					);
				})}

			<CommonStyles
				commonStylesValues={itemConfig.styles}
				item={item}
				role={COMMON_STYLES_ROLES.general}
			/>
		</>
	);
}

FragmentGeneralPanel.propTypes = {
	item: getLayoutDataItemPropTypes({
		config: PropTypes.shape({
			fragmentEntryLinkId: PropTypes.string.isRequired,
		}).isRequired,
	}),
};
