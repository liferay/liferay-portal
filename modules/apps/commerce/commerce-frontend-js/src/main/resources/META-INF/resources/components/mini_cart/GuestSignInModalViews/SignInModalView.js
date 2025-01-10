/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import React, {useEffect} from 'react';

import {FORGOT_PASSWORD, SIGN_IN, SIGN_UP} from '../util/guestModal';

function SignInModalView({setActiveView, setIsLoading, viewsMap}) {
	useEffect(() => {
		const forgotPasswordHandler = (event) => {
			if (event.target?.dataset?.linkId === FORGOT_PASSWORD) {
				event.preventDefault();

				setIsLoading(true);

				setActiveView(FORGOT_PASSWORD);

				return;
			}

			return event;
		};

		document.body.addEventListener('click', forgotPasswordHandler);

		setIsLoading(false);

		return () => {
			document.body.removeEventListener('click', forgotPasswordHandler);
		};
	}, [setActiveView, setIsLoading]);

	return (
		<ClayModal.Body className="border-0">
			<div
				dangerouslySetInnerHTML={{
					__html: viewsMap[SIGN_IN].content,
				}}
			/>

			{viewsMap[SIGN_UP].url ? (
				<ClayButton
					block
					className="mt-2"
					displayType="secondary"
					onClick={(event) => {
						event.preventDefault();

						setIsLoading(true);
						setActiveView(SIGN_UP);
					}}
					type="button"
				>
					{Liferay.Language.get('sign-up')}
				</ClayButton>
			) : null}
		</ClayModal.Body>
	);
}

export default SignInModalView;
