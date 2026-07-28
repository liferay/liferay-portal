import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import getCN from 'classnames';
import React from 'react';
import Sheet from 'shared/components/Sheet';
import WorkspacesBasePage from 'shared/components/workspaces/BasePage';
import {Routes} from 'shared/util/router';

const WorkspaceNotFound: React.FC<React.HTMLAttributes<HTMLElement>> = ({
	className,
}) => (
	<WorkspacesBasePage
		className={getCN('workspace-not-found-root text-center', className)}
		title=""
	>
		<Sheet>
			<Sheet.Header>
				<Sheet.Section className="title">
					<ClayIcon
						className="icon-root text-danger"
						symbol="exclamation-circle"
					/>{' '}
					{Liferay.Language.get('workspace-error')}
				</Sheet.Section>
				<Sheet.Section className="description">
					{`${Liferay.Language.get(
						'either-this-workspace-doesnt-exist-or-you-dont-have-access-to-it'
					)} ${Liferay.Language.get(
						'make-sure-youve-typed-the-correct-workspace-url-or-check-with-the-workspace-administrator-for-access'
					)}`}
				</Sheet.Section>
				<Sheet.Section>
					<ClayLink
						button
						className="button-root"
						displayType="secondary"
						href={Routes.WORKSPACES}
					>
						{Liferay.Language.get('go-back-to-your-workspaces')}
					</ClayLink>
				</Sheet.Section>
			</Sheet.Header>
		</Sheet>
	</WorkspacesBasePage>
);

export default WorkspaceNotFound;
