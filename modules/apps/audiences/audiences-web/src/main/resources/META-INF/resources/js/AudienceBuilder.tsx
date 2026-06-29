/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayToolbar from '@clayui/toolbar';
import {ScreenReaderAnnouncerContextProvider} from '@liferay/layout-js-components-web';
import React, {useState} from 'react';

import AttributesSidebar from './components/AttributesSidebar';
import ConditionsPanel from './components/ConditionsPanel';
import {AudiencesCriteriaType} from './types';

import './AudienceBuilder.scss';

const NAME_MAX_LENGTH = 75;

interface IProps {
	audiencesCriteriaTypes?: AudiencesCriteriaType[];
	backURL?: string;
	name?: string;
	namespace?: string;
}

export default function AudienceBuilder({
	audiencesCriteriaTypes = [],
	backURL,
	name,
	namespace = '',
}: IProps) {
	const [currentName, setCurrentName] = useState(name || '');

	return (
		<ScreenReaderAnnouncerContextProvider>
			<div className="d-flex flex-column overflow-hidden">
				<ClayToolbar>
					<ClayLayout.ContainerFluid size={false}>
						<ClayToolbar.Nav>
							<ClayToolbar.Item>
								<ClayLink
									aria-label={Liferay.Language.get('back')}
									button
									displayType="unstyled"
									href={backURL}
									monospaced
								>
									<ClayIcon symbol="angle-left" />
								</ClayLink>
							</ClayToolbar.Item>

							<ClayToolbar.Item expand>
								<ClayToolbar.Section className="text-left">
									<span className="font-weight-bold text-dark text-truncate">
										{currentName ||
											Liferay.Language.get(
												'new-audience'
											)}
									</span>
								</ClayToolbar.Section>
							</ClayToolbar.Item>

							<ClayToolbar.Item>
								<ClayLink
									button
									displayType="secondary"
									href={backURL}
									small
								>
									{Liferay.Language.get('cancel')}
								</ClayLink>
							</ClayToolbar.Item>

							<ClayToolbar.Item>
								<ClayButton
									displayType="primary"
									size="sm"
									type="submit"
								>
									{Liferay.Language.get('save')}
								</ClayButton>
							</ClayToolbar.Item>
						</ClayToolbar.Nav>
					</ClayLayout.ContainerFluid>
				</ClayToolbar>

				<div className="audience-builder-content d-flex">
					<div className="audience-builder-sidebar border-right d-flex flex-column flex-shrink-0 px-4">
						<AttributesSidebar
							audiencesCriteriaTypes={audiencesCriteriaTypes}
						/>
					</div>

					<div className="d-flex flex-column flex-grow-1 overflow-auto p-4">
						<ClayForm.Group>
							<label
								className="font-weight-semi-bold text-3"
								htmlFor={`${namespace}name`}
							>
								{Liferay.Language.get('name')}

								<span className="reference-mark">
									<ClayIcon symbol="asterisk" />
								</span>
							</label>

							<ClayInput
								className="bg-white border-0 font-weight-semi-bold h-auto mb-0 p-0 text-8"
								id={`${namespace}name`}
								maxLength={NAME_MAX_LENGTH}
								name={`${namespace}name`}
								onChange={(event) =>
									setCurrentName(event.target.value)
								}
								placeholder={Liferay.Language.get(
									'new-audience'
								)}
								required
								type="text"
								value={currentName}
							/>
						</ClayForm.Group>

						<ConditionsPanel
							audiencesCriteriaTypes={audiencesCriteriaTypes}
						/>
					</div>
				</div>
			</div>
		</ScreenReaderAnnouncerContextProvider>
	);
}
