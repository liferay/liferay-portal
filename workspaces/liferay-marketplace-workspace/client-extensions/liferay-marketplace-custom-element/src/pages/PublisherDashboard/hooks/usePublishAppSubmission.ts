/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Dispatch} from 'react';
import {useLocation, useNavigate, useParams} from 'react-router-dom';

import {
	AppActions,
	NewAppInitialState,
	NewAppTypes,
} from '../../../context/NewAppContext';
import i18n from '../../../i18n';
import {Liferay} from '../../../liferay/liferay';
import AppPublish from '../../../services/actions/AppPublish';

type ProductConfig = {
	isDraft: boolean;
};

const usePublishAppSubmission = (
	context: NewAppInitialState,
	dispatch: Dispatch<AppActions>
) => {
	const {productId} = useParams();
	const location = useLocation();
	const navigate = useNavigate();

	const _onSave = async (config: ProductConfig) => {
		dispatch({payload: true, type: NewAppTypes.SET_LOADING});

		const appPublish = new AppPublish(context);

		const product = await appPublish.sync(config);

		dispatch({payload: product, type: NewAppTypes.SET_PRODUCT});

		dispatch({payload: false, type: NewAppTypes.SET_LOADING});

		return product;
	};

	const onSaveAsDraft = async () => {
		const product = await _onSave({isDraft: true});

		Liferay.Util.openToast({
			message: i18n.sub('x-saved-as-a-draft-successfully', [
				context.profile.name,
			]),
			title: '',
			type: 'info',
		});

		if (!productId) {
			navigate(
				location.pathname.replace(
					'/publisher/',
					`/${product.productId}/publisher/`
				)
			);
		}
	};

	const onSave = async () => {
		await _onSave({isDraft: false});

		Liferay.Util.openToast({
			message: i18n.sub('app-x-submitted', [context.profile.name]),
			title: '',
			type: 'info',
		});
	};

	return {onSave, onSaveAsDraft};
};

export default usePublishAppSubmission;
