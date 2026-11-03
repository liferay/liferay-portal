/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PropTypes from 'prop-types';
import React, {useCallback} from 'react';

import {FRAGMENT_CONFIGURATION_ROLES} from '../../../../../../app/config/constants/fragmentConfigurationRoles';
import {VIEWPORT_SIZES} from '../../../../../../app/config/constants/viewportSizes';
import {config} from '../../../../../../app/config/index';
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

export function FragmentStylesPanel({item}) {
	const dispatch = useDispatch();

	const fragmentEntryLink = useSelectorCallback(
		(state) => state.fragmentEntryLinks[item.config.fragmentEntryLinkId],
		[item.config.fragmentEntryLinkId]
	);

	const languageId = useSelector(selectLanguageId);
	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const itemConfig = getResponsiveConfig(item.config, selectedViewportSize);

	const hasCustomStyles =
		fragmentEntryLink.configuration?.fieldSets?.filter(
			(fieldSet) =>
				fieldSet.configurationRole ===
				FRAGMENT_CONFIGURATION_ROLES.style
		).length && selectedViewportSize === VIEWPORT_SIZES.desktop;

	const onCustomStyleValueSelect = useCallback(
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

	return (
		<>
			{selectedViewportSize === VIEWPORT_SIZES.desktop && (
				<CustomStyles
					fragmentEntryLink={fragmentEntryLink}
					onValueSelect={onCustomStyleValueSelect}
				/>
			)}

			<CommonStyles
				className={hasCustomStyles ? 'mt-3' : null}
				commonStylesValues={itemConfig.styles}
				item={item}
			/>
		</>
	);
}

FragmentStylesPanel.propTypes = {
	item: getLayoutDataItemPropTypes({
		config: PropTypes.shape({
			fragmentEntryLinkId: PropTypes.string.isRequired,
		}).isRequired,
	}),
};

const CustomStyles = ({fragmentEntryLink, onValueSelect}) => {
	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const fragmentEntryLinksRef = useSelectorRef(
		(state) => state.fragmentEntryLinks
	);

	const fieldSets = fragmentEntryLink.configuration?.fieldSets?.filter(
		(fieldSet) =>
			fieldSet.configurationRole === FRAGMENT_CONFIGURATION_ROLES.style
	);

	return fieldSets?.length ? (
		<div className="page-editor__page-structure__section__custom-styles pb-0">
			<h1 className="sr-only">{Liferay.Language.get('custom-styles')}</h1>

			{fieldSets.map((fieldSet, index) => {
				return (
					<FieldSet
						collapsed={fieldSet.collapsed}
						description={fieldSet.description}
						fields={fieldSet.fields}
						fragmentEntryLinks={fragmentEntryLinksRef.current}
						isCustomStylesFieldSet
						key={index}
						label={fieldSet.label}
						languageId={config.defaultLanguageId}
						onValueSelect={onValueSelect}
						selectedViewportSize={selectedViewportSize}
						values={getFragmentConfigurationValues(
							fragmentEntryLink
						)}
					/>
				);
			})}
		</div>
	) : null;
};

CustomStyles.propTypes = {
	fragmentEntryLink: PropTypes.object.isRequired,
	onValueSelect: PropTypes.func.isRequired,
};
