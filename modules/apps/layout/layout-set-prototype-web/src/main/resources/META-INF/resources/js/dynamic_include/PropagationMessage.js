/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayLink from '@clayui/link';
import ClayPopover from '@clayui/popover';
import {useEventListener} from '@liferay/frontend-js-react-web';
import {openToast} from 'frontend-js-components-web';
import React, {useRef, useState} from 'react';

export default function ({
	enableDisablePropagationURL,
	portletNamespace,
	readyForPropagation,
	triggerPropagation,
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
			size="lg"
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
					small
					symbol="merge"
				/>
			}
		>
			<div>
				<p
					className="message-info mt-0"
					dangerouslySetInnerHTML={{
						__html: Liferay.Language.get(
							'each-page-modification-can-trigger-a-propagation-from-the-site-template-to-the-connected-sites'
						),
					}}
				/>

				<ClayLink
					button={{small: true}}
					displayType="primary"
					href={enableDisablePropagationURL}
					onClick={() => {
						openToast({
							autoClose: 10000,
							message: readyForPropagation
								? Liferay.Language.get(
										'propagation-is-disabled-connected-sites-might-not-have-been-updated-yet-propagation-is-only-triggered-when-a-site-created-from-the-template-is-visited'
									)
								: triggerPropagation
									? Liferay.Language.get(
											'propagation-is-enabled-connected-sites-are-being-updated'
										)
									: Liferay.Language.get(
											'propagation-is-enabled-connected-sites-will-be-updated-once-a-site-page-is-visited'
										),
							type: 'info',
						});
					}}
				>
					{readyForPropagation
						? Liferay.Language.get('disable-propagation')
						: Liferay.Language.get('ready-for-propagation')}
				</ClayLink>
			</div>
		</ClayPopover>
	);
}
