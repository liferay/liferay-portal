/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayModal, {useModal} from '@clayui/modal';
import classnames from 'classnames';
import React, {useCallback, useEffect, useState} from 'react';

import {
	INITIAL_VIEWS_MAP,
	SIGN_IN,
	setupViewsMap,
	storeImmediateCheckout,
} from './util/guestModal';

function GuestModal({isVisible, setIsVisible, signInURL}) {
	const {observer, onOpenChange} = useModal({
		defaultOpen: true,
		onClose: () => {
			storeImmediateCheckout(false);

			setIsVisible(false);
		},
	});

	const [activeView, setActiveView] = useState(SIGN_IN);
	const [alert, setAlert] = useState({message: '', type: ''});
	const [isLoading, setIsLoading] = useState(true);
	const [viewsMap, setViewsMap] = useState(INITIAL_VIEWS_MAP);

	const setupViews = useCallback(async () => {
		const initialViewsMap = await setupViewsMap(signInURL);

		if (Object.keys(initialViewsMap).length) {
			setViewsMap(initialViewsMap);
		}
		else {
			onOpenChange(false);
		}
	}, [signInURL, onOpenChange, setViewsMap]);

	useEffect(() => {
		if (viewsMap[activeView].url) {
			return;
		}

		setupViews();
	}, [activeView, setIsLoading, setupViews, viewsMap]);

	const ModalBody = viewsMap[activeView].component;

	return isVisible ? (
		<ClayModal
			center
			id="guest-sign-in-modal"
			observer={observer}
			size="md"
		>
			{activeView ? (
				<>
					<div
						className={classnames(
							'align-items-center justify-content-center d-flex loading-animation',
							{'is-loading': isLoading}
						)}
					/>

					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{viewsMap[activeView].title}
					</ClayModal.Header>

					{(viewsMap[activeView].url ||
						viewsMap[activeView].content) && (
						<>
							{alert.message && (
								<ClayAlert
									className="mb-0 mt-4 mx-4"
									displayType={alert.type}
								>
									{alert.message}
								</ClayAlert>
							)}

							<ModalBody
								setActiveView={setActiveView}
								setAlert={setAlert}
								setIsLoading={setIsLoading}
								setIsVisible={onOpenChange}
								viewsMap={viewsMap}
							/>
						</>
					)}
				</>
			) : null}
		</ClayModal>
	) : null;
}

export default GuestModal;
