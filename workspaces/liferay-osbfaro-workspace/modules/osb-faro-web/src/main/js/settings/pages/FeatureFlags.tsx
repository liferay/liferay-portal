import BasePage from 'settings/components/base-page/BasePage';
import ClayPanel from '@clayui/panel';
import React from 'react';
import ToggleSwitch from 'shared/components/ToggleSwitch';
import {
	FEATURE_FLAGS,
	FeatureFlagKey,
	isFeatureFlagEnabled,
	setFeatureFlag,
} from 'shared/util/feature-flags';
import {reloadPage} from 'shared/util/url';

/**
 * Hidden, URL-only panel (`/workspace/:groupId/settings/feature-flags`) for
 * toggling the runtime feature flags defined in `shared/util/feature-flags.ts`.
 * Toggles are persisted to `localStorage`; since the flags are resolved at
 * module load, toggling one reloads the page so the change takes effect.
 */
const FeatureFlags = () => {
	const handleToggle =
		(key: FeatureFlagKey) => (event: React.FormEvent<HTMLInputElement>) => {
			setFeatureFlag(key, event.currentTarget.checked);

			reloadPage();
		};

	return (
		<BasePage pageTitle="Feature Flags">
			<ClayPanel.Group className="mb-4">
				{FEATURE_FLAGS.map(({key}) => (
					<ClayPanel displayType="secondary" key={key}>
						<ClayPanel.Header className="align-items-center d-flex justify-content-between">
							<span>{key}</span>

							<ToggleSwitch
								checked={isFeatureFlagEnabled(key)}
								name={key}
								onChange={handleToggle(key)}
							/>
						</ClayPanel.Header>
					</ClayPanel>
				))}
			</ClayPanel.Group>
		</BasePage>
	);
};

export default FeatureFlags;
