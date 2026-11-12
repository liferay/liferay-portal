/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClaySelect} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import PropTypes from 'prop-types';
import React, {useContext, useEffect, useState} from 'react';

import {
	retrieveModelProviderModels,
	retrieveModelProviders,
} from '../../../../../util/fetchUtil';
import {DiagramBuilderContext} from '../../../../DiagramBuilderContext';
import SidebarPanel from '../../SidebarPanel';
import {getUpdatedDataItem} from '../utils';

const ModelSummary = () => {
	const {selectedItem, setSelectedItem} = useContext(DiagramBuilderContext);

	const [error, setError] = useState(false);
	const [loading, setLoading] = useState(false);
	const [modelProviders, setModelProviders] = useState([]);
	const [models, setModels] = useState([]);

	const modelProvider = selectedItem?.data.modelProvider ?? '';

	useEffect(() => {
		const makeFetch = async () => {
			const response = await retrieveModelProviders();

			if (response.ok) {
				const {items} = await response.json();

				setModelProviders(items);
			}
		};

		makeFetch();
	}, []);

	useEffect(() => {
		let ignore = false;

		setError(false);
		setLoading(Boolean(modelProvider));
		setModels([]);

		if (!modelProvider) {
			return;
		}

		const makeFetch = async () => {
			const response = await retrieveModelProviderModels(modelProvider);

			const items = response.ok ? (await response.json()).items : null;

			if (ignore) {
				return;
			}

			if (items) {
				setModels(items);
			}
			else {
				setError(true);
			}

			setLoading(false);
		};

		makeFetch();

		return () => {
			ignore = true;
		};
	}, [modelProvider]);

	return (
		<SidebarPanel panelTitle={Liferay.Language.get('model')}>
			<ClayForm.Group>
				<label htmlFor="modelProvider">
					{Liferay.Language.get('provider')}
				</label>

				<ClaySelect
					aria-label={Liferay.Language.get('provider')}
					id="modelProvider"
					onChange={({target}) =>
						setSelectedItem({
							...selectedItem,
							data: {
								...selectedItem.data,
								modelName: '',
								modelProvider: target.value,
							},
						})
					}
					value={modelProvider}
				>
					<ClaySelect.Option
						hidden
						label={Liferay.Language.get('select-a-provider')}
						value=""
					/>

					{modelProviders.map(({externalReferenceCode, title}) => (
						<ClaySelect.Option
							key={externalReferenceCode}
							label={title}
							value={externalReferenceCode}
						/>
					))}
				</ClaySelect>
			</ClayForm.Group>

			<ClayForm.Group className={error ? 'has-error' : ''}>
				<label htmlFor="modelName">
					{Liferay.Language.get('model')}
				</label>

				{loading ? (
					<ClayLoadingIndicator displayType="secondary" size="sm" />
				) : (
					<ClaySelect
						aria-label={Liferay.Language.get('model')}
						disabled={!modelProvider || error}
						id="modelName"
						onChange={({target}) =>
							setSelectedItem(
								getUpdatedDataItem(
									'modelName',
									selectedItem,
									target
								)
							)
						}
						value={selectedItem?.data.modelName ?? ''}
					>
						<ClaySelect.Option
							hidden
							label={Liferay.Language.get('choose-an-option')}
							value=""
						/>

						{models.map(({label, name}) => (
							<ClaySelect.Option
								key={name}
								label={label}
								value={name}
							/>
						))}
					</ClaySelect>
				)}

				{error && (
					<ClayForm.FeedbackItem>
						<ClayForm.FeedbackIndicator symbol="exclamation-full" />

						{Liferay.Language.get('an-unexpected-error-occurred')}
					</ClayForm.FeedbackItem>
				)}
			</ClayForm.Group>
		</SidebarPanel>
	);
};

ModelSummary.propTypes = {
	setContentName: PropTypes.func.isRequired,
};

export default ModelSummary;
