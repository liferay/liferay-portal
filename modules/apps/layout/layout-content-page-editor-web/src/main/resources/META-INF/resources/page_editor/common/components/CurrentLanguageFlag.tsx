/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React from 'react';

import {config} from '../../app/config/index';
import {useSelector} from '../../app/contexts/StoreContext';
import selectLanguageId from '../../app/selectors/selectLanguageId';

export default function CurrentLanguageFlag({className}: {className?: string}) {
	const languageId = useSelector(selectLanguageId);
	const language = config.availableLanguages[languageId];

	return (
		<div
			className={classNames(className, 'link-monospaced')}
			data-title={Liferay.Language.get('localizable')}
		>
			<ClayIcon symbol={language.languageIcon} />

			<span className="sr-only">{language.w3cLanguageId}</span>
		</div>
	);
}
