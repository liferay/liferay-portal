import Card from 'shared/components/Card';
import DocumentTitle from 'shared/components/DocumentTitle';
import LifecycleSettingsToolbar from 'lifecycle/components/LifecycleSettingsToolbar';
import React from 'react';
import StageConfigurationPanel from 'lifecycle/components/StageConfigurationPanel';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {ICatalogField} from 'shared/api/catalog';
import {
	IStageConfig,
	LIFECYCLE_STAGE_ORDER,
} from 'lifecycle/utils/stageConfiguration';

interface ILifecycleSettingsFormProps {
	backURL: string;
	catalogFields?: ICatalogField[];
	lifecycleName: string;
	onCancel: () => void;
	onLifecycleNameChange: (name: string) => void;
	onStageChange: (index: number, value: IStageConfig) => void;
	onSubmit: () => void;
	stageConfigs: IStageConfig[];
	submitDisabled?: boolean;
	submitLabel: string;
}

const LifecycleSettingsForm: React.FC<ILifecycleSettingsFormProps> = ({
	backURL,
	catalogFields,
	lifecycleName,
	onCancel,
	onLifecycleNameChange,
	onStageChange,
	onSubmit,
	stageConfigs,
	submitDisabled = false,
	submitLabel,
}) => (
	<div className="d-flex flex-column">
		<DocumentTitle title={Liferay.Language.get('lifecycle-settings')} />

		<LifecycleSettingsToolbar
			backURL={backURL}
			onCancel={onCancel}
			onSubmit={onSubmit}
			submitDisabled={submitDisabled}
			submitLabel={submitLabel}
		/>

		<div className="justify-self-center d-inline-block mt-5 mx-auto">
			<Card>
				<Card.Body>
					<Card.Title>
						{Liferay.Language.get('stage-configuration')}
					</Card.Title>

					<p className="mt-3 text-secondary">
						{Liferay.Language.get(
							'define-entry-conditions-for-each-lifecycle-stage-an-account-moves-to-a-stage-when-it-meets-the-selected-conditions'
						)}
					</p>

					<ClayForm.Group className="mb-4">
						<label
							className="font-weight-semi-bold"
							htmlFor="lifecycleName"
						>
							{Liferay.Language.get('lifecycle-name')}

							<span className="reference-mark">
								<ClayIcon symbol="asterisk" />
							</span>
						</label>

						<ClayInput
							id="lifecycleName"
							onChange={(event) =>
								onLifecycleNameChange(event.target.value)
							}
							sizing="sm"
							value={lifecycleName}
						/>
					</ClayForm.Group>

					{LIFECYCLE_STAGE_ORDER.map((stageType, index) => (
						<StageConfigurationPanel
							defaultExpanded={index === 0}
							fields={catalogFields}
							index={index + 1}
							key={stageType}
							onChange={(value) => onStageChange(index, value)}
							stageType={stageType}
							value={stageConfigs[index]}
						/>
					))}
				</Card.Body>
			</Card>
		</div>
	</div>
);

export default LifecycleSettingsForm;
