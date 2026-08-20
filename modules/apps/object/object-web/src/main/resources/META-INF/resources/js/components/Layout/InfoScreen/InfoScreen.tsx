/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayCheckbox} from '@clayui/form';
import {
	Card,
	Input,
	constantsUtils,
	invalidateRequired,
	stringUtils,
} from '@liferay/object-js-components-web';
import React from 'react';

import {defaultLanguageId} from '../../../utils/constants';
import {TYPES, useLayoutContext} from '../objectLayoutContext';

const InfoScreen: React.FC<React.HTMLAttributes<HTMLElement>> = () => {
	const [{creationLanguageId, isViewOnly, objectLayout}, dispatch] =
		useLayoutContext();

	let error: string | undefined;

	if (
		invalidateRequired(
			stringUtils.getLocalizableLabel({
				fallbackLanguageId:
					creationLanguageId as Liferay.Language.Locale,
				labels: objectLayout.name,
			})
		)
	) {
		error = constantsUtils.REQUIRED_MSG;
	}

	return (
		<Card title={Liferay.Language.get('basic-info')}>
			<Input
				disabled={isViewOnly}
				error={error}
				id="objectLayoutName"
				label={Liferay.Language.get('name')}
				name="name"
				onChange={({target: {value}}) => {
					dispatch({
						payload: {name: {[defaultLanguageId]: value}},
						type: TYPES.CHANGE_OBJECT_LAYOUT_NAME,
					});
				}}
				required
				value={stringUtils.getLocalizableLabel({
					fallbackLanguageId:
						creationLanguageId as Liferay.Language.Locale,
					labels: objectLayout.name,
				})}
			/>

			<ClayForm.Group className="mb-0">
				<ClayCheckbox
					checked={objectLayout.defaultObjectLayout}
					disabled={isViewOnly}
					label={Liferay.Language.get('mark-as-default')}
					onChange={({target: {checked}}) => {
						dispatch({
							payload: {checked},
							type: TYPES.SET_OBJECT_LAYOUT_AS_DEFAULT,
						});
					}}
				/>
			</ClayForm.Group>
		</Card>
	);
};

export default InfoScreen;
