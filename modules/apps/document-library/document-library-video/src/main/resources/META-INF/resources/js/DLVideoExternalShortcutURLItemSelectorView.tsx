/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {getOpener} from 'frontend-js-web';
import React, {useState} from 'react';

import DLVideoExternalShortcutInput from './components/DLVideoExternalShortcutInput';
import DLVideoExternalShortcutPreview from './components/DLVideoExternalShortcutPreview';
import {useDLVideoExternalShortcutFields} from './utils/useDLVideoExternalShortcutFields';

const DLVideoExternalShortcutURLItemSelectorView: React.FC<{
	eventName: string;
	getDLVideoExternalShortcutFieldsURL: string;
	namespace: string;
	returnType: string;
}> = ({
	eventName,
	getDLVideoExternalShortcutFieldsURL,
	namespace,
	returnType,
}) => {
	const [url, setUrl] = useState<string>('');

	const {error, fields, loading} = useDLVideoExternalShortcutFields({
		getDLVideoExternalShortcutFieldsURL,
		namespace,
		url,
	});

	const isDisabled = !fields || loading;

	const onClick = () => {
		if (!fields) {
			return;
		}

		getOpener().Liferay.fire(eventName, {
			data: {
				returnType,
				value: {
					html: fields.HTML,
					title: fields.TITLE || fields.URL,
					url: Liferay.FeatureFlags['LPD-11235']
						? fields.URL
						: undefined,
				},
			},
		});
	};

	return (
		<div>
			<DLVideoExternalShortcutInput
				labelTooltip={Liferay.Language.get('internal-video-tooltip')}
				onChange={setUrl}
				url={url}
			/>

			<ClayButton disabled={isDisabled} onClick={onClick}>
				{Liferay.Language.get('add')}
			</ClayButton>

			<DLVideoExternalShortcutPreview
				error={error ? error : undefined}
				loading={loading}
				videoHTML={fields?.HTML}
			/>
		</div>
	);
};

export default DLVideoExternalShortcutURLItemSelectorView;
