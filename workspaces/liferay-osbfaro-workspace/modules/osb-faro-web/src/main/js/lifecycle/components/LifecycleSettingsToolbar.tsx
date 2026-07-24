import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayToolbar from '@clayui/toolbar';
import React from 'react';
import {Link} from 'react-router-dom';
import {Text} from '@clayui/core';

interface ILifecycleSettingsToolbarProps {
	backURL: string;
	onCancel: () => void;
	onSubmit: () => void;
	submitDisabled?: boolean;
	submitLabel: string;
}

const LifecycleSettingsToolbar: React.FC<ILifecycleSettingsToolbarProps> = ({
	backURL,
	onCancel,
	onSubmit,
	submitDisabled = false,
	submitLabel,
}) => (
	<ClayToolbar className="bg-white">
		<ClayToolbar.Nav className="mx-4">
			<ClayToolbar.Item>
				<div className="component-action">
					<Link
						aria-label={Liferay.Language.get('back')}
						className="text-secondary"
						to={backURL}
					>
						<ClayIcon className="mb-1" symbol="angle-left" />
					</Link>
				</div>
			</ClayToolbar.Item>

			<ClayToolbar.Item className="pl-0">
				<ClayToolbar.Section>
					<span className="text-dark">
						<Text size={5} weight="bold">
							{Liferay.Language.get('lifecycle-settings')}
						</Text>
					</span>
				</ClayToolbar.Section>
			</ClayToolbar.Item>

			<ClayToolbar.Item expand />

			<ClayToolbar.Item>
				<ClayButton
					borderless
					className="rounded-lg"
					displayType="secondary"
					onClick={onCancel}
					size="sm"
				>
					{Liferay.Language.get('cancel')}
				</ClayButton>
			</ClayToolbar.Item>

			<ClayToolbar.Item>
				<ClayButton
					className="rounded-lg"
					disabled={submitDisabled}
					displayType="primary"
					onClick={onSubmit}
					size="sm"
				>
					{submitLabel}
				</ClayButton>
			</ClayToolbar.Item>
		</ClayToolbar.Nav>
	</ClayToolbar>
);

export default LifecycleSettingsToolbar;
