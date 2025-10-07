/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClipboardJS from 'clipboard';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef} from 'react';

const WebdavInputCopyButton = ({fields}) => {
	const inputRefs = useRef([]);

	useEffect(() => {
		const clipboards = inputRefs.current.map((input, index) => {
			if (!input) {
				return null;
			}

			const clipboard = new ClipboardJS(input, {
				text: () => fields[index].value,
			});

			clipboard.on('success', () => {
				openToast({
					message: Liferay.Language.get(
						'copied-link-to-the-clipboard'
					),
					type: 'success',
				});
			});

			clipboard.on('error', () => {
				openToast({
					message: Liferay.Language.get('an-error-occurred'),
					title: Liferay.Language.get('error'),
					type: 'warning',
				});
			});

			return clipboard;
		});

		return () => {
			clipboards.forEach((clipboard) => clipboard?.destroy());
		};
	}, [fields]);

	return (
		<>
			{fields.map(({id, label, value}, index) => (
				<div key={index}>
					<ClayForm.Group>
						<label htmlFor={id}>{label}</label>

						<ClayInput.Group>
							<ClayInput.GroupItem prepend>
								<ClayInput
									disabled
									id={id}
									type="text"
									value={value}
								/>
							</ClayInput.GroupItem>

							<ClayInput.GroupItem append shrink>
								<ClayButtonWithIcon
									displayType="secondary"
									ref={(element) => {
										inputRefs.current[index] = element;
									}}
									symbol="paste"
									title={sub(
										Liferay.Language.get('copy-x'),
										label
									)}
								/>
							</ClayInput.GroupItem>
						</ClayInput.Group>
					</ClayForm.Group>
				</div>
			))}
		</>
	);
};

export default WebdavInputCopyButton;
