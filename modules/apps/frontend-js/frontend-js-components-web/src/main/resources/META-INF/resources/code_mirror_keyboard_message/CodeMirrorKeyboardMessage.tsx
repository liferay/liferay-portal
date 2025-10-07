/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import './CodeMirrorKeyboardMessage.scss';

interface IProps {
	keyIsEnabled: boolean;
}

const REDUCE_TIMEOUT_MS = 4000;

export default function CodeMirrorKeyboardMessage({
	keyIsEnabled = false,
}: IProps) {
	const [reduce, setReduce] = useState<boolean>(false);

	useEffect(() => {
		setReduce(false);

		const time = setTimeout(() => {
			setReduce(true);
		}, REDUCE_TIMEOUT_MS);

		return () => clearTimeout(time);
	}, [keyIsEnabled]);

	return (
		<div
			className={classNames('keyboard-message popover', {
				'd-reduce': reduce,
			})}
		>
			<span className="c-kbd-sm">
				{`${sub(
					Liferay.Language.get('x-tab-key-using'),
					keyIsEnabled
						? Liferay.Language.get('enable')
						: Liferay.Language.get('disable')
				)} `}
			</span>

			<kbd className="c-kbd c-kbd-light c-kbd-sm">
				<kbd className="c-kbd">Ctrl</kbd>

				<span className="c-kbd-separator">+</span>

				<kbd className="c-kbd">M</kbd>
			</kbd>
		</div>
	);
}
