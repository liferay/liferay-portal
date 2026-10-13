/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {openToast} from 'frontend-js-components-web';
import {escapeHTML} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {Launch, getLaunch, listLaunches} from '../api/launches';

interface Props {
	launchSetId: number;
	noLaunchURL: string;
}

export default function LaunchPreviewTopbar({launchSetId, noLaunchURL}: Props) {
	const [launch, setLaunch] = useState<Launch | null>(null);

	useEffect(() => {
		if (launchSetId <= 0) {
			setLaunch(null);

			return;
		}

		let current = true;

		getLaunch(launchSetId)
			.then((nextLaunch) => {
				if (current) {
					setLaunch(nextLaunch);
				}
			})
			.catch((exception: Error) =>
				openToast({
					message: escapeHTML(exception.message),
					type: 'danger',
				})
			);

		return () => {
			current = false;
		};
	}, [launchSetId]);

	return (
		<nav
			aria-label={Liferay.Language.get('preview')}
			className="launch-preview-topbar"
		>
			<div className="launch-preview-topbar__status">
				<span className="launch-preview-topbar__label">
					{Liferay.Language.get('previewing')}
				</span>
			</div>

			<div className="launch-preview-topbar__pickers">
				<div className="launch-preview-topbar__picker">
					<span className="launch-preview-topbar__label">
						{Liferay.Language.get('launch')}
					</span>

					<LaunchPicker
						launch={launch}
						launchSetId={launchSetId}
						noLaunchURL={noLaunchURL}
					/>
				</div>
			</div>
		</nav>
	);
}

function LaunchPicker({
	launch,
	launchSetId,
	noLaunchURL,
}: {
	launch: Launch | null;
	launchSetId: number;
	noLaunchURL: string;
}) {
	const [active, setActive] = useState(false);
	const [launches, setLaunches] = useState<Launch[]>([]);

	useEffect(() => {
		if (!active || launches.length) {
			return;
		}

		listLaunches()
			.then(setLaunches)
			.catch((exception: Error) =>
				openToast({
					message: escapeHTML(exception.message),
					type: 'danger',
				})
			);
	}, [active, launches.length]);

	return (
		<ClayDropDown
			active={active}
			onActiveChange={setActive}
			trigger={
				<ClayButton displayType="unstyled" small>
					{launchSetId <= 0
						? Liferay.Language.get('no-launch')
						: launch?.name ?? Liferay.Language.get('launch')}

					<ClayIcon
						className="inline-item inline-item-after"
						symbol="caret-bottom"
					/>
				</ClayButton>
			}
		>
			<ClayDropDown.ItemList>
				<ClayDropDown.Item active={launchSetId <= 0} href={noLaunchURL}>
					{Liferay.Language.get('no-launch')}
				</ClayDropDown.Item>

				{launches.map((launchOption) => (
					<ClayDropDown.Item
						active={launchOption.id === launchSetId}
						key={launchOption.id}
						onClick={() => {
							const url = new URL(window.location.href);

							url.searchParams.set(
								'previewLaunchSetId',
								String(launchOption.id)
							);

							window.location.href = url.toString();
						}}
					>
						{launchOption.name}
					</ClayDropDown.Item>
				))}
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
}
