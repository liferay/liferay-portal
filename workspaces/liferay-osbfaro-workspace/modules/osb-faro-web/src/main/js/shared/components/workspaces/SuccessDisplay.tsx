import ClayLink from '@clayui/link';
import Constants from 'shared/util/constants';
import React from 'react';
import Sheet from 'shared/components/Sheet';
import URLConstants from 'shared/util/url-constants';
import WorkspacesBasePage from 'shared/components/workspaces/BasePage';
import {sub} from 'shared/util/lang';

const {faroURL} = Constants;

const SuccessDisplay = ({friendlyURL}: {friendlyURL: string}) => {
	const link = (
		<ClayLink href={`${faroURL}/workspace${friendlyURL}`} key="link">
			{`${faroURL}/workspace${friendlyURL}`}
		</ClayLink>
	);

	return (
		<WorkspacesBasePage title={Liferay.Language.get('new-workspace')}>
			<Sheet>
				<Sheet.Header className="mb-4">
					<h3 className="title">
						{Liferay.Language.get(
							'your-workspace-is-being-created'
						)}
					</h3>
				</Sheet.Header>
				<Sheet.Body>
					<Sheet.Section>
						<p>
							{sub(
								Liferay.Language.get(
									'well-send-you-an-email-once-its-ready-to-access-at-this-url-x'
								),
								[link],
								false
							)}
						</p>
						<p>
							{sub(
								Liferay.Language.get(
									'you-can-also-leave-this-page-open-and-well-notify-you-here.it-should-take-around-x-hour'
								),
								[1]
							)}
						</p>
						<p>
							{Liferay.Language.get(
								'in-the-meantime-check-out-our-documentation-to-get-familiar-with-the-features'
							)}
						</p>
					</Sheet.Section>
				</Sheet.Body>
				<Sheet.Footer divider={false}>
					<ClayLink
						button
						className="button-root"
						displayType="primary"
						href={URLConstants.DocumentationAdminLink}
						target="_blank"
					>
						{Liferay.Language.get('check-out-docs')}
					</ClayLink>
				</Sheet.Footer>
			</Sheet>
		</WorkspacesBasePage>
	);
};

export default SuccessDisplay;
