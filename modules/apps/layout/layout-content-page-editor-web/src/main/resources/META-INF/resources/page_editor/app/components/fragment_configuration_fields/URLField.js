/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import {useControlledState} from '@liferay/layout-js-components-web';
import {useId} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React, {useEffect, useState} from 'react';

import {LayoutSelector} from '../../../common/components/LayoutSelector';
import {ConfigurationFieldPropTypes} from '../../../prop_types/index';
import isMappedToLayout from '../../utils/editable_value/isMappedToLayout';

const SOURCE_OPTION_FROM_LAYOUT = 'fromLayout';
const SOURCE_OPTION_MANUAL = 'manual';

const SOURCE_OPTIONS = [
	{
		label: Liferay.Language.get('url'),
		value: SOURCE_OPTION_MANUAL,
	},
	{
		label: Liferay.Language.get('page'),
		value: SOURCE_OPTION_FROM_LAYOUT,
	},
];

export default function URLField({field, onValueSelect, value = {}}) {
	const [nextHref, setNextHref] = useControlledState(value.href || '');

	const [source, setSource] = useState(SOURCE_OPTION_MANUAL);

	useEffect(() => {
		if (isMappedToLayout(value)) {
			setSource(SOURCE_OPTION_FROM_LAYOUT);
		}
		else if (value.href) {
			setSource(SOURCE_OPTION_MANUAL);
		}
	}, [value]);

	const hrefInputId = useId();
	const sourceInputId = useId();

	const handleChange = (value) => {
		onValueSelect(field.name, value);
	};

	const handleSourceChange = (event) => {
		onValueSelect(field.name, {});
		setSource(event.target.value);
	};

	return (
		<>
			<ClayForm.Group small>
				<label htmlFor={sourceInputId}>{field.label}</label>

				<ClaySelectWithOption
					id={sourceInputId}
					onChange={handleSourceChange}
					options={SOURCE_OPTIONS}
					value={source}
				/>
			</ClayForm.Group>
			{source === SOURCE_OPTION_MANUAL && (
				<ClayForm.Group>
					<label htmlFor={hrefInputId}>
						{Liferay.Language.get('url')}
					</label>

					<ClayInput.Group small>
						<ClayInput.GroupItem>
							<ClayInput
								id={hrefInputId}
								onBlur={() => handleChange({href: nextHref})}
								onChange={(event) =>
									setNextHref(event.target.value)
								}
								type="text"
								value={nextHref || ''}
							/>
						</ClayInput.GroupItem>
					</ClayInput.Group>
				</ClayForm.Group>
			)}
			{source === SOURCE_OPTION_FROM_LAYOUT && (
				<LayoutSelector
					mappedLayout={value?.layout}
					onLayoutSelect={(layout) => {
						if (layout && !!Object.keys(layout).length) {
							handleChange({layout});
						}
						else {
							handleChange({});
						}
					}}
				/>
			)}
		</>
	);
}

URLField.propTypes = {
	field: PropTypes.shape(ConfigurationFieldPropTypes).isRequired,
	onValueSelect: PropTypes.func.isRequired,
	value: PropTypes.oneOfType([
		PropTypes.shape({
			classNameId: PropTypes.string,
			classPK: PropTypes.string,
			fieldId: PropTypes.string,
		}),

		PropTypes.shape({
			href: PropTypes.string,
		}),
	]),
};
