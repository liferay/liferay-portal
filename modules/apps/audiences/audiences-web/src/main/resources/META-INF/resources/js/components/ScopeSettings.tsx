/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import ClaySticker from '@clayui/sticker';
import {ItemSelector} from '@liferay/frontend-js-item-selector-web';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useId, useMemo, useState} from 'react';

import {Scope, Site} from '../types';
import {getSitesAPIURL} from '../util/getSitesAPIURL';

interface IProps {
	companyGroupERC: string;
	errorMessage?: string;
	namespace: string;
	onScopeChange: (scope: Scope) => void;
	scope: Scope;
}

function SiteSticker({logo}: {logo: string}) {
	return (
		<ClaySticker displayType="secondary" shape="circle" size="sm">
			<ClaySticker.Image alt="" src={logo} />
		</ClaySticker>
	);
}

export default function ScopeSettings({
	companyGroupERC,
	errorMessage,
	namespace,
	onScopeChange,
	scope,
}: IProps) {
	const [site, setSite] = useState<Site>();

	const allSites = scope === 'all';
	const scopeSites = allSites ? [] : scope;

	const scopeSitesLabelId = useId();

	const apiURL = useMemo(
		() => getSitesAPIURL(companyGroupERC, scope),
		[companyGroupERC, scope]
	);

	return (
		<>
			<h3 className="c-mt-4 sheet-subtitle">
				{Liferay.Language.get('scope')}
			</h3>

			<ClayCheckbox
				checked={allSites}
				label={Liferay.Language.get(
					'make-this-audience-available-for-all-sites'
				)}
				onChange={(event) =>
					onScopeChange(event.target.checked ? 'all' : [])
				}
			/>

			{!allSites && (
				<>
					<ClayForm.Group
						className={classNames('c-mt-4', {
							'has-error': !!errorMessage,
						})}
					>
						<label htmlFor={`${namespace}siteSelector`}>
							{Liferay.Language.get('sites')}

							<ClayIcon
								className="c-ml-1 reference-mark"
								symbol="asterisk"
							/>
						</label>

						<div className="autofit-row c-gap-2">
							<div className="autofit-col autofit-col-expand">
								<ItemSelector<Site>
									apiURL={apiURL}
									aria-describedby={
										errorMessage &&
										`${namespace}siteSelectorError`
									}
									aria-invalid={!!errorMessage}
									id={`${namespace}siteSelector`}
									items={site ? [site] : []}
									key={apiURL}
									onItemsChange={([item]) => setSite(item)}
									placeholder={Liferay.Language.get(
										'select-site'
									)}
								>
									{(item) => (
										<ItemSelector.Item
											className="align-items-center c-gap-2 d-flex"
											key={item.id}
											textValue={item.descriptiveName}
										>
											<SiteSticker logo={item.logo} />

											{item.descriptiveName}
										</ItemSelector.Item>
									)}
								</ItemSelector>
							</div>

							<div className="autofit-col">
								<ClayButton
									disabled={!site}
									displayType="secondary"
									onClick={() => {
										if (site) {
											onScopeChange([
												...scopeSites,
												site,
											]);

											setSite(undefined);
										}
									}}
								>
									{Liferay.Language.get('add-site-to-scope')}
								</ClayButton>
							</div>
						</div>

						{errorMessage && (
							<ClayForm.FeedbackGroup role="alert">
								<ClayForm.FeedbackItem
									id={`${namespace}siteSelectorError`}
								>
									<ClayForm.FeedbackIndicator symbol="exclamation-full" />

									{errorMessage}
								</ClayForm.FeedbackItem>
							</ClayForm.FeedbackGroup>
						)}
					</ClayForm.Group>

					{!!scopeSites.length && (
						<>
							<p
								className="font-weight-semi-bold mb-1 text-3"
								id={scopeSitesLabelId}
							>
								{Liferay.Language.get('scope-sites')}
							</p>

							<ClayList
								aria-labelledby={scopeSitesLabelId}
								className="mb-0"
							>
								{scopeSites.map((scopeSite) => (
									<ClayList.Item
										flex
										key={scopeSite.externalReferenceCode}
									>
										<ClayList.ItemField className="justify-content-center">
											<SiteSticker
												logo={scopeSite.logo}
											/>
										</ClayList.ItemField>

										<ClayList.ItemField
											className="font-weight-semi-bold justify-content-center"
											expand
										>
											{scopeSite.descriptiveName}
										</ClayList.ItemField>

										<ClayList.ItemField>
											<ClayButtonWithIcon
												aria-label={sub(
													Liferay.Language.get(
														'remove-x'
													),
													scopeSite.descriptiveName
												)}
												borderless
												displayType="secondary"
												onClick={() =>
													onScopeChange(
														scopeSites.filter(
															(item) =>
																item.externalReferenceCode !==
																scopeSite.externalReferenceCode
														)
													)
												}
												size="sm"
												symbol="times-circle"
												title={sub(
													Liferay.Language.get(
														'remove-x'
													),
													scopeSite.descriptiveName
												)}
											/>
										</ClayList.ItemField>
									</ClayList.Item>
								))}
							</ClayList>
						</>
					)}
				</>
			)}
		</>
	);
}
