/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Input} from '../../../../../components/Input/Input';
import {Section} from '../../../../../components/Section/Section';
import {
	NewAppTypes,
	useNewAppContext,
} from '../../../../../context/NewAppContext';
import i18n from '../../../../../i18n';

const Version = () => {
	const [
		{
			version: {notes, version},
		},
		dispatch,
	] = useNewAppContext();

	return (
		<Section
			label="App Version"
			tooltip="When adding app versions, you can use your own numbering system, but be sure it is consistent and understandable by the customer."
			tooltipText="More Info"
		>
			<Input
				helpMessage="This is the first version of the app to be published"
				label={i18n.translate('version')}
				onChange={({target}) =>
					dispatch({
						payload: {version: target.value},
						type: NewAppTypes.SET_VERSION,
					})
				}
				placeholder="0.0.0"
				required
				tooltip="Specify your app's version. This will help the user to understand the latest version of your app offered on the Marketplace."
				value={version}
			/>

			<Input
				component="textarea"
				label="Notes"
				onChange={({target}) =>
					dispatch({
						payload: {notes: target.value},
						type: NewAppTypes.SET_VERSION,
					})
				}
				placeholder="Enter app description"
				tooltip="Notes pertaining to the release of the project. These will be displayed when the customer goes to purchase and/or update the app."
				value={notes}
			/>
		</Section>
	);
};

export default Version;
