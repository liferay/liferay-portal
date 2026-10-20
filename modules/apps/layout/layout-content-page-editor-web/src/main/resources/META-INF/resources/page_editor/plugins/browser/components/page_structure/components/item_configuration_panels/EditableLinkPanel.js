/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import {useId} from 'frontend-js-components-web';
import React, {useEffect, useState} from 'react';

import LinkField from '../../../../../../app/components/fragment_configuration_fields/LinkField';
import {EDITABLE_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../app/config/constants/editableFragmentEntryProcessor';
import {EDITABLE_TYPES} from '../../../../../../app/config/constants/editableTypes';
import {
	useDispatch,
	useSelector,
	useSelectorCallback,
} from '../../../../../../app/contexts/StoreContext';
import selectEditableValue from '../../../../../../app/selectors/selectEditableValue';
import selectEditableValues from '../../../../../../app/selectors/selectEditableValues';
import updateEditableValues from '../../../../../../app/thunks/updateEditableValues';
import {deepEqual} from '../../../../../../app/utils/checkDeepEqual';
import isMapped from '../../../../../../app/utils/editable_value/isMapped';
import {getEditableLinkValue} from '../../../../../../app/utils/getEditableLinkValue';
import {getEditableItemPropTypes} from '../../../../../../prop_types/index';

const PREFIX_OPTIONS = [
	{
		label: Liferay.Language.get('none'),
		value: '',
	},
	{
		label: Liferay.Language.get('email'),
		value: 'mailto:',
	},
	{
		label: Liferay.Language.get('phone'),
		value: 'tel:',
	},
];

export default function EditableLinkPanel({item}) {
	const dispatch = useDispatch();
	const languageId = useSelector((state) => state.languageId);

	const editableValues = useSelectorCallback(
		(state) => selectEditableValues(state, item.fragmentEntryLinkId),
		[item.fragmentEntryLinkId]
	);

	const editableValue = useSelectorCallback(
		(state) =>
			selectEditableValue(
				state,
				item.fragmentEntryLinkId,
				item.editableId,
				EDITABLE_FRAGMENT_ENTRY_PROCESSOR
			) || {},
		[item.fragmentEntryLinkId, item.editableId],
		deepEqual
	);

	const [linkConfig, setLinkConfig] = useState({});
	const [linkValue, setLinkValue] = useState({href: '', target: ''});
	const [imageConfig, setImageConfig] = useState({});
	const [linkPrefix, setLinkPrefix] = useState('');

	const prefixId = useId();

	useEffect(() => {
		const linkConfig = {
			...(editableValue.config || {}),
		};

		if (Object.keys(linkConfig).length) {
			setImageConfig({
				alt: linkConfig.alt || '',
				imageConfiguration: linkConfig.imageConfiguration || {},
			});
			setLinkPrefix(linkConfig.prefix);

			delete linkConfig.alt;
			delete linkConfig.imageConfiguration;
			delete linkConfig.prefix;

			setLinkConfig(linkConfig);
			setLinkValue(getEditableLinkValue(linkConfig, languageId));
		}
		else {
			setImageConfig({});
			setLinkConfig({});
			setLinkPrefix('');
			setLinkValue({href: '', target: ''});
		}
	}, [editableValue.config, languageId]);

	const handleValueSelect = (nextLinkConfig) => {
		let nextConfig;

		if (isMapped(nextLinkConfig) || isMapped(linkConfig)) {
			nextConfig = {...imageConfig, ...nextLinkConfig};
		}
		else {
			nextConfig = {
				...imageConfig,
				...(linkConfig || {}),
			};

			if (Object.keys(nextLinkConfig).length) {
				nextConfig = {
					...nextConfig,
					href: {
						...(linkConfig.href || {}),
						[languageId]: nextLinkConfig.href,
					},
					target: nextLinkConfig.target || '',
				};
			}
		}

		if (item.type !== EDITABLE_TYPES.link) {
			nextConfig.mapperType = 'link';
		}

		dispatch(
			updateEditableValues({
				editableValues: {
					...editableValues,
					[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {
						...editableValues[EDITABLE_FRAGMENT_ENTRY_PROCESSOR],
						[item.editableId]: {
							...editableValue,
							config: nextConfig,
						},
					},
				},
				fragmentEntryLinkId: item.fragmentEntryLinkId,
			})
		);
	};

	return (
		<>
			<LinkField
				field={{name: 'link'}}
				onValueSelect={(_, nextLinkConfig) =>
					handleValueSelect(nextLinkConfig)
				}
				value={linkValue}
			/>

			{isMapped(linkConfig) && (
				<ClayForm.Group small>
					<label htmlFor={prefixId}>
						{Liferay.Language.get('prefix[string]')}
					</label>

					<ClaySelectWithOption
						id={prefixId}
						onChange={(event) =>
							handleValueSelect({
								...editableValue.config,
								prefix: event.target.value,
							})
						}
						options={PREFIX_OPTIONS}
						value={linkPrefix || ''}
					/>
				</ClayForm.Group>
			)}
		</>
	);
}

EditableLinkPanel.propTypes = {
	item: getEditableItemPropTypes(),
};
