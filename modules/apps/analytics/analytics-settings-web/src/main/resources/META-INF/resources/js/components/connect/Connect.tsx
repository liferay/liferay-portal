/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {useModal} from '@clayui/modal';
import classNames from 'classnames';
import React, {useEffect, useState} from 'react';

import {EPageView, Events, useData, useDispatch} from '../..';
import {fetchConnection} from '../../utils/api';
import BasePage from '../BasePage';
import Loading from '../Loading';
import DisconnectModal from './DisconnectModal';

interface IConnectProps {
	onConnect?: () => void;
	title: string;
}

const Connect: React.FC<
	{children?: React.ReactNode | undefined} & IConnectProps
> = ({onConnect, title}) => {
	const {
		connected,
		liferayAnalyticsURL,
		pageView,
		token: initialToken,
	} = useData();
	const dispatch = useDispatch();

	const [token, setToken] = useState(initialToken);
	const {observer, onOpenChange, open} = useModal();
	const [submitting, setSubmitting] = useState(false);

	useEffect(() => {
		setToken(initialToken);
	}, [initialToken]);

	return (
		<BasePage
			description={Liferay.Language.get(
				'use-the-token-generated-in-your-analytics-cloud-to-connect-this-workspace'
			)}
			title={title}
		>
			{connected && (
				<ClayAlert
					displayType="success"
					title={Liferay.Language.get('connected')}
				/>
			)}

			<ClayForm onSubmit={(event) => event.preventDefault()}>
				<ClayForm.Group>
					<label
						className={classNames({
							disabled: connected,
						})}
						htmlFor="inputToken"
					>
						{Liferay.Language.get('analytics-cloud-token')}
					</label>

					<ClayInput
						data-testid="input-token"
						disabled={connected}
						id="inputToken"
						onChange={({target: {value}}) => setToken(value)}
						placeholder={Liferay.Language.get('paste-token-here')}
						type="text"
						value={token}
					/>

					<label
						className={classNames({
							disabled: connected,
						})}
					>
						<small
							className={classNames({
								'text-secondary': !connected,
							})}
						>
							{Liferay.Language.get('analytics-cloud-token-help')}
						</small>
					</label>
				</ClayForm.Group>

				<BasePage.Footer>
					{connected ? (
						<>
							{pageView === EPageView.Wizard && (
								<ClayButton
									onClick={() => onConnect && onConnect()}
								>
									{Liferay.Language.get('next')}
								</ClayButton>
							)}

							<ClayButton
								className="mr-3"
								displayType="primary"
								onClick={() => window.open(liferayAnalyticsURL)}
							>
								{Liferay.Language.get('go-to-workspace')}

								<ClayIcon className="ml-2" symbol="shortcut" />
							</ClayButton>

							<ClayButton
								displayType="secondary"
								onClick={() => onOpenChange(true)}
							>
								{Liferay.Language.get('disconnect')}
							</ClayButton>
						</>
					) : (
						<>
							<ClayButton
								disabled={!token || submitting}
								onClick={async () => {
									setSubmitting(true);

									const response =
										await fetchConnection(token);

									setSubmitting(false);

									if (response.ok) {
										const {liferayAnalyticsURL} =
											await response.json();

										dispatch({
											payload: {
												connected: true,
												liferayAnalyticsURL,
												token,
											},
											type: Events.Connect,
										});

										onConnect && onConnect();
									}
								}}
							>
								{submitting && <Loading inline />}

								{Liferay.Language.get('connect')}
							</ClayButton>
						</>
					)}
				</BasePage.Footer>
			</ClayForm>

			{open && (
				<DisconnectModal
					observer={observer}
					onOpenChange={onOpenChange}
				/>
			)}
		</BasePage>
	);
};

export default Connect;
