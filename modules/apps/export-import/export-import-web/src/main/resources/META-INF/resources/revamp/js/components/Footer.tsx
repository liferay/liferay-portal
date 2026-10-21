/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import React from 'react';

function Footer({
	actionButton,
	backURL,
	continueDisabled,
	onPrevious,
}: {
	actionButton?: React.ReactElement;
	backURL: string;
	continueDisabled?: boolean;
	onPrevious?: () => void;
}) {
	return (
		<ClayLayout.SheetFooter className="mt-5">
			<ClayLayout.Row className="flex-fill">
				{onPrevious && (
					<ClayLayout.Col lg={4}>
						<ClayButton
							borderless
							displayType="secondary"
							onClick={onPrevious}
						>
							<span className="inline-item inline-item-before">
								<ClayIcon symbol="order-arrow-left" />
							</span>

							{Liferay.Language.get('previous')}
						</ClayButton>
					</ClayLayout.Col>
				)}

				<ClayLayout.Col lg={onPrevious && 8}>
					<div className="d-flex justify-content-end">
						<ClayLink button displayType="secondary" href={backURL}>
							{Liferay.Language.get('cancel')}
						</ClayLink>

						{actionButton ? (
							actionButton
						) : (
							<ClayButton
								disabled={continueDisabled}
								type="submit"
							>
								{Liferay.Language.get('continue')}
							</ClayButton>
						)}
					</div>
				</ClayLayout.Col>
			</ClayLayout.Row>
		</ClayLayout.SheetFooter>
	);
}

export default Footer;
