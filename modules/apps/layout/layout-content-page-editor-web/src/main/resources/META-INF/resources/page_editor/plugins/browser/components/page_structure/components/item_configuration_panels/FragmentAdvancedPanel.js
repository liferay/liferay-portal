/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PropTypes from 'prop-types';
import React, {useCallback} from 'react';

import {HideFromSearchField} from '../../../../../../app/components/fragment_configuration_fields/HideFromSearchField';
import {FRAGMENT_CONFIGURATION_ROLES} from '../../../../../../app/config/constants/fragmentConfigurationRoles';
import {config} from '../../../../../../app/config/index';
import {
	useDispatch,
	useSelector,
	useSelectorCallback,
	useSelectorRef,
} from '../../../../../../app/contexts/StoreContext';
import selectLanguageId from '../../../../../../app/selectors/selectLanguageId';
import getFragmentConfigurationValues from '../../../../../../app/utils/getFragmentConfigurationValues';
import updateConfigurationValue from '../../../../../../app/utils/updateConfigurationValue';
import {getLayoutDataItemPropTypes} from '../../../../../../prop_types/index';
import CSSFieldSet from './CSSFieldSet';
import {FieldSet} from './FieldSet';

export function FragmentAdvancedPanel({item}) {
	const dispatch = useDispatch();

	const fragmentEntryLink = useSelectorCallback(
		(state) => state.fragmentEntryLinks[item.config.fragmentEntryLinkId],
		[item.config.fragmentEntryLinkId]
	);
	const languageId = useSelector(selectLanguageId);

	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const fragmentEntryLinksRef = useSelectorRef(
		(state) => state.fragmentEntryLinks
	);

	const onConfigurationValueSelect = useCallback(
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

	const fieldSets = fragmentEntryLink.configuration?.fieldSets?.filter(
		(fieldSet) =>
			fieldSet.configurationRole === FRAGMENT_CONFIGURATION_ROLES.advanced
	);

	return (
		<>
			<h1 className="sr-only">
				{Liferay.Language.get('advanced-configuration')}
			</h1>

			{fieldSets?.length > 0 &&
				fieldSets.map((fieldSet, index) => {
					return (
						<FieldSet
							collapsed={fieldSet.collapsed}
							customComponentModule={
								fieldSet.customComponentModule
							}
							description={fieldSet.description}
							fields={fieldSet.fields}
							fragmentEntryLinks={fragmentEntryLinksRef.current}
							key={index}
							label={fieldSet.label}
							languageId={config.defaultLanguageId}
							onValueSelect={onConfigurationValueSelect}
							selectedViewportSize={selectedViewportSize}
							values={getFragmentConfigurationValues(
								fragmentEntryLink
							)}
						/>
					);
				})}

			<HideFromSearchField item={item} />

			<CSSFieldSet item={item} />
		</>
	);
}

FragmentAdvancedPanel.propTypes = {
	item: getLayoutDataItemPropTypes({
		config: PropTypes.shape({
			fragmentEntryLinkId: PropTypes.string.isRequired,
		}).isRequired,
	}),
};
