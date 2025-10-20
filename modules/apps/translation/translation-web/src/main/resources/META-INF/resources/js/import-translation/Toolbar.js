/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayToolbar from '@clayui/toolbar';
import React from 'react';

const noop = () => {};

export default function Toolbar({
	cancelURL,
	onSave = noop,
	publishButtonDisabled,
	publishButtonLabel,
	saveButtonDisabled,
	saveButtonLabel,
	title,
}) {
	return (
		<ClayToolbar className="management-bar management-bar-light navbar navbar-expand-md">
			<ClayLayout.ContainerFluid>
				<ClayToolbar.Nav>
					<ClayToolbar.Item expand>
						<ClayToolbar.Section className="pl-2 text-left">
							<h2
								className="h4 mb-0 text-truncate-inline"
								title={title}
							>
								<span className="text-truncate">{title}</span>
							</h2>
						</ClayToolbar.Section>
					</ClayToolbar.Item>

					<ClayToolbar.Item>
						<ClayToolbar.Section>
							<ClayButton.Group spaced>
								{cancelURL && (
									<ClayLink
										button
										displayType="secondary"
										href={cancelURL}
										small
									>
										{Liferay.Language.get('cancel')}
									</ClayLink>
								)}

								<ClayButton
									disabled={saveButtonDisabled}
									displayType="secondary"
									onClick={onSave}
									small
									type="submit"
								>
									{saveButtonLabel}
								</ClayButton>

								<ClayButton
									disabled={publishButtonDisabled}
									displayType="primary"
									small
									type="submit"
								>
									{publishButtonLabel}
								</ClayButton>
							</ClayButton.Group>
						</ClayToolbar.Section>
					</ClayToolbar.Item>
				</ClayToolbar.Nav>
			</ClayLayout.ContainerFluid>
		</ClayToolbar>
	);
}
