/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayLink from '@clayui/link';
import ClayPopover from '@clayui/popover';
import {useEventListener} from '@liferay/frontend-js-react-web';
import React, {useRef, useState} from 'react';

export default function ({
	linkedLayoutMessage,
	portletNamespace,
	resetPrototypeURL,
	showLinkedLayoutMessage,
	showModifiedLayoutMessage,
}) {
	const [show, setShow] = useState(false);
	const ref = useRef();

	const infoContainerId = `${portletNamespace}infoPopover`;

	useEventListener(
		'click',
		({target}) => {
			if (!target.closest(`#${infoContainerId}`)) {
				setShow(false);
			}
		},
		false,
		window
	);

	return (
		<ClayPopover
			alignPosition="bottom"
			id={infoContainerId}
			onKeyDown={(event) => {
				if (event.key === 'Escape') {
					setShow(false);

					ref.current.focus();
				}
			}}
			onShowChange={setShow}
			show={show}
			trigger={
				<ClayButtonWithIcon
					className="color-white control-menu-nav-link"
					data-qa-id="info"
					displayType="unstyled"
					onClick={(event) => {
						event.stopPropagation();

						setShow((show) => !show);
					}}
					ref={(node) => {
						ref.current = node;
					}}
					size="sm"
					symbol="information-live"
					title={Liferay.Language.get('additional-information')}
				/>
			}
		>
			{showModifiedLayoutMessage && (
				<div>
					<p
						className="c-mt-0 message-info"
						dangerouslySetInnerHTML={{
							__html: Liferay.Language.get(
								'this-page-has-been-changed-since-the-last-update-from-the-site-template'
							),
						}}
					/>

					<ClayLink
						button={{small: true}}
						displayType="primary"
						href={resetPrototypeURL}
					>
						{Liferay.Language.get('reset-changes')}
					</ClayLink>
				</div>
			)}

			{showLinkedLayoutMessage && (
				<p className="c-my-0 message-info">{linkedLayoutMessage}</p>
			)}
		</ClayPopover>
	);
}
